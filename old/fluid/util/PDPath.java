package fluid.util;

import java.util.ArrayList;

import pdstore.GUID;

public class PDPath {

	public ArrayList<PathElement> list;

	public PDPath(){
		list = new ArrayList<PathElement>();
	}
	
	public int size(){
		return list.size();
	}

	public void addToPath(boolean isRole, GUID id){
		if (!list.isEmpty()){
			PathElement p = list.get(list.size() - 1);
			if (p.getTp() == PathElement.TYPE && isRole){
				list.add(new PathElement(PathElement.ROLE, id));
			}else if (p.getTp() == PathElement.ROLE && !isRole){
				list.add(new PathElement(PathElement.TYPE, id));
			}
		}else{
			if (isRole){
				list.add(new PathElement(PathElement.ROLE, id));
			}else{
				list.add(new PathElement(PathElement.TYPE, id));
			}
		}
	}

	public void removeElement(boolean isRole, GUID id){
		PathElement toberemoved = null;
		for (PathElement p : list){
			int t = 0;
			if (!isRole)
				t = 1;
			if (p.isEqual(t, id)){
				toberemoved = p;
			}
		}
		if (toberemoved == null)
			return;
		list.remove(toberemoved);
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}

	/**
	 * The object of index 0 is boolean isRole, index 1 is the GUID ID;
	 * 
	 * @param i
	 * @return
	 */
	public Object[] getElementAt(int i){
		Object [] ret = new Object[2];
		PathElement p = list.get(i);
		if (p.getTp() == PathElement.TYPE){
			ret[0] = false;
		}else{
			ret[0] = true;
		}
		ret[1] = p.getId();
		return ret;
	}

	public String toString(){
		String s = "PDPath :: ";
		for (PathElement p : list){
			s+= p.toString()+" ";
		}
		return s;
	}

	private class PathElement{
		int tp;
		GUID id;
		final static int ROLE = 0;
		final static int TYPE = 1;

		PathElement(int t, GUID i){
			tp = t;
			id = i;
		}

		public int getTp() {
			return tp;
		}

		public GUID getId() {
			return id;
		}

		public boolean isEqual(int t, GUID i){
			if (tp == t && id.equals(i)){
				return true;
			}
			return false;
		}

		public String toString(){
			return "[PathElement type: "+tp+" | id: "+id+"]";
		}

	}
}
