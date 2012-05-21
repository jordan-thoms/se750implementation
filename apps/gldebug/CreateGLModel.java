// Currently not used due to procedurally creating the relationships currently seeming to be the better idea

package gldebug;

import pdstore.*;
//import pdstore.dal.*; // Currently not used

public class CreateGLModel
{
	public final static GUID glStateModel = new GUID("27c594c0521011e186b450e5495bfcc0");
	
	public final static GUID sessionType = new GUID("6868b8207ed611e1855d50e5495bfcc0");
	public final static GUID stateType = new GUID("27c5bbd0521011e186b450e5495bfcc0"); // Represents a state made up of a number of state variables, sits at the root of the state tree that doesn't actually hold any state data
	public final static GUID stateVariableType = new GUID("4a0a79c0876611e1a67950e5495bfcc0"); // Represents generalised state variables
	public final static GUID glCallType = new GUID("efa3f2a0951011e1a0f950e5495bfcc0");
	
	public final static GUID stateRole = new GUID("7f3857af7f2211e1a8c750e5495bfcc0");
	
	public final static GUID transactionIDRole = new GUID("f9eaf6a07e5911e189a950e5495bfcc0"); // TODO: document
	public final static GUID firstTransactionIDRole = new GUID("fc0ce4008b8511e1861750e5495bfcc0"); // Role for pointing to the first transactionID
	public final static GUID lastTransactionIDRole = new GUID("fc0d0b118b8511e1861750e5495bfcc0"); // Role for pointing to the current final transactionID
	public final static GUID nextTransactionIDRole = new GUID("fc0d0b108b8511e1861750e5495bfcc0"); // Role for indicating the next transaction ID given one
	
	public final static GUID glCallRole = new GUID("efa419b0951011e1a0f950e5495bfcc0");
	public final static GUID firstGlCallRole = new GUID("efa419b1951011e1a0f950e5495bfcc0");
	public final static GUID lastGlCallRole = new GUID("efa419b2951011e1a0f950e5495bfcc0");
	public final static GUID nextGlCallRole = new GUID("efa419b3951011e1a0f950e5495bfcc0");
	
	public final static GUID firstGlCallOrTransactionRole = new GUID("efa419b4951011e1a0f950e5495bfcc0");
	public final static GUID lastGlCallOrTransactionRole = new GUID("efa419b5951011e1a0f950e5495bfcc0");
	public final static GUID nextGlCallOrTransactionRole = new GUID("efa419b6951011e1a0f950e5495bfcc0");
	
	public final static GUID timeStampRole = new GUID("686954917ed611e1855d50e5495bfcc0");
	
	public final static GUID stateVariableValueRole = new GUID("5efd4c6087be11e1817150e5495bfcc0");
	public final static GUID stateVariableNonuniqueNameRole = new GUID("c88cb8a087c811e195ee50e5495bfcc0");
	public final static GUID childStateVariableRole = new GUID("4a0a79c1876611e1a67950e5495bfcc0");

	public static void main(String[] args)
	{
		PDStore store = new PDStore("MyGlStateDatabase");
		GUID transaction = store.begin();

		// create a new model
		store.createModel(transaction, glStateModel, "GLModel");

		// create the new complex types
		store.createType(transaction, glStateModel, stateType, "State");
		store.createType(transaction, glStateModel, stateVariableType, "StateVariable");
		store.createType(transaction, glStateModel, sessionType, "SessionData");
			
		// Set up relationships for the sessionType
		store.createRelation(transaction, sessionType, "session", "state", stateRole, stateType);
		
		store.createRelation(transaction, sessionType, "session", "transactionID", transactionIDRole, PDStore.TRANSACTION_TYPEID);
		store.createRelation(transaction, sessionType, "session", "firstTransactionID", firstTransactionIDRole, PDStore.TRANSACTION_TYPEID);
		store.createRelation(transaction, sessionType, "session", "lastTransactionID", lastTransactionIDRole, PDStore.TRANSACTION_TYPEID);
		
		store.createRelation(transaction, sessionType, "session", "glCall", glCallRole, glCallType);
		store.createRelation(transaction, sessionType, "session", "firstGlCall", firstGlCallRole, glCallType);
		store.createRelation(transaction, sessionType, "session", "lastGlCall", lastGlCallRole, glCallType);
		
		store.createRelation(transaction, sessionType, "session", "firstGlCallOrTransaction", firstGlCallOrTransactionRole, PDStore.GUID_TYPEID);
		store.createRelation(transaction, sessionType, "session", "lastGlCallOrTransaction", lastGlCallOrTransactionRole, PDStore.GUID_TYPEID);
		
		store.createRelation(transaction, sessionType, null, "sessionStartTime", timeStampRole, PDStore.TIMESTAMP_TYPEID);
		
		// Set up relationships for the stateType
		store.createRelation(transaction, stateType, null, "timeStamp", timeStampRole, PDStore.TIMESTAMP_TYPEID);
		store.createRelation(transaction, stateType, "state", "stateVariable", childStateVariableRole, stateVariableType); // The state keeps the top level state variables
		
		// Set up relationships for the stateVariableType
		store.createRelation(transaction, stateVariableType, null, "stateVariableValue", stateVariableValueRole, PDStore.STRING_TYPEID);
		store.createRelation(transaction, stateVariableType, null, "stateVariableNonuniqueName", stateVariableNonuniqueNameRole, PDStore.STRING_TYPEID);
		store.createRelation(transaction, stateVariableType, "parentStateVariable", "childStateVariable", childStateVariableRole, stateVariableType);
		
		store.createRelation(transaction, glCallRole, "glCall", "timeStamp", timeStampRole, PDStore.TIMESTAMP_TYPEID);
		
		store.commit(transaction);
	}
}
