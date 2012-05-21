package diagrameditor.dal;

import java.util.*;
import pdstore.*;
import pdstore.dal.*;

/**
 * Data access class to represent instances of type "SimpleSpatialInfo" in memory.
 * Note that this class needs to be registered with PDCache by calling:
 *    Class.forName("diagrameditor.dal.PDSimpleSpatialInfo");
 * @author PDGen
 */
public class PDSimpleSpatialInfo implements PDInstance {

	public static final GUID typeId = new GUID("920645110d6411e0b45a1cc1dec00ed3"); 

	public static final GUID roleHeightId = new GUID("920645190d6411e0b45a1cc1dec00ed3");
	public static final GUID roleShapeIDId = new GUID("9206451c0d6411e0b45a1cc1dec00ed3");
	public static final GUID roleColorId = new GUID("9206451b0d6411e0b45a1cc1dec00ed3");
	public static final GUID roleWidthId = new GUID("9206451a0d6411e0b45a1cc1dec00ed3");
	public static final GUID roleYId = new GUID("920645180d6411e0b45a1cc1dec00ed3");
	public static final GUID roleTargetIDId = new GUID("9206451d0d6411e0b45a1cc1dec00ed3");
	public static final GUID roleXId = new GUID("920645170d6411e0b45a1cc1dec00ed3");
	public static final GUID roleTypeId = new GUID("ee32adf0f68b11df860e1cc1dec00ed3");

	static {
		DALClassRegister.addDataClass(typeId, PDSimpleSpatialInfo.class);
	}
	private PDWorkingCopy pdWorkingCopy;
	private GUID id;
	public String toString() {
		String name = getName();
		if(name!=null)
			return "PDSimpleSpatialInfo:" + name;
		else
			return "PDSimpleSpatialInfo:" + id;
	}
	/**
	 * Creates an PDSimpleSpatialInfo object representing the given instance in the given cache.
	 * @param workingCopy the working copy the instance should be in
	 */
	public PDSimpleSpatialInfo(PDWorkingCopy workingCopy) {
		this(workingCopy, new GUID());
	}
	
	/**
	 * Creates an PDSimpleSpatialInfo object representing the given instance in the given copy.
	 * @param workingCopy the working copy the instance should be in
	 * @param id GUID of the instance
	 */
	public PDSimpleSpatialInfo(PDWorkingCopy workingCopy, GUID id) {
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
	public static PDSimpleSpatialInfo load(PDWorkingCopy pdWorkingCopy, GUID id) {
		PDInstance instance = pdWorkingCopy.load(typeId, id);
		return (PDSimpleSpatialInfo)instance;
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
	 * Returns the instance connected to this instance through the role "height".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getHeight() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleHeightId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "height".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getHeights() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleHeightId, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "height".
	 * If the given instance is null, nothing happens.
	 * @param height the instance to connect
	 * @throws PDStoreException
	 */
	public void addHeight(Long height) throws PDStoreException {

			if (height != null) {
				
				pdWorkingCopy.addLink(this.id, roleHeightId, height);
			}

	}

	/**
	 * Connects this instance to the given instances using role "height".
	 * If the given collection of instances is null, nothing happens.
	 * @param height the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addHeights(Collection<Long> heights) throws PDStoreException {
		if (heights == null)
			return;

		for (Long instance : heights)
			addHeight(instance);
	}


	/**
	 * Removes the link from this instance through role "height".
	 * @throws PDStoreException
	 */
	public void removeHeight() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleHeightId, 
			pdWorkingCopy.getInstance(this, roleHeightId));
	}

	/**
	 * Removes the link from this instance through role "height" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeHeight(Object height) throws PDStoreException {
		if (height == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleHeightId, height);
	}


   /**
	 * Connects this instance to the given instance using role "height".
	 * If there is already an instance connected to this instance through role "height", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param height the instance to connect
	 * @throws PDStoreException
	 */
	public void setHeight(Long height) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleHeightId, height);	
	}


	/**
	 * Returns the instance connected to this instance through the role "ShapeID".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public String getShapeID() throws PDStoreException {
	 	return (String)pdWorkingCopy.getInstance(this, roleShapeIDId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "ShapeID".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<String> getShapeIDs() throws PDStoreException {
	 	Set<String> result = new HashSet<String>();
	 	GUID StringTypeId = new GUID("4a8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleShapeIDId, String.class, StringTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "ShapeID".
	 * If the given instance is null, nothing happens.
	 * @param shapeID the instance to connect
	 * @throws PDStoreException
	 */
	public void addShapeID(String shapeID) throws PDStoreException {

			if (shapeID != null) {
				
				pdWorkingCopy.addLink(this.id, roleShapeIDId, shapeID);
			}

	}

	/**
	 * Connects this instance to the given instances using role "ShapeID".
	 * If the given collection of instances is null, nothing happens.
	 * @param shapeID the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addShapeIDs(Collection<String> shapeIDs) throws PDStoreException {
		if (shapeIDs == null)
			return;

		for (String instance : shapeIDs)
			addShapeID(instance);
	}


	/**
	 * Removes the link from this instance through role "ShapeID".
	 * @throws PDStoreException
	 */
	public void removeShapeID() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleShapeIDId, 
			pdWorkingCopy.getInstance(this, roleShapeIDId));
	}

	/**
	 * Removes the link from this instance through role "ShapeID" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeShapeID(Object shapeID) throws PDStoreException {
		if (shapeID == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleShapeIDId, shapeID);
	}


   /**
	 * Connects this instance to the given instance using role "ShapeID".
	 * If there is already an instance connected to this instance through role "ShapeID", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param shapeID the instance to connect
	 * @throws PDStoreException
	 */
	public void setShapeID(String shapeID) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleShapeIDId, shapeID);	
	}


	/**
	 * Returns the instance connected to this instance through the role "color".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public String getColor() throws PDStoreException {
	 	return (String)pdWorkingCopy.getInstance(this, roleColorId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "color".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<String> getColors() throws PDStoreException {
	 	Set<String> result = new HashSet<String>();
	 	GUID StringTypeId = new GUID("4a8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleColorId, String.class, StringTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "color".
	 * If the given instance is null, nothing happens.
	 * @param color the instance to connect
	 * @throws PDStoreException
	 */
	public void addColor(String color) throws PDStoreException {

			if (color != null) {
				
				pdWorkingCopy.addLink(this.id, roleColorId, color);
			}

	}

	/**
	 * Connects this instance to the given instances using role "color".
	 * If the given collection of instances is null, nothing happens.
	 * @param color the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addColors(Collection<String> colors) throws PDStoreException {
		if (colors == null)
			return;

		for (String instance : colors)
			addColor(instance);
	}


	/**
	 * Removes the link from this instance through role "color".
	 * @throws PDStoreException
	 */
	public void removeColor() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleColorId, 
			pdWorkingCopy.getInstance(this, roleColorId));
	}

	/**
	 * Removes the link from this instance through role "color" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeColor(Object color) throws PDStoreException {
		if (color == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleColorId, color);
	}


   /**
	 * Connects this instance to the given instance using role "color".
	 * If there is already an instance connected to this instance through role "color", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param color the instance to connect
	 * @throws PDStoreException
	 */
	public void setColor(String color) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleColorId, color);	
	}


	/**
	 * Returns the instance connected to this instance through the role "width".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getWidth() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleWidthId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "width".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getWidths() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleWidthId, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "width".
	 * If the given instance is null, nothing happens.
	 * @param width the instance to connect
	 * @throws PDStoreException
	 */
	public void addWidth(Long width) throws PDStoreException {

			if (width != null) {
				
				pdWorkingCopy.addLink(this.id, roleWidthId, width);
			}

	}

	/**
	 * Connects this instance to the given instances using role "width".
	 * If the given collection of instances is null, nothing happens.
	 * @param width the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addWidths(Collection<Long> widths) throws PDStoreException {
		if (widths == null)
			return;

		for (Long instance : widths)
			addWidth(instance);
	}


	/**
	 * Removes the link from this instance through role "width".
	 * @throws PDStoreException
	 */
	public void removeWidth() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleWidthId, 
			pdWorkingCopy.getInstance(this, roleWidthId));
	}

	/**
	 * Removes the link from this instance through role "width" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeWidth(Object width) throws PDStoreException {
		if (width == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleWidthId, width);
	}


   /**
	 * Connects this instance to the given instance using role "width".
	 * If there is already an instance connected to this instance through role "width", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param width the instance to connect
	 * @throws PDStoreException
	 */
	public void setWidth(Long width) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleWidthId, width);	
	}


	/**
	 * Returns the instance connected to this instance through the role "y".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getY() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleYId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "y".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getYs() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleYId, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "y".
	 * If the given instance is null, nothing happens.
	 * @param y the instance to connect
	 * @throws PDStoreException
	 */
	public void addY(Long y) throws PDStoreException {

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
	public void addYs(Collection<Long> ys) throws PDStoreException {
		if (ys == null)
			return;

		for (Long instance : ys)
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
	public void setY(Long y) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleYId, y);	
	}


	/**
	 * Returns the instance connected to this instance through the role "targetID".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public String getTargetID() throws PDStoreException {
	 	return (String)pdWorkingCopy.getInstance(this, roleTargetIDId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "targetID".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<String> getTargetIDs() throws PDStoreException {
	 	Set<String> result = new HashSet<String>();
	 	GUID StringTypeId = new GUID("4a8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleTargetIDId, String.class, StringTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "targetID".
	 * If the given instance is null, nothing happens.
	 * @param targetID the instance to connect
	 * @throws PDStoreException
	 */
	public void addTargetID(String targetID) throws PDStoreException {

			if (targetID != null) {
				
				pdWorkingCopy.addLink(this.id, roleTargetIDId, targetID);
			}

	}

	/**
	 * Connects this instance to the given instances using role "targetID".
	 * If the given collection of instances is null, nothing happens.
	 * @param targetID the Collection of instances to connect
	 * @throws PDStoreException
	 */
	public void addTargetIDs(Collection<String> targetIDs) throws PDStoreException {
		if (targetIDs == null)
			return;

		for (String instance : targetIDs)
			addTargetID(instance);
	}


	/**
	 * Removes the link from this instance through role "targetID".
	 * @throws PDStoreException
	 */
	public void removeTargetID() throws PDStoreException {
		pdWorkingCopy.removeLink(this.id, roleTargetIDId, 
			pdWorkingCopy.getInstance(this, roleTargetIDId));
	}

	/**
	 * Removes the link from this instance through role "targetID" to the given instance, if the link exists.
	 * If there is no such link, nothing happens.
	 * If the given instance is null, nothing happens.
	 * @throws PDStoreException
	 */
	public void removeTargetID(Object targetID) throws PDStoreException {
		if (targetID == null)
			return;
		pdWorkingCopy.removeLink(this.id, roleTargetIDId, targetID);
	}


   /**
	 * Connects this instance to the given instance using role "targetID".
	 * If there is already an instance connected to this instance through role "targetID", the link will be overwritten.
	 * If the given instance is null, an existing link is removed."
	 * @param targetID the instance to connect
	 * @throws PDStoreException
	 */
	public void setTargetID(String targetID) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleTargetIDId, targetID);	
	}


	/**
	 * Returns the instance connected to this instance through the role "x".
	 * @return the connected instance
	 * @throws PDStoreException
	 */
	 public Long getX() throws PDStoreException {
	 	return (Long)pdWorkingCopy.getInstance(this, roleXId);
	 }

	/**
	 * Returns the instance(s) connected to this instance through the role "x".
	 * @return the connected instance(s)
	 * @throws PDStoreException
	 */
	 public Collection<Long> getXs() throws PDStoreException {
	 	Set<Long> result = new HashSet<Long>();
	 	GUID LongTypeId = new GUID("4b8a986c4062db11afc0b95b08f50e2f");
		pdWorkingCopy.getInstances(this, roleXId, Long.class, LongTypeId, result);
	 	return result;
	 }
	 
   /**
	 * Connects this instance to the given instance using role "x".
	 * If the given instance is null, nothing happens.
	 * @param x the instance to connect
	 * @throws PDStoreException
	 */
	public void addX(Long x) throws PDStoreException {

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
	public void addXs(Collection<Long> xs) throws PDStoreException {
		if (xs == null)
			return;

		for (Long instance : xs)
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
	public void setX(Long x) throws PDStoreException {
		pdWorkingCopy.setLink(this.id,  roleXId, x);	
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

}
