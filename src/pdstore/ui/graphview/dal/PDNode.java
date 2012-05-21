package pdstore.ui.graphview.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "Node" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("pdstore.ui.graphview.dal.PDNode");
 * @author PDGen
 */
public class PDNode implements PDInstance {

	public static final GUID typeId = new GUID("e44c1e511d9211e1a04700235411d565"); 

	public static final GUID roleGraphId = new GUID("e44c1e521d9211e1a05700235411d565");
	public static final GUID roleShownInstanceId = new GUID("e44c1e531d9211e1a04700235411d565");
	public static final GUID roleXId = new GUID("e44c1e541d9211e1a04700235411d565");
	public static final GUID roleYId = new GUID("e44c1e551d9211e1a04700235411d565");

	static {
		register();
	}
	
	/**
	 * Registers this DAL class with the PDStore DAL layer.
	 */
	public static void register() {
		DALClassRegister.addDataClass(typeId, PDNode.class);
	}
	
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;

	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDNode:" + name;
		else
			return "PDNode:" + id;
	}
	
	/**
	 * Creates an PDNode object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDNode(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDNode object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDNode(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDNode load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDNode)instance;
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
	 * Returns the instance connected to this instance through the role "Graph".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public PDGraph getGraph() throws PDStoreException {
	 	return (PDGraph)pdWorkingCopy.getInstance(this, roleGraphId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "Graph".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<PDGraph> getGraphs() throws PDStoreException {
	 	Set<PDGraph> result = new HashSet<PDGraph>();
	 	GUID PDGraphTypeId = new GUID("e44c1e501d9211e1a04700235411d565");
		pdWorkingCopy.getInstances(this, roleGraphId, PDGraph.class, PDGraphTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "Graph".
	 * If the given instance is null, nothing happens.
	 * @param graph the instance to connect
	 * @throws PDStoreException
	 */
	public void addGraph(GUID graph) throws PDStoreException {

			if (graph != null) {
				
				pdWorkingCopy.addLink(this.id, roleGraphId, graph);
			}

	}


	/**
	 * Connects this instance to the given instance using role "Graph".
	 * If the given instance is null, nothing happens.
	 * @param graph the instance to connect
	 * @throws PDStoreException
	 */
	public void addGraph(PDGraph graph) throws PDStoreException {
		if (graph != null) {
			addGraph(graph.getId());
		}		
	}
	
	/**
	 * Connects this instance to the given instance using role "Graph".
	 * If the given collection of instances is null, nothing happens.
	 * @param graph the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addGraphs(Collection<PDGraph> graphs) throws PDStoreException {
		if (graphs == null)
			return;
		
		for (PDGraph instance : graphs)
			addGraph(instance);	
	}

	/**
	 * Removes the link from this instance through role "Graph".
	 * @throws PDStoreException
	 */
	public void removeGraph() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleGraphId, 
			pdWorkingCopy.getInstance(this, roleGraphId));
	}

	/**
	 * Removes the link from this instance through role "Graph" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeGraph(Object graph) throws PDStoreException {
		if (graph == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleGraphId, graph);
	}

	/**
	 * Removes the links from this instance through role "Graph" to the instances 
	 * in the given Collection, if the links exist.
	 * If there are no such links or the collection argument is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeGraphs(Collection<PDGraph> graphs) throws PDStoreException {
		if (graphs == null)
			return;
		
		for (PDGraph instance : graphs)
			pdWorkingCopy.removeLink(this.id, roleGraphId, instance);
	}

   /**
	 * Connects this instance to the given instance using role "Graph".
	 * If there is already an instance connected to this instance through role "Graph", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param graph the instance to connect
	 * @throws PDStoreException
	 */
	public void setGraph(GUID graph) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleGraphId, graph);	
	}
	/**
	 * Connects this instance to the given instance using role "Graph".
	 * If there is already an instance connected to this instance through role "Graph", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param graph the instance to connect
	 * @throws PDStoreException
	 */
	public void setGraph(PDGraph graph) throws PDStoreException {
		setGraph(graph.getId());
	}



	/**
	 * Returns the instance connected to this instance through the role "shown instance".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public GUID getShownInstance() throws PDStoreException {
	 	return (GUID)pdWorkingCopy.getInstance(this, roleShownInstanceId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "shown instance".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<GUID> getShownInstances() throws PDStoreException {
	 	Set<GUID> result = new HashSet<GUID>();
	 	GUID GUIDTypeId = new GUID("538a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleShownInstanceId, GUID.class, GUIDTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "shown instance".
	 * If the given instance is null, nothing happens.
	 * @param shownInstance the instance to connect
	 * @throws PDStoreException
	 */
	public void addShownInstance(GUID shownInstance) throws PDStoreException {

			if (shownInstance != null) {
				
				pdWorkingCopy.addLink(this.id, roleShownInstanceId, shownInstance);
			}

	}

	/**
	 * Connects this instance to the given instances using role "shown instance".
	 * If the given collection of instances is null, nothing happens.
	 * @param shownInstance the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addShownInstances(Collection<GUID> shownInstances) throws PDStoreException {
		if (shownInstances == null)
			return;

		for (GUID instance : shownInstances)
			addShownInstance(instance);
	}


	/**
	 * Removes the link from this instance through role "shown instance".
	 * @throws PDStoreException
	 */
	public void removeShownInstance() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleShownInstanceId, 
			pdWorkingCopy.getInstance(this, roleShownInstanceId));
	}

	/**
	 * Removes the link from this instance through role "shown instance" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeShownInstance(Object shownInstance) throws PDStoreException {
		if (shownInstance == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleShownInstanceId, shownInstance);
	}


   /**
	 * Connects this instance to the given instance using role "shown instance".
	 * If there is already an instance connected to this instance through role "shown instance", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param shownInstance the instance to connect
	 * @throws PDStoreException
	 */
	public void setShownInstance(GUID shownInstance) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleShownInstanceId, shownInstance);	
	}


	/**
	 * Returns the instance connected to this instance through the role "x".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Double getX() throws PDStoreException {
	 	return (Double)pdWorkingCopy.getInstance(this, roleXId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "x".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Double> getXs() throws PDStoreException {
	 	Set<Double> result = new HashSet<Double>();
	 	GUID DoubleTypeId = new GUID("4c8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleXId, Double.class, DoubleTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "x".
	 * If the given instance is null, nothing happens.
	 * @param x the instance to connect
	 * @throws PDStoreException
	 */
	public void addX(Double x) throws PDStoreException {

			if (x != null) {
				
				pdWorkingCopy.addLink(this.id, roleXId, x);
			}

	}

	/**
	 * Connects this instance to the given instances using role "x".
	 * If the given collection of instances is null, nothing happens.
	 * @param x the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addXs(Collection<Double> xs) throws PDStoreException {
		if (xs == null)
			return;

		for (Double instance : xs)
			addX(instance);
	}


	/**
	 * Removes the link from this instance through role "x".
	 * @throws PDStoreException
	 */
	public void removeX() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleXId, 
			pdWorkingCopy.getInstance(this, roleXId));
	}

	/**
	 * Removes the link from this instance through role "x" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeX(Object x) throws PDStoreException {
		if (x == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleXId, x);
	}


   /**
	 * Connects this instance to the given instance using role "x".
	 * If there is already an instance connected to this instance through role "x", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param x the instance to connect
	 * @throws PDStoreException
	 */
	public void setX(Double x) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleXId, x);	
	}


	/**
	 * Returns the instance connected to this instance through the role "y".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Double getY() throws PDStoreException {
	 	return (Double)pdWorkingCopy.getInstance(this, roleYId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "y".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Double> getYs() throws PDStoreException {
	 	Set<Double> result = new HashSet<Double>();
	 	GUID DoubleTypeId = new GUID("4c8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleYId, Double.class, DoubleTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "y".
	 * If the given instance is null, nothing happens.
	 * @param y the instance to connect
	 * @throws PDStoreException
	 */
	public void addY(Double y) throws PDStoreException {

			if (y != null) {
				
				pdWorkingCopy.addLink(this.id, roleYId, y);
			}

	}

	/**
	 * Connects this instance to the given instances using role "y".
	 * If the given collection of instances is null, nothing happens.
	 * @param y the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addYs(Collection<Double> ys) throws PDStoreException {
		if (ys == null)
			return;

		for (Double instance : ys)
			addY(instance);
	}


	/**
	 * Removes the link from this instance through role "y".
	 * @throws PDStoreException
	 */
	public void removeY() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleYId, 
			pdWorkingCopy.getInstance(this, roleYId));
	}

	/**
	 * Removes the link from this instance through role "y" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeY(Object y) throws PDStoreException {
		if (y == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleYId, y);
	}


   /**
	 * Connects this instance to the given instance using role "y".
	 * If there is already an instance connected to this instance through role "y", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param y the instance to connect
	 * @throws PDStoreException
	 */
	public void setY(Double y) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleYId, y);	
	}
}
