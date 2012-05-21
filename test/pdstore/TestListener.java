package pdstore;

import java.util.List;
import java.util.concurrent.Semaphore;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

public class TestListener extends PDListenerAdapter<GUID, Object, GUID> {

	public List<PDChange<GUID, Object, GUID>> lastTransaction;
	private Semaphore finished = new Semaphore(1, true);
	
	public TestListener() {
		super();
		waitUntilFinished();
	}


	public void waitUntilFinished() {
		try {
			finished.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		Debug.println("TestListener was called.", "ListenerTest");
		lastTransaction = transaction;
		finished.release();
	}

}
