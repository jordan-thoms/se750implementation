package pdintegrate.ASM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class ClassVisitationMediator implements Iterable<ClassInfo>{
	
	/**
	 * Membership in this map implies that a given class has already been marked for modification.
	 * 
	 * The iteration will keep going until the all classes membered in this map have been yielded.
	 */
	public Map<String, ClassInfo> visitedMap = new HashMap<String, ClassInfo>();
	
	private ArrayList<ClassInfo> toVisit = new ArrayList<ClassInfo>();
	
	public void discover(Class<?> c) {
		this.discover(new ClassInfo(c));
	}
	
	public void discover(ClassInfo c) {
		if (visitedMap.containsKey(c.getOldName())) {
			//Discovered before, leave it
		} else {
			visitedMap.put(c.getOldName(), c);
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
		if (!this.visitedMap.containsKey(cI.getOldName())) {
			throw new NoSuchElementException();
		}
		
	}

	public ClassInfo lookup(String owner) {
		return this.visitedMap.get(owner);
	}

}
