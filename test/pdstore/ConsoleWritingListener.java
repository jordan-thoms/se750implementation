package pdstore;

import java.util.ArrayList;
import java.util.List;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

public class ConsoleWritingListener extends PDListenerAdapter<GUID, Object, GUID> {

	GUID role2;
	public int callCount = 0;
	boolean isInterceptor;

	public int getCallCount() {
		return callCount;
	}

	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}

	public ConsoleWritingListener(GUID role2, boolean isInterceptor) {
		super();
		this.role2 = role2;
		this.isInterceptor = isInterceptor;
	}

	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		Debug.println("ConsoleWritingListener calleded", "ListenerTest");
		++callCount;
	}

}
