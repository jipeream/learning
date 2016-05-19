package com.fs.insights.storm.generic;

import com.fs.insights.storm.FsiKafkaConfig;
import com.fs.insights.storm.IFsiComponent;
import com.fs.insights.storm.IFsiRichSink;
import com.fs.insights.storm.kafka.FsiKafkaStringSpout;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.lang.reflect.Constructor;
import java.util.Map;

public class FsiGenericSink extends BaseRichBolt implements IFsiComponent {
    public FsiGenericSink() {
    }
    /**/

    private IFsiRichSink mSink;

    /**/

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        String componentId = context.getThisComponentId();
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
        try {
            String className = FsiKafkaConfig.getConfStringValue(conf, componentId, "className");
            className = className == null ? FsiKafkaStringSpout.class.getCanonicalName() : className;
            Class<?> c1ass = Class.forName(className);
            //
            Constructor<?> constructor = c1ass.getConstructor(String.class, Map.class);
            mSink = (IFsiRichSink) constructor.newInstance(componentId, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSink != null) {
            mSink.prepare(conf, context, collector);
        }
    }

    @Override
    public void execute(Tuple input) {
        if (mSink != null) {
            mSink.execute(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        if (mSink != null) {
//            mSink.declareOutputFields(declarer);
//        }
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
