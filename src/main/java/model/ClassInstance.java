package main.java.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ClassInstance {
	private String name;
	private Version version;
	private Date dateCreation;
	private int NR;
	private int NFix;
	private List<String> authors;
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
	
	public ClassInstance(ClassInstance c) {
		super();
		this.name = c.getName();
		this.version = c.getVersion();
		this.dateCreation = c.getDateCreation();
		NR = c.getNR();
		NFix = c.getNFix();
		this.authors = c.getAuthors();
		NAuth = c.getNAuth();
		this.size = c.getSize();
		this.locToched = c.getLocToched();
		this.churn = c.getChurn();
		this.maxChurn = c.getMaxChurn();
		this.maxLocAdded = c.getMaxLocAdded();
		this.avgChurn = c.getAvgChurn();
		this.committedTogether = c.getCommittedTogether();
		this.age = c.getAge();
		this.bugginess = c.isBugginess();
	}
	
	public ClassInstance(String name, Version version, Date dateCreation) {
		super();
		this.name = name;
		this.version = version;
		this.dateCreation = dateCreation;
		this.NR = 0;
		this.NFix = 0;
		this.authors = new ArrayList<String>();
		this.NAuth = 0;
		this.size = 0;      
		this.locToched = 0;
		this.churn = 0;
		this.maxChurn = 0;
		this.maxLocAdded = 0;
		this.avgChurn = 0;
		this.committedTogether = 0;
		this.age = 0;
		this.bugginess = false;
	}
	
	public void updateInstanceLoc(int added, int deleted) {
		if (added > maxLocAdded) 
			maxLocAdded = added;
		
		locToched += added + deleted;
		
		int ch = added - deleted;
		this.churn += ch;
		if (ch > maxChurn) 
			maxChurn = ch;
		size += ch;
	}
	
	public void updateInstanceMeta(String author, boolean fixCommit) {
		NR++;
		
		if(fixCommit) NFix++;
		
		if(newAuthor(author)) authors.add(author);
				
		this.avgChurn = churn / NR;
	}
	
	private boolean newAuthor(String author) {
		boolean flag= true;
		for(String auth : authors) {
			if(auth.contains(author)) flag = false; 
		}
		NAuth += 1;
		return flag;
	}
	
	public void increaseCommittedTogether(int added) {
		committedTogether += added;
	}
	
	public boolean insideAV(Version iv, Version fv) {
		boolean flag= false;
		if(version.isBefore(fv) && (!version.isBefore(iv) || version.isEqual(iv))) flag = true;
		return flag;
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

	public List<String> getAuthors() {
		return authors;
	}

	public int getMaxChurn() {
		return maxChurn;
	}

	public int getAge() {
		return age;
	}
}
