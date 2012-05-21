package pdstore.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Type" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.dal.PDType");
 * @author PDGen
 */
public class PDType implements PDInstance {

	public static final GUID typeId = new GUID("518a986c4062db11afc0b95b08f50e2f"); 

	public static final GUID roleOwnedRoleId = new GUID("648a986c4062db11afc0b95b08f50e2f");
	public static final GUID roleHasInstanceId = new GUID("ee32adf0f68b11df861e1cc1dec00ed3");
	public static final GUID roleModelId = new GUID("54134d264e6fdd11a5cba737f860105f");
	public static final GUID roleIsPrimitiveId = new GUID("5d8a986c4062db11afc0b95b08f50e2f");

	static {
		DALClassRegister.addDataClass(typeId, PDType.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDType:" + name;
		else
			return "PDType:" + id;
	}
	/**
	 * Creates an PDType object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDType(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDType object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDType(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDType load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDType)instance;
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
	 * Returns the instance connected to this instance through the role "owned role".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDRole getOwnedRole() throws PDStoreException {
	 	return (PDRole)pdWorkingCopy.getInstance(this, roleOwnedRoleId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "owned role".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDRole> getOwnedRoles() throws PDStoreException {
	 	Set<PDRole> result = new HashSet<PDRole>();
	 	GUID PDRoleTypeId = new GUID("528a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleOwnedRoleId, PDRole.class, PDRoleTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "owned role".
	 * If the given instance is null, nothing happens.
	 * @param ownedRole the instance to connect
	 * @throws PDStoreException
	 */
	public void addOwnedRole(GUID ownedRole) throws PDStoreException {

			if (ownedRole != null) {
				
				pdWorkingCopy.addLink(this.id, roleOwnedRoleId, ownedRole);
			}

	}


	/**
	 * Connects this instance to the given instance using role "owned role".
	 * If the given instance is null, nothing happens.
	 * @param ownedRole the instance to connect
	 * @throws PDStoreException
	 */
	public void addOwnedRole(PDRole ownedRole) throws PDStoreException {
		if (ownedRole != null) {
			addOwnedRole(ownedRole.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "owned role".
	 * If the given collection of instances is null, nothing happens.
	 * @param ownedRole the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addOwnedRoles(Collection<PDRole> ownedRoles) throws PDStoreException {
		if (ownedRoles == null)
			return;
		
		for (PDRole instance : ownedRoles)
			addOwnedRole(instance);	
	}

	/**
	 * Removes the link from this instance through role "owned role".
	 * @throws PDStoreException
	 */
	public void removeOwnedRole() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleOwnedRoleId, 
			pdWorkingCopy.getInstance(this, roleOwnedRoleId));
	}

	/**
	 * Removes the link from this instance through role "owned role" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeOwnedRole(Object ownedRole) throws PDStoreException {
		if (ownedRole == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleOwnedRoleId, ownedRole);
	}

	/**
	 * Removes the links from this instance through role "owned role" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeOwnedRoles(Collection<PDRole> ownedRoles) throws PDStoreException {
		if (ownedRoles == null)
			return;
		
		for (PDRole instance : ownedRoles)
			pdWorkingCopy.removeLink(this.id, roleOwnedRoleId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "owned role".
	 * If there is already an instance connected to this instance through role "owned role", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param ownedRole the instance to connect
	 * @throws PDStoreException
	 */
	public void setOwnedRole(GUID ownedRole) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleOwnedRoleId, ownedRole);	
	}
	/**
	 * Connects this instance to the given instance using role "owned role".
	 * If there is already an instance connected to this instance through role "owned role", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param ownedRole the instance to connect
	 * @throws PDStoreException
	 */
	public void setOwnedRole(PDRole ownedRole) throws PDStoreException {
		setOwnedRole(ownedRole.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "has instance".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDInstance getHasInstance() throws PDStoreException {
	 	return (PDInstance)pdWorkingCopy.getInstance(this, roleHasInstanceId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "has instance".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDInstance> getHasInstances() throws PDStoreException {
	 	Set<PDInstance> result = new HashSet<PDInstance>();
	 	GUID PDInstanceTypeId = new GUID("70da26e0fc3711dfa87b842b2b9af4fd");
		pdWorkingCopy.getInstances(this, roleHasInstanceId, PDInstance.class, PDInstanceTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "has instance".
	 * If the given instance is null, nothing happens.
	 * @param hasInstance the instance to connect
	 * @throws PDStoreException
	 */
	public void addHasInstance(GUID hasInstance) throws PDStoreException {

			if (hasInstance != null) {
				
				pdWorkingCopy.addLink(this.id, roleHasInstanceId, hasInstance);
			}

	}


	/**
	 * Connects this instance to the given instance using role "has instance".
	 * If the given instance is null, nothing happens.
	 * @param hasInstance the instance to connect
	 * @throws PDStoreException
	 */
	public void addHasInstance(PDInstance hasInstance) throws PDStoreException {
		if (hasInstance != null) {
			addHasInstance(hasInstance.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "has instance".
	 * If the given collection of instances is null, nothing happens.
	 * @param hasInstance the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addHasInstances(Collection<PDInstance> hasInstances) throws PDStoreException {
		if (hasInstances == null)
			return;
		
		for (PDInstance instance : hasInstances)
			addHasInstance(instance);	
	}

	/**
	 * Removes the link from this instance through role "has instance".
	 * @throws PDStoreException
	 */
	public void removeHasInstance() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleHasInstanceId, 
			pdWorkingCopy.getInstance(this, roleHasInstanceId));
	}

	/**
	 * Removes the link from this instance through role "has instance" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeHasInstance(Object hasInstance) throws PDStoreException {
		if (hasInstance == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleHasInstanceId, hasInstance);
	}

	/**
	 * Removes the links from this instance through role "has instance" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeHasInstances(Collection<PDInstance> hasInstances) throws PDStoreException {
		if (hasInstances == null)
			return;
		
		for (PDInstance instance : hasInstances)
			pdWorkingCopy.removeLink(this.id, roleHasInstanceId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "has instance".
	 * If there is already an instance connected to this instance through role "has instance", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param hasInstance the instance to connect
	 * @throws PDStoreException
	 */
	public void setHasInstance(GUID hasInstance) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleHasInstanceId, hasInstance);	
	}
	/**
	 * Connects this instance to the given instance using role "has instance".
	 * If there is already an instance connected to this instance through role "has instance", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param hasInstance the instance to connect
	 * @throws PDStoreException
	 */
	public void setHasInstance(PDInstance hasInstance) throws PDStoreException {
		setHasInstance(hasInstance.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "model".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDModel getModel() throws PDStoreException {
	 	return (PDModel)pdWorkingCopy.getInstance(this, roleModelId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "model".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDModel> getModels() throws PDStoreException {
	 	Set<PDModel> result = new HashSet<PDModel>();
	 	GUID PDModelTypeId = new GUID("31c54c264e6fdd11a5dba737f860105f");
		pdWorkingCopy.getInstances(this, roleModelId, PDModel.class, PDModelTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "model".
	 * If the given instance is null, nothing happens.
	 * @param model the instance to connect
	 * @throws PDStoreException
	 */
	public void addModel(GUID model) throws PDStoreException {

			if (model != null) {
				
				pdWorkingCopy.addLink(this.id, roleModelId, model);
			}

	}


	/**
	 * Connects this instance to the given instance using role "model".
	 * If the given instance is null, nothing happens.
	 * @param model the instance to connect
	 * @throws PDStoreException
	 */
	public void addModel(PDModel model) throws PDStoreException {
		if (model != null) {
			addModel(model.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "model".
	 * If the given collection of instances is null, nothing happens.
	 * @param model the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addModels(Collection<PDModel> models) throws PDStoreException {
		if (models == null)
			return;
		
		for (PDModel instance : models)
			addModel(instance);	
	}

	/**
	 * Removes the link from this instance through role "model".
	 * @throws PDStoreException
	 */
	public void removeModel() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleModelId, 
			pdWorkingCopy.getInstance(this, roleModelId));
	}

	/**
	 * Removes the link from this instance through role "model" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeModel(Object model) throws PDStoreException {
		if (model == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleModelId, model);
	}

	/**
	 * Removes the links from this instance through role "model" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeModels(Collection<PDModel> models) throws PDStoreException {
		if (models == null)
			return;
		
		for (PDModel instance : models)
			pdWorkingCopy.removeLink(this.id, roleModelId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "model".
	 * If there is already an instance connected to this instance through role "model", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param model the instance to connect
	 * @throws PDStoreException
	 */
	public void setModel(GUID model) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleModelId, model);	
	}
	/**
	 * Connects this instance to the given instance using role "model".
	 * If there is already an instance connected to this instance through role "model", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param model the instance to connect
	 * @throws PDStoreException
	 */
	public void setModel(PDModel model) throws PDStoreException {
		setModel(model.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "is primitive".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Boolean getIsPrimitive() throws PDStoreException {
	 	return (Boolean)pdWorkingCopy.getInstance(this, roleIsPrimitiveId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "is primitive".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Boolean> getIsPrimitives() throws PDStoreException {
	 	Set<Boolean> result = new HashSet<Boolean>();
	 	GUID BooleanTypeId = new GUID("4d8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleIsPrimitiveId, Boolean.class, BooleanTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "is primitive".
	 * If the given instance is null, nothing happens.
	 * @param isPrimitive the instance to connect
	 * @throws PDStoreException
	 */
	public void addIsPrimitive(Boolean isPrimitive) throws PDStoreException {

			if (isPrimitive != null) {
				
				pdWorkingCopy.addLink(this.id, roleIsPrimitiveId, isPrimitive);
			}

	}

	/**
	 * Connects this instance to the given instances using role "is primitive".
	 * If the given collection of instances is null, nothing happens.
	 * @param isPrimitive the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addIsPrimitives(Collection<Boolean> isPrimitives) throws PDStoreException {
		if (isPrimitives == null)
			return;

		for (Boolean instance : isPrimitives)
			addIsPrimitive(instance);
	}


	/**
	 * Removes the link from this instance through role "is primitive".
	 * @throws PDStoreException
	 */
	public void removeIsPrimitive() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleIsPrimitiveId, 
			pdWorkingCopy.getInstance(this, roleIsPrimitiveId));
	}

	/**
	 * Removes the link from this instance through role "is primitive" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeIsPrimitive(Object isPrimitive) throws PDStoreException {
		if (isPrimitive == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleIsPrimitiveId, isPrimitive);
	}


   /**
	 * Connects this instance to the given instance using role "is primitive".
	 * If there is already an instance connected to this instance through role "is primitive", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param isPrimitive the instance to connect
	 * @throws PDStoreException
	 */
	public void setIsPrimitive(Boolean isPrimitive) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleIsPrimitiveId, isPrimitive);	
	}
	public Collection<PDRole> getAccessibleRoles() throws PDStoreException {
		Set<PDRole> result = new HashSet<PDRole>();
		for (PDRole role1 : getOwnedRoles())
			result.add(role1.getPartner());
		return result;
	}
}
