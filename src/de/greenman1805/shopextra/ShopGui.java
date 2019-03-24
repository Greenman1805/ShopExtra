package de.greenman1805.shopextra;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopGui implements Listener {
	Player p;
	Inventory inv;
	Section s;

	public ShopGui(Player p, Section s) {
		if (p != null && s != null) {
			this.p = p;
			this.s = s;
			inv = Bukkit.getServer().createInventory(null, 54, "§9" + s.name + " §9Shop");
			if (s.items.size() > 18) {
				System.out.println(Main.prefix + "Zu viele Items in Section: " + s.name);
				return;
			}
			ItemStack sell_gap = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
			ShopAPI.setItemName(sell_gap, "§8▼ §4Verkaufen §8▼", null);

			for (int i = 0; i < 9; i++) {
				inv.setItem(i, sell_gap);
			}

			for (int i = 0; i < s.items.size(); i++) {
				ItemStack item = s.items.get(i).item.clone();
				ArrayList<String> lore_list = new ArrayList<String>();
				lore_list.add("§fLinksklick:");
				lore_list.add("§cEinzeln verkaufen");
				lore_list.add("§9Shards: §f" + ShopAPI.getItemPrice(s.items.get(i), ShopAPI.getPlayerLevel(p)));
				lore_list.add("§9XP: §f" + s.items.get(i).xp);
				lore_list.add("");
				lore_list.add("§fRechtsklick:");
				lore_list.add("§cStack verkaufen");
				lore_list.add("§9Shards: §f" + ShopAPI.getItemPrice(s.items.get(i), ShopAPI.getPlayerLevel(p)) * item.getMaxStackSize());
				lore_list.add("§9XP: §f" + s.items.get(i).xp * item.getMaxStackSize());
				ShopAPI.setItemName(item, item.getItemMeta().getDisplayName(), lore_list);
				inv.setItem(i + 9, item);
			}

			ItemStack buy_gap = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
			ShopAPI.setItemName(buy_gap, "§8▼ §aKaufen §8▼", null);

			for (int i = 27; i < 36; i++) {
				inv.setItem(i, buy_gap);
			}

			for (int i = 0; i < s.items.size(); i++) {
				ItemStack item = s.items.get(i).item.clone();
				ArrayList<String> lore_list = new ArrayList<String>();
				lore_list.add("§fLinksklick:");
				lore_list.add("§aEinzeln kaufen");
				lore_list.add("§9Shards: §f" + ShopAPI.getItemPrice(s.items.get(i), 30));
				lore_list.add("");
				lore_list.add("§fRechtsklick:");
				lore_list.add("§aStack kaufen");
				lore_list.add("§9Shards: §f" + ShopAPI.getItemPrice(s.items.get(i), 30) * item.getMaxStackSize());
				ShopAPI.setItemName(item, item.getItemMeta().getDisplayName(), lore_list);
				inv.setItem(i + 36, item);
			}
			
			Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
			p.openInventory(inv);
		}
	}

	private boolean isInventoryFull(Player p) {
		return p.getInventory().firstEmpty() == -1;
	}

	@EventHandler
	public void clickedOnItem(InventoryClickEvent e) {
		if (e.getInventory().equals(inv)) {
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
											p.getInventory().setItem(i, new ItemStack(current.getType(), current.getAmount() - 1));
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
								new ShopGui(p, s);
							}

							// Kaufen
						} else if (slot >= 27 && slot <= 53) {
							if (!isInventoryFull(p)) {
								double price = ShopAPI.getItemPrice(item, 30);
								ItemStack itemToGive = new ItemStack(item.item.getType(), 1);

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
