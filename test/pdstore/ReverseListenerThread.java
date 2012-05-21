package pdstore;

import java.util.concurrent.Semaphore;

class ReverseListenerThread implements Runnable {

	PDStore serverForThread;
	Semaphore lock;

	public ReverseListenerThread(PDStore server, Semaphore waitLock) {
		super();
		serverForThread = server;
		lock = waitLock;
	}

	public int callCount = 0;

	public void run() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			// no action needed
		}
		System.err.println("reverseListenerTest is running:");
		reverseListenerCall();
		callCount++;
		lock.release();
		System.err.println("reverseListenerTest was notified:");
	}

	public void reverseListenerCall() {
		serverForThread.nextTransaction();
	}
}
