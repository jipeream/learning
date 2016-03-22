/*
 * Decompiled with CFR 0_115.
 */
package com.fs.fsnews.config;

import es.jipeream.library.JavaUtils;
import java.util.Properties;

public class FsnMalletConfig {
    public static Properties loadProperties(String configDirName) throws Exception {
        Properties properties = JavaUtils.loadProperties("mallet/" + configDirName + "/fsn.mallet.properties");
        return properties;
    }
}

