package main.java.model;

import java.util.ArrayList;
import java.util.Date;
/*
 * Ticket class: Container for relevant information about JIRA tickets.  
 */
public class Ticket {
	private long id;
	private String key;
	private Date created;
	private Date resolDate;
	private Version av;
	private Version ov;
	private Version fv;

	
	public Ticket(long id, String key, Date created, Date resolDate, Version av,  Version ov, Version fv) {
		this.id = id;
		this.key = key;
		this.created = created;
		this.resolDate = resolDate;
		this.av = av;
		this.ov = ov;
		this.fv = fv;
	}

	/*
	 * Check presence Affected Version
	 */
	public boolean withoutAv() {
		if (this.av == null) return true;
		else return false;
	}
	
	/*
	 * Set Affected Version from proportion
	 */
	public void setAvWithProp(float prop, ArrayList<Version> allVersions) {
		float ovRel = (float) ov.getNumRel();
		float fvRel = (float) fv.getNumRel();
		float posFloat = fvRel - (fvRel - ovRel) * prop;
		//if(posFloat == fvRel) posFloat -= 1;
		int pos = Math.round(posFloat);
		if(pos < 1) pos = 1;
		av = allVersions.get(pos-1);
		av.setNumRel(pos);
	}
	
	public Version getOv() {
		return ov;
	}

	public void setOv(Version ov) {
		this.ov = ov;
	}

	public Date getResolDate() {
		return resolDate;
	}

	public void setResolDate(Date resolDate) {
		this.resolDate = resolDate;
	}

	public long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public Version getAv() {
		return av;
	}

	public Date getCreated() {
		return created;
	}

	public Version getFv() {
		return fv;
	}
}
