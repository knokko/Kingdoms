package nl.knokko.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import nl.knokko.kingdoms.Kingdom;
import nl.knokko.main.KingdomsPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import static java.util.Calendar.*;

public class ItemLogger implements Listener {
	
	private static final String FOLDER = "Item Logger";
	
	private static final byte ID_DROP = -128;
	private static final byte ID_FURNACE_EXTRACT = -127;
	private static final byte ID_PUT_MOVE = -126;
	private static final byte ID_TAKE_MOVE = -125;
	private static final byte ID_PUT = -124;
	private static final byte ID_TAKE = -123;
	private static final byte ID_COLLECT_OUT = -122;
	private static final byte ID_COLLECT_IN = -121;
	private static final byte ID_BREAK = -120;
	
	private final Map<UUID, PlayerLog> logs = new HashMap<UUID, PlayerLog>();
	private final KingdomsPlugin plugin;

	public ItemLogger(KingdomsPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void save(){
		new File(plugin.getDataFolder() + File.separator + FOLDER).mkdirs();
		Iterator<Entry<UUID,PlayerLog>> iterator = logs.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<UUID,PlayerLog> entry = iterator.next();
			entry.getValue().save(plugin, entry.getKey());
		}
	}
	
	public void logPlayer(UUID playerID){
		getLog(playerID).saveToTextFile(plugin, playerID);
	}
	
	public void logKingdom(Kingdom kd){
		if(kd.getCentre() == null || kd.getRadius() <= 0 || kd.getShape() == null)
			throw new IllegalArgumentException("Kingdom " + kd.getName() + " does not have a marked territory!");
		save();
		ArrayList<String> lines = new ArrayList<String>();
		File folder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER);
		folder.mkdir();
		File[] files = folder.listFiles();
		for(File file : files){
			if(file.getName().endsWith(".ilg")){
				String id = file.getName().substring(0, file.getName().length() - 4);
				String playerName = id;
				try {
					playerName = Bukkit.getOfflinePlayer(UUID.fromString(id)).getName();
					FileInputStream input;
					input = new FileInputStream(file);
					while(input.available() > 0){
						byte[] action = new byte[PlayerLog.SIZE];
						input.read(action);
						ByteBuffer buffer = ByteBuffer.wrap(action);
						int x = buffer.getInt(12);
						int y = buffer.get(16) + 128;
						int z = buffer.getInt(17);
						if(kd.isInside(new Kingdom.SafeLocation(kd.getCentre().getWorld(), x, y, z))){
							lines.add(PlayerLog.toTextLine(action, playerName));
						}
					}
					input.close();
				} catch (Exception ex) {
					Bukkit.getLogger().log(Level.WARNING, "Failed to read player data of " + playerName, ex);
				}
			}
		}
		try {
			PrintWriter writer = new PrintWriter(plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + kd.getName() + " item log.txt");
			for(String line : lines)
				writer.println(line);
			writer.close();
		} catch(Exception ex){
			Bukkit.getLogger().log(Level.WARNING, "Failed to save text item log for " + kd.getName(), ex);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event){
		if(plugin.getSettings().logChestActivity())
			getLog(event.getPlayer()).addAction(ID_DROP, event.getItemDrop().getItemStack(), event.getPlayer().getLocation());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFurnaceExtract(FurnaceExtractEvent event){
		if(plugin.getSettings().logChestActivity())
			getLog(event.getPlayer()).addAction(ID_FURNACE_EXTRACT, event.getItemType().ordinal(), event.getItemAmount(), event.getBlock().getLocation());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getState() instanceof InventoryHolder){
			InventoryHolder invHolder = (InventoryHolder) event.getBlock().getState();
			Inventory inv = invHolder.getInventory();
			ItemStack[] contents = inv.getContents();
			PlayerLog log = getLog(event.getPlayer());
			for(ItemStack stack : contents){
				if(stack != null)
					log.addAction(ID_BREAK, stack, event.getBlock().getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemMove(InventoryMoveItemEvent event){
		if(!plugin.getSettings().logChestActivity())
			return;
		if(event.getSource().getHolder() instanceof Player){
			Player putter = (Player) event.getSource().getHolder();
			Inventory des = event.getDestination();
			if(des.getLocation() != null)
				getLog(putter).addAction(ID_PUT_MOVE, event.getItem(), des.getLocation());
		}
		if(event.getDestination().getHolder() instanceof Player){
			Player taker = (Player) event.getDestination().getHolder();
			if(event.getSource().getLocation() != null)
				getLog(taker).addAction(ID_TAKE_MOVE, event.getItem(), event.getSource().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event){
		if(!plugin.getSettings().logChestActivity())
			return;
		if(!(event.getWhoClicked() instanceof Player) || event.getInventory() == null)
			return;
		boolean flag = event.getView().getTopInventory().getHolder() == event.getView().getBottomInventory().getHolder() ? true : event.getSlot() != event.getRawSlot();
		Player player = (Player) event.getWhoClicked();
		Location loc = event.getView().getTopInventory().getLocation();
		if(loc == null)
			return;
		InventoryAction a = event.getAction();
		if(!flag && a == InventoryAction.PLACE_ALL)
			getLog(player).addAction(ID_PUT, event.getCursor(), loc);
		if(!flag && a == InventoryAction.PLACE_SOME)
			getLog(player).addAction(ID_PUT, new ItemStack(event.getCursor().getType(), event.getCurrentItem().getMaxStackSize() - event.getCurrentItem().getAmount()), loc);
		if(!flag && a == InventoryAction.PLACE_ONE)
			getLog(player).addAction(ID_PUT, new ItemStack(event.getCursor().getType()), loc);
		if(!flag && a == InventoryAction.PICKUP_ALL)
			getLog(player).addAction(ID_TAKE, event.getCurrentItem(), loc);
		if(!flag && a == InventoryAction.PICKUP_HALF){
			int amount = event.getCurrentItem().getAmount() / 2;
			if(amount != event.getCurrentItem().getAmount() / 2d)
				++amount;
			getLog(player).addAction(ID_TAKE, new ItemStack(event.getCurrentItem().getType(), amount), loc);
		}
		if(!flag && a == InventoryAction.PICKUP_SOME)
			getLog(player).addAction(ID_TAKE, new ItemStack(event.getCurrentItem().getType(), event.getCursor().getMaxStackSize() - event.getCursor().getAmount()), loc);
		if(!flag && a == InventoryAction.PICKUP_ONE)
			getLog(player).addAction(ID_TAKE, new ItemStack(event.getCurrentItem().getType()), loc);
		if(a == InventoryAction.MOVE_TO_OTHER_INVENTORY){
			if(flag)
				getLog(player).addAction(ID_PUT, event.getCurrentItem(), loc);
			else
				getLog(player).addAction(ID_TAKE, event.getCurrentItem(), loc);
		}
		if(!flag && a == InventoryAction.SWAP_WITH_CURSOR){
			getLog(player).addAction(ID_PUT, event.getCursor(), loc);
			getLog(player).addAction(ID_TAKE, event.getCurrentItem(), loc);
		}
		if(a == InventoryAction.COLLECT_TO_CURSOR){
			if(flag)
				getLog(player).addAction(ID_COLLECT_IN, new ItemStack(event.getCurrentItem().getType(), event.getCurrentItem().getMaxStackSize() - event.getCurrentItem().getAmount()), loc);
			else
				getLog(player).addAction(ID_COLLECT_OUT, event.getCurrentItem(), loc);
		}
	}
	
	private PlayerLog getLog(Player player){
		return getLog(player.getUniqueId());
	}
	
	private PlayerLog getLog(UUID playerID){
		PlayerLog log = logs.get(playerID);
		if(log == null){
			log = new PlayerLog();
			logs.put(playerID, log);
		}
		return log;
	}
	
	private static class PlayerLog {
		
		private static final int SIZE = 1 + 2 + 1 + 8 + 4 + 1 + 4;
		
		private static String getPath(KingdomsPlugin plugin, UUID playerID){
			return plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + playerID.toString() + ".ilg";
		}
		
		private static String getTextPath(KingdomsPlugin plugin, UUID playerID){
			return plugin.getDataFolder().getAbsolutePath() + File.separator + FOLDER + File.separator + Bukkit.getOfflinePlayer(playerID).getName() + " item log.txt";
		}
		
		private static byte[] toBytes(byte type, short materialID, byte amount, long time, int x, byte y, int z){
			ByteBuffer buffer = ByteBuffer.allocate(SIZE);
			buffer.put(type);//0
			buffer.putShort(materialID);//1
			buffer.put(amount);//3
			buffer.putLong(time);//4
			buffer.putInt(x);//12
			buffer.put(y);//16
			buffer.putInt(z);//17
			return buffer.array();
		}
		
		private static byte[] loadBytes(KingdomsPlugin plug, UUID playerID){
			try {
				File file = new File(getPath(plug, playerID));
				byte[] data = new byte[(int) file.length()];//let's hope there won't be more than 2.1 GB data xD
				FileInputStream input = new FileInputStream(file);
				input.read(data);
				input.close();
				return data;
			} catch(Exception ex){
				Bukkit.getLogger().warning("No previous block entity could be found for " + playerID + ": " + ex.getMessage());
				return null;
			}
		}
		
		private static byte[][] loadByteList(KingdomsPlugin plug, UUID playerID){
			try {
				File file = new File(getPath(plug, playerID));
				int size = (int) (file.length() / SIZE);
				byte[][] data = new byte[size][];
				FileInputStream input = new FileInputStream(file);
				for(int i = 0; i < data.length; i++){
					byte[] current = new byte[SIZE];
					input.read(current);
					data[i] = current;
				}
				input.close();
				return data;
			} catch(Exception ex){
				ex.printStackTrace();
				return null;
			}
		}
		
		private static String toTextLine(byte[] action, UUID playerID){
			return toTextLine(action, Bukkit.getOfflinePlayer(playerID).getName());
		}
		
		private static String toTextLine(byte[] action, String playerName){
			ByteBuffer buffer = ByteBuffer.wrap(action);
			byte type = buffer.get();
			Material material = Material.values()[buffer.getShort()];
			byte amount = buffer.get();
			Calendar cal = new Calendar.Builder().setInstant(buffer.getLong()).build();
			int x = buffer.getInt();
			int y = buffer.get() + 128;
			int z = buffer.getInt();
			String time = "[" + cal.get(DAY_OF_MONTH) + "/" + (cal.get(MONTH) + 1) + "/" + cal.get(YEAR) + " " + cal.get(HOUR_OF_DAY) + ":" + cal.get(MINUTE) + ":" + cal.get(SECOND) + "] ";
			String itemName = amount + " " + material.name().toLowerCase();
			String location = " (" + x + "," + y + "," + z + ")";
			if(type == ID_DROP)
				return time + playerName + " dropped " + itemName + " at" + location;
			if(type == ID_FURNACE_EXTRACT)
				return time + playerName + " extracted " + itemName + " from a furnace at" + location;
			if(type == ID_TAKE_MOVE)
				return time + playerName + " drained " + itemName + " from" + location;
			if(type == ID_PUT_MOVE)
				return time + playerName + " thrashed " + itemName + " in" + location;
			if(type == ID_PUT)
				return time + playerName + " put " + itemName + " in" + location;
			if(type == ID_TAKE)
				return time + playerName + " took " + itemName + " from" + location;
			if(type == ID_COLLECT_OUT)
				return time + playerName + " took at least " + itemName + " from" + location;
			if(type == ID_COLLECT_IN)
				return time + playerName + " took at most " + itemName + " from" + location;
			if(type == ID_BREAK)
				return time + playerName + " dropped " + itemName + " by breaking the chest at" + location;
			throw new IllegalArgumentException("Unknown type ID: " + type);
		}
		
		private final List<byte[]> currentData = new ArrayList<byte[]>();
		
		private void addAction(byte type, ItemStack stack, Location location){
			addAction(type, (short) stack.getType().ordinal(), (byte) stack.getAmount(), System.currentTimeMillis(), location.getBlockX(), (byte) (location.getBlockY() - 128), location.getBlockZ());
		}
		
		private void addAction(byte type, int materialID, int amount, Location location){
			addAction(type, (short) materialID, (byte) amount, System.currentTimeMillis(), location.getBlockX(), (byte)(location.getBlockY() - 128), location.getBlockZ());
		}
		
		private void addAction(byte type, short id, byte amount, long time, int x, byte y, int z){
			if(type != 0)//air
				currentData.add(toBytes(type, id, amount, time, x, y, z));
		}
		
		private void save(KingdomsPlugin plugin, UUID playerID){
			try {
				byte[] previousData = loadBytes(plugin, playerID);
				FileOutputStream output = new FileOutputStream(getPath(plugin, playerID));
				if(previousData != null)
					output.write(previousData);
				for(byte[] action : currentData)
					output.write(action);
				output.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		private void saveToTextFile(KingdomsPlugin plugin, UUID playerID){
			byte[][] previous = loadByteList(plugin, playerID);
			try {
				PrintWriter writer = new PrintWriter(getTextPath(plugin, playerID));
				if(previous != null){
					for(byte[] data : previous)
						writer.println(toTextLine(data, playerID));
				}
				for(byte[] data : currentData)
					writer.println(toTextLine(data, playerID));
				writer.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}
