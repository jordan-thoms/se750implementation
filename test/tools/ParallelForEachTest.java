package tools;

import nz.ac.auckland.se.genoupe.parallel.ParallelForEach;

public class ParallelForEachTest extends ForEachTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		p = new ParallelForEach<StringBuffer>();
	}


	
}
