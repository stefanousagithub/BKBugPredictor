package main.java.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.java.model.ClassInstance;

public class Utilities {
   public static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONArray json = new JSONArray(jsonText);
         return json;
       } finally {
         is.close();
       }
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	         String jsonText = readAll(rd);
	         JSONObject json = new JSONObject(jsonText);
	         return json;
	       } finally {
	         is.close();
	       }
	   }
	   
   public static boolean IsContain(String source, String subItem){
		// Analizing exact matching for strings
	    String pattern = "\\b"+subItem+"\\b";
	    Pattern p=Pattern.compile(pattern);
	    Matcher m=p.matcher(source);
	    return m.find();
   }  
   
   public static Date IntToDate(int d) throws ParseException {
	   System.out.println(d);
	   SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
	   Date date = originalFormat.parse(String.valueOf(d));
	   return date;
   }
   
	public static List<ClassInstance> clone(List<ClassInstance> list){
		List<ClassInstance> clonedList = new ArrayList<>();
		for(ClassInstance c : list) {
			clonedList.add(new ClassInstance(c.getName(), c.getVersion(), c.getDateCreation(), c.getNR(),
					c.getNFix(), c.getAuthors(), c.getNAuth(), c.getSize(), c.getLocToched(),
						c.getChurn(), c.getMaxChurn(), c.getMaxLocAdded(), c.getAvgChurn(), c.getCommittedTogether(), c.getAge(),
							c.isBugginess()));
		}
		return clonedList;
	}
}
