package pdstore.notify;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import pdstore.generic.PDChange;

/**
 * This class implements a service for reverse listeners.
 * A reverse listener is a callee that calls the nextMessage()
 * method. The callee wants this call only to return once a
 * new message has arrived, and being blocked otherwise.
 * 
 * @author gweb017
 *
 * @param <Message>
 */
public class PDStoreListenerService<Message> {

	public PDStoreListenerService() {
       transactionSignalLock();
	}


	private Message recentMessages;
	private Semaphore turnoverSignal = new Semaphore(1, true);
	private Semaphore transactionSignal = new Semaphore(1, true);


	/**
	 * This method performs a blocking wait for the next message.
	 * 
	 * @return the next message
	 */
	public Message nextMessage() {
		turnoverLock();
		turnoverUnLock();
		transactionSignalLock();
		transactionSignalUnLock();
		return recentMessages;
	}

	/**
	 * This method is able to return a list of new messages for the 
	 * caller, if there have been some, and otherwise it should block.
	 * In the future implementation, it should return all new messages
	 * not yet seen by the client. 
	 * Currently it only returns the next new message, i.e it always blocks.
	 * 
	 * @return the list of new messages.
	 */
	public List<Message> newMessages() {
		ArrayList<Message> message = new ArrayList<Message>();
		message.add(nextMessage());
		return message;
	}
	
	/** This method is called by message providers to pass a new message.
	 *  All waiting reverse listeners will be notified.
	 *  
	 * @param message
	 */
	public void serverSideListenerTurnover(
			Message message) {
		turnoverLock();
		// for the moment, no new nextTransactions() can wait directly for the
		// transaction
		processMessages(message);
		transactionSignalUnLock();
		transactionSignalLock();
		// At this point in time, all nextTransaction() calls should be
		// satisfied.
		turnoverUnLock();
	}

	public void processMessages(Message message) {
		recentMessages = message;
	}

	void turnoverLock() {
		try {
			turnoverSignal.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void turnoverUnLock() {
		turnoverSignal.release();
	}

	protected void transactionSignalLock() {
		try {
			transactionSignal.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void transactionSignalUnLock() {
		try {
			transactionSignal.release();
		} catch (Exception e) {
			System.err.println("strange lock not set");
		}
	}
	
}
