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
import nz.ac.auckland.se.genoupe.tools.ReverseListIterator;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

public class ReverseListIteratorTest extends TestCase {

	public void testEmptyList() {
		String[] input1 = { };

		ReverseListIterator<String> concatIterator = new ReverseListIterator<String>(
				Arrays.asList(input1));

		assertFalse(concatIterator.hasNext());
	}
	
	public void test1() {
		String[] input1 = { "1" };

		ReverseListIterator<String> concatIterator = new ReverseListIterator<String>(
				Arrays.asList(input1));

		assertTrue(concatIterator.hasNext());
		assertEquals("1", concatIterator.next());
		assertFalse(concatIterator.hasNext());
	}
	
	public void test2() {
		String[] input1 = { "1", "2", "3" };

		ReverseListIterator<String> concatIterator = new ReverseListIterator<String>(
				Arrays.asList(input1));

		assertTrue(concatIterator.hasNext());
		assertEquals("3", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("2", concatIterator.next());
		assertTrue(concatIterator.hasNext());
		assertEquals("1", concatIterator.next());
		assertFalse(concatIterator.hasNext());
	}
}
