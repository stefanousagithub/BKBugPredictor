package main.java.milestone1;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.json.JSONException;

import main.java.model.Commit;
import main.java.model.Ticket;
import main.java.model.Version;
import main.java.utils.Utilities;

public class RetrieveCommits {
   private Git git;
   private ArrayList<Commit> commits;
   
   public RetrieveCommits() {
		this.commits = new ArrayList<Commit>();
   }
   
   public ArrayList<Commit> getCommits(Git git, ArrayList<Ticket> tickets, ArrayList<Version> versions) throws IOException, JSONException, ParseException, NoHeadException, GitAPIException {	   	
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
			  ArrayList<String> classes = getFilesCommit(rev);
			  
			  
			  // Take tickets
			  ArrayList<Ticket> buggyTickets = getBuggyTickets(rev, tickets);
			  
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
   
   private ArrayList<Ticket> getBuggyTickets(RevCommit commit, ArrayList<Ticket> tickets){
	   ArrayList<Ticket> buggyTickets = new ArrayList<>();
	   String msg = commit.getFullMessage();
	   for(Ticket ticket : tickets) {
		   if(msg.contains(ticket.getKey())) buggyTickets.add(ticket);
	   }
	   return buggyTickets;
   }
   
   private ArrayList<String> getFilesCommit(RevCommit commit) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
   	ArrayList<String> affectedFiles = new ArrayList<>();
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
