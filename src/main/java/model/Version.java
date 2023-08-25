package main.java.model;

import java.util.List;
import java.util.Date;

public class Version {
	private long id;
	private String name;
	private Date endDate;
	private Date startDate;
	private int numRel;
	
	public Version(long id, String name, Date endDate) {
		super();
		this.id = id;
		this.name = name;
		this.endDate = endDate;
		this.startDate = null;
		this.numRel = -1;
	}

	/*
	 * Check dates order. v1
	 */
	public boolean isBefore(Date v) {
		if(this.endDate.before(v) || this.endDate.equals(v)) return true;
		return false;
	}
	
	/*
	 * Check dates order. v2
	 */
	public boolean isBefore(Version v) {
		if(this.endDate.before(v.endDate)) return true;
		return false;
	}
	
	public boolean isEqual(Version v) {
	    if(this.endDate.equals(v.endDate)) return true;
	    return false;
	}
	
	public boolean findNumRel(List<Version> vs) {
		for(int i = 0; i < vs.size(); i++) {
			if(vs.get(i).getId() == this.id) {
				this.numRel = i+1;
				return true;
			}
		}
		return false;
	}
	
	public String getName() {
		return name;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getId() {
		return id;
	}

	public int getNumRel() {
		return numRel;
	}

	public void setNumRel(int numRel) {
		this.numRel = numRel;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
