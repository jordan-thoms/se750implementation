package nz.ac.auckland.se.genoupe.tools;

/**
 * This interface relates to FilterIterator the same way
 * as Runnable relates to Thread.
 * 
 * If you want to create a FilterIterator from a Tester
 * you should however use the class MultiFilterIterator.
 * 
 * @author gweb017
 *
 * @param <Element>
 */
public interface Tester<Element> {

	public boolean filterCondition(Element nextChangeInBase);

}