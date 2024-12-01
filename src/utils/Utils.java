package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase que contiene diversos metodos estaticos de utilidad general
 */
public class Utils {
	/**
	 * Metodo que devuelve el hash MD5 de una contraseña
	 * @param password La contraseña a la que aplicar el hash
	 * @return El hash de la contraseña
	 */
	public static String passwordHash(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
