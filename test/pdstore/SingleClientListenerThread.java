package pdstore;

import java.util.concurrent.Semaphore;

public class SingleClientListenerThread extends ReverseListenerThread {

	public SingleClientListenerThread(PDStore server, Semaphore waitLock) {
		super(server, waitLock);
		// TODO Auto-generated constructor stub
	}
	
	public void reverseListenerCall() {
		serverForThread.newTransactions();
	}


}
