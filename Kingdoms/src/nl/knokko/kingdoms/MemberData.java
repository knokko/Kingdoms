package nl.knokko.kingdoms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import nl.knokko.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MemberData {
	
	public static final byte MAX_TITLE_LENGTH = 50;
	
	private UUID player;
	
	private String title;
	private ChatColor titleColor;
	
	private boolean canInvite;
	private boolean isDiplomatic;

	public MemberData(UUID playerID) {
		player = playerID;
		reset();
	}
	
	private MemberData(UUID playerID, String title, ChatColor titleColor, boolean canInvite, boolean isDiplomatic){
		this.player = playerID;
		this.title = title;
		this.titleColor = titleColor;
		this.canInvite = canInvite;
		this.isDiplomatic = isDiplomatic;
	}
	
	public void reset(){
		canInvite = false;
		isDiplomatic = false;
		titleColor = ChatColor.GRAY;
	}
	
	public void setTitle(String title, ChatColor color){
		if(title.length() > MAX_TITLE_LENGTH)
			throw new IllegalArgumentException("The length of title " + title + " (" + title.length() + ") exceeds the limit of " + MAX_TITLE_LENGTH + " characters!");
		this.title = title;
		this.titleColor = color;
	}
	
	public void setInviting(boolean canInvite){
		this.canInvite = canInvite;
	}
	
	public void setDiplomatic(boolean isDiplomatic){
		this.isDiplomatic = isDiplomatic;
	}
	
	public UUID getPlayerID(){
		return player;
	}
	
	public OfflinePlayer getOfflinePlayer(){
		return Bukkit.getOfflinePlayer(player);
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(player);
	}
	
	public String getTitle(){
		return title != null ? title : "Burger";
	}
	
	public String getColoredTitle(){
		return titleColor + getTitle();
	}
	
	public boolean canInvite(){
		return canInvite;
	}
	
	public boolean isDiplomatic(){
		return isDiplomatic;
	}
	
	public ByteBuffer save(){
		ByteBuffer buffer = ByteBuffer.allocate(19 + (title != null ? (title.length() * 2) : 0));
		buffer.putLong(player.getLeastSignificantBits());
		buffer.putLong(player.getMostSignificantBits());
		buffer.putChar(titleColor.getChar());
		buffer.put(Utils.fromBinair(new boolean[]{canInvite, isDiplomatic, false, false, false, false, false, false}));
		if(title != null){
			for(byte b = 0; b < title.length(); b++)
				buffer.putChar(title.charAt(b));
		}
		return buffer;
	}
	
	public static MemberData load(ByteBuffer buffer) throws IOException {
		long least = buffer.getLong();
		long most = buffer.getLong();
		ChatColor color = ChatColor.getByChar(buffer.getChar());
		boolean[] permissions = Utils.toBinair(buffer.get());
		String title;
		if(buffer.hasRemaining()){
			title = "";
			while(buffer.hasRemaining())
				title += buffer.getChar();
		}
		else 
			title = null;
		return new MemberData(new UUID(most, least), title, color, permissions[0], permissions[1]);
	}
}
