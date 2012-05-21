package nz.ac.auckland.se.genoupe.parallel;

import java.util.Map;


public interface AbstractForEach<Element> {

	public abstract Map<Element, ApplyThread<Element>> forEach(
			Iterable<Element> iterator, Applicable<Element> applicable);

}