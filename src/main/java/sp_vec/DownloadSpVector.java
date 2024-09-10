package sp_vec;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
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
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DownloadSpVector {

    private static final String BASE_URL = "https://www.space-track.org";
    private static final String AUTH_PATH = "/ajaxauth/login";
    private static final String USERNAME = "example@example.com";  // Username
    private static final String PASSWORD = "password";  // Password
    ////FolderUrl to download from spacetrack
    private static final String FOLDER_URL = "https://www.space-track.org/files/getFolder?folderID=7156&recursive=1"; 
    //Local Directory path to copy newest file and override others.
    private static final String LOCAL_DIR = "C:\\Users\\nedim göktuð tabak\\Desktop\\spacetrack\\ExtractTarLocal";
    //Download directory path to download from folder_url with newest tar.gz files.
    private static final String DOWNLOAD_DIR = "C:\\Users\\nedim göktuð tabak\\Desktop\\spacetrack\\ExtractTarDownload";

    public static void main(String[] args) {
        try {
            // Step 1: Login and download folder content
            loginAndDownloadFolder(BASE_URL, AUTH_PATH, USERNAME, PASSWORD, FOLDER_URL, LOCAL_DIR);

            // Step 2: List local files in the download directory
            Set<String> localFiles = listLocalFiles(LOCAL_DIR);

            // Step 3: Fetch file list from the downloaded folder
            Set<String> serverFiles = fetchTarGzFiles(LOCAL_DIR);

            // Step 4: Download new files that are not already in the local directory
            downloadNewFiles(localFiles, serverFiles, DOWNLOAD_DIR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loginAndDownloadFolder(String baseURL, String authPath, String username, String password,
                                              String folderURL, String downloadDirectory) {
        // CookieManager Setup
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

        try {
            // Perform Login
            URL url = new URL(baseURL + authPath);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            // Send login credentials
            String input = "identity=" + username + "&password=" + password;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            }

            // Read login response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }

            // Download ZIP folder
            URL folderUrl = new URL(folderURL);
            HttpURLConnection downloadConn = (HttpURLConnection) folderUrl.openConnection();
            downloadConn.setRequestMethod("GET");

            File zipFile = new File(downloadDirectory, "folder.zip");
            try (InputStream in = new BufferedInputStream(downloadConn.getInputStream());
                 FileOutputStream fos = new FileOutputStream(zipFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            // Extract the downloaded ZIP file
            unzip(zipFile.getAbsolutePath(), downloadDirectory);

            System.out.println("Folder Successfully Downloaded and Extracted: " + zipFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to extract ZIP file
    private static void unzip(String source, String destination) {
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

    // List files in a local directory
    public static Set<String> listLocalFiles(String directoryPath) {
        File folder = new File(directoryPath);
        Set<String> localFiles = new HashSet<>();
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                localFiles.add(file.getName());
            }
        }
        return localFiles;
    }

    // Fetch files with ".tar.gz" extension from a directory
    public static Set<String> fetchTarGzFiles(String directoryPath) {
        File folder = new File(directoryPath);
        Set<String> tarGzFiles = new HashSet<>();
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".tar.gz")) {
                    tarGzFiles.add(file.getName());
                }
            }
        }
        return tarGzFiles;
    }

    // Download files that are not present locally
    public static void downloadNewFiles(Set<String> localFiles, Set<String> serverFiles, String downloadDirectory) {
        for (String fileName : serverFiles) {
            if (!localFiles.contains(fileName)) {
                downloadFileFromServer(fileName, downloadDirectory);
            }
        }
    }

    // Download newest tar.gz from url. (New Added)
    private static void downloadFileFromServer(String fileName, String downloadDirectory) {
        String downloadURL = "https://www.space-track.org/files/download?fileName=" + fileName;
        try (InputStream in = new BufferedInputStream(new URL(downloadURL).openStream());
             FileOutputStream fos = new FileOutputStream(new File(downloadDirectory, fileName))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("Downloaded new file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
