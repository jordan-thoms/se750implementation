package nz.ac.auckland.se.genoupe.tools;

import java.util.*;

public class ReverseListIterator<T> implements Iterator<T> {
	private ListIterator<T> listIterator;

	public ReverseListIterator(List<T> list) {
		listIterator = list.listIterator(list.size());
	}

	public boolean hasNext() {
		return listIterator.hasPrevious();
	}

	public T next() {
		return listIterator.previous();
	}

	public void remove() {
		listIterator.remove();
	}
}