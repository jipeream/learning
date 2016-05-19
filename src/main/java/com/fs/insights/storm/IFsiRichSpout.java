package com.fs.insights.storm;

import org.apache.storm.spout.ISpout;
import org.apache.storm.task.IBolt;
import org.apache.storm.topology.IComponent;

public interface IFsiRichSpout extends ISpout, IComponent {
}
