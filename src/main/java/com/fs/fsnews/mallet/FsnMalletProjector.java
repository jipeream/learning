/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  cc.mallet.pipe.Pipe
 *  cc.mallet.topics.ParallelTopicModel
 *  cc.mallet.topics.TopicInferencer
 *  cc.mallet.types.Alphabet
 *  cc.mallet.types.IDSorter
 *  cc.mallet.types.Instance
 *  cc.mallet.types.InstanceList
 *  org.apache.log4j.Logger
 */
package com.fs.fsnews.mallet;

import cc.mallet.pipe.Pipe;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.fs.fsnews.config.FsnMalletConfig;
import es.jipeream.library.JavaUtils;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import org.apache.log4j.Logger;

public class FsnMalletProjector {
    private static Logger logger = Logger.getLogger((Class)FsnMalletProjector.class);
    private static FsnMalletProjector instance = null;
    private String modelName;
    private int modelNumIterations;
    private InstanceList instanceList;
    private ParallelTopicModel model;
    private int numIterations;
    private int thinning = 1;
    private int burnIn = 20;
    private double weightThreshold;

    public static synchronized FsnMalletProjector getInstance() throws Exception {
        if (instance == null) {
            instance = new FsnMalletProjector();
        }
        return instance;
    }

    public String getModelName() {
        return this.modelName;
    }

    public InstanceList getInstanceList() {
        return this.instanceList;
    }

    public ParallelTopicModel getModel() {
        return this.model;
    }

    public double getWeightThreshold() {
        return this.weightThreshold;
    }

    public void prepare(String configDirName) throws Exception {
        Properties fsnMalletProperties = FsnMalletConfig.loadProperties(configDirName);
        //
        this.modelName = fsnMalletProperties.getProperty("fsn.mallet.model.name", "model_00");
        this.modelNumIterations = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.model.numIterations", "1000"));
        //
        // File malletBaseDir = new File(fsnMalletProperties.getProperty("fsn.mallet.malletBaseDir", "./mallet"));
        File malletBaseDir = new File("./mallet");
        File configDir = new File(malletBaseDir, configDirName);
        File modelDir = new File(configDir, modelName);
        File modelFile = new File(modelDir, "model");
        File instancesFile = new File(modelDir, "instances");
        //
        this.instanceList = InstanceList.load((File)instancesFile);
        this.model = ParallelTopicModel.read((File)modelFile);
        this.numIterations = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.projector.numIterations", "1000"));
        this.thinning = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.projector.thinning", "1"));
        this.burnIn = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.projector.burnIn", "20"));
        this.weightThreshold = Double.parseDouble(fsnMalletProperties.getProperty("fsn.mallet.projector.weightThreshold", "0.0001"));
    }

    public List<TreeSet<IDSorter>> getTopicSortedWords() {
        return this.model.getSortedWords();
    }

    public InstanceList createInstanceList() {
        Pipe pipe = this.getInstanceList().getPipe();
        InstanceList instanceList = new InstanceList(pipe);
        return instanceList;
    }

    public List<InferredTopic> getInferredTopicList(String text) {
        ArrayList<InferredTopic> inferredTopicList = new ArrayList<InferredTopic>();
        InstanceList instanceList = this.createInstanceList();
        Instance instance = new Instance((Object)text, (Object)null, (Object)"instance", (Object)null);
        instanceList.addThruPipe(instance);
        TopicInferencer topicInferencer = this.model.getInferencer();
        double[] weightArray = topicInferencer.getSampledDistribution(instance, this.numIterations, this.thinning, this.burnIn);
        for (int topicNum = 0; topicNum < weightArray.length; ++topicNum) {
            InferredTopic inferredTopic = new InferredTopic(this.getModelName(), topicNum, weightArray[topicNum]);
            if (inferredTopic.getWeight() < this.getWeightThreshold()) continue;
            inferredTopicList.add(inferredTopic);
        }
        Collections.sort(inferredTopicList, new Comparator<InferredTopic>(){

            @Override
            public int compare(InferredTopic inferredTopic1, InferredTopic inferredTopic2) {
                return - (int)Math.signum(inferredTopic1.getWeight() - inferredTopic2.getWeight());
            }
        });
        return inferredTopicList;
    }

    public static void main(String[] args) throws Exception {
        String configDirName = JavaUtils.getConfigDir(args);
        Properties fsnMalletProperties = FsnMalletConfig.loadProperties(configDirName);
        //
        FsnMalletProjector fsnMalletProjector = FsnMalletProjector.getInstance();
        fsnMalletProjector.prepare(configDirName);
        //
        String text = fsnMalletProperties.getProperty("fsn.mallet.testing.text", "");
        List<InferredTopic> inferredTopicList = fsnMalletProjector.getInferredTopicList(text);
        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        for (InferredTopic inferredTopic : inferredTopicList) {
            logger.info("  topic: " + inferredTopic.getTopicNum() + " prob:" + decimalFormat.format(inferredTopic.getWeight()));
            List<TreeSet<IDSorter>> topicSortedWords = fsnMalletProjector.getTopicSortedWords();
            Iterator<IDSorter> iterator = topicSortedWords.get(inferredTopic.getTopicNum()).iterator();
            for (int rank = 0; iterator.hasNext() && rank < 100; ++rank) {
                IDSorter idSorter = iterator.next();
                logger.info(String.format("   %s (%.0f)", fsnMalletProjector.getInstanceList().getDataAlphabet().lookupObject(idSorter.getID()), idSorter.getWeight()));
                ++rank;
            }
        }
    }

    public static class InferredTopic {
        private final String modelName;
        private final int topicNum;
        private final double weight;

        public String getModelName() {
            return this.modelName;
        }

        public int getTopicNum() {
            return this.topicNum;
        }

        public double getWeight() {
            return this.weight;
        }

        public InferredTopic(String modelName, int topicNum, double weight) {
            this.modelName = modelName;
            this.topicNum = topicNum;
            this.weight = weight;
        }
    }

}

