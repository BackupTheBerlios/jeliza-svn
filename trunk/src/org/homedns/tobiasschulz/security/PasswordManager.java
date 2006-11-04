package org.homedns.tobiasschulz.security;

public class PasswordManager {
	private static int MIN_LENGTH = 10;

	public static int SECURITY_VERY_HIGH = 25;

	public static int SECURITY_HIGH = 20;

	public static int SECURITY_NORMAL = 10;

	public static int SECURITY_LOW = 5;

	protected static java.util.Random r = new java.util.Random();

	protected static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
			'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'1', '2', '3', '4', '5', '6', '7', '8', '9',
			};

	public static String getGeneratedPassword(int length) {
		MIN_LENGTH = length;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < MIN_LENGTH; i++) {
			sb.append(goodChar[r.nextInt(goodChar.length)]);
		}
		return sb.toString();
	}

	public static void main(String[] argv) {
	        System.out.println("Unsicher (5): "+
	        PasswordManager.getGeneratedPassword(PasswordManager.SECURITY_LOW));
	        System.out.println("Mittelsicher (10): "+
	        PasswordManager.getGeneratedPassword(PasswordManager.SECURITY_NORMAL));
	        System.out.println("Sehr sicher (20): "+
	        PasswordManager.getGeneratedPassword(PasswordManager.SECURITY_HIGH));
	        System.out.println("\"Unknackbar\": "+
	        PasswordManager.getGeneratedPassword(100));	
	}
}
