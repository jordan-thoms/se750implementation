package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import nz.ac.auckland.se.genoupe.tools.Debug;

import junit.framework.TestCase;

import pdstore.generic.ChangeTemplateKind;
import pdstore.generic.PDChange;

public class GetChangesTest extends TestCase {

	public PDStore store;

	public final GUID decaf1 = new GUID("decaf100000000000000000000000000");
	public final GUID decaf2 = new GUID("decaf200000000000000000000000000");
	public final GUID decaf3 = new GUID("decaf300000000000000000000000000");

	public void setUp() {
		// create a brand new store (including the metamodel) to prevent
		// interference with earlier test runs
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		store = new PDStore("GetChangesTest-" + dateFormat.format(date));
	}

	/**
	 * A parameterized test case that can be used for more specific test cases.
	 * The test case adds a link consisting of fresh GUIDs, and then removes the
	 * link.
	 * 
	 * Requirement: At least one fresh GUID should be used in the given
	 * changeTemplate for instance1, role2 or instance2 to guarantee that the
	 * changes we are searching for are unique. This assumption is used when
	 * checking the size of the returned result set.
	 * 
	 * @param changeTemplate
	 *            the general change template that is used when testing
	 *            getChanges() (with small modifications to test variations,
	 *            e.g. in ChangeType)
	 */
	private void testTwoInverseChanges(
			PDChange<GUID, Object, GUID> changeTemplate) {

		// get ChangeTemplateKind to respect special cases
		ChangeTemplateKind templateKind = ChangeTemplateKind
				.getKind(changeTemplate);

		// construct two inverse changes matching the given template
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				changeTemplate);
		change1.setChangeType(ChangeType.LINK_ADDED);
		// make sure the link is set
		if (change1.getInstance1() == null)
			change1.setInstance1(new GUID());
		if (change1.getRole2() == null)
			change1.setRole2(new GUID());
		if (change1.getInstance2() == null)
			change1.setInstance2(new GUID());
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				change1);
		change2.setChangeType(ChangeType.LINK_REMOVED);

		// add the two changes
		Debug.println("\nbegin()", "getChanges");
		GUID transaction = store.begin();
		// make sure addChange gets the right open transaction ID
		change1.setTransaction(transaction);
		change2.setTransaction(transaction);
		Debug.println("\nadding " + change1, "getChanges");
		store.addChange(change1);
		Debug.println("\nadding " + change2, "getChanges");
		store.addChange(change2);
		Debug.println("\ncommit()", "getChanges");
		GUID durableTransaction = store.commit(transaction);

		// set the right durable transaction ID for the expected changes
		change1.setTransaction(durableTransaction);
		change2.setTransaction(durableTransaction);

		/*
		 * Case xxx should return normalized changes because no additional
		 * denormalizeIterator is used, and the changes come straight out of an
		 * index where all changes should be normalized.
		 */
		if (templateKind == ChangeTemplateKind.XXX) {
			change1 = change1.getNormalizedChange();
			change2 = change2.getNormalizedChange();
		}

		/*
		 * Go through different cases and check the results.
		 * 
		 * We are assuming that only change1 and change2 can possibly match the
		 * changeTemplate because there is at least one fresh GUID in it used
		 * for instance1, role2 or instance2.
		 * 
		 * Special cases:
		 * 
		 * 1. Case xxx is open-ended (no link given), therefore no asserts about
		 * the result size are made.
		 * 
		 * 2. Cases IXX and XXI return extra changes for the USES_ROLE links.
		 * TODO count he extra changes and write asserts for the exact result
		 * sizes, or at least bounds (e.g. <=).
		 */
		Collection<PDChange<GUID, Object, GUID>> result;

		Debug.println("\ncase ChangeType==null:", "getChanges");
		changeTemplate.setTransaction(durableTransaction);
		changeTemplate.setChangeType(null);
		result = store.getChanges(changeTemplate);
		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		if (templateKind != ChangeTemplateKind.XXX
				&& templateKind != ChangeTemplateKind.IXX
				&& templateKind != ChangeTemplateKind.XXI)
			assertTrue(result.size() == 2);

		Debug.println("\ncase ChangeType==LINK_ADDED:", "getChanges");
		changeTemplate.setChangeType(ChangeType.LINK_ADDED);
		result = store.getChanges(changeTemplate);
		assertTrue(result.contains(change1));
		assertFalse(result.contains(change2));
		if (templateKind != ChangeTemplateKind.XXX
				&& templateKind != ChangeTemplateKind.IXX
				&& templateKind != ChangeTemplateKind.XXI)
			assertTrue(result.size() == 1);

		Debug.println("\ncase ChangeType==LINK_REMOVED:", "getChanges");
		changeTemplate.setChangeType(ChangeType.LINK_REMOVED);
		result = store.getChanges(changeTemplate);
		assertTrue(result.contains(change2));
		assertFalse(result.contains(change1));
		if (templateKind != ChangeTemplateKind.XXX
				&& templateKind != ChangeTemplateKind.IXX
				&& templateKind != ChangeTemplateKind.XXI)
			assertTrue(result.size() == 1);

		Debug.println("\ncase ChangeType==LINK_EFFECTIVE:", "getChanges");
		changeTemplate.setChangeType(ChangeType.LINK_EFFECTIVE);
		result = store.getChanges(changeTemplate);
		assertTrue(result.contains(change2));
		assertFalse(result.contains(change1));
		if (templateKind != ChangeTemplateKind.XXX
				&& templateKind != ChangeTemplateKind.IXX
				&& templateKind != ChangeTemplateKind.XXI)
			assertTrue(result.size() == 1);
	}

	public void testXXX1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				null, null, null));
	}

	public void testXXX2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance2);
		store.addLink(transaction, instance1, role1, instance3);
		store.addLink(transaction, instance2, role1, instance3);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance3);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance2, role1, instance3);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, null, null, null);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		// getChanges() should return normalize changes in this case because
		// the changes come straight out of an index where all changes should be
		// normalized.
		assertTrue(result.contains(change1.getNormalizedChange()));
		assertTrue(result.contains(change2.getNormalizedChange()));
		assertTrue(result.contains(change3.getNormalizedChange()));
	}

	public void testIXX1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				new GUID(), null, null));
	}

	public void testIXX2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance1);
		store.addLink(transaction, instance1, role1, instance2);
		store.addLink(transaction, instance1, role1, instance3);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance1);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance3);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, null, null);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		assertTrue(result.contains(change3));
		// TODO figure out how many extra changes are caused by USES_ROLE and
		// then reactivate the assert below
		// assertTrue(result.size() == 3);
	}

	public void testXRX1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				null, new GUID(), null));
	}

	public void testXRX2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance3);
		store.addLink(transaction, instance2, role1, instance2);
		store.addLink(transaction, instance3, role1, instance1);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance3);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance2, role1, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance3, role1, instance1);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, null, role1, null);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		assertTrue(result.contains(change3));
		assertTrue(result.size() == 3);
	}

	public void testIRX1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				new GUID(), new GUID(), null));
	}

	/**
	 * This is the same as testIRX1 except that role1 uses always a non-"first"
	 * GUID. This used to cause problems for equals() in the partner change
	 * mechanism in PDChange and its subtype.
	 */
	public void testIRX1b() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID().getFirst().getPartner();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance2);
		transaction = store.commit(transaction);

		// expected change
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance2);
		Debug.println("change1.getRole2().isFirst()="
				+ change1.getRole2().isFirst(), "getChanges");

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, null);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		Debug.println(result.toString(), "getChanges");
		assertTrue(result.size() == 1);
	}

	public void testIRX2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance1);
		store.addLink(transaction, instance1, role1, instance2);
		store.addLink(transaction, instance1, role1, instance3);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance1);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance3);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, null);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		assertTrue(result.contains(change3));
		assertTrue(result.size() == 3);
	}

	public void testXXI1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				null, null, new GUID()));
	}

	public void testXXI2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();
		GUID role2 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance3);
		store.addLink(transaction, instance2, role1, instance3);
		store.addLink(transaction, instance1, role2, instance3);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance3);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance2, role1, instance3);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role2, instance3);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, null, null, instance3);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		assertTrue(result.contains(change3));
		// TODO figure out how many extra changes are caused by USES_ROLE and
		// then reactivate the assert below
		// assertTrue(result.size() == 3 + 2);
	}

	public void testIXI1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				new GUID(), null, new GUID()));
	}

	public void testIXI2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();
		GUID role2 = new GUID();
		GUID role3 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance2);
		store.addLink(transaction, instance1, role2, instance2);
		store.addLink(transaction, instance1, role3, instance2);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role2, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role3, instance2);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, null, instance2);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		assertTrue(result.contains(change3));
		assertTrue(result.size() == 3);
	}

	public void testXRI1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				null, new GUID(), new GUID()));
	}

	public void testXRI2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();

		GUID transaction = store.begin();
		store.addLink(transaction, instance1, role1, instance1);
		store.addLink(transaction, instance2, role1, instance1);
		store.addLink(transaction, instance3, role1, instance1);
		transaction = store.commit(transaction);

		// expected changes
		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance1, role1, instance1);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance2, role1, instance1);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, instance3, role1, instance1);

		PDChange<GUID, Object, GUID> changeTemplate1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, null, role1, instance1);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(changeTemplate1);

		assertTrue(result.contains(change1));
		assertTrue(result.contains(change2));
		assertTrue(result.contains(change3));
		assertTrue(result.size() == 3);
	}

	public void testIRI1() {
		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				new GUID(), new GUID(), new GUID()));
	}

	public void testIRI1b() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();

		// make sure the role is a second role, as this once caused a bug
		if (role1.isFirst())
			role1 = role1.getPartner();

		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				instance1, role1, instance2));
	}

	public void testIRI1c() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();

		// make sure the role is a first role, as there was once a bug that had
		// to do with it
		if (!role1.isFirst())
			role1 = role1.getPartner();

		testTwoInverseChanges(new PDChange<GUID, Object, GUID>(null, null,
				instance1, role1, instance2));
	}
	
	public void testDuplicates() {
		GUID guid1 = new GUID();
		GUID guid3 = new GUID();
		GUID guid6 = new GUID("111decaf123456111111111111111111");
		GUID roleA = new GUID();

		PDStore store1 = store;
		PDStore store2 = store;
		Collection<Object> result;

		// make sure guid6 is added
		GUID transaction0 = store2.begin();
		result = store.getInstances(transaction0, guid1, roleA);
		store1.addLink(transaction0, guid1, roleA, guid6);

		GUID durableTransaction = store2.commit(transaction0);
		// now test two concurrent transactions
		GUID transaction1 = store1.begin();

		// t1 sets value to guid3
		store1.removeLink(transaction1, guid1, roleA, guid6);
		store1.addLink(transaction1, guid1, roleA, guid3);
		
		Collection<PDChange<GUID, Object, GUID>> changes = store
				.getChanges(new PDChange<GUID, Object, GUID>(
						ChangeType.LINK_EFFECTIVE, transaction1, guid1,
						roleA, null));
		
		assertTrue(changes.size()==2);
	}
}
