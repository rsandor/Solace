package solace.util;
import java.security.*;

/**
 * Utility class for hashing strings.
 * @author Ryan Sandor Richards
 */
public class Digest {
	/**
	 * Calculates the SHA-256 hex digest for a given string.
	 * @param s String to hash.
	 * @return The hex digest of the hashed string.
	 */
	public static String sha256(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(s.getBytes());
		
			StringBuffer hex = new StringBuffer();
			String digits = "0123456789abcdef";
		
			for (byte b : bytes) { 
				hex.append(digits.charAt((b & 0xF0) >> 4));
				hex.append(digits.charAt(b & 0x0F));
			}
			return hex.toString();
		}
		catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm!");
			return null;
		}
	}
}