package pdstore.dal;

import java.util.Collection;

import pdstore.Blob;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.generic.PDChange;

/**
 * A cache of objects that represent instances of the database in memory. Only a
 * single transaction can be executed on one PDWorkingCopy at a time, however,
 * several PDWorkingCopys may be used concurrently by an application.
 * 
 * 
 * A PDWorkingCopy is a container for a collection of Java Objects of type
 * PDinstance, which are composite (live-and-die) parts of the PDWorkingCopy.
 * Since here the Java Mapping of the PD model is discussed, these Java objects
 * of type PDinstance are referred to as PD Java objects, in order to
 * distinguish them from the PD instances that they represent. Every PD Java
 * Object has a PD type, and this PD type can either have a Java type mapped to
 * it, which we call a PD Java type, or the PD type can exist only as a dynamic
 * type, meaning it is a PD Java Object representing that PD type. A PD Java
 * type for a PD type can be generated at any point in development time with the
 * PDGen tool. In typical PDWorkingCopy implementations, once a PD type has a PD
 * Java type associated with it, it will only be allowed to create objects of
 * that PD Java type, not PD Java objects with dynamic type. PDWorkingCopy
 * offers a dynamic interface to all PD Java Objects. PD instances are
 * associated to other PD instances over relations that can be navigated with
 * role names. For PD Java Objects of all PD types these roles can be accessed
 * through their PDWorkingCopy with methods such as getInstances that comprise
 * the dynamic interface.
 * 
 * PDWorkingCopy implements a factory pattern for all PD Java Objects: All PD
 * Java objects can be created with regards to their Java live cycle with the
 * PDWorkingCopy.load() method. Instances of PD Java Types are not created with
 * constructors, but with a static method that encapsulates the PDWorkingCopy
 * load method.
 * 
 * PD Java Objects in one PDWorkingCopy can only be connected to PD Java Objects
 * in the same PDWorkingCopy over these roles. The roles of PD types are mapped
 * to groups of methods in the PD Java type that are similar to properties in
 * Java Beans. A PD Java type must follow the PD Java type convention. The PD
 * Java type convention is positively defining what a PD Java Type is allowed to
 * offer, all other things are forbidden. A PD Java Type must have for every
 * property, lets assume it has the name friend, the following methods:
 * getFriends(), getFriend(), setFriend(), removeFriend(), addFriend().
 * 
 * 
 * @author Gerald
 * @author clut002
 * 
 */
public interface PDWorkingCopy {

	/**
	 * Makes a transaction that just begun read-only. Can only be called right
	 * at the beginning of a transaction.
	 * 
	 * @throws PDStoreException
	 */
	void beginReadOnly() throws PDStoreException;

	/**
	 * makes a transaction write-only for optimisation
	 * 
	 * @throws PDStoreException
	 */
	void beginWriteOnly() throws PDStoreException;

	/**
	 * Commits the current transaction and starts a new transaction. Until
	 * commit() is called no other transactions can see the changes of the
	 * current transaction. If commit() is not called at all, the changes are
	 * lost.
	 * 
	 * @throws PDStoreException
	 */
	//TODO commit should return a GUID or null
	void commit() throws PDStoreException;

	/**
	 * Rolls the current transaction back and starts a new transaction. This
	 * means that all changes of the current transaction are discarded.
	 * 
	 * @throws PDStoreException
	 */
	void rollback() throws PDStoreException;

	/**
	 * load in instance from the database into this PDWorkingCopy from only the
	 * instance id
	 * 
	 * @param instanceId
	 * @return
	 * @throws PDStoreException
	 */
	PDInstance load(GUID instanceId) throws PDStoreException;

	/**
	 * Loads an instance from the database into this PDWorkingCopy. If the
	 * typeId is statically known, this method should not be used, but instead
	 * the static load method of the corresponding DAL class should be used,
	 * since these static load methods have statically correctly typed return
	 * values.
	 * 
	 * @param typeId
	 *            GUID of the instance type
	 * @param instanceId
	 *            GUID of the instance
	 * @return an object representing the instance in memory
	 * @throws PDStoreException
	 */
	PDInstance load(GUID typeId, GUID instanceId) throws PDStoreException;

	/**
	 * gets instances connected to an instance through a one-to-many
	 * relationship
	 * 
	 * @param instance1
	 * @param role2
	 * @return
	 */
	Collection<Object> getInstances(PDInstance instance1, GUID role2);

	<T> void getInstances(PDInstance instance1, GUID role2, Class<T> javaType,
			GUID typeId, Collection<T> result);

	/**
	 * get an instance connected to the given instance through the given role
	 * 
	 * @param instance1
	 * @param role2
	 * @return
	 */
	Object getInstance(PDInstance instance1, GUID role2);

	/**
	 * add a link between instances
	 * 
	 * @param instance1
	 * @param role2
	 * @param instance2
	 */
	void addLink(PDInstance instance1, PDRole role2, Object instance2);

	void addLink(GUID instance1Id, GUID role2Id, Object instance2);

	/**
	 * add a link between instances
	 * 
	 * @param instance1
	 * @param role2
	 * @param instance2
	 */
	void setLink(PDInstance instance1, PDRole role2, Object instance2);

	void setLink(GUID instance1Id, GUID role2Id, Object instance2);

	/**
	 * remove a link between instances
	 * 
	 * @param instance1
	 * @param role2
	 * @param instance2
	 */

	void removeLink(GUID instance1, GUID role2, Object instance2);

	/**
	 * Creates a new instance of the given type.
	 * 
	 * @param typeid
	 *            GUID of the type
	 * @return a new instance
	 * @throws PDStoreException
	 */
	PDInstance newInstance(GUID typeId) throws PDStoreException;

	/**
	 * Determines if an instance exists in the database.
	 * 
	 * @param typeid
	 *            GUID of the instance type
	 * @param id
	 *            GUID of the instance
	 * @return true iff the instance was found in the database
	 * @throws PDStoreException
	 */
	boolean instanceExists(GUID typeId, Object instance)
			throws PDStoreException;

	/**
	 * Determines if an instance exists in the database.
	 * 
	 * with only the instance id
	 * 
	 * @param instanceId
	 * @return
	 * @throws PDStoreException
	 */
	boolean instanceExists(Object instance) throws PDStoreException;

	/**
	 * Gets all the GUIDs that are associated with the given name.
	 * 
	 * @param name
	 *            the name
	 * @return the GUID associated with the name
	 * @throws PDStoreException
	 */
	Collection<GUID> getIds(String name) throws PDStoreException;

	/**
	 * Gets the GUID that is associated with the given name.
	 * 
	 * @param name
	 *            the name
	 * @return the GUID associated with the name
	 * @throws PDStoreException
	 */
	GUID getId(String name) throws PDStoreException;

	/**
	 * Constructs a best-effort human-readable name for an instance.
	 * 
	 * @param instance
	 *            the instance to construct a label for
	 * @return a label for the given instance
	 */
	String getLabel(Object instance);

	/**
	 * Constructs a best-effort human-readable name for a PDChange.
	 * 
	 * @param change
	 *            the change to construct a name for
	 * @return a label for the given change
	 */
	String toString(PDChange<GUID, Object, GUID> change);
	
	/**
	 * Gets the name associated with a GUID. In PDStore every instance can be
	 * given a name.
	 * 
	 * @param id
	 *            GUID to get the name for
	 * @return the name associated with the GUID
	 * @throws PDStoreException
	 */
	String getName(GUID instanceId) throws PDStoreException;

	/**
	 * Sets the name associated with a GUID. In PDStore every instance can be
	 * given a name. If the instance already has a name, the name will be
	 * overwritten. If the given name is null, an existing name will be removed.
	 * 
	 * @param id
	 *            GUID of the instance
	 * @param name
	 *            the new instance name
	 * @throws PDStoreException
	 */
	void setName(GUID instanceId, String name) throws PDStoreException;

	/**
	 * Removes the name associated with a GUID. In PDStore every instance can be
	 * given a name. If the instance does not have a name, nothing happens.
	 * 
	 * @param id
	 *            GUID of the instance
	 * @throws PDStoreException
	 */
	void removeName(GUID instanceId) throws PDStoreException;

	/**
	 * Gets the icon associated with a GUID. In PDStore every instance can be
	 * given an icon.
	 * 
	 * @param id
	 *            GUID to get the name for
	 * @return the icon associated with the GUID
	 * @throws PDStoreException
	 */
	Blob getIcon(GUID instanceId) throws PDStoreException;

	/**
	 * Sets the icon associated with a GUID. In PDStore every instance can be
	 * given an icon. If the instance already has an icon, the icon will be
	 * overwritten. If the given icon is null, an existing icon will be removed.
	 * 
	 * @param id
	 *            GUID of the instance
	 * @param icon
	 *            the new instance icon
	 * @throws PDStoreException
	 */
	void setIcon(GUID instanceId, Blob icon) throws PDStoreException;

	/**
	 * Removes the icon associated with a GUID. In PDStore every instance can be
	 * given an icon. If the instance does not have an icon, nothing happens.
	 * 
	 * @param id
	 *            GUID of the instance
	 * @throws PDStoreException
	 */
	void removeIcon(GUID instanceId) throws PDStoreException;

	/**
	 * Gets all the instances of the given complex type that are stored in the
	 * database.
	 * 
	 * @param typeid
	 *            GUID of the complex type
	 * @return all stored instances of that type
	 * @throws PDStoreException
	 */
	Collection<PDInstance> getAllInstancesOfType(GUID typeId)
			throws PDStoreException;

	public void setAutocommit(boolean autocommit);

	public boolean isAutocommit();

	public PDStore getStore();

	public GUID getTransaction();

}