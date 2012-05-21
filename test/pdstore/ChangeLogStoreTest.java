package pdstore;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pdstore.changelog.ChangeLogStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.util.Starter;

public class ChangeLogStoreTest extends TestCase {

	private ChangeLogStore store = new ChangeLogStore(
			"pddata/ChangeLogStoreTest.pds");

	private GUID defaultBranch;

	private GUID roleA = new GUID("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

	@Before
	public void setUp() {
		defaultBranch = store.getRepository().getBranchID();
	}

	@After
	public void tearDown() throws IOException {
	}

	public final void testGetRepository() {
		GUID repository = store.getRepository();
		assertTrue(repository != null);

		GUID repositoryBranch = repository.getBranchID();
		assertTrue(repositoryBranch != null);

		GUID localBranch = new GUID().getBranchID();
		assertTrue(localBranch != null);
	}

	@Test
	public final void testReadWriteStrings() throws IOException {
		// add a transaction
		GUID transactionId = GUID.newTransactionId(defaultBranch);
		Transaction<GUID, Object, GUID> transaction = new Transaction<GUID, Object, GUID>(
				transactionId);
		PDChange<GUID, Object, GUID> c1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, "Ernie", roleA, "Cookies");
		transaction.add(c1);
		store.addTransaction(transaction);

		// see if the changes have been added
		List<PDChange<GUID, Object, GUID>> result = getChanges(store);
		assertTrue(result.size() >= 1);
		assertTrue(result.contains(c1));

		// add another transaction
		transactionId = GUID.newTransactionId(defaultBranch);
		transaction = new Transaction<GUID, Object, GUID>(transactionId);
		PDChange<GUID, Object, GUID> c2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, "Bert", roleA, "Toothbrush");
		transaction.add(c2);
		store.addTransaction(transaction);

		// see if all changes have been added
		result = getChanges(store);
		assertTrue(result.size() >= 2);
		assertTrue(result.contains(c1));
		assertTrue(result.contains(c2));
	}

	@Test
	public final void testMerge() {
		// create timestamp string for the filenames to ensure fresh files
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		String timeString = dateFormat.format(date);

		// add changes c1, c2 to store1
		ChangeLogStore store1 = new ChangeLogStore(
				"pddata/ChangeLogStoreTest1-" + timeString + ".pds");
		GUID transactionId = GUID.newTransactionId(defaultBranch);
		Transaction<GUID, Object, GUID> transaction = new Transaction<GUID, Object, GUID>(
				transactionId);
		PDChange<GUID, Object, GUID> c1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, "Ernie", roleA, "Cookies");
		transaction.add(c1);
		PDChange<GUID, Object, GUID> c2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, "Bert", roleA, "Cookies");
		transaction.add(c2);
		store1.addTransaction(transaction);

		// add changes c3, c4 to store2
		ChangeLogStore store2 = new ChangeLogStore(
				"pddata/ChangeLogStoreTest2-" + timeString + ".pds");
		transactionId = GUID.newTransactionId(defaultBranch);
		transaction = new Transaction<GUID, Object, GUID>(transactionId);
		PDChange<GUID, Object, GUID> c3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, "Ernie", roleA, "Cookies");
		transaction.add(c3);
		PDChange<GUID, Object, GUID> c4 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, "Bert", roleA, "Cookies");
		transaction.add(c4);
		store2.addTransaction(transaction);

		// merge store1 and store2 into store3
		ChangeLogStore store3 = store1.merge(store2,
				"pddata/ChangeLogStoreTest3-" + timeString + ".pds");

		// check if all changes c1, c2, c3, c4 have been merged into store3
		List<PDChange<GUID, Object, GUID>> result = getChanges(store3);
		assertTrue(result.contains(c1));
		assertTrue(result.contains(c2));
		assertTrue(result.contains(c3));
		assertTrue(result.contains(c4));

	}

	List<PDChange<GUID, Object, GUID>> getChanges(
			PDCoreI<GUID, Object, GUID> core) {
		List<PDChange<GUID, Object, GUID>> result = new ArrayList<PDChange<GUID, Object, GUID>>();
		for (PDChange<GUID, Object, GUID> c : core) {
			result.add(c);
		}
		return result;
	}

}
