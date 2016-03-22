/*
 * Decompiled with CFR 0_115.
 */
package es.jipeream.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class JavaUtils {
    public static String getConfigDir(String[] args) throws IllegalArgumentException {
        String configDir = getArgsValue(args, "configDir", null);
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                String arg = args[i];
                if (!arg.startsWith("-configDir=")) continue;
                configDir = arg.substring("-configDir=".length());
                return configDir;
            }
        }
        if (configDir == null) {
            throw new IllegalArgumentException("USO: -configDir=...");
        } else {
            return configDir;
        }
    }

    public static String getArgsValue(String[] args, String name, String defaultValue) throws IllegalArgumentException {
        String prefix = "-" + name + "=";
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                String arg = args[i];
                if (!arg.startsWith(prefix)) continue;
                String value = arg.substring(prefix.length());
                return value;
            }
        }
        if (defaultValue ==null) {
            throw new IllegalArgumentException("USO: -configDir=...");
        } else {
            return defaultValue;
        }
    }

    public static Properties loadProperties(File propertiesFile) throws Exception {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);
        properties.load(fileInputStream);
        fileInputStream.close();
        return properties;
    }

    public static Properties loadProperties(String propertiesFilename) throws Exception {
        File propertiesFile = new File(propertiesFilename);
        Properties properties = JavaUtils.loadProperties(propertiesFile);
        return properties;
    }

    public static Properties loadPropertiesFromResources(String propertiesFilename) throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = JavaUtils.class.getClassLoader().getResourceAsStream(propertiesFilename);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }
}

