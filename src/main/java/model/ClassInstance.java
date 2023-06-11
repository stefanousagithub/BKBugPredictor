package main.java.model;

import java.util.ArrayList;
import java.util.Date;

public class ClassInstance {
	private String name;
	private Version version;
	private Date dateCreation;
	private int NR;
	private int NFix;
	private ArrayList<String> authors;
	private int NAuth;
	private int size;
	private int locToched;
	private int churn;
	private int maxChurn;
	private int maxLocAdded;
	private int avgChurn;
	private int committedTogether;
	private int age;
	private boolean bugginess;
	
	public ClassInstance(String name, Version version, Date dateCreation) {
		super();
		this.name = name;
		this.version = version;
		this.dateCreation = dateCreation;
		this.NR = 0;
		this.NFix = 0;
		this.authors = new ArrayList<String>();
		this.NAuth = 0;
		this.size = 0;      // Forse da aggiungere al construttore (?)
		this.locToched = 0;
		this.churn = 0;
		this.maxChurn = 0;
		this.maxLocAdded = 0;
		this.avgChurn = 0;
		this.committedTogether = 0;
		this.age = 0;
		this.bugginess = false;
	}
	
	public ClassInstance(String name, Version version, Date dateCreation, int nR, int nFix, ArrayList<String> authors,
			int nAuth, int size, int locToched, int churn, int maxChurn, int maxLocAdded, int avgChurn,
			int committedTogether, int age, boolean bugginess) {
		super();
		this.name = name;
		this.version = version;
		this.dateCreation = dateCreation;
		this.NR = nR;
		this.NFix = nFix;
		this.authors = authors;
		this.NAuth = nAuth;
		this.size = size;
		this.locToched = locToched;
		this.churn = churn;
		this.maxChurn = maxChurn;
		this.maxLocAdded = maxLocAdded;
		this.avgChurn = avgChurn;
		this.committedTogether = committedTogether;
		this.age = age;
		this.bugginess = bugginess;
	}
	
	public void updateInstanceLoc(int added, int deleted) {
		if (added > maxLocAdded) 
			maxLocAdded = added;
		
		locToched += added + deleted;
		
		int churn = added - deleted;
		this.churn += churn;
		if (churn > maxChurn) 
			maxChurn = churn;
		size += churn;
	}
	
	public void updateInstanceMeta(String author, boolean fixCommit) {
		NR++;
		
		if(fixCommit) NFix++;
		
		if(newAuthor(author)) authors.add(author);
				
		this.avgChurn = churn / NR;
	}
	
	private boolean newAuthor(String author) {
		for(String auth : authors) {
			if(auth.contains(author)) return false; 
		}
		NAuth += 1;
		return true;
	}
	
	public void increaseCommittedTogether(int added) {
		committedTogether += added;
	}
	
	public boolean insideAV(Version iv, Version fv) {
		if(version.isBefore(fv) && !version.isBefore(iv)) return true;
		else return false;
	}
	
	public void increaseAge() {
		this.age++;
	}
	
	public void setBugginess(boolean bugginess) {
		this.bugginess = bugginess;
	}

	public String getName() {
		return name;
	}

	public Version getVersion() {
		return version;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public int getNR() {
		return NR;
	}

	public int getNFix() {
		return NFix;
	}

	public int getNAuth() {
		return NAuth;
	}

	public int getSize() {
		return size;
	}

	public int getLocToched() {
		return locToched;
	}

	public int getChurn() {
		return churn;
	}

	public int getMaxLocAdded() {
		return maxLocAdded;
	}

	public int getAvgChurn() {
		return avgChurn;
	}

	public int getCommittedTogether() {
		return committedTogether;
	}

	public boolean isBugginess() {
		return bugginess;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public ArrayList<String> getAuthors() {
		return authors;
	}

	public int getMaxChurn() {
		return maxChurn;
	}

	public int getAge() {
		return age;
	}
}
