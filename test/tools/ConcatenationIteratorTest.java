package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.tools.javac.util.List;

import junit.framework.TestCase;
import nz.ac.auckland.se.genoupe.parallel.AbstractForEach;
import nz.ac.auckland.se.genoupe.parallel.Applicable;
import nz.ac.auckland.se.genoupe.parallel.ParallelForEach;
import nz.ac.auckland.se.genoupe.tools.ActionIterator;
import nz.ac.auckland.se.genoupe.tools.ConcatenationIterator;
import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

public class ConcatenationIteratorTest extends TestCase {

	public void test1() {
		String[] input1 = { "1", "2", "3" };
		Iterator<String> baseIterator1 = Arrays.asList(input1).iterator();

		ConcatenationIterator<String> concatIterator = new ConcatenationIterator<String>(
				baseIterator1);

		assertTrue(concatIterator.hasNext());
		assertEquals("1", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("2", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("3", concatIterator.next());
		assertFalse(concatIterator.hasNext());
	}

	public void test2() {
		String[] input1 = { "1", "2", "3" };
		Iterator<String> baseIterator1 = Arrays.asList(input1).iterator();

		String[] input2 = { "4", "5", "6" };
		Iterator<String> baseIterator2 = Arrays.asList(input2).iterator();

		ConcatenationIterator<String> concatIterator = new ConcatenationIterator<String>(
				baseIterator1, baseIterator2);

		assertTrue(concatIterator.hasNext());
		assertEquals("1", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("2", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("3", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("4", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("5", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("6", concatIterator.next());
		assertFalse(concatIterator.hasNext());
	}

	public void test3() {
		String[] input1 = { "1", "2", "3" };
		Iterator<String> baseIterator1 = Arrays.asList(input1).iterator();

		String[] input2 = { "4", "5", "6" };
		Iterator<String> baseIterator2 = Arrays.asList(input2).iterator();

		String[] input3 = { "7", "8", "9" };
		Iterator<String> baseIterator3 = Arrays.asList(input3).iterator();

		ConcatenationIterator<String> concatIterator = new ConcatenationIterator<String>(
				baseIterator1, baseIterator2, baseIterator3);

		assertTrue(concatIterator.hasNext());
		assertEquals("1", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("2", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("3", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("4", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("5", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("6", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("7", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("8", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("9", concatIterator.next());
		assertFalse(concatIterator.hasNext());
	}
}
