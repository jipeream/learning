/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  cc.mallet.pipe.CharSequence2TokenSequence
 *  cc.mallet.pipe.CharSequenceLowercase
 *  cc.mallet.pipe.Pipe
 *  cc.mallet.pipe.SerialPipes
 *  cc.mallet.pipe.TokenSequence2FeatureSequence
 *  cc.mallet.pipe.TokenSequenceRemoveStopwords
 *  cc.mallet.pipe.iterator.CsvIterator
 *  cc.mallet.topics.ParallelTopicModel
 *  cc.mallet.topics.TopicAssignment
 *  cc.mallet.types.Alphabet
 *  cc.mallet.types.FeatureSequence
 *  cc.mallet.types.IDSorter
 *  cc.mallet.types.Instance
 *  cc.mallet.types.InstanceList
 *  cc.mallet.types.LabelSequence
 *  org.apache.log4j.Logger
 */
package com.fs.fsnews.mallet;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import com.fs.fsnews.config.FsnMalletConfig;
import es.jipeream.library.JavaUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class FsnMalletTrainer {
    private static Logger logger = Logger.getLogger((Class)FsnMalletTrainer.class);

    public static void main(String[] args) throws Exception {
        String configDirName = JavaUtils.getConfigDir(args);
        Properties fsnMalletProperties = FsnMalletConfig.loadProperties(configDirName);
        //
        String modelName = fsnMalletProperties.getProperty("fsn.mallet.model.name", "model_00");
        int numIterations = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.model.numIterations", "1000"));
        //
        // File malletBaseDir = new File(fsnMalletProperties.getProperty("fsn.mallet.malletBaseDir", "./mallet"));
        File malletBaseDir = new File("./mallet");
        File configDir = new File(malletBaseDir, configDirName);
        File modelDir = new File(configDir, modelName);
        File modelFile = new File(modelDir, "model");
        File instancesFile = new File(modelDir, "instances");
        //
        int numTopics = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.training.numTopics", "45"));
        double alpha = Double.parseDouble(fsnMalletProperties.getProperty("fsn.mallet.training.alpha", "1"));
        double beta = Double.parseDouble(fsnMalletProperties.getProperty("fsn.mallet.training.beta", "0.2"));
        int numThreads = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.training.numThreads", "2"));
        int optimizeInterval = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.training.optimizeInterval", "50"));
        //
        File trainingFile = new File(configDir, fsnMalletProperties.getProperty("fsn.mallet.training.fileName", "training.txt"));
        File stopwordsFile = new File(configDir, fsnMalletProperties.getProperty("fsn.mallet.stopwords.fileName", "stopwords.txt"));
        //
        modelDir.mkdirs();
        //
        // Pipes: lowercase, tokenize, remove stopwords, map to features
        ArrayList<Pipe> pipeList = new ArrayList<>();
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequenceRemoveStopwords(stopwordsFile, "UTF-8", false, false, false));
        pipeList.add(new TokenSequence2FeatureSequence());
        //
        InstanceList instanceList = new InstanceList(new SerialPipes(pipeList));
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(trainingFile), "UTF-8");
        instanceList.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data, label, name fields
        //
        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        ParallelTopicModel model = new ParallelTopicModel(numTopics, alpha, beta);
        model.addInstances(instanceList);
        model.setNumThreads(numThreads);
        //
        // Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setOptimizeInterval(optimizeInterval);
        model.setNumIterations(numIterations);
        model.setSaveSerializedModel(numIterations, modelFile.getAbsolutePath());
        model.estimate();
        instanceList.save(instancesFile);
        //
        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instanceList.getDataAlphabet();
        //
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        //
        Formatter tokenFormatter = new Formatter(new StringBuilder(), Locale.US);
        for (int i = 0; i < tokens.getLength(); ++i) {
            tokenFormatter.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(i)), topics.getIndexAtPosition(i));
        }
        logger.info(String.valueOf(tokenFormatter));
        //
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        //
//        PrintWriter outFilesTopic = new PrintWriter(currentDirectory + outputTopicFile);
//        // Mostrar documentos de cada topico con una similitud > 0.75
//        for (int i = 0; i < numTopics; i++) {
//            getDocumentsFromTopic(model, i);
//        }
//        outFilesTopic.close();
//        System.out.println(" FOUND : ["+ totalFound +"/"+ model.getData().size() +"]");
        //
        Formatter topicFormatter = new Formatter(new StringBuilder(), Locale.US);
        for (int topicNum = 0; topicNum < numTopics; ++topicNum) {
            topicFormatter.format("Topic %d \n", topicNum);
            int rank = 0;
            Iterator<IDSorter> iterator = topicSortedWords.get(topicNum).iterator();
            while (iterator.hasNext() && rank < 100) {
                IDSorter idCountPair = iterator.next();
                topicFormatter.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            topicFormatter.format("\n", new Object[0]);
        }
        logger.info(String.valueOf(topicFormatter));
    }
}

