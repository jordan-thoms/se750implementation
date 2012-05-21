/**
 * 
 */
package pdqueue.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.tools.javac.code.Type;

import pdqueue.dal.*;
import pdqueue.tools.Filter;
import pdqueue.tools.ItemState;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.dal.*;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.ListenerDispatcher;
import pdstore.notify.PDListener;
import pdstore.*;


/**
 * @author mcai023
 *
 */
public class QueueOperation implements Operable{

	private PDStore store;		
	private PDSimpleWorkingCopy copy;
	private Collection<GUID> liveTransactions;
	private Collection<PDQueue> queuePool;
	
	public Collection<PDQueue> getQueuePool() {
		return queuePool;
	}

	public QueueOperation() {
		store = PDSConnection.getStore();
		copy = PDSConnection.getNewCopy();
		liveTransactions = new HashSet<GUID>();
		queuePool = new HashSet<PDQueue>();
		fillPool();
	}
	
	private void fillPool() {
		Collection<PDInstance> allQueueInstances = copy.getAllInstancesOfType(PDQueue.typeId);
		System.err.println("There are " + allQueueInstances.size() + " instances of PDQueue");
		for (PDInstance pdi : allQueueInstances) {
			queuePool.add((PDQueue)pdi);
		}
		copy.commit();
	}
	
	public GUID newTrasnaction(IsolationLevel isolationLevel) {
		GUID transaction = store.begin(store.getRepository(), isolationLevel);
		liveTransactions.add(transaction);
		return transaction;
	}
	
	public boolean commit(GUID transaction) {
		if(!liveTransactions.remove(transaction)) {
			System.err.println("Transaction " + transaction + " does not exist!");
			return false;
		}
		store.commit(transaction);
		return true;
	}
	
	public boolean abort(GUID transaction) {
		if(!liveTransactions.remove(transaction)) {
			System.err.println("Transaction " + transaction + " does not exist!");
			return false;
		}
		store.rollback(transaction);
		return true;
	}
	
	public void insert(GUID transaction, PDInstance instance1, GUID role,
			PDInstance instance2) {
		store.addLink(transaction, instance1.getId(), role, instance2.getId());
	}
	
	public void remove(GUID transaction, PDItem item, PDQueue queue) {
			store.removeLink(transaction, queue.getId(), PDQueue.roleItemId, item.getId());
	}
	
	public PDItem searchItem(GUID transaction, ItemState currentState,
			PDQueue queue) {
		Collection<Object> result = store.getInstances(transaction, queue.getId(), queue.roleItemId);
		Collection<PDItem> filteredResult = new ArrayList<PDItem>();
		filteredResult = Filter.filterAndSort(result, ItemState.PENDING);
		PDItem item = null;
		if (filteredResult.size() > 0) {
			item = filteredResult.iterator().next();
		}
		return item;
	}
	
	public String readState(GUID transaction, PDItem item) {
		Object state = store.getInstance(transaction, item, PDItem.roleStateId);
		System.out.println(state.getClass().toString());
		return state.toString();
	}

	@Override
	public void changeState(GUID transaction, PDItem item, ItemState newState) {
		// store.setLink does not work ..
		copy.setTransactionId(transaction);
		item.setState(newState.toString());
	}
}
