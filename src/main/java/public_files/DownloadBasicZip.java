package public_files;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadBasicZip {

	final static byte[] EmptyZip={80,75,05,06,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};
	public static void createEmptyZip(String path){
	    try{
	        FileOutputStream fos=new FileOutputStream(new File(path));
	        fos.write(EmptyZip, 0, 22);
	        fos.flush();
	        fos.close();
	    }catch (FileNotFoundException e){
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	}
    public static void main(String[] args) {
    	
        try {
        	//Base URL of website
            String baseURL = "https://www.space-track.org";
            //AuthPath for Login Page
            String authPath = "/ajaxauth/login";
            //Username and Password (Space-Track.org)
            String username = "email";  
            String password = "password";  // 
            //URL of Zip File You Want To Download
            String downloadURL = "https://www.space-track.org/publicfiles/query/class/download?name=SpaceX_Ephemeris_552_SpaceX_2024-08-12UTC13:21:02_01.zip";
            //Output File (.zip) For Download Path
//            createEmptyZip("C:\\Users\\nedim göktuð tabak\\Desktop\\git");
            String path = "C:\\\\Users\\\\nedim göktuð tabak\\\\Desktop\\\\git\\\\a.zip";
            createEmptyZip(path);
			File outFile = new File(path);  // Dosyanýn kaydedileceði yolu belirtin
           
            
            
            
            // CookieManager Setting
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            // Log In Operation For space-track
            URL url = new URL(baseURL + authPath);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            //Same format as space-track wants (HTTP POST request ('identity=your_username&password=your_password'))
            String input = "identity=" + username + "&password=" + password;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            }

            // Reading the Login Answer with BufferedReader
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                //To end of line
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }

            // Downloading the Zip File due to URL.
            url = new URL(downloadURL);
            HttpURLConnection downloadConn = (HttpURLConnection) url.openConnection();
            downloadConn.setRequestMethod("GET");

            try (InputStream in = new BufferedInputStream(downloadConn.getInputStream());
                 FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("File Succesfully Downloaded: " + outFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
