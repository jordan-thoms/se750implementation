package pdedit;

import java.util.ArrayList;
import java.util.Collection;

import pdedit.dal.util.ModelAccessor;
import pdstore.GUID;
import pdstore.PDStore;

public class ModelStore {
	/**
	 * Retrieves the current models in the store.
	 * @param store
	 * @return
	 */
	public static Collection<Object> retrieveCurrentModels(PDStore store) {
		GUID t = store.begin();
		Collection<Object> modelInstances = null;
		//TODO this looks like a misuse of the old role ACCESSOR_ROLEID
		//store.getInstances(t, ModelAccessor.IsModel,PDStore.ACCESSOR_ROLEID);
		System.out.println("Number of instances of Model founded: "+modelInstances.size());
		return modelInstances;
	}

	/**
	 * Retrieves the names of the models.
	 * @param store
	 * @return
	 */
	public static ArrayList<String> retrieveModelNames(PDStore store) {
		ArrayList<String> list = new ArrayList<String>();
		GUID t = store.begin();
		Collection<Object> modelInstances = null;
		//TODO this looks like a misuse of the old role ACCESSOR_ROLEID
		//store.getInstances(t, ModelAccessor.IsModel,PDStore.ACCESSOR_ROLEID);
		String temp = "";
		for (Object s : modelInstances){
			temp = (String)store.getInstance(t, s, PDStore.NAME_ROLEID);
			if (temp == null)
				continue;
			if (!temp.equals("PD Metamodel") && !temp.equals("PDEditDiagram")){
				list.add(temp+"|"+s);
			}
		}
		
		return list;
	}
}
