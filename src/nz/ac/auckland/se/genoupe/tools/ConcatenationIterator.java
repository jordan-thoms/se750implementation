package nz.ac.auckland.se.genoupe.tools;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This is a generic iterator that concatenates several base iterators, in the
 * order in which the base iterators are given.
 * 
 * @author Christof
 * 
 */
public class ConcatenationIterator<Element> extends StatefulIterator<Element> {

	private List<Iterator<Element>> baseIterators;
	int iteratorIndex = 0;

	public ConcatenationIterator(List<Iterator<Element>> baseIterators) {
		this.baseIterators = baseIterators;
	}
	
	public ConcatenationIterator(Iterator<Element>... baseIterators) {
		this.baseIterators = Arrays.asList(baseIterators);
	}

	protected Element computeNext() {
		while (iteratorIndex < baseIterators.size()) {
			Iterator<Element> currentIterator = baseIterators
					.get(iteratorIndex);
			while (currentIterator.hasNext()) {
				return currentIterator.next();
			}
			iteratorIndex++;
		}
		return null;
	}
}
