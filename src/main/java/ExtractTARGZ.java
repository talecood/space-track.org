import java.io.*;

import java.util.zip.GZIPInputStream; // GZ dosyasýný açmak için
import org.apache.commons.compress.archivers.tar.TarArchiveEntry; // TAR dosyasýndaki yüklemeleri okur
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream; // TAR dosyasýný okumak için 
import org.apache.commons.io.IOUtils; // Byte dizilerini kullanýp taþýma iþlemi yapmak için.

public class ExtractTARGZ {  
    public static void main(String[] args) {
        // Tar ve GZ dosyasýný okuma için kullanacaðýmýz deðiþkenleri tanýmlýyoruz.
        TarArchiveInputStream tarInputStream = null;
        FileOutputStream outputFile = null;
        
        try {
            // GZ dosyasýný açmak için GZIPInputStream kullanýyoruz.
            GZIPInputStream gzipInputStream = new GZIPInputStream(
            									//Buraya indirdiðimiz tar.gz dosyanýnýn bilgisayardaki yolunu koyuyoruz.
                    new FileInputStream(new File("C:\\Users\\nedim göktuð tabak\\Desktop\\dwsample-tar-gz.tar.gz")));
            
            // TAR dosyasýný okumak için TarArchiveInputStream kullanýyoruz.
            tarInputStream = new TarArchiveInputStream(gzipInputStream);
            
            // TAR dosyasýndaki her bir belgeyi (dosya/klasör) okumak için döngü oluþturuyoruz.
            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                // Okunan Belgenin (dosya veya klasör) adýný alýyoruz.
                String individualFiles = entry.getName();
                
                // Çýkartýlacak dosyalar için hedef dizini belirliyoruz.
                //Aldýðýmýz dosyanýn ismini sonuna ekleyip (individualFiles) klasör içine dosyayý koyuyoruz.
                String outputPath = "C:\\Users\\nedim göktuð tabak\\Desktop\\EXAMPLE_TAR_FILE_TO_UNZIP\\" + individualFiles;
                File outputFileObj = new File(outputPath);

                // Eðer gelen dosya bir klasörse, bu klasörü oluþturuyoruz.
                if (entry.isDirectory()) {
                    outputFileObj.mkdirs();
                    continue; // Dizin için baþka iþlem yapmaya gerek yok, bir sonraki giriþe geçiyoruz.
                }

                // Üst klasörleri oluþturuyoruz, çünkü dosyayý kaydedeceðimiz klasör manuel olarak açýlmamýþ olabilir.
                new File(outputFileObj.getParent()).mkdirs();
                
                // Dosya boyutuna göre bir byte dizisi oluþturuyoruz.
                //Burada Performans iyileþtirmeleri yapýlabilir.
                byte[] content = new byte[(int) entry.getSize()];
                // Dosyayý byte dizisine okuyoruz.
                IOUtils.readFully(tarInputStream, content);
                
                // Dosyayý diske yazmak için bir FileOutputStream kullanýyoruz.
                outputFile = new FileOutputStream(outputFileObj);
                IOUtils.write(content, outputFile); // Aldýðýmýz Byte klasör hiyerarþisine göre istenen outputFile'ya koyuyoruz.
                outputFile.close();
                
                // TAR'ýn içinden hangi dosyanýn Unzip edildiðini ismiyle birlikte konsola yazdýrýyoruz.
                System.out.println("File extracted: " + individualFiles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Hafýza yönetimi için açýk kalan her þeyi kapatýyoruz.
            try {
                if (tarInputStream != null) {
                    tarInputStream.close(); // TAR dosyasýný kapatýyoruz.
                }
                if (outputFile != null) {
                    outputFile.close(); // Açýk dosya varsa kapatýyoruz.
                }
            } catch (IOException e) {
              //Hata yazdýrma
                e.printStackTrace();
            }
        }
    }
}
