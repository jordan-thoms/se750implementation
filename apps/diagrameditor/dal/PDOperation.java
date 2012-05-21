package diagrameditor.dal;

import java.lang.reflect.Method;
import java.util.*;


import diagrameditor.ops.NewShape;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Operation" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("diagrameditor.dal.PDOperation");
 * @author PDGen
 */
public class PDOperation implements PDInstance {

	public static final GUID typeId = new GUID("920645120d6411e0b45a1cc1dec00ed3"); 

	public static final GUID roleCommandId = new GUID("920645150d6411e0b45a1cc1dec00ed3");
	public static final GUID roleNextId = new GUID("7e8d67ca4f4c11e0a5d4842b2b9af4fd");
	public static final GUID roleSuperParameterId = new GUID("920645160d6411e0b45a1cc1dec00ed3");
	public static final GUID rolePreviousId = new GUID("7e8d67ca4f4c11e0a5c4842b2b9af4fd");
	public static final GUID roleTypeId = new GUID("ee32adf0f68b11df860e1cc1dec00ed3");
	public static final GUID roleHistoryId = new GUID("920645130d6411e0b44a1cc1dec00ed3");

	static {
		DALClassRegister.addDataClass(typeId, PDOperation.class);
	}
	public PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDOperation:" + name;
		else
			return "PDOperation:" + id;
	}
	/**
	 * Creates an PDOperation object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDOperation(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDOperation object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDOperation(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDOperation load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDOperation)instance;
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
	 * Returns the instance connected to this instance through the role "command".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public String getCommand() throws PDStoreException {
	 	return (String)pdWorkingCopy.getInstance(this, roleCommandId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "command".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<String> getCommands() throws PDStoreException {
	 	Set<String> result = new HashSet<String>();
	 	GUID StringTypeId = new GUID("4a8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleCommandId, String.class, StringTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "command".
	 * If the given instance is null, nothing happens.
	 * @param command the instance to connect
	 * @throws PDStoreException
	 */
	public void addCommand(String command) throws PDStoreException {

			if (command != null) {
				
				pdWorkingCopy.addLink(this.id, roleCommandId, command);
			}

	}

	/**
	 * Connects this instance to the given instances using role "command".
	 * If the given collection of instances is null, nothing happens.
	 * @param command the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addCommands(Collection<String> commands) throws PDStoreException {
		if (commands == null)
			return;

		for (String instance : commands)
			addCommand(instance);
	}


	/**
	 * Removes the link from this instance through role "command".
	 * @throws PDStoreException
	 */
	public void removeCommand() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleCommandId, 
			pdWorkingCopy.getInstance(this, roleCommandId));
	}

	/**
	 * Removes the link from this instance through role "command" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeCommand(Object command) throws PDStoreException {
		if (command == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleCommandId, command);
	}


   /**
	 * Connects this instance to the given instance using role "command".
	 * If there is already an instance connected to this instance through role "command", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param command the instance to connect
	 * @throws PDStoreException
	 */
	public void setCommand(String command) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleCommandId, command);	
	}


	/**
	 * Returns the instance connected to this instance through the role "next".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDOperation getNext() throws PDStoreException {
	 	return (PDOperation)pdWorkingCopy.getInstance(this, roleNextId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "next".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDOperation> getNexts() throws PDStoreException {
	 	Set<PDOperation> result = new HashSet<PDOperation>();
	 	GUID PDOperationTypeId = new GUID("920645120d6411e0b45a1cc1dec00ed3");
		pdWorkingCopy.getInstances(this, roleNextId, PDOperation.class, PDOperationTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "next".
	 * If the given instance is null, nothing happens.
	 * @param next the instance to connect
	 * @throws PDStoreException
	 */
	public void addNext(GUID next) throws PDStoreException {

			if (next != null) {
				
				pdWorkingCopy.addLink(this.id, roleNextId, next);
			}

	}


	/**
	 * Connects this instance to the given instance using role "next".
	 * If the given instance is null, nothing happens.
	 * @param next the instance to connect
	 * @throws PDStoreException
	 */
	public void addNext(PDOperation next) throws PDStoreException {
		if (next != null) {
			addNext(next.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "next".
	 * If the given collection of instances is null, nothing happens.
	 * @param next the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addNexts(Collection<PDOperation> nexts) throws PDStoreException {
		if (nexts == null)
			return;
		
		for (PDOperation instance : nexts)
			addNext(instance);	
	}

	/**
	 * Removes the link from this instance through role "next".
	 * @throws PDStoreException
	 */
	public void removeNext() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleNextId, 
			pdWorkingCopy.getInstance(this, roleNextId));
	}

	/**
	 * Removes the link from this instance through role "next" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeNext(Object next) throws PDStoreException {
		if (next == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleNextId, next);
	}

	/**
	 * Removes the links from this instance through role "next" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeNexts(Collection<PDOperation> nexts) throws PDStoreException {
		if (nexts == null)
			return;
		
		for (PDOperation instance : nexts)
			pdWorkingCopy.removeLink(this.id, roleNextId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "next".
	 * If there is already an instance connected to this instance through role "next", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param next the instance to connect
	 * @throws PDStoreException
	 */
	public void setNext(GUID next) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleNextId, next);	
	}
	/**
	 * Connects this instance to the given instance using role "next".
	 * If there is already an instance connected to this instance through role "next", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param next the instance to connect
	 * @throws PDStoreException
	 */
	public void setNext(PDOperation next) throws PDStoreException {
		setNext(next.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "superParameter".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDInstance getSuperParameter() throws PDStoreException {
	 	return (PDInstance)pdWorkingCopy.getInstance(this, roleSuperParameterId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "superParameter".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDInstance> getSuperParameters() throws PDStoreException {
	 	Set<PDInstance> result = new HashSet<PDInstance>();
	 	GUID PDInstanceTypeId = new GUID("70da26e0fc3711dfa87b842b2b9af4fd");
		pdWorkingCopy.getInstances(this, roleSuperParameterId, PDInstance.class, PDInstanceTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "superParameter".
	 * If the given instance is null, nothing happens.
	 * @param superParameter the instance to connect
	 * @throws PDStoreException
	 */
	public void addSuperParameter(GUID superParameter) throws PDStoreException {

			if (superParameter != null) {
				
				pdWorkingCopy.addLink(this.id, roleSuperParameterId, superParameter);
			}

	}


	/**
	 * Connects this instance to the given instance using role "superParameter".
	 * If the given instance is null, nothing happens.
	 * @param superParameter the instance to connect
	 * @throws PDStoreException
	 */
	public void addSuperParameter(PDInstance superParameter) throws PDStoreException {
		if (superParameter != null) {
			addSuperParameter(superParameter.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "superParameter".
	 * If the given collection of instances is null, nothing happens.
	 * @param superParameter the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addSuperParameters(Collection<PDInstance> superParameters) throws PDStoreException {
		if (superParameters == null)
			return;
		
		for (PDInstance instance : superParameters)
			addSuperParameter(instance);	
	}

	/**
	 * Removes the link from this instance through role "superParameter".
	 * @throws PDStoreException
	 */
	public void removeSuperParameter() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleSuperParameterId, 
			pdWorkingCopy.getInstance(this, roleSuperParameterId));
	}

	/**
	 * Removes the link from this instance through role "superParameter" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeSuperParameter(Object superParameter) throws PDStoreException {
		if (superParameter == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleSuperParameterId, superParameter);
	}

	/**
	 * Removes the links from this instance through role "superParameter" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeSuperParameters(Collection<PDInstance> superParameters) throws PDStoreException {
		if (superParameters == null)
			return;
		
		for (PDInstance instance : superParameters)
			pdWorkingCopy.removeLink(this.id, roleSuperParameterId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "superParameter".
	 * If there is already an instance connected to this instance through role "superParameter", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param superParameter the instance to connect
	 * @throws PDStoreException
	 */
	public void setSuperParameter(GUID superParameter) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleSuperParameterId, superParameter);	
	}
	/**
	 * Connects this instance to the given instance using role "superParameter".
	 * If there is already an instance connected to this instance through role "superParameter", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param superParameter the instance to connect
	 * @throws PDStoreException
	 */
	public void setSuperParameter(PDInstance superParameter) throws PDStoreException {
		setSuperParameter(superParameter.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "previous".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDOperation getPrevious() throws PDStoreException {
	 	return (PDOperation)pdWorkingCopy.getInstance(this, rolePreviousId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "previous".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDOperation> getPreviouss() throws PDStoreException {
	 	Set<PDOperation> result = new HashSet<PDOperation>();
	 	GUID PDOperationTypeId = new GUID("920645120d6411e0b45a1cc1dec00ed3");
		pdWorkingCopy.getInstances(this, rolePreviousId, PDOperation.class, PDOperationTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "previous".
	 * If the given instance is null, nothing happens.
	 * @param previous the instance to connect
	 * @throws PDStoreException
	 */
	public void addPrevious(GUID previous) throws PDStoreException {

			if (previous != null) {
				
				pdWorkingCopy.addLink(this.id, rolePreviousId, previous);
			}

	}


	/**
	 * Connects this instance to the given instance using role "previous".
	 * If the given instance is null, nothing happens.
	 * @param previous the instance to connect
	 * @throws PDStoreException
	 */
	public void addPrevious(PDOperation previous) throws PDStoreException {
		if (previous != null) {
			addPrevious(previous.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "previous".
	 * If the given collection of instances is null, nothing happens.
	 * @param previous the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addPreviouss(Collection<PDOperation> previouss) throws PDStoreException {
		if (previouss == null)
			return;
		
		for (PDOperation instance : previouss)
			addPrevious(instance);	
	}

	/**
	 * Removes the link from this instance through role "previous".
	 * @throws PDStoreException
	 */
	public void removePrevious() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, rolePreviousId, 
			pdWorkingCopy.getInstance(this, rolePreviousId));
	}

	/**
	 * Removes the link from this instance through role "previous" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removePrevious(Object previous) throws PDStoreException {
		if (previous == null)
			return;
		pdWorkingCopy.removeLink(this.id, rolePreviousId, previous);
	}

	/**
	 * Removes the links from this instance through role "previous" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removePreviouss(Collection<PDOperation> previouss) throws PDStoreException {
		if (previouss == null)
			return;
		
		for (PDOperation instance : previouss)
			pdWorkingCopy.removeLink(this.id, rolePreviousId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "previous".
	 * If there is already an instance connected to this instance through role "previous", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param previous the instance to connect
	 * @throws PDStoreException
	 */
	public void setPrevious(GUID previous) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  rolePreviousId, previous);	
	}
	/**
	 * Connects this instance to the given instance using role "previous".
	 * If there is already an instance connected to this instance through role "previous", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param previous the instance to connect
	 * @throws PDStoreException
	 */
	public void setPrevious(PDOperation previous) throws PDStoreException {
		setPrevious(previous.getId());
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



	/**
	 * Returns the instance connected to this instance through the role "History".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDHistory getHistory() throws PDStoreException {
	 	return (PDHistory)pdWorkingCopy.getInstance(this, roleHistoryId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "History".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDHistory> getHistorys() throws PDStoreException {
	 	Set<PDHistory> result = new HashSet<PDHistory>();
	 	GUID PDHistoryTypeId = new GUID("920645100d6411e0b45a1cc1dec00ed3");
		pdWorkingCopy.getInstances(this, roleHistoryId, PDHistory.class, PDHistoryTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "History".
	 * If the given instance is null, nothing happens.
	 * @param history the instance to connect
	 * @throws PDStoreException
	 */
	public void addHistory(GUID history) throws PDStoreException {

			if (history != null) {
				
				pdWorkingCopy.addLink(this.id, roleHistoryId, history);
			}

	}


	/**
	 * Connects this instance to the given instance using role "History".
	 * If the given instance is null, nothing happens.
	 * @param history the instance to connect
	 * @throws PDStoreException
	 */
	public void addHistory(PDHistory history) throws PDStoreException {
		if (history != null) {
			addHistory(history.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "History".
	 * If the given collection of instances is null, nothing happens.
	 * @param history the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addHistorys(Collection<PDHistory> historys) throws PDStoreException {
		if (historys == null)
			return;
		
		for (PDHistory instance : historys)
			addHistory(instance);	
	}

	/**
	 * Removes the link from this instance through role "History".
	 * @throws PDStoreException
	 */
	public void removeHistory() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleHistoryId, 
			pdWorkingCopy.getInstance(this, roleHistoryId));
	}

	/**
	 * Removes the link from this instance through role "History" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeHistory(Object history) throws PDStoreException {
		if (history == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleHistoryId, history);
	}

	/**
	 * Removes the links from this instance through role "History" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeHistorys(Collection<PDHistory> historys) throws PDStoreException {
		if (historys == null)
			return;
		
		for (PDHistory instance : historys)
			pdWorkingCopy.removeLink(this.id, roleHistoryId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "History".
	 * If there is already an instance connected to this instance through role "History", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param history the instance to connect
	 * @throws PDStoreException
	 */
	public void setHistory(GUID history) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleHistoryId, history);	
	}
	/**
	 * Connects this instance to the given instance using role "History".
	 * If there is already an instance connected to this instance through role "History", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param history the instance to connect
	 * @throws PDStoreException
	 */
	public void setHistory(PDHistory history) throws PDStoreException {
		setHistory(history.getId());
	}

}
