package main.java.utils;

public class Parameters {
    private Parameters () {
    	throw new IllegalStateException("Parameters class must not be instantiated");
    }
    public static final String PROJECT1 = "BOOKKEEPER";
    public static final String PROJECT2 = "ZOOKEEPER";
    public static final String CLASSTYPE = ".java"; 
    public static final String DATASET = "dataset.csv";
    public static final String DATASETDIR= "./dataset/";
    public static final String DATASET_ANALISYS = "analisys.csv";
    	
    public static String toUrl(String project) {
	return String.format("https://github.com/apache/%s.git", project);
    }
}

