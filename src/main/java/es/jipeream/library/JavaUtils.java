package es.jipeream.library;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class JavaUtils {
    public static Properties loadProperties(File propertiesFile) throws Exception {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);
        properties.load(fileInputStream);
        fileInputStream.close();
        return properties;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }
}
