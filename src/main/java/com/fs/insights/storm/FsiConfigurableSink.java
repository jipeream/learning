package com.fs.insights.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.lang.reflect.Constructor;
import java.util.Map;

public class FsiConfigurableSink extends BaseRichBolt {
    public FsiConfigurableSink() {
    }

    private IFsiRichSink mSink;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        String id = context.getThisComponentId();
        try {
            String className = FsiConfigUtils.getConfStringValue(conf, id, "className");
            className = className == null ? FsiKafkaStringSpout.class.getCanonicalName() : className;
            Class<?> c1ass = Class.forName(className);
            //
            Constructor<?> constructor = c1ass.getConstructor(String.class, Map.class);
            mSink = (IFsiRichSink) constructor.newInstance(id, conf);
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
        if (mSink != null) {
            mSink.declareOutputFields(declarer);
        }
    }
}
