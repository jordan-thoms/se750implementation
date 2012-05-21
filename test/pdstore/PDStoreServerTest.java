package pdstore;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;

import pdstore.util.Starter;

public class PDStoreServerTest extends PDStoreTest {

	// create two different connections to the same store
	public PDStore store1 = PDStore.connectToServer(null);
	public PDStore store2 = PDStore.connectToServer(null);

	// public PDStore store2 =
	// PDStore.connectToServer("STF-CHRISTOF.sfac.auckland.ac.nz"); // Christof
	// desktop
	// public PDStore store3 = PDStore.connectToServer("130.216.36.87"); //
	// Danver desktop
	

	/** This constant is the delay used to make sure that local and remote objects
	 *   do steps in the intended order.
	 *   If the tests using this constant fail, consider increasing it.
	 */
	public static final int LISTENER_DELAY = 100;
	
	PDStore serverForThread;
	Semaphore lock;
	public int callCount = 0;

	@Before
	public void setUp() {
		store = store1;
		VariableInitialization();
	}

	public final void testReadWriteConcurrently() {
		roleA = new GUID();
		GUID transaction1 = store1.begin();
		GUID transaction2 = store2.begin();
		Collection<Object> result;

		// connection 1
		store1.addLink(transaction1, guid1, roleA, guid2);
		store1.removeLink(transaction1, guid1, roleA, guid3);
		result = store1.getInstances(transaction1, guid1, roleA);
		assertTrue(result.contains(guid2));
		assertTrue(!result.contains(guid3));

		// connection 2
		store2.addLink(transaction2, guid1, roleA, guid3);
		store2.removeLink(transaction2, guid1, roleA, guid2);
		result = store2.getInstances(transaction2, guid1, roleA);
		assertTrue(result.contains(guid3));
		assertTrue(!result.contains(guid2));
		store2.commit(transaction2);

		// check that transaction1 still cannot see transaction2
		result = store1.getInstances(transaction1, guid1, roleA);
		assertTrue(result.contains(guid2));
		assertTrue(!result.contains(guid3));

		store1.commit(transaction1);

		// a new transaction should see committed transaction1 as current
		// database state
		transaction2 = store2.begin();
		result = store.getInstances(transaction2, guid1, roleA);
		assertTrue(result.contains(guid2));
		store2.commit(transaction2);
	}

	public void runMe() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			// no action needed
		}
		System.err.println("Hello! runMe is running:");
		serverForThread.nextTransaction();
		callCount++;
		lock.release();
		System.err.println("runMe was notified:");
	}

	
	public final void testStarterReverseListener() {
		// remote listener:
		Semaphore waitLock = new Semaphore(1, true);
		serverForThread = store;
		lock = waitLock;
		Starter starter = new Starter(this, "runMe");

		try {
			Thread.sleep(LISTENER_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		GUID transaction = store.begin();
		store.addLink(transaction, guid1, roleA, guid2);
		assertTrue(callCount == 0);
		store.commit(transaction);
		// Currently difficult to create delay
		// try {
		// waitLock.acquire();
		// } catch (InterruptedException e) {
		// // no action required
		// }
		try {
			Thread.sleep(LISTENER_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(callCount == 1);
	}

	
	public final void testAdHocReverseListener() {
		// remote listener:
		Semaphore waitLock = new Semaphore(1, true);
		ReverseListenerThread r = new ReverseListenerThread(store, waitLock);
		Thread thread = new Thread(r);
		thread.start();

		try {
			Thread.sleep(LISTENER_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		GUID transaction = store.begin();
		store.addLink(transaction, guid1, roleA, guid2);
		assertTrue(r.callCount == 0);
		store.commit(transaction);
		// currenltly difficult to create delay
		// try {
		// waitLock.acquire();
		// } catch (InterruptedException e) {
		// // no action required
		// }
		try {
			Thread.sleep(LISTENER_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(r.callCount == 1);
	}
	
	public final void testSingleClientListener() {
		// remote listener:
		Semaphore waitLock = new Semaphore(1, true);
		ReverseListenerThread r = new SingleClientListenerThread(store, waitLock);
		Thread thread = new Thread(r);
		thread.start();

		try {
			Thread.sleep(LISTENER_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		GUID transaction = store.begin();
		store.addLink(transaction, guid1, roleA, guid2);
		assertTrue(r.callCount == 0);
		store.commit(transaction);
		// currenltly difficult to create delay
		// try {
		// waitLock.acquire();
		// } catch (InterruptedException e) {
		// // no action required
		// }
		try {
			Thread.sleep(LISTENER_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(r.callCount == 1);
	}



}
