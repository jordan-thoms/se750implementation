package nz.ac.auckland.se.genoupe.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.sun.tools.doclets.internal.toolkit.taglets.BaseInlineTaglet;

import pdstore.sparql.Variable;

/**
 * A "lazy" Collection that can be created on Iterators, so that basic functions
 * such as contains are available for them.
 * 
 * @author Christof
 * 
 * @param <E>
 */
public class IteratorBasedCollection<E> implements Collection<E> {

	/**
	 * The iterator that this list is based on.
	 */
	Iterator<E> baseIterator;

	/**
	 * The action iterator that adds elements to the internal list.
	 */
	Iterator<E> addIterator;

	/**
	 * An internal list storing elements that have already been returned by the
	 * iterator.
	 */
	List<E> list = new ArrayList<E>();

	/**
	 * Flag indicating whether all elements from the iterator have been added to
	 * the internal list.
	 */
	boolean allElementsInList = false;

	/**
	 * Flag indicating whether the base iterator has already been used.
	 */
	boolean startedUsingBaseIterator = false;

	public IteratorBasedCollection(Iterator<E> baseIterator) {
		super();
		initBaseIterator(baseIterator);
	}

	private void initBaseIterator(Iterator<E> baseIterator) {
		this.baseIterator = baseIterator;

		addIterator = new ActionIterator<E>(baseIterator) {
			public void firstElement(E element) {
				startedUsingBaseIterator = true;
			}
			
			public void action(E element) {
				list.add(element);
			}

			public void lastElement(E element) {
				allElementsInList = true;
			}
		};
	}

	public Iterator<E> iterator() {
		// if all the iterator elements have already been added to the list,
		// use it
		if (allElementsInList)
			return list.iterator();

		if (startedUsingBaseIterator)
			throw new UnsupportedOperationException(
					"The list supports only one iterator at a time.");

		// otherwise return the action iterator that adds the elements to the
		// list
		return addIterator;
	}

	public boolean addAll(Collection<? extends E> collection) {
		if (collection.isEmpty())
			return false;

		if (allElementsInList)
			return list.addAll(collection);

		if (startedUsingBaseIterator)
			throw new UnsupportedOperationException(
					"The list does not support modification after iteration has been started and before it has been iterated completely.");

		Iterator<E> newIterator = (Iterator<E>) collection.iterator();
		initBaseIterator(new ConcatenationIterator<E>(baseIterator, newIterator));
		return true;
	}

	public boolean isEmpty() {
		if (allElementsInList)
			return list.isEmpty();

		// if the base iterator has been used for iteration, then there is at
		// least one element
		if (startedUsingBaseIterator)
			return true;

		// the base iterator has not been used for iteration yet, so we can peek
		// if there is a first element
		return !baseIterator.hasNext();
	}

	/**
	 * Makes sure all elements from the base iterator are added to the internal
	 * list.
	 */
	void fillList() {
		while (addIterator.hasNext())
			addIterator.next();
	}

	public boolean contains(Object o) {
		fillList();
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		fillList();
		return list.containsAll(c);
	}

	public int size() {
		fillList();
		return list.size();
	}

	public Object[] toArray() {
		fillList();
		return list.toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		fillList();
		return list.toArray(arg0);
	}

	public String toString() {
		fillList();
		return list.toString();
	}

	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
}
