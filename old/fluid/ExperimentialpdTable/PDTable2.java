package fluid.ExperimentialpdTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;

public class PDTable2 {
	PDStore store;
	GUID currentModel;
	
	public PDTable2(PDStore store, String model) {
		this(store , (new PDSimpleWorkingCopy(store)).getId(model));
	}

	public PDTable2(PDStore store, GUID model){
		this.store = store;
		currentModel = model;
		initRun();
	}
	
	private void initRun(){
		// Find all complex types and choose one
		Object anchorType = findAnchorComplexType();
		Collection <Object> cols = findAllAccessibleRoles(anchorType);
		
		GUID t = store.begin();
		Collection <Object> anchorInstances = store.getAllInstancesOfType(t, (GUID)anchorType);
		for(Object col : cols){
			System.out.println(store.getName(t, (GUID)col));
			findInstancesOfRoleFromTypeInstance(anchorInstances,(GUID)col);
		}
		store.commit(t);
	}

	private Object findAnchorComplexType() {
		GUID t = store.begin();
		Collection<Object> c = store.getInstances(t, currentModel, PDStore.MODELTYPE_ROLEID);
		store.commit(t);
		return c.iterator().next();
	}
	
	private Collection<Object> findAllAccessibleRoles(Object type){
		GUID t = store.begin();
		Collection<Object> columns = new ArrayList<Object>();
		Collection<Object> roles = store.getInstances(t, type, PDStore.ACCESSIBLE_ROLES_ROLEID);
		for (Object o : roles){
			GUID other = store.getAccessorType(t, ((GUID)o).getPartner());
			if ((Boolean)store.getInstance(t, other, PDStore.ISPRIMITIVE_ROLEID)){
				columns.add((GUID)o);
			}
		}
		store.commit(t);
		return roles;
	}
	
	private HashMap<GUID, Collection<Object>> findInstancesOfRoleFromTypeInstance(Collection<Object> instances, GUID role){
		GUID t = store.begin();
		HashMap<GUID, Collection<Object>> type2Role = new HashMap<GUID, Collection<Object>>();
		for (Object obj : instances){
			type2Role.put((GUID)obj,  store.getInstances(t, obj, role));
			for (Object o : store.getInstances(t, obj, role)){
				System.out.println(o);
			}
		}
		store.commit(t);
		return type2Role;
	}
}
