package nz.ac.auckland.se.genoupe.tools;

import java.util.Iterator;

/**
 * This is a generic iterator that calls methods for each element in the given
 * base iterator.
 * 
 * firstElement(Element) is called for the first element only.
 * 
 * middleElement(Element) is called for the element that are neither first nor
 * last.
 * 
 * lastElement()Element) is called for the last element only.
 * 
 * action(Element) is called for all elements, after firstElement(),
 * middleElement() or lastElement().
 * 
 * @author Christof
 * 
 */
public class ActionIterator<Element> extends StatefulIterator<Element> {

	private Iterator<Element> baseIterator;

	/**
	 * Flag indicating whether the current element is the first element in the
	 * iterator.
	 */
	boolean isFirst = true;

	public ActionIterator(Iterator<Element> baseIterator) {
		this.baseIterator = baseIterator;
	}

	protected Element computeNext() {
		// Why is this not a forEach?
		// Because we stop and resume with the next call.
		Element nextInBase = null;
		while (baseIterator.hasNext()) {
			nextInBase = baseIterator.next();

			if (isFirst) {
				// first element
				firstElement(nextInBase);
				isFirst = false;
			}

			// call action for all elements
			action(nextInBase);

			if (!baseIterator.hasNext()) {
				// last element, which could potentially also be the first
				// element
				lastElement(nextInBase);
			}

			return nextInBase; // change to State 2.
		}
		return null;
	}

	/**
	 * This method is called only for the first element, before action(). The
	 * first element could also be the last element, so lastElement() may be
	 * called as well for a first element.
	 * 
	 * @param nextChangeInBase
	 *            the first element
	 */
	public void firstElement(Element nextChangeInBase) {
	}

	/**
	 * This method is called for all elements, after a possible call to
	 * firstElement() and before a possible call to lastElement().
	 * 
	 * @param nextChangeInBase
	 *            the element of the base iterator
	 */
	public void action(Element nextChangeInBase) {
	}

	/**
	 * This method is called only for the last element, after action(). The last
	 * element could also be the first element, so firstElement() may be called
	 * as well for a last element.
	 * 
	 * @param nextChangeInBase
	 *            the last element
	 */
	public void lastElement(Element nextChangeInBase) {
	}
}
