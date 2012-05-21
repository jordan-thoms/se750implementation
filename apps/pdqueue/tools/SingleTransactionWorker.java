package pdqueue.tools;

import pdqueue.concurrency.PDQException;
import pdqueue.concurrency.QueueOperation;
import pdqueue.dal.PDItem;
import pdstore.GUID;
import pdstore.IsolationLevel;

public class SingleTransactionWorker extends Thread implements Worker<GUID>, Dispatcher<GUID>{

	private QueueOperation q;
	
	@Override
	public void setConnection() {
		q = new QueueOperation();
	}
	
	@Override
	public GUID next() {
		return null;
	}

	@Override
	public void dequeue(GUID id) {
		// TODO Auto-generated method stub
		
	}
	
	public void run() {
		setConnection();
		q.newTrasnaction(IsolationLevel.READ_UNCOMMITTED);

		
    }

}
