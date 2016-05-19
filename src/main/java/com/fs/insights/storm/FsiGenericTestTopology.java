package com.fs.insights.storm;

import com.fs.insights.storm.generic.FsiGenericSink;
import com.fs.insights.storm.generic.FsiGenericSpout;
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

public class FsiGenericTestTopology {

    // TODO Cambiar nombres de constantes
    public static final String FsiGenericTestTopology_NAME = "FsiGenericTestTopology";
    public static final String FsiGenericSpout_ID = "FsiGenericSpout";
    public static final String FsiGenericSink_ID = "FsiGenericSink";

    public static void main(String[] args) throws Exception {
        System.out.println("CLASSPATH :");
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) classLoader).getURLs();
        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        //
        FsiGenericSpout fsiGenericSpout = new FsiGenericSpout();
        int numSpouts = 1;
        //
        FsiGenericSink fsiGenericSink = new FsiGenericSink();
        //
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        SpoutDeclarer fsiGenericSpoutDeclarer = topologyBuilder.setSpout(FsiGenericSpout_ID, fsiGenericSpout, numSpouts);
        BoltDeclarer fsiGenericSinkBoltDeclarer = topologyBuilder.setBolt(FsiGenericSink_ID, fsiGenericSink, 1).shuffleGrouping(FsiGenericSpout_ID);
        StormTopology stormTopology = topologyBuilder.createTopology();
//        TridentTopology topology = new TridentTopology();
//        Stream spoutStream = topology.newStream("kafka-stream", fsiGenericSpout);
        //
        boolean debug = args.length == 0;
        //
        Config conf = new Config();
        conf.setNumWorkers(1);
        conf.setNumAckers(1);
        conf.setMaxTaskParallelism(1);
        //
        if (debug) {
            FsiKafkaConfig.readConfig(conf, "src/main/resources/FsiGenericTestTopology.properties");
            conf.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(FsiGenericTestTopology_NAME, conf, stormTopology);
//            Thread.sleep(60000);
//            cluster.shutdown();
        } else {
            FsiKafkaConfig.readConfig(conf, args[0]);
            Map clusterConf = Utils.readStormConfig();
            StormSubmitter.submitTopologyWithProgressBar(FsiGenericTestTopology_NAME, conf, stormTopology);
            Nimbus.Client client = NimbusClient.getConfiguredClient(clusterConf).getClient();
        }
    }
}
