package es.jipeream.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class JavaUtils {
    public static Properties loadProperties(File propertiesFile) throws Exception {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);
        properties.load(fileInputStream);
        fileInputStream.close();
        return properties;
    }

    public static Properties loadProperties(String filename) throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = JavaUtils.class.getClassLoader().getResourceAsStream(filename);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }
}
