package nl.knokko.commands;

import java.util.ArrayList;
import java.util.List;

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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;

import static nl.knokko.commands.CommandKingdomOP.getNamedStack;
import static nl.knokko.commands.CommandKingdomOP.multipleOf9;
import static nl.knokko.commands.CommandKingdomOP.COLOR_PRE_TEXT;
import static nl.knokko.commands.CommandKingdomOP.getWoolStack;

public class CommandKingdom implements CommandExecutor {
	
	public static final String NAME_KD_GUI = "Your Kingdom";
	public static final String NAME_NO_KD_GUI = "You are not in a kingdom.";
	
	public static final String NAME_INVITE = "Invite a player...";
	public static final String NAME_INVITES = "Current Invites";
	public static final String NAME_ALLIES = "Current Allies";
	public static final String NAME_ALLY_INVITE = "Add or remove ally request";
	public static final String NAME_OWN_INVITES = "Your Invites";
	public static final String NAME_DECLINE_INVITES = "Decline your invites";
	public static final String NAME_KINGDOMS = "Kingdoms";
	public static final String NAME_CHANGE_KING = "Choose the new king";
	public static final String NAME_KICK_MEMBER = "Choose someone to kick";
	public static final String NAME_GRANT_RANK = "Grant ... a rank";
	public static final String NAME_PERMISSIONS = "Change the permissions of ...";
	public static final String NAME_CHANGE_COLORS = "Change your colors...";
	
	public static Inventory createKingdomGUI(Player player, Kingdoms kds){
		Kingdom kd = kds.getPlayerKingdom(player);
		if(kd != null){
			if(kd.isKing(player)){
				Inventory gui = Bukkit.createInventory(null, 27, NAME_KD_GUI);
				gui.setItem(0, getNamedStack(Material.BARRIER, "Close"));
				gui.setItem(2, getNamedStack(Material.LAVA_BUCKET, "Delete", ChatColor.RED + "Delete your kingdom."));
				gui.setItem(4, getNamedStack(Material.GOLD_BLOCK, "Crown", "Promote another member", "to the king, you", "will become a", "normal member."));
				gui.setItem(5, getNamedStack(Material.IRON_BOOTS, "Kick", "Kick a player from", "your kingdom."));
				gui.setItem(6, getNamedStack(Material.CLAY_BRICK, "Invite", "Invite a player", "to your kingdom."));
				gui.setItem(7, getNamedStack(Material.BRICK, "Invites", "View all invites", "that are sent", "by your kingdom."));
				gui.setItem(8, getNamedStack(Material.DIAMOND, "Rank", "Grant a member of", "this kingdom a rank."));
				gui.setItem(9, getNamedStack(Material.PAPER, "Permissions", "Grant a member of", "this kingdom extra", "permissions."));
				gui.setItem(10, getNamedStack(Material.BED, "Set Spawn", "Set the spawn of", "your kingdom at your", "current location"));
				gui.setItem(11, getNamedStack(Material.ENDER_PEARL, "To Spawn", "Teleport to the", "spawn of your kingdom."));
				gui.setItem(12, getNamedStack(Material.WOOL, "Colors", "Change the colors", "of your kingdom."));
				gui.setItem(13, getNamedStack(Material.BANNER, "Kingdoms", "View all kingdoms."));
				gui.setItem(14, getNamedStack(Material.COMPASS, "Territory", "See in who's", "territory you", "currently are."));
				gui.setItem(15, getNamedStack(Material.WRITTEN_BOOK, "Info", "Add the book", "with kingdom info", "to your inventory."));
				gui.setItem(16, getNamedStack(Material.BOOK_AND_QUILL, "Set info", "Set the kingdom info", "to the written", "book you are holding."));
				gui.setItem(18, getNamedStack(Material.SHIELD, "Allies", "View and remove", "your allies here."));
				gui.setItem(19, getNamedStack(Material.IRON_CHESTPLATE, "Add ally", "Accept ally", "invitations here,", "request an ally or", "remove your invites."));
				gui.setItem(24, getNamedStack(Material.WATER_BUCKET, "Announcements", "Add the book with", "the internal announcements", "of your kingdom", "to your inventory."));
				gui.setItem(25, getNamedStack(Material.BUCKET, "Set Announcements", "Set the book with", "the internal announcements", "of your kingdom to", "the written book", "in your main hand."));
				return gui;
			}
			Inventory gui = Bukkit.createInventory(null, 18, NAME_KD_GUI);
			gui.setItem(0, getNamedStack(Material.BARRIER, "Close"));
			if(KingdomsPlugin.getInstance().getSettings().isFreeToLeave())
				gui.setItem(2, getNamedStack(Material.WOOD_DOOR, "Leave kingdom", "Leave your kingdom."));
			gui.setItem(4, getNamedStack(Material.ENDER_PEARL, "To Spawn", "Teleport to the", "spawn of your kingdom."));
			gui.setItem(5, getNamedStack(Material.WATER_BUCKET, "Announcements", "Add the book with", "the internal announcements", "of your kingdom", "to your inventory."));
			if(kd.canInvite(player.getUniqueId()))
				gui.setItem(6, getNamedStack(Material.CLAY_BRICK, "Invite", "Invite a player", "to your kingdom."));
			gui.setItem(7, getNamedStack(Material.WRITTEN_BOOK, "Info", "Add the book", "with kingdom info", "to your inventory."));
			gui.setItem(8, getNamedStack(Material.IRON_INGOT, "Own invites", "You can view, accept", "and deny invites", "that were sent", "to you."));
			gui.setItem(9, getNamedStack(Material.COMPASS, "Territory", "See in who's", "territory you", "are currently."));
			if(kd.isDiplomatic(player.getUniqueId())){
				gui.setItem(10, getNamedStack(Material.SHIELD, "Allies", "View and remove", "your allies here."));
				gui.setItem(11, getNamedStack(Material.IRON_CHESTPLATE, "Add ally", "Accept ally", "invitations here."));
				gui.setItem(12, getNamedStack(Material.GOLD_CHESTPLATE, "Request ally", "Invite another", "kingdom to", "become allies."));
			}
			else {
				gui.setItem(10, getNamedStack(Material.SHIELD, "Allies", "View your allies."));
			}
			gui.setItem(13, getNamedStack(Material.BANNER, "Kingdoms", "View all kingdoms."));
			return gui;
		}
		Inventory gui = Bukkit.createInventory(null, 9, NAME_NO_KD_GUI);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Close"));
		gui.setItem(4, getNamedStack(Material.IRON_INGOT, "Invites", "You can view, accept", "and deny invites", "that were sent", "to you."));
		gui.setItem(7, getNamedStack(Material.COMPASS, "Territory", "See in who's", "territory you", "are currently."));
		gui.setItem(8, getNamedStack(Material.BANNER, "Kingdoms", "View all kingdoms."));
		return gui;
	}
	
	public static Inventory createKingdomsGUI(Kingdoms kds){
		ArrayList<Kingdom> list = kds.getKingdoms();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(list.size() + 1), NAME_KINGDOMS);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < list.size(); i++){
			Kingdom kd = list.get(i);
			gui.setItem(i + 1, getNamedStack(Material.BANNER, kd.getColoredName(), "King is " + kd.getKing().getName(), kd.getMembers().size() + 1 + " members", "Click for more info!"));
		}
		return gui;
	}
	
	public static Inventory createInviteGUI(Kingdom kd, Kingdoms kds){
		ArrayList<Player> players = kd.getPlayersToInvite(kds);
		Inventory gui = Bukkit.createInventory(null, multipleOf9(players.size() + 1), NAME_INVITE);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < players.size(); i++){
			String name = players.get(i).getName();
			gui.setItem(i + 1, getNamedStack(Material.PAPER, name, "Invite " + name, " to your kingdom."));
		}
		return gui;
	}
	
	public static Inventory createInvitesGUI(Kingdom kd){
		ArrayList<OfflinePlayer> invites = kd.getInvitedPlayers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(invites.size() + 1), NAME_INVITES);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < invites.size(); i++){
			OfflinePlayer player = invites.get(i);
			gui.setItem(i + 1, getNamedStack(Material.PAPER, player.getName(), player.getName() + " has", "been invited to", "join this kingdom.", "Click to cancel", "this invite."));
		}
		return gui;
	}
	
	public static Inventory createAlliesGUI(Kingdom kd, Player player){
		List<Kingdom> allies = kd.getAllies(KingdomsPlugin.getInstance().getKingdoms());
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + allies.size()), NAME_ALLIES);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		if(KingdomsPlugin.getInstance().isStaff(player)){
			for(int i = 0; i < allies.size(); i++)
				gui.setItem(i + 1, getNamedStack(Material.EMERALD_BLOCK, allies.get(i).getName(), "This kingdom is your ally.", ChatColor.RED + "Click to remove this ally!"));
		}
		else {
			for(int i = 0; i < allies.size(); i++)
				gui.setItem(i + 1, getNamedStack(Material.EMERALD_BLOCK, allies.get(i).getName(), "This kingdom is your ally."));
		}
		return gui;
	}
	
	public static Inventory createAllyInvitesGUI(Kingdom kd, Player player){
		ArrayList<Kingdom> kds = KingdomsPlugin.getInstance().getKingdoms().getKingdoms();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(kds.size()), NAME_ALLY_INVITE);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		int i = 0;
		for(Kingdom k : kds){
			if(k != kd){
				if(kd.isInvited(k))
					gui.setItem(i + 1, getNamedStack(Material.GOLD_CHESTPLATE, k.getName(), "Your kingdom has invited", "this kingdom to become an ally.", ChatColor.RED + "Click to cancel the invite."));
				else
					if(!k.isInvited(kd))
						gui.setItem(i + 1, getNamedStack(Material.IRON_CHESTPLATE, kd.getName(), "You have not invited", "this kingdom to", "become your ally.", "Click to invite."));
					else
						gui.setItem(i + 1, getNamedStack(Material.DIAMOND_CHESTPLATE, kd.getName(), "This kingdom has", "invited your kingdom", "to become their ally.", ChatColor.GREEN + "Click to accept."));
				i++;
			}
		}
		return gui;
	}
	
	public static Inventory createChangeKingGUI(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + members.size()), NAME_CHANGE_KING);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 1, getNamedStack(Material.GOLD_BLOCK, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createKickGUI(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + members.size()), NAME_KICK_MEMBER);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 1, getNamedStack(Material.IRON_BOOTS, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createRankGUI(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(2 + members.size()), NAME_GRANT_RANK);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(1, getNamedStack(Material.GOLD_BLOCK, kd.getKing().getName(), "Change the title", "of the king."));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 2, getNamedStack(Material.DIAMOND, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createPermissionsGUI(Kingdom kd){
		ArrayList<MemberData> members = kd.getMembers();
		Inventory gui = Bukkit.createInventory(null, multipleOf9(1 + members.size()), NAME_PERMISSIONS);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < members.size(); i++)
			gui.setItem(i + 1, getNamedStack(Material.PAPER, members.get(i).getOfflinePlayer().getName()));
		return gui;
	}
	
	public static Inventory createColorsGUI(Kingdom kd){
		Inventory gui = Bukkit.createInventory(null, 18, NAME_CHANGE_COLORS);
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
	
	public static Inventory createOwnInvitesGUI(Player player, Kingdoms kds){
		ArrayList<Kingdom> invites = new ArrayList<Kingdom>();
		ArrayList<Kingdom> kingdoms = kds.getKingdoms();
		for(Kingdom kd : kingdoms)
			if(kd.isInvited(player))
				invites.add(kd);
		Inventory gui = Bukkit.createInventory(null, multipleOf9(invites.size() + 2), NAME_OWN_INVITES);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		gui.setItem(1, getNamedStack(Material.LAVA_BUCKET, "Deny", "Click to open a", "gui where you", "can deny invites."));
		for(int i = 0; i < invites.size(); i++){
			Kingdom kd = invites.get(i);
			gui.setItem(i + 2, getNamedStack(Material.PAPER, kd.getName(), "king is " + kd.getKing().getName(), kd.getMembers().size() + 1 + " members", "Click to join!"));
		}
		return gui;
	}
	
	public static Inventory createDeclineInvitesGUI(Player player, Kingdoms kds){
		ArrayList<Kingdom> invites = new ArrayList<Kingdom>();
		ArrayList<Kingdom> kingdoms = kds.getKingdoms();
		for(Kingdom kd : kingdoms)
			if(kd.isInvited(player))
				invites.add(kd);
		Inventory gui = Bukkit.createInventory(null, multipleOf9(invites.size() + 1), NAME_DECLINE_INVITES);
		gui.setItem(0, getNamedStack(Material.BARRIER, "Back"));
		for(int i = 0; i < invites.size(); i++){
			Kingdom kd = invites.get(i);
			gui.setItem(i + 1, getNamedStack(Material.PAPER, kd.getName(), "king is " + kd.getKing().getName(), kd.getMembers().size() + 1 + " members", "Click to deny!"));
		}
		return gui;
	}
	
	public static void toKDSpawnNow(Player player){
		Kingdom kd = KingdomsPlugin.getInstance().getKingdoms().getPlayerKingdom(player);
		if(kd != null)
			toKDSpawnNow(kd, player);
		else
			Translator.needKDToSpawn(player);
	}
	
	public static void toKDSpawnNow(Kingdom kd, Player player){
		if(kd.getKingdomSpawn() != null){
			player.teleport(kd.getBukkitKingdomSpawn(true), TeleportCause.PLUGIN);
			player.closeInventory();
		}
		else
			Translator.noKingdomSpawn(player);
	}
	
	public static void toKDSpawnStart(Kingdom kd, Player player){
		if(kd.getKingdomSpawn() != null){
			Translator.startTeleportingToKDSpawn(player);
			KingdomsPlugin.getInstance().startSpawning(player);
		}
		else
			Translator.noKingdomSpawn(player);
	}
	
	private final KingdomsPlugin plug;

	public CommandKingdom(KingdomsPlugin plugin) {
		plug = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0){
			if(sender instanceof Player){
				Player player = (Player) sender;
				player.openInventory(createKingdomGUI(player, plug.getKingdoms()));
			}
			else
				Translator.openGuiNotPlayer(sender);
			return true;
		}
		if(args.length == 1){
			if(args[0].equals("s") || args[0].equals("spawn")){
				if(sender instanceof Player){
					Kingdom kd = plug.getKingdoms().getPlayerKingdom((Player) sender);
					if(kd != null)
						toKDSpawnStart(kd, (Player) sender);
					else
						Translator.needKDToSpawn(sender);
				}
				else
					Translator.onlyPlayerCanSpawn(sender);
				return true;
			}
			if(args[0].equals("here")){
				if(sender instanceof Player){
					Kingdom kd = plug.getKingdoms().getLocationKingdom(((Player) sender).getLocation());
					Translator.showKDHere(sender, kd);
				}
				else
					Translator.onlyPlayerCanCheckLocation(sender);
				return true;
			}
			if(args[0].equals("info")){
				if(sender instanceof Player){
					Kingdom kd = plug.getKingdoms().getPlayerKingdom((Player) sender);
					if(kd != null)
						kd.getKingdomInfo().give((Player) sender);
					else
						Translator.noKDInfo(sender);
				}
				else
					Translator.noKDInfo(sender);
				return true;
			}
			if(args[0].equals("announcements")){
				if(sender instanceof Player){
					Kingdom kd = plug.getKingdoms().getPlayerKingdom((Player) sender);
					if(kd != null)
						kd.getInternalAnnouncements().give((Player)sender);
					else
						Translator.needKDForAnnouncements(sender);
				}
				else
					Translator.seeKDOPAnnouncements(sender);
				return true;
			}
		}
		if(args.length == 2){
			if(args[0].equals("kingtitle")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
					if(kd != null){
						if(kd.isKing(player)){
							kd.setKingTitle(args[1]);
							Translator.setKingTitle(player, args[1]);
						}
						else
							Translator.onlyKingCanPromote(player);
					}
					else
						Translator.needKDToPromote(player);
				}
				else
					Translator.seeKDOPKingTitle(sender);
				return true;
			}
			if(args[0].equals("info")){
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
		}
		if(args.length == 4){
			if(args[0].equals("promote")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
					if(kd != null){
						if(kd.isKing(player)){
							OfflinePlayer target = kd.getPlayer(args[1]);
							if(target != null){
								ChatColor rankColor = ChatColor.valueOf(args[3].toUpperCase());
								if(rankColor != null){
									kd.setPlayerTitle(target, args[2], rankColor);
									Translator.setMemberTitle(sender, target.getName(), args[2], rankColor);
								}
								else
									Translator.noSuchColor(sender, args[3]);
							}
							else
								Translator.playerNotInKingdom(player, args[1]);
						}
						else
							Translator.onlyKingCanPromote(player);
					}
					else
						Translator.needKDToPromote(player);
				}
				else
					Translator.seeKDOPPromote(sender);
				return true;
			}
			if(args[0].equals("permissions")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
					if(kd != null){
						if(kd.isKing(player)){
							OfflinePlayer target = kd.getPlayer(args[1]);
							if(target != null){
								String perm = args[2];
								String value = args[3];
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
										Translator.setInvitePermission(sender, target.getName());
									else
										Translator.playerNotInKingdom(sender, args[1]);
									return true;
								}
								else if(perm.equals("isDiplomatic")){
									if(kd.setDiplomaticPermission(target, bool))
										Translator.setDiplomaticPermission(sender, target.getName());
									else
										Translator.playerNotInKingdom(sender, args[1]);
									return true;
								}
								else {
									Translator.noSuchPermission(sender, perm);
									return true;
								}
							}
							else
								Translator.playerNotInKingdom(sender, args[1]);
						}
						else
							Translator.onlyKingCanPermissions(player);
					}
					else
						Translator.needKDForPermissions(player);
				}
				else
					Translator.seeKDOPPermissions(sender);
				return true;
			}
		}
		Translator.howToUseKingdom(sender);
		return false;
	}

}
