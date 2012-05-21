package nz.ac.auckland.se.genoupe.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class can be used to create a FilterIterator from Tester
 * instances.
 * It accepts a whole list of Testers.
 * 
 * Since a short-circuit logical AND can be implemented
 * with piping FilterIterators, the natural semantics
 * for this list is a short-circuit logical OR on all
 * the testers in the list.
 * 
 * @author gweb017
 *
 * @param <Element>
 */
public class MultiFilterIterator<Element> extends FilterIterator<Element> {
	
	private List<Tester<Element>> list = new ArrayList<Tester<Element>>();

	public MultiFilterIterator(Iterator<Element> baseIterator) {
		super(baseIterator);
	}

	public List<Tester<Element>> getList() {
		return list;
	}

	@Override
	public boolean filterCondition(Element nextChangeInBase) {
		// The following loop is a short-circuit logical OR
		for(Tester<Element> tester:list)
			if(tester.filterCondition(nextChangeInBase))
				return true;
	 	return false;
	}
	
}
