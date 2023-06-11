package main.java.milestone1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.model.ClassInstance;
import main.java.model.Commit;
import main.java.model.Version;
import main.java.model.Ticket;

public class GetBuggyClass {
	public void setBugginess(ArrayList<ClassInstance> instances, ArrayList<Commit> commits, Map<String, ArrayList<Integer>> mapInst) {
		for (Commit commit : commits) {
			ArrayList<String> classes = commit.getClassesTouched();
			for (Ticket ticket : commit.getBuggyTickets()) {
				setTicketBuggyClass(instances, ticket, classes, mapInst);
			}
		}
	}

	private void setTicketBuggyClass(ArrayList<ClassInstance> instances, Ticket ticket, ArrayList<String> classes, Map<String, ArrayList<Integer>> mapInst){
		int count = 0;
		for (String file : classes) {
			ArrayList<Integer> idxs = mapInst.get(file);
			for(Integer idx : idxs) {
				ClassInstance instance = instances.get(idx);
				boolean ret = instance.insideAV(ticket.getAv(), ticket.getFv());
				if (ret) instance.setBugginess(true); 
				if (ret) count++;  // DEBUG
			}
		}
	}

}
