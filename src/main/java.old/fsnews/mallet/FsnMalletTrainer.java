package com.fs.fsnews.mallet;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.*;
import com.fs.fsnews.config.FsnMalletConfig;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class FsnMalletTrainer {

    private static Logger logger = Logger.getLogger(FsnMalletTrainer.class);

//    static int matriz[][] = new int[45][45];
//
//    private static void getDocumentsFromTopic(ParallelTopicModel model, int numberTopic) throws Exception {
//        int total = 0;
//
//        outFilesTopic.println("<h3 style='cursor:pointer;' onclick='slide("+ numberTopic +")'>Topic - "+ numberTopic +"</h3>");
//        outFilesTopic.println("<ul  id='liTopic"+ numberTopic +"'>");
//
//        File fileHash = new File(currentDirectory + inputTrainingHashFile);
//        String hash = readFile(fileHash, ENCODING);
//        String[] lines = hash.split("\n");
//
//        for (int documentPos = 0; documentPos < model.data.size(); documentPos++) {
//            TopicAssignment ta = model.data.get(documentPos);
//            String instanceName = ta.instances.getName().toString();
//
//            double[] tpcDistribution = model.getTopicProbabilities(documentPos);
//
//            /** DOBLE PERFILADO **/
//            List<Integer> topics = new ArrayList<Integer>();
//
//            int onlyTopic = -1;
//
//            for (int i = 0; i < tpcDistribution.length; i++) {
//                if (tpcDistribution[i] > percentTopic) {
//                    topics.add(i);
//                    if (onlyTopic == -1)
//                        onlyTopic = i;
//                }
//            }
//
//            if (topics.size() == 3) {
//                for (Integer numTopic : topics) {
//                    String[] values = lines[documentPos].split("\t");
//                    outFilesTopic.println("<li>"+ instanceName +"\t"+ numTopic +"\t"+ tpcDistribution[numTopic] +"\t"+ values[1] +
//                            "<button onclick='addSerie("+ instanceName +");'> add </button>"+
//                            "<button onclick='removeSerie("+ instanceName +");'> remove </button></li>");
//                }
//                //System.out.println(" matriz[topics.get(0)][topics.get(1)] "+ matriz[topics.get(0)][topics.get(1)]);
//                //matriz[topics.get(0)][topics.get(1)]++;
//                //matriz[topics.get(1)][topics.get(0)]++;
//                total++;
//
//            } else if (topics.size() == 1) {
//                //matriz[onlyTopic][topics.get(0)]++;
//            }
//        		/* else if (topics.size() == 3){
//        	}
//        		matriz[topics.get(0)][topics.get(1)]++;
//        	} else {
//        		matriz[topics.get(0)][topics.get(1)]++;
//        	}*/
//
//            //if (tpcDistribution[numberTopic] > percentTopic) {
////        		String[] values = lines[documentPos].split("\t");
////        		outFilesTopic.println("<li>"+ instanceName +"\t"+ tpcDistribution[numberTopic] +"\t"+ values[1] +
////        				"<button onclick='addSerie("+ instanceName +");'> add </button>"+
////        				"<button onclick='removeSerie("+ instanceName +");'> remove </button></li>");
//            //	total++;
//            //matriz[numberTopic][i]++;
//            //}
//
//        }
//        outFilesTopic.println("</ul>");
//        outFilesTopic.println("<span> Topic - "+ numberTopic +" Documentos : "+ total +"</span>");
//
//        totalFound = totalFound + total;
//
//        for (int x=0; x < matriz.length; x++) {
//            System.out.print("-"+ x +"-");
//            for (int y=0; y < matriz[x].length; y++) {
//                System.out.print(matriz[x][y] +" ");
//            }
//            System.out.println("------");
//        }
//    }

    public static void main(String[] args) throws Exception {
        Properties fsnMalletProperties = FsnMalletConfig.loadProperties("");
        //
        String modelName = fsnMalletProperties.getProperty("fsn.mallet.model.name", "model_00");
        int numIterations = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.model.numIterations", "1000"));
        //
        File modelsDir = new File(fsnMalletProperties.getProperty("fsn.mallet.malletDir", "./mallet"));
        File modelDir = new File(modelsDir, modelName);
        //File modelFile = new File(modelDir, "model." + numIterations);
        File modelFile = new File(modelDir, "model");
        File instancesFile = new File(modelDir, "instances");
        //
        int numTopics = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.training.numTopics", "45"));
        double alpha = Double.parseDouble(fsnMalletProperties.getProperty("fsn.mallet.training.alpha", "1"));
        double beta = Double.parseDouble(fsnMalletProperties.getProperty("fsn.mallet.training.beta", "0.2"));
        //
        int numThreads = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.training.numThreads", "2"));
        int optimizeInterval = Integer.parseInt(fsnMalletProperties.getProperty("fsn.mallet.training.optimizeInterval", "50"));
        //
        File trainingFile = new File (fsnMalletProperties.getProperty("fsn.mallet.training.fileName","./config/mallet/training.clean.txt"));
        //
        File stopwordsFile = new File(fsnMalletProperties.getProperty("fsn.mallet.stopwords.fileName", "./config/mallet/stopwords.txt"));
        //
        modelDir.mkdirs();
        //
        // Begin by importing documents from text to feature sequences
        List<Pipe> pipeList = new ArrayList<>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add(new TokenSequenceRemoveStopwords(stopwordsFile, "UTF-8", false, false, false));
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instanceList = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader = new InputStreamReader(new FileInputStream(trainingFile), "UTF-8");
        instanceList.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 2, 1)); // data, label, name fields
        //
        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        ParallelTopicModel model = new ParallelTopicModel(numTopics, alpha, beta);
        model.addInstances(instanceList);
        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(numThreads);
        // Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setOptimizeInterval(optimizeInterval);
        model.setNumIterations(numIterations);
        model.setSaveSerializedModel(numIterations, modelFile.getAbsolutePath());
        model.estimate();
        //
        instanceList.save(instancesFile);
        //
        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instanceList.getDataAlphabet();

        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;

        Formatter tokenFormatter = new Formatter(new StringBuilder(), Locale.US);
        for (int i = 0; i < tokens.getLength(); ++i) {
            tokenFormatter.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(i)), topics.getIndexAtPosition(i));
        }
        logger.info(String.valueOf(tokenFormatter));
        //
//        PrintWriter outFilesTopic = new PrintWriter(currentDirectory + outputTopicFile);
//        // Mostrar documentos de cada topico con una similitud > 0.75
//        for (int i = 0; i < numTopics; i++) {
//            getDocumentsFromTopic(model, i);
//        }
//        outFilesTopic.close();
//        System.out.println(" FOUND : ["+ totalFound +"/"+ model.getData().size() +"]");
        //
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        //
        Formatter topicFormatter = new Formatter(new StringBuilder(), Locale.US);
        for (int topicNum = 0; topicNum < numTopics; topicNum++) {
            topicFormatter.format("Topic %d \n", topicNum);
            int rank = 0;
            Iterator<IDSorter> iterator = topicSortedWords.get(topicNum).iterator();
            while (iterator.hasNext() && rank < 100) {
                IDSorter idCountPair = iterator.next();
                topicFormatter.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            topicFormatter.format("\n");
        }
        logger.info(String.valueOf(topicFormatter));
    }
}
