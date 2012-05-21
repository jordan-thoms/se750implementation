package nz.ac.auckland.se.genoupe.parallel;

/**
 * This interface must be extended to implement a parallel loop body
 * for the ParallelForEach
 * 
 * @author gweb017
 *
 * @param <Element>
 */
public interface Applicable<Element> {
	
	/**
	 * The method that should implement the parallel loop body.
	 * 
	 * @param element
	 */
	void apply(Element element);

}
