package com.fs.insights.storm.generic;

import com.fs.insights.storm.FsiStormConfig;
import com.fs.insights.storm.IFsiStormComponent;
import com.fs.insights.storm.IFsiStormSink;
import com.fs.insights.storm.kafka.FsiStormKafkaStringSource;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.lang.reflect.Constructor;
import java.util.Map;

public class FsiStormGenericSink extends BaseRichBolt implements IFsiStormComponent {
    public FsiStormGenericSink() {
    }
    /**/

    private IFsiStormSink mSink;

    /**/

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        String componentId = context.getThisComponentId();
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
        try {
            String className = FsiStormConfig.getConfStringValue(conf, componentId, "className");
            className = className == null ? FsiStormKafkaStringSource.class.getCanonicalName() : className;
            Class<?> c1ass = Class.forName(className);
            //
            Constructor<?> constructor = c1ass.getConstructor(String.class, Map.class);
            mSink = (IFsiStormSink) constructor.newInstance(componentId, conf);
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
