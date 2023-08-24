package main.java.modelML;

public class ProfileML {
    public ProfileML() {
	throw new IllegalStateException("ProfileML class must not be instantiated");
    }
    
    static public enum CLASSIF {
        RANDOM_FOREST,
        NAIVE_BAYES,
        IBK;
    }
    
    static public enum FS {
        NO_SELECTION,
        BEST_FIRST;
    }
        
    static public enum SMP {
        NO_SAMPLING,
        OVERSAMPLING,	
        UNDERSAMPLING,
        SMOTE;
    }
    
    static public enum CS {
        NO_COST_SENSITIVE,
        SENSITIVE_THRESHOLD,
        SENSITIVE_LEARNING;
    }
}
