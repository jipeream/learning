package com.fs.insights.storm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import java.lang.reflect.Constructor;
import java.util.Map;

public class FsiConfigurableSpout extends BaseRichSpout {
    public FsiConfigurableSpout() {

    }

    private IFsiRichSpout mSpout;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        String id = context.getThisComponentId();
        try {
            String className = FsiConfigUtils.getConfStringValue(conf, id, "className");
            className = className == null ? FsiKafkaStringSpout.class.getCanonicalName() : className;
            Class<?> c1ass = Class.forName(className);
            //
            Constructor<?> constructor = c1ass.getConstructor(String.class, Map.class);
            mSpout = (IFsiRichSpout) constructor.newInstance(id, conf);
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
        if (mSpout != null) {
            mSpout.declareOutputFields(declarer);
        }
        declarer.declare(new Fields("data"));
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
}
