package pdqueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;

import pdqueue.concurrency.PDSConnection;
import pdqueue.concurrency.QueueOperation;
import pdqueue.dal.*;
import pdqueue.tools.ItemState;
import pdqueue.tools.PDItemFactory;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.notify.PDListener;

public class QueueConsole {
	
	PDQueue queue;
	public static void main(String args[]) {
		new QueueConsole();
	}
	
	public QueueConsole() {
		console();
	}
	
	private void console() {
		verification();
	}
	
	private void verification() {
		System.out.println("Searching PDQueue objects ..");
		QueueOperation qo = new QueueOperation();
		Collection<PDQueue> queues = qo.getQueuePool();
		for (PDQueue q : queues) {
			if(q.getName().equals("Request")) {
				queue = q;
				System.out.println("Request queue is found");
			}
		}
		//insertNewItems(qo);
		searchAllItems(qo);
	}

	private void insertNewItems(QueueOperation qo) {
		PDItemFactory factory = new PDItemFactory();
		GUID transaction = qo.newTrasnaction(IsolationLevel.READ_COMMITTED_NONE_BLOCKED);

		
		for (int i = 0; i < 50; i++) {
			PDItem item = factory.createPDItem("Item_" + i, "local", "local", "Message_" + i);
			System.out.println(item.getName());
			qo.insert(transaction, queue, queue.roleItemId, item);
		}
		qo.commit(transaction);
	}
	
	private void searchAllItems(QueueOperation qo) {
		GUID transaction = qo.newTrasnaction(IsolationLevel.READ_UNCOMMITTED);
		PDItem item = qo.searchItem(transaction, ItemState.PENDING, queue);
		if (item == null)
			return;
		System.out.println(item.getName());
		qo.changeState(transaction, item, ItemState.COMPLETED);
		qo.abort(transaction);
		System.out.println(item.getStates().size());
	}
}
