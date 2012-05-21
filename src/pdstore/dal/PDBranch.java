package pdstore.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Branch" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.dal.PDBranch");
 * @author PDGen
 */
public class PDBranch implements PDInstance {

	public static final GUID typeId = new GUID("5921e0a079b811dfb27f002170295281"); 

	public static final GUID roleTransactionId = new GUID("5921e0a479b811dfb26f002170295281");

	static {
		DALClassRegister.addDataClass(typeId, PDBranch.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDBranch:" + name;
		else
			return "PDBranch:" + id;
	}
	/**
	 * Creates an PDBranch object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDBranch(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDBranch object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDBranch(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDBranch load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDBranch)instance;
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
	 * Returns the instance connected to this instance through the role "transaction".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDTransaction getTransaction() throws PDStoreException {
	 	return (PDTransaction)pdWorkingCopy.getInstance(this, roleTransactionId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "transaction".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDTransaction> getTransactions() throws PDStoreException {
	 	Set<PDTransaction> result = new HashSet<PDTransaction>();
	 	GUID PDTransactionTypeId = new GUID("5921e0a179b811dfb27f002170295281");
		pdWorkingCopy.getInstances(this, roleTransactionId, PDTransaction.class, PDTransactionTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "transaction".
	 * If the given instance is null, nothing happens.
	 * @param transaction the instance to connect
	 * @throws PDStoreException
	 */
	public void addTransaction(GUID transaction) throws PDStoreException {

			if (transaction != null) {
				
				pdWorkingCopy.addLink(this.id, roleTransactionId, transaction);
			}

	}


	/**
	 * Connects this instance to the given instance using role "transaction".
	 * If the given instance is null, nothing happens.
	 * @param transaction the instance to connect
	 * @throws PDStoreException
	 */
	public void addTransaction(PDTransaction transaction) throws PDStoreException {
		if (transaction != null) {
			addTransaction(transaction.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "transaction".
	 * If the given collection of instances is null, nothing happens.
	 * @param transaction the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addTransactions(Collection<PDTransaction> transactions) throws PDStoreException {
		if (transactions == null)
			return;
		
		for (PDTransaction instance : transactions)
			addTransaction(instance);	
	}

	/**
	 * Removes the link from this instance through role "transaction".
	 * @throws PDStoreException
	 */
	public void removeTransaction() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleTransactionId, 
			pdWorkingCopy.getInstance(this, roleTransactionId));
	}

	/**
	 * Removes the link from this instance through role "transaction" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTransaction(Object transaction) throws PDStoreException {
		if (transaction == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleTransactionId, transaction);
	}

	/**
	 * Removes the links from this instance through role "transaction" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTransactions(Collection<PDTransaction> transactions) throws PDStoreException {
		if (transactions == null)
			return;
		
		for (PDTransaction instance : transactions)
			pdWorkingCopy.removeLink(this.id, roleTransactionId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "transaction".
	 * If there is already an instance connected to this instance through role "transaction", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param transaction the instance to connect
	 * @throws PDStoreException
	 */
	public void setTransaction(GUID transaction) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleTransactionId, transaction);	
	}
	/**
	 * Connects this instance to the given instance using role "transaction".
	 * If there is already an instance connected to this instance through role "transaction", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param transaction the instance to connect
	 * @throws PDStoreException
	 */
	public void setTransaction(PDTransaction transaction) throws PDStoreException {
		setTransaction(transaction.getId());
	}

}
