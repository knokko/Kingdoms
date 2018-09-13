package nl.knokko.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import nl.knokko.kingdoms.Kingdom;
import nl.knokko.kingdoms.Kingdoms;
import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GriefLogger implements Listener {
	
	private static final String FOLDER = "Grief Logger";
	
	//block log identifiers
	private static final byte ID_BREAK = -128;
	private static final byte ID_PLACE = -127;
	private static final byte IDB_INTERACT = -126;
	private static final byte ID_BUCKET_FILL = -125;
	private static final byte ID_BUCKET_EMPTY = -124;
	private static final byte IDB_ENTITY_INTERACT = -123;
	private static final byte ID_EXPLODE = -122;
	
	//entity log identifiers
	private static final byte IDE_INTERACT = -128;
	private static final byte ID_ATTACK = -127;
	private static final byte ID_ATTACKED = -126;
	
	private Map<String, BlockLog> blockLogs = new HashMap<String, BlockLog>();
	private Map<String, EntityLog> entityLogs = new HashMap<String, EntityLog>();
	
	private final KingdomsPlugin plug;

	public GriefLogger(KingdomsPlugin plugin) {
		plug = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event){
		reportBlockAction(ID_PLACE, event.getPlayer(), event.getBlockPlaced());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event){
		reportBlockAction(ID_BREAK, event.getPlayer(), event.getBlock());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event){
		if(!plug.getSettings().logGriefing())
			return;
		Player player = Utils.getPlayer(event.getEntity());
		if(player != null){
			for(Block block : event.blockList())
				reportBlockAction(ID_EXPLODE, player, block);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityInteract(EntityInteractEvent event){
		Player player = Utils.getPlayer(event.getEntity());
		if(player != null)
			reportBlockAction(IDB_ENTITY_INTERACT, player, event.getBlock());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityAttack(EntityDamageByEntityEvent event){
		Player attacker = Utils.getPlayer(event.getDamager());
		if(attacker != null){
			reportEntityAction(ID_ATTACK, attacker, event.getEntity());
			return;//prevent double logged attacks
		}
		Player victim = Utils.getPlayer(event.getEntity());
		if(victim != null){
			Kingdom kd = plug.getKingdoms().getLocationKingdom(victim.getLocation());
			if(kd != null)
				reportEntityAction(ID_ATTACKED, victim.getUniqueId(), kd, event.getDamager(), System.currentTimeMillis(), victim.getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEntityInteract(PlayerInteractEntityEvent event){
		reportEntityAction(IDE_INTERACT, event.getPlayer(), event.getRightClicked());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event){
		reportBlockAction(IDB_INTERACT, event.getPlayer(), event.getClickedBlock());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBucketFill(PlayerBucketFillEvent event){
		reportBlockAction(ID_BUCKET_FILL, event.getPlayer(), event.getBlockClicked());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBucketEmpty(PlayerBucketEmptyEvent event){
		reportBlockAction(ID_BUCKET_EMPTY, event.getPlayer(), event.getBlockClicked());
	}
	
	public void save(){
		new File(plug.getDataFolder().getAbsolutePath() + File.separator + FOLDER).mkdirs();
		Iterator<Entry<String, BlockLog>> iterator = blockLogs.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String,BlockLog> entry = iterator.next();
			entry.getValue().save(plug, entry.getKey());
		}
		Iterator<Entry<String, EntityLog>> it = entityLogs.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, EntityLog> entry = it.next();
			entry.getValue().save(plug, entry.getKey());
		}
	}
	
	public void logKingdom(Kingdom kd, boolean ignoreOwnMembers){
		getBlockLog(kd).saveToText(plug, kd, ignoreOwnMembers);
		getEntityLog(kd).saveToText(plug, kd, ignoreOwnMembers);
	}
	
	private void logPlayerBlocks(String playerName, boolean ignoreOwnKingdom){
		ArrayList<String> lines = new ArrayList<String>();
		File folder = new File(plug.getDataFolder().getAbsolutePath() + File.separator + FOLDER);
		File[] files = folder.listFiles();
		for(File file : files){
			if(file.getName().endsWith(".blg")){
				Kingdom kd = plug.getKingdoms().getKingdom(file.getName().substring(0, file.getName().length() - 4));
				if(kd != null)
					lines.addAll(getBlockLog(kd).produceLines(file, plug, playerName, kd, ignoreOwnKingdom));
			}
		}
		try {
			PrintWriter writer = new PrintWriter(plug.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + "block log of " + playerName + ".txt");
			for(String line : lines)
				writer.println(line);
			writer.close();
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void logPlayer(String playerName, boolean ignoreOwnKingdom){
		logPlayerBlocks(playerName, ignoreOwnKingdom);
		logPlayerEntities(playerName, ignoreOwnKingdom);
	}
	
	private void logPlayerEntities(String playerName, boolean ignoreOwnKingdom){
		ArrayList<String> lines = new ArrayList<String>();
		File folder = new File(plug.getDataFolder().getAbsolutePath() + File.separator + FOLDER);
		File[] files = folder.listFiles();
		for(File file : files){
			if(file.getName().endsWith(".elg")){
				Kingdom kd = plug.getKingdoms().getKingdom(file.getName().substring(0, file.getName().length() - 4));
				if(kd != null)
					lines.addAll(getEntityLog(kd).produceLines(file, plug, playerName, kd, ignoreOwnKingdom));
			}
		}
		try {
			PrintWriter writer = new PrintWriter(plug.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + "entity log of " + playerName + ".txt");
			for(String line : lines)
				writer.println(line);
			writer.close();
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void reportBlockAction(byte type, UUID playerID, Kingdom kd, Material old, long time, int x, int y, int z){
		if(!plug.getSettings().logOwnKingdomEdit() && plug.getKingdoms().getPlayerKingdom(playerID) == kd)
			return;
		if(plug.getSettings().logGriefing())
			getBlockLog(kd).addAction(type, playerID, old, time, x, y, z);
	}
	
	public void reportBlockAction(byte type, UUID playerID, Kingdom kd, Material old, long time, Location location){
		reportBlockAction(type, playerID, kd, old, time, location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public void reportBlockAction(byte type, Player player, Block block){
		Kingdom kd = plug.getKingdoms().getLocationKingdom(block.getLocation());
		if(kd != null)
			reportBlockAction(type, player.getUniqueId(), kd, block.getType(), System.currentTimeMillis(), block.getLocation());
	}
	
	public void reportEntityAction(byte type, UUID playerID, Kingdom kd, Entity target, long time, int x, int y, int z){
		if(plug.getSettings().logGriefing())
			getEntityLog(kd).addAction(type, playerID, target, time, x, y, z);
	}
	
	public void reportEntityAction(byte type, UUID playerID, Kingdom kd, Entity target, long time, Location location){
		reportEntityAction(type, playerID, kd, target, time, location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public void reportEntityAction(byte type, Player player, Entity target){
		Kingdom kd = plug.getKingdoms().getLocationKingdom(target.getLocation());
		if(kd != null)
			reportEntityAction(type, player.getUniqueId(), kd, target, System.currentTimeMillis(), target.getLocation());
	}
	
	private BlockLog getBlockLog(Kingdom kd){
		BlockLog log = blockLogs.get(kd.getName());
		if(log == null){
			log = new BlockLog();
			blockLogs.put(kd.getName(), log);
		}
		return log;
	}
	
	private EntityLog getEntityLog(Kingdom kd){
		EntityLog log = entityLogs.get(kd.getName());
		if(log == null){
			log = new EntityLog();
			entityLogs.put(kd.getName(), log);
		}
		return log;
	}
	
	private static class BlockLog {
		
		private static final int BYTES = 1 + 16 + 2 + 8 + 4 + 4 + 4;
		
		private static byte[] toBytes(byte type, UUID playerID, Material old, long time, int x, int y, int z){
			ByteBuffer buffer = ByteBuffer.allocate(BYTES);
			buffer.put(type);
			buffer.putLong(playerID.getMostSignificantBits());
			buffer.putLong(playerID.getLeastSignificantBits());
			buffer.putShort((short) old.ordinal());//there aren't more than 32000 materials
			buffer.putLong(time);
			buffer.putInt(x);
			buffer.putInt(y);
			buffer.putInt(z);
			return buffer.array();
		}
		
		private static byte[] loadBytes(KingdomsPlugin plugin, String kdName){
			try {
				File file = new File(getAbsolutePath(plugin, kdName));
				if(file.length() > Integer.MAX_VALUE)
					throw new RuntimeException("Grief log too long! (" + file.getAbsolutePath() + ") [" + file.length() + " bytes]");
				byte[] data = new byte[(int) file.length()];
				FileInputStream input = new FileInputStream(file);
				input.read(data);
				input.close();
				return data;
			} catch(Exception ex){
				Bukkit.getLogger().warning("No previous block log could be found for " + kdName + ": " + ex.getMessage());
				return null;
			}
		}
		
		private static byte[][] split(byte[] data){
			if(data == null)
				return null;
			byte[][] splitted = new byte[data.length / BYTES][];
			for(int i = 0; i < splitted.length; i++){
				splitted[i] = Arrays.copyOfRange(data, i * BYTES, i * BYTES + BYTES);
			}
			return splitted;
		}
		
		private static String toLine(byte[] line, Kingdom kd, Kingdoms kds, boolean ignoreOwnMembers){
			ByteBuffer buffer = ByteBuffer.wrap(line);
			byte type = buffer.get();
			UUID id = new UUID(buffer.getLong(), buffer.getLong());
			short mater = buffer.getShort();
			long time = buffer.getLong();
			int x = buffer.getInt();
			int y = buffer.getInt();
			int z = buffer.getInt();
			OfflinePlayer player = Bukkit.getOfflinePlayer(id);
			if(ignoreOwnMembers && kds.getPlayerKingdom(player.getUniqueId()) == kd)
				return null;
			String sMaterial;
			if(mater < 0 || mater >= Material.values().length)
				sMaterial = "Unknown Material";
			else
				sMaterial = Material.values()[mater].name().toLowerCase();
			String playerName = player != null ? player.getName() : "Unknown Player";
			Calendar cal = new Calendar.Builder().setInstant(time).build();
			String sTime = "[" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "] ";
			String sLoc = " at (" + x + "," + y + "," + z + ")";
			if(type == ID_BREAK)
				return sTime + playerName + " broke " + sMaterial + sLoc;
			if(type == ID_PLACE)
				return sTime + playerName + " placed " + sMaterial + sLoc;
			if(type == IDB_INTERACT)
				return sTime + playerName + " interacted with " + sMaterial + sLoc;
			if(type == ID_BUCKET_FILL)
				return sTime + playerName + " filled a bucket with " + sMaterial + sLoc;
			if(type == ID_BUCKET_EMPTY)
				return sTime + playerName + " emptied a bucket with " + sMaterial + sLoc;
			throw new RuntimeException("Unknown type: " + type);
		}
		
		private static void addLine(byte[] data, KingdomsPlugin plug, String playerName, Kingdom kd, ArrayList<String> lines, boolean ignoreOwnKD){
			ByteBuffer buffer = ByteBuffer.wrap(data, 1, 16);
			UUID id = new UUID(buffer.getLong(), buffer.getLong());
			if(Bukkit.getOfflinePlayer(id).getName().equals(playerName)){
				String line = toLine(data, kd, plug.getKingdoms(), ignoreOwnKD);
				line += " (" + kd.getName() + ")";
				lines.add(line);
			}
		}
		
		private static String getAbsolutePath(KingdomsPlugin plugin, String kdName){
			return plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + kdName + ".blg";
		}
		
		private static String getTextPath(KingdomsPlugin plugin, String kdName){
			return plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + kdName + " block log.txt";
		}
		
		private List<byte[]> bytes = new ArrayList<byte[]>();
		
		private void addAction(byte type, UUID playerID, Material old, long time, int x, int y, int z){
			bytes.add(toBytes(type, playerID, old, time, x, y, z));
		}
		
		private void save(KingdomsPlugin plugin, String kdName){
			try {
				byte[] previousData = loadBytes(plugin, kdName);
				FileOutputStream output = new FileOutputStream(getAbsolutePath(plugin, kdName));
				if(previousData != null)
					output.write(previousData);
				for(byte[] data : bytes)
					output.write(data);
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void saveToText(KingdomsPlugin plugin, Kingdom kd, boolean ignoreOwnMembers){
			String[] lines = produceLines(plugin, kd, ignoreOwnMembers);
			try {
				PrintWriter writer = new PrintWriter(getTextPath(plugin, kd.getName()));
				for(String line : lines)
					if(line != null)
						writer.println(line);
				writer.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		private String[] produceLines(KingdomsPlugin plug, Kingdom kd, boolean ignoreOwnMembers){
			byte[][] firstData = split(loadBytes(plug, kd.getName()));
			if(firstData != null){
				String[] lines = new String[firstData.length + bytes.size()];
				for(int i = 0; i < firstData.length; i++){
					lines[i] = toLine(firstData[i], kd, plug.getKingdoms(), ignoreOwnMembers);
				}
				for(int i = 0; i < bytes.size(); i++){
					lines[i + firstData.length] = toLine(bytes.get(i), kd, plug.getKingdoms(), ignoreOwnMembers);
				}
				return lines;
			}
			String[] lines = new String[bytes.size()];
			for(int i = 0; i < bytes.size(); i++)
				lines[i] = toLine(bytes.get(i), kd, plug.getKingdoms(), ignoreOwnMembers);
			return lines;
		}
		
		private ArrayList<String> produceLines(File file, KingdomsPlugin plug, String playerName, Kingdom kd, boolean ignoreOwnKD){
			ArrayList<String> lines = new ArrayList<String>();
			byte[][] firstData = split(loadBytes(plug, kd.getName()));
			if(firstData != null)
				for(int i = 0; i < firstData.length; i++)
					addLine(firstData[i], plug, playerName, kd, lines, ignoreOwnKD);
			for(byte[] data : bytes)
				addLine(data, plug, playerName, kd, lines, ignoreOwnKD);
			return lines;
		}
	}
	
	private static class EntityLog {
		
		private static final int BYTES = 1 + 16 + 2 + 8 + 4 + 4 + 4;
		
		private static byte[] toBytes(byte type, UUID playerID, Entity target, long time, int x, int y, int z){
			ByteBuffer buffer = ByteBuffer.allocate(BYTES);
			buffer.put(type);
			buffer.putLong(playerID.getMostSignificantBits());
			buffer.putLong(playerID.getLeastSignificantBits());
			buffer.putShort((short) target.getType().ordinal());//there aren't more than 32000 materials
			buffer.putLong(time);
			buffer.putInt(x);
			buffer.putInt(y);
			buffer.putInt(z);
			return buffer.array();
		}
		
		private static byte[] loadBytes(KingdomsPlugin plugin, String kdName){
			try {
				File file = new File(getAbsolutePath(plugin, kdName));
				if(file.length() > Integer.MAX_VALUE)
					throw new RuntimeException("Grief log too long! (" + file.getAbsolutePath() + ") [" + file.length() + " bytes]");
				byte[] data = new byte[(int) file.length()];
				FileInputStream input = new FileInputStream(file);
				input.read(data);
				input.close();
				return data;
			} catch(Exception ex){
				Bukkit.getLogger().warning("No previous entity log could be found for " + kdName + ": " + ex.getMessage());
				return null;
			}
		}
		
		private static byte[][] split(byte[] data){
			if(data == null)
				return null;
			byte[][] splitted = new byte[data.length / BYTES][];
			for(int i = 0; i < splitted.length; i++){
				splitted[i] = Arrays.copyOfRange(data, i * BYTES, i * BYTES + BYTES);
			}
			return splitted;
		}
		
		private static String toLine(byte[] line, Kingdom kd, Kingdoms kds, boolean ignoreOwnMembers){
			ByteBuffer buffer = ByteBuffer.wrap(line);
			byte type = buffer.get();
			UUID id = new UUID(buffer.getLong(), buffer.getLong());
			short entityType = buffer.getShort();
			long time = buffer.getLong();
			int x = buffer.getInt();
			int y = buffer.getInt();
			int z = buffer.getInt();
			OfflinePlayer player = Bukkit.getOfflinePlayer(id);
			if(ignoreOwnMembers && kds.getPlayerKingdom(player.getUniqueId()) == kd)
				return null;
			String sEntity;
			if(entityType < 0 || entityType >= EntityType.values().length)
				sEntity = "Unknown Material";
			else
				sEntity = EntityType.values()[entityType].name().toLowerCase();
			String playerName = player != null ? player.getName() : "Unknown Player";
			Calendar cal = new Calendar.Builder().setInstant(time).build();
			String sTime = "[" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + (cal.get(Calendar.YEAR) + 1) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "] ";
			String sLoc = " at (" + x + "," + y + "," + z + ")";
			if(type == IDB_INTERACT)
				return sTime + playerName + " interacted with " + sEntity + sLoc;
			if(type == ID_ATTACK)
				return sTime + playerName + " attacked " + sEntity + sLoc;
			if(type == ID_ATTACKED)
				return sTime + playerName + " was attacked by " + sEntity + sLoc;
			throw new RuntimeException("Unknown type: " + type);
		}
		
		private static void addLine(byte[] data, KingdomsPlugin plug, String playerName, Kingdom kd, ArrayList<String> lines, boolean ignoreOwnKD){
			ByteBuffer buffer = ByteBuffer.wrap(data, 1, 16);
			UUID id = new UUID(buffer.getLong(), buffer.getLong());
			if(Bukkit.getOfflinePlayer(id).getName().equals(playerName)){
				String line = toLine(data, kd, plug.getKingdoms(), ignoreOwnKD);
				line += " (" + kd.getName() + ")";
				lines.add(line);
			}
		}
		
		private static String getAbsolutePath(KingdomsPlugin plugin, String kdName){
			return plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + kdName + ".elg";
		}
		
		private static String getTextPath(KingdomsPlugin plugin, String kdName){
			return plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + kdName + " entity log.txt";
		}
		
		private List<byte[]> bytes = new ArrayList<byte[]>();
		
		private void addAction(byte type, UUID playerID, Entity entity, long time, int x, int y, int z){
			bytes.add(toBytes(type, playerID, entity, time, x, y, z));
		}
		
		private void save(KingdomsPlugin plugin, String kdName){
			try {
				byte[] previousData = loadBytes(plugin, kdName);
				FileOutputStream output = new FileOutputStream(getAbsolutePath(plugin, kdName));
				if(previousData != null)
					output.write(previousData);
				for(byte[] data : bytes)
					output.write(data);
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void saveToText(KingdomsPlugin plugin, Kingdom kd, boolean ignoreOwnMembers){
			String[] lines = produceBookLines(plugin, kd, ignoreOwnMembers);
			try {
				PrintWriter writer = new PrintWriter(getTextPath(plugin, kd.getName()));
				for(String line : lines)
					if(line != null)
						writer.println(line);
				writer.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		private String[] produceBookLines(KingdomsPlugin plug, Kingdom kd, boolean ignoreOwnMembers){
			byte[][] firstData = split(loadBytes(plug, kd.getName()));
			if(firstData != null){
				String[] lines = new String[firstData.length + bytes.size()];
				for(int i = 0; i < firstData.length; i++){
					String line = toLine(firstData[i], kd, plug.getKingdoms(), ignoreOwnMembers);
					lines[i] = line;
				}
				for(int i = 0; i < bytes.size(); i++){
					String line = toLine(bytes.get(i), kd, plug.getKingdoms(), ignoreOwnMembers);
					lines[i + firstData.length] = line;
				}
				return lines;
			}
			String[] lines = new String[bytes.size()];
			for(int i = 0; i < bytes.size(); i++){
				String line = toLine(bytes.get(i), kd, plug.getKingdoms(), ignoreOwnMembers);
				lines[i] = line;
			}
			return lines;
		}
		
		private ArrayList<String> produceLines(File file, KingdomsPlugin plug, String playerName, Kingdom kd, boolean ignoreOwnKD){
			ArrayList<String> lines = new ArrayList<String>();
			byte[][] firstData = split(loadBytes(plug, kd.getName()));
			if(firstData != null)
				for(int i = 0; i < firstData.length; i++)
					addLine(firstData[i], plug, playerName, kd, lines, ignoreOwnKD);
			for(byte[] data : bytes)
				addLine(data, plug, playerName, kd, lines, ignoreOwnKD);
			return lines;
		}
	}
}
