package utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int BUFFER_SIZE = 4096;

    private static void zipFile(File file, ZipOutputStream zos) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = bufferedInputStream.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
        }
        zos.closeEntry();
    }
    public static void zip(String fileLocation, String destZipFile) throws IOException {
        File file = new File(fileLocation);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
        if (file.isDirectory()) {
            zipDirectory(file, file.getName(), zos);
        } else {
            zos.putNextEntry(new ZipEntry(file.getName()));
            zipFile(file, zos);
        }
        zos.flush();
        zos.close();
    }
    private static void zipDirectory(File folder, String parentFolder,
                                     ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            zipFile(file,zos);
        }
    }
}
