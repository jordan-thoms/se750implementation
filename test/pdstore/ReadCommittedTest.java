package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import pdqueue.CreatePDQueueModel;
import pdqueue.concurrency.PDSConnection;
import pdqueue.dal.*;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;

import junit.framework.TestCase;

/**
 * Testing the READ_COMMITTED_NONE_BLOCKED Isolation level
 * 
 * @author gweb017
 * @author clut002
 * 
 */
public class ReadCommittedTest extends TestCase {

	// use local embedded store
	protected PDStore store;
	protected PDSimpleWorkingCopy copy;

	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		// create new store without metamodel; give it a timestamp name
		store = new PDStore("PDStoreTestx-" + dateFormat.format(date));
	}

	public final void testReadWriteLink_GUID_GUID() {
		GUID reader = store.begin(store.getRepository(),
				IsolationLevel.READ_COMMITTED_NONE_BLOCKED);
		GUID t2 = store.begin(store.getRepository(),
				IsolationLevel.READ_UNCOMMITTED);
		GUID t3 = store.begin(store.getRepository(), IsolationLevel.SNAPSHOT);
		
		GUID guid1 = new GUID();
		GUID guid2 = new GUID();
		GUID guid3 = new GUID();
		GUID guid4 = new GUID();
		GUID roleA = new GUID();

		// READ COMMITTED transactions
		// reader:  guid1 -- roleA -- guid2 
		// t2:      guid1 -- roleA -- guid3 
		// t3:      guid1 -- roleA -- guid4


		// READ_COMMITTED_NONE_BLOCKED
		store.addLink(reader, guid1, roleA, guid2);
		// Read_UNCOMMITTED
		store.addLink(t2, guid1, roleA, guid3);
		// SNAPSHOT
		store.addLink(t3, guid1, roleA, guid4);

		Collection<Object> resultReadCommitted = store.getInstances(reader, guid1,
				roleA); // only changes done in 'reader' should be returned
		
		assertTrue("Could  see the uncommitted changes of itself",
				resultReadCommitted.contains(guid2));
		assertFalse(
				"Uncommitted changes of other transaction should not be observable to the READ_COMMITTED transaction",
				resultReadCommitted.contains(guid3));
		assertFalse(
				"Uncommitted changes of other transactions were observable to the READ_COMMITTED transaction",
				resultReadCommitted.contains(guid4));

		Collection<Object> resultReadCommitted2 = store.getInstances(null, guid1,
				roleA); // only changes done in 'reader' should be returned
		assertFalse(
				"Uncommitted changes of other transactions were observable to the null transaction",
				resultReadCommitted2.contains(guid3));
		assertFalse(
				"Uncommitted changes of other transactions were observable to the null transaction",
				resultReadCommitted2.contains(guid4));

		
		store.commit(t2);
		store.commit(t3);
		
		Collection<Object> resultReadCommitted21 = store.getInstances(null, guid1,
				roleA); // only changes done in 'reader' should be returned
		assertTrue(
				"committed changes of other transactions were not observable to the null transaction",
				resultReadCommitted21.contains(guid3));
		assertTrue(
				"committed changes of other transactions were not observable to the null transaction",
				resultReadCommitted21.contains(guid4));
		
		Collection<Object> resultReadCommitted3 = store.getInstances(reader, guid1,
					roleA); // changes done in 'reader', 't1' and 't2' should be returned
		assertTrue(resultReadCommitted3.contains(guid2));
		assertTrue(resultReadCommitted3.contains(guid3));
		assertTrue(resultReadCommitted3.contains(guid4));
		
		store.commit(reader);

		// After all transactions committed, transaction t4 should see all
		// changes that have been done previously
		GUID t4 = store.begin(store.getRepository(),
				IsolationLevel.READ_COMMITTED_NONE_BLOCKED);
		Collection<Object> resultReadCommittedAfterCommit = store.getInstances(
				t4, guid1, roleA);
		assertTrue(resultReadCommittedAfterCommit.contains(guid2));
		assertTrue(resultReadCommittedAfterCommit.contains(guid3));
		assertTrue(resultReadCommittedAfterCommit.contains(guid4));
	}
}
