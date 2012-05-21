package nz.ac.auckland.se.genoupe.tools;

import java.util.Iterator;

/**
 * This is a utility class that simplifies implementation of Iterators.
 * Java iterators are reasonably convenient for the clients (callers) but 
 * in many cases they are hard to implement because of the contract of the hasNext()
 * function. 
 * The hasNext function in many cases requires to compute the next element
 * before it can be dispatched to the caller.
 * 
 * This requires a uniquely defined lifecycle for the iterator, and this 
 * lifecycle is encapsulated in this iterator.
 *
 * Arguing from the view of the programmer tasked with implementing a Java 
 * iterator, the most preferrable interface is to just implement a single 
 * method computeNext() which returns either the next element or null if there
 * is no next element. 
 *
 * This Stateful iterator bridges the gap between the iterator programmers
 * needs and the caller' needs.
 * To program a new iterator one can write a new class that extends this class 
 * and only overwrites the computeNext() method.
 * 
 * This class itself is not abstract, since it can be seen as a correct 
 * iterator of a collection that has Zero elements.
 *
 * An instance of this class is a state machine with three states:
 * State 1: hasNext is unknown. Then theNextChange is null.
 *      This state will be left to State 2 if hasNext is called and returns true.
 *      If hasNext gives false, we enter the State 3, the final state.
 *      State 1 is also the start state.
 *      next() should not be called in this state by good code, because the
 *      code should first call hasNext(). But the implementation is safe
 *      and will still return the right next() result or null if !hasNext().
 * State 2: hasNext is known to be true. Then the next element is already stored in 
 *      theNextChange. This state is left to State 1 if next() is called.
 * State 3: the final state. hasNext will always return false.
 *        
 *  
 * 
 * @author Gerald
 *
 */
public  class StatefulIterator<Element> implements Iterator<Element>{

	private Element theNextElement = null;
	private Element current = null;

	public Element getCurrent() {
		return current;
	}

	public Element getNext() {
		hasNext();
		return theNextElement;
	}

	public StatefulIterator() {
		super();
	}

	public Element next() {
		// First make the method safe
		if (theNextElement== null)
			// should actually not happen
			// shows that the code has not called hasNext()
			if(!hasNext())	
				return null;
		
		current = theNextElement;
		theNextElement = null; // Change to State 1
		return current;
	}

	protected Element computeNext() { return null;}

	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasNext() {
		if (theNextElement!= null)
		  return true;
		// assert: theNextChange==null
		theNextElement = computeNext();
		if (theNextElement!= null)
			 return true;
     	return false;
	}

}