package de.greenman1805.shopextra;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	public static Economy econ = null;
	public static String prefix = "§f[§9Shop§f] ";
	public static String HomeDir = System.getProperty("user.home");
	public static Main plugin;

	public static String host;
	public static String port;
	public static String user;
	public static String password;
	public static String database;

	public static YamlConfiguration shopitems;

	@Override
	public void onEnable() {
		plugin = this;
		setupEconomy();
		setupConfig();
		getValues();
		getFiles();

		checkDatabase();

		getServer().getPluginManager().registerEvents(new ShopListener(), this);
		getCommand("shop").setExecutor(new ShopCommands());
		ShopAPI.loadSections();
	}

	@Override
	public void onDisable() {
		MySQL.closeConnection();
	}

	private void checkDatabase() {
		if (!MySQL.openConnection()) {
			this.setEnabled(false);
			System.out.println("MySQL Verbindung konnte nicht hergestellt werden!");
			return;
		}
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopstats (UUID VARCHAR(100), xp INT);");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getFiles() {
		shopitems = YamlConfiguration.loadConfiguration(new File(HomeDir + "//shopitems.yml"));
	}

	private void setupConfig() {
		reloadConfig();
		getConfig().addDefault("MySQL.host", "localhost");
		getConfig().addDefault("MySQL.port", "3306");
		getConfig().addDefault("MySQL.user", "user");
		getConfig().addDefault("MySQL.password", "user");
		getConfig().addDefault("MySQL.database", "database");
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void getValues() {
		host = getConfig().getString("MySQL.host");
		port = getConfig().getString("MySQL.port");
		user = getConfig().getString("MySQL.user");
		password = getConfig().getString("MySQL.password");
		database = getConfig().getString("MySQL.database");
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

}