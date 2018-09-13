package nl.knokko.commands;

import nl.knokko.kingdoms.Kingdom;
import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Translator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGriefLogger implements CommandExecutor {
	
	private final KingdomsPlugin plugin;

	public CommandGriefLogger(KingdomsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 1){
			if(args[0].equals("save")){
				plugin.getGriefLogger().save();
				Translator.savedGriefLogData(sender);
				return true;
			}
		}
		if(args.length == 2){
			/*
			if(args[0].equals("request")){
				Kingdom kd = plugin.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					if(sender instanceof Player){
						plugin.getGriefLogger().sendKingdomBlockLog((Player) sender, kd);
						plugin.getGriefLogger().sendKingdomEntityLog((Player) sender, kd);
					}
					else
						Translator.onlyPlayerCanUseGriefLogger(sender);
				}
				else
					Translator.cantFindKingdom(sender, args[1]);
			}
			*/
			if(args[0].equals("kingdom")){
				Kingdom kd = plugin.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					plugin.getGriefLogger().logKingdom(kd, true);
					Translator.loggedGriefLog(sender, kd.getName(), plugin);
				}
				else
					Translator.cantFindKingdom(sender, args[1]);
				return true;
			}
			if(args[0].equals("player")){
				Translator.searchForPlayerGriefing(sender, args[1]);
				plugin.getGriefLogger().logPlayer(args[1], false);
				Translator.savedPlayerGriefing(sender, plugin, args[1]);
				return true;
			}
		}
		if(args.length == 3){
			if(args[0].equals("kingdom")){
				Kingdom kd = plugin.getKingdoms().getKingdom(args[1]);
				if(kd != null){
					String b = args[2];
					if(b.equals("yes")){
						plugin.getGriefLogger().logKingdom(kd, false);
						Translator.loggedGriefLog(sender, kd.getName(), plugin);
					}
					else if(b.equals("no")){
						plugin.getGriefLogger().logKingdom(kd, true);
						Translator.loggedGriefLog(sender, kd.getName(), plugin);
					}
					else
						Translator.notValidBooleanValue(sender, args[2]);
					return true;
				}
				else
					Translator.cantFindKingdom(sender, args[1]);
				return true;
			}
			if(args[0].equals("player")){
				if(args[2].equals("yes")){
					plugin.getGriefLogger().logPlayer(args[1], false);
					Translator.savedPlayerGriefing(sender, plugin, args[1]);
				}
				else if(args[2].equals("no")){
					plugin.getGriefLogger().logPlayer(args[1], true);
					Translator.savedPlayerGriefing(sender, plugin, args[1]);
				}
				else
					Translator.notValidBooleanValue(sender, args[2]);
				return true;
			}
		}
		Translator.howToUseGriefLogger(sender);
		return false;
	}

}
