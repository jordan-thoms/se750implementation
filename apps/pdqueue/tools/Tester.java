package pdqueue.tools;

import java.util.Collection;

import pdqueue.dal.PDItem;
import pdqueue.dal.PDQueue;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;

public class Tester {
	public static void main(String args[]) {
		new Tester().go();
	}
	
	public void go() {
		PDStore store = new PDStore("QueueBase");
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		
		Collection<PDInstance> result = copy.getAllInstancesOfType(PDQueue.typeId);
		for (PDInstance o : result) {
			System.err.println(o.toString() + " " + o.getName());
		}
	}
}

