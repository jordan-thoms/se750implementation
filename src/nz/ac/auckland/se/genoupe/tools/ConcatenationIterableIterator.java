package nz.ac.auckland.se.genoupe.tools;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Similar to ConcatenationIterator, but is based on Iterables.
 * 
 * @author gweb017
 *
 * @param <Element>
 */
public class ConcatenationIterableIterator<Element> extends StatefulIterator<Element> {

	private Iterator<Iterable<Element>> baseIterators;
	Iterator<Element> currentIterator = null;

	public ConcatenationIterableIterator(Iterable<Iterable<Element>> baseIterators) {
		this.baseIterators = baseIterators.iterator();
	}

	public ConcatenationIterableIterator(Iterable<Element>... baseIterators) {
		this.baseIterators = Arrays.asList(baseIterators).iterator();
	}

	protected Element computeNext() {
		Debug.println("called", "ConcatenationIterableIterator");
		do{
			if(currentIterator == null) {
				// This is the case at the very first call to computeNext()
				// and also every time after currentIterator has been finished
				if(!baseIterators.hasNext())
					return null;
				currentIterator = baseIterators.next().iterator();
			}
			if (currentIterator.hasNext()) {
				return currentIterator.next();
			}
			// reset currentIterator and start over.
			currentIterator = null;
		} while(true);		
	}
}

