package tools;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

public class StopwatchTest extends TestCase {
	
	Stopwatch stopwatch = new Stopwatch();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public void setUp() {
		stopwatch = new Stopwatch();
	}

	public void testStart() {
		stopwatch.reset();
		stopwatch.start();
		assertTrue(stopwatch.nanoSeconds() > 0);
		stopwatch.reset();
	}

	public void testStop() {
		stopwatch.reset();
		stopwatch.start();
		stopwatch.stop();
		long stopTime = stopwatch.nanoSeconds();
		assertEquals(stopwatch.nanoSeconds(), stopTime);
		stopwatch.reset();
	}
	
	public void testResume() {
		stopwatch.reset();
		stopwatch.start();
		stopwatch.stop();
		long stopTime = stopwatch.nanoSeconds();
		stopwatch.start();
		stopwatch.stop();
		assertTrue(stopwatch.nanoSeconds() > stopTime);
		stopwatch.reset();
	}

	public void testReset() {
		stopwatch.reset();
		stopwatch.start();
		stopwatch.stop();
		stopwatch.reset();
		assertEquals(stopwatch.nanoSeconds(), 0);
	}

}
