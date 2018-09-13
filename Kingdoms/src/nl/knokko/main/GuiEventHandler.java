package nl.knokko.main;

import java.util.ArrayList;
import java.util.Arrays;

import nl.knokko.commands.CommandKingdom;
import nl.knokko.commands.CommandKingdomOP;
import nl.knokko.kingdoms.Kingdom;
import nl.knokko.kingdoms.Kingdom.Shape;
import nl.knokko.kingdoms.MemberData;
import nl.knokko.utils.Book;
import nl.knokko.utils.Translator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class GuiEventHandler implements Listener {
	
	private final KingdomsPlugin plug;

	public GuiEventHandler(KingdomsPlugin plugin) {
		plug = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			if(!(event.getWhoClicked() instanceof Player))
				return;
			Player player = (Player) event.getWhoClicked();
			if(event.getInventory() == null || event.getCurrentItem() == null)
				return;
			String invName = event.getInventory().getName();
			Material type = event.getCurrentItem().getType();
			if(plug.isStaff(player)){
				if(invName.equals(CommandKingdomOP.NAME_GUI)){
					if(type == Material.BARRIER)
						player.closeInventory();
					if(type == Material.BOOK_AND_QUILL)
						CommandKingdomOP.save(plug, player);
					if(type == Material.WRITTEN_BOOK)
						CommandKingdomOP.load(plug, player);
					if(type == Material.REDSTONE_COMPARATOR)
						player.openInventory(CommandKingdomOP.createSettingsGUI(plug.getSettings()));
					if(type == Material.SAPLING)
						player.openInventory(CommandKingdomOP.createKingdomCreateGUI(plug.getKingdoms()));
					if(type == Material.BANNER)
						player.openInventory(CommandKingdomOP.createKingdomsGUI(plug.getKingdoms()));
					event.setCancelled(true);
				}
				else if(invName.equals(CommandKingdomOP.NAME_SETTINGS_GUI)){
					event.setCancelled(true);
					if(type == Material.BARRIER){
						player.openInventory(CommandKingdomOP.GUI);
						return;
					}
					if(type == Material.GOLD_SWORD)
						plug.getSettings().setFriendlyFire(!plug.getSettings().canAttackAllies());
					if(type == Material.DIAMOND_PICKAXE)
						plug.getSettings().setOtherKDGrief(!plug.getSettings().canGriefOtherKingdoms());
					if(type == Material.WOODEN_DOOR)
						plug.getSettings().setFreeToLeave(!plug.getSettings().isFreeToLeave());
					if(type == Material.BOOK_AND_QUILL)
						plug.getSettings().doLogGriefing(!plug.getSettings().logGriefing());
					if(type == Material.BOOKSHELF)
						plug.getSettings().doLogOwnKingdomEdit(!plug.getSettings().logOwnKingdomEdit());
					if(type == Material.CHEST)
						plug.getSettings().doLogChests(!plug.getSettings().logChestActivity());
					player.openInventory(CommandKingdomOP.createSettingsGUI(plug.getSettings()));
				}
				else if(invName.equals(CommandKingdomOP.NAME_CREATE_KD_GUI)){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdomOP.GUI);
					if(type == Material.GOLD_BLOCK){
						Translator.helpCreateKingdom(player, event.getCurrentItem().getItemMeta().getDisplayName());
						player.closeInventory();
					}
					event.setCancelled(true);
				}
				else if(invName.equals(CommandKingdomOP.NAME_KINGDOMS_GUI)){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdomOP.GUI);
					if(type == Material.BANNER){
						String name = event.getCurrentItem().getItemMeta().getDisplayName();
						Kingdom kd = plug.getKingdoms().getKingdom(name);
						if(kd != null)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else
							player.closeInventory();
					}
					event.setCancelled(true);
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_KINGDOM_GUI)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_KINGDOM_GUI.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.GUI);
						if(type == Material.LAVA_BUCKET){
							if(plug.getKingdoms().removeKingdom(kd))
								Translator.removedKingdom(player, kdName);
							else
								Translator.cantFindKingdom(player, kdName);
							player.openInventory(CommandKingdomOP.createKingdomsGUI(plug.getKingdoms()));
						}
						if(type == Material.GOLD_BLOCK)
							player.openInventory(CommandKingdomOP.createChangeKingGUI(kd));
						if(type == Material.CLAY_BRICK)
							player.openInventory(CommandKingdomOP.createInviteGUI(kd, plug.getKingdoms()));
						if(type == Material.BRICK)
							player.openInventory(CommandKingdomOP.createInvitesGUI(kd));
						if(type == Material.IRON_BOOTS)
							player.openInventory(CommandKingdomOP.createKickPlayerGUI(kd));
						if(type == Material.DIAMOND)
							player.openInventory(CommandKingdomOP.createPromotePlayerGui(kd));
						if(type == Material.PAPER)
							player.openInventory(CommandKingdomOP.createMemberPermissionsGui(kd));
						if(type == Material.BANNER){
							kd.setLocation(player.getLocation(), kd.getShape(), kd.getRadius());
							Translator.setKingdomCentre(player, kd.getName());
						}
						if(type == Material.BED){
							kd.setKingdomSpawn(player.getLocation());
							Translator.setKingdomSpawn(player, kd.getName());
						}
						if(type == Material.SLIME_BALL)
							player.openInventory(CommandKingdomOP.createTerritoryShapeGui(kd));
						if(type == Material.BEACON)
							player.openInventory(CommandKingdomOP.createTerritoryRadiusGUI(kd));
						if(type == Material.ENDER_PEARL){
							Location spawn = kd.getBukkitKingdomSpawn(true);
							if(spawn != null){
								player.teleport(spawn, TeleportCause.PLUGIN);
								player.closeInventory();
							}
							else
								Translator.noKingdomSpawn(player, kd.getName());
						}
						if(type == Material.WOOL)
							player.openInventory(CommandKingdomOP.createColorsGUI(kd));
						if(type == Material.WRITTEN_BOOK)
							kd.getKingdomInfo().give(player);
						if(type == Material.WATER_BUCKET)
							kd.getInternalAnnouncements().give(player);
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_CHANGE_KING)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_CHANGE_KING.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else {
							OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
							if(target != null){
								kd.changeKing(target.getUniqueId());
								player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
								Translator.youChangedKing(player, target.getName(), kdName);
							}
							else
								player.closeInventory();
						}
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_KICK_PLAYER)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_KICK_PLAYER.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else {
							OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
							if(target != null){
								if(kd.removeMember(plug.getKingdoms(), target.getUniqueId())){
									player.openInventory(CommandKingdomOP.createKickPlayerGUI(kd));
									Translator.youKickedPlayer(player, target.getName(), kdName);
									Translator.broadcastStaffKick(kd, target.getName());
								}
								else
									Translator.cannotKickPlayer(player, target.getName(), kdName);
							}
							else
								player.closeInventory();
						}
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_PROMOTE_PLAYER)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_PROMOTE_PLAYER.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else if(type == Material.GOLD_BLOCK){
							OfflinePlayer target = kd.getKing();
							if(target != null){
								Translator.howToPromoteKingOP(player, kd.getName());
								player.closeInventory();
							}
							else
								player.closeInventory();
						}
						else {
							OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
							if(target != null){
								Translator.howToPromoteOP(player, target.getName(), kd);
								player.closeInventory();
							}
							else
								player.closeInventory();
						}
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_PERMISSIONS_MEMBER)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_PERMISSIONS_MEMBER.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else {
							OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
							if(target != null){
								Translator.howToChangeMemberPermissionsOP(player, target.getName(), kd);
								player.closeInventory();
							}
							else
								player.closeInventory();
						}
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_TERRITORY_SHAPE)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_TERRITORY_SHAPE.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER){
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						}
						else if(type == Material.SLIME_BALL){
							if(kd.getShape() == Shape.CIRCLE)
								Translator.alreadyThisShape(player, Shape.CIRCLE, kd.getName());
							else {
								kd.setLocation(kd.getCentre(), Shape.CIRCLE, kd.getRadius());
								Translator.changedKingdomShape(player, Shape.CIRCLE, kd.getName());
							}
						}
						else if(type == Material.SLIME_BLOCK){
							if(kd.getShape() == Shape.SQUARE)
								Translator.alreadyThisShape(player, Shape.SQUARE, kd.getName());
							else {
								kd.setLocation(kd.getCentre(), Shape.SQUARE, kd.getRadius());
								Translator.changedKingdomShape(player, Shape.SQUARE, kd.getName());
							}
						}
						else
							player.closeInventory();
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_TERRITORY_RADIUS)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_TERRITORY_RADIUS.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER){
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
							return;
						}
						try {
							int radius = Integer.decode(event.getCurrentItem().getItemMeta().getDisplayName());
							if(kd.getRadius() != radius){
								kd.setLocation(kd.getCentre(), kd.getShape(), radius);
								Translator.changedKingdomRadius(player, radius, kdName);
							}
							else
								Translator.alreadyThisRadius(player, radius, kdName);
						} catch(Exception ex){
							ex.printStackTrace();
							player.closeInventory();
						}
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_COLORS)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_COLORS.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else if(type == Material.LAVA_BUCKET)
							kd.setColors(new ChatColor[]{ChatColor.GRAY});
						else {
							String lore = event.getCurrentItem().getItemMeta().getLore().get(0);
							ChatColor color = ChatColor.valueOf(lore.substring(CommandKingdomOP.COLOR_PRE_TEXT.length()).toUpperCase());
							ChatColor[] colors = kd.getColors();
							if(colors.length > 1 || colors[0] != ChatColor.GRAY){
								colors = Arrays.copyOf(colors, colors.length + 1);
								colors[colors.length - 1] = color;
							}
							else
								colors[0] = color;
							kd.setColors(colors);
						}
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_INVITE)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_INVITE.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else if(kd.invite(event.getCurrentItem().getItemMeta().getDisplayName())){
							Translator.invitedPlayer(player, event.getCurrentItem().getItemMeta().getDisplayName(), kdName);
							player.openInventory(CommandKingdomOP.createInviteGUI(kd, plug.getKingdoms()));
						}
						else
							player.closeInventory();
					}
					else
						player.closeInventory();
				}
				else if(invName.startsWith(CommandKingdomOP.PRE_NAME_INVITES)){
					event.setCancelled(true);
					String kdName = invName.substring(CommandKingdomOP.PRE_NAME_INVITES.length());
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null){
						if(type == Material.BARRIER)
							player.openInventory(CommandKingdomOP.createKingdomGUI(kd));
						else {
							if(kd.cancelInvite(event.getCurrentItem().getItemMeta().getDisplayName())){
								Translator.cancelInvite(player, event.getCurrentItem().getItemMeta().getDisplayName(), kdName);
								player.openInventory(CommandKingdomOP.createInvitesGUI(kd));
							}
							else
								player.closeInventory();
						}
					}
					else
						player.closeInventory();
				}
			}
			if(invName.equals(CommandKingdom.NAME_INVITE)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else {
						String targetName = event.getCurrentItem().getItemMeta().getDisplayName();
						if(kd.invite(targetName)){
							Translator.invitedPlayer(player, targetName);
							player.openInventory(CommandKingdom.createInviteGUI(kd, plug.getKingdoms()));
						}
						else
							player.closeInventory();
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_KD_GUI)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.closeInventory();
					if(type == Material.LAVA_BUCKET){
						if(kd.isKing(player) && plug.getKingdoms().removeKingdom(kd)){
							player.closeInventory();
							Translator.removedKingdom(player);
						}
						else
							player.closeInventory();
					}
					if(type == Material.GOLD_BLOCK)
						player.openInventory(CommandKingdom.createChangeKingGUI(kd));
					if(type == Material.IRON_BOOTS)
						player.openInventory(CommandKingdom.createKickGUI(kd));
					if(type == Material.CLAY_BRICK)
						player.openInventory(CommandKingdom.createInviteGUI(kd, plug.getKingdoms()));
					if(type == Material.BRICK)
						player.openInventory(CommandKingdom.createInvitesGUI(kd));
					if(type == Material.DIAMOND)
						player.openInventory(CommandKingdom.createRankGUI(kd));
					if(type == Material.PAPER)
						player.openInventory(CommandKingdom.createPermissionsGUI(kd));
					if(type == Material.BED){
						Location playerLocation = player.getLocation();
						if(kd.isInside(playerLocation)){
							kd.setKingdomSpawn(playerLocation);
							Translator.setKingdomSpawn(player);
						}
						else
							Translator.cannotSetKingdomSpawn(player);
					}
					if(type == Material.ENDER_PEARL){
						CommandKingdom.toKDSpawnStart(kd, player);
						player.closeInventory();
					}
					if(type == Material.WRITTEN_BOOK)
						kd.getKingdomInfo().give(player);
					if(type == Material.BOOK_AND_QUILL){
						ItemStack item = player.getInventory().getItemInMainHand();
						if(item != null && item.getType() == Material.WRITTEN_BOOK)
							kd.setKingdomInfo(new Book(item));
						else
							Translator.needWrittenBookToSetKingdomInfo(player);
					}
					if(type == Material.WOOL)
						player.openInventory(CommandKingdom.createColorsGUI(kd));
					if(type == Material.BANNER)
						player.openInventory(CommandKingdom.createKingdomsGUI(plug.getKingdoms()));
					if(type == Material.IRON_INGOT)
						player.openInventory(CommandKingdom.createOwnInvitesGUI(player, plug.getKingdoms()));
					if(type == Material.WOODEN_DOOR && plug.getSettings().isFreeToLeave()){
						if(kd.removeMember(plug.getKingdoms(), player.getUniqueId())){
							Translator.broadcastLeftKingdom(kd, player.getName());
							Translator.leftKingdom(player, kd.getName());
						}
						else
							player.closeInventory();
					}
					if(type == Material.WATER_BUCKET)
						kd.getInternalAnnouncements().give(player);
					if(type == Material.BUCKET){
						ItemStack item = player.getInventory().getItemInMainHand();
						if(item != null && item.getType() == Material.WRITTEN_BOOK)
							kd.setAnnouncements(new Book(item));
						else
							Translator.needWrittenBookToSetKingdomAnnouncements(player);
					}
					if(type == Material.COMPASS)
						Translator.showKDHere(player, plug.getKingdoms().getLocationKingdom(player.getLocation()));
					if(type == Material.SHIELD)
						player.openInventory(CommandKingdom.createAlliesGUI(kd, player));
					if(type == Material.IRON_CHESTPLATE)
						player.openInventory(CommandKingdom.createAllyInvitesGUI(kd, player));
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_ALLIES)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else if(kd.isDiplomatic(player.getUniqueId())){
						String ally = event.getCurrentItem().getItemMeta().getDisplayName();
						Kingdom ak = plug.getKingdoms().getKingdom(ally);
						if(ak != null)
							plug.getKingdoms().removeAlliance(kd, ak);
						else
							player.closeInventory();
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_ALLY_INVITE)){
				event.setCancelled(true);
				if(type == Material.BARRIER){
					player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					return;
				}
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(!kd.isDiplomatic(player.getUniqueId()))
					return;
				Kingdom other = plug.getKingdoms().getKingdom(event.getCurrentItem().getItemMeta().getDisplayName());
				if(kd != null && other != null){
					if(type == Material.GOLD_CHESTPLATE){
						if(kd.isInvited(other)){
							kd.cancelInvite(other);
							player.openInventory(CommandKingdom.createAllyInvitesGUI(kd, player));
						}
						else
							player.closeInventory();
					}
					else if(type == Material.IRON_CHESTPLATE || type == Material.DIAMOND_CHESTPLATE){
						kd.inviteAlly(other);
						player.openInventory(CommandKingdom.createAllyInvitesGUI(kd, player));
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_INVITES)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else {
						String targetName = event.getCurrentItem().getItemMeta().getDisplayName();
						if(kd.cancelInvite(targetName)){
							Translator.cancelInvite(player, targetName);
							player.openInventory(CommandKingdom.createInvitesGUI(kd));
						}
						else
							player.closeInventory();
					}
				}
			}
			else if(invName.equals(CommandKingdom.NAME_KINGDOMS)){
				event.setCancelled(true);
				if(type == Material.BARRIER)
					player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
				if(type == Material.BANNER){
					String kdName = event.getCurrentItem().getItemMeta().getDisplayName();
					Kingdom kd = plug.getKingdoms().getKingdom(kdName);
					if(kd != null)
						kd.getKingdomInfo().give(player);
					else
						player.closeInventory();
				}	
			}
			else if(invName.equals(CommandKingdom.NAME_CHANGE_KING)){
				event.setCancelled(true);
				if(type == Material.BARRIER)
					player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
				else {
					Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
					if(kd != null && kd.isKing(player)){
						String targetName = event.getCurrentItem().getItemMeta().getDisplayName();
						ArrayList<MemberData> members = kd.getMembers();
						for(MemberData data : members){
							OfflinePlayer member = data.getOfflinePlayer();
							if(member != null && member.getName().equals(targetName)){
								kd.changeKing(member.getUniqueId());
								Translator.youChangedKing(player, targetName);
								return;
							}
						}
						player.closeInventory();
					}
					else
						player.closeInventory();
				}
			}
			else if(invName.equals(CommandKingdom.NAME_KICK_MEMBER)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null && kd.isKing(player)){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else {
						OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
						if(target != null){
							if(kd.removeMember(plug.getKingdoms(), target.getUniqueId())){
								Translator.youKickedPlayer(player, target.getName());
								Translator.broadcastKick(kd, target.getName());
								player.openInventory(CommandKingdom.createKickGUI(kd));
							}
							else
								Translator.cannotKickPlayer(player, target.getName());
						}
						else
							player.closeInventory();
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_GRANT_RANK)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else if(type == Material.GOLD_BLOCK){
						OfflinePlayer target = kd.getKing();
						if(target != null){
							Translator.howToPromoteKing(player);
							player.closeInventory();
						}
						else
							player.closeInventory();
					}
					else {
						OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
						if(target != null){
							Translator.howToPromote(player, target.getName());
							player.closeInventory();
						}
						else
							player.closeInventory();
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_PERMISSIONS)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else {
						OfflinePlayer target = kd.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
						if(target != null){
							Translator.howToChangeMemberPermissions(player, target.getName());
							player.closeInventory();
						}
						else
							player.closeInventory();
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_CHANGE_COLORS)){
				event.setCancelled(true);
				Kingdom kd = plug.getKingdoms().getPlayerKingdom(player);
				if(kd != null){
					if(type == Material.BARRIER)
						player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					else if(type == Material.LAVA_BUCKET)
						kd.setColors(new ChatColor[]{ChatColor.GRAY});
					else {
						String lore = event.getCurrentItem().getItemMeta().getLore().get(0);
						ChatColor color = ChatColor.valueOf(lore.substring(CommandKingdomOP.COLOR_PRE_TEXT.length()).toUpperCase());
						ChatColor[] colors = kd.getColors();
						if(colors.length > 1 || colors[0] != ChatColor.GRAY){
							colors = Arrays.copyOf(colors, colors.length + 1);
							colors[colors.length - 1] = color;
						}
						else
							colors[0] = color;
						kd.setColors(colors);
					}
				}
				else
					player.closeInventory();
			}
			else if(invName.equals(CommandKingdom.NAME_OWN_INVITES)){
				event.setCancelled(true);
				if(type == Material.BARRIER)
					player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
				else if(type == Material.LAVA_BUCKET)
					player.openInventory(CommandKingdom.createDeclineInvitesGUI(player, plug.getKingdoms()));
				else {
					Kingdom kd = plug.getKingdoms().getKingdom(event.getCurrentItem().getItemMeta().getDisplayName());
					if(kd != null){
						if(kd.isInvited(player)){
							Kingdom current = plug.getKingdoms().getPlayerKingdom(player);
							if(current == null || plug.getSettings().isFreeToLeave()){
								Translator.broadcastJoinedKingdom(kd, player.getName());
								kd.addMember(plug.getKingdoms(), player, true);
								Translator.joinedKingdom(player, kd.getColoredName());
							}
							else
								Translator.cannotLeaveKingdom(player, current.getName(), kd.getName());
							player.closeInventory();
						}
						else
							player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					}
					else
						player.closeInventory();
				}
			}
			else if(invName.equals(CommandKingdom.NAME_DECLINE_INVITES)){
				event.setCancelled(true);
				if(type == Material.BARRIER)
					player.openInventory(CommandKingdom.createOwnInvitesGUI(player, plug.getKingdoms()));
				else {
					Kingdom kd = plug.getKingdoms().getKingdom(event.getCurrentItem().getItemMeta().getDisplayName());
					if(kd != null){
						if(kd.isInvited(player)){
							Translator.broadcastDeclinedInvite(kd, player.getName());
							kd.cancelInvite(player.getName());
							Translator.declinedInvite(player, kd.getColoredName());
							player.openInventory(CommandKingdom.createDeclineInvitesGUI(player, plug.getKingdoms()));
						}
						else
							player.openInventory(CommandKingdom.createKingdomGUI(player, plug.getKingdoms()));
					}
					else
						player.closeInventory();
				}
			}
			else if(invName.equals(CommandKingdom.NAME_NO_KD_GUI)){
				event.setCancelled(true);
				if(type == Material.BARRIER)
					player.closeInventory();
				if(type == Material.IRON_INGOT)
					player.openInventory(CommandKingdom.createOwnInvitesGUI(player, plug.getKingdoms()));
				if(type == Material.COMPASS)
					Translator.showKDHere(player, plug.getKingdoms().getLocationKingdom(player.getLocation()));
				if(type == Material.BANNER)
					player.openInventory(CommandKingdom.createKingdomsGUI(plug.getKingdoms()));
			}
		}
	}
}
