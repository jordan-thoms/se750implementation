package tools;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

public class DebugTest extends TestCase {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public void setUp() {
	}

	public void testGetCaller() {
		StackTraceElement caller = Debug.getCaller(1);
		assertTrue(caller.getMethodName().equals("testGetCaller"));
	}


}
