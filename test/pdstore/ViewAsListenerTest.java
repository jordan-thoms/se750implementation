package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;
import pdstore.generic.PDChange;


public class ViewAsListenerTest extends TestCase {

	// use local embedded store
	protected PDStore store;


	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		
		// create new store without metamodel; give it a timestamp name
		store = new PDStore("ViewAsListenerTest-" + dateFormat.format(date));
	}

	public final void testInstance1RoleListener() {
		final GUID role2Id = new GUID();
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		//PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(null, null, instance1, role2Id, null);
		PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(ChangeType.LINK_ADDED, null, null, null, null);
		
		CompleteRoleViewListener listener = new CompleteRoleViewListener(role2Id, instance1);
		store.getViewDispatcher().addListener(listener, changeTemplate);
		
		GUID transaction = store.begin();
		PDChange<GUID, Object, GUID> changeTemplate2 = 
				new PDChange<GUID, Object, GUID>(ChangeType.LINK_ADDED, transaction, instance1, role2Id, instance2);
		Collection<PDChange<GUID, Object, GUID>> result = store.getChanges(changeTemplate2);
    	assertTrue(!result.isEmpty());

		PDChange<GUID, Object, GUID> changeTemplate3 = 
				new PDChange<GUID, Object, GUID>(ChangeType.LINK_ADDED, transaction, instance2, role2Id, instance2);
		Collection<PDChange<GUID, Object, GUID>> result3 = store.getChanges(changeTemplate3);
    	assertTrue(result3.isEmpty());

	}
	


}
