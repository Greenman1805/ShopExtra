package de.greenman1805.shopextra;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopAPI {

	public static void loadSections() {
		for (String key : Main.shopitems.getConfigurationSection("").getKeys(false)) {
			new Section(key);
		}

		for (Section s : Section.sections) {
			for (String key : Main.shopitems.getConfigurationSection(s.name).getKeys(false)) {
				String[] split = key.split("-");
				Material type = Material.getMaterial(split[0]);
				int data = Integer.parseInt(split[1]);
				int price = Main.shopitems.getInt(s.name + "." + key + ".price");
				int xp = Main.shopitems.getInt(s.name + "." + key + ".xp");
				Item item = new Item(type, data, price, xp);
				s.items.add(item);
			}
		}
	}

	public static void openShopInventory(Player p, Section s) {
		Inventory inv = Bukkit.getServer().createInventory(null, 54, "§9" + s.name + " §9Shop");
		if (s.items.size() > 18) {
			System.out.println(Main.prefix + "Zu viele Items in Section: " + s.name);
			return;
		}
		ItemStack sell_gap = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		setItemName(sell_gap, "§8▼ §4Verkaufen §8▼", null);

		for (int i = 0; i < 9; i++) {
			inv.setItem(i, sell_gap);
		}

		for (int i = 0; i < s.items.size(); i++) {
			ItemStack item = s.items.get(i).item.clone();
			ArrayList<String> lore_list = new ArrayList<String>();
			lore_list.add("§fLinksklick:");
			lore_list.add("§cEinzeln verkaufen");
			lore_list.add("§9Shards: §f" + getItemPrice(s.items.get(i), getPlayerLevel(p)));
			lore_list.add("§9XP: §f" + s.items.get(i).xp);
			lore_list.add("");
			lore_list.add("§fRechtsklick:");
			lore_list.add("§cStack verkaufen");
			lore_list.add("§9Shards: §f" + getItemPrice(s.items.get(i), getPlayerLevel(p)) * item.getMaxStackSize());
			lore_list.add("§9XP: §f" + s.items.get(i).xp * item.getMaxStackSize());
			setItemName(item, item.getItemMeta().getDisplayName(), lore_list);
			inv.setItem(i + 9, item);
		}

		ItemStack buy_gap = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
		setItemName(buy_gap, "§8▼ §aKaufen §8▼", null);

		for (int i = 27; i < 36; i++) {
			inv.setItem(i, buy_gap);
		}

		for (int i = 0; i < s.items.size(); i++) {
			ItemStack item = s.items.get(i).item.clone();
			ArrayList<String> lore_list = new ArrayList<String>();
			lore_list.add("§fLinksklick:");
			lore_list.add("§aEinzeln kaufen");
			lore_list.add("§9Shards: §f" + getItemPrice(s.items.get(i), 30));
			lore_list.add("");
			lore_list.add("§fRechtsklick:");
			lore_list.add("§aStack kaufen");
			lore_list.add("§9Shards: §f" + getItemPrice(s.items.get(i), 30) * item.getMaxStackSize());
			setItemName(item, item.getItemMeta().getDisplayName(), lore_list);
			inv.setItem(i + 36, item);
		}
		p.openInventory(inv);
	}

	public static void setItemName(ItemStack item, String name, ArrayList<String> lore_list) {
		ItemMeta meta;
		meta = item.getItemMeta();
		meta.setLore(lore_list);
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	public static void addPlayerXp(UUID uuid, int add) {
		int xp = getPlayerXp(uuid) + add;
		setXp(uuid, xp);
	}

	private static boolean hasAccount(UUID uuid) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT xp FROM shopstats WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getPlayerXp(UUID uuid) {
		if (!hasAccount(uuid)) {
			createAccount(uuid);
		}
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT xp FROM shopstats WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("xp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void createAccount(UUID uuid) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO shopstats (UUID, xp) VALUES (?,?)");
			ps.setString(1, uuid.toString());
			ps.setString(2, 0 + "");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setXp(UUID uuid, int xp) {
		if (!hasAccount(uuid)) {
			createAccount(uuid);
		}
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE shopstats SET xp = ? WHERE UUID = ?");
			ps.setString(1, xp + "");
			ps.setString(2, uuid.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getXpForLevel(int level) {
		return Main.plugin.getConfig().getInt("Level." + level);
	}

	public static int getPlayerLevel(Player p) {
		int xp = getPlayerXp(p.getUniqueId());

		for (int i = 0; i <= 30; i++) {
			if (xp < getXpForLevel(i + 1)) {
				return i;
			}
		}
		return 0;
	}

	private static double round(double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

	public static double getItemPrice(Item item, int level) {
		double itemprice = item.price;
		level--;
		double price = ((double) itemprice) + (((double) itemprice) * (((double) level) / 10));
		price = round(price, 1);
		return price;
	}

}