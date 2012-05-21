package pdstore;

import java.util.ArrayList;
import java.util.List;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

public class TestRoleListener extends PDListenerAdapter<GUID, Object, GUID> {

	GUID role2;
	public int callCount = 0;
	boolean isInterceptor;

	public int getCallCount() {
		return callCount;
	}

	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}

	public TestRoleListener(GUID role2, boolean isInterceptor) {
		super();
		this.role2 = role2;
		this.isInterceptor = isInterceptor;
	}

	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		List<PDChange<GUID, Object, GUID>> removeList = new ArrayList<PDChange<GUID, Object, GUID>>();
		for (PDChange<GUID, Object, GUID> t : transaction) {
			if (t.getRole2().equals(role2)) {
				++callCount;
				removeList.add(t);
			}
		}
		if (isInterceptor) {
			for (PDChange<GUID, Object, GUID> t : removeList) {
				++callCount;
				transaction.add(new PDChange<GUID, Object, GUID>(ChangeType.LINK_REMOVED, t
						.getTransaction(), t.getInstance1(), role2, t
						.getInstance2()));
			}
		}
	}

}
