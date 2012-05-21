package texteditor;

import static org.junit.Assert.fail;

import java.io.File;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;

public class CreateModel {
	PDStore store = new PDStore("TextEditor");

	private void createModel() {
		GUID transaction = store.begin();
		GUID modelId = new GUID("3bef955054d111e09c2a001e6805726d");
		GUID historyTypeId = new GUID("3c08266054d111e09c2a001e6805726d");
		//GUID simpleSpatialInfoTypeId = new GUID("920645110d6411e0b45a1cc1dec00ed3");
		GUID operationTypeId = new GUID("3c08266154d111e09c2a001e6805726d");
		GUID wordTypeId = new GUID("3c08266254d111e09c2a001e6805726d");
		GUID insertTypeId = new GUID("3c08266354d111e09c2a001e6805726d");
		GUID deleteTypeId = new GUID("3c08266454d111e09c2a001e6805726d");
		GUID cutTypeId = new GUID("3c08266554d111e09c2a001e6805726d");
		GUID copyTypeId = new GUID("3c08266654d111e09c2a001e6805726d");

		// Create central History Model
		store.createModel(transaction, modelId, "HistEdit");
		store.createType(transaction, modelId, historyTypeId, "History");
		store.createType(transaction, modelId, operationTypeId, "Operation");
		store.createRelation(transaction, historyTypeId, "History",
				"Operation", new GUID("3c08266754d111e09c2a001e6805726d"),
				operationTypeId);
		store.createRelation(transaction, operationTypeId, null, "timeStamp",
				new GUID("3c08266854d111e09c2a001e6805726d"),
				PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, operationTypeId, null, "command",
				new GUID("3c08266954d111e09c2a001e6805726d"),
				PDStore.STRING_TYPEID);
		store.createRelation(transaction, operationTypeId, null,
				"superParameter", new GUID("3c08266a54d111e09c2a001e6805726d"),
				PDStore.OBJECT_TYPEID);
		store.createRelation(transaction, operationTypeId, null, "user", 
				new GUID("913dc29d5a6711e0b82c001e6805726d"), PDStore.STRING_TYPEID);

		store.createType(transaction, modelId, wordTypeId, "Word");
		store.createRelation(transaction, wordTypeId, null, "text", new GUID("3c08266b54d111e09c2a001e6805726d"),
				PDStore.STRING_TYPEID);
		
		store.createType(transaction, modelId, insertTypeId, "Insert");
		store.createRelation(transaction, insertTypeId, null, "word", new GUID("3c08266c54d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, insertTypeId, null, "after", new GUID("3c08266d54d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, insertTypeId, null, "type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);

		store.createType(transaction, modelId, deleteTypeId, "Delete");
		store.createRelation(transaction, deleteTypeId, null, "word", new GUID("3c08266e54d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, deleteTypeId, null, "type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);
		
		store.createType(transaction, modelId, cutTypeId, "Cut");
		store.createRelation(transaction, cutTypeId, null, "word", new GUID("3c08266f54d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, cutTypeId, null, "type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);
		
		store.createType(transaction, modelId, copyTypeId, "Copy");
		store.createRelation(transaction, copyTypeId, null, "originalWord", new GUID("3c08267154d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, copyTypeId, null, "newWord", new GUID("3c08267254d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, copyTypeId, null, "toAfter", new GUID("3c08267354d111e09c2a001e6805726d"), wordTypeId);
		store.createRelation(transaction, copyTypeId, null, "type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);

		store.commit(transaction);
	}

	public void createDALClasses() {
		GUID transaction = store.begin();
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		PDGen.generateModel("HistEdit", "apps", copy, "texteditor.dal");
		store.commit(transaction);
	}

	public static void main(String[] args) {
		CreateModel m = new CreateModel();
		m.createModel();
		m.createDALClasses();
	}
}
