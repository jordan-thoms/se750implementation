package fluid.ExperimentialpdTable;

import java.util.ArrayList;
import java.util.Collection;

import fluid.util.PDPath;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;

public class PDTableBuffer {

	PDStore store;
	GUID currentModel;
	ArrayList<GUID> columns = new ArrayList<GUID>();
	ArrayList<PDPath> pathBetweenColumns = new ArrayList<PDPath>();
	ArrayList<ArrayList<Object>> columnInst = new ArrayList<ArrayList<Object>>();
	ArrayList<PDRow> pdrows = new ArrayList<PDRow>();

	public PDTableBuffer(PDStore store, String model) {
		this(store , (new PDSimpleWorkingCopy(store)).getId(model));
	}

	public PDTableBuffer(PDStore store, GUID model){
		this.store = store;
		currentModel = model;
		run();
	}

	public void run(){
		GUID t = store.begin();
		Collection<Object> c = store.getInstances(t, currentModel, PDStore.MODELTYPE_ROLEID);
		for (Object o : c){
			findColumnsHeading(t,(GUID)o);
		}
		for (GUID g : columns){
			System.out.println(store.getName(t, g));
		}
		findPathBetweenColumns(t);
		columnInst.add(findAllInstancesOfAccessorOfRole(t, columns.get(0)));
		ArrayList<Object> next = new ArrayList<Object>();
		for (Object oo : columnInst.get(0)){
			next.add(pathWalkToInst(pathBetweenColumns.get(0),t,oo));
		}
	}

	private Object pathWalkToInst(PDPath p,GUID t, Object start){
		Object temp = start;
		for (int step = 0; step < p.size(); step++){
			Object [] element = p.getElementAt(step);
			if ((Boolean)element[0]){
				temp = findNode(t,temp,null,(GUID)element[1]);	;
			}
			System.out.println("path walk "+temp);
		}
		return temp;
	}

	private Object findNode(GUID t, Object inst,GUID type, GUID role){
		Object ins = store.getInstance(t, inst, role.getPartner());
		if (ins == null){
			ins = store.getInstance(t, inst, role);
		}
		if (type != null){
			Collection<Object> instOfType = store.getAllInstancesOfType(t, type);
			boolean isType = false;
			for (Object on : instOfType){
				GUID o = (GUID)on;
				if (o.equals(ins)){
					isType = true;
					break;
				}
			}
			if (!isType){
				System.err.println("Not of type");
			}
		}
		return ins;
	}

	private ArrayList<Object> findAllInstancesOfAccessorOfRole(GUID t, GUID role){
		GUID accessor = store.getAccessorType(t, role);
		Collection<Object> inst = store.getAllInstancesOfType(t, accessor);
		ArrayList<Object> ret = new ArrayList<Object>();
		for (Object o : inst){
			System.out.println(o);
			ret.add(store.getInstance(t, o, role));
		}
		return ret;
	}

	private void findColumnsHeading(GUID t, GUID type){
		Collection<Object> roles = store.getInstances(t, type, PDStore.ACCESSIBLE_ROLES_ROLEID);
		for (Object o : roles){
			GUID other = store.getAccessorType(t, ((GUID)o).getPartner());
			if ((Boolean)store.getInstance(t, other, PDStore.ISPRIMITIVE_ROLEID)){
				columns.add((GUID)o);
			}
		}
	}

	public void findPathBetweenColumns(GUID t){
		for (int i = 0; i < columns.size()-1; i ++){
			GUID current = columns.get(i);
			GUID next = columns.get(i+1);
			PDPath path = new PDPath();
			modelWalk(t, path, current, next);
			if (!path.isEmpty()){
				pathBetweenColumns.add(path);
			}
		}
	}

	private boolean modelWalk(GUID t, PDPath path, GUID current, GUID next) {
		path.addToPath(true, current);
		GUID accessor = store.getAccessorType(t, current);
		path.addToPath(false, accessor);
		Collection<Object> accessible = store.getInstances(t, accessor, PDStore.ACCESSIBLE_ROLES_ROLEID);
		boolean found = false;
		for (Object o : accessible){
			GUID temp = (GUID)o;
			if (!temp.equals(current)){
				if (temp.equals(next)){
					found = true;
					path.addToPath(true, temp);
					System.out.println(path.toString());
				}
			}
		}
		if (!found){
			for (Object o : accessible){
				GUID temp = (GUID)o;
				if (!temp.equals(current)){
					found = modelWalk(t, path, current, next);
					if (found){
						break;
					}
				}
			}
			if (!found){
				path.removeElement(true, current);
				path.removeElement(false, accessor);
			}
		}
		return found;
	}
}
