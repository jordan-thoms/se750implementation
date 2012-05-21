package nz.ac.auckland.se.genoupe.tools;

import java.util.Iterator;

/**
 * This is a generic iterator that maps the elements of its base iterator to
 * other elements, using method map(InputType).
 * 
 * @author Christof
 * 
 */
public class MapIterator<InputType, OutputType> extends
		StatefulIterator<OutputType> {

	private Iterator<InputType> baseIterator;

	public MapIterator(Iterator<InputType> baseIterator) {
		this.baseIterator = baseIterator;
	}

	protected OutputType computeNext() {
		// Why is this not a foEach?
		// Because we stop and resume with the next call.
		InputType nextInBase = null;
		while (baseIterator.hasNext()) {
			nextInBase = baseIterator.next();
			return map(nextInBase); // change to State 2.
		}
		return null;
	}

	/**
	 * Maps the given element to a corresponding element of the OutputType.
	 * 
	 * @param nextChangeInBase
	 *            the element to be mapped
	 * @return
	 */
	public OutputType map(InputType nextChangeInBase) {
		return null;
	}
}
