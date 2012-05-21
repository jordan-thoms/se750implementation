package pdedit.dal.util;

import pdstore.GUID;
import pdstore.PDStore;

public interface PDEditPortal {
	
	PDStore getPDStore();
	String getStoreName();
	String getAppName();
	
	void nodeSelected(GUID id, String type);
	void nodeChanged(GUID id, String type);
	
	void modelSelected(GUID id);
	
	GUID selectedNode();
	GUID changedNode();
}
