package pdqueue.concurrency;

import java.util.List;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

public class QueueListener extends PDListenerAdapter<GUID, Object, GUID>{
	
public List<PDChange<GUID, Object, GUID>> lastTransaction;

	GUID role;
	int linkAdded;
	int linkRemoved;
	int sizeOfCurrentQueue;
	
	public QueueListener(GUID role) {
		super();
		this.role = role;
		linkAdded = 0;
		linkRemoved = 0;
		sizeOfCurrentQueue = 0;
	}
	
	@Override
	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		System.err.println("TestListener was called.");
		
		for (PDChange<GUID, Object, GUID> t : transaction) {
			if (t.getRole2().equals(role)) {
				switch (t.getChangeType()) {
					case LINK_ADDED: linkAdded++; break;
					case LINK_REMOVED: linkRemoved++; break;
				}
			}
		}
		sizeOfCurrentQueue = linkAdded - linkRemoved;
		System.err.println("Size of the Queue: " + sizeOfCurrentQueue);
		if (queueIsNotEmpty()) {
			System.err.println("The queue is not empyt. All workers should wake up.");
		}
	}
	
	public boolean queueIsNotEmpty() {
		return sizeOfCurrentQueue != 0;
	}
		 
}
