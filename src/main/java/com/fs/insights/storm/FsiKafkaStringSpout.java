package com.fs.insights.storm;

import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;

import java.util.Map;
import java.util.UUID;

public class FsiKafkaStringSpout extends KafkaSpout implements IFsiRichSpout {
    public FsiKafkaStringSpout(String id, Map conf) {
        super(new SpoutConfig(new ZkHosts("localhost:2181"), FsiConfigUtils.getKafkaTopicName(conf, id), "/" + FsiConfigUtils.getKafkaTopicName(conf, id), UUID.randomUUID().toString()));
    }

    // SpoutConfig Parameters
//   + hosts   Any implementation of the BrokerHosts interface, currently either ZkHosts or StaticHosts.
//   + topic   Name of the Kafka topic.
//   + zkroot  Root directory in Zookeeper where all topics and partition information is stored. By default, this is /brokers.
//   + id      Unique identifier for this spout.

}
