package pdstore.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Model" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.dal.PDModel");
 * @author PDGen
 */
public class PDModel implements PDInstance {

	public static final GUID typeId = new GUID("31c54c264e6fdd11a5dba737f860105f"); 

	public static final GUID roleTypeId = new GUID("54134d264e6fdd11a5dba737f860105f");

	static {
		DALClassRegister.addDataClass(typeId, PDModel.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDModel:" + name;
		else
			return "PDModel:" + id;
	}
	/**
	 * Creates an PDModel object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDModel(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDModel object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDModel(PDWorkingCopy workingCopy, GUID id) {
		this.pdWorkingCopy = workingCopy;
		this.id = id;
		
		// set the has-type link for this instance
		GUID transaction = pdWorkingCopy.getTransaction();
		pdWorkingCopy.getStore().setType(transaction, id, typeId);
	}

	/**
	 * Loads an instance object of this type into a cache.
	 * If the instance is already in the cache, the cached instance is returned.
	 * @param PDWorkingCopy pdWorkingCopy to load the instance into
	 * @param id GUID of the instance
	 * Do not directly call this method. Use the newInstance() method in PDCache which would call this method
	 */
	public static PDModel load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDModel)instance;
	}

	/**
	 * Gets the pdWorkingCopy this object is stored in.
	 */
	public PDWorkingCopy getPDWorkingCopy() {
		return pdWorkingCopy;
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
	 * Gets the name of this instance.
	 * In PDStore every instance can be given a name.
	 * @return name the instance name
	 * @throws PDStoreException
	 */
	public String getName() throws PDStoreException {
		return pdWorkingCopy.getName(id);
	}
	/**
	 * Sets the name of this instance.
	 * In PDStore every instance can be given a name.
	 * If the instance already has a name, the name will be overwritten.
	 * If the given name is null, an existing name will be removed.
	 * @return name the new instance name
	 * @throws PDStoreException
	 */
	public void setName(String name) throws PDStoreException {
		pdWorkingCopy.setName(id, name);
	}
	/**
	 * Removes the name of this instance.
	 * In PDStore every instance can be given a name.
	 * If the instance does not have a name, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeName() throws PDStoreException {
		pdWorkingCopy.removeName(id);
	}
	/**
	 * Gets the icon of this instance.
	 * In PDStore every instance can be given an icon.
	 * @return icon the instance icon
	 * @throws PDStoreException
	 */
	public Blob getIcon() throws PDStoreException {
		return pdWorkingCopy.getIcon(id);
	}
	/**
	 * Sets the icon of this instance.
	 * In PDStore every instance can be given an icon.
	 * If the instance already has an icon, the icon will be overwritten.
	 * If the given icon is null, an existing icon will be removed.
	 * @return icon the new instance icon
	 * @throws PDStoreException
	 */
	public void setIcon(Blob icon) throws PDStoreException {
		pdWorkingCopy.setIcon(id, icon);
	}
	/**
	 * Removes the icon of this instance.
	 * In PDStore every instance can be given an icon.
	 * If the instance does not have an icon, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeIcon() throws PDStoreException {
		pdWorkingCopy.removeIcon(id);
	}

	
	

	/**
	 * Returns the instance connected to this instance through the role "type".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDType getType() throws PDStoreException {
	 	return (PDType)pdWorkingCopy.getInstance(this, roleTypeId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "type".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDType> getTypes() throws PDStoreException {
	 	Set<PDType> result = new HashSet<PDType>();
	 	GUID PDTypeTypeId = new GUID("518a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleTypeId, PDType.class, PDTypeTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "type".
	 * If the given instance is null, nothing happens.
	 * @param type the instance to connect
	 * @throws PDStoreException
	 */
	public void addType(GUID type) throws PDStoreException {

			if (type != null) {
				
				pdWorkingCopy.addLink(this.id, roleTypeId, type);
			}

	}


	/**
	 * Connects this instance to the given instance using role "type".
	 * If the given instance is null, nothing happens.
	 * @param type the instance to connect
	 * @throws PDStoreException
	 */
	public void addType(PDType type) throws PDStoreException {
		if (type != null) {
			addType(type.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "type".
	 * If the given collection of instances is null, nothing happens.
	 * @param type the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addTypes(Collection<PDType> types) throws PDStoreException {
		if (types == null)
			return;
		
		for (PDType instance : types)
			addType(instance);	
	}

	/**
	 * Removes the link from this instance through role "type".
	 * @throws PDStoreException
	 */
	public void removeType() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleTypeId, 
			pdWorkingCopy.getInstance(this, roleTypeId));
	}

	/**
	 * Removes the link from this instance through role "type" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeType(Object type) throws PDStoreException {
		if (type == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleTypeId, type);
	}

	/**
	 * Removes the links from this instance through role "type" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTypes(Collection<PDType> types) throws PDStoreException {
		if (types == null)
			return;
		
		for (PDType instance : types)
			pdWorkingCopy.removeLink(this.id, roleTypeId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "type".
	 * If there is already an instance connected to this instance through role "type", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param type the instance to connect
	 * @throws PDStoreException
	 */
	public void setType(GUID type) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleTypeId, type);	
	}
	/**
	 * Connects this instance to the given instance using role "type".
	 * If there is already an instance connected to this instance through role "type", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param type the instance to connect
	 * @throws PDStoreException
	 */
	public void setType(PDType type) throws PDStoreException {
		setType(type.getId());
	}

}
