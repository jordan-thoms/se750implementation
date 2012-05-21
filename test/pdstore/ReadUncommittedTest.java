package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdqueue.CreatePDQueueModel;
import pdqueue.concurrency.PDSConnection;
import pdqueue.dal.*;
import pdqueue.tools.Status;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;

import junit.framework.TestCase;

/**
 * Testing the READ_UNCOMMITTED Isolation level
 * TODO: consider merging with ReadCommitted Test and use parameterized test example.
 * 
 * @author gweb017
 * @author clut002
 * @author mcai023
 */
public class ReadUncommittedTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Debug.removeDebugTopic("ReadUncommitted");
	}

	// use local embedded store
	protected PDStore store;
	protected PDSimpleWorkingCopy copy;

	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		// create new store without metamodel; give it a timestamp name
		store = new PDStore("PDStoreTest-" + dateFormat.format(date));
	}

	public final void testReadWriteLink_GUID_GUID() {
		GUID t1 = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID t2 = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID t3 = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID t4 = store.begin(store.getRepository(), IsolationLevel.SNAPSHOT);
		GUID guid1 = new GUID();
		GUID guid2 = new GUID();
		GUID guid3 = new GUID();
		GUID guid4 = new GUID();
		GUID roleA = new GUID();

		assertEquals(t3.getBranchID(), t4.getBranchID());
		assertEquals(t2.getBranchID(), t4.getBranchID());
		assertEquals(t1.getBranchID(), t4.getBranchID());

		// READ UNCOMMITTED transactions
		store.addLink(t1, guid1, roleA, guid2);
		store.addLink(t2, guid1, roleA, guid3);
		// SNAPSHOT transaction
		store.addLink(t4, guid1, roleA, guid4);

//		Debug.addDebugTopic("ReadUncommitted");

		Collection<Object> resultReadUncommitted = store.getInstances(t3, guid1, roleA);
		Collection<Object> resultSNAPSHOT = store.getInstances(t4, guid1, roleA);
		
		Debug.println("assert", "ReadUncommitted");
		assertTrue(resultSNAPSHOT.contains(guid4));
		Debug.println("assert", "ReadUncommitted");
		assertTrue(!resultSNAPSHOT.contains(guid3));
		Debug.println("assert", "ReadUncommitted");
		assertTrue(!resultSNAPSHOT.contains(guid2));
		
		Debug.println("assert", "ReadUncommitted");
		assertTrue(resultReadUncommitted.contains(guid4));
		Debug.println("assert", "ReadUncommitted");
		assertTrue(resultReadUncommitted.contains(guid3));
		Debug.println("assert", "ReadUncommitted");
		assertTrue(resultReadUncommitted.contains(guid2));
//		Debug.removeDebugTopic("ReadUncommitted");


		store.commit(t1);
		store.commit(t2);
		store.commit(t3);
		store.commit(t4);

		// Read links after commit
		GUID tRU = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID tSS = store.begin(store.getRepository(), IsolationLevel.SNAPSHOT);

		resultReadUncommitted = store.getInstances(tRU, guid1, roleA);
		resultSNAPSHOT = store.getInstances(tSS, guid1, roleA);
		assertTrue(resultReadUncommitted.contains(guid2) && resultReadUncommitted.contains(guid3)
				&& resultReadUncommitted.contains(guid4));
		assertTrue(resultSNAPSHOT.contains(guid4) && resultSNAPSHOT.contains(guid3)
				&& resultSNAPSHOT.contains(guid2));
	}

	public final void testReadWriteLinkFromCache() {
		PDSimpleWorkingCopy copy1 = new PDSimpleWorkingCopy(store);
		PDSimpleWorkingCopy copy2 = new PDSimpleWorkingCopy(store);
		GUID t1 = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID t2 = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID t3 = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID t4 = store.begin(store.getRepository(), IsolationLevel.SNAPSHOT);
		PDItem item = new PDItem(copy1);
		String one = "one";
		String two = "two";
		String three = "three";

		// READ UNCOMMITTED transactions
		store.addLink(t1, item.getId(), PDItem.roleMessageId, one);
		store.addLink(t2, item.getId(), PDItem.roleMessageId, two);
		// SNAPSHOT transaction
		store.addLink(t4, item.getId(), PDItem.roleMessageId, three);

		copy1.setTransactionId(t3);
		Collection<Object> resultReadUncommitted = copy1.getInstances(item, PDItem.roleMessageId);
		copy2.setTransactionId(t4);
		Collection<Object> resultSNAPSHOT = copy2.getInstances(item, PDItem.roleMessageId);

		assertTrue(resultSNAPSHOT.contains(three));
		assertTrue( !resultSNAPSHOT.contains(two));
		assertTrue( !resultSNAPSHOT.contains(one));
		assertTrue(resultReadUncommitted.contains(one));
		assertTrue( resultReadUncommitted.contains(two));
		assertTrue( resultReadUncommitted.contains(three));

		store.commit(t1);
		store.commit(t2);
		// Commit Transaction t3 and t4 by using cache copy1 and copy2
		copy1.commit();
		copy2.commit();

		// Read links after commit
		GUID tRU = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		GUID tSS = store.begin(store.getRepository(), IsolationLevel.SNAPSHOT);
		copy1.setTransactionId(tRU);
		resultReadUncommitted = copy1.getInstances(item, PDItem.roleMessageId);
		copy2.setTransactionId(tSS);
		resultSNAPSHOT = copy2.getInstances(item, PDItem.roleMessageId);
		assertTrue(resultReadUncommitted.contains(one) && resultReadUncommitted.contains(two)
				&& resultReadUncommitted.contains(three));
		assertTrue(resultSNAPSHOT.contains(three) && resultSNAPSHOT.contains(two)
				&& resultSNAPSHOT.contains(one));
	}
}
