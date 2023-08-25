package main.java.milestone1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.json.JSONException;

import main.java.model.ClassInstance;
import main.java.model.Commit;
import main.java.model.Ticket;
import main.java.model.Version;
import main.java.utils.Parameters;

public class ControllerDB {
	private static final Logger LOGGER = Logger.getLogger("Analyzer");
	private RetrieveCommits RC;
	private RetrieveTicketsID RT;
	private GetMetrics GM;
	private GetBuggyClass GBC;
	
	private Git git;
	String projName;
	String path;
	
	public ControllerDB(String projName) {
		this.projName = projName;
		RC = new RetrieveCommits();
		RT = new RetrieveTicketsID(projName);
		GM = new GetMetrics();
		GBC = new GetBuggyClass();
	}

	public void setProject() {
		try {
			String folderName = projName.toLowerCase();
			path = System.getProperty("user.home");
			File dir = new File(path, folderName);
			dir.mkdir();
			// If is not empty, then refresh the directory
			if (dir.list().length == 0) {
				LOGGER.log(Level.INFO , String.format("clone repository %s inside %s", Parameters.toUrl(projName), path));
				Git.cloneRepository().setURI(Parameters.toUrl(projName)).setDirectory(dir).call();
				git = Git.open(dir);
				LOGGER.log(Level.INFO , "checkout completed");
			// Otherwise checkout the project
			} else {
				git = Git.open(dir);
				git.pull();
				git.checkout();
				LOGGER.log(Level.INFO , String.format("checkout completed %s/%s", path, projName));
			}
		} catch (GitAPIException | IOException e) {
			LOGGER.log(Level.SEVERE, "Error in instantiation phase", e);
		}
	}
	
	public static void main(String[] args) throws IOException, GitAPIException, JSONException, ParseException{
		int size = 0;
		String output;
		List<Commit> commits = null;
		List<Ticket> tickets = null;
		List<Version> versions = null;
		List<ClassInstance> instances = null;
		Map<String, List<Integer>> mapInst = new HashMap<>();

		String projName = Parameters.PROJECT1;        // Change project
		ControllerDB controller = new ControllerDB(projName);
		controller.setProject();

		
		// RetrieveVersions.GetRealeaseInfo(projName);
		output = String.format("Dataset Creation: %s\n", projName);
		LOGGER.log(Level.INFO, output);
		versions = RetrieveVersions.GetVersions(projName + "VersionInfo.csv");
		size = versions.size();
		LOGGER.log(Level.INFO , String.format("Versions: %s" , size));
		tickets = controller.getTickets(versions);
		size = tickets.size();
		LOGGER.log(Level.INFO , String.format("Buggy Tickets (clean): %s", size));
		commits = controller.getCommits(tickets, versions);
		size = commits.size();
		LOGGER.log(Level.INFO , String.format("Commits: %s", size));
		instances = controller.getInstances(commits, versions, mapInst);
		size = instances.size();
		LOGGER.log(Level.INFO , String.format("Instances: %s", size));
		controller.setBugginess(instances, commits, mapInst);
		controller.fillDataset(instances);
	}
	
	public List<Ticket> getTickets(List<Version> versions) throws JSONException, IOException, ParseException{
		return RT.getTickets(versions);
	}
	
	public List<Commit> getCommits( List<Ticket> tickets, List<Version> versions) throws JSONException, IOException, ParseException, GitAPIException{
		return RC.getCommits(git, tickets, versions);
	}
	
	public List<ClassInstance> getInstances(List<Commit> commits, List<Version> versions, Map<String, List<Integer>> mapInst) throws IOException{
		return GM.getInstances(git, commits, versions, mapInst);
	} 
	public void setBugginess(List<ClassInstance> instances, List<Commit> commits, Map<String, List<Integer>> mapInst) {
		GBC.setBugginess(instances, commits, mapInst);
	}
	
	public void fillDataset(List<ClassInstance> instances) {
        FileWriter fileWriter = null;
		 try {
	           String outname = projName + Parameters.DATASET;
		   //Name of CSV for output
		   fileWriter = new FileWriter(outname);
	           fileWriter.append("Version,Name,Size,LocTouched,MaxLocAdded,Churn,MaxChurn,AvgChurn,NR,NFix,NAuth,CommittedTogether,Age,Bugginess");
	           fileWriter.append("\n");
	           for (ClassInstance instance : instances) {
		          int bugginess = instance.isBugginess() ? 1 : 0;
	        	  String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n", instance.getVersion().getName(),
	        			  instance.getName(), Integer.toString(instance.getSize()), Integer.toString(instance.getLocToched()),
	        			  Integer.toString(instance.getMaxLocAdded()), Integer.toString(instance.getChurn()), Integer.toString(instance.getMaxChurn()),
	        			  Integer.toString(instance.getAvgChurn()), Integer.toString(instance.getNR()), Integer.toString(instance.getNFix()), 
	        			  Integer.toString(instance.getNAuth()), Integer.toString(instance.getCommittedTogether()), Integer.toString(instance.getAge()), Integer.toString(bugginess)); 
	              fileWriter.append(line);
	           }
	
	        } catch (Exception e) {
	        	LOGGER.log(Level.SEVERE,"Error in dataset.csv writer",e);
	           e.printStackTrace();
	        } finally {
	           try {
	              fileWriter.flush();
	              fileWriter.close();
	           } catch (IOException e) {
	        	   LOGGER.log(Level.SEVERE,"Error while flushing/closing fileWriter !!!",e);
	              e.printStackTrace();
	           }
	        }
	}
	
}
	


