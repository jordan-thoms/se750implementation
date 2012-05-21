package pdstore.dal;

import java.lang.ref.SoftReference;
import java.util.Set;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;

/**
 * Generic class for PD Java Objects of that can represent any instance in memory. This
 * class is chosen if no specific data access class can be found for a type. If
 * this class is used to represent an instance although a specific data access
 * class has been created, then this is probably because the specific data
 * access class has not been registered in PDCache (by loading it with
 * Class.forName(...)).
 * 
 * @author Gerald
 * @author clut002
 * 
 */
public class PDGenericInstance implements PDInstance {

    public final GUID typeId;
    private final PDWorkingCopy cache;
    private final GUID id;

    @Override
    public String toString() {
	return "PDGenericInstance:" + id;
    }

    /**
     * Loads a generic instance object into a cache. If the instance is already
     * in the cache, the cached instance is returned.
     * 
     * @param cache
     *                cache to load the instance into
     * @param id
     *                GUID of the instance
     */
    public static PDInstance load(PDSimpleWorkingCopy cache, GUID typeid, GUID id) {
	PDInstance i = null;

	SoftReference<PDInstance> ref = cache.ObjectForGUID.get(id);
	if (ref != null)
	    i = ref.get();
	if (i != null)
	    return i;

	i = new PDGenericInstance(cache, typeid, id);

	cache.ObjectForGUID.put(id, new SoftReference<PDInstance>(i));

	return i;
    }

    /**
     * Creates a PDGenericInstance object representing the given instance of the
     * given type in the given cache.
     * 
     * @param cache
     *                the cache the instance should be in
     * @param typeid
     *                GUID of the instance type
     * @param id
     *                GUID of the instance
     */
    private PDGenericInstance(PDWorkingCopy cache, GUID typeid, GUID id) {
	this.cache = cache;
	this.typeId = typeid;
	this.id = id;
    }
    
    
    /**
     * Creates a PDGenericInstance object representing the given existing instance in the given cache.
     * 
     * @param cache
     *                the cache the instance should be in
     * @param id
     *                GUID of the instance
     */
    public PDGenericInstance(PDWorkingCopy cache, GUID id) {
	this.cache = cache;
	this.id = id;
	this.typeId = (GUID)cache.getInstance(this, PDStore.TYPE_TYPEID);
    }

    /**
     * Gets the cache this object is stored in.
     */
    
    public PDWorkingCopy getPDWorkingCopy() {
	return cache;
    }

    /**
     * Gets the GUID of the instance represented by this object.
     */
    
    public GUID getId() {
	return id;
    }

    /**
     * Gets the GUID of the type of the instance represented by this object.
     */
    
    public GUID getTypeId() {
	return typeId;
    }

    /**
     * Gets the name of this instance. In PDStore every instance can be given a
     * name.
     * 
     * @return name the instance name
     * @throws PDStoreException
     */
    
    public String getName() throws PDStoreException {
	return cache.getName(id);
    }

    /**
     * Sets the name of this instance. In PDStore every instance can be given a
     * name. If the instance already has a name, the name will be overwritten.
     * 
     * @return name the new instance name
     * @throws PDStoreException
     */
    
    public void setName(String name) throws PDStoreException {
	cache.setName(id, name);
    }

    /**
     * Removes the name of this instance. In PDStore every instance can be given
     * a name. If the instance does not have a name, nothing happens.
     * 
     * @throws PDStoreException
     */
    
    public void removeName() throws PDStoreException {
	cache.removeName(id);
    }

    /**
     * Gets the instances connected to this instance through the given role.
     * This is done by calling the appropriate getR() method for the role R.
     * 
     * @param roleId
     *                the GUID of the role
     * @return result the connected instances
     * @throws PDStoreException
     */
    
    public Set<Object> getInstances(GUID roleId) throws PDStoreException {
	// TODO Auto-generated method stub
	throw new PDStoreException("Not implemented yet.");
    }

    /**
     * Connects this instance to the given instance using the given role. This
     * is done by calling the appropriate setR() or addR() method for the role
     * R.
     * 
     * @param roleId
     *                the GUID of the role
     * @param o
     *                the instance to connect
     * @throws PDStoreException
     */
    
    public void addInstance(GUID roleId, Object o) throws PDStoreException {
	// TODO Auto-generated method stub
	throw new PDStoreException("Not implemented yet.");
    }

    /**
     * Removes the link from this instance to the given instance using the given
     * role if the link exists. This is done by calling the appropriate
     * removeR() method for the role R.
     * 
     * @param roleId
     *                the GUID of the role
     * @param o
     *                the instance to remove
     * @throws PDStoreException
     */
    
    public void removeInstance(GUID roleId, Object o) throws PDStoreException {
	// TODO Auto-generated method stub
	throw new PDStoreException("Not implemented yet.");
    }

}
