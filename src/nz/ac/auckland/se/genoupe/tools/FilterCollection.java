package nz.ac.auckland.se.genoupe.tools;

import java.util.Collection;
import java.util.Iterator;

public class FilterCollection<Element> implements Collection<Element>{
	
	private Collection<Element> base;
	private Tester<Element> tester;

	public Tester<Element> getTester() {
		return tester;
	}

	public void setTester(Tester<Element> tester) {
		this.tester = tester;
	}

	public FilterCollection(Collection<Element> base, Tester<Element> tester) {
		super();
		this.base = base;
		this.tester = tester;
	}
	
	public FilterCollection(Collection<Element> base) {
		super();
		this.base = base;
	}
	

	@Override
	public Iterator<Element> iterator() {
		Iterator<Element> baseIt = base.iterator();
		MultiFilterIterator<Element> multiFilterIterator = new MultiFilterIterator<Element>(baseIt);
		multiFilterIterator.getList().add(tester);
		return multiFilterIterator;
	}

    

	@Override
	public boolean add(Element e) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public boolean addAll(Collection<? extends Element> c) {
		return base.addAll(c);
		
	}

	@Override
	public void clear() {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public boolean contains(Object o) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public boolean containsAll(Collection<?> c) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
		
	}

	@Override
	public boolean remove(Object o) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}

	@Override
	public int size() {
		return base.size();
	}

	@Override
	public Object[] toArray() {
		return base.toArray();
		
	}

	@Override
	public <T> T[] toArray(T[] a) {
				throw new UnsupportedOperationException("not intended to be used");
		
	}



}
