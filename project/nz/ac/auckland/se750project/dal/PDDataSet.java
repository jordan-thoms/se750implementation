package nz.ac.auckland.se750project.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "DataSet" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("nz.ac.auckland.se750project.dal.PDDataSet");
 * @author PDGen
 */
public class PDDataSet implements PDInstance {

	public static final GUID typeId = new GUID("fbc29377374c11e0bfe4001e8c7f9d82"); 

	public static final GUID roleRecordId = new GUID("005319b036ed11e08144001e8c7f9d82");

	static {
		register();
	}
	
	/**
	 * Registers this DAL class with the PDStore DAL layer.
	 */
	public static void register() {
		DALClassRegister.addDataClass(typeId, PDDataSet.class);
	}
	
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;

	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDDataSet:" + name;
		else
			return "PDDataSet:" + id;
	}
	
	/**
	 * Creates an PDDataSet object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDDataSet(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDDataSet object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDDataSet(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDDataSet load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDDataSet)instance;
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
	 * Gets a textual label for this instance, for use in UIs.
	 * @return a textual label for the instance
	 */
	public String getLabel() {
		return pdWorkingCopy.getLabel(id);
	}
	
	/**
	 * Gets the name of this instance.
	 * In PDStore every instance can be given a name.
	 * @return name the instance name
	 * @throws PDStoreException
	 */
	public String getName() {
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
	public void setName(String name) {
		pdWorkingCopy.setName(id, name);
	}

	/**
	 * Removes the name of this instance.
	 * In PDStore every instance can be given a name.
	 * If the instance does not have a name, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeName() {
		pdWorkingCopy.removeName(id);
	}

	/**
	 * Gets the icon of this instance.
	 * In PDStore every instance can be given an icon.
	 * @return icon the instance icon
	 * @throws PDStoreException
	 */
	public Blob getIcon() {
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
	public void setIcon(Blob icon) {
		pdWorkingCopy.setIcon(id, icon);
	}

	/**
	 * Removes the icon of this instance.
	 * In PDStore every instance can be given an icon.
	 * If the instance does not have an icon, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeIcon() {
		pdWorkingCopy.removeIcon(id);
	}
	

	/**
	 * Returns the instance connected to this instance through the role "record".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDDataRecord getRecord() throws PDStoreException {
	 	return (PDDataRecord)pdWorkingCopy.getInstance(this, roleRecordId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "record".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDDataRecord> getRecords() throws PDStoreException {
	 	Set<PDDataRecord> result = new HashSet<PDDataRecord>();
	 	GUID PDDataRecordTypeId = new GUID("661937aaa3f111e19924742f68b11197");
		pdWorkingCopy.getInstances(this, roleRecordId, PDDataRecord.class, PDDataRecordTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "record".
	 * If the given instance is null, nothing happens.
	 * @param record the instance to connect
	 * @throws PDStoreException
	 */
	public void addRecord(GUID record) throws PDStoreException {

			if (record != null) {
				
				pdWorkingCopy.addLink(this.id, roleRecordId, record);
			}

	}


	/**
	 * Connects this instance to the given instance using role "record".
	 * If the given instance is null, nothing happens.
	 * @param record the instance to connect
	 * @throws PDStoreException
	 */
	public void addRecord(PDDataRecord record) throws PDStoreException {
		if (record != null) {
			addRecord(record.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "record".
	 * If the given collection of instances is null, nothing happens.
	 * @param record the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addRecords(Collection<PDDataRecord> records) throws PDStoreException {
		if (records == null)
			return;
		
		for (PDDataRecord instance : records)
			addRecord(instance);	
	}

	/**
	 * Removes the link from this instance through role "record".
	 * @throws PDStoreException
	 */
	public void removeRecord() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleRecordId, 
			pdWorkingCopy.getInstance(this, roleRecordId));
	}

	/**
	 * Removes the link from this instance through role "record" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeRecord(Object record) throws PDStoreException {
		if (record == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleRecordId, record);
	}

	/**
	 * Removes the links from this instance through role "record" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeRecords(Collection<PDDataRecord> records) throws PDStoreException {
		if (records == null)
			return;
		
		for (PDDataRecord instance : records)
			pdWorkingCopy.removeLink(this.id, roleRecordId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "record".
	 * If there is already an instance connected to this instance through role "record", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param record the instance to connect
	 * @throws PDStoreException
	 */
	public void setRecord(GUID record) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleRecordId, record);	
	}
	/**
	 * Connects this instance to the given instance using role "record".
	 * If there is already an instance connected to this instance through role "record", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param record the instance to connect
	 * @throws PDStoreException
	 */
	public void setRecord(PDDataRecord record) throws PDStoreException {
		setRecord(record.getId());
	}

}
