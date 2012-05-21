package pdstore.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Role" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.dal.PDRole");
 * @author PDGen
 */
public class PDRole implements PDInstance {

	public static final GUID typeId = new GUID("528a986c4062db11afc0b95b08f50e2f"); 

	public static final GUID roleOwnerId = new GUID("648a986c4062db11afd0b95b08f50e2f");
	public static final GUID roleMinMultId = new GUID("678a986c4062db11afc0b95b08f50e2f");
	public static final GUID rolePartnerId = new GUID("6d8a986c4062db11afc0b95b08f50e2f");
	public static final GUID roleMaxMultId = new GUID("698a986c4062db11afc0b95b08f50e2f");

	static {
		DALClassRegister.addDataClass(typeId, PDRole.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDRole:" + name;
		else
			return "PDRole:" + id;
	}
	/**
	 * Creates an PDRole object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDRole(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDRole object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDRole(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDRole load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDRole)instance;
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
	 * Returns the instance connected to this instance through the role "owner".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDType getOwner() throws PDStoreException {
	 	return (PDType)pdWorkingCopy.getInstance(this, roleOwnerId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "owner".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDType> getOwners() throws PDStoreException {
	 	Set<PDType> result = new HashSet<PDType>();
	 	GUID PDTypeTypeId = new GUID("518a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleOwnerId, PDType.class, PDTypeTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "owner".
	 * If the given instance is null, nothing happens.
	 * @param owner the instance to connect
	 * @throws PDStoreException
	 */
	public void addOwner(GUID owner) throws PDStoreException {

			if (owner != null) {
				
				pdWorkingCopy.addLink(this.id, roleOwnerId, owner);
			}

	}


	/**
	 * Connects this instance to the given instance using role "owner".
	 * If the given instance is null, nothing happens.
	 * @param owner the instance to connect
	 * @throws PDStoreException
	 */
	public void addOwner(PDType owner) throws PDStoreException {
		if (owner != null) {
			addOwner(owner.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "owner".
	 * If the given collection of instances is null, nothing happens.
	 * @param owner the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addOwners(Collection<PDType> owners) throws PDStoreException {
		if (owners == null)
			return;
		
		for (PDType instance : owners)
			addOwner(instance);	
	}

	/**
	 * Removes the link from this instance through role "owner".
	 * @throws PDStoreException
	 */
	public void removeOwner() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleOwnerId, 
			pdWorkingCopy.getInstance(this, roleOwnerId));
	}

	/**
	 * Removes the link from this instance through role "owner" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeOwner(Object owner) throws PDStoreException {
		if (owner == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleOwnerId, owner);
	}

	/**
	 * Removes the links from this instance through role "owner" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeOwners(Collection<PDType> owners) throws PDStoreException {
		if (owners == null)
			return;
		
		for (PDType instance : owners)
			pdWorkingCopy.removeLink(this.id, roleOwnerId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "owner".
	 * If there is already an instance connected to this instance through role "owner", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param owner the instance to connect
	 * @throws PDStoreException
	 */
	public void setOwner(GUID owner) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleOwnerId, owner);	
	}
	/**
	 * Connects this instance to the given instance using role "owner".
	 * If there is already an instance connected to this instance through role "owner", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param owner the instance to connect
	 * @throws PDStoreException
	 */
	public void setOwner(PDType owner) throws PDStoreException {
		setOwner(owner.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "min mult".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getMinMult() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleMinMultId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "min mult".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getMinMults() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleMinMultId, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "min mult".
	 * If the given instance is null, nothing happens.
	 * @param minMult the instance to connect
	 * @throws PDStoreException
	 */
	public void addMinMult(Long minMult) throws PDStoreException {

			if (minMult != null) {
				
				pdWorkingCopy.addLink(this.id, roleMinMultId, minMult);
			}

	}

	/**
	 * Connects this instance to the given instances using role "min mult".
	 * If the given collection of instances is null, nothing happens.
	 * @param minMult the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addMinMults(Collection<Long> minMults) throws PDStoreException {
		if (minMults == null)
			return;

		for (Long instance : minMults)
			addMinMult(instance);
	}


	/**
	 * Removes the link from this instance through role "min mult".
	 * @throws PDStoreException
	 */
	public void removeMinMult() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleMinMultId, 
			pdWorkingCopy.getInstance(this, roleMinMultId));
	}

	/**
	 * Removes the link from this instance through role "min mult" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeMinMult(Object minMult) throws PDStoreException {
		if (minMult == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleMinMultId, minMult);
	}


   /**
	 * Connects this instance to the given instance using role "min mult".
	 * If there is already an instance connected to this instance through role "min mult", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param minMult the instance to connect
	 * @throws PDStoreException
	 */
	public void setMinMult(Long minMult) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleMinMultId, minMult);	
	}


	/**
	 * Returns the instance connected to this instance through the role "partner".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDRole getPartner() throws PDStoreException {
	 	return (PDRole)pdWorkingCopy.getInstance(this, rolePartnerId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "partner".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDRole> getPartners() throws PDStoreException {
	 	Set<PDRole> result = new HashSet<PDRole>();
	 	GUID PDRoleTypeId = new GUID("528a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, rolePartnerId, PDRole.class, PDRoleTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "partner".
	 * If the given instance is null, nothing happens.
	 * @param partner the instance to connect
	 * @throws PDStoreException
	 */
	public void addPartner(GUID partner) throws PDStoreException {

			if (partner != null) {
				
				pdWorkingCopy.addLink(this.id, rolePartnerId, partner);
			}

	}


	/**
	 * Connects this instance to the given instance using role "partner".
	 * If the given instance is null, nothing happens.
	 * @param partner the instance to connect
	 * @throws PDStoreException
	 */
	public void addPartner(PDRole partner) throws PDStoreException {
		if (partner != null) {
			addPartner(partner.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "partner".
	 * If the given collection of instances is null, nothing happens.
	 * @param partner the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addPartners(Collection<PDRole> partners) throws PDStoreException {
		if (partners == null)
			return;
		
		for (PDRole instance : partners)
			addPartner(instance);	
	}

	/**
	 * Removes the link from this instance through role "partner".
	 * @throws PDStoreException
	 */
	public void removePartner() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, rolePartnerId, 
			pdWorkingCopy.getInstance(this, rolePartnerId));
	}

	/**
	 * Removes the link from this instance through role "partner" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removePartner(Object partner) throws PDStoreException {
		if (partner == null)
			return;
		pdWorkingCopy.removeLink(this.id, rolePartnerId, partner);
	}

	/**
	 * Removes the links from this instance through role "partner" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removePartners(Collection<PDRole> partners) throws PDStoreException {
		if (partners == null)
			return;
		
		for (PDRole instance : partners)
			pdWorkingCopy.removeLink(this.id, rolePartnerId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "partner".
	 * If there is already an instance connected to this instance through role "partner", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param partner the instance to connect
	 * @throws PDStoreException
	 */
	public void setPartner(GUID partner) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  rolePartnerId, partner);	
	}
	/**
	 * Connects this instance to the given instance using role "partner".
	 * If there is already an instance connected to this instance through role "partner", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param partner the instance to connect
	 * @throws PDStoreException
	 */
	public void setPartner(PDRole partner) throws PDStoreException {
		setPartner(partner.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "max mult".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getMaxMult() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleMaxMultId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "max mult".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getMaxMults() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleMaxMultId, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "max mult".
	 * If the given instance is null, nothing happens.
	 * @param maxMult the instance to connect
	 * @throws PDStoreException
	 */
	public void addMaxMult(Long maxMult) throws PDStoreException {

			if (maxMult != null) {
				
				pdWorkingCopy.addLink(this.id, roleMaxMultId, maxMult);
			}

	}

	/**
	 * Connects this instance to the given instances using role "max mult".
	 * If the given collection of instances is null, nothing happens.
	 * @param maxMult the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addMaxMults(Collection<Long> maxMults) throws PDStoreException {
		if (maxMults == null)
			return;

		for (Long instance : maxMults)
			addMaxMult(instance);
	}


	/**
	 * Removes the link from this instance through role "max mult".
	 * @throws PDStoreException
	 */
	public void removeMaxMult() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleMaxMultId, 
			pdWorkingCopy.getInstance(this, roleMaxMultId));
	}

	/**
	 * Removes the link from this instance through role "max mult" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeMaxMult(Object maxMult) throws PDStoreException {
		if (maxMult == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleMaxMultId, maxMult);
	}


   /**
	 * Connects this instance to the given instance using role "max mult".
	 * If there is already an instance connected to this instance through role "max mult", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param maxMult the instance to connect
	 * @throws PDStoreException
	 */
	public void setMaxMult(Long maxMult) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleMaxMultId, maxMult);	
	}
}
