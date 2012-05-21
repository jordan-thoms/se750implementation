package diagrameditor;

import java.awt.Component;
import java.util.List;

import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListenerAdapter;

public class RepaintListener extends
		PDListenerAdapter<GUID, Object, GUID> {
	
	Component component;

	public RepaintListener(Component component) {
		super();
		this.component = component;
	}
	
	@Override
	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {

//      TODO: is the following necessary?
//		if (NETWORK_ACCESS)
//			workingCopy.commit();

		component.repaint();

	}


}
