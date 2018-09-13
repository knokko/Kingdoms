package nl.knokko.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import nl.knokko.commands.*;
import nl.knokko.data.Settings;
import nl.knokko.kingdoms.Kingdoms;
import nl.knokko.logger.GriefLogger;
import nl.knokko.logger.ItemLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class KingdomsPlugin extends JavaPlugin {
	
	public static final byte VERSION_10C = -128;
	public static final byte VERSION_11C = -127;
	public static final byte VERSION_12C = -126;
	
	private static KingdomsPlugin instance;
	
	public static KingdomsPlugin getInstance(){
		if(instance == null)
			throw new RuntimeException("Can't get plugin instance while disabled!");
		return instance;
	}
	
	private ArrayList<ServerTitle> titles = new ArrayList<ServerTitle>();
	private ArrayList<SpawningPlayer> spawningPlayers = new ArrayList<SpawningPlayer>();
	
	private Kingdoms kingdoms = new Kingdoms();
	private Settings settings = new Settings();
	private GriefLogger griefLogger = new GriefLogger(this);
	private ItemLogger itemLogger = new ItemLogger(this);

	public KingdomsPlugin() {}

	public KingdomsPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}
	
	@Override
	public void onEnable(){
		instance = this;
		load();
		getServer().getPluginManager().registerEvents(new KDEventHandler(this), this);
		getServer().getPluginManager().registerEvents(new GuiEventHandler(this), this);
		getServer().getPluginManager().registerEvents(griefLogger, this);
		getServer().getPluginManager().registerEvents(itemLogger, this);
		getCommand("kingdom").setExecutor(new CommandKingdom(this));
		getCommand("kingdomop").setExecutor(new CommandKingdomOP(this));
		getCommand("title").setExecutor(new CommandTitle(this));
		getCommand("grieflogger").setExecutor(new CommandGriefLogger(this));
		getCommand("itemlogger").setExecutor(new CommandItemLogger(this));
		getCommand("book").setExecutor(new CommandBook());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
            public void run() {
                for(int i = 0; i < spawningPlayers.size(); i++){
                	SpawningPlayer sp = spawningPlayers.get(i);
                	if(sp.secondsLeft > 0)
                		sp.secondsLeft--;
                	if(sp.secondsLeft <= 0){
                		Player player = Bukkit.getPlayer(sp.id);
                		if(player != null)
                			CommandKingdom.toKDSpawnNow(player);
                		spawningPlayers.remove(i);
                		i--;
                	}
                }
            }
        }, 0, 20);
	}
	
	
	
	@Override
	public void onDisable(){
		Bukkit.getScheduler().cancelTasks(this);
		save();
		instance = null;
	}
	
	public boolean save(){
		getDataFolder().mkdir();
		boolean succes = true;
		if(!kingdoms.save(getDataFolder().getAbsolutePath()))
			succes = false;
		if(!settings.save(getDataFolder().getAbsolutePath()))
			succes = false;
		griefLogger.save();
		itemLogger.save();
		try {
			FileOutputStream output = new FileOutputStream(getDataFolder().getAbsolutePath() + File.separator + "titles.data");
			for(ServerTitle title : titles)
				output.write(title.data());
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			succes = false;
		}
		return succes;
	}
	
	public boolean load(){
		if(!getDataFolder().exists()){
			Bukkit.getLogger().log(Level.WARNING, "Can't find data folder for Kingdoms Core.");
			Bukkit.getLogger().log(Level.WARNING, "This is fine if this is the first time this plug-in is used.");
			return false;
		}
		boolean succes = true;
		if(!kingdoms.load(getDataFolder().getAbsolutePath()))
			succes = false;
		if(!settings.load(getDataFolder().getAbsolutePath()))
			succes = false;
		try {
			FileInputStream input = new FileInputStream(getDataFolder().getAbsolutePath() + File.separator + "titles.data");
			while(input.available() > 0)
				setTitle(new ServerTitle(input));
			input.close();
		} catch(Exception ex){
			ex.printStackTrace();
			succes = false;
		}
		return succes;
	}
	
	public Kingdoms getKingdoms(){
		return kingdoms;
	}
	
	public Settings getSettings(){
		return settings;
	}
	
	public GriefLogger getGriefLogger(){
		return griefLogger;
	}
	
	public ItemLogger getItemLogger(){
		return itemLogger;
	}
	
	public boolean isStaff(CommandSender player){
		return player.isOp();
	}
	
	public boolean isSpawning(Player player){
		for(SpawningPlayer sp : spawningPlayers)
			if(sp.id.equals(player.getUniqueId()))
				return true;
		return false;
	}
	
	public void startSpawning(Player player){
		if(!isSpawning(player))
			spawningPlayers.add(new SpawningPlayer(player));
	}
	
	public void endSpawning(Player player){
		endSpawning(player.getUniqueId());
	}
	
	public void endSpawning(UUID id){
		for(int i = 0; i < spawningPlayers.size(); i++)
			if(spawningPlayers.get(i).id.equals(id)){
				spawningPlayers.remove(i);
				return;
			}
	}
	
	public void setTitle(Player player, String title, ChatColor color){
		setTitle(new ServerTitle(player.getUniqueId(), title, color));
	}
	
	public String getServerTitle(Player player){
		for(ServerTitle st : titles){
			if(st.id.equals(player.getUniqueId())){
				return "[" + st.getColor() + st.getTitle() + ChatColor.WHITE + "]";
			}
		}
		return "";
	}
	
	public void setTitle(ServerTitle title){
		for(int i = 0; i < titles.size(); i++){
			ServerTitle st = titles.get(i);
			if(st.getID().equals(title.getID())){
				titles.remove(i);
				i--;
			}
		}
		titles.add(title);
	}
	
	public static class ServerTitle {
		
		private UUID id;
		
		private String title;
		private ChatColor color;

		public ServerTitle(UUID id, String title, ChatColor color) {
			this.id = id;
			this.title = title;
			this.color = color;
		}
		
		public ServerTitle(InputStream input){
			try {
				load(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public UUID getID(){
			return id;
		}
		
		public String getTitle(){
			return title;
		}
		
		public ChatColor getColor(){
			return color;
		}
		
		public byte[] data(){
			char[] chars = title.toCharArray();
			ByteBuffer buffer = ByteBuffer.allocate(16 + 1 + chars.length * 2 + 2);
			buffer.put((byte) (chars.length - 128));
			buffer.putLong(id.getMostSignificantBits());
			buffer.putLong(id.getLeastSignificantBits());
			for(char c : chars){
				buffer.putChar(c);
			}
			buffer.putChar(color.getChar());
			return buffer.array();
		}
		
		public void load(InputStream input) throws Exception{
			int length = (byte) input.read() + 128;
			byte[] data = new byte[16 + length * 2 + 2];
			input.read(data);
			ByteBuffer buffer = ByteBuffer.wrap(data);
			id = new UUID(buffer.getLong(), buffer.getLong());
			title = "";
			for(int i = 0; i < length; i++)
				title += buffer.getChar();
			color = ChatColor.getByChar(buffer.getChar());
		}
	}
	
	private static class SpawningPlayer {
		
		private SpawningPlayer(Player player){
			secondsLeft = 5;
			id = player.getUniqueId();
		}
		
		private final UUID id;
		private int secondsLeft;
	}
}
