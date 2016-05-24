package com.fs.insights.storm.twitter;

import com.fs.insights.storm.FsiStormConfig;
import com.fs.insights.storm.IFsiStormSource;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.MultiScheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import twitter4j.Status;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FsiStormTwitterSource extends TwitterSampleSpout implements IFsiStormSource {
    public FsiStormTwitterSource(String componentId, Map conf) {
        super(FsiStormConfig.getTwitterConsumerKey(conf, componentId), FsiStormConfig.getTwitterConsumerSecret(conf, componentId),
                FsiStormConfig.getTwitterAccessToken(conf, componentId), FsiStormConfig.getTwitterAccessTokenSecret(conf, componentId),
                FsiStormConfig.getTwitterKeywords(conf, componentId));
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
    }

    public FsiStormTwitterSource(String consumerKey, String consumerSecret,
                                 String accessToken, String accessTokenSecret, String[] keyWords) {
        super(consumerKey, consumerSecret,
                accessToken, accessTokenSecret, keyWords);
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
    }

    /**/

    private static SpoutConfig createSpoutConfig(String componentId, Map conf) {
        String topicName = FsiStormConfig.getKafkaTopicName(conf, componentId);
        return createSpoutConfig(topicName);
    }

    private static SpoutConfig createSpoutConfig(String topicName) {
        // SpoutConfig Parameters
        // + hosts   Any implementation of the BrokerHosts interface, currently either ZkHosts or StaticHosts.
        // + topic   Name of the Kafka topic.
        // + zkroot  Root directory in Zookeeper where all topics and partition information is stored. By default, this is /brokers.
        // + id      Unique identifier for this spout.

        BrokerHosts hosts = new ZkHosts("localhost:2181");
//        String zkRoot = "/" + FsiStormConfig.getKafkaZkRoot();
        String zkRoot = "/" + topicName;
        String id = UUID.randomUUID().toString();
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, zkRoot, id);
        // spoutConfig.scheme =new StringMultiSchemeWithTopic();
        spoutConfig.scheme = new MultiScheme() {
            @Override
            public Iterable<List<Object>> deserialize(ByteBuffer byteBuffer) {
                List<Object> items = new Values(StringScheme.deserializeString(byteBuffer));
                return Collections.singletonList(items);
            }

            @Override
            public Fields getOutputFields() {
//                Fields fields = new Fields(FsiStormConfig.getOutputDataFieldName(conf, componentId));
                Fields fields = new Fields(FsiStormConfig.getOutputDataFieldName());
                return fields;
            }
        };
        return spoutConfig;
    }

    /**/

//    private Map mConf;
//    private String mComponentId;
//
//    @Override
//    public Map getConf() {
//        return mConf;
//    }
//
//    @Override
//    public String getComponentId() {
//        return mComponentId;
//    }

    /**/

    @Override
    public void nextTuple() {
        Status status = queue.poll();

        if (status == null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values(status.getText()));
        }
    }



}
