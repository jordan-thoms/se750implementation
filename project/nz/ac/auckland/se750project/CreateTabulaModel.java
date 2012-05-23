package nz.ac.auckland.se750project;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

public class CreateTabulaModel {


	// GUID of PDModel
	private static final GUID MODEL = new GUID("6b599f6038d511e094e4001e8c7f9d82");
	
	// GUIDs of type page and action
	private static final GUID DATA_SET = new GUID("fbc29377374c11e0bfe4001e8c7f9d82");
	private static final GUID DATA_RECORD = new GUID("661937aaa3f111e19924742f68b11197");

	private static final GUID DATARECORD_MEMBER_ROLE = new GUID("005319b036ed11e08144001e8c7f9d82");
	
	private static final GUID ROW1_ROLE = new GUID("005319b136ed11e08144001e8c7f9d82");
	private static final GUID ROW2_ROLE = new GUID("005319b236ed11e08144001e8c7f9d82");
	private static final GUID ROW3_ROLE = new GUID("005319b336ed11e08144001e8c7f9d82");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		PDStore store = new PDStore("Tabula");
		PDStore store = PDStore.connectToServer("localhost");

		createModel(store);
		// use the PDGen class to automatically create PDPage and PDAction
		// classes with their get/set/add methods
//		PDWorkingCopy wc = new PDSimpleWorkingCopy(store);
//		PDGen.generateModel("loadmodel", "project", wc, "nz.ac.auckland.se750project.dal");
	}
	
	public static void createModel(PDStore store) {
		GUID transaction = store.begin();
		
		
		store.createModel(transaction, CreateTabulaModel.MODEL, "loadmodel");
		
		// Create Page and Action types
		store.createType(transaction, CreateTabulaModel.MODEL, CreateTabulaModel.DATA_SET, "DataSet");
		store.createType(transaction, CreateTabulaModel.MODEL, CreateTabulaModel.DATA_RECORD, "DataRecord");
		
		store.createRelation(transaction, DATA_SET, "contained_in", "record", DATARECORD_MEMBER_ROLE, DATA_RECORD);

		
		// Create roles of page and action
		store.createRelation(transaction, DATA_RECORD, null, "Row1", ROW1_ROLE, PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, DATA_RECORD, null, "Row2", ROW2_ROLE, PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, DATA_RECORD, null, "Row3", ROW3_ROLE, PDStore.INTEGER_TYPEID);
		
		// Commit all the changes to pdstore Loadtest.pds
		store.commit(transaction);
	}

}
