package main.java.modelML;

public class ProfileML {
    private ProfileML() {
    	throw new IllegalStateException("ProfileML class must not be instantiated");
    }
    
    public enum CLASSIF {
        RANDOM_FOREST,
        NAIVE_BAYES,
        IBK;
    }
    
    public enum FS {
        NO_SELECTION,
        BEST_FIRST;
    }
        
    public enum SMP {
        NO_SAMPLING,
        OVERSAMPLING,	
        UNDERSAMPLING,
        SMOTE;
    }
    
    public enum CS {
        NO_COST_SENSITIVE,
        SENSITIVE_THRESHOLD,
        SENSITIVE_LEARNING;
    }
}
