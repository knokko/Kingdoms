package nl.knokko.commands;

import nl.knokko.kingdoms.Kingdom;
import nl.knokko.main.KingdomsPlugin;
import nl.knokko.utils.Translator;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandItemLogger implements CommandExecutor {
	
	private final KingdomsPlugin plugin;

	public CommandItemLogger(KingdomsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 1){
			if(args[0].equals("save")){
				if(plugin.isStaff(sender)){
					plugin.getItemLogger().save();
					Translator.savedItemLogData(sender);
				}
				else
					Translator.itemLoggerRequiresOP(sender);
				return true;
			}
		}
		if(args.length == 2){
			if(args[0].equals("player")){
				if(plugin.isStaff(sender)){
					OfflinePlayer target = plugin.getKingdoms().getPlayerByName(args[1]);
					if(target != null){
						plugin.getItemLogger().logPlayer(target.getUniqueId());
						Translator.loggedItemLog(sender, args[1], plugin);
					}
					else
						Translator.playerNotOnline(sender, args[1]);
				}
				else
					Translator.itemLoggerRequiresOP(sender);
				return true;
			}
			if(args[0].equals("kingdom")){
				if(plugin.isStaff(sender)){
					Kingdom kd = plugin.getKingdoms().getKingdom(args[1]);
					if(kd != null){
						if(kd.getCentre() != null && kd.getRadius() > 0 && kd.getShape() != null){
							plugin.getItemLogger().logKingdom(kd);
							Translator.loggedItemKingdomLog(sender, args[1], plugin);
						}
						else {
							Translator.noKingdomTerritory(sender, kd);
						}
					}
					else
						Translator.cantFindKingdom(sender, args[1]);
				}
				else
					Translator.itemLoggerRequiresOP(sender);
				return true;
			}
		}
		Translator.howToUseItemLogger(sender);
		return false;
	}

}
