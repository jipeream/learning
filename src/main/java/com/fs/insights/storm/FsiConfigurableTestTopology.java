package com.fs.insights.storm;

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

public class FsiConfigurableTestTopology {

    // TODO Cambiar nombres de constantes
    public static final String FsiTestTopology_NAME = "FsiConfigurableTestTopology";
    public static final String FsiConfigurableSpout_ID = "FsiConfigurableSpout";
    public static final String FsiConfigurableSink_ID = "FsiConfigurableSink";

    public static void main(String[] args) throws Exception {
        System.out.println("CLASSPATH :");
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) classLoader).getURLs();
        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        //
        FsiConfigurableSpout fsiConfigurableSpout = new FsiConfigurableSpout();
        int numSpouts = 1;
        //
        FsiConfigurableSink fsiConfigurableSink = new FsiConfigurableSink();
        //
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        SpoutDeclarer fsiConfigurableSpoutDeclarer = topologyBuilder.setSpout(FsiConfigurableSpout_ID, fsiConfigurableSpout, numSpouts);
        BoltDeclarer fsiConfigurableSinkBoltDeclarer = topologyBuilder.setBolt(FsiConfigurableSink_ID, fsiConfigurableSink, 1).shuffleGrouping(FsiConfigurableSpout_ID);
        StormTopology stormTopology = topologyBuilder.createTopology();
//        TridentTopology topology = new TridentTopology();
//        Stream spoutStream = topology.newStream("kafka-stream", fsiConfigurableSpout);
        boolean debug = args.length == 0;
        //
        Config conf = new Config();
        FsiConfigUtils.readConfig(conf, "fsinsights.properties");
        conf.setDebug(true);
        conf.setNumWorkers(1);
        conf.setNumAckers(1);
        conf.setMaxTaskParallelism(1);
        //
        if (debug) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(FsiTestTopology_NAME, conf, stormTopology);
        } else {
            Map clusterConf = Utils.readStormConfig();
            StormSubmitter.submitTopologyWithProgressBar(FsiTestTopology_NAME, conf, stormTopology);
            Nimbus.Client client = NimbusClient.getConfiguredClient(clusterConf).getClient();
        }
    }
}
