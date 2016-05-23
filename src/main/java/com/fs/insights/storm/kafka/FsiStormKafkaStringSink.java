package com.fs.insights.storm.kafka;

import com.fs.insights.storm.FsiStormConfig;
import com.fs.insights.storm.IFsiStormSink;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;

import java.util.Map;
import java.util.Properties;

public class FsiStormKafkaStringSink extends KafkaBolt<String, String> implements IFsiStormSink {
    public FsiStormKafkaStringSink(String topicName) {
        super();
        //
        mTopicName = topicName;
        mInputDataFieldName = FsiStormConfig.getInputDataFieldName();
        //
        init();
    }

    public FsiStormKafkaStringSink(String componentId, Map conf) {
        super();
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
        mTopicName = FsiStormConfig.getKafkaTopicName(conf, componentId);
//        mInputDataFieldName = FsiStormConfig.getInputDataFieldName(conf, componentId);
        mInputDataFieldName = FsiStormConfig.getInputDataFieldName();
        //
        init();
    }

    private void init() {
        withTopicSelector(new DefaultTopicSelector(mTopicName));
        //
//        withTupleToKafkaMapper(new TupleToKafkaMapper<String, String>() {
//            @Override
//            public String getKeyFromTuple(Tuple tuple) {
//                return null;
//            }
//
//            @Override
//            public String getMessageFromTuple(Tuple tuple) {
//                return tuple.getStringByField(mInputDataFieldName);
//            }
//        });
        //
        withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>(null, mInputDataFieldName));
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
