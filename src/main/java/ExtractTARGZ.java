import java.io.*;

import java.util.zip.GZIPInputStream; // GZ dosyas�n� a�mak i�in
import org.apache.commons.compress.archivers.tar.TarArchiveEntry; // TAR dosyas�ndaki y�klemeleri okur
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream; // TAR dosyas�n� okumak i�in 
import org.apache.commons.io.IOUtils; // Byte dizilerini kullan�p ta��ma i�lemi yapmak i�in.

public class ExtractTARGZ {  
    public static void main(String[] args) {
        // Tar ve GZ dosyas�n� okuma i�in kullanaca��m�z de�i�kenleri tan�ml�yoruz.
        TarArchiveInputStream tarInputStream = null;
        FileOutputStream outputFile = null;
        
        try {
            // GZ dosyas�n� a�mak i�in GZIPInputStream kullan�yoruz.
            GZIPInputStream gzipInputStream = new GZIPInputStream(
            									//Buraya indirdi�imiz tar.gz dosyan�n�n bilgisayardaki yolunu koyuyoruz.
                    new FileInputStream(new File("C:\\Users\\nedim g�ktu� tabak\\Desktop\\dwsample-tar-gz.tar.gz")));
            
            // TAR dosyas�n� okumak i�in TarArchiveInputStream kullan�yoruz.
            tarInputStream = new TarArchiveInputStream(gzipInputStream);
            
            // TAR dosyas�ndaki her bir belgeyi (dosya/klas�r) okumak i�in d�ng� olu�turuyoruz.
            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                // Okunan Belgenin (dosya veya klas�r) ad�n� al�yoruz.
                String individualFiles = entry.getName();
                
                // ��kart�lacak dosyalar i�in hedef dizini belirliyoruz.
                //Ald���m�z dosyan�n ismini sonuna ekleyip (individualFiles) klas�r i�ine dosyay� koyuyoruz.
                String outputPath = "C:\\Users\\nedim g�ktu� tabak\\Desktop\\EXAMPLE_TAR_FILE_TO_UNZIP\\" + individualFiles;
                File outputFileObj = new File(outputPath);

                // E�er gelen dosya bir klas�rse, bu klas�r� olu�turuyoruz.
                if (entry.isDirectory()) {
                    outputFileObj.mkdirs();
                    continue; // Dizin i�in ba�ka i�lem yapmaya gerek yok, bir sonraki giri�e ge�iyoruz.
                }

                // �st klas�rleri olu�turuyoruz, ��nk� dosyay� kaydedece�imiz klas�r manuel olarak a��lmam�� olabilir.
                new File(outputFileObj.getParent()).mkdirs();
                
                // Dosya boyutuna g�re bir byte dizisi olu�turuyoruz.
                //Burada Performans iyile�tirmeleri yap�labilir.
                byte[] content = new byte[(int) entry.getSize()];
                // Dosyay� byte dizisine okuyoruz.
                IOUtils.readFully(tarInputStream, content);
                
                // Dosyay� diske yazmak i�in bir FileOutputStream kullan�yoruz.
                outputFile = new FileOutputStream(outputFileObj);
                IOUtils.write(content, outputFile); // Ald���m�z Byte klas�r hiyerar�isine g�re istenen outputFile'ya koyuyoruz.
                outputFile.close();
                
                // TAR'�n i�inden hangi dosyan�n Unzip edildi�ini ismiyle birlikte konsola yazd�r�yoruz.
                System.out.println("File extracted: " + individualFiles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Haf�za y�netimi i�in a��k kalan her �eyi kapat�yoruz.
            try {
                if (tarInputStream != null) {
                    tarInputStream.close(); // TAR dosyas�n� kapat�yoruz.
                }
                if (outputFile != null) {
                    outputFile.close(); // A��k dosya varsa kapat�yoruz.
                }
            } catch (IOException e) {
              //Hata yazd�rma
                e.printStackTrace();
            }
        }
    }
}
