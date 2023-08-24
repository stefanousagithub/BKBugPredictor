package main.java.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {
	private RevCommit rev;
	private String author;
	private Version version;
	private Date date;
	private List<String> classes;
	private List<String> classesTouched;
	private List<Ticket> buggyTickets;
	
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

	public List<String> getClasses() {
		return classes;
	}

	public List<Ticket> getBuggyTickets() {
		return buggyTickets;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public void setBuggyTickets(List<Ticket> buggyTickets) {
		this.buggyTickets = buggyTickets;
	}

	public String getAuthor() {
		return author;
	}

	public RevCommit getRev() {
		return rev;
	}

	public List<String> getClassesTouched() {
		return classesTouched;
	}
	
	public void addTouchedClass (String file) {
		classesTouched.add(file);
	}
}
