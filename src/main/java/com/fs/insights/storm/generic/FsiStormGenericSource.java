package com.fs.insights.storm.generic;

import com.fs.insights.storm.FsiStormConfig;
import com.fs.insights.storm.IFsiStormComponent;
import com.fs.insights.storm.IFsiStormSource;
import com.fs.insights.storm.kafka.FsiStormKafkaStringSource;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import java.lang.reflect.Constructor;
import java.util.Map;

public class FsiStormGenericSource extends BaseRichSpout implements IFsiStormComponent {
    public FsiStormGenericSource() {
    }

    /**/

    private IFsiStormSource mSpout;

    /**/

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
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
            mSpout = (IFsiStormSource) constructor.newInstance(componentId, conf);
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

    private String mOutputDataFieldName = FsiStormConfig.getOutputDataFieldName();

    public String getOutputDataFieldName() {
        return mOutputDataFieldName;
    }
}
