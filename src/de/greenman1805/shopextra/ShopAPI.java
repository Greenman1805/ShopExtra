package de.greenman1805.shopextra;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopAPI {

	public static void loadSections() {
		for (String key : Main.shopitems.getConfigurationSection("").getKeys(false)) {
			new Section(key);
		}

		for (Section s : Section.sections) {
			for (String key : Main.shopitems.getConfigurationSection(s.name).getKeys(false)) {
				Material type = Material.getMaterial(key);
				int price = Main.shopitems.getInt(s.name + "." + key + ".price");
				int xp = Main.shopitems.getInt(s.name + "." + key + ".xp");
				Item item = new Item(type, price, xp);
				s.items.add(item);
			}
		}
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