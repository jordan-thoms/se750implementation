package pdstore.concurrent;

import java.util.List;

import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.generic.PDStoreI;
import pdstore.generic.Pairable;
import pdstore.notify.PDListener;

public class DetachedListenerDispatcher<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>> implements Runnable  {

	private Thread detachedListenerThread;
	List<PDListener<GUID, Object, GUID>> list;

	PDCoreI<GUID, Object, GUID> core;
	List<PDChange<GUID, Object, GUID>> changes;

	public DetachedListenerDispatcher(
			List<PDListener<GUID, Object, GUID>> list,
			PDCoreI<GUID, Object, GUID> core,
			List<PDChange<GUID, Object, GUID>> changes) {
		super();
		this.list = list;
		this.core = core;
		this.changes = changes;
		
		detachedListenerThread = new Thread(this);
		detachedListenerThread.start();

	}
	

	@Override
	public void run() {
		for (PDListener<GUID, Object, GUID> listener : list) {
			listener.transactionCommitted(changes, changes, core);
		}
	}

}
