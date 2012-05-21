package pdintegrate.DAL;

import java.util.Collection;

import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

public class Relation implements PDInstance{
	
	public static final GUID TYPE_ID = new GUID("126e55a3bed011dfaaa4005056c00001");
	public static final GUID ENTITY_LINK_ID = new GUID("12709f90bed011dfaaa4005056c00001");
	public static final GUID MULTIPLE_LINK_ID = new GUID("12709f91bed011dfaaa4005056c00001");
	
	static {
		DALClassRegister.addDataClass(TYPE_ID, Relation.class);
	}
	
	private PDWorkingCopy repository;
	private GUID id;
	
	public Relation(PDWorkingCopy r, GUID id) {
		this.repository = r;
		this.id = id;
	}
	
	public Relation(PDWorkingCopy r) {
		this.repository = r;
		PDInstance p = repository.newInstance(TYPE_ID); 
		this.id = p.getId();
		repository.commit();
	}
	
	public Collection<Object> getEntities() {
		return this.repository.getInstances(this, ENTITY_LINK_ID);
	}
	
	public Entity getEntity() {
		return (Entity)this.repository.getInstance(this, ENTITY_LINK_ID);
	}
	
	public void addEntity(Entity e) {
		this.repository.addLink(this.id, ENTITY_LINK_ID, e);
		this.repository.commit();
	}
	
	public void removeEntity(Entity e) {
		this.repository.removeLink(this.id, ENTITY_LINK_ID, e);
		this.repository.commit();
	}
	
	public Boolean getMultiple() {
		return (Boolean)this.repository.getInstance(this, MULTIPLE_LINK_ID);
	}
	
	public void setMultiple(boolean b) {
		this.repository.setLink(this.id, MULTIPLE_LINK_ID, new Boolean(b));
	}
		
	
	// Boilerplate
	
	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.repository;
	}

	@Override
	public GUID getTypeId() {
		return TYPE_ID;
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
