package com.fs.insights.storm.kafka;

import com.fs.insights.storm.FsiKafkaConfig;
import com.fs.insights.storm.IFsiRichSpout;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.MultiScheme;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FsiKafkaStringSpout extends KafkaSpout implements IFsiRichSpout {
    public FsiKafkaStringSpout(String componentId, Map conf) {
        super(createSpoutConfig(componentId, conf));
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
    }

    private static SpoutConfig createSpoutConfig(String componentId, Map conf) {
        // SpoutConfig Parameters
        // + hosts   Any implementation of the BrokerHosts interface, currently either ZkHosts or StaticHosts.
        // + topic   Name of the Kafka topic.
        // + zkroot  Root directory in Zookeeper where all topics and partition information is stored. By default, this is /brokers.
        // + id      Unique identifier for this spout.

        BrokerHosts hosts = new ZkHosts("localhost:2181");
        String topicName = FsiKafkaConfig.getKafkaTopicName(conf, componentId);
//        String zkRoot = "/" + FsiKafkaConfig.getKafkaZkRoot();
        String zkRoot = "/" + topicName;
        String id = UUID.randomUUID().toString();
        SpoutConfig spoutConfig = new SpoutConfig(hosts , topicName, zkRoot, id);
        // spoutConfig.scheme =new StringMultiSchemeWithTopic();
        spoutConfig.scheme = new MultiScheme() {
            @Override
            public Iterable<List<Object>> deserialize(ByteBuffer byteBuffer) {
                List<Object> items = new Values(StringScheme.deserializeString(byteBuffer));
                return Collections.singletonList(items);
            }

            @Override
            public Fields getOutputFields() {
                Fields fields = new Fields(FsiKafkaConfig.getOutputDataFieldName(conf, componentId));
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

}
