package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	/**
	 * 
	 * @param password
	 * @return
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
