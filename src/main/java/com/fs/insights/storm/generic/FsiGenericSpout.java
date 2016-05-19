package com.fs.insights.storm.generic;

import com.fs.insights.storm.FsiKafkaConfig;
import com.fs.insights.storm.IFsiComponent;
import com.fs.insights.storm.IFsiRichSpout;
import com.fs.insights.storm.kafka.FsiKafkaStringSpout;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import java.lang.reflect.Constructor;
import java.util.Map;

public class FsiGenericSpout extends BaseRichSpout implements IFsiComponent {
    public FsiGenericSpout() {
    }

    /**/

    private IFsiRichSpout mSpout;

    /**/

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
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
            mSpout = (IFsiRichSpout) constructor.newInstance(componentId, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSpout != null) {
            mSpout.open(conf, context, collector);
        }
    }

    @Override
    public void close() {
        if (mSpout != null) {
            mSpout.close();
        }
    }

    @Override
    public void nextTuple() {
        if (mSpout != null) {
            mSpout.nextTuple();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        if (mSpout != null) {
//            mSpout.declareOutputFields(declarer);
//        }
        declarer.declare(new Fields(mOutputDataFieldName));
    }

    @Override
    public void activate() {
        if (mSpout != null) {
            mSpout.activate();
        }
    }

    @Override
    public void deactivate() {
        if (mSpout != null) {
            mSpout.deactivate();
        }
    }

    @Override
    public void ack(Object msgId) {
        if (mSpout != null) {
            mSpout.ack(msgId);
        }
    }

    @Override
    public void fail(Object msgId) {
        if (mSpout != null) {
            mSpout.fail(msgId);
        }
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

    private String mOutputDataFieldName = FsiKafkaConfig.getOutputDataFieldName();

    public String getOutputDataFieldName() {
        return mOutputDataFieldName;
    }
}
