package pdintegrate.DAL;

import java.util.Collection;

import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDGenericInstance;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

public class Entity implements PDInstance{
	
	public static final GUID TYPE_ID = new GUID("126e55a0bed011dfaaa4005056c00001");
	public static final GUID RELATION_LINK_ID = new GUID("126e55a1bed011dfaaa4005056c00001");
	public static final GUID TYPEDEF_LINK_ID = new GUID("126e55a2bed011dfaaa4005056c00001");
	
	static {
		DALClassRegister.addDataClass(TYPE_ID, Entity.class);
	}
	
	private PDWorkingCopy repository;
	private GUID id;
	
	
	
	/**
	 * Creates a dummy instance for an existing $this in the PDStore.
	 *  
	 * Do not use this method if you wish to create a new $this from scratch.
	 * 
	 * @param repository
	 * @param id
	 */
	public Entity(PDWorkingCopy repository, GUID id) {
		this.repository = repository;
		this.id = id;
	}
	
	public Entity(PDWorkingCopy repository, String name, String type) {
		this.repository = repository;
		PDInstance p = repository.newInstance(TYPE_ID); 
		this.id = p.getId();
		this.setName(name);
		this.setType(type);
		repository.commit();
		// We can refer to instances of Entities using GenericInstance classes
		DALClassRegister.addDataClass(this.id, PDGenericInstance.class);
	}
	
	public Collection<Object> getRelations() {
		return this.repository.getInstances(this, RELATION_LINK_ID);
	}
	
	public void addRelated(Entity o, boolean multiple) {
		Relation r = new Relation(this.repository);
		r.addEntity(o);
		r.setMultiple(multiple);
		this.repository.addLink(this.id, RELATION_LINK_ID, r);
		this.repository.commit();
	}
	
	public void removeRelation(Relation r) {
		this.repository.removeLink(this.id, RELATION_LINK_ID, r);
	}
	
	public String getType() {
		return (String)this.repository.getInstance(this, TYPEDEF_LINK_ID);
	}
	
	public void setType(String type) {
		 this.repository.addLink(this.id, TYPEDEF_LINK_ID, type);
	}
	
	@Override
	public String toString() {
		return this.getName() + ": " + this.getType();
	}
	
	
	//Boilerplate

	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.repository;
	}

	@Override
	public GUID getTypeId() {
		return this.TYPE_ID;
	}

	@Override
	public GUID getId() {
		return this.id;
	}

	@Override
	public String getName() throws PDStoreException {
		return this.repository.getName(this.getId());
	}

	@Override
	public void setName(String name) throws PDStoreException {
		this.repository.setName(this.id, name);
	}

	@Override
	public void removeName() throws PDStoreException {
		this.repository.removeName(this.id);
		
	}

}
