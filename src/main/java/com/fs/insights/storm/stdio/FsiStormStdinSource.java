package com.fs.insights.storm.stdio;

import com.fs.insights.storm.FsiStormConfig;
import com.fs.insights.storm.IFsiStormSource;
import com.fs.insights.storm.twitter.TwitterSampleSpout;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.MultiScheme;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import twitter4j.Status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FsiStormStdinSource extends BaseRichSpout implements IFsiStormSource {
    public FsiStormStdinSource(String componentId, Map conf) {
        super();
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
    }

    public FsiStormStdinSource() {
        super();
        //
//        mConf = conf;
//        mComponentId = componentId;
        //
    }

    /**/


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

    private SpoutOutputCollector mSpoutOutputCollector;
    private BufferedReader mBufferedReader;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        mSpoutOutputCollector = spoutOutputCollector;
        mBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void nextTuple() {
        try {
            String line = mBufferedReader.readLine();
            if (line != null) {
                mSpoutOutputCollector.emit(new Values(line));
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
