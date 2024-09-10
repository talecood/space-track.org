package sp_vec;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
  nedim göktuğ tabak
  10 Eyl 2024
 */
public class UnzipVector {

	public static void unzip(String source,String destination) {
		
				// Zipped Folder's password(if it exist)
				String password = "password";
				char[] passwordCharArray = password.toCharArray();

				try {
					ZipFile zipFile = new ZipFile(source);
					if (zipFile.isEncrypted()) {
						zipFile.setPassword(passwordCharArray);
					}
					zipFile.extractAll(destination);
				} catch (ZipException e) {
					e.printStackTrace();
				}
	}
	
	public static void unzip(String source,String destination,String password) {
		
		// Zipped Folder's password(if it exist)
		char[] passwordCharArray = password.toCharArray();

		try {
			ZipFile zipFile = new ZipFile(source);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(passwordCharArray);
			}
			zipFile.extractAll(destination);
		} catch (ZipException e) {
			e.printStackTrace();
		}
}
}
