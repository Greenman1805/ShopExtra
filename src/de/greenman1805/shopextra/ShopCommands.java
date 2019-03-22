package de.greenman1805.shopextra;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("shop")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("stats")) {
						int playerlevel = ShopAPI.getPlayerLevel(p);
						p.sendMessage("§fDeine Shop Stats:");
						p.sendMessage("§9Level: §f" + playerlevel);
						p.sendMessage("§9XP: §f" + ShopAPI.getPlayerXp(p.getUniqueId()) + "§9/§f" + ShopAPI.getXpForLevel(playerlevel + 1));
					}
				}

				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("open")) {
						Section s = Section.getSection(args[1]);
						if (s != null) {
							new ShopGui(p, s);

						} else {
							p.sendMessage(Main.prefix + "§4Section nicht gefunden!");
						}
					}

				}

			}
		}
		return false;
	}

}
