/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.fs.fsnews.input.endpoints;

import com.fs.fsnews.config.KafkaConfig;
import com.fs.fsnews.input.endpoints.config.FsnGtrendsConfig;
import es.jipeream.library.JavaUtils;
import java.util.ArrayList;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import org.apache.log4j.Logger;

public class FsnInputGtrendsEndpointMain {
    static Logger logger = Logger.getLogger((Class)FsnInputGtrendsEndpointMain.class);

    public static void main(String[] args) throws Exception {
        String configDir = JavaUtils.getConfigDir(args);
        Properties kafkaProperties = KafkaConfig.loadProperties(configDir);
        Producer producer = null;
        ArrayList entryUrlList = new ArrayList();
        Properties fsnGtrendsProperties = FsnGtrendsConfig.loadProperties(configDir);
        do {
            if (producer != null) {
                producer.close();
                producer = null;
            }
            Thread.sleep(Long.parseLong(fsnGtrendsProperties.getProperty("fsn.gtrends.loopIntervalMs", "10000")));
        } while (true);
    }
}

