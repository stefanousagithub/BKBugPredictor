package main.java.milestone1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import main.java.model.Version;
import main.java.utils.Utilities;

public class RetrieveVersions {
	private RetrieveVersions() {
		super();
	}
	private static final Logger LOGGER = Logger.getLogger("Analyzer");
	private static Map<LocalDateTime, String> releaseNames;
	private static Map<LocalDateTime, String> releaseID;
	private static List<LocalDateTime> releases;
	private static Integer numVersions;

	public static void GetRealeaseInfo(String projName) throws IOException, JSONException {
	   //Fills the List with releases dates and orders them
	   //Ignores releases with missing dates
	   releases = new ArrayList<LocalDateTime>();
         Integer i;
         String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
         JSONObject json = Utilities.readJsonFromUrl(url);
         JSONArray versions = json.getJSONArray("versions");
         releaseNames = new HashMap<LocalDateTime, String>();
         releaseID = new HashMap<LocalDateTime, String> ();
         for (i = 0; i < versions.length(); i++ ) {
            String name = "";
            String id = "";
            if(versions.getJSONObject(i).has("releaseDate")) {
               if (versions.getJSONObject(i).has("name"))
                  name = versions.getJSONObject(i).get("name").toString();
               if (versions.getJSONObject(i).has("id"))
                  id = versions.getJSONObject(i).get("id").toString();
               addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
                          name,id);
            }
         }
         // order releases by date
         Collections.sort(releases, new Comparator<LocalDateTime>(){
            //@Override
            public int compare(LocalDateTime o1, LocalDateTime o2) {
                return o1.compareTo(o2);
            }
         });
         if (releases.size() < 6) return;
         FileWriter fileWriter = null;
	 try {
            fileWriter = null;
            String outname = projName + "VersionInfo.csv";
				    //Name of CSV for output
				    fileWriter = new FileWriter(outname);
            fileWriter.append("Index,Version ID,Version Name,Date");
            fileWriter.append("\n");
            numVersions = releases.size();
            for ( i = 0; i < releases.size(); i++) {
               Integer index = i + 1;
               fileWriter.append(index.toString());
               fileWriter.append(",");
               fileWriter.append(releaseID.get(releases.get(i)));
               fileWriter.append(",");
               fileWriter.append(releaseNames.get(releases.get(i)));
               fileWriter.append(",");
               fileWriter.append(releases.get(i).toString());
               fileWriter.append("\n");
            }

         } catch (Exception e) {
        	LOGGER.log(Level.SEVERE, "Error in csv writer", e);
            e.printStackTrace();
         } finally {
            try {
               fileWriter.flush();
               fileWriter.close();
            } catch (IOException e) {
               LOGGER.log(Level.SEVERE, "Error while flushing/closing fileWriter !!!", e);
               e.printStackTrace();
            }
         }
   }

   private static void addRelease(String strDate, String name, String id) {
	      LocalDate date = LocalDate.parse(strDate);
	      LocalDateTime dateTime = date.atStartOfDay();
	      if (!releases.contains(dateTime))
	         releases.add(dateTime);
	      releaseNames.put(dateTime, name);
	      releaseID.put(dateTime, id);
	   }
   
	public static List<Version> GetVersions(String pathVersion) throws IOException, JSONException {
		 Pattern pattern = Pattern.compile(",");
	 	 BufferedReader in = new BufferedReader(new FileReader(pathVersion));
	 	 
	 	 // Get versions
		 List<Version> versions = in.lines().skip(1).map(line->{
		    String[] x = pattern.split(line);
		 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		 	Date d = null;
			try {
				d = sdf.parse(x[3]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		    return new Version(Long.parseLong(x[1]), x[2], d);
		 }).collect(Collectors.toList());
		 ObjectMapper mapper = new ObjectMapper();
		 mapper.enable(SerializationFeature.INDENT_OUTPUT);
		 
		 // Set startDate
		 Date d = null;
		 for(int i = 0; i < versions.size(); i++) {
			versions.get(i).setStartDate(d);
			d = versions.get(i).getEndDate();
		 }
		 in.close();
	 	 return versions;
	}
	
	public static Version FindVersion(Date date, List<Version> allVersions){
		 for (Version v : allVersions) {
			 if (!v.isBefore(date)) return v; 
		 }
		 return null;
	 }
}
