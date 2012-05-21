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
import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

public class ActionIteratorTest extends TestCase {

	public void test1() {
		String[] input = { "1", "2", "3", "4" };
		Iterator<String> baseIterator = Arrays.asList(input).iterator();

		final StringBuffer output = new StringBuffer();
		ActionIterator<String> actionIterator = new ActionIterator<String>(
				baseIterator) {
			public void action(String e) {
				output.append(e);
			}
		};

		while (actionIterator.hasNext())
			actionIterator.next();

		assertEquals("1234", output.toString());
	}

	public void test2() {
		String[] input = { "1", "2", "3", "4" };
		Iterator<String> baseIterator = Arrays.asList(input).iterator();

		final StringBuffer output = new StringBuffer();
		ActionIterator<String> actionIterator = new ActionIterator<String>(
				baseIterator) {
			public void firstElement(String e) {
				output.append("{ ");
			}

			public void action(String e) {
				output.append(e + " ");
			}
			
			public void lastElement(String e) {
				output.append("}");
			}
		};

		while (actionIterator.hasNext())
			actionIterator.next();

		assertEquals("{ 1 2 3 4 }", output.toString());
	}

	public void test3() {
		String[] input = { "1" };
		Iterator<String> baseIterator = Arrays.asList(input).iterator();

		final StringBuffer output = new StringBuffer();
		ActionIterator<String> actionIterator = new ActionIterator<String>(
				baseIterator) {
			public void firstElement(String e) {
				output.append("{ ");
			}

			public void action(String e) {
				output.append(e + " ");
			}
			
			public void lastElement(String e) {
				output.append("}");
			}
		};

		while (actionIterator.hasNext())
			actionIterator.next();

		assertEquals("{ 1 }", output.toString());
	}

	public void test4() {
		String[] input = {};
		Iterator<String> baseIterator = Arrays.asList(input).iterator();

		final StringBuffer output = new StringBuffer();
		ActionIterator<String> actionIterator = new ActionIterator<String>(
				baseIterator) {
			public void firstElement(String e) {
				output.append("{ ");
			}

			public void action(String e) {
				output.append(e + " ");
			}
			
			public void lastElement(String e) {
				output.append("}");
			}
		};

		while (actionIterator.hasNext())
			actionIterator.next();

		assertEquals("", output.toString());
	}
}
