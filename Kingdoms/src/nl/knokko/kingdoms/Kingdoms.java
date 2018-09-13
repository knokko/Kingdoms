package nl.knokko.kingdoms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import nl.knokko.main.KDEventHandler;
import nl.knokko.utils.Translator;
import nl.knokko.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class Kingdoms {
	
	private final HashMap<UUID, Kingdom> playerKingdoms = new HashMap<UUID, Kingdom>();
	
	private final ArrayList<Kingdom> kingdoms = new ArrayList<Kingdom>();
	private final ArrayList<Kingdom> removedKingdoms = new ArrayList<Kingdom>();

	public Kingdom getPlayerKingdom(Player player){
		return playerKingdoms.get(player.getUniqueId());
	}
	
	public Kingdom getPlayerKingdom(UUID id){
		return playerKingdoms.get(id);
	}
	
	public void setPlayerKingdom(UUID id, Kingdom kd){
		playerKingdoms.put(id, kd);
	}
	
	public Kingdom getEntityKingdom(Entity entity){
		Player player = Utils.getPlayer(entity);
		return player != null ? getPlayerKingdom(player) : null;
	}
	
	public Kingdom getLocationKingdom(Location location){
		for(Kingdom kd : kingdoms){
			if(kd.isInside(location))
				return kd;
		}
		return null;
	}
	
	public OfflinePlayer getPlayerByName(String name){
		Player player = Bukkit.getPlayer(name);
		if(player != null)
			return player;
		for(Kingdom kd : kingdoms){
			ArrayList<MemberData> members = kd.getMembers();
			for(MemberData member : members){
				if(member.getOfflinePlayer().getName().equals(name))
					return member.getOfflinePlayer();
			}
		}
		return null;
	}
	
	public Kingdom getKingdom(String name){
		for(Kingdom kd : kingdoms)
			if(kd.getName().equals(name))
				return kd;
		return null;
	}
	
	public boolean hasKingdom(String name){
		for(Kingdom kd : kingdoms)
			if(kd.getName().equals(name))
				return true;
		return false;
	}
	
	public void addKingdom(Kingdom kd){
		kingdoms.add(kd);
		playerKingdoms.put(kd.getKingID(), kd);
		OfflinePlayer king = kd.getKing();
		if(king instanceof Player)
			KDEventHandler.updatePlayerName((Player) king, this);
	}
	
	public boolean removeKingdom(Kingdom kd){
		if(kingdoms.remove(kd)){
			removedKingdoms.add(kd);
			kd.clearMembers(this);
			return true;
		}
		return false;
	}
	
	public void removeAlliance(Kingdom breaker, Kingdom ally){
		Translator.broadcastQuitAlly(breaker, ally);
		breaker.removeAllyFromList(ally);
		ally.removeAllyFromList(breaker);
	}
	
	public ArrayList<Kingdom> getKingdoms(){
		return kingdoms;
	}
	
	public boolean save(String path){
		try {
			boolean succes = true;
			PrintWriter writer = new PrintWriter(path + File.separator + "kingdoms.list");
			for(Kingdom kd : kingdoms)
				writer.println(kd.getName());
			writer.close();
			for(Kingdom kd : removedKingdoms)
				kd.deleteData(path);
			for(Kingdom kd : kingdoms){
				try {
					kd.save(path);
				} catch(Exception ex){
					Bukkit.getLogger().log(Level.WARNING, "Failed to save kingdom " + kd.getName(), ex);
					succes = false;
				}
			}
			return succes;
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to save the kingdoms!", ex);
			return false;
		}
	}
	
	public boolean load(String path){
		try {
			playerKingdoms.clear();
			kingdoms.clear();
			boolean succes = true;
			BufferedReader reader = new BufferedReader(new FileReader(new File(path + File.separator + "kingdoms.list")));
			String line = reader.readLine();
			while(line != null){
				try {
					addKingdom(Kingdom.loadKingdom(path, line));
				} catch(Exception ex){
					Bukkit.getLogger().log(Level.WARNING, "Failed to load kingdom " + line, ex);
					succes = false;
				}
				line = reader.readLine();
			}
			reader.close();
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			for(Player player : players)
				KDEventHandler.updatePlayerName(player, this);
			return succes;
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to load the kingdoms!", ex);
			return false;
		}
	}
}
