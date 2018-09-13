package nl.knokko.utils;

import java.io.File;
import java.util.ArrayList;

import nl.knokko.kingdoms.Kingdom;
import nl.knokko.kingdoms.MemberData;
import nl.knokko.kingdoms.Kingdom.Shape;
import nl.knokko.main.KingdomsPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public final class Translator {
	
	public static void ownKDAttack(Player player){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt geen leden van je eigen kingdom aanvallen.");
		else
			player.sendMessage(RED + "You can't hurt members of your own kingdom.");
	}
	
	public static void otherKDAttack(Player player){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt alleen monsters aanvallen in andere kingdoms, tenzij er oorlog is.");
		else
			player.sendMessage(RED + "You can only attack monsters in other kingdoms, unless there is war.");
	}
	
	public static void otherKDEdit(Player player){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt alleen andere kingdoms bewerken als er oorlog is.");
		else
			player.sendMessage(RED + "You can only edit other kingdoms if there is war.");
	}
	
	public static void removedKingdom(Player player, String kd){
		if(isNL(player))
			player.sendMessage(YELLOW + "Je hebt het kingdom " + kd + " succesvol verwijderd.");
		else
			player.sendMessage(YELLOW + "You succesfully removed kingdom " + kd);
	}
	
	public static void removedKingdom(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(YELLOW + "Je hebt je kingdom succesvol verwijderd.");
		else
			sender.sendMessage(YELLOW + "You have removed your kingdom succesfully.");
	}
	
	public static void receivedBookAndQuil(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Als het goed is, heb je een 'boek en veer' gekregen.");
		else
			sender.sendMessage(GREEN + "You should have received a 'book and quil'.");
	}
	
	public static void cantFindKingdom(CommandSender player, String name){
		if(isNL(player))
			player.sendMessage(RED + "Er is geen kingdom met de naam " + name);
		else
			player.sendMessage(RED + "There is no kingdom with name " + name);
	}
	
	public static void titleRequiresOperator(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen operatoren kunnen titels geven!");
		else
			sender.sendMessage(RED + "Only operators can give titles!");
	}
	
	public static void itemLoggerRequiresOP(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen operatoren kunnen de Item Logger geven!");
		else
			sender.sendMessage(RED + "Only operators can use the Item Logger!");
	}
	
	public static void needKDToPromote(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je kunt alleen rangen uitdelen als je in een kingdom zit.");
		else
			sender.sendMessage(RED + "You can only grant ranks if you are in a kingdom.");
	}
	
	public static void onlyKingCanPromote(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen de koningen kan rangen geven aan andere leden.");
		else
			sender.sendMessage(RED + "Only the king can grant ranks to other members.");
	}
	
	public static void needKDForPermissions(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je kunt alleen bevoegdheden wijzigen als je in een kingdom zit.");
		else
			sender.sendMessage(RED + "You can only edit permissions if you are in a kingdom.");
	}
	
	public static void needKDToSpawn(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je kunt niet naar je kingdom spawn teleporteren omdat je niet in een kingdom zit.");
		else
			sender.sendMessage(RED + "You can't teleport to your kingdom spawn because you are not in a kingdom.");
	}
	
	public static void needKDForAnnouncements(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je kunt alleen de interne aankondigingen van je kingdom bekijken als je in een kingdom zit.");
		else
			sender.sendMessage(RED + "You can only see the internal announcements of your kingdom if you are in a kingdom.");
	}
	
	public static void needOPForKDOP(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen staffleden kunnen gebruik maken van /kdop");
		else
			sender.sendMessage(RED + "Only staff members can use /kdop");
	}
	
	public static void onlyKingCanPermissions(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen de koningen kan bevoegdheden geven aan andere leden.");
		else
			sender.sendMessage(RED + "Only the king can edit permissions of other members.");
	}
	
	public static void enterKingdom(Player player, Kingdom kd){
		if(isNL(player)){
			if(kd != null)
				player.sendMessage(WHITE + "Je betreedt het gebied van " + kd.getColoredName() + WHITE + ".");
			else
				player.sendMessage(WHITE + "Je betreedt de wildernis.");
		}
		else {
			if(kd != null)
				player.sendMessage(WHITE + "You have entered the territory of " + kd.getColoredName() + WHITE + ".");
			else
				player.sendMessage(WHITE + "You have entered the wilderniss.");
		}
	}
	
	public static void showKDHere(CommandSender sender, Kingdom kd){
		if(isNL(sender)){
			if(kd != null)
				sender.sendMessage(WHITE + "Je bevindt je in het gebied van " + kd.getColoredName());
			else
				sender.sendMessage(WHITE + "Je bevindt je in de wildernis.");
		}
		else {
			if(kd != null)
				sender.sendMessage(WHITE + "You are in the territory of " + kd.getColoredName());
			else
				sender.sendMessage(WHITE + "You are in the wilderniss.");
		}
	}
	
	public static void changeKing(Player player, String newKingName){
		if(isNL(player))
			player.sendMessage(GOLD + newKingName + " is je nieuwe koning!");
		else
			player.sendMessage(GOLD + newKingName + " is your new king!");
	}
	
	public static void youChangedKing(Player player, String newKingName, String kdName){
		if(isNL(player))
			player.sendMessage(GREEN + "Je hebt " + newKingName + " benoemd tot de koning van " + kdName);
		else
			player.sendMessage(GREEN + "You have made " + newKingName + " the king of " + kdName);
	}
	
	public static void youChangedKing(CommandSender sender, String newKing){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + newKing + " benoemd tot de nieuwe koning van je kingdom.");
		else
			sender.sendMessage(GREEN + "You have made " + newKing + " the new king of your kingdom.");
	}
	
	public static void youKickedPlayer(Player player, String targetName, String kdName){
		if(isNL(player))
			player.sendMessage(GREEN + "Je hebt " + targetName + " uit " + kdName + " geschopt.");
		else
			player.sendMessage(GREEN + "You have kicked " + targetName + " out of " + kdName);
	}
	
	public static void youKickedPlayer(Player player, String targetName){
		if(isNL(player))
			player.sendMessage(GREEN + "Je hebt " + targetName + " uit je kingdom geschopt.");
		else
			player.sendMessage(GREEN + "You have kicked " + targetName + " out of your kingdom.");
	}
	
	public static void cannotKickPlayer(Player player, String targetName, String kdName){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt " + targetName + " niet uit " + kdName + " schoppen omdat hij geen lid is van " + kdName);
		else
			player.sendMessage(RED + "You can't kick " + targetName + " out of " + kdName + " because he is not a member of " + kdName);
	}
	
	public static void cannotKickPlayer(Player player, String targetName){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt " + targetName + " niet uit je kingdom schoppen omdat hij geen lid is van je kingdom.");
		else
			player.sendMessage(RED + "You can't kick " + targetName + " out of your kingdom because he is not a member of your kingdom.");
	}
	
	public static void cannotLeaveKingdom(Player player, String currentKD, String wishedKD){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt nog geen lid worden van " + wishedKD + " omdat je nog lid bent van " + currentKD);
		else
			player.sendMessage(RED + "You can't join " + wishedKD + " because you are still a member of " + currentKD);
	}
	
	public static void invitedPlayer(CommandSender sender, String targetName, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + targetName + " uitgenodigt om lid te worden van " + kdName);
		else
			sender.sendMessage(GREEN + "You have invited " + targetName + " for " + kdName);
	}
	
	public static void invitedPlayer(CommandSender sender, String targetName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + targetName + " uitgenodigt om lid te worden van je kingdom");
		else
			sender.sendMessage(GREEN + "You have invited " + targetName + " for your kingdom");
	}
	
	public static void receivedInvite(CommandSender sender, String kdName){
		if(isNL(sender))
			sender.sendMessage(BLUE + "Je bent uitgenodigd om lid te worden van " + kdName);
		else
			sender.sendMessage(BLUE + "You have been invited to join " + kdName);
	}
	
	public static void declinedInvite(CommandSender sender, String kdName){
		if(isNL(sender))
			sender.sendMessage(BLUE + "Je hebt de uitnodiging om lid te worden van " + kdName + " afgewezen");
		else
			sender.sendMessage(BLUE + "You have declined the invitation to join " + kdName);
	}
	
	public static void cancelInvite(CommandSender sender, String targetName, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de uitnodiging voor " + targetName + " ingetrokken om lid te worden van " + kdName);
		else
			sender.sendMessage(GREEN + "You cancelled the invite for " + targetName + " to join " + kdName);
	}
	
	public static void cancelInvite(CommandSender sender, String targetName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de uitnodiging voor " + targetName + " ingetrokken");
		else
			sender.sendMessage(GREEN + "You cancelled the invite for " + targetName);
	}
	
	public static void joinedKingdom(Player player, String kdName){
		if(isNL(player))
			player.sendMessage(GREEN + "Je bent lid geworden van " + kdName);
		else
			player.sendMessage(GREEN + "You joined " + kdName);
	}
	
	public static void leftKingdom(Player player, String kdName){
		if(isNL(player))
			player.sendMessage(YELLOW + "Je hebt " + kdName + " verlaten");
		else
			player.sendMessage(YELLOW + "You have left " + kdName);
	}
	
	public static void searchForPlayerGriefing(CommandSender sender, String targetName){
		if(isNL(sender))
			sender.sendMessage(YELLOW + "De gehele grief log wordt doorzocht voor acties van " + targetName + ", dit kan even duren...");
		else
			sender.sendMessage(YELLOW + "The entire grief log will be searched for actions of " + targetName + ", this can take a while...");
	}
	
	public static void savedPlayerGriefing(CommandSender sender, KingdomsPlugin plug, String targetName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "De grief log van " + targetName + " is opgeslagen als " + plug.getDataFolder().getAbsolutePath() + File.separator + "Grief Logger" + File.separator + "... log of " + targetName + ".txt");
		else
			sender.sendMessage(GREEN + "The grief log of " + targetName + " has been saved as " + plug.getDataFolder().getAbsolutePath() + File.separator + "Grief Logger" + File.separator + "... log of " + targetName + ".txt");
	}
	
	public static void loggedGriefLog(CommandSender sender, String kdName, KingdomsPlugin plugin){
		if(isNL(sender))
			sender.sendMessage(GREEN + "De log van " + kdName + " is opgeslagen als " + plugin.getDataFolder().getAbsolutePath() + "/Grief Logger/" + kdName + " ... log.txt");
		else
			sender.sendMessage(GREEN + "The log of " + kdName + " has been saved as " + plugin.getDataFolder().getAbsolutePath() + "/Grief Logger/" + kdName + " ... log.txt");
	}
	
	public static void loggedItemKingdomLog(CommandSender sender, String kdName, KingdomsPlugin plugin){
		if(isNL(sender))
			sender.sendMessage(GREEN + "De item log van " + kdName + " is opgeslagen als " + plugin.getDataFolder().getAbsolutePath() + "/Item Logger/" + kdName + " item log.txt");
		else
			sender.sendMessage(GREEN + "The item log of " + kdName + " has been saved as " + plugin.getDataFolder().getAbsolutePath() + "/Item Logger/" + kdName + " item log.txt");
	}
	
	public static void loggedItemLog(CommandSender sender, String targetName, KingdomsPlugin plugin){
		if(isNL(sender))
			sender.sendMessage(GREEN + "De item log van " + targetName + " is opgeslagen als " + plugin.getDataFolder().getAbsolutePath() + "/Item Logger/" + targetName + " item log.txt");
		else
			sender.sendMessage(GREEN + "The item log of " + targetName + " has been saved as " + plugin.getDataFolder().getAbsolutePath() + "/Item Logger/" + targetName + "item log.txt");
	}
	
	public static void savedGriefLogData(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Alle gegevens van de Grief Logger zijn opgeslagen.");
		else
			sender.sendMessage(GREEN + "All data of the Item Logger has been saved.");
	}
	
	public static void savedItemLogData(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Alle gegevens van de Item Logger zijn opgeslagen.");
		else
			sender.sendMessage(GREEN + "All data of the Item Logger has been saved.");
	}
	
	public static void save(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Alle kingdom gegevens zijn succesvol opgeslagen.");
		else
			sender.sendMessage(GREEN + "All kingdom data has been saved succesfully.");
	}
	
	public static void load(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Alle kingdom gegevens zijn succesvol geladen.");
		else
			sender.sendMessage(GREEN + "All kingdom data is succesfully loaded.");
	}
	
	public static void failSave(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(DARK_RED + "Het opslaan van de gegevens is (gedeeltelijk) mislukt!");
		else
			sender.sendMessage(DARK_RED + "Saving all data failed (partially)!");
	}
	
	public static void failLoad(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(DARK_RED + "Het laden van alle gegevens is (gedeeltelijk) mislukt!");
		else
			sender.sendMessage(DARK_RED + "Loading all data failed (partially)!");
	}
	
	public static void openGuiNotPlayer(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage("Alleen spelers kunnen een GUI openen.");
		else
			sender.sendMessage("Only players can open a GUI.");
	}
	
	public static void onlyPlayerCanSpawn(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je kunt niet naar je kingdom spawn teleporteren omdat je geen speler bent.");
		else
			sender.sendMessage(RED + "You can't teleport to your kingdom spawn because you are not a player.");
	}
	
	public static void onlyPlayerCanCheckLocation(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen spelers kunnen zien in het gebied van welk kingdom ze zich bevinden.");
		else
			sender.sendMessage(RED + "Only players can see in which territory they are.");
	}
	
	public static void onlyPlayerCanUseGriefLogger(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen spelers kunnen de grief log inzien.");
		else
			sender.sendMessage(RED + "Only players can look at the grief log.");
	}
	
	public static void onlyPlayerCanReceiveBook(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Alleen spelers kunnen een 'boek en veer' krijgen.");
		else
			sender.sendMessage(RED + "Only players can receive a 'book and quil'.");
	}
	
	public static void seeKDOPPromote(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage("Alleen spelers kunnen [/kd promote ...] gebruiken. Maar elke operator kan het volgende commando gebruiken:");
			sender.sendMessage("/kdop promote [kingdom] [speler] [naam van de rang] [kleur van de rang]");
		}
		else {
			sender.sendMessage("Only players can use [/kd promote ...]. However, any operator can use the following command:");
			sender.sendMessage("/kdop promote [kingdom] [player name] [rankname] [rankcolor]");
		}
	}
	
	public static void seeKDOPKingTitle(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage("Alleen spelers kunnen [/kd kingtitle ...] gebruiken. Maar elke operator kan het volgende commando gebruiken:");
			sender.sendMessage("/kdop kingtitle [kingdom] [titel van de koning]");
		}
		else {
			sender.sendMessage("Only players can use [/kd kingtitle ...]. However, any operator can use the following command:");
			sender.sendMessage("/kdop kingtitle [kingdom] [king title]");
		}
	}
	
	public static void seeKDOPPermissions(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage("Alleen spelers kunnen [/kd permissions ...] gebruiken. Maar elke operator kan het volgende commando gebruiken:");
			sender.sendMessage("/kdop permissions [kingdom] [speler] [naam van de permissie] ['yes' of 'no']");
			sender.sendMessage("De beschikbare permissies zijn:" + getMemberPermissions());
		}
		else {
			sender.sendMessage("Only players can use [/kd permissions ...]. However, any operator can use the following command:");
			sender.sendMessage("/kdop permissions [kingdom] [player name] [permission name] ['yes' or 'no']");
			sender.sendMessage("The available permissions are:" + getMemberPermissions());
		}
	}
	
	public static void seeKDOPAnnouncements(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage("Alleen spelers kunnen gebruik maken van [/kd announcements], maar elke operator kan het volgende commando gebruiken:");
			sender.sendMessage("/kdop announcements [naam van het kingdom]");
		}
		else {
			sender.sendMessage("Only players can use [/kd announcements], but every operator can use the following command:");
			sender.sendMessage("/kdop announcements [name of the kingdom]");
		}
	}
	
	public static void noKDInfo(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage("Alleen spelers in een kingodm kunnen [/kd info] gebruiken, maar iedereen kan het volgende commando gebruiken:");
			sender.sendMessage("/kd info [naam van het kingdom]");
		}
		else {
			sender.sendMessage("Only players in a kingodm can use [/kd info], but everyone can use the following command:");
			sender.sendMessage("/kd info [name of the kingdom]");
		}
	}
	
	public static void kingdomAlreadyExists(CommandSender sender, String name){
		if(isNL(sender))
			sender.sendMessage(RED + "Er is al een kingdom met de naam " + name);
		else
			sender.sendMessage(RED + "A kingdom with name " + name + " already exists.");
	}
	
	public static void playerNotOnline(CommandSender sender, String name){
		if(isNL(sender))
			sender.sendMessage(RED + "De speler " + name + " is niet online.");
		else
			sender.sendMessage(RED + "The player " + name + " is not online.");
	}
	
	public static void playerAlreadyInKingdom(CommandSender sender, String name, Kingdom kd){
		if(isNL(sender))
			sender.sendMessage(RED + "De speler " + name + " is al lid van " + kd.getColoredName());
		else
			sender.sendMessage(RED + "The player " + name + " is already a member of " + kd.getColoredName());
	}
	
	public static void createdKingdom(CommandSender sender, String name){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt het kingdom " + name + " succesvol aangemaakt.");
		else
			sender.sendMessage(GREEN + "You succesfully created kingdom " + name);
	}
	
	public static void helpCreateKingdom(Player player, String king){
		if(isNL(player)){
			player.sendMessage(YELLOW + "Gebruik het volgende commando:");
			player.sendMessage(YELLOW + "/kdop create [naam van de kingdom] " + king);
		}
		else {
			player.sendMessage(YELLOW + "Use the following command:");
			player.sendMessage(YELLOW + "/kdop create [name of the kingdom] " + king);
		}
	}
	
	public static void howToPromoteOP(Player player, String target, Kingdom kd){
		if(isNL(player)){
			player.sendMessage(YELLOW + "Gebruik het volgende commando:");
			player.sendMessage(YELLOW + "/kdop promote " + kd.getName() + " " + target + " [de naam van de rang] [de kleur van de rang]");
			player.sendMessage(YELLOW + "De beschikbare kleuren zijn: " + getChatColors());
		}
		else {
			player.sendMessage(YELLOW + "Use the following command:");
			player.sendMessage(YELLOW + "/kdop promote " + kd.getName() + " " + target + " [the name of the rank] [the color of the rank]");
			player.sendMessage(YELLOW + "The available colors are:" + getChatColors());
		}
	}
	
	public static void howToPromote(Player player, String target){
		if(isNL(player)){
			player.sendMessage(YELLOW + "Gebruik het volgende commando:");
			player.sendMessage(YELLOW + "/kd promote " + target + " [de naam van de rang] [de kleur van de rang]");
			player.sendMessage(YELLOW + "De beschikbare kleuren zijn: " + getChatColors());
		}
		else {
			player.sendMessage(YELLOW + "Use the following command:");
			player.sendMessage(YELLOW + "/kd promote " + target + " [the name of the rank] [the color of the rank]");
			player.sendMessage(YELLOW + "The available colors are:" + getChatColors());
		}
	}
	
	public static void howToPromoteKing(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage(YELLOW + "Gebruik het volgende commando:");
			sender.sendMessage(YELLOW + "/kd kingtitle [de titel van de king]");
		}
		else {
			sender.sendMessage(YELLOW + "Use the following command:");
			sender.sendMessage(YELLOW + "/kd kingtitle [title of the king]");
		}
	}
	
	public static void howToPromoteKingOP(CommandSender sender, String kdName){
		if(isNL(sender)){
			sender.sendMessage(YELLOW + "Gebruik het volgende commando:");
			sender.sendMessage(YELLOW + "/kdop kingtitle " + kdName + " [de titel van de king]");
		}
		else {
			sender.sendMessage(YELLOW + "Use the following command:");
			sender.sendMessage(YELLOW + "/kdop kingtitle " + kdName + "[title of the king]");
		}
	}
	
	public static void howToChangeMemberPermissionsOP(Player player, String target, Kingdom kd){
		if(isNL(player)){
			player.sendMessage(YELLOW + "Gebruik het volgende commando:");
			player.sendMessage(YELLOW + "/kdop permissions " + kd.getName() + " " + target + " [permissie die je wilt aanpassen] ['yes' of 'no'");
			player.sendMessage(YELLOW + "Je kunt kiezen uit de permissies:" + getMemberPermissions());
		}
		else {
			player.sendMessage(YELLOW + "Use the following command:");
			player.sendMessage(YELLOW + "/kdop permissions " + kd.getName() + " " + target + " [the permission you want to change] ['yes' or 'no'");
			player.sendMessage(YELLOW + "You can choose from the permissions:" + getMemberPermissions());
		}
	}
	
	public static void howToChangeMemberPermissions(Player player, String target){
		if(isNL(player)){
			player.sendMessage(YELLOW + "Gebruik het volgende commando:");
			player.sendMessage(YELLOW + "/kdop permissions " + target + " [permissie die je wilt aanpassen] ['yes' of 'no'");
			player.sendMessage(YELLOW + "Je kunt kiezen uit de permissies:" + getMemberPermissions());
		}
		else {
			player.sendMessage(YELLOW + "Use the following command:");
			player.sendMessage(YELLOW + "/kdop permissions " + target + " [the permission you want to change] ['yes' or 'no'");
			player.sendMessage(YELLOW + "You can choose from the permissions:" + getMemberPermissions());
		}
	}
	
	public static void howToUseGriefLogger(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage(RED + "Gebruik /gl kingdom [naam van kingdom] (rapporteer ook acties van leden)");
			sender.sendMessage(RED + "Of gebruik /gl player [naam van de speler] (rapporteer ook acties binnen eigen kingdom)");
		}
		else {
			sender.sendMessage(RED + "Use /gl kingdom [kingdomname] (report actions of own members as well)");
			sender.sendMessage(RED + "or use /gl player [name of the player] (report actions in own kingdom as well)");
		}
	}
	
	public static void howToUseItemLogger(CommandSender sender){
		if(isNL(sender)){
			sender.sendMessage(RED + "Gebruik /il player [spelersnaam]");
			sender.sendMessage(RED + "Of gebruik /il kingdom [naam van het kingdom]");
		}
		else {
			sender.sendMessage(RED + "Use /il player [playername]");
			sender.sendMessage(RED + "Or use /il kingdom [kingdomname]");
		}
	}
	
	public static void howToUseKingdomOP(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Gebruik gewoon /kdop");
		else
			sender.sendMessage(RED + "Just use /kdop");
	}
	
	public static void howToUseKingdom(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Gebruik gewoon /kd");
		else
			sender.sendMessage(RED + "Just use /kd");
	}
	
	public static void howToUseTitle(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Gebruik /title [spelersnaam] [titelnaam] [titelkleur]");
		else
			sender.sendMessage(RED + "Use /title [player name] [title name] [title color]");
	}
	
	public static void playerNotInKingdom(CommandSender sender, String playerName, String kdName){
		if(isNL(sender))
			sender.sendMessage(RED + "De speler " + playerName + " is geen lid van kingdom " + kdName);
		else
			sender.sendMessage(RED + "The player " + playerName + " is no member of kingdom " + kdName);
	}
	
	public static void playerNotInKingdom(CommandSender sender, String playerName){
		if(isNL(sender))
			sender.sendMessage(RED + playerName + " is geen lid van je kingdom.");
		else
			sender.sendMessage(RED + playerName + " is not a member of your kingdom.");
	}
	
	public static void notValidBooleanValue(CommandSender sender, String falseValue){
		if(isNL(sender))
			sender.sendMessage(RED + "'" + falseValue + "' zou 'yes' of 'nee' moeten zijn");
		else
			sender.sendMessage(RED + "'" + falseValue + "' should be 'yes' or 'no'");
	}
	
	public static void setInvitePermission(CommandSender sender, String targetName, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + targetName + " het recht gegeven om spelers uit te nodigen in " + kdName);
		else
			sender.sendMessage(GREEN + "You gave " + targetName + " the permission to invite players in " + kdName);
	}
	
	public static void setInvitePermission(CommandSender sender, String targetName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + targetName + " het recht gegeven om spelers uit te nodigen in je kingdom");
		else
			sender.sendMessage(GREEN + "You gave " + targetName + " the permission to invite players in your kingdom");
	}
	
	public static void setDiplomaticPermission(CommandSender sender, String targetName, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + targetName + " diplomatieke rechten gegeven in " + kdName);
		else
			sender.sendMessage(GREEN + "You gave " + targetName + " diplomatic permissions in " + kdName);
	}
	
	public static void setDiplomaticPermission(CommandSender sender, String targetName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt " + targetName + " diplomatieke rechten gegeven in je kingdom");
		else
			sender.sendMessage(GREEN + "You gave " + targetName + " diplomatic permissions in your kingdom");
	}
	
	public static void setMemberTitle(CommandSender sender, String targetName, String titleName, ChatColor titleColor, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de rang van " + targetName + " in " + kdName + " veranderd naar " + titleColor + titleName);
		else
			sender.sendMessage(GREEN + "You changed the rang of " + targetName + " in " + kdName + " to " + titleColor + titleName);
	}
	
	public static void setMemberTitle(CommandSender sender, String targetName, String titleName, ChatColor titleColor){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de rang van " + targetName + " veranderd naar " + titleColor + titleName);
		else
			sender.sendMessage(GREEN + "You changed the rang of " + targetName + " to " + titleColor + titleName);
	}
	
	public static void setKingTitle(CommandSender sender, String titleName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de titel van de koning veranderd naar " + titleName);
		else
			sender.sendMessage(GREEN + "You changed the title of the king to " + titleName);
	}
	
	public static void setKingTitleOP(CommandSender sender, String titleName, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de titel van de koning van " + kdName + " veranderd naar " + titleName);
		else
			sender.sendMessage(GREEN + "You changed the title of the king of " + kdName + " to " + titleName);
	}
	
	public static void setKingdomCentre(Player sender, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt het centrum van het territorium van " + kdName + " verzet naar " + sender.getLocation());
		else
			sender.sendMessage(GREEN + "You have set the centre of the territory of " + kdName + " to " + sender.getLocation());
	}
	
	public static void setKingdomSpawn(Player player, String kdName){
		if(isNL(player))
			player.sendMessage(GREEN + "Je hebt de spawn van " + kdName + " verzet naar " + getLocationString(player.getLocation()));
		else
			player.sendMessage(GREEN + "You have set the spawn of " + kdName + " to " + getLocationString(player.getLocation()));
	}
	
	public static void setKingdomSpawn(Player player){
		if(isNL(player))
			player.sendMessage(GREEN + "Je hebt de spawn van je kingdom verzet naar " + getLocationString(player.getLocation()));
		else
			player.sendMessage(GREEN + "You have changed your kingdom spawn to " + getLocationString(player.getLocation()));
	}
	
	public static void cannotSetKingdomSpawn(Player player){
		if(isNL(player))
			player.sendMessage(RED + "Je kunt de spawn van je kingdom alleen binnen het territorium van je kingdom zetten.");
		else
			player.sendMessage(RED + "You can only set your kingdom spawn within your kingdoms territory.");
	}
	
	public static void changedKingdomShape(CommandSender sender, Shape shape, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de vorm van het territorium van " + kdName + " veranderd naar een " + shape.getDutchName());
		else
			sender.sendMessage(GREEN + "You have changed the shape of the territory of " + kdName + " to a " + shape.name().toLowerCase());
	}
	
	public static void changedKingdomRadius(CommandSender sender, int newRadius, String kdName){
		if(isNL(sender))
			sender.sendMessage(GREEN + "Je hebt de straal van het territorium van " + kdName + " veranderd naar " + newRadius + " blokken");
		else
			sender.sendMessage(GREEN + "You have changed the radius of the territory of " + kdName + " to " + newRadius + " blocks");
	}
	
	public static void alreadyThisShape(CommandSender sender, Shape shape, String kdName){
		if(isNL(sender))
			sender.sendMessage(RED + "De vorm van het territorium van " + kdName + " is al een " + shape.getDutchName());
		else
			sender.sendMessage(RED + "The shape of the territory of " + kdName + " is already a " + shape.name().toLowerCase());
	}
	
	public static void alreadyThisRadius(CommandSender sender, int radius, String kdName){
		if(isNL(sender))
			sender.sendMessage(RED + "De straal van het territorium van " + kdName + " is al " + radius + " blokken.");
		else
			sender.sendMessage(RED + "The radius of the territory of " + kdName + " is already " + radius + " blocks");
	}
	
	public static void noSuchPermission(CommandSender sender, String permission){
		if(isNL(sender))
			sender.sendMessage(RED + "'" + permission + "' is geen permissie, je kunt kiezen uit:" + getMemberPermissions());
		else
			sender.sendMessage(RED + "'" + permission + "' is no valid permission, you can choose from:" + getMemberPermissions());
	}
	
	public static void noSuchColor(CommandSender sender, String falseColor){
		if(isNL(sender))
			sender.sendMessage(RED + "'" + falseColor + "' is geen geldige kleur, je kunt wel kiezen uit:" + getChatColors());
		else
			sender.sendMessage(RED + "'" + falseColor + "' is no valid color, you can choose from:" + getChatColors());
	}
	
	public static void noKingdomSpawn(CommandSender sender, String kdName){
		if(isNL(sender))
			sender.sendMessage(RED + kdName + " heeft nog geen kingdom spawn");
		else
			sender.sendMessage(RED + kdName + " Doet not have a kingdom spawn");
	}
	
	public static void noKingdomSpawn(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je kingdom heeft nog geen kingdom spawn");
		else
			sender.sendMessage(RED + "Your kingdom does not have a spawn point");
	}
	
	public static void noKingdomTerritory(CommandSender sender, Kingdom kd){
		if(isNL(sender))
			sender.sendMessage(RED + "Er is nog geen territorium gemarkeerd voor " + kd.getColoredName());
		else
			sender.sendMessage(RED + "There is no territory claimed for " + kd.getColoredName());
	}
	
	public static void needWrittenBookToSetKingdomInfo(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je moet een geschreven boek vasthouden om je kingdom info naar te veranderen.");
		else
			sender.sendMessage(RED + "You need to hold a written book to change your kingdom info to.");
	}
	
	public static void needWrittenBookToSetKingdomAnnouncements(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je moet een geschreven boek vasthouden om je interne aankondigingen naar te veranderen.");
		else
			sender.sendMessage(RED + "You need to hold a written book to change your internal announcements to.");
	}
	
	public static void startTeleportingToKDSpawn(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(LIGHT_PURPLE + "Je wordt over 5 seconden geteleporteerd naar je kingdom spawn, als je niet beweegt.");
		else
			sender.sendMessage(LIGHT_PURPLE + "You will be teleported to your kingdom spawn in 5 seconds if you don't move.");
	}
	
	public static void cancelledKDSpawnTeleport(CommandSender sender){
		if(isNL(sender))
			sender.sendMessage(RED + "Je teleportatie is gestopt omdat je hebt bewogen!");
		else
			sender.sendMessage(RED + "Your teleportation has been stopped because you moved!");
	}
	
	public static void broadcastLeftKingdom(Kingdom kd, String name){
		broadcast(kd, YELLOW + name + " has left your kingdom", YELLOW + name + " heeft je kingdom verlaten");
	}
	
	public static void broadcastStaffKick(Kingdom kd, String name){
		broadcast(kd, YELLOW + name + " has been kicked out of your kingdom by a staff-member", YELLOW + name + " is uit je kingdom geschopt door een staff-lid");
	}
	
	public static void broadcastKick(Kingdom kd, String name){
		broadcast(kd, YELLOW + name + " has been kicked out of your kingdom", YELLOW + name + " is uit je kingdom geschopt");
	}
	
	public static void broadcastJoinedKingdom(Kingdom kd, String name){
		broadcast(kd, YELLOW + name + " has joined your kingdom", YELLOW + name + " is lid geworden van je kingdom");
	}
	
	public static void broadcastDeclinedInvite(Kingdom kd, String name){
		broadcast(kd, YELLOW + name + " has declined your invitation", YELLOW + name + " heeft jullie uitnodiging afgewezen");
	}
	
	public static void broadcastStaffJoinedKingdom(Kingdom kd, String name){
		broadcast(kd, YELLOW + name + " has put himself in your kingdom", YELLOW + name + " heeft zichzelf in jouw kingdom gezet");
	}
	
	public static void broadcastQuitAlly(Kingdom breaker, Kingdom ally){
		broadcast(breaker, YELLOW + "Your kindom has broken the alliance with " + ally.getColoredName(), YELLOW + "Jouw kingdom heeft het bondgenootschap met " + ally.getColoredName() + " verbroken.");
		broadcast(ally, RED + "The kingdom " + breaker.getColoredName() + " has broken the alliance.", RED + "Het kingdom " + breaker.getColoredName() + " heeft het bondgenootschap met jullie verbroken.");
	}
	
	public static void broadcastFormedAlliance(Kingdom accepter, Kingdom inviter){
		broadcast(accepter, GREEN + "Your kingdom has accepted the alliance with " + inviter.getColoredName(), GREEN + "Je kingdom heeft het bondgenootschap met " + inviter.getColoredName() + GREEN + " geaccepteerd.");
		broadcast(inviter, GREEN + "The kingdom " + accepter.getColoredName() + GREEN + " has accepted your alliance invitation.", GREEN + "Het kingdom " + accepter.getColoredName() + GREEN + " heeft het bondgenootschap met je kingdom geaccepteerd.");
	}
	
	public static void invitedForAlliance(Kingdom inviter, Kingdom receiver){
		broadcast(inviter, BLUE + "Your kingdom has invited the kingdom " + receiver.getColoredName() + GREEN + " to become your ally.", BLUE + "Je kingdom heeft het kingdom " + receiver.getColoredName() + BLUE + " om bondgenoten te worden.");
		broadcast(receiver, BLUE + "The kingdom " + inviter.getColoredName() + GREEN + " has invited your kingdom to become their ally.", BLUE + "Het kingdom " + inviter.getColoredName() + BLUE + " heeft je kingdom uitgenodigd om bondgenoten te worden.");
	}
	
	public static void broadcastCancelledAllianceInvite(Kingdom inviter, Kingdom receiver){
		broadcast(inviter, YELLOW + "Your kingdom has cancelled the alliance invitation for " + receiver.getColoredName(), YELLOW + "Je kingdom heeft de uitnodiging voor " + receiver.getColoredName() + YELLOW + " ingetrokken.");
		broadcast(receiver, RED + "The kingdom " + inviter.getColoredName() + RED + " has cancelled the invitation to become allies.", RED + "Het kingdom " + inviter.getColoredName() + RED + " om bondgenoten te worden ingetrokken.");
	}
	
	private static void broadcast(Kingdom kd, String en, String nl){
		ArrayList<MemberData> members = kd.getMembers();
		for(MemberData data : members){
			Player player = Bukkit.getPlayer(data.getPlayerID());
			if(player != null){
				if(isNL(player))
					player.sendMessage(nl);
				else
					player.sendMessage(en);
			}
		}
		if(kd.getKing() instanceof Player){
			Player king = kd.getKing().getPlayer();
			if(isNL(king))
				king.sendMessage(nl);
			else
				king.sendMessage(en);
		}
	}
	
	private static String getMemberPermissions(){
		return " canInvite, isDiplomatic";
	}
	
	private static String getChatColors(){
		return " black,dark_blue,dark_green,dark_red,dark_aqua,dark_purple,gold,gray,dark_gray,blue,green,red,aqua,light_purple,yellow";
	}
	
	private static String getLocationString(Location location){
		return "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")[" + location.getWorld().getName() + "]";
	}
	
	public static boolean isNL(CommandSender sender){
		if(sender instanceof Player)
			return isNL((Player) sender);
		return true;
	}
	
	public static boolean isNL(Player player){
		return true;
	}
}
