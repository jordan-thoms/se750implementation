package testmanager.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Test Suite" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("testmanager.dal.PDTestSuite");
 * @author PDGen
 */
public class PDTestSuite implements PDInstance {

	public static final GUID typeId = new GUID("98f060317e0211e18975842b2b9af4fd"); 

	public static final GUID roleTestRepositoryId = new GUID("98f060357e0211e18965842b2b9af4fd");
	public static final GUID roleTestCaseId = new GUID("98f060367e0211e18975842b2b9af4fd");

	static {
		DALClassRegister.addDataClass(typeId, PDTestSuite.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDTestSuite:" + name;
		else
			return "PDTestSuite:" + id;
	}
	/**
	 * Creates an PDTestSuite object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDTestSuite(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDTestSuite object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDTestSuite(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDTestSuite load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDTestSuite)instance;
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
	 * Returns the instance connected to this instance through the role "test repository".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDTestRepository getTestRepository() throws PDStoreException {
	 	return (PDTestRepository)pdWorkingCopy.getInstance(this, roleTestRepositoryId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "test repository".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDTestRepository> getTestRepositorys() throws PDStoreException {
	 	Set<PDTestRepository> result = new HashSet<PDTestRepository>();
	 	GUID PDTestRepositoryTypeId = new GUID("98f060307e0211e18975842b2b9af4fd");
		pdWorkingCopy.getInstances(this, roleTestRepositoryId, PDTestRepository.class, PDTestRepositoryTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "test repository".
	 * If the given instance is null, nothing happens.
	 * @param testRepository the instance to connect
	 * @throws PDStoreException
	 */
	public void addTestRepository(GUID testRepository) throws PDStoreException {

			if (testRepository != null) {
				
				pdWorkingCopy.addLink(this.id, roleTestRepositoryId, testRepository);
			}

	}


	/**
	 * Connects this instance to the given instance using role "test repository".
	 * If the given instance is null, nothing happens.
	 * @param testRepository the instance to connect
	 * @throws PDStoreException
	 */
	public void addTestRepository(PDTestRepository testRepository) throws PDStoreException {
		if (testRepository != null) {
			addTestRepository(testRepository.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "test repository".
	 * If the given collection of instances is null, nothing happens.
	 * @param testRepository the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addTestRepositorys(Collection<PDTestRepository> testRepositorys) throws PDStoreException {
		if (testRepositorys == null)
			return;
		
		for (PDTestRepository instance : testRepositorys)
			addTestRepository(instance);	
	}

	/**
	 * Removes the link from this instance through role "test repository".
	 * @throws PDStoreException
	 */
	public void removeTestRepository() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleTestRepositoryId, 
			pdWorkingCopy.getInstance(this, roleTestRepositoryId));
	}

	/**
	 * Removes the link from this instance through role "test repository" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTestRepository(Object testRepository) throws PDStoreException {
		if (testRepository == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleTestRepositoryId, testRepository);
	}

	/**
	 * Removes the links from this instance through role "test repository" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTestRepositorys(Collection<PDTestRepository> testRepositorys) throws PDStoreException {
		if (testRepositorys == null)
			return;
		
		for (PDTestRepository instance : testRepositorys)
			pdWorkingCopy.removeLink(this.id, roleTestRepositoryId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "test repository".
	 * If there is already an instance connected to this instance through role "test repository", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param testRepository the instance to connect
	 * @throws PDStoreException
	 */
	public void setTestRepository(GUID testRepository) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleTestRepositoryId, testRepository);	
	}
	/**
	 * Connects this instance to the given instance using role "test repository".
	 * If there is already an instance connected to this instance through role "test repository", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param testRepository the instance to connect
	 * @throws PDStoreException
	 */
	public void setTestRepository(PDTestRepository testRepository) throws PDStoreException {
		setTestRepository(testRepository.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "test case".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDTestCase getTestCase() throws PDStoreException {
	 	return (PDTestCase)pdWorkingCopy.getInstance(this, roleTestCaseId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "test case".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDTestCase> getTestCases() throws PDStoreException {
	 	Set<PDTestCase> result = new HashSet<PDTestCase>();
	 	GUID PDTestCaseTypeId = new GUID("98f060327e0211e18975842b2b9af4fd");
		pdWorkingCopy.getInstances(this, roleTestCaseId, PDTestCase.class, PDTestCaseTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "test case".
	 * If the given instance is null, nothing happens.
	 * @param testCase the instance to connect
	 * @throws PDStoreException
	 */
	public void addTestCase(GUID testCase) throws PDStoreException {

			if (testCase != null) {
				
				pdWorkingCopy.addLink(this.id, roleTestCaseId, testCase);
			}

	}


	/**
	 * Connects this instance to the given instance using role "test case".
	 * If the given instance is null, nothing happens.
	 * @param testCase the instance to connect
	 * @throws PDStoreException
	 */
	public void addTestCase(PDTestCase testCase) throws PDStoreException {
		if (testCase != null) {
			addTestCase(testCase.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "test case".
	 * If the given collection of instances is null, nothing happens.
	 * @param testCase the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addTestCases(Collection<PDTestCase> testCases) throws PDStoreException {
		if (testCases == null)
			return;
		
		for (PDTestCase instance : testCases)
			addTestCase(instance);	
	}

	/**
	 * Removes the link from this instance through role "test case".
	 * @throws PDStoreException
	 */
	public void removeTestCase() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleTestCaseId, 
			pdWorkingCopy.getInstance(this, roleTestCaseId));
	}

	/**
	 * Removes the link from this instance through role "test case" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTestCase(Object testCase) throws PDStoreException {
		if (testCase == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleTestCaseId, testCase);
	}

	/**
	 * Removes the links from this instance through role "test case" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTestCases(Collection<PDTestCase> testCases) throws PDStoreException {
		if (testCases == null)
			return;
		
		for (PDTestCase instance : testCases)
			pdWorkingCopy.removeLink(this.id, roleTestCaseId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "test case".
	 * If there is already an instance connected to this instance through role "test case", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param testCase the instance to connect
	 * @throws PDStoreException
	 */
	public void setTestCase(GUID testCase) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleTestCaseId, testCase);	
	}
	/**
	 * Connects this instance to the given instance using role "test case".
	 * If there is already an instance connected to this instance through role "test case", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param testCase the instance to connect
	 * @throws PDStoreException
	 */
	public void setTestCase(PDTestCase testCase) throws PDStoreException {
		setTestCase(testCase.getId());
	}

}
