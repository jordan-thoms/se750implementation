package pdstore;

import java.util.ArrayList;
import java.util.List;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

/**   
 * A listener used as View example in tetst
 * Makes a total role out of the role and instance1 provided in the constructor,
 * i.e. all instance2 will match.
 * @author gweb017
 *
 */
public class CompleteRoleViewListener extends PDListenerAdapter<GUID, Object, GUID> {

	GUID role2;
	GUID instance1;
	public int callCount = 0;

	public int getCallCount() {
		return callCount;
	}

	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}

	public CompleteRoleViewListener(GUID role2, GUID instance1) {
		super();
		this.role2 = role2;
		this.instance1 = instance1;
	}

	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		System.out.println("CompleteRoleListener");
		for (PDChange<GUID, Object, GUID> change: matchedChanges) {
			System.out.println("CompleteRoleListener loop");
			if(role2.equals(change.getRole2()) && instance1.equals(change.getInstance1()))
			transaction.add(change);		
		}
	}

}
