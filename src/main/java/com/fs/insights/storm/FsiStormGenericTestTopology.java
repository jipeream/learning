package com.fs.insights.storm;

import com.fs.insights.storm.generic.FsiStormGenericSink;
import com.fs.insights.storm.generic.FsiStormGenericSource;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.Nimbus;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.SpoutDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class FsiStormGenericTestTopology {

    // TODO Cambiar nombres de constantes
    public static final String fsiStormGenericTestTopology_NAME = "fsiStormGenericTestTopology";
    public static final String fsiStormGenericTestSource_ID = "fsiStormGenericTestSource";
    public static final String fsiStormGenericTestSink_ID = "fsiStormGenericTestSink";

    public static void main(String[] args) throws Exception {
        System.out.println("CLASSPATH :");
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) classLoader).getURLs();
        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        //
        FsiStormGenericSource fsiStormGenericTestSource = new FsiStormGenericSource();
        int numSpouts = 1;
        //
        FsiStormGenericSink fsiStormGenericTestSink = new FsiStormGenericSink();
        //
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        SpoutDeclarer FsiStormGenericSourceDeclarer = topologyBuilder.setSpout(fsiStormGenericTestSource_ID, fsiStormGenericTestSource, numSpouts);
        BoltDeclarer FsiStormGenericSinkBoltDeclarer = topologyBuilder.setBolt(fsiStormGenericTestSink_ID, fsiStormGenericTestSink, 1).shuffleGrouping(fsiStormGenericTestSource_ID);
        StormTopology stormTopology = topologyBuilder.createTopology();
//        TridentTopology topology = new TridentTopology();
//        Stream spoutStream = topology.newStream("kafka-stream", FsiStormGenericSource);
        //
        boolean debug = args.length == 0;
        //
        Config conf = new Config();
        conf.setNumWorkers(1);
        conf.setNumAckers(1);
        conf.setMaxTaskParallelism(1);
        //
        if (debug) {
            FsiStormConfig.readConfig(conf, "src/main/resources/" + fsiStormGenericTestTopology_NAME + ".properties");
            conf.setDebug(true);
            //
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(fsiStormGenericTestTopology_NAME, conf, stormTopology);
//            Thread.sleep(60000);
//            cluster.shutdown();
        } else {
            FsiStormConfig.readConfig(conf, args[0]);
            Map clusterConf = Utils.readStormConfig();
            StormSubmitter.submitTopologyWithProgressBar(fsiStormGenericTestTopology_NAME, conf, stormTopology);
            Nimbus.Client client = NimbusClient.getConfiguredClient(clusterConf).getClient();
        }
    }
}
