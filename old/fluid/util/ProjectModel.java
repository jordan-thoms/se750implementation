package fluid.util;

import java.util.Collection;

import pdstore.GUID;
import pdstore.PDStore;

public class ProjectModel {
	public static final GUID Project = new GUID("59c3ed56cb5711dfaae30026b0db3c2e");
	public static final GUID ProjectModel = new GUID("59c3ed50cb5711dfaae30026b0db3c2e");
	public static final GUID DefaultModel = new GUID("59c3ed51cb5711dfaae30026b0db3c2e");
	public static final GUID ProjectHasName = new GUID("59c3ed52cb5711dfaae30026b0db3c2e");
	public static final GUID ProjectHasDefaultModel = new GUID("59c3ed59cb5711dfaae30026b0db3c2e");
	public static final GUID DefaultModelHasName = new GUID("59c3ed53cb5711dfaae30026b0db3c2e");
	public static final GUID DefaultModelHasGUID = new GUID("59c3ed54cb5711dfaae30026b0db3c2e");
	public static final String Name = "ProjectModel";
	public static final String ProjectType = "Project";
	public static final String DeMoType = "DefaultModel";
	
	public static void Load(PDStore store){
		GUID transaction = store.begin();
		Collection <Object> list = store.getAllInstancesOfType(transaction, PDStore.MODEL_TYPEID);
		boolean found = false;
		for (Object o : list){
			String name = (String)store.getInstance(transaction, o, PDStore.NAME_ROLEID);
			if (name.equals(Name)){
				found = true;
				break;
			}
		}
		store.commit(transaction);
		if (!found){
			System.out.println("Model not founded creating model");
			createModel(store);
		}
	}

	private static void createModel(PDStore store) {
		GUID transaction = store.begin();
		store.createModel(transaction, ProjectModel, Name);
		store.createType(transaction, ProjectModel, Project, "Project");
		store.createType(transaction, ProjectModel, DefaultModel, "DefaultModel");
		store.createRelation(transaction, Project, "defaultModelIsSetToProject", "projectHasDefaultModel", ProjectHasDefaultModel, DefaultModel);
		store.createRelation(transaction, Project, "", "projectHasName", ProjectHasName, PDStore.STRING_TYPEID);
		store.createRelation(transaction, DefaultModel, "", "defaultModelHasName", DefaultModelHasName, PDStore.STRING_TYPEID);
		store.createRelation(transaction, DefaultModel, "", "defaultModelHasGUID", DefaultModelHasGUID, PDStore.STRING_TYPEID);
		store.commit(transaction);
	}
	
	public static void addNewProject(PDStore store, String prjName, DataBox defaultModel){
		GUID transaction = store.begin();
		GUID pro = new GUID();
		GUID def = new GUID();
		store.addLink(transaction, pro, ProjectHasDefaultModel, def);
		store.addLink(transaction, pro, ProjectHasName, prjName);
		store.addLink(transaction, def, DefaultModelHasName, defaultModel.getElement());
		store.addLink(transaction, def, DefaultModelHasGUID, defaultModel.getID().toString());
		store.commit(transaction);
	}
}
