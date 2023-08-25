package main.java.milestone2;

import java.text.DecimalFormat;

import main.java.modelML.ProfileML;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;

public class FilterDB {
    public FilterDB() {
	super();
    }

    Instances train;
    Instances test;
	public void featureSelection(ProfileML.FS fs) throws Exception {
		if (fs.equals(ProfileML.FS.BEST_FIRST)) {
			AttributeSelection attrSelection = new AttributeSelection();
			attrSelection.setEvaluator(new CfsSubsetEval());
			attrSelection.setSearch(new BestFirst());
			attrSelection.SelectAttributes(train);
			
			Remove removeFilter = new Remove();
			removeFilter.setAttributeIndicesArray(attrSelection.selectedAttributes());
			removeFilter.setInvertSelection(true);
			removeFilter.setInputFormat(train);
			
			train = Filter.useFilter(train, removeFilter);
			test = Filter.useFilter(test, removeFilter);			
		}
	}
	
	public void sampling(ProfileML.SMP smp) throws Exception {
		if (smp.equals(ProfileML.SMP.OVERSAMPLING)) {
			Resample resample = new Resample();
			resample.setInputFormat(train);
			DecimalFormat df = new DecimalFormat("#.##");
			resample.setOptions(Utils.splitOptions(String.format("%s %s", "-B 1.0 -Z", df.format(computerMajorityClassPercentage()))));
			train = Filter.useFilter(train, resample);
		} else if (smp.equals(ProfileML.SMP.UNDERSAMPLING)) {
			SpreadSubsample underSampling = new SpreadSubsample();
			underSampling.setInputFormat(train);
			underSampling.setOptions(Utils.splitOptions("-M 1.0"));
			train = Filter.useFilter(train, underSampling);
		} else if (smp.equals(ProfileML.SMP.SMOTE)) {
			SMOTE smote = new SMOTE();
			smote.setInputFormat(train);
			train = Filter.useFilter(train, smote);
		}
	}
	
	private double computerMajorityClassPercentage() {
		int buggyClasses = 0;
		Instances dataset = new Instances(train);
		dataset.addAll(train);
		
		for (Instance recordDataset: dataset) {
			String buggy = recordDataset.stringValue(recordDataset.numAttributes()-1);
			if (buggy.equals("1"))
				buggyClasses++;
		}
		
		double percentage = (100 * 2 * buggyClasses/dataset.size());
		if (percentage >= 50)
			return percentage;
		else
			return 100-percentage;
	}
	
	public Instances getTrainSet(Instances dataset, int trainingRelease, int releases) throws Exception{
		RemoveWithValues filter = new RemoveWithValues();
		int range = releases-trainingRelease;
		int[] arr = new int[range];
		for(int i = 1; i < range+1; i++) {
			arr[range-i] = releases-i;
		}
		filter.setAttributeIndex("1");
		filter.setNominalIndicesArr(arr);
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	public Instances getTestSet(Instances dataset, int trainingRelease) throws Exception{
		String options =  String.format("-C 1 -L %d -V", trainingRelease+1);
		RemoveWithValues filter = new RemoveWithValues();
		filter.setOptions(Utils.splitOptions(options));
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
}
