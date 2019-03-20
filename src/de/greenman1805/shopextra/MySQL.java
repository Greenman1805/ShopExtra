package de.greenman1805.shopextra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;

public class MySQL {
	private static Connection con;

	public static void startRefreshingConnection() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			@Override
			public void run() {
				MySQL.checkConnection();
				try {
					PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT version()");
					ps.executeQuery();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}, 20 * 3600, 20 * 3600);

	}

	public static boolean openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + Main.host + ":" + Main.port + "/" + Main.database, Main.user, Main.password);
			startRefreshingConnection();
			return true;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static void checkConnection() {
		try {
			if (con.isClosed() || con == null) {
				MySQL.openConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		con = null;
	}

	public static Connection getConnection() {
		return con;
	}

}