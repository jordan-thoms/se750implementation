package angil;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

/**
 * The MakePDModel class is developed to generate PDModel of Angil system. Two
 * user-defined PD types are defined - Page and Action. The user-defined types
 * need static GUIDs. The roles between them need static GUIDs.
 * 
 * @author gaozhan
 * 
 */
public class CreateAngilModel {
	// Give USER_ROLE,SYSTEM_ROLE, REQUEST_URL_ROLE, PROBABILITY_ROLE static
	// GUID
	private static final GUID USER_ROLE = new GUID(
			"005319b036ed11e08144001e8c7f9d82");
	private static final GUID SYSTEM_ROLE = new GUID(
			"005319b136ed11e08144001e8c7f9d82");
	private static final GUID REQUEST_URL_ROLE = new GUID(
			"005319b236ed11e08144001e8c7f9d82");
	private static final GUID PROBABILITY_ROLE = new GUID(
			"005319b336ed11e08144001e8c7f9d82");

	// GUIDs of type page and action
	private static final GUID PAGE = new GUID(
			"fbc29377374c11e0bfe4001e8c7f9d82");
	private static final GUID ACTION = new GUID(
			"fbc29378374c11e0bfe4001e8c7f9d82");

	// GUID of PDModel
	private static final GUID MODEL = new GUID(
			"6b599f6038d511e094e4001e8c7f9d82");

	/**
	 * The starting point of this class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create new PDStore instance
		PDStore store = new PDStore("Loadtest");
		
		// Start the PDStore and return the PDStore transaction GUID.
		GUID transaction = store.begin();
		store.createModel(transaction, CreateAngilModel.MODEL, "loadmodel");
		
		// Create Page and Action types
		store.createType(transaction, CreateAngilModel.MODEL, CreateAngilModel.PAGE, "Page");
		store.createType(transaction, CreateAngilModel.MODEL, CreateAngilModel.ACTION, "Action");
		
		// Create roles of page and action
		store.createRelation(transaction, CreateAngilModel.PAGE, null, "nextAction", CreateAngilModel.USER_ROLE,
				CreateAngilModel.ACTION);
		store.createRelation(transaction, CreateAngilModel.ACTION, null, "nextPage",
				CreateAngilModel.SYSTEM_ROLE, CreateAngilModel.PAGE);
		store.createRelation(transaction, CreateAngilModel.ACTION, null, "requestURL",
				CreateAngilModel.REQUEST_URL_ROLE, PDStore.STRING_TYPEID);
		store.createRelation(transaction, CreateAngilModel.ACTION, null, "probability",
				CreateAngilModel.PROBABILITY_ROLE, PDStore.DOUBLE_PRECISION_TYPEID);
		
		// Commit all the changes to pdstore Loadtest.pds
		store.commit(transaction);
		
		// use the PDGen class to automatically create PDPage and PDAction
		// classes with their get/set/add methods
		PDWorkingCopy wc = new PDSimpleWorkingCopy(store);
		PDGen.generateModel("loadmodel", "apps", wc, "angil.dal");
	}
}
