package pdstore.ui.graphview.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Graph" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.ui.graphview.dal.PDGraph");
 * @author PDGen
 */
public class PDGraph implements PDInstance {

	public static final GUID typeId = new GUID("e44c1e501d9211e1a04700235411d565"); 

	public static final GUID roleNodeId = new GUID("e44c1e521d9211e1a04700235411d565");

	static {
		register();
	}
	
	/**
	 * Registers this DAL class with the PDStore DAL layer.
	 */
	public static void register() {
		DALClassRegister.addDataClass(typeId, PDGraph.class);
	}
	
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;

	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDGraph:" + name;
		else
			return "PDGraph:" + id;
	}
	
	/**
	 * Creates an PDGraph object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDGraph(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDGraph object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDGraph(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDGraph load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDGraph)instance;
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
	 * Returns the instance connected to this instance through the role "Node".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDNode getNode() throws PDStoreException {
	 	return (PDNode)pdWorkingCopy.getInstance(this, roleNodeId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "Node".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDNode> getNodes() throws PDStoreException {
	 	Set<PDNode> result = new HashSet<PDNode>();
	 	GUID PDNodeTypeId = new GUID("e44c1e511d9211e1a04700235411d565");
		pdWorkingCopy.getInstances(this, roleNodeId, PDNode.class, PDNodeTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "Node".
	 * If the given instance is null, nothing happens.
	 * @param node the instance to connect
	 * @throws PDStoreException
	 */
	public void addNode(GUID node) throws PDStoreException {

			if (node != null) {
				
				pdWorkingCopy.addLink(this.id, roleNodeId, node);
			}

	}


	/**
	 * Connects this instance to the given instance using role "Node".
	 * If the given instance is null, nothing happens.
	 * @param node the instance to connect
	 * @throws PDStoreException
	 */
	public void addNode(PDNode node) throws PDStoreException {
		if (node != null) {
			addNode(node.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "Node".
	 * If the given collection of instances is null, nothing happens.
	 * @param node the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addNodes(Collection<PDNode> nodes) throws PDStoreException {
		if (nodes == null)
			return;
		
		for (PDNode instance : nodes)
			addNode(instance);	
	}

	/**
	 * Removes the link from this instance through role "Node".
	 * @throws PDStoreException
	 */
	public void removeNode() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleNodeId, 
			pdWorkingCopy.getInstance(this, roleNodeId));
	}

	/**
	 * Removes the link from this instance through role "Node" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeNode(Object node) throws PDStoreException {
		if (node == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleNodeId, node);
	}

	/**
	 * Removes the links from this instance through role "Node" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeNodes(Collection<PDNode> nodes) throws PDStoreException {
		if (nodes == null)
			return;
		
		for (PDNode instance : nodes)
			pdWorkingCopy.removeLink(this.id, roleNodeId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "Node".
	 * If there is already an instance connected to this instance through role "Node", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param node the instance to connect
	 * @throws PDStoreException
	 */
	public void setNode(GUID node) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleNodeId, node);	
	}
	/**
	 * Connects this instance to the given instance using role "Node".
	 * If there is already an instance connected to this instance through role "Node", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param node the instance to connect
	 * @throws PDStoreException
	 */
	public void setNode(PDNode node) throws PDStoreException {
		setNode(node.getId());
	}

}
