package com.fs.insights.storm.kafka;

import com.fs.insights.storm.FsiKafkaConfig;
import com.fs.insights.storm.IFsiRichSink;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.TupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;

import java.util.Map;
import java.util.Properties;

public class FsiKafkaStringSink extends KafkaBolt<String, String> implements IFsiRichSink {
    public FsiKafkaStringSink(String topicName) {
        super();
        //
        mTopicName = topicName;
        mInputDataFieldName = FsiKafkaConfig.getInputDataFieldName();
        //
        init();
    }

    public FsiKafkaStringSink(String componentId, Map conf) {
        super();
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
        mTopicName = FsiKafkaConfig.getKafkaTopicName(conf, componentId);
        mInputDataFieldName = FsiKafkaConfig.getInputDataFieldName(conf, componentId);
        //
        init();
    }

    private void init() {
        withTopicSelector(new DefaultTopicSelector(mTopicName));
        //
        withTupleToKafkaMapper(new TupleToKafkaMapper<String, String>() {
            @Override
            public String getKeyFromTuple(Tuple tuple) {
                return mTopicName;
            }

            @Override
            public String getMessageFromTuple(Tuple tuple) {
                return tuple.getStringByField(mInputDataFieldName);
            }
        });
        //
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("acks", "1");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        withProducerProperties(producerProperties);
    }

    /**/

//    private final Map mConf;
//    private final String mComponentId;
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

    private final String mTopicName;
    private final String mInputDataFieldName;

    public String getTopicName() {
        return mTopicName;
    }

    public String getInputDataFieldName() {
        return mInputDataFieldName;
    }
}
