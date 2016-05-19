package com.fs.insights.storm;

import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;

import java.util.Map;

public class FsiKafkaStringSink extends KafkaBolt<String, String> implements IFsiRichSink {
    public FsiKafkaStringSink() {
    }

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        String id = context.getThisComponentId();
        //
        String topicName = FsiConfigUtils.getConfStringValue(conf, id, "kafka", "topicName");
        //
        withTopicSelector(new DefaultTopicSelector(topicName));
        withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>());
        //
        super.prepare(conf, context, collector);
    }
}
