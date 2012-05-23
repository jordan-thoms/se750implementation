package pdstore;

import java.awt.Graphics2D;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import nz.ac.auckland.se.genoupe.tools.Debug;
import pdstore.changelog.ChangeLogStore;
import pdstore.changelog.PrimitiveType;
import pdstore.concurrent.ConcurrentStore;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.GenericPDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;
import pdstore.sparql.Variable;

/**
 * PDStore is the convenience class that is mostly used to set up a PDStore
 * connection. Following the principle of separation of concerns, the
 * functionality of PDStore is implemented in a series of Classes that act
 * together in a tier architecture. The clients only connect PDStore, but
 * benefit from the underlying classes:
 * 
 * The main description of the functional features can be found in the base
 * interface GenericPDStore. The description of the non-functional properties
 * can be found in the following classes. - The class ConcurrentStore describes
 * the concurrency properties of PDStore. - The class LogAndIndexStore describes
 * the properties of efficient access. The PDStore class can be used for local
 * file-based stores, in which cases it serves as an embedded database, or for
 * remote connections, in which case it serves as an improved proxy object and
 * primarily removes annoying Java Remote Exceptions.
 * 
 * @author Christof, Gerald
 * 
 */
public class PDStore extends GenericPDStore<GUID, Object, GUID> {

	static {
		Debug.addDebugTopic("PDStore");
	}

	/** GUIDs for primitive types **/
	public final static GUID BLOB_TYPEID = new GUID(
			"4f8a986c4062db11afc0b95b08f50e2f");
	public final static GUID BOOLEAN_TYPEID = new GUID(
			"4d8a986c4062db11afc0b95b08f50e2f");
	public final static GUID CHAR_TYPEID = new GUID(
			"508a986c4062db11afc0b95b08f50e2f");
	public final static GUID DOUBLE_PRECISION_TYPEID = new GUID(
			"4c8a986c4062db11afc0b95b08f50e2f");
	public final static GUID GUID_TYPEID = new GUID(
			"538a986c4062db11afc0b95b08f50e2f");
	public final static GUID INTEGER_TYPEID = new GUID(
			"4b8a986c4062db11afc0b95b08f50e2f");
	public final static GUID STRING_TYPEID = new GUID(
			"4a8a986c4062db11afc0b95b08f50e2f");
	public final static GUID TIMESTAMP_TYPEID = new GUID(
			"4e8a986c4062db11afc0b95b08f50e2f");
	public final static GUID IMAGE_TYPEID = new GUID(
			"d19fffbbf28bdb118ab1d56a70f8a30f");

	/** GUIDs for complex types **/
	public final static GUID TYPE_TYPEID = new GUID(
			"518a986c4062db11afc0b95b08f50e2f");
	public final static GUID ROLE_TYPEID = new GUID(
			"528a986c4062db11afc0b95b08f50e2f");
	public final static GUID REPOSITORY_TYPEID = new GUID(
			"6d35b690232e11e1a16400235411d565");
	public final static GUID MODEL_TYPEID = new GUID(
			"31c54c264e6fdd11a5dba737f860105f");
	public final static GUID BRANCH_TYPEID = new GUID(
			"5921e0a079b811dfb27f002170295281");
	public final static GUID TRANSACTION_TYPEID = new GUID(
			"5921e0a179b811dfb27f002170295281");
	public final static GUID OBJECT_TYPEID = new GUID(
			"70da26e0fc3711dfa87b842b2b9af4fd");
	
	public final static GUID CHANGE_TYPEID = new GUID(
			"005422218cf711e1926b842b2b9af4fd");
	
	public final static GUID OPERATION_TYPEID = new GUID(
			"dbe74a2f89cb11e197441cc1dec00ed3");
	public final static GUID WIDGET_TYPEID = new GUID(
			"005422208cf711e1926b842b2b9af4fd");

	
	/** GUIDs for roles **/
	public final static GUID OWNED_ROLE_ROLEID = new GUID(
			"648a986c4062db11afc0b95b08f50e2f");
	public final static GUID OWNER_TYPE_ROLEID = OWNED_ROLE_ROLEID.getPartner();
	public final static GUID ICON_ROLEID = new GUID(
			"88bf14821704dc11b933e6037c01b10a");
	public final static GUID NAME_ROLEID = new GUID(
			"84bf14821704dc11b933e6037c01b10a");
	public final static GUID DESCRIPTION_ROLEID = new GUID(
			"ca5984600b7c11e19db300224300a31a");
	public final static GUID PARTNER_ROLEID = new GUID(
			"6d8a986c4062db11afc0b95b08f50e2f");
	public final static GUID MODELTYPE_ROLEID = new GUID(
			"54134d264e6fdd11a5dba737f860105f");
	public final static GUID ISPRIMITIVE_ROLEID = new GUID(
			"5d8a986c4062db11afc0b95b08f50e2f");
	public final static GUID PARENT_TRANSACTION_ROLEID = new GUID(
			"5921e0a279b811dfb27f002170295281");
	public final static GUID PARENTTRANSACTION_CHILDBRANCH_ROLEID = PARENT_TRANSACTION_ROLEID
			.getPartner();
	public final static GUID BRANCH_ROLEID = new GUID(
			"5921e0a479b811dfb27f002170295281");
	public final static GUID TRANSACTION_ROLEID = BRANCH_ROLEID.getPartner();
	public final static GUID MIN_MULT_ROLEID = new GUID(
			"678a986c4062db11afc0b95b08f50e2f");
	public final static GUID MAX_MULT_ROLEID = new GUID(
			"698a986c4062db11afc0b95b08f50e2f");
	public final static GUID REPOSITORYMODEL_ROLEID = new GUID(
			"d2911880a5cc11dfa9e40026bb06e946");
	public final static GUID HAS_TYPE_ROLEID = new GUID(
			"ee32adf0f68b11df860e1cc1dec00ed3");
	public final static GUID ISOLATIONLEVEL_ROLEID = new GUID(
			"878e3970134d11e1b3151cc1dec00ed3");
	public final static GUID DURABLE_TRANSACTION_ROLEID = new GUID(
			"878e396d134d11e1b3151cc1dec00ed3");
	
	public final static GUID CHANGE_TRANSACTION_ROLEID = new GUID(
			"0054221f8cf711e1926b842b2b9af4fd");
	public final static GUID CHANGE_CHANGETYPE_ROLEID = new GUID(
			"0054221e8cf711e1926b842b2b9af4fd");
	public final static GUID CHANGE_INSTANCE1_ROLEID = new GUID(
			"0054221d8cf711e1926b842b2b9af4fd");
	public final static GUID CHANGE_ROLE2_ROLEID = new GUID(
			"0054221c8cf711e1926b842b2b9af4fd");
	public final static GUID CHANGE_INSTANCE2_ROLEID = new GUID(
			"0054221b8cf711e1926b842b2b9af4fd");

	public final static GUID IS_NOT_IN_INDEX_ROLEID = new GUID(
			"730324c0154811e1b4f61cc1dec00ed3");
	public final static GUID IS_NOT_IN_LOG_ROLEID = new GUID(
			"73034bd1154811e1b4f61cc1dec00ed3");

	public final static GUID USES_ROLE_ROLEID = new GUID(
			"cdc181d1319311e1b10c1cc1dec00ed3").getFirst();

	public final static GUID PARAMETER_TYPE_ROLEID = 
			new GUID("dbe74a3089cb11e197441cc1dec00ed3");
	public final static GUID OPERATION_IMPLEMENTATION_ROLEID = 
			 new GUID("dbe74a3189cb11e197441cc1dec00ed3");

	public final static GUID WIDGET_IMPLEMENTATION_ROLEID = 
		 new GUID("0054221a8cf711e1926b842b2b9af4fd");
	public final static GUID VISUALIZED_BY_ROLEID = 
		 new GUID("005422198cf711e1926b842b2b9af4fd");

	
	/** GUIDs for instances **/
	public final static GUID PDMETAMODEL_ID = new GUID(
			"55134d264e6fdd11a5dba737f860105f");
	
	/**
	 * GUID for the transaction that adds the metamodel. This is the same for
	 * all databases. By using getFirst() we ensure that the GUID is a durable
	 * transaction ID.
	 */
	public final static GUID METAMODEL_TRANSACTION = new GUID(
			"4c3397f01e4211e1b51b00188b6fa587").getFirst();

	/** Defaults for data file name **/
	public static final String DEFAULT_FILEPATH = "pddata/";
	public static final String DEFAULT_FILENAME = "pdstore";
	public static final String DEFAULT_EXTENSION = ".pds";
	public static final String DEFAULT_FILELOCATION = DEFAULT_FILEPATH
			+ DEFAULT_FILENAME + DEFAULT_EXTENSION;

	private String fileName;
	private String filePath;
	private String extension;

	/**
	 * This constructor creates a PDStore just as a facade to use the
	 * convenience functions, does not do the initPDStore().
	 * 
	 * @param store
	 */
	public PDStore(PDStoreI<GUID, Object, GUID> store) {
		this.store = store;
	}

	public PDStore(String fileName, String filePath, String extension)
			throws PDStoreException {
		this.fileName = fileName;
		this.filePath = filePath;
		this.extension = extension;
		this.store = new ConcurrentStore(new ChangeLogStore(filePath + fileName
				+ extension));
		initPDStore();
	}

	/**
	 * It is preferred that the single-string Constructor adds a filepath and in
	 * particular the default extension.
	 * 
	 * @param fileName
	 * @throws PDStoreException
	 */
	public PDStore(String fileName) throws PDStoreException {
		this(fileName, DEFAULT_FILEPATH, DEFAULT_EXTENSION);
	}

	public PDStore() {
		
	}

	private void initPDStore() {
		// add the metamodel to the index
		// TODO should the metamodel have a normal begin-ID, instead of having
		// METAMODEL_TRANSACTION as begin-ID and durable ID?
		GUID transactionId = METAMODEL_TRANSACTION;
		Transaction<GUID, Object, GUID> metamodelTransaction = new Transaction<GUID, Object, GUID>(
				transactionId);
		metamodelTransaction.setDurableId(METAMODEL_TRANSACTION);
		metamodelTransaction.setPersistenceLevel(PersistenceLevel.INDEX_ONLY);
		begin(metamodelTransaction);
		addMetamodel(transactionId);
		commit(transactionId);

		// merge metamodel branch into default branch
		transactionId = GUID.newTransactionId(getRepository().getBranchID());
		Transaction<GUID, Object, GUID> transaction = new Transaction<GUID, Object, GUID>(
				transactionId);
		transaction.setPersistenceLevel(PersistenceLevel.INDEX_ONLY);
		begin(transaction);
		merge(transactionId, METAMODEL_TRANSACTION);
		commit(transactionId);

		// add the listener & interceptor dispatchers
		getDetachedListenerList().add(listenerDispatcher);
		getInterceptorList().add(interceptorDispatcher);
		immediateDispatcher.getMatchingTemplates()
		   .add(new PDChange<GUID, Object, GUID>(ChangeType.LINK_NOW_ADDED, null, null, null, null));
		getInterceptorList().add(immediateDispatcher);
		// soon there might be a READ changetype
//		readDispatcher.getMatchingTemplates()
//		   .add(new PDChange<GUID, Object, GUID>(ChangeType.LINK_READ, null, null, null, null));
		getInterceptorList().add(readDispatcher);

		// make sure there is a PDStore for debugging
		debugStore = this;
	}

	public static PDStore connectToServer(String host) throws PDStoreException {
		return new pdstore.rmi.PDStore(host);
	}

	public static PDStore connectToServer(String host, String rmiKey)
			throws PDStoreException {
		return new pdstore.rmi.PDStore(host, rmiKey);
	}

	/**
	 * Gets all the instances of the given type that are stored in the database.
	 * 
	 * @param transactionId
	 *            transaction id on which to search
	 * @param typeid
	 *            ID of the type
	 * @return all stored instances of that type
	 * @throws PDStoreException
	 */
	public Collection<Object> getAllInstancesOfType(GUID transaction, GUID type)
			throws PDStoreException {
		return getInstances(transaction, type, HAS_TYPE_ROLEID.getPartner());
	}

	/**
	 * @see PDWorkingCopy#getId(String)
	 * @param transaction
	 *            - current transaction id
	 * @param name
	 * @return
	 * @throws PDStoreException
	 */
	public GUID getId(GUID transaction, String name) throws PDStoreException {
		return (GUID) getInstance(transaction, name,
				PDStore.NAME_ROLEID.getPartner());
	}

	/**
	 * @see PDWorkingCopy#getIds(String)
	 * @param transaction
	 *            - current transaction id
	 * @param name
	 * @return
	 * @throws PDStoreException
	 */
	public Collection<GUID> getIds(GUID transaction, String name)
			throws PDStoreException {
		Set<GUID> Ids = new HashSet<GUID>();
		for (Object result : store.getInstances(transaction, name,
				PDStore.NAME_ROLEID.getPartner()))
			Ids.add((GUID) result);
		return Ids;
	}

	/**
	 * @see PDWorkingCopy#getName(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @return
	 * @throws PDStoreException
	 */
	public String getName(GUID transaction, Object instance)
			throws PDStoreException {
		return (String) getInstance(transaction, instance, PDStore.NAME_ROLEID);
	}

	/**
	 * Returns the name of the given instance if it has one, otherwise the
	 * String representation of the instance value.
	 * 
	 * @param transaction
	 * @param instance
	 * @return
	 */
	public String getNameOrValue(GUID transaction, Object instance) {
		String name = getName(transaction, instance);
		if (name != null)
			return name;

		return instance.toString();
	}

	/**
	 * @see PDWorkingCopy#setName(GUID, String)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @param name
	 * @throws PDStoreException
	 */
	public void setName(GUID transaction, Object instance, String name)
			throws PDStoreException {
		setLink(transaction, instance, PDStore.NAME_ROLEID, name);
	}

	/**
	 * @see PDWorkingCopy#removeName(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @throws PDStoreException
	 */
	public void removeName(GUID transaction, Object instance)
			throws PDStoreException {
		String name = getName(transaction, instance);
		if (name != null)
			removeLink(transaction, instance, PDStore.NAME_ROLEID, name);
	}

	/**
	 * @see PDWorkingCopy#getIcon(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @return
	 * @throws PDStoreException
	 */
	public Blob getIcon(GUID transaction, Object instance)
			throws PDStoreException {
		return (Blob) getInstance(transaction, instance, PDStore.ICON_ROLEID);
	}

	/**
	 * @see PDWorkingCopy#setIcon(GUID, Blob)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @param icon
	 * @throws PDStoreException
	 */
	public void setIcon(GUID transaction, Object instance, Blob icon)
			throws PDStoreException {
		setLink(transaction, instance, PDStore.ICON_ROLEID, icon);
	}

	/**
	 * Removes the icon of the given instance using the given transaction.
	 * 
	 * @see PDWorkingCopy#removeIcon(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @throws PDStoreException
	 */
	public void removeIcon(GUID transaction, Object instance)
			throws PDStoreException {
		Blob icon = getIcon(transaction, instance);
		if (icon != null)
			removeLink(transaction, instance, PDStore.ICON_ROLEID, icon);
	}

	/**
	 * Provides a String with data about the given instance.
	 * 
	 * @param transaction
	 * @param instance
	 */
	String instanceToString(GUID transaction, Object instance) {
		GUID type = getType(transaction, instance);
		Collection<GUID> accessibleRoles = getAccessibleRoles(transaction, type);

		String result = "Instance " + getNameOrValue(transaction, instance)
				+ " has type " + getNameOrValue(transaction, type)
				+ " and accessible roles with values:\n";

		for (GUID role : accessibleRoles) {
			Collection<Object> values = getInstances(transaction, instance,
					role);

			result += getNameOrValue(transaction, role) + ": ";
			for (Object v : values)
				result += getNameOrValue(transaction, v) + ", ";
			result += "\n";
		}
		return result;
	}

	// TODO: deprecated?
	/**
	 * Cleans up any connections the Store has to external dependencies. This
	 * may be a NO-OP depending on the store.
	 * 
	 * @throws PDStoreException
	 */
	public void close() throws PDStoreException {
	}

	/***
	 * This creates a PDModel and adds it to the model
	 * 
	 * 
	 * @param transaction
	 * @param modelId
	 * @param typeId
	 * @param typeName
	 */
	public void createModel(GUID transaction, GUID modelId, String modelName) {
		setName(transaction, modelId, modelName);
		addLink(transaction, this.getRepository(),
				PDStore.REPOSITORYMODEL_ROLEID, modelId);
		setType(transaction, this.getRepository(), PDStore.REPOSITORY_TYPEID);
		setType(transaction, modelId, PDStore.MODEL_TYPEID);
	}

	/***
	 * This create a PDType and adds it to the model
	 * 
	 * @param transaction
	 * @param modelId
	 * @param typeId
	 * @param typeName
	 */
	public void createType(GUID transaction, GUID modelId, GUID typeId,
			String typeName) {
		addLink(transaction, modelId, PDStore.MODELTYPE_ROLEID, typeId);
		setType(transaction, typeId, PDStore.TYPE_TYPEID);
		if (typeName != null)
			setName(transaction, typeId, typeName);
		addLink(transaction, typeId, PDStore.ISPRIMITIVE_ROLEID, false);
	}

	public void createPrimitiveType(GUID transaction, GUID modelId,
			GUID typeId, String typeName) {
		addLink(transaction, modelId, PDStore.MODELTYPE_ROLEID, typeId);
		setType(transaction, typeId, PDStore.TYPE_TYPEID);
		setName(transaction, typeId, typeName);
		addLink(transaction, typeId, PDStore.ISPRIMITIVE_ROLEID, true);
	}

	/***
	 * This method creates a PDRole between two types with 2 roles
	 * 
	 * @param transaction
	 * @param type1Id
	 * @param role2Id
	 * @param type2Id
	 */
	public void createRelation(GUID transaction, GUID type1Id, GUID role2Id,
			GUID type2Id) {
		GUID role1Id = role2Id.getPartner();
		addLink(transaction, type1Id, PDStore.OWNED_ROLE_ROLEID, role1Id);
		addLink(transaction, type2Id, PDStore.OWNED_ROLE_ROLEID, role2Id);

		// TODO is this necessary?
		addLink(transaction, role2Id, PDStore.PARTNER_ROLEID, role1Id);
		addLink(transaction, role1Id, PDStore.PARTNER_ROLEID, role2Id);

		setType(transaction, role1Id, PDStore.ROLE_TYPEID);
		setType(transaction, role2Id, PDStore.ROLE_TYPEID);
	}

	/***
	 * This method creates a PDRole between two types with 2 roles with role
	 * names
	 * 
	 * @param transaction
	 * @param typeId1
	 * @param role1Name
	 * @param role2Name
	 * @param role2Id
	 * @param typeId2
	 */
	public void createRelation(GUID transaction, GUID typeId1,
			String role1Name, String role2Name, GUID role2, GUID typeId2) {
		GUID role1 = role2.getPartner();
		createRelation(transaction, typeId1, role2, typeId2);
		if (role1Name != null)
			setName(transaction, role1, role1Name);
		if (role2Name != null)
			setName(transaction, role2, role2Name);
	}

	public void addMetamodel(GUID transaction) {
		createModel(transaction, PDStore.PDMETAMODEL_ID, "PD Metamodel");

		/* Primitive types */
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.GUID_TYPEID, "GUID");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.DOUBLE_PRECISION_TYPEID, "Double");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.INTEGER_TYPEID, "Integer");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.BOOLEAN_TYPEID, "Boolean");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.TIMESTAMP_TYPEID, "Timestamp");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.CHAR_TYPEID, "Char");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.STRING_TYPEID, "String");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.BLOB_TYPEID, "Blob");
		createPrimitiveType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.IMAGE_TYPEID, "Image");

		/* Object - name */
		createRelation(transaction, PDStore.OBJECT_TYPEID, null, "name",
				PDStore.NAME_ROLEID, PDStore.STRING_TYPEID);

		/* Object - icon */
		createRelation(transaction, PDStore.OBJECT_TYPEID, null, "icon",
				PDStore.ICON_ROLEID, PDStore.IMAGE_TYPEID);

		/* Metatype Type */
		createType(transaction, PDStore.PDMETAMODEL_ID, PDStore.TYPE_TYPEID,
				"Type");

		// is primitive
		createRelation(transaction, PDStore.TYPE_TYPEID, null, "is primitive",
				PDStore.ISPRIMITIVE_ROLEID, PDStore.BOOLEAN_TYPEID);

		/* Metatype Role */
		createType(transaction, PDStore.PDMETAMODEL_ID, PDStore.ROLE_TYPEID,
				"Role");

		// owned roles
		createRelation(transaction, PDStore.TYPE_TYPEID, "owner", "owned role",
				PDStore.OWNED_ROLE_ROLEID, PDStore.ROLE_TYPEID);

		/*
		 * Partner relation is recursive and symmetrical, therefore only one
		 * role entry
		 */
		createRelation(transaction, PDStore.ROLE_TYPEID, null, "partner",
				PDStore.PARTNER_ROLEID, PDStore.ROLE_TYPEID);

		// min mult
		createRelation(transaction, PDStore.ROLE_TYPEID, null, "min mult",
				PDStore.MIN_MULT_ROLEID, PDStore.INTEGER_TYPEID);

		// max mult
		createRelation(transaction, PDStore.ROLE_TYPEID, null, "max mult",
				PDStore.MAX_MULT_ROLEID, PDStore.INTEGER_TYPEID);

		// TODO: make decision about "is to supertype" relation

		/* Type Model */
		createType(transaction, PDStore.PDMETAMODEL_ID, PDStore.MODEL_TYPEID,
				"Model");

		// models-types
		createRelation(transaction, PDStore.MODEL_TYPEID, "model", "type",
				PDStore.MODELTYPE_ROLEID, PDStore.TYPE_TYPEID);

		// objects to types (for dynamic type info)
		createRelation(transaction, PDStore.OBJECT_TYPEID, "has instance",
				"has type", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);

		/* Types for branching */
		createType(transaction, PDStore.PDMETAMODEL_ID, PDStore.BRANCH_TYPEID,
				"Branch");
		createType(transaction, PDStore.PDMETAMODEL_ID,
				PDStore.TRANSACTION_TYPEID, "Transaction");

		// parent to child transaction
		createRelation(transaction, PDStore.TRANSACTION_TYPEID, "parent",
				"child", PDStore.PARENTTRANSACTION_CHILDBRANCH_ROLEID,
				PDStore.TRANSACTION_TYPEID);

		// branch to transaction
		createRelation(transaction, PDStore.TRANSACTION_TYPEID, "transaction",
				"branch", PDStore.BRANCH_ROLEID, PDStore.BRANCH_TYPEID);

		// transaction to isolation level
		createRelation(transaction, PDStore.TRANSACTION_TYPEID, "transaction",
				"isolation level", PDStore.ISOLATIONLEVEL_ROLEID,
				PDStore.INTEGER_TYPEID);

		// transaction to durable transaction
		createRelation(transaction, PDStore.TRANSACTION_TYPEID,
				"begin-transaction", "durable transaction",
				PDStore.DURABLE_TRANSACTION_ROLEID, PDStore.TRANSACTION_TYPEID);

		// Object - USES_ROLE - Role
		createRelation(transaction, PDStore.OBJECT_TYPEID, "user", "used role",
				PDStore.USES_ROLE_ROLEID, PDStore.ROLE_TYPEID);
		
		// Change
		createType(transaction, PDStore.PDMETAMODEL_ID,
				CHANGE_TYPEID, "Change");
		createRelation(transaction, CHANGE_TYPEID, null, "transaction",
				CHANGE_TRANSACTION_ROLEID, TRANSACTION_TYPEID);
		createRelation(transaction, CHANGE_TYPEID, null, "change type",
				CHANGE_CHANGETYPE_ROLEID, STRING_TYPEID);
		createRelation(transaction, CHANGE_TYPEID, null, "instance1",
				CHANGE_INSTANCE1_ROLEID, OBJECT_TYPEID);
		createRelation(transaction, CHANGE_TYPEID, null, "role2",
				CHANGE_ROLE2_ROLEID, ROLE_TYPEID);
		createRelation(transaction, CHANGE_TYPEID, null, "instance2",
				CHANGE_INSTANCE2_ROLEID, OBJECT_TYPEID);
		
		// Operation
		createType(transaction, PDStore.PDMETAMODEL_ID,
				OPERATION_TYPEID, "Operation");
		createRelation(transaction, OPERATION_TYPEID, "applicable operation", "parameter type",
				PARAMETER_TYPE_ROLEID, TYPE_TYPEID);
		createRelation(transaction, OPERATION_TYPEID, null, "implementation",
				OPERATION_IMPLEMENTATION_ROLEID, STRING_TYPEID);
		
		// Widget
		createType(transaction, PDStore.PDMETAMODEL_ID,
				WIDGET_TYPEID, "Widget");
		createRelation(transaction, OPERATION_TYPEID, null, "implementation",
				WIDGET_IMPLEMENTATION_ROLEID, STRING_TYPEID);
		createRelation(transaction, OBJECT_TYPEID, "visualizes", "is visualized by",
				VISUALIZED_BY_ROLEID, WIDGET_TYPEID);
	}

	/**
	 * Get the accessor type of a role. Instances of that type can use the role
	 * to navigate to other instances.
	 * 
	 * @param transaction
	 *            ID of the transaction to read from
	 * @param role2Id
	 *            ID of the role to get the accessor type for
	 * @return ID of the accessor type
	 */
	public GUID getAccessorType(GUID transaction, GUID role2Id) {
		return (GUID) getInstance(transaction, role2Id.getPartner(),
				PDStore.OWNER_TYPE_ROLEID);
	}

	/**
	 * Get the owner type of a role. Instances of that type can appear in that
	 * role, i.e. they can be navigated to from instances of the corresponding
	 * accessor type.
	 * 
	 * @param transaction
	 *            ID of the transaction to read from
	 * @param role2Id
	 *            ID of the role to get the owner type for
	 * @return ID of the owner type
	 */
	public GUID getOwnerType(GUID transaction, GUID role2Id) {
		return (GUID) getInstance(transaction, role2Id,
				PDStore.OWNER_TYPE_ROLEID);
	}

	/**
	 * Get the dynamic type for the given instance.
	 * 
	 * @param transaction
	 *            ID of the transaction to read from
	 * @param instance
	 *            instance to get the type of
	 * @return ID of the type of the given instance
	 */
	public GUID getType(GUID transaction, Object instance) {
		GUID type = (GUID) getInstance(transaction, instance,
				PDStore.HAS_TYPE_ROLEID);

		// Assuming that all complex types have a has-type link,
		// use the GUIDs for primitive types in case no has-type link is found.
		if (type == null)
			type = pdstore.changelog.PrimitiveType.typeIdOf(instance);

		return type;
	}

	/**
	 * Set the dynamic type for the given instance.
	 * 
	 * @param transaction
	 *            ID of the transaction to write on
	 * @param instance
	 *            instance to set the type for
	 * @param type
	 *            ID of the type to set
	 */
	public void setType(GUID transaction, Object instance, GUID type) {
		setLink(transaction, instance, HAS_TYPE_ROLEID, type);
	}

	public Collection<GUID> getAccessibleRoles(GUID transaction, Object type) {
		Collection<GUID> result = new HashSet<GUID>();
		for (Object role : store.getInstances(transaction, type,
				PDStore.OWNED_ROLE_ROLEID))
			result.add(((GUID) role).getPartner());
		return result;
	}

	/**
	 * Creates a new branch from the given parent transaction, and begins a new
	 * transaction on that branch. The transaction will be open and needs to be
	 * committed (possibly with other changes on the new branch) to conclude the
	 * branch operation.
	 * 
	 * @param parentTransactionId
	 *            the transaction to branch from (branch source)
	 * @return ID of the first transaction on the new branch (branch target)
	 * @throws PDStoreException
	 */
	public GUID branch(GUID parentTransactionId) throws PDStoreException {
		GUID branchId = GUID.newBranchId();
		GUID transactionId = begin(branchId);
		merge(transactionId, parentTransactionId);
		return transactionId;
	}

	/**
	 * Merge the given parent transaction into the current transaction.
	 * 
	 * @param transactionId
	 *            ID of the current transaction (merge target)
	 * @param parentTransactionId
	 *            ID of the parent transaction (merge source)
	 * @throws PDStoreException
	 */
	public void merge(GUID transactionId, GUID parentTransactionId)
			throws PDStoreException {
		store.addLink(transactionId, parentTransactionId,
				PDStore.PARENTTRANSACTION_CHILDBRANCH_ROLEID,
				transactionId.getBranchID());
	}

	public List<PDChange<GUID, Object, GUID>> nextTransaction()
			throws PDStoreException {
		throw new UnsupportedOperationException(
				"this method should not be called on a local store.");
	}

	public List<List<PDChange<GUID, Object, GUID>>> newTransactions()
			throws PDStoreException {
		throw new UnsupportedOperationException(
				"this method should not be called on a local store.");
	}

	/**
	 * Constructs a best-effort human-readable name for an instance.
	 * 
	 * @param transaction
	 * @param instance
	 *            the instance to construct a label for
	 * @return a label for the give instance
	 */
	public String getLabel(GUID transaction, Object instance) {
		String typeName = null;
		String name;

		if (instance == null)
			return "null";

		if (instance instanceof Variable) {
			return instance.toString();
		} else if (instance instanceof GUID) {
			GUID type = getType(transaction, instance);
			if (type != null)
				typeName = getName(transaction, type);

			name = getName(transaction, (GUID) instance);
			if (name != null)
				name = "\"" + name + "\"";
			else
				name = instance.toString();
		} else {
			PrimitiveType typeOf = PrimitiveType.typeOf(instance);
			if (typeOf == null)
				typeName = "null";
			else
				typeName = typeOf.toString();
			name = instance.toString();
		}

		String label = "";
		if (typeName != null)
			label += typeName + " ";
		label += name;

		return label;
	}

	/**
	 * Constructs a best-effort human-readable name for an Iterable of
	 * PDChanges.
	 * 
	 * @param transaction
	 * @param changes
	 * @return
	 */
	public String toString(GUID transaction,
			Iterable<PDChange<GUID, Object, GUID>> changes) {
		StringBuffer result = new StringBuffer("[");
		for (PDChange<GUID, Object, GUID> change : changes)
			result.append(change.toString(this, transaction) + " ");
		result.append("]");
		return result.toString();
	}
	
	/**
	 * Generic function to execute Java code from PDStore, 
	 * where the Java code is dynamically loaded.
	 * 
	 * @param transaction
	 * @param operation
	 * @param parameter
	 * @return
	 */
	public Object applyOperation(GUID transaction, GUID operation,
			Object parameter) {
		String implementation = (String) getInstance(transaction, operation,
				OPERATION_IMPLEMENTATION_ROLEID);
		Debug.assertTrue(implementation != null, "Operation instance "
				+ getLabel(transaction, operation)
				+ "does not specify an implementation.");

		OperationI singleton = null;
		Class<?> operationClass;
		try {
			operationClass = Class.forName(implementation);
			singleton = (OperationI) operationClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new PDStoreException("", e);
		} catch (InstantiationException e) {
			throw new PDStoreException("", e);
		} catch (IllegalAccessException e) {
			throw new PDStoreException("", e);
		}
		return singleton.apply(this, transaction, parameter);
	}
	
	/**
	 * Returns the IDs of the operations that can be applied to the given instance. 
	 * @param transaction the transaction to query the operations on
	 * @param instance the instance that is the potential actual superparameter
	 * @return IDs of operations that can be applied to instance
	 */
	public Collection<GUID> getApplicableOperations(GUID transaction,
			GUID instance) {
		GUID type = getType(transaction, instance);
		return (Collection<GUID>) (Object) getInstances(transaction, type,
				PARAMETER_TYPE_ROLEID.getPartner());
	}


	/**
	 * A function that ensures that a link exists.
	 * 
	 * 
	 * TODO: If this has been given a general semantics, this might be a
	 * convenience function that can be made public and moved to PDStore. Note
	 * that the current implementation queries the concurrent store, but writes
	 * to the
	 * 
	 * @param changeTemplate
	 */
	// public void ensureChangeExists(
	// PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {
	//
	// Collection<PDChange<TransactionType, InstanceType, RoleType>> list =
	// concurrentStore
	// .getChanges(changeTemplate);
	// if (!list.isEmpty())
	// return;
	// addChange(changeTemplate);
	// }

}