package nl.knokko.utils;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;

public final class Utils {
	
	public static final byte[] BYTES = new byte[]{64,32,16,8,4,2,1};

	public static boolean[] toBinair(byte b){
		boolean[] bools = new boolean[8];
		if(b >= 0)
			bools[7] = true;
		else {
			b++;
			b *= -1;
		}
		byte t = 0;
		while(t < 7){
			if(b >= BYTES[t]){
				b -= BYTES[t];
				bools[t] = true;
			}
			++t;
		}
		return bools;
	}
	
	public static byte fromBinair(boolean[] bools){
		byte b = 0;
		int t = 0;
		while(t < 7){
			if(bools[t])
				b += BYTES[t];
			++t;
		}
		if(!bools[7]){
			b *= -1;
			b--;
		}
		return b;
	}
	
	/**
	 * Use this method to find the player that 'owns' this Entity or is somehow responsible for this Entity.
	 * This method will return the same entity if the entity is a player.
	 * @param entity The entity
	 * @return The player that owns this Entity or is responsible for it, or null if there is no player responsible for this Entity.
	 */
	public static Player getPlayer(Object entity){
		if(entity instanceof Player)
			return (Player) entity;
		if(entity instanceof Tameable){
			Tameable tamed = (Tameable) entity;
			if(tamed.isTamed() && tamed.getOwner() != null)
				return getPlayer(tamed.getOwner());
		}
		if(entity instanceof Projectile){
			Projectile projectile = (Projectile) entity;
			ProjectileSource source = projectile.getShooter();
			if(source != null)
				return getPlayer(source);
		}
		if(entity instanceof TNTPrimed){
			TNTPrimed tnt = (TNTPrimed) entity;
			Entity source = tnt.getSource();
			if(source != null)
				return getPlayer(source);
		}
		return null;
	}
	
	public static boolean isMonster(Object entity){
		return entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast || entity instanceof EnderDragon;
	}
}
