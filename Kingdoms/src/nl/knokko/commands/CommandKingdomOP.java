package nl.knokko.commands;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.data.Settings;
import nl.knokko.kingdoms.Kingdom;
import nl.knokko.kingdoms.Kingdoms;
import nl.knokko.kingdoms.MemberData;
import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Translator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandKingdomOP implements CommandExecutor {
	
	public static final String NAME_GUI = "Kingdom OP";
	public static final String NAME_SETTINGS_GUI = "Kingdom Settings";
	public static final String NAME_CREATE_KD_GUI = "Select the king";
	public static final String NAME_KINGDOMS_GUI = " Kingdoms";
	
	public static final String PRE_NAME_KINGDOM_GUI = " Kingdom ";
	public static final String PRE_NAME_CHANGE_KING = "Select the new king for ";
	public static final String PRE_NAME_KICK_PLAYER = "Kick a player from ";
	public static final String PRE_NAME_PROMOTE_PLAYER = "Promote a player in ";
	public static final String PRE_NAME_PERMISSIONS_MEMBER = "Change the permissions in ";
	public static final String PRE_NAME_TERRITORY_SHAPE = "Change the territory shape of ";
	public static final String PRE_NAME_TERRITORY_RADIUS = "Change the territory radius of ";
	public static final String PRE_NAME_COLORS = "Change the colors of ";
	public static final String PRE_NAME_INVITES = "The invites of ";
	public static final String PRE_NAME_INVITE = "Invite a player for ";
	
	public static final Inventory GUI = Bukkit.createInventory(null, 9, NAME_GUI);
	
	static {
		GUI.setItem(0, getNamedStack(Material.BARRIER, "Close"));
		GUI.setItem(1, getNamedStack(Material.BOOK_AND_QUILL, "Save Kingdom Data"));
		GUI.setItem(2, getNamedStack(Material.WRITTEN_BOOK, "Load All Kingdom Data"));
		GUI.setItem(3, getNamedStack(Material.BANNER, "Kingdoms"));
		GUI.setItem(4, getNamedStack(Material.REDSTONE_COMPARATOR, "Settings"));
		GUI.setItem(5, getNamedStack(Material.SAPLING, "Create Kingdom"));
	}
	
	public static Inventory createSettingsGUI(Settings settings){
		Inventory set = Bukkit.createInventory(null, 9, NAME_SETTINGS_GUI);
		set.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		set.setItem(1, getNamedStack(Material.GOLD_SWORD, "Attack allies", "Allow players to attack members", "of their own kingdom.", getBoolString(settings.canAttackAllies()), getChangeString(settings.canAttackAllies())));
		set.setItem(2, getNamedStack(Material.DIAMOND_PICKAXE, "Grief other kingdoms", "Allow players to grief", "other kingdoms without war.", getBoolString(settings.canGriefOtherKingdoms()), getChangeString(settings.canGriefOtherKingdoms())));
		set.setItem(3, getNamedStack(Material.WOOD_DOOR, "Can leave kingdom", "Allow players to leave", "their kingdom without", "their king or staff.", getBoolString(settings.isFreeToLeave()), getChangeString(settings.isFreeToLeave())));
		set.setItem(4, getNamedStack(Material.BOOK_AND_QUILL, "Log kingdom edit", "Whenever a player edits", "something in a kingdom,", "it will be saved in a file.", getBoolString(settings.logGriefing()), getChangeString(settings.logGriefing())));
		set.setItem(5, getNamedStack(Material.BOOKSHELF, "Log own kingdom edit", "Log the actions", "of members in their own", "kingdom as well.", getBoolString(settings.logOwnKingdomEdit()), getChangeString(settings.logOwnKingdomEdit())));
		set.setItem(6, getNamedStack(Material.CHEST, "Log chests", "Whenever a player opens a chest,", "closes a chest, takes an item", "or puts an item, it will be saved in a file.", getBoolString(settings.logChestActivity()), getChangeString(settings.logChestActivity())));
		return set;
	}
	
	public static Inventory createKingdomCreateGUI(Kingdoms kds){
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		ArrayList<Player> freePlayers = new ArrayList<Player>();
		for(Player player : players){
			if(kds.getPlayerKingdom(player) == null)
				freePlayers.add(player);
		}
		Inventory gui = Bukkit.createInventory(null, multipleOf9(freePlayers.size() + 1), NAME_CREATE_KD_GUI);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Cancel"));
		for(int i = 0; i < freePlayers.size(); i++){
			String name = freePlayers.get(i).getName();
			gui.setItem(i + 1, getNamedStack(Material.GOLD_BLOCK, name, "Choose " + name + " as the", "king of the new kingdom."));
		}
		return gui;
	}
	
	public static Inventory createKingdomsGUI(Kingdoms kds){
		ArrayList<Kingdom> kingdoms = kds.getKingdoms();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + kingdoms.size()), NAME_KINGDOMS_GUI);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < kingdoms.size(); i++){
			Kingdom kd = kingdoms.get(i);
			gui.setItem(i + 1, getNamedStack(Material.BANNER, kd.getName(), "Open the menu for kingdom " + kd.getColoredName()));
		}
		return gui;
	}
	
	public static Inventory createKingdomGUI(Kingdom kd){
		Inventory gui = Bukkit.createInventory(null, 18, PRE_NAME_KINGDOM_GUI + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(2, getNamedStack(Material.LAVA_BUCKET, "Delete", ChatColor.RED + "Delete this kingdom!"));
		gui.setItem(4, getNamedStack(Material.GOLD_BLOCK, "Change King", "Select a new king for this kingdom.", "The old king will become a member."));
		gui.setItem(5, getNamedStack(Material.IRON_BOOTS, "Kick", "Remove a member from this kingdom."));
		gui.setItem(6, getNamedStack(Material.CLAY_BRICK, "Invite", "Invite a player", "for this kingdom."));
		gui.setItem(7, getNamedStack(Material.BRICK, "Invites", "View all invites", "that are sent by", "this kingdom."));
		gui.setItem(8, getNamedStack(Material.DIAMOND, "Promote", "Promote a member of this kingdom.", "This member will get a rank."));
		gui.setItem(9, getNamedStack(Material.PAPER, "Permissions", "Grant a member of this kingdom extra permissions."));
		gui.setItem(10, getNamedStack(Material.BANNER, "Territory Centre", "Set the centre of the territory", "of this kingdom at your current location.", "The effect of the territory depends", "on the settings of the server."));
		gui.setItem(11, getNamedStack(Material.BED, "Set Spawn", "Set the kingdom spawn at ", "your current location."));
		gui.setItem(12, getNamedStack(Material.SLIME_BALL, "Territory Shape", "Change the shape of", "the territory of", "this kingdom."));
		gui.setItem(13, getNamedStack(Material.BEACON, "Territory radius", "Set the radius of", "the territory of", "this kingdom."));
		gui.setItem(14, getNamedStack(Material.ENDER_PEARL, "Spawn", "Teleport to the", "kingdom spawn."));
		gui.setItem(15, getNamedStack(Material.WOOL, "Colors", "Change the colors of", "the name of this kingdom."));
		gui.setItem(16, getNamedStack(Material.WRITTEN_BOOK, "Info", "Send the book with", "info about this kingdom", "to your inventory."));
		gui.setItem(17, getNamedStack(Material.WATER_BUCKET, "Announcements", "View the secret", "internal announcements", "of this kingdom."));
		return gui;
	}
	
	public static Inventory createChangeKingGUI(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + members.size()), PRE_NAME_CHANGE_KING + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 1, getNamedStack(Material.GOLD_BLOCK, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createKickPlayerGUI(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + members.size()), PRE_NAME_KICK_PLAYER + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 1, getNamedStack(Material.IRON_BOOTS, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createPromotePlayerGui(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(2 + members.size()), PRE_NAME_PROMOTE_PLAYER + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(1, getNamedStack(Material.GOLD_BLOCK, kd.getKing().getName(), "Change the title", "of the king."));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 2, getNamedStack(Material.DIAMOND, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createMemberPermissionsGui(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + members.size()), PRE_NAME_PERMISSIONS_MEMBER + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 1, getNamedStack(Material.PAPER, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createTerritoryShapeGui(Kingdom kd){
		Inventory gui = Bukkit.createInventory(null, 9, PRE_NAME_TERRITORY_SHAPE + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(1, getNamedStack(Material.SLIME_BLOCK, "Square", "Set the shape of", "the territory of", "this kingdom to", "a square."));
		gui.setItem(2, getNamedStack(Material.SLIME_BALL, "Circle", "Set the shape of", "the territory of", "this kingdom to", "a circle."));
		return gui;
	}
	
	public static Inventory createInviteGUI(Kingdom kd, Kingdoms kds){
		ArrayList<Player> players = kd.getPlayersToInvite(kds);
		Inventory gui = Bukkit.createInventory(null, multipleOf9(players.size() + 1), PRE_NAME_INVITE + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < players.size(); i++){
			String name = players.get(i).getName();
			gui.setItem(i + 1, getNamedStack(Material.PAPER, name, "Invite " + name, " to this kingdom."));
		}
		return gui;
	}
	
	public static Inventory createInvitesGUI(Kingdom kd){
		ArrayList<OfflinePlayer> invites = kd.getInvitedPlayers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(invites.size() + 1), PRE_NAME_INVITES + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < invites.size(); i++){
			OfflinePlayer player = invites.get(i);
			gui.setItem(i + 1, getNamedStack(Material.PAPER, player.getName(), player.getName() + " has", "been invited to", "join this kingdom.", "Click to cancel", "his invite."));
		}
		return gui;
	}
	
	public static final String COLOR_PRE_TEXT = "Add the color ";
	
	public static Inventory createColorsGUI(Kingdom kd){
		Inventory gui = Bukkit.createInventory(null, 18, PRE_NAME_COLORS + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(1, getNamedStack(Material.LAVA_BUCKET, "Reset", "Reset the colors of", "this kingdom."));
		gui.setItem(2, getWoolStack("Black", DyeColor.BLACK, COLOR_PRE_TEXT + "black"));
		gui.setItem(3, getWoolStack("Dark Blue", DyeColor.BLUE, COLOR_PRE_TEXT + "dark_blue"));
		gui.setItem(4, getWoolStack("Dark Green", DyeColor.GREEN, COLOR_PRE_TEXT + "dark_green"));
		gui.setItem(5, getWoolStack("Dark Red", DyeColor.RED, COLOR_PRE_TEXT + "dark_red"));
		gui.setItem(6, getNamedStack(Material.WATER_BUCKET, "Dark Aqua", COLOR_PRE_TEXT + "dark_aqua"));
		gui.setItem(7, getWoolStack("Dark Purple", DyeColor.PURPLE, COLOR_PRE_TEXT + "dark_purple"));
		gui.setItem(8, getNamedStack(Material.GOLD_BLOCK, "Gold", COLOR_PRE_TEXT + "gold"));
		gui.setItem(9, getWoolStack("Gray", DyeColor.SILVER, COLOR_PRE_TEXT + "gray"));
		gui.setItem(10, getWoolStack("Dark Gray", DyeColor.GRAY, COLOR_PRE_TEXT + "dark_gray"));
		gui.setItem(11, getWoolStack("Blue", DyeColor.LIGHT_BLUE, COLOR_PRE_TEXT + "blue"));
		gui.setItem(12, getWoolStack("Green", DyeColor.LIME, COLOR_PRE_TEXT + "green"));
		gui.setItem(13, getWoolStack("Red", DyeColor.ORANGE, COLOR_PRE_TEXT + "red"));
		gui.setItem(14, getNamedStack(Material.PRISMARINE, "Aqua", COLOR_PRE_TEXT + "aqua"));
		gui.setItem(15, getWoolStack("Light Purple", DyeColor.MAGENTA, COLOR_PRE_TEXT + "light_purple"));
		gui.setItem(16, getWoolStack("Yellow", DyeColor.YELLOW, COLOR_PRE_TEXT + "yellow"));
		return gui;
	}
	
	private static String[] radiusText(float radius){
		return new String[]{"Set the radius of", "the territory of", "this kingdom to", radius + " blocks."};
	}
	
	public static Inventory createTerritoryRadiusGUI(Kingdom kd){
		Inventory gui = Bukkit.createInventory(null, 27, PRE_NAME_TERRITORY_RADIUS + kd.getName());
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(1, getNamedStack(Material.STICK, "50", radiusText(50)));
		gui.setItem(2, getNamedStack(Material.WOOD, "100", radiusText(100)));
		gui.setItem(3, getNamedStack(Material.LOG, "150", radiusText(150)));
		gui.setItem(4, getNamedStack(Material.COBBLESTONE, "200", radiusText(200)));
		gui.setItem(5, getNamedStack(Material.STONE, "250", radiusText(250)));
		gui.setItem(6, getNamedStack(Material.SMOOTH_BRICK, "300", radiusText(300)));
		gui.setItem(7, getNamedStack(Material.COAL_ORE, "400", radiusText(400)));
		gui.setItem(8, getNamedStack(Material.COAL, "500", radiusText(500)));
		gui.setItem(9, getNamedStack(Material.COAL_BLOCK, "600", radiusText(600)));
		gui.setItem(10, getNamedStack(Material.IRON_ORE, "700", radiusText(700)));
		gui.setItem(11, getNamedStack(Material.IRON_INGOT, "800", radiusText(800)));
		gui.setItem(12, getNamedStack(Material.IRON_BLOCK, "900", radiusText(900)));
		gui.setItem(13, getNamedStack(Material.LAPIS_ORE, "1000", radiusText(1000)));
		gui.setItem(14, getNamedStack(Material.LAPIS_BLOCK, "1250", radiusText(1250)));
		gui.setItem(15, getNamedStack(Material.REDSTONE_ORE, "1500", radiusText(1500)));
		gui.setItem(16, getNamedStack(Material.REDSTONE, "1750", radiusText(1750)));
		gui.setItem(17, getNamedStack(Material.REDSTONE_BLOCK, "2000", radiusText(2000)));
		gui.setItem(18, getNamedStack(Material.GOLD_ORE, "2500", radiusText(2500)));
		gui.setItem(19, getNamedStack(Material.GOLD_INGOT, "3000", radiusText(3000)));
		gui.setItem(20, getNamedStack(Material.GOLD_BLOCK, "3500", radiusText(3500)));
		gui.setItem(21, getNamedStack(Material.DIAMOND_ORE, "4000", radiusText(4000)));
		gui.setItem(22, getNamedStack(Material.DIAMOND, "4500", radiusText(4500)));
		gui.setItem(23, getNamedStack(Material.DIAMOND_BLOCK, "5000", radiusText(5000)));
		gui.setItem(24, getNamedStack(Material.EMERALD_ORE, "6000", radiusText(6000)));
		gui.setItem(25, getNamedStack(Material.EMERALD, "7500", radiusText(7500)));
		gui.setItem(26, getNamedStack(Material.EMERALD_BLOCK, "10000", radiusText(10000)));
		return gui;
	}
	
	static int multipleOf9(int number){
		int num = number / 9;
		if(num != number / 9d)
			return num * 9 + 9;
		return number;
	}
	
	static ItemStack getNamedStack(Material material, String name){
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
		meta.setDisplayName(name);
		ItemStack stack = new ItemStack(material);
		stack.setItemMeta(meta);
		return stack;
	}
	
	static ItemStack getNamedStack(Material material, String name, String... lore){
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
		meta.setDisplayName(name);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String line : lore)
			loreList.add(line);
		meta.setLore(loreList);
		ItemStack stack = new ItemStack(material);
		stack.setItemMeta(meta);
		return stack;
	}
	
	static ItemStack getWoolStack(String name, DyeColor color, String... lore){
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.WOOL);
		meta.setDisplayName(name);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String line : lore)
			loreList.add(line);
		meta.setLore(loreList);
		@SuppressWarnings("deprecation")
		ItemStack stack = new ItemStack(Material.WOOL, 1, color.getWoolData());
		stack.setItemMeta(meta);
		return stack;
	}
	
	private static String getBoolString(boolean value){
		return "Currently: " + (value ? (ChatColor.GREEN + "ON") : (ChatColor.RED + "OF"));
	}
	
	private static String getChangeString(boolean currentValue){
		return "Click to " + (currentValue ? (ChatColor.RED + "disable") : (ChatColor.GREEN + "enable"));
	}
	
	private final KingdomsPlugin plug;

	public CommandKingdomOP(KingdomsPlugin plugin) {
		plug = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!plug.isStaff(sender)){
			Translator.needOPForKDOP(sender);
			return true;
		}
		if(args.length == 0){
			if(sender instanceof Player)
				((Player) sender).openInventory(GUI);
			else
				Translator.openGuiNotPlayer(sender);
			return true;
		}
		String a1 = args[0];
		if(args.length == 1){
			if(a1.equalsIgnoreCase("save"))
				return save(plug, sender);
			if(a1.equalsIgnoreCase("load"))
				return load(plug, sender);
		}
		if(args.length == 2){
			if(a1.equals("info")){
				Kingdom kd = plug.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					if(sender instanceof Player)
						kd.getKingdomInfo().give((Player) sender);
					else {
						String[] info = kd.getKingdomInfo().getPages();
						for(String page : info)
							sender.sendMessage(page);
					}
				}
				else
					Translator.cantFindKingdom(sender, args[1]);
				return true;
			}
			if(a1.equals("announcements")){
				Kingdom kd = plug.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					if(sender instanceof Player)
						kd.getInternalAnnouncements().give((Player) sender);
					else {
						String[] info = kd.getInternalAnnouncements().getPages();
						for(String page : info)
							sender.sendMessage(page);
					}
				}
				else
					Translator.cantFindKingdom(sender, args[1]);
				return true;
			}
		}
		if(args.length == 3){
			if(a1.equals("create")){
				String kdName = args[1];
				String kingName = args[2];
				if(plug.getKingdoms().hasKingdom(kdName)){
					Translator.kingdomAlreadyExists(sender, kdName);
					return true;
				}
				@SuppressWarnings("deprecation")
				Player king = Bukkit.getPlayer(kingName);
				if(king == null){
					Translator.playerNotOnline(sender, kingName);
					return true;
				}
				Kingdom oldKD = plug.getKingdoms().getPlayerKingdom(king);
				if(oldKD != null){
					Translator.playerAlreadyInKingdom(sender, kingName, oldKD);
					return true;
				}
				plug.getKingdoms().addKingdom(new Kingdom(kdName, king));
				Translator.createdKingdom(sender, kdName);
				return true;
			}
			if(a1.equals("kingtitle")){
				Kingdom kd = plug.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					kd.setKingTitle(args[2]);
					Translator.setKingTitleOP(sender, args[2], args[1]);
				}
				else
					Translator.cantFindKingdom(sender, args[1]);
				return true;
			}
		}
		if(args.length == 5){
			if(a1.equals("permissions")){
				Kingdom kd = plug.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					OfflinePlayer target = kd.getPlayer(args[2]);
					if(target != null){
						String perm = args[3];
						String value = args[4];
						boolean bool;
						if(value.equals("yes"))
							bool = true;
						else if(value.equals("no"))
							bool = false;
						else {
							Translator.notValidBooleanValue(sender, value);
							return true;
						}
						if(perm.equals("canInvite")){
							if(kd.setInvitePermission(target, bool))
								Translator.setInvitePermission(sender, target.getName(), kd.getName());
							else
								Translator.playerNotInKingdom(sender, args[2], kd.getName());
							return true;
						}
						else if(perm.equals("isDiplomatic")){
							if(kd.setDiplomaticPermission(target, bool))
								Translator.setDiplomaticPermission(sender, target.getName(), kd.getName());
							else
								Translator.playerNotInKingdom(sender, args[2], kd.getName());
							return true;
						}
						else {
							Translator.noSuchPermission(sender, perm);
							return true;
						}
					}
					else
						Translator.playerNotInKingdom(sender, args[2], kd.getName());
				}
				else {
					Translator.cantFindKingdom(sender, args[1]);
					return true;
				}
				return true;
			}
			if(a1.equals("promote")){
				Kingdom kd = plug.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					OfflinePlayer target = kd.getPlayer(args[2]);
					if(target != null){
						String titleName = args[3];
						ChatColor color = ChatColor.valueOf(args[4].toUpperCase());
						if(color != null){
							if(kd.setPlayerTitle(target, titleName, color))
								Translator.setMemberTitle(sender, target.getName(), titleName, color, kd.getName());
							else
								Translator.playerNotInKingdom(sender, target.getName(), kd.getName());
							return true;
						}
						else {
							Translator.noSuchColor(sender, args[4]);
							return true;
						}
					}
					else {
						Translator.playerNotInKingdom(sender, args[2], kd.getName());
						return true;
					}
				}
				else {
					Translator.cantFindKingdom(sender, args[1]);
					return true;
				}
			}
		}
		Translator.howToUseKingdomOP(sender);
		return false;
	}
	
	public static boolean save(KingdomsPlugin plug, CommandSender sender){
		if(plug.save())
			Translator.save(sender);
		else
			Translator.failSave(sender);
		return true;
	}
	
	public static boolean load(KingdomsPlugin plug, CommandSender sender){
		if(plug.load())
			Translator.load(sender);
		else
			Translator.failLoad(sender);
		return true;
	}
}
