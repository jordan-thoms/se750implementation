package pdstore.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Transaction" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.dal.PDTransaction");
 * @author PDGen
 */
public class PDTransaction implements PDInstance {

	public static final GUID typeId = new GUID("5921e0a179b811dfb27f002170295281"); 

	public static final GUID roleBranchId = new GUID("5921e0a479b811dfb27f002170295281");
	public static final GUID roleParentId = new GUID("5921e0a279b811dfb27f002170295281");
	public static final GUID roleChildId = new GUID("5921e0a279b811dfb26f002170295281");

	static {
		DALClassRegister.addDataClass(typeId, PDTransaction.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDTransaction:" + name;
		else
			return "PDTransaction:" + id;
	}
	/**
	 * Creates an PDTransaction object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDTransaction(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDTransaction object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDTransaction(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDTransaction load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDTransaction)instance;
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
	 * Returns the instance connected to this instance through the role "branch".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDBranch getBranch() throws PDStoreException {
	 	return (PDBranch)pdWorkingCopy.getInstance(this, roleBranchId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "branch".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDBranch> getBranchs() throws PDStoreException {
	 	Set<PDBranch> result = new HashSet<PDBranch>();
	 	GUID PDBranchTypeId = new GUID("5921e0a079b811dfb27f002170295281");
		pdWorkingCopy.getInstances(this, roleBranchId, PDBranch.class, PDBranchTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "branch".
	 * If the given instance is null, nothing happens.
	 * @param branch the instance to connect
	 * @throws PDStoreException
	 */
	public void addBranch(GUID branch) throws PDStoreException {

			if (branch != null) {
				
				pdWorkingCopy.addLink(this.id, roleBranchId, branch);
			}

	}


	/**
	 * Connects this instance to the given instance using role "branch".
	 * If the given instance is null, nothing happens.
	 * @param branch the instance to connect
	 * @throws PDStoreException
	 */
	public void addBranch(PDBranch branch) throws PDStoreException {
		if (branch != null) {
			addBranch(branch.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "branch".
	 * If the given collection of instances is null, nothing happens.
	 * @param branch the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addBranchs(Collection<PDBranch> branchs) throws PDStoreException {
		if (branchs == null)
			return;
		
		for (PDBranch instance : branchs)
			addBranch(instance);	
	}

	/**
	 * Removes the link from this instance through role "branch".
	 * @throws PDStoreException
	 */
	public void removeBranch() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleBranchId, 
			pdWorkingCopy.getInstance(this, roleBranchId));
	}

	/**
	 * Removes the link from this instance through role "branch" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeBranch(Object branch) throws PDStoreException {
		if (branch == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleBranchId, branch);
	}

	/**
	 * Removes the links from this instance through role "branch" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeBranchs(Collection<PDBranch> branchs) throws PDStoreException {
		if (branchs == null)
			return;
		
		for (PDBranch instance : branchs)
			pdWorkingCopy.removeLink(this.id, roleBranchId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "branch".
	 * If there is already an instance connected to this instance through role "branch", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param branch the instance to connect
	 * @throws PDStoreException
	 */
	public void setBranch(GUID branch) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleBranchId, branch);	
	}
	/**
	 * Connects this instance to the given instance using role "branch".
	 * If there is already an instance connected to this instance through role "branch", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param branch the instance to connect
	 * @throws PDStoreException
	 */
	public void setBranch(PDBranch branch) throws PDStoreException {
		setBranch(branch.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "parent".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDTransaction getParent() throws PDStoreException {
	 	return (PDTransaction)pdWorkingCopy.getInstance(this, roleParentId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "parent".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDTransaction> getParents() throws PDStoreException {
	 	Set<PDTransaction> result = new HashSet<PDTransaction>();
	 	GUID PDTransactionTypeId = new GUID("5921e0a179b811dfb27f002170295281");
		pdWorkingCopy.getInstances(this, roleParentId, PDTransaction.class, PDTransactionTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "parent".
	 * If the given instance is null, nothing happens.
	 * @param parent the instance to connect
	 * @throws PDStoreException
	 */
	public void addParent(GUID parent) throws PDStoreException {

			if (parent != null) {
				
				pdWorkingCopy.addLink(this.id, roleParentId, parent);
			}

	}


	/**
	 * Connects this instance to the given instance using role "parent".
	 * If the given instance is null, nothing happens.
	 * @param parent the instance to connect
	 * @throws PDStoreException
	 */
	public void addParent(PDTransaction parent) throws PDStoreException {
		if (parent != null) {
			addParent(parent.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "parent".
	 * If the given collection of instances is null, nothing happens.
	 * @param parent the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addParents(Collection<PDTransaction> parents) throws PDStoreException {
		if (parents == null)
			return;
		
		for (PDTransaction instance : parents)
			addParent(instance);	
	}

	/**
	 * Removes the link from this instance through role "parent".
	 * @throws PDStoreException
	 */
	public void removeParent() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleParentId, 
			pdWorkingCopy.getInstance(this, roleParentId));
	}

	/**
	 * Removes the link from this instance through role "parent" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeParent(Object parent) throws PDStoreException {
		if (parent == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleParentId, parent);
	}

	/**
	 * Removes the links from this instance through role "parent" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeParents(Collection<PDTransaction> parents) throws PDStoreException {
		if (parents == null)
			return;
		
		for (PDTransaction instance : parents)
			pdWorkingCopy.removeLink(this.id, roleParentId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "parent".
	 * If there is already an instance connected to this instance through role "parent", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param parent the instance to connect
	 * @throws PDStoreException
	 */
	public void setParent(GUID parent) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleParentId, parent);	
	}
	/**
	 * Connects this instance to the given instance using role "parent".
	 * If there is already an instance connected to this instance through role "parent", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param parent the instance to connect
	 * @throws PDStoreException
	 */
	public void setParent(PDTransaction parent) throws PDStoreException {
		setParent(parent.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "child".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDTransaction getChild() throws PDStoreException {
	 	return (PDTransaction)pdWorkingCopy.getInstance(this, roleChildId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "child".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDTransaction> getChilds() throws PDStoreException {
	 	Set<PDTransaction> result = new HashSet<PDTransaction>();
	 	GUID PDTransactionTypeId = new GUID("5921e0a179b811dfb27f002170295281");
		pdWorkingCopy.getInstances(this, roleChildId, PDTransaction.class, PDTransactionTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "child".
	 * If the given instance is null, nothing happens.
	 * @param child the instance to connect
	 * @throws PDStoreException
	 */
	public void addChild(GUID child) throws PDStoreException {

			if (child != null) {
				
				pdWorkingCopy.addLink(this.id, roleChildId, child);
			}

	}


	/**
	 * Connects this instance to the given instance using role "child".
	 * If the given instance is null, nothing happens.
	 * @param child the instance to connect
	 * @throws PDStoreException
	 */
	public void addChild(PDTransaction child) throws PDStoreException {
		if (child != null) {
			addChild(child.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "child".
	 * If the given collection of instances is null, nothing happens.
	 * @param child the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addChilds(Collection<PDTransaction> childs) throws PDStoreException {
		if (childs == null)
			return;
		
		for (PDTransaction instance : childs)
			addChild(instance);	
	}

	/**
	 * Removes the link from this instance through role "child".
	 * @throws PDStoreException
	 */
	public void removeChild() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleChildId, 
			pdWorkingCopy.getInstance(this, roleChildId));
	}

	/**
	 * Removes the link from this instance through role "child" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeChild(Object child) throws PDStoreException {
		if (child == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleChildId, child);
	}

	/**
	 * Removes the links from this instance through role "child" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeChilds(Collection<PDTransaction> childs) throws PDStoreException {
		if (childs == null)
			return;
		
		for (PDTransaction instance : childs)
			pdWorkingCopy.removeLink(this.id, roleChildId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "child".
	 * If there is already an instance connected to this instance through role "child", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param child the instance to connect
	 * @throws PDStoreException
	 */
	public void setChild(GUID child) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleChildId, child);	
	}
	/**
	 * Connects this instance to the given instance using role "child".
	 * If there is already an instance connected to this instance through role "child", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param child the instance to connect
	 * @throws PDStoreException
	 */
	public void setChild(PDTransaction child) throws PDStoreException {
		setChild(child.getId());
	}

}
