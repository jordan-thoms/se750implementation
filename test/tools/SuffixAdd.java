package tools;

import nz.ac.auckland.se.genoupe.parallel.Applicable;

public class SuffixAdd implements Applicable<StringBuffer> {

	@Override
	public void apply(StringBuffer element) {
		element.append("Hello");
		
	}

}
