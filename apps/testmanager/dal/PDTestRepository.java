package testmanager.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Test Repository" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("testmanager.dal.PDTestRepository");
 * @author PDGen
 */
public class PDTestRepository implements PDInstance {

	public static final GUID typeId = new GUID("98f060307e0211e18975842b2b9af4fd"); 

	public static final GUID roleTestSuiteId = new GUID("98f060357e0211e18975842b2b9af4fd");

	static {
		DALClassRegister.addDataClass(typeId, PDTestRepository.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDTestRepository:" + name;
		else
			return "PDTestRepository:" + id;
	}
	/**
	 * Creates an PDTestRepository object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDTestRepository(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDTestRepository object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDTestRepository(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDTestRepository load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDTestRepository)instance;
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
	 * Returns the instance connected to this instance through the role "test suite".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDTestSuite getTestSuite() throws PDStoreException {
	 	return (PDTestSuite)pdWorkingCopy.getInstance(this, roleTestSuiteId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "test suite".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDTestSuite> getTestSuites() throws PDStoreException {
	 	Set<PDTestSuite> result = new HashSet<PDTestSuite>();
	 	GUID PDTestSuiteTypeId = new GUID("98f060317e0211e18975842b2b9af4fd");
		pdWorkingCopy.getInstances(this, roleTestSuiteId, PDTestSuite.class, PDTestSuiteTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "test suite".
	 * If the given instance is null, nothing happens.
	 * @param testSuite the instance to connect
	 * @throws PDStoreException
	 */
	public void addTestSuite(GUID testSuite) throws PDStoreException {

			if (testSuite != null) {
				
				pdWorkingCopy.addLink(this.id, roleTestSuiteId, testSuite);
			}

	}


	/**
	 * Connects this instance to the given instance using role "test suite".
	 * If the given instance is null, nothing happens.
	 * @param testSuite the instance to connect
	 * @throws PDStoreException
	 */
	public void addTestSuite(PDTestSuite testSuite) throws PDStoreException {
		if (testSuite != null) {
			addTestSuite(testSuite.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "test suite".
	 * If the given collection of instances is null, nothing happens.
	 * @param testSuite the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addTestSuites(Collection<PDTestSuite> testSuites) throws PDStoreException {
		if (testSuites == null)
			return;
		
		for (PDTestSuite instance : testSuites)
			addTestSuite(instance);	
	}

	/**
	 * Removes the link from this instance through role "test suite".
	 * @throws PDStoreException
	 */
	public void removeTestSuite() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleTestSuiteId, 
			pdWorkingCopy.getInstance(this, roleTestSuiteId));
	}

	/**
	 * Removes the link from this instance through role "test suite" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTestSuite(Object testSuite) throws PDStoreException {
		if (testSuite == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleTestSuiteId, testSuite);
	}

	/**
	 * Removes the links from this instance through role "test suite" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTestSuites(Collection<PDTestSuite> testSuites) throws PDStoreException {
		if (testSuites == null)
			return;
		
		for (PDTestSuite instance : testSuites)
			pdWorkingCopy.removeLink(this.id, roleTestSuiteId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "test suite".
	 * If there is already an instance connected to this instance through role "test suite", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param testSuite the instance to connect
	 * @throws PDStoreException
	 */
	public void setTestSuite(GUID testSuite) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleTestSuiteId, testSuite);	
	}
	/**
	 * Connects this instance to the given instance using role "test suite".
	 * If there is already an instance connected to this instance through role "test suite", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param testSuite the instance to connect
	 * @throws PDStoreException
	 */
	public void setTestSuite(PDTestSuite testSuite) throws PDStoreException {
		setTestSuite(testSuite.getId());
	}

}
