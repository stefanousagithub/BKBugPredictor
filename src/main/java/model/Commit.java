package main.java.model;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {
	private RevCommit rev;
	private String author;
	private Version version;
	private Date date;
	private ArrayList<String> classes;
	private ArrayList<String> classesTouched;
	private ArrayList<Ticket> buggyTickets;
	
	public Commit(RevCommit rev, String author, Version version, Date date) {
		super();
		this.rev = rev;
		this.author = author;
		this.version = version;
		this.date = date;
		this.classes = new ArrayList<String>();
		this.classesTouched = new ArrayList<String>();
		this.buggyTickets= new ArrayList<Ticket>();
	}

	public Version getVersion() {
		return version;
	}

	public Date getDate() {
		return date;
	}

	public ArrayList<String> getClasses() {
		return classes;
	}

	public ArrayList<Ticket> getBuggyTickets() {
		return buggyTickets;
	}

	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	public void setBuggyTickets(ArrayList<Ticket> buggyTickets) {
		this.buggyTickets = buggyTickets;
	}

	public String getAuthor() {
		return author;
	}

	public RevCommit getRev() {
		return rev;
	}

	public ArrayList<String> getClassesTouched() {
		return classesTouched;
	}
	
	public void addTouchedClass (String file) {
		classesTouched.add(file);
	}
}
