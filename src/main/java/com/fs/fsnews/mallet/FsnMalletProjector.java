package com.fs.fsnews.mallet;

import cc.mallet.pipe.CharSequenceRemoveHTML;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.fs.fsnews.config.FsnMalletConfig;
import es.jipeream.library.JavaUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class FsnMalletProjector {

    private static Logger logger = Logger.getLogger(FsnMalletProjector.class);

    private static FsnMalletProjector instance = null;

    public synchronized static FsnMalletProjector getInstance() throws Exception {
        if (instance == null) {
            instance = new FsnMalletProjector();
        }
        return instance;
    }

    /**/

    private String modelName;
    private InstanceList instanceList;
    private ParallelTopicModel model;
    private double weightThreshold;

    public String getModelName() {
        return modelName;
    }

    public InstanceList getInstanceList() {
        return instanceList;
    }

    public InstanceList createInstanceList() {
        Pipe pipe = new SerialPipes(new Pipe[]{new Input2CharSequence(), new CharSequenceRemoveHTML()});
        InstanceList instanceList = new InstanceList(pipe);
        return instanceList;
    }

    public ParallelTopicModel getModel() {
        return model;
    }

    public double getWeightThreshold() {
        return weightThreshold;
    }

    /**/

    public void prepare() throws Exception {
        Properties fsnMalletProperties = FsnMalletConfig.loadProperties("");
        prepare(fsnMalletProperties);
    }

    public void prepare(File propertiesFile) throws Exception {
        Properties fsnMalletProperties = JavaUtils.loadProperties(propertiesFile);
        prepare(fsnMalletProperties);
    }

    public void prepare(Properties fsnMalletProperties) throws Exception {
        String modelName = fsnMalletProperties.getProperty("fsn.mallet.model.modelName", "model_00");
        int numIterations = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.model.numIterations", "1000"));
        //
        File modelsDir = new File(fsnMalletProperties.getProperty("fsn.mallet.modelsDir", "mallet"));
        File modelDir = new File(modelsDir, modelName);
        File modelFile = new File(modelDir, "model." + numIterations);
        File instancesFile = new File(modelDir, "instances");
        //
        this.modelName = modelName;
        this.instanceList = InstanceList.load(instancesFile);
        this.model = ParallelTopicModel.read(modelFile);
        this.weightThreshold = Double.parseDouble(fsnMalletProperties.getProperty("fsn.mallet.projector.weightThreshold", "0.0001"));
    }

    /**/

    public List<TreeSet<IDSorter>> getTopicSortedWords() {
        return model.getSortedWords();
    }

    /**/

    public static class InferredTopic {
        private final String modelName;
        private final int topicNum;
        private final double weight;

        public String getModelName() {
            return modelName;
        }

        public int getTopicNum() {
            return topicNum;
        }

        public double getWeight() {
            return weight;
        }

        public InferredTopic(String modelName, int topicNum, double weight) {
            this.modelName = modelName;
            this.topicNum = topicNum;
            this.weight = weight;
        }
    }

    public List<InferredTopic> getInferredTopicList(String text) {
        List<InferredTopic> inferredTopicList = new ArrayList<>();
        //
//        InstanceList instanceList = createInstanceList();
//        instanceList.addThruPipe(new Instance(text, null, "instance", null));
        //
        InstanceList instanceList = getInstanceList();
        //
        TopicInferencer topicInferencer = model.getInferencer();
        double[] weightArray = topicInferencer.getSampledDistribution(instanceList.get(0), 10000, 1, 20);
        //
        for (int topicNum = 0 ; topicNum < weightArray.length; ++ topicNum) {
            InferredTopic inferredTopic = new InferredTopic(getModelName(), topicNum, weightArray[topicNum]);
            if (inferredTopic.getWeight() >= getWeightThreshold()) {
                inferredTopicList.add(inferredTopic);
            }
        }
        //
        Collections.sort(inferredTopicList, new Comparator<InferredTopic>() {
                    @Override
                    public int compare(InferredTopic inferredTopic1, InferredTopic inferredTopic2) {
                        return -(int)Math.signum(inferredTopic1.getWeight() - inferredTopic2.getWeight());
                    }
                }
        );
        //
        return inferredTopicList;
    }

    /**/

    public static void main(String[] args) throws Exception {
        FsnMalletProjector fsnMalletProjector = getInstance();
        fsnMalletProjector.prepare();
        //
        Properties fsnMalletProperties = FsnMalletConfig.loadProperties("");
        String text = fsnMalletProperties.getProperty("fsn.mallet.testing.text", "Un dos tres probando");
        //
        List<InferredTopic> inferredTopicList = fsnMalletProjector.getInferredTopicList(text);
        //
        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        for (InferredTopic inferredTopic : inferredTopicList) {
            logger.info("  topic: " + inferredTopic.getTopicNum() + " prob:" + decimalFormat.format(inferredTopic.getWeight()));
            List<TreeSet<IDSorter>> topicSortedWords = fsnMalletProjector.getTopicSortedWords();
            int rank = 0;
            for (Iterator<IDSorter> iterator = topicSortedWords.get(inferredTopic.getTopicNum()).iterator(); iterator.hasNext() && rank < 100; ++rank) {
                IDSorter idSorter = iterator.next();
                logger.info(String.format("   %s (%.0f)", fsnMalletProjector.getInstanceList().getDataAlphabet().lookupObject(idSorter.getID()), idSorter.getWeight()));
                rank++;
            }
        }
    }

}
