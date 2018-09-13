package nl.knokko.commands;

import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Translator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTitle implements CommandExecutor {

private final KingdomsPlugin plugin;
	
	public CommandTitle(KingdomsPlugin plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender.isOp()){
			if(args.length <= 2){
				Translator.howToUseTitle(sender);
				return true;
			}
			String name = args[0];
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayer(name);
			String title = args[1];
			ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
			if(color == null){
				Translator.noSuchColor(sender, args[2]);
				return true;
			}
			if(player != null){
				plugin.setTitle(player, title, color);
				return true;
			}
			else
				Translator.playerNotOnline(sender, name);
			return true;
		}
		else {
			Translator.titleRequiresOperator(sender);
			return true;
		}
	}
}
