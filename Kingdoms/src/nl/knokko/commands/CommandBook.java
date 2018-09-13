package nl.knokko.commands;

import nl.knokko.utils.Translator;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandBook implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if(sender instanceof Player){
			((Player) sender).getInventory().addItem(new ItemStack[]{new ItemStack(Material.BOOK_AND_QUILL)});
			Translator.receivedBookAndQuil(sender);
		}
		else
			Translator.onlyPlayerCanReceiveBook(sender);
		return false;
	}

}
