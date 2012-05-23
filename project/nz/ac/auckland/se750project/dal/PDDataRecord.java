package nz.ac.auckland.se750project.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "DataRecord" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("nz.ac.auckland.se750project.dal.PDDataRecord");
 * @author PDGen
 */
public class PDDataRecord implements PDInstance {

	public static final GUID typeId = new GUID("661937aaa3f111e19924742f68b11197"); 

	public static final GUID roleRow3Id = new GUID("005319b336ed11e08144001e8c7f9d82");
	public static final GUID roleRow2Id = new GUID("005319b236ed11e08144001e8c7f9d82");
	public static final GUID roleRow1Id = new GUID("005319b136ed11e08144001e8c7f9d82");
	public static final GUID roleContained_inId = new GUID("005319b036ed11e08154001e8c7f9d82");

	static {
		register();
	}
	
	/**
	 * Registers this DAL class with the PDStore DAL layer.
	 */
	public static void register() {
		DALClassRegister.addDataClass(typeId, PDDataRecord.class);
	}
	
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;

	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDDataRecord:" + name;
		else
			return "PDDataRecord:" + id;
	}
	
	/**
	 * Creates an PDDataRecord object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDDataRecord(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDDataRecord object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDDataRecord(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDDataRecord load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDDataRecord)instance;
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
	 * Returns the instance connected to this instance through the role "Row3".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getRow3() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleRow3Id);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "Row3".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getRow3s() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleRow3Id, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "Row3".
	 * If the given instance is null, nothing happens.
	 * @param row3 the instance to connect
	 * @throws PDStoreException
	 */
	public void addRow3(Long row3) throws PDStoreException {

			if (row3 != null) {
				
				pdWorkingCopy.addLink(this.id, roleRow3Id, row3);
			}

	}

	/**
	 * Connects this instance to the given instances using role "Row3".
	 * If the given collection of instances is null, nothing happens.
	 * @param row3 the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addRow3s(Collection<Long> row3s) throws PDStoreException {
		if (row3s == null)
			return;

		for (Long instance : row3s)
			addRow3(instance);
	}


	/**
	 * Removes the link from this instance through role "Row3".
	 * @throws PDStoreException
	 */
	public void removeRow3() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleRow3Id, 
			pdWorkingCopy.getInstance(this, roleRow3Id));
	}

	/**
	 * Removes the link from this instance through role "Row3" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeRow3(Object row3) throws PDStoreException {
		if (row3 == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleRow3Id, row3);
	}


   /**
	 * Connects this instance to the given instance using role "Row3".
	 * If there is already an instance connected to this instance through role "Row3", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param row3 the instance to connect
	 * @throws PDStoreException
	 */
	public void setRow3(Long row3) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleRow3Id, row3);	
	}


	/**
	 * Returns the instance connected to this instance through the role "Row2".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getRow2() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleRow2Id);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "Row2".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getRow2s() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleRow2Id, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "Row2".
	 * If the given instance is null, nothing happens.
	 * @param row2 the instance to connect
	 * @throws PDStoreException
	 */
	public void addRow2(Long row2) throws PDStoreException {

			if (row2 != null) {
				
				pdWorkingCopy.addLink(this.id, roleRow2Id, row2);
			}

	}

	/**
	 * Connects this instance to the given instances using role "Row2".
	 * If the given collection of instances is null, nothing happens.
	 * @param row2 the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addRow2s(Collection<Long> row2s) throws PDStoreException {
		if (row2s == null)
			return;

		for (Long instance : row2s)
			addRow2(instance);
	}


	/**
	 * Removes the link from this instance through role "Row2".
	 * @throws PDStoreException
	 */
	public void removeRow2() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleRow2Id, 
			pdWorkingCopy.getInstance(this, roleRow2Id));
	}

	/**
	 * Removes the link from this instance through role "Row2" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeRow2(Object row2) throws PDStoreException {
		if (row2 == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleRow2Id, row2);
	}


   /**
	 * Connects this instance to the given instance using role "Row2".
	 * If there is already an instance connected to this instance through role "Row2", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param row2 the instance to connect
	 * @throws PDStoreException
	 */
	public void setRow2(Long row2) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleRow2Id, row2);	
	}


	/**
	 * Returns the instance connected to this instance through the role "Row1".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getRow1() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleRow1Id);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "Row1".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getRow1s() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleRow1Id, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "Row1".
	 * If the given instance is null, nothing happens.
	 * @param row1 the instance to connect
	 * @throws PDStoreException
	 */
	public void addRow1(Long row1) throws PDStoreException {

			if (row1 != null) {
				
				pdWorkingCopy.addLink(this.id, roleRow1Id, row1);
			}

	}

	/**
	 * Connects this instance to the given instances using role "Row1".
	 * If the given collection of instances is null, nothing happens.
	 * @param row1 the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addRow1s(Collection<Long> row1s) throws PDStoreException {
		if (row1s == null)
			return;

		for (Long instance : row1s)
			addRow1(instance);
	}


	/**
	 * Removes the link from this instance through role "Row1".
	 * @throws PDStoreException
	 */
	public void removeRow1() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleRow1Id, 
			pdWorkingCopy.getInstance(this, roleRow1Id));
	}

	/**
	 * Removes the link from this instance through role "Row1" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeRow1(Object row1) throws PDStoreException {
		if (row1 == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleRow1Id, row1);
	}


   /**
	 * Connects this instance to the given instance using role "Row1".
	 * If there is already an instance connected to this instance through role "Row1", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param row1 the instance to connect
	 * @throws PDStoreException
	 */
	public void setRow1(Long row1) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleRow1Id, row1);	
	}


	/**
	 * Returns the instance connected to this instance through the role "contained_in".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDDataSet getContained_in() throws PDStoreException {
	 	return (PDDataSet)pdWorkingCopy.getInstance(this, roleContained_inId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "contained_in".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDDataSet> getContained_ins() throws PDStoreException {
	 	Set<PDDataSet> result = new HashSet<PDDataSet>();
	 	GUID PDDataSetTypeId = new GUID("fbc29377374c11e0bfe4001e8c7f9d82");
		pdWorkingCopy.getInstances(this, roleContained_inId, PDDataSet.class, PDDataSetTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "contained_in".
	 * If the given instance is null, nothing happens.
	 * @param contained_in the instance to connect
	 * @throws PDStoreException
	 */
	public void addContained_in(GUID contained_in) throws PDStoreException {

			if (contained_in != null) {
				
				pdWorkingCopy.addLink(this.id, roleContained_inId, contained_in);
			}

	}


	/**
	 * Connects this instance to the given instance using role "contained_in".
	 * If the given instance is null, nothing happens.
	 * @param contained_in the instance to connect
	 * @throws PDStoreException
	 */
	public void addContained_in(PDDataSet contained_in) throws PDStoreException {
		if (contained_in != null) {
			addContained_in(contained_in.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "contained_in".
	 * If the given collection of instances is null, nothing happens.
	 * @param contained_in the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addContained_ins(Collection<PDDataSet> contained_ins) throws PDStoreException {
		if (contained_ins == null)
			return;
		
		for (PDDataSet instance : contained_ins)
			addContained_in(instance);	
	}

	/**
	 * Removes the link from this instance through role "contained_in".
	 * @throws PDStoreException
	 */
	public void removeContained_in() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleContained_inId, 
			pdWorkingCopy.getInstance(this, roleContained_inId));
	}

	/**
	 * Removes the link from this instance through role "contained_in" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeContained_in(Object contained_in) throws PDStoreException {
		if (contained_in == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleContained_inId, contained_in);
	}

	/**
	 * Removes the links from this instance through role "contained_in" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeContained_ins(Collection<PDDataSet> contained_ins) throws PDStoreException {
		if (contained_ins == null)
			return;
		
		for (PDDataSet instance : contained_ins)
			pdWorkingCopy.removeLink(this.id, roleContained_inId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "contained_in".
	 * If there is already an instance connected to this instance through role "contained_in", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param contained_in the instance to connect
	 * @throws PDStoreException
	 */
	public void setContained_in(GUID contained_in) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleContained_inId, contained_in);	
	}
	/**
	 * Connects this instance to the given instance using role "contained_in".
	 * If there is already an instance connected to this instance through role "contained_in", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param contained_in the instance to connect
	 * @throws PDStoreException
	 */
	public void setContained_in(PDDataSet contained_in) throws PDStoreException {
		setContained_in(contained_in.getId());
	}

}
