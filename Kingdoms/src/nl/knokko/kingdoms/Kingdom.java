package nl.knokko.kingdoms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import nl.knokko.main.KDEventHandler;
import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Book;
import nl.knokko.utils.Translator;
import nl.knokko.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Kingdom {
	
	public static final short MAX_MEMBERS = 256;
	public static final short MAX_INVITES = 256;
	
	public static final String DEFAULT_KING_TITLE = "Koning";
	
	private UUID king;
	private String kingTitle;
	private String name;
	private String coloredName;
	
	private SafeLocation spawnPoint;
	private SafeLocation centre;
	
	private Book info;
	private Book announcements;
	
	private Shape shape;
	
	private int radius;
	
	private ArrayList<UUID> invites;
	private ArrayList<String> allyInvites;
	private ArrayList<MemberData> members;
	
	private ArrayList<String> allies;
	
	private ChatColor[] colors;
	
	private static Book createDefaultInfo(String colName){
		return new Book("No info about " + colName, "Kingdoms Core", "The king has not set the information about " + colName + ChatColor.WHITE + " yet.");
	}
	
	private static Book createDefaultAnnouncements(){
		return new Book("No internal announcements", "Kingdoms Core", "The king has not set the internal announcements yet.");
	}

	public Kingdom(String name, OfflinePlayer king) {
		this.name = name;
		this.king = king.getUniqueId();
		this.invites = new ArrayList<UUID>();
		this.members = new ArrayList<MemberData>();
		this.shape = Shape.SQUARE;
		this.kingTitle = DEFAULT_KING_TITLE;
		setColors(new ChatColor[]{ChatColor.GRAY});
		this.info = createDefaultInfo(getColoredName());
		this.announcements = createDefaultAnnouncements();
		this.allies = new ArrayList<String>();
		this.allyInvites = new ArrayList<String>();
	}
	
	private Kingdom(String name, UUID kingID, SafeLocation kingdomSpawn, SafeLocation kingdomCentre, Shape shape, int radius, ChatColor[] colors, ArrayList<MemberData> memberData, ArrayList<UUID> invites, String kingTitle, Book info, Book announcements, ArrayList<String> allies, ArrayList<String> allyInvites){
		this.name = name;
		this.king = kingID;
		this.spawnPoint = kingdomSpawn;
		this.centre = kingdomCentre;
		this.shape = shape;
		this.radius = radius;
		this.members = memberData;
		for(MemberData md : memberData)
			KingdomsPlugin.getInstance().getKingdoms().setPlayerKingdom(md.getPlayerID(), this);
		this.invites = invites;
		this.kingTitle = kingTitle;
		setColors(colors);
		this.info = info;
		this.announcements = announcements;
		this.allies = allies;
		this.allyInvites = allyInvites;
	}
	
	public void setColors(ChatColor[] colors){
		if(colors.length > 256)
			colors = new ChatColor[]{ChatColor.GRAY};
		this.colors = colors;
		setColoredName();
		for(MemberData data : members){
			Player player = Bukkit.getPlayer(data.getPlayerID());
			if(player != null){
				KDEventHandler.updatePlayerName(player, KingdomsPlugin.getInstance().getKingdoms());
			}
		}
		OfflinePlayer k = getKing();
		if(k instanceof Player)
			KDEventHandler.updatePlayerName((Player) k, KingdomsPlugin.getInstance().getKingdoms());
	}
	
	public Book getKingdomInfo(){
		return info;
	}
	
	public Book getInternalAnnouncements(){
		return announcements;
	}
	
	public ChatColor[] getColors(){
		return colors;
	}
	
	public String getName(){
		return name;
	}
	
	public String getColoredName(){
		if(coloredName == null)
			setColoredName();
		return coloredName;
	}
	
	public String getRankString(Player player){
		if(player.getUniqueId().equals(king))
			return "[" + ChatColor.GOLD + kingTitle + ChatColor.WHITE+ "]";
		for(MemberData data : members){
			if(data.getPlayerID().equals(player.getUniqueId())){
				return "[" + data.getColoredTitle() + ChatColor.WHITE + "]";
			}
		}
		return "";
	}
	
	public OfflinePlayer getKing(){
		return Bukkit.getOfflinePlayer(king);
	}
	
	public UUID getKingID(){
		return king;
	}
	
	public Location getBukkitKingdomSpawn(boolean loadWorldIfNeeded){
		return spawnPoint.getLocation(loadWorldIfNeeded);
	}
	
	public Location getBukkitCentre(boolean loadWorldIfNeeded){
		return centre.getLocation(loadWorldIfNeeded);
	}
	
	public SafeLocation getKingdomSpawn(){
		return spawnPoint;
	}
	
	public SafeLocation getCentre(){
		return centre;
	}
	
	public Shape getShape(){
		return shape;
	}
	
	public int getRadius(){
		return radius;
	}
	
	public boolean isKing(OfflinePlayer player){
		return king.equals(player.getUniqueId());
	}
	
	public boolean canInvite(UUID id){
		if(id.equals(king))
			return true;
		for(MemberData data : members)
			if(id.equals(data.getPlayerID()))
				return data.canInvite();
		return false;
	}
	
	public boolean isDiplomatic(UUID id){
		if(id.equals(king))
			return true;
		for(MemberData data : members)
			if(id.equals(data.getPlayerID()))
				return data.isDiplomatic();
		return false;
	}
	
	public ArrayList<MemberData> getMembers(){
		return members;
	}
	
	/**
	 * @return an ArrayList<Player> that contains all online players who do not belong to this kingdom and are not invited.
	 */
	public ArrayList<Player> getPlayersToInvite(Kingdoms kds){
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		ArrayList<Player> players = new ArrayList<Player>();
		Iterator<? extends Player> iterator = onlinePlayers.iterator();
		while(iterator.hasNext()){
			Player player = iterator.next();
			if(kds.getPlayerKingdom(player) != this && !isInvited(player))
				players.add(player);
		}
		return players;
	}
	
	public ArrayList<OfflinePlayer> getInvitedPlayers(){
		ArrayList<OfflinePlayer> invitedPlayers = new ArrayList<OfflinePlayer>(invites.size());
		for(UUID id : invites){
			OfflinePlayer player = Bukkit.getOfflinePlayer(id);
			if(player != null)
				invitedPlayers.add(player);
		}
		return invitedPlayers;
	}
	
	public List<Player> getOnlinePlayers(){
		List<Player> players = new ArrayList<Player>();
		for(MemberData data : members){
			Player player = data.getPlayer();
			if(player != null)
				players.add(player);
		}
		OfflinePlayer king = getKing();
		if(king instanceof Player)
			players.add((Player) king);
		return players;
	}
	
	public OfflinePlayer getPlayer(String name){
		if(Bukkit.getOfflinePlayer(king).getName().equals(name))
			return Bukkit.getOfflinePlayer(king);
		for(MemberData member : members){
			if(member.getOfflinePlayer().getName().equals(name))
				return member.getOfflinePlayer();
		}
		return null;
	}
	
	public boolean setInvitePermission(OfflinePlayer player, boolean value){
		for(MemberData data : members){
			if(player.getUniqueId().equals(data.getPlayerID())){
				data.setInviting(value);
				return true;
			}
		}
		return false;
	}
	
	public boolean setDiplomaticPermission(OfflinePlayer player, boolean value){
		for(MemberData data : members){
			if(player.getUniqueId().equals(data.getPlayerID())){
				data.setDiplomatic(value);
				return true;
			}
		}
		return false;
	}
	
	public boolean setPlayerTitle(OfflinePlayer player, String titleName, ChatColor titleColor){
		for(MemberData data : members){
			if(player.getUniqueId().equals(data.getPlayerID())){
				data.setTitle(titleName, titleColor);
				return true;
			}
		}
		return false;
	}
	
	public void setKingTitle(String title){
		kingTitle = title;
	}
	
	public void setKingdomInfo(Book info){
		if(info == null)
			throw new NullPointerException("Can't set kingdom info to " + info);
		this.info = info;
	}
	
	public void setAnnouncements(Book announcements){
		if(announcements == null)
			throw new NullPointerException("Can't set announcements to " + announcements);
		this.announcements = announcements;
	}
	
	public boolean isDirectMember(OfflinePlayer player){
		if(player.getUniqueId().equals(king))
			return true;
		for(MemberData data : members){
			if(data.getPlayerID().equals(player.getUniqueId()))
				return true;
		}
		return false;
	}
	
	public boolean isMember(Object entity){
		Player player = Utils.getPlayer(entity);
		if(player != null)
			return isDirectMember(player);
		return false;
	}
	
	public boolean isInvited(Player player){
		return invites.contains(player.getUniqueId());
	}
	
	public boolean isInvited(Kingdom kd){
		return allyInvites.contains(kd.getName());
	}
	
	public boolean isAlly(Kingdom kd){
		return allies.contains(kd.getName());
	}
	
	public boolean intersectsWith(Kingdom other){
		if(centre == null)
			return false;
		return Shape.doIntersect(centre, other.centre, shape, other.shape, radius, other.radius);
	}
	
	public boolean isInside(Location location){
		return isInside(new SafeLocation(location));
	}
	
	public boolean isInside(SafeLocation location){
		if(centre == null || shape == null || radius <= 0)
			return false;
		return Shape.isInside(location, centre, shape, radius);
	}
	
	public boolean invite(OfflinePlayer player){
		if(!invites.contains(player.getUniqueId())){
			invites.add(player.getUniqueId());
			if(player instanceof Player)
				Translator.receivedInvite((Player)player, getColoredName());
			return true;
		}
		return false;
	}
	
	public boolean invite(String name){
		@SuppressWarnings("deprecation")
		Player player = Bukkit.getPlayer(name);
		if(player != null)
			return invite(player);
		return false;
	}
	
	public boolean inviteAlly(Kingdom kingdom){
		if(kingdom.isInvited(this)){
			kingdom.addAlly(this);
			addAlly(kingdom);
			Translator.broadcastFormedAlliance(this, kingdom);
			return true;
		}
		if(allyInvites.contains(kingdom.getName()))
			return false;
		allyInvites.add(kingdom.getName());
		Translator.invitedForAlliance(this, kingdom);
		return true;
	}
	
	public boolean cancelInvite(Kingdom kingdom){
		if(allyInvites.remove(kingdom)){
			Translator.broadcastCancelledAllianceInvite(this, kingdom);
			return true;
		}
		return false;
	}
	
	private void addAlly(Kingdom kingdom){
		allyInvites.remove(kingdom.getName());
		allies.add(kingdom.getName());
	}
	
	public boolean cancelInvite(String name){
		for(int i = 0; i < invites.size(); i++){
			OfflinePlayer player = Bukkit.getOfflinePlayer(invites.get(i));
			if(player.getName().equals(name)){
				invites.remove(i);
				return true;
			}
		}
		return false;
	}

	public void setKingdomSpawn(Location kingdomSpawn){
		spawnPoint = new SafeLocation(kingdomSpawn);
	}
	
	public void setLocation(SafeLocation centre, Shape shape, int radius){
		this.centre = centre;
		this.shape = shape;
		this.radius = radius;
	}
	
	public void setLocation(Location centre, Shape shape, int radius){
		this.centre = new SafeLocation(centre);
		this.shape = shape;
		this.radius = radius;
	}
	
	public void addMember(Kingdoms kds, Player newMember, boolean leaveOld){
		if(leaveOld){
			Kingdom kd = kds.getPlayerKingdom(newMember);
			if(kd != null){
				kd.removeMember(kds, newMember.getUniqueId());
				Translator.broadcastLeftKingdom(kd, newMember.getName());
			}
		}
		if(!isDirectMember(newMember)){
			members.add(new MemberData(newMember.getUniqueId()));
			kds.setPlayerKingdom(newMember.getUniqueId(), this);
			invites.remove(newMember.getUniqueId());
		}
	}
	
	public boolean removeMember(Kingdoms kds, UUID memberID){
		for(int i = 0; i < members.size(); i++){
			MemberData member = members.get(i);
			if(member.getPlayerID().equals(memberID)){
				members.remove(i);
				kds.setPlayerKingdom(memberID, null);
				return true;
			}
		}
		return false;
	}
	
	public void changeKing(UUID newKing){
		for(int i = 0; i < members.size(); i++){
			MemberData data = members.get(i);
			if(data.getPlayerID() == newKing){
				members.remove(i);
				i = members.size();
			}
		}
		king = newKing;
		members.add(new MemberData(king));
		for(MemberData data : members){
			Player player = Bukkit.getPlayer(data.getPlayerID());
			if(player != null)
				Translator.changeKing(player, Bukkit.getOfflinePlayer(newKing).getName());
		}
	}
	
	public void save(String path) throws IOException {
		save12C(path);
	}
	
	public List<Kingdom> getAllies(Kingdoms kds){
		List<Kingdom> list = new ArrayList<Kingdom>();
		for(String ally : allies){
			Kingdom kd = kds.getKingdom(ally);
			if(kd != null)
				list.add(kd);
			else
				allies.remove(ally);
		}
		return list;
	}
	
	public boolean removeAllyFromList(Kingdom formerAlly){
		for(String ally : allies)
			if(ally.equals(formerAlly.getName())){
				allies.remove(ally);
				return true;
			}
		return false;
	}
	
	@SuppressWarnings("unused")
	private void save10C(String path) throws IOException {
		ByteBuffer kingBuffer = ByteBuffer.allocate(16);
		kingBuffer.putLong(king.getLeastSignificantBits());
		kingBuffer.putLong(king.getMostSignificantBits());
		ByteBuffer spawnBuffer;
		if(spawnPoint != null){
			spawnBuffer = ByteBuffer.allocate(40);
			spawnBuffer.putDouble(spawnPoint.x);
			spawnBuffer.putDouble(spawnPoint.y);
			spawnBuffer.putDouble(spawnPoint.z);
			UUID worldID = spawnPoint.world;
			spawnBuffer.putLong(worldID.getLeastSignificantBits());
			spawnBuffer.putLong(worldID.getMostSignificantBits());
		}
		else {
			spawnBuffer = ByteBuffer.allocate(8);
			spawnBuffer.putDouble(Double.NaN);
		}
		ByteBuffer regionBuffer;
		if(centre != null && shape != null){
			regionBuffer = ByteBuffer.allocate(45);
			regionBuffer.putDouble(0, centre.x);
			regionBuffer.putDouble(8, centre.y);
			regionBuffer.putDouble(16, centre.z);
			UUID worldID = centre.world;
			regionBuffer.putLong(24, worldID.getLeastSignificantBits());
			regionBuffer.putLong(32, worldID.getMostSignificantBits());
			regionBuffer.put(40, shape.id);
			regionBuffer.putInt(41, radius);
		}
		else {
			regionBuffer = ByteBuffer.allocate(8);
			regionBuffer.putDouble(Double.NaN);
		}
		ByteBuffer colorBuffer = ByteBuffer.allocate(1 + colors.length * 2);
		colorBuffer.put((byte) (colors.length - 128));
		for(ChatColor color : colors)
			colorBuffer.putChar(color.getChar());
		FileOutputStream output = new FileOutputStream(path + File.separator + name + ".kd");
		output.write(KingdomsPlugin.VERSION_10C);
		output.write(kingBuffer.array());
		output.write(spawnBuffer.array());
		output.write(regionBuffer.array());
		output.write(colorBuffer.array());
		output.write((byte)(members.size() - 128));
		for(MemberData data : members){
			ByteBuffer memberBuffer = data.save();
			output.write((byte) (memberBuffer.capacity() - 128));
			output.write(memberBuffer.array());
		}
		output.write((byte)(invites.size() - 128));
		for(UUID invite : invites){
			ByteBuffer inviteBuffer = ByteBuffer.allocate(16);
			inviteBuffer.putLong(invite.getLeastSignificantBits());
			inviteBuffer.putLong(invite.getMostSignificantBits());
			output.write(inviteBuffer.array());
		}
		output.close();
	}
	
	@SuppressWarnings("unused")
	private void save11C(String path) throws IOException {
		ByteBuffer kingBuffer = ByteBuffer.allocate(16);
		kingBuffer.putLong(king.getLeastSignificantBits());
		kingBuffer.putLong(king.getMostSignificantBits());
		ByteBuffer spawnBuffer;
		if(spawnPoint != null){
			spawnBuffer = ByteBuffer.allocate(40);
			spawnBuffer.putDouble(spawnPoint.x);
			spawnBuffer.putDouble(spawnPoint.y);
			spawnBuffer.putDouble(spawnPoint.z);
			UUID worldID = spawnPoint.world;
			spawnBuffer.putLong(worldID.getLeastSignificantBits());
			spawnBuffer.putLong(worldID.getMostSignificantBits());
		}
		else {
			spawnBuffer = ByteBuffer.allocate(8);
			spawnBuffer.putDouble(Double.NaN);
		}
		ByteBuffer regionBuffer;
		if(centre != null && shape != null){
			regionBuffer = ByteBuffer.allocate(45);
			regionBuffer.putDouble(0, centre.x);
			regionBuffer.putDouble(8, centre.y);
			regionBuffer.putDouble(16, centre.z);
			UUID worldID = centre.world;
			regionBuffer.putLong(24, worldID.getLeastSignificantBits());
			regionBuffer.putLong(32, worldID.getMostSignificantBits());
			regionBuffer.put(40, shape.id);
			regionBuffer.putInt(41, radius);
		}
		else {
			regionBuffer = ByteBuffer.allocate(8);
			regionBuffer.putDouble(Double.NaN);
		}
		ByteBuffer colorBuffer = ByteBuffer.allocate(1 + colors.length * 2);
		colorBuffer.put((byte) (colors.length - 128));
		for(ChatColor color : colors)
			colorBuffer.putChar(color.getChar());
		FileOutputStream output = new FileOutputStream(path + File.separator + name + ".kd");
		output.write(KingdomsPlugin.VERSION_11C);
		output.write(kingBuffer.array());
		output.write(spawnBuffer.array());
		output.write(regionBuffer.array());
		output.write(colorBuffer.array());
		output.write((byte)(members.size() - 128));
		for(MemberData data : members){
			ByteBuffer memberBuffer = data.save();
			output.write((byte) (memberBuffer.capacity() - 128));
			output.write(memberBuffer.array());
		}
		output.write((byte)(invites.size() - 128));
		for(UUID invite : invites){
			ByteBuffer inviteBuffer = ByteBuffer.allocate(16);
			inviteBuffer.putLong(invite.getLeastSignificantBits());
			inviteBuffer.putLong(invite.getMostSignificantBits());
			output.write(inviteBuffer.array());
		}
		ByteBuffer kingTitleBuffer = ByteBuffer.allocate(1 + kingTitle.length() * 2);
		kingTitleBuffer.put((byte)(kingTitle.length() - 128));
		for(int i = 0; i < kingTitle.length(); i++)
			kingTitleBuffer.putChar(kingTitle.charAt(i));
		output.write(kingTitleBuffer.array());
		output.write(info.toBytes());
		output.write(announcements.toBytes());
		output.close();
	}
	
	private void save12C(String path) throws IOException {
		ByteBuffer kingBuffer = ByteBuffer.allocate(16);
		kingBuffer.putLong(king.getLeastSignificantBits());
		kingBuffer.putLong(king.getMostSignificantBits());
		ByteBuffer spawnBuffer;
		if(spawnPoint != null){
			spawnBuffer = ByteBuffer.allocate(40);
			spawnBuffer.putDouble(spawnPoint.x);
			spawnBuffer.putDouble(spawnPoint.y);
			spawnBuffer.putDouble(spawnPoint.z);
			UUID worldID = spawnPoint.world;
			spawnBuffer.putLong(worldID.getLeastSignificantBits());
			spawnBuffer.putLong(worldID.getMostSignificantBits());
		}
		else {
			spawnBuffer = ByteBuffer.allocate(8);
			spawnBuffer.putDouble(Double.NaN);
		}
		ByteBuffer regionBuffer;
		if(centre != null && shape != null){
			regionBuffer = ByteBuffer.allocate(45);
			regionBuffer.putDouble(0, centre.x);
			regionBuffer.putDouble(8, centre.y);
			regionBuffer.putDouble(16, centre.z);
			UUID worldID = centre.world;
			regionBuffer.putLong(24, worldID.getLeastSignificantBits());
			regionBuffer.putLong(32, worldID.getMostSignificantBits());
			regionBuffer.put(40, shape.id);
			regionBuffer.putInt(41, radius);
		}
		else {
			regionBuffer = ByteBuffer.allocate(8);
			regionBuffer.putDouble(Double.NaN);
		}
		ByteBuffer colorBuffer = ByteBuffer.allocate(1 + colors.length * 2);
		colorBuffer.put((byte) (colors.length - 128));
		for(ChatColor color : colors)
			colorBuffer.putChar(color.getChar());
		FileOutputStream output = new FileOutputStream(path + File.separator + name + ".kd");
		output.write(KingdomsPlugin.VERSION_12C);
		output.write(kingBuffer.array());
		output.write(spawnBuffer.array());
		output.write(regionBuffer.array());
		output.write(colorBuffer.array());
		output.write((byte)(members.size() - 128));
		for(MemberData data : members){
			ByteBuffer memberBuffer = data.save();
			output.write((byte) (memberBuffer.capacity() - 128));
			output.write(memberBuffer.array());
		}
		output.write((byte)(invites.size() - 128));
		for(UUID invite : invites){
			ByteBuffer inviteBuffer = ByteBuffer.allocate(16);
			inviteBuffer.putLong(invite.getLeastSignificantBits());
			inviteBuffer.putLong(invite.getMostSignificantBits());
			output.write(inviteBuffer.array());
		}
		ByteBuffer kingTitleBuffer = ByteBuffer.allocate(1 + kingTitle.length() * 2);
		kingTitleBuffer.put((byte)(kingTitle.length() - 128));
		for(int i = 0; i < kingTitle.length(); i++)
			kingTitleBuffer.putChar(kingTitle.charAt(i));
		output.write(kingTitleBuffer.array());
		output.write(info.toBytes());
		output.write(announcements.toBytes());
		int allySize = 1 + allies.size();
		for(String ally : allies)
			allySize += 2 * ally.length();
		ByteBuffer allyBuffer = ByteBuffer.allocate(allySize);
		allyBuffer.put((byte)(allies.size() - 128));
		for(String ally : allies){
			allyBuffer.put((byte)(ally.length() - 128));
			for(int i = 0; i < ally.length(); i++)
				allyBuffer.putChar(ally.charAt(i));
		}
		output.write(allyBuffer.array());
		int allyInviteSize = 1 + allyInvites.size();
		for(String invite : allyInvites)
			allyInviteSize += 2 * invite.length();
		ByteBuffer allyInviteBuffer = ByteBuffer.allocate(allyInviteSize);
		allyInviteBuffer.put((byte) (allyInvites.size() - 128));
		for(String allyInvite : allyInvites){
			allyInviteBuffer.put((byte) (allyInvite.length() - 128));
			for(int i = 0; i < allyInvite.length(); i++)
				allyInviteBuffer.putChar(allyInvite.charAt(i));
		}
		output.write(allyInviteBuffer.array());
		output.close();
	}
	
	public static Kingdom loadKingdom(String path, String name) throws Exception {
		FileInputStream input = new FileInputStream(path + File.separator + name + ".kd");
		byte version = (byte) input.read();
		if(version == KingdomsPlugin.VERSION_10C)
			return load10C(input, name);
		if(version == KingdomsPlugin.VERSION_11C)
			return load11C(input, name);
		if(version == KingdomsPlugin.VERSION_12C)
			return load12C(input, name);
		input.close();
		throw new IllegalArgumentException("Unknown version: " + version);
	}
	
	private static Kingdom load10C(FileInputStream input, String name) throws IOException{
		//start loading king id
		byte[] kingData = new byte[16];
		input.read(kingData);
		ByteBuffer buffer = ByteBuffer.wrap(kingData);
		UUID kingID = new UUID(buffer.getLong(8), buffer.getLong(0));
		//start loading spawn data
		byte[] spawnData = new byte[40];
		input.read(spawnData, 0, 8);
		ByteBuffer spawnBuffer = ByteBuffer.wrap(spawnData);
		double spawnX = spawnBuffer.getDouble(0);
		SafeLocation kingdomSpawn;
		if(spawnX == spawnX){ //check if there was a spawn
			input.read(spawnData, 8, 32);
			spawnBuffer = ByteBuffer.wrap(spawnData);
			double spawnY = spawnBuffer.getDouble(8);
			double spawnZ = spawnBuffer.getDouble(16);
			UUID spawnWorldID = new UUID(spawnBuffer.getLong(32), spawnBuffer.getLong(24));
			kingdomSpawn = new SafeLocation(spawnWorldID, spawnX, spawnY, spawnZ);
		}
		else
			kingdomSpawn = null;
		//start loading region data
		SafeLocation centre;
		Shape shape;
		int radius = 0;
		byte[] regionData = new byte[45];
		input.read(regionData, 0, 8);
		ByteBuffer regionBuffer = ByteBuffer.wrap(regionData);
		double centreX = regionBuffer.getDouble(0);
		if(centreX == centreX){
			input.read(regionData, 8, 37);
			regionBuffer = ByteBuffer.wrap(regionData);
			double centreY = regionBuffer.getDouble(8);
			double centreZ = regionBuffer.getDouble(16);
			UUID centreWorldID = new UUID(regionBuffer.getLong(32), regionBuffer.getLong(24));
			centre = new SafeLocation(centreWorldID, centreX, centreY, centreZ);
			shape = Shape.fromID(regionBuffer.get(40));
			radius = regionBuffer.getInt(41);
		}
		else {
			centre = null;
			shape = null;
		}
		//start loading color data
		ChatColor[] colors = new ChatColor[(byte) input.read() + 128];
		byte[] colorData = new byte[colors.length * 2];
		input.read(colorData);
		ByteBuffer colorBuffer = ByteBuffer.wrap(colorData);
		for(short s = 0; s < colors.length; s++)
			colors[s] = ChatColor.getByChar(colorBuffer.getChar());
		//start loading member data
		int size = (byte) (input.read()) + 128;
		ArrayList<MemberData> memberList = new ArrayList<MemberData>(size);
		for(short s = 0; s < size; s++){
			int capacity = (byte)(input.read()) + 128;
			byte[] memberData = new byte[capacity];
			input.read(memberData);
			ByteBuffer memberBuffer = ByteBuffer.wrap(memberData);
			memberList.add(s, MemberData.load(memberBuffer));
		}
		//start loading invites
		size = (byte)(input.read()) + 128;
		ArrayList<UUID> inviteList = new ArrayList<UUID>(size);
		for(short s = 0; s < size; s++){
			byte[] inviteData = new byte[16];
			input.read(inviteData);
			ByteBuffer inviteBuffer = ByteBuffer.wrap(inviteData);
			inviteList.add(s, new UUID(inviteBuffer.getLong(8), inviteBuffer.getLong(0)));
		}
		input.close();
		return new Kingdom(name, kingID, kingdomSpawn, centre, shape, radius, colors, memberList, inviteList, DEFAULT_KING_TITLE, createDefaultInfo(name), createDefaultAnnouncements(), new ArrayList<String>(), new ArrayList<String>());
	}
	
	private static Kingdom load11C(FileInputStream input, String name) throws Exception{
		//start loading king id
		byte[] kingData = new byte[16];
		input.read(kingData);
		ByteBuffer buffer = ByteBuffer.wrap(kingData);
		UUID kingID = new UUID(buffer.getLong(8), buffer.getLong(0));
		//start loading spawn data
		byte[] spawnData = new byte[40];
		input.read(spawnData, 0, 8);
		ByteBuffer spawnBuffer = ByteBuffer.wrap(spawnData);
		double spawnX = spawnBuffer.getDouble(0);
		SafeLocation kingdomSpawn;
		if(spawnX == spawnX){ //check if there was a spawn
			input.read(spawnData, 8, 32);
			spawnBuffer = ByteBuffer.wrap(spawnData);
			double spawnY = spawnBuffer.getDouble(8);
			double spawnZ = spawnBuffer.getDouble(16);
			UUID spawnWorldID = new UUID(spawnBuffer.getLong(32), spawnBuffer.getLong(24));
			kingdomSpawn = new SafeLocation(spawnWorldID, spawnX, spawnY, spawnZ);
		}
		else
			kingdomSpawn = null;
		//start loading region data
		SafeLocation centre;
		Shape shape;
		int radius = 0;
		byte[] regionData = new byte[45];
		input.read(regionData, 0, 8);
		ByteBuffer regionBuffer = ByteBuffer.wrap(regionData);
		double centreX = regionBuffer.getDouble(0);
		if(centreX == centreX){
			input.read(regionData, 8, 37);
			regionBuffer = ByteBuffer.wrap(regionData);
			double centreY = regionBuffer.getDouble(8);
			double centreZ = regionBuffer.getDouble(16);
			UUID centreWorldID = new UUID(regionBuffer.getLong(32), regionBuffer.getLong(24));
			centre = new SafeLocation(centreWorldID, centreX, centreY, centreZ);
			shape = Shape.fromID(regionBuffer.get(40));
			radius = regionBuffer.getInt(41);
		}
		else {
			centre = null;
			shape = null;
		}
		//start loading color data
		ChatColor[] colors = new ChatColor[(byte) input.read() + 128];
		byte[] colorData = new byte[colors.length * 2];
		input.read(colorData);
		ByteBuffer colorBuffer = ByteBuffer.wrap(colorData);
		for(short s = 0; s < colors.length; s++)
			colors[s] = ChatColor.getByChar(colorBuffer.getChar());
		//start loading member data
		int size = (byte) (input.read()) + 128;
		ArrayList<MemberData> memberList = new ArrayList<MemberData>(size);
		for(short s = 0; s < size; s++){
			int capacity = (byte)(input.read()) + 128;
			byte[] memberData = new byte[capacity];
			input.read(memberData);
			ByteBuffer memberBuffer = ByteBuffer.wrap(memberData);
			memberList.add(s, MemberData.load(memberBuffer));
		}
		//start loading invites
		size = (byte)(input.read()) + 128;
		ArrayList<UUID> inviteList = new ArrayList<UUID>(size);
		for(short s = 0; s < size; s++){
			byte[] inviteData = new byte[16];
			input.read(inviteData);
			ByteBuffer inviteBuffer = ByteBuffer.wrap(inviteData);
			inviteList.add(s, new UUID(inviteBuffer.getLong(8), inviteBuffer.getLong(0)));
		}
		size = (byte)(input.read()) + 128;
		byte[] kingTitleData = new byte[size * 2];
		input.read(kingTitleData);
		ByteBuffer kingTitleBuffer = ByteBuffer.wrap(kingTitleData);
		char[] array = new char[size];
		for(int i = 0; i < size; i++)
			array[i] = kingTitleBuffer.getChar();
		Book info = new Book(input);
		Book announcements = new Book(input);
		input.close();
		return new Kingdom(name, kingID, kingdomSpawn, centre, shape, radius, colors, memberList, inviteList, new String(array), info, announcements, new ArrayList<String>(), new ArrayList<String>());
	}
	
	private static Kingdom load12C(FileInputStream input, String name) throws Exception{
		//start loading king id
		byte[] kingData = new byte[16];
		input.read(kingData);
		ByteBuffer buffer = ByteBuffer.wrap(kingData);
		UUID kingID = new UUID(buffer.getLong(8), buffer.getLong(0));
		//start loading spawn data
		byte[] spawnData = new byte[40];
		input.read(spawnData, 0, 8);
		ByteBuffer spawnBuffer = ByteBuffer.wrap(spawnData);
		double spawnX = spawnBuffer.getDouble(0);
		SafeLocation kingdomSpawn;
		if(spawnX == spawnX){ //check if there was a spawn
			input.read(spawnData, 8, 32);
			spawnBuffer = ByteBuffer.wrap(spawnData);
			double spawnY = spawnBuffer.getDouble(8);
			double spawnZ = spawnBuffer.getDouble(16);
			UUID spawnWorldID = new UUID(spawnBuffer.getLong(32), spawnBuffer.getLong(24));
			kingdomSpawn = new SafeLocation(spawnWorldID, spawnX, spawnY, spawnZ);
		}
		else
			kingdomSpawn = null;
		//start loading region data
		SafeLocation centre;
		Shape shape;
		int radius = 0;
		byte[] regionData = new byte[45];
		input.read(regionData, 0, 8);
		ByteBuffer regionBuffer = ByteBuffer.wrap(regionData);
		double centreX = regionBuffer.getDouble(0);
		if(centreX == centreX){
			input.read(regionData, 8, 37);
			regionBuffer = ByteBuffer.wrap(regionData);
			double centreY = regionBuffer.getDouble(8);
			double centreZ = regionBuffer.getDouble(16);
			UUID centreWorldID = new UUID(regionBuffer.getLong(32), regionBuffer.getLong(24));
			centre = new SafeLocation(centreWorldID, centreX, centreY, centreZ);
			shape = Shape.fromID(regionBuffer.get(40));
			radius = regionBuffer.getInt(41);
		}
		else {
			centre = null;
			shape = null;
		}
		//start loading color data
		ChatColor[] colors = new ChatColor[(byte) input.read() + 128];
		byte[] colorData = new byte[colors.length * 2];
		input.read(colorData);
		ByteBuffer colorBuffer = ByteBuffer.wrap(colorData);
		for(short s = 0; s < colors.length; s++)
			colors[s] = ChatColor.getByChar(colorBuffer.getChar());
		//start loading member data
		int size = (byte) (input.read()) + 128;
		ArrayList<MemberData> memberList = new ArrayList<MemberData>(size);
		for(short s = 0; s < size; s++){
			int capacity = (byte)(input.read()) + 128;
			byte[] memberData = new byte[capacity];
			input.read(memberData);
			ByteBuffer memberBuffer = ByteBuffer.wrap(memberData);
			memberList.add(s, MemberData.load(memberBuffer));
		}
		//start loading invites
		size = (byte)(input.read()) + 128;
		ArrayList<UUID> inviteList = new ArrayList<UUID>(size);
		for(short s = 0; s < size; s++){
			byte[] inviteData = new byte[16];
			input.read(inviteData);
			ByteBuffer inviteBuffer = ByteBuffer.wrap(inviteData);
			inviteList.add(s, new UUID(inviteBuffer.getLong(8), inviteBuffer.getLong(0)));
		}
		size = (byte)(input.read()) + 128;
		byte[] kingTitleData = new byte[size * 2];
		input.read(kingTitleData);
		ByteBuffer kingTitleBuffer = ByteBuffer.wrap(kingTitleData);
		char[] array = new char[size];
		for(int i = 0; i < size; i++)
			array[i] = kingTitleBuffer.getChar();
		Book info = new Book(input);
		Book announcements = new Book(input);
		size = (byte)(input.read()) + 128;
		ArrayList<String> allies = new ArrayList<String>(size);
		for(int i = 0; i < size; i++){
			int length = (byte)(input.read()) + 128;
			ByteBuffer nameBuffer = ByteBuffer.allocate(length * 2);
			char[] chars = new char[length];
			for(int j = 0; j < length; j++)
				chars[j] = nameBuffer.getChar();
			allies.add(new String(chars));
		}
		size = (byte)(input.read()) + 128;
		ArrayList<String> allyInvites = new ArrayList<String>(size);
		for(int i = 0; i < size; i++){
			int length = (byte)(input.read()) + 128;
			ByteBuffer nameBuffer = ByteBuffer.allocate(length * 2);
			char[] chars = new char[length];
			for(int j = 0; j < length; j++)
				chars[j] = nameBuffer.getChar();
			allyInvites.add(new String(chars));
		}
		input.close();
		return new Kingdom(name, kingID, kingdomSpawn, centre, shape, radius, colors, memberList, inviteList, new String(array), info, announcements, allies, allyInvites);
	}
	
	private void setColoredName(){
		int n = 0;
		int c = 0;
		coloredName = "";
		while(n < name.length()){
			coloredName += colors[c];
			coloredName += name.charAt(n);
			++n;
			++c;
			if(c >= colors.length)
				c = 0;
		}
	}
	
	void clearMembers(Kingdoms kds){
		for(MemberData data : members){
			kds.setPlayerKingdom(data.getPlayerID(), null);
			Player player = data.getPlayer();
			if(player != null)
				KDEventHandler.updatePlayerName(player, kds);
		}
		kds.setPlayerKingdom(king, null);
		Player player = Bukkit.getPlayer(king);
		if(player != null)
			KDEventHandler.updatePlayerName(player, kds);
	}
	
	void deleteData(String path){
		new File(path + File.separator + name + ".kd").delete();
	}
	
	public static enum Shape {
		
		SQUARE((byte) -128),
		CIRCLE((byte) -127);
		
		public static final Shape fromID(byte id){
			if(id == -128)
				return SQUARE;
			if(id == -127)
				return CIRCLE;
			throw new IllegalArgumentException("Invalid Shape ID: " + id);
		}
		
		public static boolean doIntersect(SafeLocation centre1, SafeLocation centre2, Shape shape1, Shape shape2, int radius1, int radius2){
			if(!centre1.world.equals(centre2.world))
				return false;
			if(shape1 == SQUARE && shape2 == SQUARE)
				return Math.abs(centre1.x - centre2.x) <= radius1 + radius2 && Math.abs(centre1.z - centre2.z) <= radius1 + radius2;
			if(shape1 == CIRCLE && shape2 == CIRCLE)
				return Math.hypot(centre1.x - centre2.x, centre1.z - centre2.z) <= radius1 + radius2;
			if(shape1 == CIRCLE && shape2 == SQUARE)
				return squareWithCircle(centre2, radius2, centre1, radius1);
			if(shape1 == SQUARE && shape2 == CIRCLE)
				return squareWithCircle(centre1, radius1, centre2, radius2);
			throw new RuntimeException("Can't check for intersection between " + shape1 + " and " + shape2 + "!");
		}
		
		public static boolean isInside(SafeLocation location, SafeLocation centre, Shape shape, int radius){
			if(!location.world.equals(centre.world))
				return false;
			if(shape == SQUARE)
				return Math.abs(centre.x - location.x) <= radius && Math.abs(centre.z - location.z) <= radius;
			if(shape == CIRCLE)
				return Math.hypot(centre.x - location.x, centre.z - location.z) <= radius;
			throw new RuntimeException("Can't check if location " + location + " is inside shape " + shape + "!");
		}
		
		private static boolean squareWithCircle(SafeLocation locSquare, float radSquare, SafeLocation locCircle, float radCircle){
			double z = locCircle.z;
			if(z > locSquare.z + radSquare)
				z = locSquare.z + radSquare;
			if(z < locSquare.z - radSquare)
				z = locSquare.z - radSquare;
			double x = locCircle.x;
			if(x > locSquare.x + radSquare)
				x = locSquare.x + radSquare;
			if(x < locSquare.x - radSquare)
				x = locSquare.x - radSquare;
			return Math.hypot(locCircle.x - x, locCircle.z - z) <= radCircle;
		}
		
		public final byte id;
		
		Shape(byte id){
			this.id = id;
		}
		
		public String getDutchName(){
			if(this == SQUARE)
				return "vierkant";
			if(this == CIRCLE)
				return "cirkel";
			return null;
		}
	}
	
	public static class SafeLocation {
		
		private final double x;
		private final double y;
		private final double z;
		
		private final UUID world;
		
		public SafeLocation(UUID world, double x, double y, double z){
			if(world == null)
				throw new NullPointerException("world");
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		private SafeLocation(Location location){
			this(location.getWorld().getUID(), location.getX(), location.getY(), location.getZ());
		}
		
		private Location getLocation(boolean loadWorldIfNeeded){
			World bukkitWorld = Bukkit.getWorld(world);
			if(bukkitWorld == null){
				if(!loadWorldIfNeeded)
					return null;
				loadMVWorld(world);
				bukkitWorld = Bukkit.getWorld(world);
				if(bukkitWorld == null)
					return null;
			}
			return new Location(bukkitWorld, x, y, z);
		}
		
		public double getX(){
			return x;
		}
		
		public double getY(){
			return y;
		}
		
		public double getZ(){
			return z;
		}
		
		public UUID getWorld(){
			return world;
		}
	}
	
	private static void loadMVWorld(UUID id){
		/*
		try {
			JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
			Object mvWorldManager = plugin.getClass().getMethod("getMVWorldManager").invoke(plugin);
			mvWorldManager.getClass().getMethod("loadWorlds", boolean.class).invoke(mvWorldManager, false);
		} catch(Exception ex){
			ex.printStackTrace();//This does the job, but it might be better to retrieve the world name from Multiverse
			//and only load that world rather than all worlds.
		}
		*/
		Bukkit.getLogger().info("Trying to load the world with uid " + id + "...");
		String worldName = null;
		File folder = KingdomsPlugin.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
		File[] files = folder.listFiles();
		if(files != null){
			for(File file : files){
				if(checkWorldFolder(file, id)){
					worldName = file.getName();
					break;
				}
			}
		}
		else
			Bukkit.getLogger().warning("The file " + folder + " is no folder?");
		if(worldName != null){
			try {
				JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
				Object mvWorldManager = plugin.getClass().getMethod("getMVWorldManager").invoke(plugin);
				mvWorldManager.getClass().getMethod("loadWorld", String.class).invoke(mvWorldManager, worldName);
				Bukkit.getLogger().info("The plugin Kingdoms forced Multiverse to load world " + worldName);
			} catch(Exception ex){
				Bukkit.getLogger().info("Failed to let Multiverse load world " + worldName + ": " + ex.getMessage());
				Bukkit.getLogger().info("This is ok if Multiverse is not installed.");
			}
		}
		else
			Bukkit.getLogger().warning("Can't find a world with uid " + id);
	}
	
	private static boolean checkWorldFolder(File file, UUID id){
		File[] files = file.listFiles();
		if(files != null){
			for(File sub : files){
				if(sub.getName().equals("uid.dat")){
					if(sub.length() == 16){
						byte[] bytes = new byte[16];
						try {
							FileInputStream input = new FileInputStream(sub);
							input.read(bytes);
							input.close();
							ByteBuffer buffer = ByteBuffer.wrap(bytes);
							UUID uuid = new UUID(buffer.getLong(), buffer.getLong());
							System.out.println("The UUID of world " + file.getName() + " is " + uuid);
							return id.equals(uuid);
						} catch(IOException ioex){
							Bukkit.getLogger().warning("Could not retrieve uid of world " + file.getName() + ": " + ioex.getMessage());
						}
					}
					else
						Bukkit.getLogger().warning("The uid.dat file of world " + file.getName() + " doesn't have the right length.");
				}
			}
		}
		return false;
	}
}
