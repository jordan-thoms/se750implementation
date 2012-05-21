package nz.ac.auckland.se.genoupe.parallel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * This class allows to process all elements of an iterable in parallel.
 * 
 * @author Gerald
 *
 */
public class ParallelForEach<Element> implements AbstractForEach<Element>  {

    

	/* (non-Javadoc)
	 * @see nz.ac.auckland.se.genoupe.tools.AbstractForEach#forEach(java.lang.Iterable, nz.ac.auckland.se.genoupe.tools.Applicable)
	 */
	@Override
	public Map<Element, ApplyThread<Element>> forEach(Iterable<Element> iterator , Applicable<Element> applicable) {
		Map<Element, ApplyThread<Element>> threadSet = new HashMap<Element, ApplyThread<Element>>();
		for(Element elem: iterator){
			ApplyThread<Element> thread = new ApplyThread<Element>(applicable, elem, this);
			threadSet.put(elem, thread);
			thread.start();
		}
		return threadSet;
	}

}
