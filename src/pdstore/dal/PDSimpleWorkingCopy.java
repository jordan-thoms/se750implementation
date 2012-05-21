package pdstore.dal;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.Blob;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.changelog.PrimitiveType;
import pdstore.generic.PDChange;

/**
 * An implementation of PDWorkingCopy that does not perform read- or write
 * caching, but only caching of java instances. PDWorkingcopy is not threadsafe
 * on the transaction demarcation.
 * 
 */
public class PDSimpleWorkingCopy implements PDWorkingCopy {

	static {
		// Debug.debugThisClass();
	}

	/**
	 * Loads metamodel when created
	 */
	public static void Load_METAMODEL_DAL() {
		DALClassRegister.addDataClass(PDModel.typeId, PDModel.class);
		DALClassRegister.addDataClass(PDType.typeId, PDType.class);
		DALClassRegister.addDataClass(PDRole.typeId, PDRole.class);
	}

	/**
	 * If true, then the load methods in this cache will create
	 * PDGenericInstance objects if there is no registered specific data access
	 * class. It is false by default, so PDWorkingCopy will throw an exception
	 * if trying to load an instance of a type without registered specific data
	 * access class.
	 */

	public boolean allowGenericInstances = false;
	private GUID transactionId;

	boolean autocommit = true;

	private PDStore store;

	/**
	 * A list of unused GUIDs that were pre-generated in bulk. Use method
	 * generateGUID() to get one.
	 */
	List<GUID> generatedGUIDs;

	/**
	 * Maps GUIDs of cached instances to their object representations in memory.
	 * Uses soft references so that instances may be removed from the cache.
	 */
	public Dictionary<GUID, SoftReference<PDInstance>> ObjectForGUID = new Hashtable<GUID, SoftReference<PDInstance>>();

	/**
	 * Creates a new PD data cache that can be used to access a given PDStore
	 * 
	 * @param store
	 *            An Instance of PDStore to use.
	 */
	public PDSimpleWorkingCopy(PDStore store) {
		this.store = store;
		Load_METAMODEL_DAL();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#beginReadOnly()
	 */
	public void beginReadOnly() throws PDStoreException {
		// No-op
	}

	// TODO: implement
	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#beginWriteOnly()
	 */
	public void beginWriteOnly() throws PDStoreException {
		// No-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#commit()
	 */
	public void commit() throws PDStoreException {
		this.store.commit(getTransaction());
		transactionId = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#rollback()
	 */
	public void rollback() throws PDStoreException {
		this.store.rollback(getTransaction());
	}

	// TODO: implement
	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#load(pdstore.GUID)
	 */
	public PDInstance load(GUID instanceId) throws PDStoreException {
		Object type = store.getInstance(getTransaction(), instanceId,
				PDStore.HAS_TYPE_ROLEID);

		// error checking
		if (type == null)
			throw new PDStoreException(
					"Type lookup without explicit type link not yet implemented.\n"
							+ "Please specify the type of instance "
							+ instanceId + ".");
		else if (!(type instanceof GUID))
			throw new PDStoreException(
					"The has-type link (PDStore.HAS_TYPE_ROLEID) of instance "
							+ instanceId
							+ " does not link to a type, but to a non-GUID: "
							+ type
							+ "\n"
							+ "Please make sure you added a correct has-type link for that instance.");

		GUID typeId = (GUID) type;
		return load(typeId, instanceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#load(pdstore.GUID, pdstore.GUID)
	 */
	public PDInstance load(GUID typeId, GUID instanceId)
			throws PDStoreException {
		PDInstance i = null;
		Class<?> dalClass = DALClassRegister.getDataClass(typeId);
		if (dalClass != null) {
			// if there is a registered wrapper class, then use it
			SoftReference<PDInstance> ref = ObjectForGUID.get(instanceId);
			if (ref != null)
				i = ref.get();
			if (i == null) {
				// if the instance is not in the cache, then
				// create and register a new PDInstance object

				Debug.println("Instantiating new DAL object of " + dalClass
						+ "...");

				try {
					Constructor<?> constr = dalClass.getDeclaredConstructor(
							PDWorkingCopy.class, GUID.class);
					i = (PDInstance) constr.newInstance(this, instanceId);
				} catch (NoSuchMethodException e) {
					throw new PDStoreException(
							"Could not find a constructor(PDWorkingCopy, GUID) for class "
									+ dalClass.getName());
				} catch (SecurityException e) {
					throw new PDStoreException(
							"The constructor(PDWorkingCopy, GUID) for class "
									+ dalClass.getName()
									+ " is not accessible.");
				} catch (Exception e) {
					throw new PDStoreException(
							"Something went wrong when instantiating class "
									+ dalClass.getName() + ".\n"
									+ "See inner exception.", e);
				}

				ObjectForGUID.put(instanceId, new SoftReference<PDInstance>(i));
			}

		} else if (allowGenericInstances) {
			// if there is no registered wrapper class, then use the
			// PDGenericInstance
			i = PDGenericInstance.load(this, typeId, instanceId);
		} else {
			String typename = getName(typeId);
			throw new PDStoreException(
					"Could not find specific data access class for type "
							+ ((typename != null) ? typename : typeId));
		}
		return i;
	}

	public GUID getTransaction() {
		if (transactionId == null)
			// transactions are only started on demand
			transactionId = this.store.begin();
		return transactionId;
	}

	public boolean isInTransaction() {
		return (transactionId != null);
	}

	public void setTransactionId(GUID transactionId) {
		this.transactionId = transactionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getInstances(pdstore.PDInstance,
	 * pdstore.PDRole)
	 */
	public Collection<Object> getInstances(PDInstance instance1, GUID role2) {

		// get rawObjects from PDStore,
		// which are either GUIDs or primitive values
		Collection<Object> rawObjects = store.getInstances(getTransaction(),
				instance1.getId(), role2);

		// try to get type info
		GUID role1Id = role2.getPartner();
		GUID type2Id = store.getAccessorType(getTransaction(), role1Id);

		// replace GUIDs with proper PDInstance/primitive objects
		Set<Object> result = new HashSet<Object>();
		for (Object o : rawObjects) {
			result.add(getProperInstanceObject(instance1, role2, type2Id, o));
		}
		return result;
	}

	/**
	 * Adds instances connected to instance1 by role2 to the given Collection
	 * result. The instances are of type type2Id and are represented by Java
	 * object of type javaType. This type information is used to cast the raw
	 * PDStore values into the correct Java objects.
	 * 
	 * This method is used in the getters of DAL classes, as the type of the
	 * returned instances is known in these getters.
	 */
	public <T> void getInstances(PDInstance instance1, GUID role2,
			Class<T> javaType, GUID type2Id, Collection<T> result) {

		// get raw objects from PDStore,
		// which are either GUIDs or primitive values
		Collection<Object> rawObjects = store.getInstances(getTransaction(),
				instance1.getId(), role2);

		// cast raw objects to proper type
		for (Object o : rawObjects) {
			result.add(javaType.cast(getProperInstanceObject(instance1, role2,
					type2Id, o)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getInstance(pdstore.PDInstance,
	 * pdstore.PDRole)
	 */
	public Object getInstance(PDInstance instance1, GUID role2) {

		// get rawObject from PDStore,
		// which is either a GUID or a primitive value
		Object rawObject = store.getInstance(getTransaction(),
				instance1.getId(), role2);

		// try to get type info
		GUID role1Id = role2.getPartner();
		GUID type2Id = store.getAccessorType(getTransaction(), role1Id);

		return getProperInstanceObject(instance1, role2, type2Id, rawObject);
	}

	/**
	 * Gets the right Java object for the raw primitive value in rawObject. This
	 * makes sure that a raw GUID value read from a PDStore is represented using
	 * a proper DAL class, if possible.
	 * 
	 * @param instance1
	 * @param role2
	 * @param type2Id
	 * @param rawObject
	 * @return
	 */
	private Object getProperInstanceObject(PDInstance instance1, GUID role2,
			GUID type2Id, Object rawObject) {

		// if it is not a GUID, then it is a primitive value:
		// just return it
		if (!(rawObject instanceof GUID))
			return rawObject;

		// the instance has a GUID
		GUID instance2Id = (GUID) rawObject;

		// handle case where no type info was given
		if (type2Id == null) {

			// handle error case that the link is a has-type link without
			// relation in the model,
			// i.e. if a has-type relation is missing in the model
			if (role2 == PDStore.HAS_TYPE_ROLEID) {
				String type1Name = store.getName(getTransaction(),
						instance1.getTypeId());
				throw new PDStoreException(
						"Missing relation in the model: "
								+ "you need to create a relation that associates instances of type "
								+ type1Name
								+ " with their type:\n"
								+ "store.createRelation(transaction, "
								+ type1Name
								+ "_ID, null,"
								+ "\"type\", PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID);");
			}

			// try to load corresponding PDInstance using type information from
			// a has-type link
			try {
				return load(instance2Id);
			} catch (PDStoreException e) {
				// If there is no has-type link, we don't know what type the
				// instance2Id has.
				// In this case, print warning and simply return the GUID (for
				// untyped usage).
				e.printStackTrace();
				Debug.println("Warning: Object instance "
						+ rawObject
						+ " seems to have no type information.\n"
						+ "Make sure you create a relation for role "
						+ role2
						+ ", or add a has-type link (role PDStore.HAS_TYPE_ROLEID) for that instance.");
				return instance2Id;
			}
		}

		// handle cases where model info was found

		// if it is supposed to be simply a primitive value of type GUID,
		// return it
		if (type2Id.equals(PDStore.GUID_TYPEID))
			return rawObject;

		// if type is Object, then the type is determined dynamically
		// using the has-type relation in load
		if (type2Id.equals(PDStore.OBJECT_TYPEID)) {
			try {
				return load(instance2Id);
			} catch (PDStoreException e) {
				// If there is no has-type link, we don't know what type the
				// instance2Id has.
				// In this case, print warning and simply return the GUID (for
				// untyped usage).
				e.printStackTrace();
				Debug.println("Warning: Object instance "
						+ rawObject
						+ " seems to have no type information.\n"
						+ "Make sure you create a relation for role "
						+ role2
						+ ", or add a has-type link (role PDStore.HAS_TYPE_ROLEID) for that instance.");
				return instance2Id;
			}
		}

		// otherwise, type2Id is the actual type and can be used to load a
		// PDInstance for instance2Id
		return load(type2Id, instance2Id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#addLink(pdstore.PDInstance, pdstore.PDRole,
	 * java.lang.Object)
	 */
	public void addLink(PDInstance instance1, PDRole role2, Object instance2) {
		this.addLink(instance1.getId(), role2.getId(), instance2);
	}

	public void addLink(GUID instance1Id, GUID role2Id, Object instance2) {
		if (instance2 == null)
			return;

		// instance2 is either a primitive or a PDInstance
		// if it is a PDInstance, then the store needs to be given the GUID of
		// the instance
		if (instance2 instanceof PDInstance) {
			instance2 = ((PDInstance) instance2).getId();
		}

		store.addLink(getTransaction(), instance1Id, role2Id, instance2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#removeLink(java.lang.Object, pdstore.GUID,
	 * java.lang.Object)
	 */
	public void removeLink(GUID instance1, GUID role2, Object instance2) {
		if (instance2 == null)
			return;

		// instance2 is either a primitive or a PDInstance
		// if it is a PDInstance, then the store needs to be given the GUID of
		// the instance
		if (instance2 instanceof PDInstance) {
			instance2 = ((PDInstance) instance2).getId();
		}

		store.removeLink(getTransaction(), instance1, role2, instance2);
	}

	public void setLink(PDInstance instance1, PDRole role2, Object instance2) {
		this.setLink(instance1.getId(), role2.getId(), instance2);
	}

	public void setLink(GUID instance1Id, GUID role2Id, Object instance2) {
		store.setLink(getTransaction(), instance1Id, role2Id, instance2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#newInstance(pdstore.GUID)
	 */
	public PDInstance newInstance(GUID typeId) throws PDStoreException {
		return load(typeId, new GUID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#removeInstance(pdstore.GUID, pdstore.GUID,
	 * pdstore.GUID)
	 */
	public void removeInstance(GUID typeid, GUID roleId, GUID o) {
		// store.removeInstance(transactionId, typeid, roleId, o);
	}

	// TODO: deprecated?
	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#instanceExists(pdstore.GUID, pdstore.GUID)
	 */
	public boolean instanceExists(GUID typeId, Object instance)
			throws PDStoreException {
		return store.instanceExists(getTransaction(), instance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#instanceExists(pdstore.GUID)
	 */
	public boolean instanceExists(Object instance) throws PDStoreException {
		return store.instanceExists(getTransaction(), instance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getGUIDs(java.lang.String)
	 */
	public Collection<GUID> getIds(String name) throws PDStoreException {
		return store.getIds(getTransaction(), name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getGUID(java.lang.String)
	 */
	public GUID getId(String name) throws PDStoreException {
		return store.getId(getTransaction(), name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getLabel(java.lang.Object)
	 */
	public String getLabel(Object instance) {
		return store.getLabel(getTransaction(), instance);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getLabel(java.lang.Object)
	 */
	public String toString(PDChange<GUID, Object, GUID> change) {
		return change.toString(store, getTransaction());
	}

	/**
	 * Gets the name associated with a GUID. In PDStore every instance can be
	 * given a name.
	 * 
	 * @param id
	 *            GUID to get the name for
	 * @return the name associated with the GUID
	 * @throws PDStoreException
	 */
	public String getName(GUID instanceId) throws PDStoreException {
		return store.getName(getTransaction(), instanceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#setName(pdstore.GUID, java.lang.String)
	 */
	public void setName(GUID instanceId, String name) throws PDStoreException {
		store.setName(getTransaction(), instanceId, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#removeName(pdstore.GUID)
	 */
	public void removeName(GUID instanceId) throws PDStoreException {
		store.removeName(getTransaction(), instanceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getIcon(pdstore.GUID)
	 */
	public Blob getIcon(GUID instanceId) throws PDStoreException {
		return store.getIcon(getTransaction(), instanceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#setIcon(pdstore.GUID, java.sql.Blob)
	 */
	public void setIcon(GUID instanceId, Blob icon) throws PDStoreException {
		store.setIcon(getTransaction(), instanceId, icon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#removeIcon(pdstore.GUID)
	 */
	public void removeIcon(GUID instanceId) throws PDStoreException {
		store.removeIcon(getTransaction(), instanceId);
	}

	/**
	 * Generates a timestamp-based GUID. Note that the GUID may have been
	 * generated in bulk a-priori, so the timestamp may not reflect the current
	 * time.
	 * 
	 * @return the new GUID
	 * @throws PDStoreException
	 */
	@Deprecated
	public GUID generateGUID() throws PDStoreException {
		return new GUID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.PDWorkingCopy#getAllInstances(pdstore.GUID)
	 */
	public Collection<PDInstance> getAllInstancesOfType(GUID typeId)
			throws PDStoreException {
		Collection<Object> instanceIds = this.store.getAllInstancesOfType(
				getTransaction(), typeId);
		Set<PDInstance> instances = new HashSet<PDInstance>();
		for (Object instanceId : instanceIds) {
			GUID id = (GUID) instanceId;
			if (instanceId != null) {
				instances.add(this.load(typeId, id));
			}
		}
		return instances;
	}

	public void setAutocommit(boolean autocommit) {
		this.autocommit = autocommit;
	}

	public boolean isAutocommit() {
		return autocommit;
	}

	public PDStore getStore() {
		return store;
	}
}
