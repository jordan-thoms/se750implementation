package pdstore.rmi;

import java.rmi.RemoteException;
import java.util.List;

import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.notify.PDListener;

/**
 * This class is running on the client side in a separate thread, listening for
 * remote changes. This current version is prototypical, a later version must
 * receive a list of transactions.
 * 
 * @author Gerald
 * 
 */
public class ReverseListener implements Runnable {

	PDStore server;

	/**
	 * Receives the local PDStore.
	 * 
	 * @param server
	 */
	public ReverseListener(PDStore server) {
		super();
		this.server = server;
	}

	public void run() {
		// TODO Auto-generated method stub
		for (;;) {
			long time = System.nanoTime();
			List<PDChange<GUID, Object, GUID>> nextTransaction;
			try {
				nextTransaction = server.getServer().nextTransaction();
			} catch (RemoteException e) {
				// capture if the operation was closed by remote host
				// but don't try too often.
				long now = System.nanoTime();
				if (now - time < 10000000) { // was less than 10 ms
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						throw new RuntimeException(e1);

					}
				}
				continue;
			}
			System.err.println("reverse returned");
			System.err.println(nextTransaction.toString());
			System.err.println();

			for (PDListener<GUID, Object, GUID> listener : server
					.getDetachedListenerList()) {
				listener.transactionCommitted(nextTransaction, null, server);
			}
		}
	}

}
