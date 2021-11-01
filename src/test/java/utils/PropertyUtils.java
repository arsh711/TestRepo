package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {

    public static String getProperty(String propertyName){
        Properties properties = new Properties();
        String path = System.getProperty("user.dir") + "/src/test/resources/configuration/config.properties";
        try {
            FileInputStream inputStream = new FileInputStream(path);
            properties.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not Found "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(propertyName);
    }
}
