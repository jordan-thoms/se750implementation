package diagrameditor;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;

public class CreateModel {
	PDStore store = new PDStore("DiagramEditor");
	public final static GUID OPERATION_TYPE_ID = new GUID(
	"920645120d6411e0b45a1cc1dec00ed3");
	public final static GUID SIMPLE_SPATIAL_INFO_TYPE_ID = new GUID(
	"920645110d6411e0b45a1cc1dec00ed3");
	public final static GUID HISTORY_TYPE_ID = new GUID(
	"920645100d6411e0b45a1cc1dec00ed3");
	public final static GUID HISTORY_MODELID = new GUID(
	"92050c900d6411e0b45a1cc1dec00ed3");

	private void createModel() {
		GUID transaction = store.begin();
		
		// Create central History Model
		store.createModel(transaction, CreateModel.HISTORY_MODELID, "HistEdit");
		
		store.createType(transaction, CreateModel.HISTORY_MODELID, CreateModel.HISTORY_TYPE_ID, "History");
		store.createType(transaction, CreateModel.HISTORY_MODELID, CreateModel.OPERATION_TYPE_ID, "Operation");
		
		// Roles for type History
		store.createRelation(transaction, CreateModel.HISTORY_TYPE_ID, "History",
				"Operation", new GUID("920645130d6411e0b45a1cc1dec00ed3"),
				CreateModel.OPERATION_TYPE_ID);
		
		// Roles for type Operation		
		store.createRelation(transaction, CreateModel.OPERATION_TYPE_ID, null, 
				"type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);
		
		store.createRelation(transaction, CreateModel.OPERATION_TYPE_ID, "previous",
				"next", new GUID("7e8d67ca4f4c11e0a5d4842b2b9af4fd"),
				CreateModel.OPERATION_TYPE_ID);
		
		store.createRelation(transaction, CreateModel.OPERATION_TYPE_ID, null, "command",
				new GUID("920645150d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, CreateModel.OPERATION_TYPE_ID, null,
				"superParameter", new GUID("920645160d6411e0b45a1cc1dec00ed3"),
				PDStore.OBJECT_TYPEID);
		

		// Create simple Spatial Info Model
		store.createType(transaction, CreateModel.HISTORY_MODELID, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID,
				"SimpleSpatialInfo");
		
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null,
				"type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null, "x",
				new GUID("920645170d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null, "y",
				new GUID("920645180d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null,
				"height", new GUID("920645190d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null,
				"width", new GUID("9206451a0d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null,
				"color", new GUID("9206451b0d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null,
				"ShapeID", new GUID("9206451c0d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, CreateModel.SIMPLE_SPATIAL_INFO_TYPE_ID, null,
				"targetID", new GUID("9206451d0d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);

		store.commit(transaction);
	}

	public void createDALClasses() {
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		PDGen.generateModel("HistEdit", "apps", copy, "diagrameditor.dal");
	}

	public static void main(String[] args) {
		CreateModel m = new CreateModel();
		m.createModel();
		m.createDALClasses();
	}
}
