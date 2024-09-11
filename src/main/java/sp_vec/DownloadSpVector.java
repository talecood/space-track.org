package sp_vec;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileInputStream;
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
	private static final String USERNAME = "example@example.com"; // Username
	private static final String PASSWORD = "password"; // Password
	private static final String FOLDER_URL = "https://www.space-track.org/files/getFolder?folderID=7156&recursive=1";
	private static final String LOCAL_DIR = "C:\\Users\\nedim göktuð tabak\\Desktop\\spacetrack\\ExtractTarLocal";
	private static final String DOWNLOAD_DIR = "C:\\Users\\nedim göktuð tabak\\Desktop\\spacetrack\\ExtractTarDownload";

	public static void main(String[] args) {
		try {
			// Step 1: Login and download folder content
			loginAndDownloadFolder(BASE_URL, AUTH_PATH, USERNAME, PASSWORD, FOLDER_URL, DOWNLOAD_DIR);

			// Step 2: List local files in the local directory
			Set<String> localFiles = listLocalFiles(LOCAL_DIR);

			// Step 3: Fetch the latest zip file from the downloaded folder and extract it
			Set<String> serverFiles = fetchTarGzFiles(DOWNLOAD_DIR);

			// Step 4: Download new files by extracting the latest zip and copying new files
			for (String fileName : serverFiles) {
				downloadAndExtractZip(fileName, DOWNLOAD_DIR, LOCAL_DIR, localFiles);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loginAndDownloadFolder(String baseURL, String authPath, String username, String password,
			String folderURL, String downloadDirectory) {
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

	// Method to download and extract ZIP file, then copy new files
	private static void downloadAndExtractZip(String zipFileName, String downloadDirectory, String localDirectory,
			Set<String> localFiles) {
		String zipFilePath = downloadDirectory + File.separator + zipFileName;
		try {
			// ZIP dosyasýný indir
			String downloadURL = FOLDER_URL;
			try (InputStream in = new BufferedInputStream(new URL(downloadURL).openStream());
					FileOutputStream fos = new FileOutputStream(zipFilePath)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
				System.out.println("Downloaded ZIP file: " + zipFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Extract Zip File and save files in a temp directory.
			String extractDirectory = downloadDirectory + File.separator + "extracted";
			unzip(zipFilePath, extractDirectory);

			// List and Compare the files which comes from extracting zip
			File extractedFolder = new File(extractDirectory);
			for (File extractedFile : extractedFolder.listFiles()) {
				String extractedFileName = extractedFile.getName();
				if (!localFiles.contains(extractedFileName)) {
					// If file does not exist in localDirectory, copy it
					File destFile = new File(localDirectory + File.separator + extractedFileName);
					copyFile(extractedFile, destFile);
					System.out.println("Copied new file: " + extractedFileName);
				}
			}

			// Free Temporary folder
			deleteDirectory(extractedFolder);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to copy a file from source to destination
	private static void copyFile(File source, File destination) {
		try (InputStream in = new BufferedInputStream(new FileInputStream(source));
				OutputStream out = new FileOutputStream(destination)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to delete a directory and its contents
	private static void deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
			directory.delete();
		}
	}
}
