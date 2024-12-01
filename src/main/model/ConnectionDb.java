package main.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que guarda de manera estatica la conexion a la base de datos
 */
public class ConnectionDb {
	/**
	 * Ruta donde esta ubicada la base de datos
	 */
	private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/population";
	
	/**
	 * Conexion a la base de datos
	 */
	private static Connection connection;
	
	/**
	 * Getter que devuelve la conexion a la base de datos
	 * @return Conexion a la base de datos
	 */
	public static Connection getConnection() {
		return connection;
	}
	
	/**
	 * Metodo que crea una conexion a la base de datos
	 * @param user Usuario de la base de datos
	 * @param password Contraseña del usuario
	 * @return Si se ha podido crear la conexion o no
	 */
	public static boolean instantiateConnection(String user, String password) {
		try {
			connection = DriverManager.getConnection(DB_ROUTE, user, password);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Metodo que cierra la conexion a la base de datos
	 * @return Si se ha podido cerrar la conexion o no
	 */
	public static boolean closeConnection() {
		try {
			connection.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
