package main.java.milestone1;

import java.util.List;
import java.util.Map;
import main.java.model.ClassInstance;
import main.java.model.Commit;
import main.java.model.Ticket;

public class GetBuggyClass {
	public void setBugginess(List<ClassInstance> instances, List<Commit> commits, Map<String, List<Integer>> mapInst) {
		for (Commit commit : commits) {
			List<String> classes = commit.getClassesTouched();
			for (Ticket ticket : commit.getBuggyTickets()) {
				setTicketBuggyClass(instances, ticket, classes, mapInst);
			}
		}
	}

	private void setTicketBuggyClass(List<ClassInstance> instances, Ticket ticket, List<String> classes, Map<String, List<Integer>> mapInst){
	    	for (String file : classes) {
			List<Integer> idxs = mapInst.get(file);
			for(Integer idx : idxs) {
				ClassInstance instance = instances.get(idx);
				boolean ret = instance.insideAV(ticket.getAv(), ticket.getFv());
				if (ret) instance.setBugginess(true); 
			}
		}
	}

}
