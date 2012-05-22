package pdtransform.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Generator Application" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdtransform.dal.PDGeneratorApplication");
 * @author PDGen
 */
public class PDGeneratorApplication implements PDInstance {

	public static final GUID typeId = new GUID("e45c1f278e0ede11980f9a097666e103"); 

	public static final GUID roleInputId = new GUID("e65c1f278e0ede11980f9a097666e103");
	public static final GUID roleGeneratorId = new GUID("f05c1f278e0ede11980f9a097666e103");
	public static final GUID roleOutputId = new GUID("ed5c1f278e0ede11980f9a097666e103");

	static {
		DALClassRegister.addDataClass(typeId, PDGeneratorApplication.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDGeneratorApplication:" + name;
		else
			return "PDGeneratorApplication:" + id;
	}
	/**
	 * Creates an PDGeneratorApplication object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDGeneratorApplication(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDGeneratorApplication object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDGeneratorApplication(PDWorkingCopy workingCopy, GUID id) {
		this.pdWorkingCopy = workingCopy;
		this.id = id;
		
		
	}
	/**
	 * Loads an instance object of this type into a cache.
	 * If the instance is already in the cache, the cached instance is returned.
	 * @param PDWorkingCopy pdWorkingCopy to load the instance into
	 * @param id GUID of the instance
	 * Do not directly call this method. Use the newInstance() method in PDCache which would call this method
	 */
	public static PDGeneratorApplication load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDGeneratorApplication)instance;
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

	
	  // Code generated by PDGetSetRemoveGen:


	/**
	 * Returns the instance connected to this instance through the role "input".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public GUID getInput() throws PDStoreException {
	 	return (GUID)pdWorkingCopy.getInstance(this, roleInputId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "input".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<GUID> getInputs() throws PDStoreException {
	 	Set<GUID> result = new HashSet<GUID>();
	 	GUID GUIDTypeId = new GUID("538a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleInputId, GUID.class, GUIDTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "input".
	 * If the given instance is null, nothing happens.
	 * @param input the instance to connect
	 * @throws PDStoreException
	 */
	public void addInput(GUID input) throws PDStoreException {

			if (input != null) {
				
				pdWorkingCopy.addLink(this.id, roleInputId, input);
			}

	}


	/**
	 * Removes the link from this instance through role "input".
	 * @throws PDStoreException
	 */
	public void removeInput() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleInputId, 
			pdWorkingCopy.getInstance(this, roleInputId));
	}

	/**
	 * Removes the link from this instance through role "input" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.input.getId()
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeInput(Object input) throws PDStoreException {
		if (input==null)
			return;
		pdWorkingCopy.removeLink(this.id, roleInputId, input);
	}

   /**
	 * Connects this instance to the given instance using role "input".
	 * If there is already an instance connected to this instance through role "input", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param input the instance to connect
	 * @throws PDStoreException
	 */
	public void setInput(GUID input) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleInputId, input);	
	}
  // Code generated by PDGetSetRemoveGen:


	/**
	 * Returns the instance connected to this instance through the role "generator".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDGenerator getGenerator() throws PDStoreException {
	 	return (PDGenerator)pdWorkingCopy.getInstance(this, roleGeneratorId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "generator".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDGenerator> getGenerators() throws PDStoreException {
	 	Set<PDGenerator> result = new HashSet<PDGenerator>();
	 	GUID PDGeneratorTypeId = new GUID("fa5c1f278e0ede11980f9a097666e103");
		pdWorkingCopy.getInstances(this, roleGeneratorId, PDGenerator.class, PDGeneratorTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "generator".
	 * If the given instance is null, nothing happens.
	 * @param generator the instance to connect
	 * @throws PDStoreException
	 */
	public void addGenerator(GUID generator) throws PDStoreException {

			if (generator != null) {
				
				pdWorkingCopy.addLink(this.id, roleGeneratorId, generator);
			}

	}

	/**
	 * Connects this instance to the given instance using role "generator".
	 * If the given instance is null, nothing happens.
	 * @param generator the instance to connect
	 * @throws PDStoreException
	 */
	public void addGenerator(PDGenerator generator) throws PDStoreException {
		if (generator != null) {
			addGenerator(generator.getId());
		}		
	}

	/**
	 * Removes the link from this instance through role "generator".
	 * @throws PDStoreException
	 */
	public void removeGenerator() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleGeneratorId, 
			pdWorkingCopy.getInstance(this, roleGeneratorId));
	}

	/**
	 * Removes the link from this instance through role "generator" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.generator.getId()
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeGenerator(Object generator) throws PDStoreException {
		if (generator==null)
			return;
		pdWorkingCopy.removeLink(this.id, roleGeneratorId, generator);
	}

   /**
	 * Connects this instance to the given instance using role "generator".
	 * If there is already an instance connected to this instance through role "generator", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param generator the instance to connect
	 * @throws PDStoreException
	 */
	public void setGenerator(GUID generator) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleGeneratorId, generator);	
	}
	/**
	 * Connects this instance to the given instance using role "generator".
	 * If there is already an instance connected to this instance through role "generator", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param generator the instance to connect
	 * @throws PDStoreException
	 */
	public void setGenerator(PDGenerator generator) throws PDStoreException {
		setGenerator(generator.getId());
	}

  // Code generated by PDGetSetRemoveGen:


	/**
	 * Returns the instance connected to this instance through the role "output".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public GUID getOutput() throws PDStoreException {
	 	return (GUID)pdWorkingCopy.getInstance(this, roleOutputId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "output".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<GUID> getOutputs() throws PDStoreException {
	 	Set<GUID> result = new HashSet<GUID>();
	 	GUID GUIDTypeId = new GUID("538a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleOutputId, GUID.class, GUIDTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "output".
	 * If the given instance is null, nothing happens.
	 * @param output the instance to connect
	 * @throws PDStoreException
	 */
	public void addOutput(GUID output) throws PDStoreException {

			if (output != null) {
				
				pdWorkingCopy.addLink(this.id, roleOutputId, output);
			}

	}


	/**
	 * Removes the link from this instance through role "output".
	 * @throws PDStoreException
	 */
	public void removeOutput() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleOutputId, 
			pdWorkingCopy.getInstance(this, roleOutputId));
	}

	/**
	 * Removes the link from this instance through role "output" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.output.getId()
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeOutput(Object output) throws PDStoreException {
		if (output==null)
			return;
		pdWorkingCopy.removeLink(this.id, roleOutputId, output);
	}

   /**
	 * Connects this instance to the given instance using role "output".
	 * If there is already an instance connected to this instance through role "output", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param output the instance to connect
	 * @throws PDStoreException
	 */
	public void setOutput(GUID output) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleOutputId, output);	
	}
}