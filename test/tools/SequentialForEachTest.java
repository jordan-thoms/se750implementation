package tools;

import nz.ac.auckland.se.genoupe.parallel.SequentialForEach;

public class SequentialForEachTest extends ForEachTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		p = new SequentialForEach<StringBuffer>();
	}


	
}
