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
		boolean flag = false;
		if(this.endDate.before(v) || this.endDate.equals(v)) flag = true;
		return flag;
	}
	
	/*
	 * Check dates order. v2
	 */
	public boolean isBefore(Version v) {
		boolean flag = false;
		if(this.endDate.before(v.endDate)) flag = true;
		return flag;
	}
	
	public boolean isEqual(Version v) {
		boolean flag = false;
	    if(this.endDate.equals(v.endDate)) flag = true;
	    return flag;
	}
	
	public boolean findNumRel(List<Version> vs) {
		boolean flag = false;
		for(int i = 0; i < vs.size(); i++) {
			if(vs.get(i).getId() == this.id) {
				this.numRel = i+1;
				flag = true;
			}
		}
		return flag;
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
