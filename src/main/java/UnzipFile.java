import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;

public class UnzipFile {
	
	public static void main(String[] args) {
		//Location of Zip File
		String source = "C:\\\\Users\\\\nedim göktuð tabak\\\\Desktop\\\\git\\\\a.zip";
		//Location of Folder to Unzip
	    String destination = "C:\\Users\\nedim göktuð tabak\\Desktop\\git\\Unzip";
	    //Zipped Folder's password(if it exist)
	    String password = "password";

	    try {
	         ZipFile zipFile = new ZipFile(source);
	         if (zipFile.isEncrypted()) {
	            zipFile.setPassword(password);
	         }
	         zipFile.extractAll(destination);
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
	}
	
}