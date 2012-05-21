package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class BranchTest extends TestCase {

	public PDStore store;

	public GUID guid1 = new GUID("11111111111111111111111111111111");
	public GUID guid2 = new GUID("22222222222222222222222222222222");

	public GUID roleA = new GUID("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	public GUID roleB = new GUID("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		// create the usual store including the metamodel by default
		// IMPORTANT: Leave this a separate filename.
		store = new PDStore("BranchTest-" + dateFormat.format(date));
	}

	/**
	 * create a new branch from default branch without meging it back to default
	 * store 99 and 98 in default branch store 97 in new branch retrieve data
	 * from different branches
	 */
	public final void testTwoBranchesWithoutMerging() {
		GUID transaction = store.begin();
		store.addLink(transaction, guid1, roleA, 99);
		GUID parentTransaction = store.commit(transaction);

		Collection<Object> resultInDefaultBranch1 = store.getInstances(
				parentTransaction, guid1, roleA);
		assertTrue(resultInDefaultBranch1.contains(99L)
				|| resultInDefaultBranch1.contains(99));

		GUID BranchBTransaction = store.branch(parentTransaction);
		BranchBTransaction = store.commit(BranchBTransaction);
		// retrieve data at branch transaction
		// the result is expect the same as the transaction that stored the data
		Collection<Object> BranchTransactionResult = store.getInstances(
				BranchBTransaction, guid1, roleA);
		assertTrue(BranchTransactionResult.contains(99L)
				|| BranchTransactionResult.contains(99));

		GUID BranchBID = BranchBTransaction.getBranchID();
		GUID newTransactionOnDefaultBranch = store.begin();
		store.addLink(newTransactionOnDefaultBranch, guid1, roleA, 98);
		parentTransaction = store.commit(newTransactionOnDefaultBranch);

		Collection<Object> resultInDefaultBranch2 = store.getInstances(
				parentTransaction, guid1, roleA);
		// the collection should contain only Default Branch data
		// e.g. result should contain 99 and 98
		assertTrue(resultInDefaultBranch2.contains(99L)
				|| resultInDefaultBranch2.contains(99));
		assertTrue(resultInDefaultBranch2.contains(98L)
				|| resultInDefaultBranch2.contains(98));

		// store new data in Branch B
		GUID BranchBTransaction2 = store.begin(BranchBID,
				IsolationLevel.SNAPSHOT);
		store.addLink(BranchBTransaction2, guid1, roleA, 97);
		BranchBTransaction = store.commit(BranchBTransaction2);

		// the collection should contain only Branch B data and the data that
		// before branching operation
		// e.g result should contains 99 before branching operation and 97 is
		// stored in Branch B
		Collection<Object> resultInBranchB2 = store.getInstances(
				BranchBTransaction, guid1, roleA);
		assertTrue(resultInBranchB2.contains(99L)
				|| resultInBranchB2.contains(99));
		assertTrue(resultInBranchB2.contains(97L)
				|| resultInBranchB2.contains(97));
		assertFalse(resultInBranchB2.contains(98L)
				|| resultInBranchB2.contains(98));
	}

	/**
	 * create a new branch from default branch with meging it back to
	 * default(only up to merging transaction) store B and C in default branch
	 * store D in new branch retrieve data from different branches, and merging
	 * transaction
	 */

	public final void testTwoBranchesWithMerging() {
		GUID defaultTransaction = store.begin();
		store.addLink(defaultTransaction, "A", roleB, "B");
		GUID parentTransaction = store.commit(defaultTransaction);

		Collection<Object> resultInDefaultBranch1 = store.getInstances(
				parentTransaction, "A", roleB);
		assertTrue(resultInDefaultBranch1.contains("B"));

		GUID BranchBTransaction = store.branch(parentTransaction);
		BranchBTransaction = store.commit(BranchBTransaction);
		resultInDefaultBranch1 = store.getInstances(BranchBTransaction, "A",
				roleB);
		assertTrue(resultInDefaultBranch1.contains("B"));

		GUID BranchBID = BranchBTransaction.getBranchID();

		GUID newTransactionOnDefaultBranch = store.begin();
		store.addLink(newTransactionOnDefaultBranch, "A", roleB, "C");
		parentTransaction = store.commit(newTransactionOnDefaultBranch);

		Collection<Object> resultInDefaultBranch2 = store.getInstances(
				parentTransaction, "A", roleB);
		// the collection should contain only Default Branch data
		// e.g. result should contain B and C
		assertTrue(resultInDefaultBranch2.contains("B"));
		assertTrue(resultInDefaultBranch2.contains("C"));

		// store new data in Branch B
		GUID BranchBTransaction2 = store.begin(BranchBID,
				IsolationLevel.SNAPSHOT);
		store.addLink(BranchBTransaction2, "A", roleB, "D");
		BranchBTransaction = store.commit(BranchBTransaction2);

		// the collection should contain only Branch B data and the data that
		// before branching operation
		// e.g result should contains A before branching operation and D is
		// stored in Branch B
		Collection<Object> resultInBranchB2 = store.getInstances(
				BranchBTransaction, "A", roleB);
		assertTrue(resultInBranchB2.contains("D"));
		assertTrue(resultInBranchB2.contains("B"));
		assertFalse(resultInBranchB2.contains("C"));

		// merging branch B back to default
		// the result at merging transaction should contains
		// contains B, C and D
		GUID mergingTransaction = store.begin();
		store.merge(mergingTransaction, BranchBTransaction);
		mergingTransaction = store.commit(mergingTransaction);
		Collection<Object> result = store.getInstances(mergingTransaction, "A",
				roleB);
		assertTrue(result.contains("D"));
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));

	}

}
