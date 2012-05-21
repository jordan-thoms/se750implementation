package texteditor;

import static org.junit.Assert.fail;

import java.io.File;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;

public class CreateHistEditModel {
	PDStore store = new PDStore("histedit");

	private void createModel() {
		GUID transaction = store.begin();
		GUID modelId = new GUID("92050c900d6411e0b45a1cc1dec00ed3");
		GUID historyTypeId = new GUID("920645100d6411e0b45a1cc1dec00ed3");
		GUID simpleSpatialInfoTypeId = new GUID(
				"920645110d6411e0b45a1cc1dec00ed3");
		GUID operationTypeId = new GUID("920645120d6411e0b45a1cc1dec00ed3");

		// Create central History Model
		store.createModel(transaction, modelId, "HistEdit");
		store.createType(transaction, modelId, historyTypeId, "History");
		store.createType(transaction, modelId, operationTypeId, "Operation");
		store.createRelation(transaction, historyTypeId, "History",
				"Operation", new GUID("920645130d6411e0b45a1cc1dec00ed3"),
				operationTypeId);
		store.createRelation(transaction, operationTypeId, null, "timeStamp",
				new GUID("920645140d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, operationTypeId, null, "command",
				new GUID("920645150d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, operationTypeId, null,
				"superParameter", new GUID("920645160d6411e0b45a1cc1dec00ed3"),
				PDStore.OBJECT_TYPEID);

		// Create simple Spatial Info Model
		store.createType(transaction, modelId, simpleSpatialInfoTypeId,
				"SimpleSpatialInfo");
		store.createRelation(transaction, simpleSpatialInfoTypeId, null,
				"type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null, "x",
				new GUID("920645170d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null, "y",
				new GUID("920645180d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null,
				"height", new GUID("920645190d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null,
				"width", new GUID("9206451a0d6411e0b45a1cc1dec00ed3"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null,
				"color", new GUID("9206451b0d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null,
				"ShapeID", new GUID("9206451c0d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, simpleSpatialInfoTypeId, null,
				"targetID", new GUID("9206451d0d6411e0b45a1cc1dec00ed3"),
				PDStore.STRING_TYPEID);

		store.commit(transaction);
	}

	public void createDALClasses() {
		GUID transaction = store.begin();
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		PDGen.generateModel("HistEdit", "apps", copy, "pdstore.histedit.dal");
		store.commit(transaction);
	}

	public static void main(String[] args) {
		CreateHistEditModel m = new CreateHistEditModel();
		m.createModel();
		m.createDALClasses();
	}
}
