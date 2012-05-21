package pdstore.applications;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

public class ComponentRoleListener extends PDListenerAdapter<GUID, Object, GUID> {
	
	public ComponentRoleListener(GUID role2, Component comp) {
		super();
		this.role2 = role2;
		this.comp = comp;
		role2partner = role2.getPartner();
	}

	GUID role2;
	Component comp;
	GUID role2partner;

	
	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		boolean needsRepaint = false;
		for (PDChange<GUID, Object, GUID> change : transaction) {
			if (change.getChangeType() == ChangeType.LINK_ADDED
					&& (change.getRole2() == role2 || change.getRole2() == role2partner)) {
				needsRepaint = true;
				System.err.println("Change: " + change);
			}
		}
		if (needsRepaint)
			comp.repaint();
	}
}
