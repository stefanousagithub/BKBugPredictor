package main.java.milestone1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

public class ControllerDBCreation {
	private static final Logger LOGGER = Logger.getLogger("Analyzer");
	private RetrieveCommits RC;
	private RetrieveTicketsID RT;
	private GetMetrics GM;
	private GetBuggyClass GBC;
	
	private Git git;
	String projName;
	String path;
	
	public ControllerDBCreation(String projName) {
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
				git = Git.cloneRepository().setURI(Parameters.toUrl(projName)).setDirectory(dir).call();
			// Otherwise checkout the project
			} else {
				git = Git.open(dir);
				git.pull();
				git.checkout();
			}
		} catch (GitAPIException | IOException e) {
			LOGGER.log(Level.SEVERE, "Error in instantiation phase", e);
		}
	}
	
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException, JSONException, ParseException{
		ArrayList<Commit> commits = null;
		ArrayList<Ticket> tickets = null;
		ArrayList<Version> versions = null;
		ArrayList<ClassInstance> instances = null;
		Map<String, ArrayList<Integer>> mapInst = new HashMap<>();

		String projName = Parameters.PROJECT1;        // Change project
		ControllerDBCreation controller = new ControllerDBCreation(projName);
		controller.setProject();
		
		//RetrieveVersions.GetRealeaseInfo(projName);
		System.out.println("Dataset Creation: " + projName + "\n");
		versions = RetrieveVersions.GetVersions(projName + "VersionInfo.csv");
		System.out.println("Versions: " + versions.size());
		tickets = controller.getTickets(versions);
		System.out.println("Buggy Tickets (clean): " + tickets.size());
		commits = controller.getCommits(tickets, versions);
		System.out.println("Commits: " + commits.size());
		instances = controller.getInstances(commits, tickets, versions, mapInst);
		System.out.println("Instances: " + instances.size());
		controller.setBugginess(instances, commits, mapInst);
		
		controller.fillDataset(instances);
	}
	
	public ArrayList<Ticket> getTickets(ArrayList<Version> versions) throws JSONException, IOException, ParseException{
		return RT.getTickets(versions);
	}
	
	public ArrayList<Commit> getCommits( ArrayList<Ticket> tickets, ArrayList<Version> versions) throws JSONException, NoHeadException, IOException, ParseException, GitAPIException{
		return RC.getCommits(git, tickets, versions);
	}
	
	public ArrayList<ClassInstance> getInstances(ArrayList<Commit> commits, ArrayList<Ticket> tickets, ArrayList<Version> versions, Map<String, ArrayList<Integer>> mapInst) throws IOException{
		return GM.getInstances(git, commits, tickets, versions, mapInst);
	} 
	public void setBugginess(ArrayList<ClassInstance> instances, ArrayList<Commit> commits, Map<String, ArrayList<Integer>> mapInst) {
		GBC.setBugginess(instances, commits, mapInst);
	}
	
	public void fillDataset(ArrayList<ClassInstance> instances) {
        FileWriter fileWriter = null;
		 try {
	           fileWriter = null;
	           String outname = projName + Parameters.DATASET;
					    //Name of CSV for output
					    fileWriter = new FileWriter(outname);
	           fileWriter.append("Version,Name,Size,LocTouched,MaxLocAdded,Churn,MaxChurn,AvgChurn,NR,NFix,NAuth,CommittedTogether,Age,Bugginess");
	           fileWriter.append("\n");
	           for (ClassInstance instance : instances) {
		          int bugginess = instance.isBugginess() ? 1 : 0;
	        	  String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", instance.getVersion().getName(),
	        			  instance.getName(), Integer.toString(instance.getSize()), Integer.toString(instance.getLocToched()),
	        			  Integer.toString(instance.getMaxLocAdded()), Integer.toString(instance.getChurn()), Integer.toString(instance.getMaxChurn()),
	        			  Integer.toString(instance.getAvgChurn()), Integer.toString(instance.getNR()), Integer.toString(instance.getNFix()), 
	        			  Integer.toString(instance.getNAuth()), Integer.toString(instance.getCommittedTogether()), Integer.toString(instance.getAge()), Integer.toString(bugginess)); 
	              fileWriter.append(line);
	           }
	
	        } catch (Exception e) {
	           System.out.println("Error in dataset.csv writer");
	           e.printStackTrace();
	        } finally {
	           try {
	              fileWriter.flush();
	              fileWriter.close();
	           } catch (IOException e) {
	              System.out.println("Error while flushing/closing fileWriter !!!");
	              e.printStackTrace();
	           }
	        }
	}
	
}
	


