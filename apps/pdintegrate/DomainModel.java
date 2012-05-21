package pdintegrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import pdstore.GUID;

public class DomainModel implements Iterable<ClassInfo>{
	
	/**
	 * Membership in this map implies that a given class has already been marked for modification.
	 * 
	 * The iteration will keep going until the all classes membered in this map have been yielded.
	 */
	public Map<String, ClassInfo> visitedMap = new HashMap<String, ClassInfo>();
	
	public GUID ModelID;
	
	public DomainModel(GUID ModelID) {
		this.ModelID = ModelID;
	}
	
	private ArrayList<ClassInfo> toVisit = new ArrayList<ClassInfo>();
	
	public void register(Class<?> c) {
		this.register(new ClassInfo(c.getCanonicalName()));
	}
	
	public void register(String s) {
		this.register(new ClassInfo(s));
	}
	
	public void register(ClassInfo c) {
		if (visitedMap.containsKey(c.getName())) {
			//Discovered before, leave it
		} else {
			visitedMap.put(c.getName(), c);
			toVisit.add(c);
		}
	}
	
	public boolean discovered(String name){
		return visitedMap.containsKey(name);
	}
	

	@Override
	public Iterator<ClassInfo> iterator() {
		return new Iterator<ClassInfo>() {
			
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < toVisit.size();
			}

			@Override
			public ClassInfo next() {
				return toVisit.get(index++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	public void expect(ClassInfo cI) {
		if (!this.visitedMap.containsKey(cI.getName())) {
			throw new NoSuchElementException();
		}
		
	}

	public ClassInfo lookup(String owner) {
		return this.visitedMap.get(owner.replace('.', '/'));
	}

	public String getTypeID(String classname) {
		return this.lookup(classname).getTypeID().toString();
	}

}
