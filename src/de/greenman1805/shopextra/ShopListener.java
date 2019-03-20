package de.greenman1805.shopextra;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

	public boolean isInventoryFull(Player p) {
		return p.getInventory().firstEmpty() == -1;
	}

	@EventHandler
	public void clickedOnItem(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			String title = e.getInventory().getTitle();

			if (title.contains("§9Shop")) {
				String sectionString = title.replace(" §9Shop", "").replace("§9", "");
				Section s = Section.getSection(sectionString);
				if (s != null) {
					int slot = e.getRawSlot();
					if (slot >= 0 && slot <= 53) {
						if (!e.getCurrentItem().getType().name().equalsIgnoreCase("AIR")) {
							Item item = s.getItem(e.getCurrentItem());
							int level = ShopAPI.getPlayerLevel(p);
							if (item != null) {

								// Verkaufen
								if (slot >= 9 && slot <= 26) {
									for (int i = 0; i <= 35; i++) {
										ItemStack current = p.getInventory().getItem(i);
										if (current != null) {
											if (current.isSimilar(item.item)) {
												double price = ShopAPI.getItemPrice(item, level);
												int xp = item.xp;
												int amount = 1;
												if (e.isRightClick()) {
													amount = current.getAmount();
													price *= current.getAmount();
													xp *= current.getAmount();
													p.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
												} else {
													p.getInventory().setItem(i, new ItemStack(current.getType(), current.getAmount() - 1, current.getDurability()));
												}
												ShopAPI.addPlayerXp(p.getUniqueId(), xp);
												String item_name = item.item.getType().toString();
												item_name = item_name.substring(0, 1).toUpperCase() + item_name.substring(1).toLowerCase();
												p.sendMessage(Main.prefix + "§aDu hast §f" + amount + " " + item_name + " §afür §f" + price + " §aShards verkauft.");
												Main.econ.depositPlayer(p, price);
												p.updateInventory();
												break;
											}
										}
										if (i == 35) {
											p.sendMessage(Main.prefix + "§4Du hast nichts mehr zu verkaufen.");
										}
									}
									if (level < ShopAPI.getPlayerLevel(p)) {
										p.sendMessage(Main.prefix + "§aDu hast Level §f" + (level + 1) + "§a erreicht!");
										ShopAPI.openShopInventory(p, s);
									}

									// Kaufen
								} else if (slot >= 27 && slot <= 53) {
									if (!isInventoryFull(p)) {
										double price = ShopAPI.getItemPrice(item, 30);
										ItemStack itemToGive = new ItemStack(item.item.getType(), 1, item.data);

										if (e.isRightClick()) {
											price = price * item.item.getMaxStackSize();
											itemToGive.setAmount(item.item.getMaxStackSize());
										}

										int account_after = (int) (Main.econ.getBalance(p) - price);
										if (account_after >= 0) {
											Main.econ.withdrawPlayer(p, price);
											String item_name = item.item.getType().toString();
											item_name = item_name.substring(0, 1).toUpperCase() + item_name.substring(1).toLowerCase();
											p.sendMessage(Main.prefix + "§aDu hast §f" + itemToGive.getAmount() + " " + item_name + " §afür §f" + price + " §aShards gekauft.");
											p.getInventory().addItem(itemToGive);
											p.updateInventory();
										} else {
											p.sendMessage(Main.prefix + "§4Du hast nicht genug Geld!");
										}

									} else {
										p.sendMessage(Main.prefix + "§4Dein Inventar ist voll.");
									}

								}
							}
						}
						e.setCancelled(true);
					}
				}
			}
		}
	}

}
