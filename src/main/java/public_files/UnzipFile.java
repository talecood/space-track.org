package public_files;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class UnzipFile {

	public static void main(String[] args) {
		// Location of Zip File
		String source = "C:\\\\Users\\\\nedim göktug tabak\\\\Desktop\\\\git\\\\a.zip";
		// Location of Folder to Unzip
		String destination = "C:\\Users\\nedim göktuð tabak\\Desktop\\git\\Unzip";
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

}
