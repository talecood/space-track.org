import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;

public class UnzipFile {
	
	public static void main(String[] args) {
		//Location of Zip File
		String source = "src/main/resources/SpaceX_Ephemeris_552_SpaceX_2024-08-12UTC05_21_02_13.zip";
		//Location of Folder to Unzip
	    String destination = "src/main/resources/unzipTest";
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