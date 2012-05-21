package nz.ac.auckland.se.genoupe.parallel;

import java.util.HashMap;
import java.util.Map;


/**
 * This class allows to process all elements of an iterable in parallel.
 * 
 * @author Gerald
 *
 */
public class SequentialForEach<Element> implements AbstractForEach<Element>  {

    

	/* (non-Javadoc)
	 * @see nz.ac.auckland.se.genoupe.tools.AbstractForEach#forEach(java.lang.Iterable, nz.ac.auckland.se.genoupe.tools.Applicable)
	 */
	@Override
	public Map<Element, ApplyThread<Element>> forEach(Iterable<Element> iterator , Applicable<Element> applicable) {
		for(Element elem: iterator){
			applicable.apply(elem);
		}
		return null;
	}

}
