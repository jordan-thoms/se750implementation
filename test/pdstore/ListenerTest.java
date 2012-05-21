package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;
import pdstore.generic.PDChange;


public class ListenerTest extends TestCase {

	// use local embedded store
	protected PDStore store;


	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		
		// create new store without metamodel; give it a timestamp name
		store = new PDStore("PDStoreTest-" + dateFormat.format(date));
	}

	/**	
	 * This method tests for a prepared Change template the 
	 * ListenerDispatcher index structure.
	 * 
	 * 
	 * @param role2Id
	 * @param instance1
	 * @param instance2
	 * @param changeTemplate
	 */
	private void applyTemplate(final GUID role2Id, GUID instance1,
			GUID instance2, PDChange<GUID, Object, GUID> changeTemplate) {
		
		// Step1: test adding the listener with addListener()
		TestListener listener = new TestListener();
		
		store.getListenerDispatcher().addListener(listener, 
				changeTemplate);
		
		testRegisteredListener(role2Id, instance1, instance2, listener);

		// Step2: test adding the listener with add()
		TestListener listener2 = new TestListener();
		
		listener2.getMatchingTemplates().add(changeTemplate);
		store.getListenerDispatcher().add(listener2);
		
		testRegisteredListener(role2Id, instance1, instance2, listener2);
	}

	/**
	 * The generic test that is executed in many different configurations.
	 * 
	 * @param role2Id
	 * @param instance1
	 * @param instance2
	 * @param listener
	 */
	private void testRegisteredListener(final GUID role2Id, GUID instance1,
			GUID instance2, TestListener listener) {
		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role2Id, instance2);
		transaction = store.commit(transaction);
	
		listener.waitUntilFinished();
		assertTrue(listener.lastTransaction != null);
		assertEquals(transaction, listener.lastTransaction.get(0).getTransaction());
		
		listener.lastTransaction = null;
		GUID transaction2 = store.begin();
		//add an arbitrary change that should not activate the listener.
		store.addLink(transaction2, new GUID(), new GUID(), new GUID());
		transaction2 = store.commit(transaction2);
		// Showing that the listener has not been activated for an arbitrary change
		assertEquals(null, listener.lastTransaction);
	}

	public final void testRoleListener() {
		final GUID role2Id = new GUID();
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(null, null, null, role2Id, null);
		applyTemplate(role2Id, instance1, instance2, changeTemplate);
		
	}

	public final void testTripleListener() {
		final GUID role2Id = new GUID();
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		
		PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(null, null, instance1, role2Id, instance2);
		applyTemplate(role2Id, instance1, instance2, changeTemplate);
	}

	public final void testInstance1RoleListener() {
		final GUID role2Id = new GUID();
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(null, null, instance1, role2Id, null);
		
		applyTemplate(role2Id, instance1, instance2, changeTemplate);
	}
	
	public final void testInstance2RoleListener() {
		final GUID role2Id = new GUID();
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		
		PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(null, null, null, role2Id, instance2);
		applyTemplate(role2Id, instance1, instance2, changeTemplate);
	}
	
	public final void testChangeTypeListener() {
		final GUID role2Id = new GUID();
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		
		PDChange<GUID, Object, GUID> changeTemplate = new PDChange<GUID, Object, GUID>(ChangeType.LINK_REMOVED, null, null, role2Id, instance2);
		TestListener listener = new TestListener();	
		store.getListenerDispatcher().addListener(listener, 
				changeTemplate);
			
		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role2Id, instance2);
		store.commit(transaction);
		assertEquals(null, listener.lastTransaction);
		
		transaction = store.begin();
		store.removeLink(transaction, instance1, role2Id, instance2);
		transaction = store.commit(transaction);
		listener.waitUntilFinished();
		assertTrue(listener.lastTransaction != null);
	}

	public final void testListener() {
		// local listener
		GUID transaction = store.begin();
		GUID roleA = new GUID();
		// TestRoleListener t = new TestRoleListener(roleA, false);
		ConsoleWritingListener t = new ConsoleWritingListener(roleA, false);
		store.getDetachedListenerList().add(t);
		GUID guid1 = new GUID();
		GUID guid2 = new GUID();
		store.addLink(transaction, guid1, roleA, guid2);
		assertTrue(t.callCount == 0);
		store.commit(transaction);

		// The following is necessary to give asynchronous listener
		// time to finish
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(t.callCount > 0);
	}

	// only for embedded stores, not remote ones

	public void testInterceptor() {
		GUID roleA = new GUID();
		GUID transaction = store.begin();
		TestRoleListener t = new TestRoleListener(roleA, true);
		store.getInterceptorList().add(t);
		GUID guid1 = new GUID();
		GUID guid2 = new GUID();
		store.addLink(transaction, guid1, roleA, guid2);
		Collection<Object> result = store.getInstances(transaction, guid1,
				roleA);
		assertTrue(result.contains(guid2));
		assertTrue(t.callCount == 0);
		store.commit(transaction);
		assertTrue(t.callCount > 0);
		transaction = store.begin();
		result = store.getInstances(transaction, guid1, roleA);

		// The following must hold true:
		// for that, the interceptors have to delete the cache
		assertTrue(!result.contains(guid2));
	}

}
