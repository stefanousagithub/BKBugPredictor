package main.java.milestone1;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.java.model.Ticket;
import main.java.model.Version;
import main.java.utils.Utilities;

public class RetrieveTicketsID {
	private final Logger LOGGER = Logger.getLogger("Analyzer");
	private String projName;
	private List<Ticket> tickets;
	int discarded;

	public RetrieveTicketsID(String projName) {
		this.projName = projName;
		this.tickets = new ArrayList<Ticket>();
		discarded = 0;
	}
	 
   public List<Ticket> getTickets(List<Version> allVersions) throws IOException, JSONException, ParseException {
	   getJiraInfo(allVersions);

      // Check realible tickets
	  checkReliableTickets(tickets);

      // Proportion
      proportion(allVersions, tickets);
      LOGGER.log(Level.INFO , "# Discarded tickets: " + discarded);

      return tickets;
  }
   
   private void getJiraInfo(List<Version> allVersions) throws JSONException, IOException, ParseException {
	   Integer j = 0, i = 0, total = 1;
	  //Get JSON API for closed bugs w/ AV in the project
      do {
         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
         j = i + 1000;
         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + i.toString() + "&maxResults=" + j.toString();
         JSONObject json = Utilities.readJsonFromUrl(url);
         JSONArray issues = json.getJSONArray("issues");
         total = json.getInt("total");
         for (; i < total && i < j; i++) {
            //Iterate through each bug
        	 // Get Json file
        	JSONObject issue = issues.getJSONObject(i%1000);
        	JSONObject field = issue.getJSONObject("fields");
        	
        	// Get fields
        	String key = issue.getString("key");
        	long id = issue.getLong("id");
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        	Date resolved = sdf.parse(field.getString("resolutiondate"));
        	Date created = sdf.parse(field.getString("created"));
        	JSONArray versions = field.getJSONArray("versions");
        	
        	// Set affected version
        	Version av = null;
        	if(!versions.isNull(0)) {
        		JSONObject v = versions.getJSONObject(0);
        		if(v.isNull("releaseDate")) continue;
            	SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");
            	Date dateAv = sdfSimple.parse(v.getString("releaseDate"));
        		av = new Version(v.getLong("id"), v.getString("name"), dateAv);
            	av.findNumRel(allVersions);
        	}
        	
        	// Set opening version
        	Version ov = RetrieveVersions.FindVersion(created, allVersions);
        	if (ov == null) {
        		discarded += 1;
        		continue;
        	}
        	ov.findNumRel(allVersions);
        	
        	// Set fixed version 
        	Version fv = RetrieveVersions.FindVersion(resolved, allVersions);
        	if (fv == null) {
        		discarded += 1;
        		continue;
        	}
        	fv.findNumRel(allVersions);
        	tickets.add(new Ticket(id, key, created, resolved, av, ov, fv));
         }  
	   } while (i < total);
   }
   
   private void checkReliableTickets(List<Ticket> tickets) {
	      // Check realible tickets
		  int i = 0;
	      for(i = 0; i < tickets.size(); i++) {
	    	  Ticket t = tickets.get(i);
	    	  if(t.withoutAv()) {
	    		  continue;
	    	  }
	    	  if(t.getOv().isBefore(t.getAv())) {
	    		  discarded++;
	    		  tickets.remove(i+0);
	    		  i -= 1;
	    	  }
	      }
   }
   
   private void proportion(List<Version> allVersions, List<Ticket> tickets) {
	      // PART 1: Calculate the proportion
	      float avSum = 0;
	      float ovSum = 0;
	      float fvSum = 0;
	      float p = 0;
	      for(Ticket t : tickets) {
	    	  if(!t.withoutAv()) {
	    		  if(t.getOv().getName().contains(t.getFv().getName())) continue;
    			  avSum += (float)t.getAv().getNumRel();
	    		  ovSum += (float)t.getOv().getNumRel();
	    		  fvSum += (float)t.getFv().getNumRel();
		    	  p = (fvSum - avSum) / (fvSum - ovSum);
	    	  }
	    	  else t.setAvWithProp(p, allVersions);
	      }

   }
   
}
