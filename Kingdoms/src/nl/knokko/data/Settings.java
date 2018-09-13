package nl.knokko.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Utils;

import org.bukkit.Bukkit;

public class Settings {
	
	private boolean friendlyFire;
	private boolean griefOtherKDs;
	private boolean freeToLeave;
	
	private boolean logGriefing;
	private boolean logOwnKingdomEdit;
	private boolean logChests;
	
	public Settings(boolean friendlyFire, boolean griefOtherKDs, boolean freeToLeave, boolean logGriefing, boolean logKingdomMembers, boolean logChests){
		this.friendlyFire = friendlyFire;
		this.griefOtherKDs = griefOtherKDs;
		this.logGriefing = logGriefing;
		this.logOwnKingdomEdit = logKingdomMembers;
		this.logChests = logChests;
	}
	
	public Settings(){
		this(false, true, true, true, false, true);
	}
	
	public boolean save(String path){
		return save11C(path);
	}
	
	@SuppressWarnings("unused")
	private boolean save10C(String path){
		try {
			FileOutputStream stream = new FileOutputStream(path + File.separator + "general.set");
			stream.write(KingdomsPlugin.VERSION_10C);
			stream.write(Utils.fromBinair(new boolean[]{friendlyFire, griefOtherKDs, logGriefing, logChests, freeToLeave, false, false, false}));
			stream.close();
			return true;
		} catch(Exception ex){
			Bukkit.getLogger().log(Level.WARNING, "Failed to save the general settings", ex);
			return false;
		}
	}
	
	private boolean save11C(String path){
		try {
			FileOutputStream stream = new FileOutputStream(path + File.separator + "general.set");
			stream.write(KingdomsPlugin.VERSION_10C);
			stream.write(Utils.fromBinair(new boolean[]{friendlyFire, griefOtherKDs, logGriefing, logChests, freeToLeave, logOwnKingdomEdit, false, false}));
			stream.close();
			return true;
		} catch(Exception ex){
			Bukkit.getLogger().log(Level.WARNING, "Failed to save the general settings", ex);
			return false;
		}
	}
	
	public boolean load(String path){
		try {
			FileInputStream stream = new FileInputStream(path + File.separator + "general.set");
			byte version = (byte) stream.read();
			if(version == KingdomsPlugin.VERSION_10C)
				load10C(stream);
			if(version == KingdomsPlugin.VERSION_11C)
				load11C(stream);
			stream.close();
			return true;
		} catch(Exception ex){
			Bukkit.getLogger().log(Level.WARNING, "Failed to load the general settings", ex);
			return false;
		}
	}
	
	private void load10C(FileInputStream stream) throws IOException {
		boolean[] settings = Utils.toBinair((byte) stream.read());
		friendlyFire = settings[0];
		griefOtherKDs = settings[1];
		logGriefing = settings[2];
		logChests = settings[3];
		freeToLeave = settings[4];
		logOwnKingdomEdit = false;
	}
	
	private void load11C(FileInputStream stream) throws IOException {
		boolean[] settings = Utils.toBinair((byte) stream.read());
		friendlyFire = settings[0];
		griefOtherKDs = settings[1];
		logGriefing = settings[2];
		logChests = settings[3];
		freeToLeave = settings[4];
		logOwnKingdomEdit = settings[5];
	}
	
	public boolean canGriefOtherKingdoms(){
		return griefOtherKDs;
	}
	
	public boolean canAttackAllies(){
		return friendlyFire;
	}
	
	public boolean logGriefing(){
		return logGriefing;
	}
	
	public boolean logOwnKingdomEdit(){
		return logOwnKingdomEdit;
	}
	
	public boolean logChestActivity(){
		return logChests;
	}
	
	public void setFriendlyFire(boolean value){
		friendlyFire = value;
	}
	
	public void setOtherKDGrief(boolean value){
		griefOtherKDs = value;
	}
	
	public void doLogGriefing(boolean value){
		logGriefing = value;
	}
	
	public void doLogOwnKingdomEdit(boolean value){
		logOwnKingdomEdit = value;
	}
	
	public void doLogChests(boolean value){
		logChests = value;
	}
	
	public void setFreeToLeave(boolean value){
		freeToLeave = value;
	}
	
	public boolean isFreeToLeave(){
		return freeToLeave;
	}
}
