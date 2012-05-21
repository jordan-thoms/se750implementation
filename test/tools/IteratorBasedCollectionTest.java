package tools;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;
import nz.ac.auckland.se.genoupe.tools.IteratorBasedCollection;

public class IteratorBasedCollectionTest extends TestCase {

	public void test1() {
		String[] input = { "1", "2", "3" };
		Iterator<String> baseIterator = Arrays.asList(input).iterator();
		IteratorBasedCollection<String> collection = new IteratorBasedCollection<String>(
				baseIterator);

		assertFalse(collection.isEmpty());

		Iterator<String> testIterator = collection.iterator();
		assertEquals(true, testIterator.hasNext());
		assertEquals("1", testIterator.next());
		assertEquals(true, testIterator.hasNext());
		assertEquals("2", testIterator.next());
		assertEquals(true, testIterator.hasNext());
		assertEquals("3", testIterator.next());
		assertEquals(false, testIterator.hasNext());

		testIterator = collection.iterator();
		assertTrue(testIterator.hasNext());
		assertEquals("1", testIterator.next());
		assertTrue(testIterator.hasNext());
		assertEquals("2", testIterator.next());
		assertTrue(testIterator.hasNext());
		assertEquals("3", testIterator.next());
		assertFalse(testIterator.hasNext());

		assertFalse(collection.isEmpty());
		assertEquals(3, collection.size());
		assertTrue(collection.contains("1"));
		assertTrue(collection.contains("2"));
		assertTrue(collection.contains("3"));
		assertFalse(collection.contains("4"));
	}
}
