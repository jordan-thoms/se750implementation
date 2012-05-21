package pdstore.rmi;

import java.util.List;



import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;
import pdstore.notify.PDStoreListenerService;

public class ServerSideMainListener extends PDListenerAdapter<GUID, Object, GUID> {
	
	PDStoreListenerService<List<PDChange<GUID, Object, GUID>>> store;


	public ServerSideMainListener(PDStoreListenerService<List<PDChange<GUID, Object, GUID>>> service) {
		super();
		this.store = service;
	}


	
	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID>  core) {
		store.serverSideListenerTurnover(transaction);
	}

}
