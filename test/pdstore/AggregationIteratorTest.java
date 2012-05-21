package pdstore;

import java.util.*;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.ReverseListIterator;

import pdstore.changeindex.AggregationIterator;
import pdstore.generic.PDChange;

import junit.framework.TestCase;

public class AggregationIteratorTest extends TestCase {
	
	public final GUID decaf1 = new GUID("decaf100000000000000000000000000");
	public final GUID decaf2 = new GUID("decaf200000000000000000000000000");
	public final GUID decaf3 = new GUID("decaf300000000000000000000000000");
	public final GUID decaf4 = new GUID("decaf400000000000000000000000000");
	public final GUID decaf5 = new GUID("decaf500000000000000000000000000");
	public final GUID decaf6 = new GUID("decaf600000000000000000000000000");
	
	public void testSingleChange() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();

		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);

		ArrayList<PDChange<GUID, Object, GUID>> input = new ArrayList<PDChange<GUID, Object, GUID>>();
		input.add(change1);

		Iterator<PDChange<GUID, Object, GUID>> baseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
				input);
		AggregationIterator<GUID, Object, GUID> iterator = new AggregationIterator<GUID, Object, GUID>(
				baseIterator);

		assertTrue(iterator.hasNext());
		assertEquals(change1, iterator.next());
	}

	public void testThreeIndependentChanges() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID instance3 = new GUID();
		GUID role1 = new GUID();

		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance1);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance3);

		ArrayList<PDChange<GUID, Object, GUID>> input = new ArrayList<PDChange<GUID, Object, GUID>>();
		input.add(change1);
		input.add(change2);
		input.add(change3);

		Iterator<PDChange<GUID, Object, GUID>> baseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
				input);
		AggregationIterator<GUID, Object, GUID> iterator = new AggregationIterator<GUID, Object, GUID>(
				baseIterator);

		assertTrue(iterator.hasNext());
		assertEquals(change3, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(change2, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(change1, iterator.next());
	}
	
	public void testTwoDependentChanges1() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();

		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);

		ArrayList<PDChange<GUID, Object, GUID>> input = new ArrayList<PDChange<GUID, Object, GUID>>();
		input.add(change1);
		input.add(change2);

		Iterator<PDChange<GUID, Object, GUID>> baseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
				input);
		AggregationIterator<GUID, Object, GUID> iterator = new AggregationIterator<GUID, Object, GUID>(
				baseIterator);

		assertTrue(iterator.hasNext());
		assertEquals(change2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	public void testTwoDependentChanges2() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();

		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);

		ArrayList<PDChange<GUID, Object, GUID>> input = new ArrayList<PDChange<GUID, Object, GUID>>();
		input.add(change1);
		input.add(change2);

		Iterator<PDChange<GUID, Object, GUID>> baseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
				input);
		AggregationIterator<GUID, Object, GUID> iterator = new AggregationIterator<GUID, Object, GUID>(
				baseIterator);

		assertTrue(iterator.hasNext());
		assertEquals(change2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	public void testAllDependentChanges1() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();

		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change4 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role1, instance2);

		ArrayList<PDChange<GUID, Object, GUID>> input = new ArrayList<PDChange<GUID, Object, GUID>>();
		input.add(change1);
		input.add(change2);
		input.add(change3);
		input.add(change4);

		Iterator<PDChange<GUID, Object, GUID>> baseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
				input);
		AggregationIterator<GUID, Object, GUID> iterator = new AggregationIterator<GUID, Object, GUID>(
				baseIterator);

		assertTrue(iterator.hasNext());
		assertEquals(change4, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	public void testMixedChanges1() {
		GUID instance1 = new GUID();
		GUID instance2 = new GUID();
		GUID role1 = new GUID();
		GUID role2 = new GUID();

		PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role2, instance2);
		PDChange<GUID, Object, GUID> change3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, new GUID(), instance1, role1, instance2);
		PDChange<GUID, Object, GUID> change4 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, new GUID(), instance1, role2, instance2);

		ArrayList<PDChange<GUID, Object, GUID>> input = new ArrayList<PDChange<GUID, Object, GUID>>();
		input.add(change1);
		input.add(change2);
		input.add(change3);
		input.add(change4);

		Iterator<PDChange<GUID, Object, GUID>> baseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
				input);
		AggregationIterator<GUID, Object, GUID> iterator = new AggregationIterator<GUID, Object, GUID>(
				baseIterator);

		assertTrue(iterator.hasNext());
		assertEquals(change4, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(change3, iterator.next());
		assertFalse(iterator.hasNext());
	}
}
