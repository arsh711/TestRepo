package utils;

import org.bouncycastle.util.encoders.Base64;
import org.openqa.selenium.io.Zip;
import java.io.*;

public class ZipUtils {

    public static void zipFiles(File location,String zipFileLocation) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(zipFileLocation);
        fileOutputStream.write(Base64.decode(Zip.zip(location)));
    }
}
