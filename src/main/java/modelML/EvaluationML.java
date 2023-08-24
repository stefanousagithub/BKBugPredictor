package main.java.modelML;

import main.java.modelML.ProfileML.CLASSIF;
import main.java.modelML.ProfileML.CS;
import main.java.modelML.ProfileML.FS;
import main.java.modelML.ProfileML.SMP;
import weka.classifiers.Evaluation;

public class EvaluationML {
    Evaluation eval;
    ProfileML.FS fs;
    ProfileML.SMP smp;
    ProfileML.CS cs;
    ProfileML.CLASSIF classif;
    
    public EvaluationML(Evaluation eval, FS fs, SMP smp, CS cs, CLASSIF classif) {
	super();
	this.eval = eval;
	this.fs = fs;
	this.smp = smp;
	this.cs = cs;
	this.classif = classif;
    }
    
    public Evaluation getEval() {
        return eval;
    }
    public ProfileML.FS getFs() {
        return fs;
    }
    public ProfileML.SMP getSmp() {
        return smp;
    }
    public ProfileML.CS getCs() {
        return cs;
    }
    public ProfileML.CLASSIF getClassif() {
        return classif;
    }
}
