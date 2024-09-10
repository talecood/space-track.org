package public_files;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class DownloadTLE {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            String baseURL = "https://www.space-track.org";
            String authPath = "/ajaxauth/login";
            String userName = "USERNAME"; //Space-Track username
            String password = "PASSWORD"; //Space-Track password
            String query = "/basicspacedata/query/class/tle_latest/ORDINAL/1/EPOCH/%3Enow-30/orderby/NORAD_CAT_ID/format/3le";


            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            URL url = new URL(baseURL+authPath);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            String input = "identity="+userName+"&password="+password;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            url = new URL(baseURL + query);

            br = new BufferedReader(new InputStreamReader((url.openStream())));

            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            url = new URL(baseURL + "/ajaxauth/logout");
            br = new BufferedReader(new InputStreamReader((url.openStream())));
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}