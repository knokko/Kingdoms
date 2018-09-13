package nl.knokko.main;

import java.util.Collection;
import java.util.List;

import nl.knokko.kingdoms.Kingdom;
import nl.knokko.kingdoms.Kingdoms;
import nl.knokko.utils.Translator;
import nl.knokko.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.potion.PotionEffectType.*;

public class KDEventHandler implements Listener {
	
	private KingdomsPlugin plug;

	public KDEventHandler(KingdomsPlugin plugin) {
		plug = plugin;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event){
		updatePlayerName(event.getPlayer(), plug.getKingdoms());
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityAttack(EntityDamageByEntityEvent event){
		if(!canAttack(event.getEntity(), event.getDamager())){
			Translator.ownKDAttack(Utils.getPlayer(event.getDamager()));
			event.setCancelled(true);
			return;
		}
		if(!canAttackHere(event.getEntity(), event.getDamager(), plug.getKingdoms().getLocationKingdom(event.getEntity().getLocation()))){
			Translator.otherKDAttack(Utils.getPlayer(event.getDamager()));
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event){
		ThrownPotion potion = event.getPotion();
		Player player = Utils.getPlayer(potion);
		if(player == null)
			return;
		Collection<PotionEffect> effects = potion.getEffects();
		boolean negative = false;
		for(PotionEffect effect : effects)
			if(isNegative(effect))
				negative = true;
		if(!negative)
			return;
		Collection<LivingEntity> targets = event.getAffectedEntities();
		for(LivingEntity target : targets){
			if(!canAttack(target, potion)){
				event.setIntensity(target, 0);
				Translator.ownKDAttack(player);
			}
			else if(!canAttackHere(target, potion, plug.getKingdoms().getLocationKingdom(target.getLocation()))){
				event.setIntensity(target, 0);
				Translator.otherKDAttack(player);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLingeringSplash(AreaEffectCloudApplyEvent event){
		AreaEffectCloud potion = event.getEntity();
		Player player = Utils.getPlayer(potion);
		if(player == null)
			return;
		Collection<PotionEffect> effects = potion.getCustomEffects();
		boolean negative = false;
		for(PotionEffect effect : effects)
			if(isNegative(effect))
				negative = true;
		if(!negative)
			return;
		List<LivingEntity> targets = event.getAffectedEntities();
		for(LivingEntity target : targets){
			if(!canAttack(target, potion))
				targets.remove(target);
			else if(!canAttackHere(target, potion, plug.getKingdoms().getLocationKingdom(target.getLocation())))
				targets.remove(target);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event){
		if(!canEditHere(event.getPlayer(), event.getBlock().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event){
		if(!canEditHere(event.getPlayer(), event.getBlockPlaced().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event){
		Player player = Utils.getPlayer(event.getEntity());
		if(!canEditHere(player, event.getLocation())){
			Translator.otherKDEdit(player);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityInteract(EntityInteractEvent event){
		Player player = Utils.getPlayer(event.getEntity());
		if(!canEditHere(player, event.getBlock().getLocation())){
			Translator.otherKDEdit(player);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event){
		if(event.getTarget() == null)
			return;
		if(!canAttack(event.getTarget(), event.getEntity()) || !canAttackHere(event.getTarget(), event.getEntity(), plug.getKingdoms().getLocationKingdom(event.getTarget().getLocation())))
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerEntityInteract(PlayerInteractEntityEvent event){
		if(!canEditHere(event.getPlayer(), event.getRightClicked().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getClickedBlock() != null && !canEditHere(event.getPlayer(), event.getClickedBlock().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityLeash(PlayerLeashEntityEvent event){
		if(!canEditHere(event.getPlayer(), event.getEntity().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityShear(PlayerShearEntityEvent event){
		if(!canEditHere(event.getPlayer(), event.getEntity().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBedEnter(PlayerBedEnterEvent event){
		if(!canEditHere(event.getPlayer(), event.getBed().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketUse(PlayerBucketFillEvent event){
		if(!canEditHere(event.getPlayer(), event.getBlockClicked().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketUse(PlayerBucketEmptyEvent event){
		if(!canEditHere(event.getPlayer(), event.getBlockClicked().getLocation())){
			Translator.otherKDEdit(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event){
		double dis = event.getFrom().distance(event.getTo());
		if(dis == 0)
			return;
		if(dis >= 0.0647577 /*sneak speed */ && plug.isSpawning(event.getPlayer())){
			plug.endSpawning(event.getPlayer());
			Translator.cancelledKDSpawnTeleport(event.getPlayer());
		}
		Kingdom from = plug.getKingdoms().getLocationKingdom(event.getFrom());
		Kingdom to = plug.getKingdoms().getLocationKingdom(event.getTo());
		if(from != to)
			Translator.enterKingdom(event.getPlayer(), to);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		plug.endSpawning(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		updatePlayerName(event.getPlayer(), plug.getKingdoms());
		Kingdom kd = plug.getKingdoms().getPlayerKingdom(event.getPlayer());
		if(kd != null && !event.getMessage().startsWith("!")){
			event.setCancelled(true);
			String message = KingdomsPlugin.getInstance().getServerTitle(event.getPlayer()) + kd.getRankString(event.getPlayer()) + event.getPlayer().getName() + ": " + event.getMessage();
			List<Player> players = kd.getOnlinePlayers();
			for(Player player : players)
				player.sendMessage(message);
		}
		else if(kd != null)
			event.setMessage(event.getMessage().substring(1));
	}
	
	public boolean isNegative(PotionEffect effect){
		PotionEffectType type = effect.getType();
		return type == BLINDNESS || type == CONFUSION || type == HARM || type == POISON || type == SLOW || type == SLOW_DIGGING || type == UNLUCK || type == WEAKNESS || type == WITHER;
	}
	
	public boolean canEditHere(Player player, Location location){
		if(plug.getSettings().canGriefOtherKingdoms())
			return true;
		if(player == null)
			return true;
		Kingdom locKD = plug.getKingdoms().getLocationKingdom(location);
		if(locKD == null)
			return true;
		Kingdom playerKD = plug.getKingdoms().getPlayerKingdom(player);
		return locKD == playerKD;
	}
	
	public boolean canAttack(Entity target, Entity attacker){
		if(plug.getSettings().canAttackAllies())
			return true;
		Player targetPlayer = Utils.getPlayer(target);
		Player attackPlayer = Utils.getPlayer(attacker);
		if(targetPlayer == null || attackPlayer == null)
			return true;
		Kingdom targetKD = plug.getKingdoms().getPlayerKingdom(targetPlayer);
		Kingdom attackKD = plug.getKingdoms().getPlayerKingdom(attackPlayer);
		return canAttack(targetKD, attackKD);
	}
	
	public boolean canAttackHere(Entity target, Entity attacker, Kingdom kd){
		if(plug.getSettings().canAttackAllies())
			return true;
		if(kd == null)
			return true;
		if(Utils.isMonster(target))
			return true;
		Kingdom attKD = plug.getKingdoms().getEntityKingdom(attacker);
		return attKD == kd;
	}
	
	public boolean canAttack(Kingdom targetKD, Kingdom attackKD){
		if(plug.getSettings().canAttackAllies())
			return true;
		if(targetKD == null || attackKD == null)
			return true;
		return targetKD != attackKD;
	}
	
	public static void updatePlayerName(Player player, Kingdoms kds){
		Kingdom kd = kds.getPlayerKingdom(player);
		String dis;
		if(kd == null)
			dis = getKingdomString(kd) + player.getName();
		else
			dis = getKingdomString(kd) + kd.getRankString(player) + player.getName() + kd.getColors()[0];
		dis = KingdomsPlugin.getInstance().getServerTitle(player) + dis;
		player.setDisplayName(dis);
		player.setPlayerListName(dis);
		player.setCustomName(dis);
	}
	
	public static String getKingdomString(Kingdom kd){
		if(kd != null)
			return ChatColor.WHITE + "[" + kd.getColoredName() + ChatColor.WHITE + "]";
		else
			return "[Free]";
	}
}
