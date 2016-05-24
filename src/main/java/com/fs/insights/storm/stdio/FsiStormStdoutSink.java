package com.fs.insights.storm.stdio;

import com.fs.insights.storm.FsiStormConfig;
import com.fs.insights.storm.IFsiStormSink;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;
import java.util.Properties;

public class FsiStormStdoutSink extends BaseRichBolt implements IFsiStormSink {
    public FsiStormStdoutSink(String topicName) {
        super();
    }

    public FsiStormStdoutSink(String componentId, Map conf) {
        super();
        //
//        mConf = conf;
//        mComponentId = componentId;
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

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    @Override
    public void execute(Tuple tuple) {
        System.out.println(tuple.getString(0));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

}
