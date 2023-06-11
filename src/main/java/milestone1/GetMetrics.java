package main.java.milestone1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import main.java.model.ClassInstance;
import main.java.model.Commit;
import main.java.model.Ticket;
import main.java.model.Version;
import main.java.utils.Utilities;

public class GetMetrics {
	private Git git;
	private ArrayList<ClassInstance> instances;
	
	public GetMetrics() {
		this.instances = new ArrayList<ClassInstance>();
	}
	
	public ArrayList<ClassInstance> getInstances(Git git, ArrayList<Commit> commits, ArrayList<Ticket> tickets, ArrayList<Version> versions, Map<String, ArrayList<Integer>> mapInst) throws IOException{
		this.git = git;
		ArrayList<ClassInstance> temp = new ArrayList<ClassInstance>();
		Map<String, Integer> mapTemp = new HashMap<>();
		ClassInstance inst = null;
		RevCommit prevCommit = null;		
		Version version = versions.get(0);
		for(Commit commit : commits) {
			String author = commit.getAuthor();
			boolean fixCommit = false;
			if(commit.getBuggyTickets().size() != 0) fixCommit = true;
			
			if(version.getName() != commit.getVersion().getName()) {
				updateInstances(mapInst, temp, mapTemp);                
				version = commit.getVersion();
				for (int i = 0; i < temp.size(); i++) {
					ClassInstance t = temp.get(i);
					t.setVersion(version);	
					t.increaseAge();
				}
			}			
			List<DiffEntry> listDe = diff(commit.getRev(), prevCommit);
			for(String file : commit.getClasses()) {
				ArrayList<Edit> edits = getEdits(listDe, file);
				if(edits.size() == 0) continue; 
				commit.addTouchedClass(file);
				
				boolean isPresent = mapTemp.containsKey(file);

				if (isPresent) inst = temp.get(mapTemp.get(file));
				else inst = new ClassInstance(file, version, commit.getDate());
				for(Edit edit : edits) {
					int added = edit.getEndB() - edit.getBeginB();
					int deleted = edit.getEndA() - edit.getBeginA();
					inst.updateInstanceLoc(added, deleted);
				}
				inst.updateInstanceMeta(author, fixCommit);
				if (!isPresent) {
					temp.add(inst);
					mapTemp.put(file, temp.size()-1);
				}
			}
			
			int nCommTogether = commit.getClassesTouched().size();
			for(String file : commit.getClassesTouched()) {
				inst = temp.get(mapTemp.get(file));
				inst.increaseCommittedTogether(nCommTogether);
			}
			
			prevCommit = commit.getRev();
		}
		updateInstances(mapInst, temp, mapTemp);  
		return instances;
	}
	
    private List<DiffEntry> diff(RevCommit newCommit, RevCommit oldCommit) throws IOException {
    	List<DiffEntry> lstDe = null;
    	DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
        df.setRepository(git.getRepository());
        if (oldCommit != null) {
        	lstDe = df.scan(oldCommit.getTree(), newCommit.getTree());
		} else {           // (?) corretto ?
			ObjectReader reader = git.getRepository().newObjectReader();
			AbstractTreeIterator newTree = new CanonicalTreeParser(null, reader, newCommit.getTree());
			AbstractTreeIterator oldTree = new EmptyTreeIterator();
			lstDe = df.scan(oldTree, newTree);
		}
        return lstDe;
    }
    
    private ArrayList<Edit> getEdits(List<DiffEntry> listDe, String file) throws IOException{
    	ArrayList<Edit> edits = new ArrayList<Edit>();
		DiffFormatter df = new DiffFormatter(null);
		df.setRepository(git.getRepository());
		for (DiffEntry diff : listDe) {
			if (diff.toString().contains(file)) {
				df.setDetectRenames(true);
				EditList editList = df.toFileHeader(diff).toEditList();
				
				for (Edit editElement : editList)
					edits.add(editElement);
			} else {
				df.setDetectRenames(false);
			}
		}
		return edits;
    }
    
    private void updateInstances(Map<String, ArrayList<Integer>> mapInst, ArrayList<ClassInstance> temp, Map<String, Integer> mapTemp) {
    	int size = instances.size();
		for(int i = 0; i < temp.size(); i++) {
			String f = temp.get(i).getName();
			mapInst.computeIfAbsent(f, k -> new ArrayList<>()).add(mapTemp.get(f) + size);
		}
		
		instances.addAll(Utilities.clone(temp));
    }
}
