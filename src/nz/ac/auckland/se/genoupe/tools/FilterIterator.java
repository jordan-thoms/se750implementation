package nz.ac.auckland.se.genoupe.tools;

import java.util.Iterator;

/**
 * This is a generic iterator that implements a filter.
 * 
 * @author Gerald
 *
 */
public class FilterIterator<Element> extends StatefulIterator<Element> implements Tester<Element> {
	
	private Iterator<Element> baseIterator;
	
	public FilterIterator(
			Iterator<Element> baseIterator) {
		this.baseIterator = baseIterator;
	}

	protected Element computeNext() {
		// Why is this not a foEach?
		// Because we stop and resume with the next call.
		Element nextInBase = null;
        while(baseIterator.hasNext()){
        	nextInBase =  baseIterator.next();
        	if(filterCondition(nextInBase)) {
        		return  nextInBase;  // change to State 2.
        	}
        }
		return null;
	}

	/* (non-Javadoc)
	 * @see nz.ac.auckland.se.genoupe.tools.Tester#filterCondition(Element)
	 */
	public boolean filterCondition(Element nextChangeInBase) {
		return true;
	}

	

}
