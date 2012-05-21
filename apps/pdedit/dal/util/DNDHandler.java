package pdedit.dal.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import pdedit.PDEditLogger;
import pdedit.dal.PDNode;
import pdedit.layout.LayoutAlgorithm;
import pdedit.layout.SimpleLayoutAlgorithm;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

/***
 * Diagram Node Data Handler (DNDHandler) is a class that have two responsiblity
 * The first is keeping the node data up-to-date and the second is to run the 
 * layout engine.
 * 
 * @author tedyeung
 *
 */
public class DNDHandler extends Thread {
	private HashMap<GUID, Object> nodes = new HashMap<GUID, Object>();
	private LayoutAlgorithm engine;

	public DNDHandler(){
		engine = new SimpleLayoutAlgorithm();
	}

	public DNDHandler(LayoutAlgorithm eng){
		engine = eng;
	}


	//This runs the layout engine if any
	public void run() {
		int count = 1;
		if (engine != null){
			while (true){
				System.out.println("Running: "+count);
				engine.solve();
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
			}
		}
	}

	public synchronized void addNode(GUID id, PDNode n){
		nodes.put(id, n);
	}

	public synchronized void removeNode(PDNode n){
		nodes.remove(new GUID(n.getHasInstance()));
	}

	public synchronized Object getNodesByGUID (GUID id){
		return nodes.get(id);
	}

	public synchronized Collection<Object> getListOfNodes(Collection<Object> id){
		HashSet<Object> n = new HashSet<Object>();
		for(Object o : id){
			PDNode temp = (PDNode)nodes.get(o);
			if (temp != null){
				n.add(temp);
			}
		}
		return n;
	}

	public synchronized void loadDiagramMap(ModelAccessor modelAccessor){
		PDEditLogger.addToLog("PDEdit.DNDHandler: Loading Diagram Map . . .");
		PDStore store = modelAccessor.getStore();
		GUID t = store.begin();
		Collection<Object> list = store.getAllInstancesOfType(t, ModelAccessor.NodeTypeGUID);
		PDWorkingCopy cache = new PDSimpleWorkingCopy(store);
		for (Object o : list){
			GUID instance = new GUID((String)store.getInstance(t, o, ModelAccessor.hasInstanceROLEGUID));
			nodes.put(instance, PDNode.load(cache, (GUID)o));

		}
		store.commit(t);
		PDEditLogger.appendDurationToLastMassage();
		PDEditLogger.addToLogWithoutTimestamp(" ("+list.size()+" node instances loaded)");
		PDEditLogger.newLine();
	}
}
