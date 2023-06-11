package main.java.milestone2;

//imports
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import main.java.model.ClassInstance;
import main.java.utils.Parameters;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.instance.NonSparseToSparse;
import weka.filters.unsupervised.instance.RemoveWithValues;

public class ControllerAnalizeClassifier {
	static String projName = "BOOKKEEPER";
	public static void main(String args[]) throws Exception{
		//load dataset
		DataSource source = new DataSource("/home/stefano/Desktop/ISW2/BKBugPredictor/BOOKKEEPERdataset.arff");
		Instances dataset = source.getDataSet();
		dataset.deleteStringAttributes();
		
		// Cicle for walk forward (from 1 to numRelease)
			// Divide dataset:

		ArrayList<Evaluation> evals = new ArrayList<>();
		int numAttr = dataset.numAttributes();
		int numVers = dataset.attribute(0).numValues();
		Evaluation eval;
		for (int i = 1; i < numVers; i++) {
			// get train and test set: Walk Forward
			Instances dataset_train = getTrainSet(dataset,i,numVers);
			Instances dataset_test = getTestSet(dataset,i);
			dataset_train.deleteAttributeAt(0);
			dataset_test.deleteAttributeAt(0);			
			dataset_train.setClassIndex(numAttr - 2);
			dataset_test.setClassIndex(numAttr - 2);
			
			// Apply RandomForest
			RandomForest rf = new RandomForest();
			rf.buildClassifier(dataset_train);
			eval = new Evaluation(dataset_test);	
			eval.evaluateModel(rf, dataset_test);
			evals.add(eval);
			
			// Apply NaiveBayes
			NaiveBayes nb = new NaiveBayes();
			nb.buildClassifier(dataset_train);
			eval = new Evaluation(dataset_test);	
			eval.evaluateModel(nb, dataset_test);
			evals.add(eval);
			
			// Apply IBk
			IBk ibk = new IBk();
			ibk.buildClassifier(dataset_train);
			eval = new Evaluation(dataset_test);	
			eval.evaluateModel(ibk, dataset_test);
			evals.add(eval);	
		}
		// Print confusion matrix (?)
		createCsv(evals);
	}
	
	public static Instances getTrainSet(Instances dataset, int trainingRelease, int releases) throws Exception{
		RemoveWithValues filter = new RemoveWithValues();
		int range = releases-trainingRelease;
		int[] arr = new int[range];
		for(int i = 1; i < range+1; i++) {
			arr[range-i] = releases-i;
		}
		filter.setAttributeIndex("1");
		filter.setNominalIndicesArr(arr);
		filter.setInputFormat(dataset);
		Instances newData = Filter.useFilter(dataset, filter);
		return newData;
	}
	
	public static Instances getTestSet(Instances dataset, int trainingRelease) throws Exception{
		String options =  String.format("-C 1 -L %d -V", trainingRelease+1);
		RemoveWithValues filter = new RemoveWithValues();
		filter.setOptions(Utils.splitOptions(options));
		filter.setInputFormat(dataset);
		Instances newData = Filter.useFilter(dataset, filter);
		return newData;
	}
	
	public static void createCsv(ArrayList<Evaluation> evals) {
		 FileWriter fileWriter = null;
		 try {
           String outname = projName + Parameters.DATASET_ANALISYS;
				    //Name of CSV for output
				    fileWriter = new FileWriter(outname);
           fileWriter.append("Dataset,#TrainingRelease,Classifier,Precision,Recall,AUC,Kappa\n");
           int trainRelease = 1;
           int count = 0;
           String classifier = null;
           for (Evaluation eval : evals) {
              if(count > 2) {
            	  trainRelease++;
            	  count = 0;
              }
              if(count == 0) classifier = "RandomForest";
              if(count == 1) classifier = "NaiveBayes";
              if(count == 2) classifier = "IBk";
              
              String line = String.format("%s,%d,%s,%2f,%2f,%2f,%2f\n", projName, 
            		  trainRelease, classifier, eval.precision(0), eval.recall(0), eval.areaUnderROC(0), eval.kappa());
              fileWriter.append(line);
              count++;
           }
			
        } catch (Exception e) {
           System.out.println("Error in dataset.csv writer");
           e.printStackTrace();
        } finally {
           try {
              fileWriter.flush();
              fileWriter.close();
           } catch (IOException e) {
              System.out.println("Error while flushing/closing fileWriter !!!");
              e.printStackTrace();
           }
        }
	}
}
