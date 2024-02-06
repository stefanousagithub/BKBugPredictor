package main.java.milestone1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.json.JSONException;

import main.java.model.Commit;
import main.java.model.Ticket;
import main.java.model.Version;

public class RetrieveCommits {
 //private final Logger LOGGER = Logger.getLogger("Analyzer");
   private Git git;
   private ArrayList<Commit> commits;
   
   public RetrieveCommits() {
		this.commits = new ArrayList<Commit>();
   }
   
   public List<Commit> getCommits(Git git, List<Ticket> tickets, List<Version> versions) throws IOException, JSONException, GitAPIException {	   	
	    this.git = git;
		Iterable<RevCommit> log = git.log().call();
		for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
			  RevCommit rev = iterator.next();			 
			  // Take author
			  PersonIdent pi = rev.getAuthorIdent();
			  String author = pi.getName();
			  Date creationTime = pi.getWhen();
			  
			  // Take Version
			  Version version = RetrieveVersions.FindVersion(creationTime, versions);
			  
			  // Exclude commits outside our range: 2017
			  if(version == null) continue;
			  		  
			  Commit commit = new Commit(rev, author, version, creationTime);
			  
			  // Take Classes
			  List<String> classes = getFilesCommit(rev);
			  
			  
			  // Take tickets
			  List<Ticket> buggyTickets = getBuggyTickets(rev, tickets);
			  
			  commit.setClasses(classes);
			  commit.setBuggyTickets(buggyTickets);
			  
			  commits.add(commit);
		}
		
		// Ascending order
		Collections.reverse(commits);
		Collections.sort(commits, new Comparator<Commit>() {
		    @Override
		    public int compare(Commit z1, Commit z2) {
		        if (!z1.getDate().before(z2.getDate()))
		            return 1;
		        else
		            return -1;
		    }
		});
		
		return commits;
	}
   
   private List<Ticket> getBuggyTickets(RevCommit commit, List<Ticket> tickets){
	   List<Ticket> buggyTickets = new ArrayList<>();
	   String msg = commit.getFullMessage();
	   for(Ticket ticket : tickets) {
		   if(msg.contains(ticket.getKey())) buggyTickets.add(ticket);
	   }
	   return buggyTickets;
   }
   
   private List<String> getFilesCommit(RevCommit commit) throws  IOException {
   	List<String> affectedFiles = new ArrayList<>();
   	ObjectId treeId = commit.getTree().getId();
   	TreeWalk treeWalk = new TreeWalk(git.getRepository());
		treeWalk.reset(treeId);
		while (treeWalk.next()) {
			if (treeWalk.isSubtree()) {
				treeWalk.enterSubtree();
			} else {
				if (treeWalk.getPathString().endsWith(".java")) {
					String fileToAdd = treeWalk.getPathString();
					affectedFiles.add(fileToAdd);
				}	
			}
		}
		return affectedFiles;
   }   
}
