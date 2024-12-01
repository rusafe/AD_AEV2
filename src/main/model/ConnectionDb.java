package main.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDb {
	private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/population";
	
	private static Connection connection;
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static boolean instantiateConnection(String user, String password) {
		try {
			connection = DriverManager.getConnection(DB_ROUTE, user, password);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public static boolean closeConnection() {
		try {
			connection.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
