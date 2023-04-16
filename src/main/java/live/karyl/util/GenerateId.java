package live.karyl.util;

public class GenerateId {

	public static String generateId(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = (int)(Math.random() * chars.length());
			sb.append(chars.charAt(index));
		}

		return sb.toString();
	}
}
