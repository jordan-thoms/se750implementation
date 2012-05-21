package texteditor;

import java.util.ArrayList;
import java.util.List;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

public class RoleListener extends PDListenerAdapter<GUID, Object, GUID> {
	
	GUID role2;
	

	public RoleListener(GUID role2) {
		super();
		this.role2 = role2;
	}

	
	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID>  core) {
		// TODO Auto-generated method stub
		List<PDChange<GUID, Object, GUID>> changeListForRole = new ArrayList<PDChange<GUID, Object, GUID>>();
		for(PDChange<GUID, Object, GUID> t:transaction){
			if(t.getRole2() == role2){
				changeListForRole.add(t);
				doChangeAction(t);
			}
		}
	}
	
	public void doChangeAction(PDChange<GUID, Object, GUID> change) {
				System.err.println("Change: " + change);	
	}
}
