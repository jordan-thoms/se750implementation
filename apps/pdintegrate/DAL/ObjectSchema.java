package pdintegrate.DAL;

import java.lang.reflect.Field;
import java.util.Collection;

import pdintegrate.annotations.PDAttribute;
import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

public class ObjectSchema implements PDInstance  {
	
	public static final GUID TYPE_ID = new GUID("126e55a6bed011dfaaa4005056c00001");
	
	public static final GUID ENTITY_LINK_ID = new GUID("126e55a7bed011dfaaa4005056c00001");

	public static final GUID MATCH_LINK_ID = new GUID("126e55a8bed011dfaaa4005056c00001");
	
	

	static {
		DALClassRegister.addDataClass(TYPE_ID, Match.class);
	}
	
	public Collection<Object> getMatches() {
		return this.repository.getInstances(this, MATCH_LINK_ID);
	}
	public void addMatch(Match m) {
		this.repository.addLink(this.getId(), MATCH_LINK_ID, m);
	}
	
	public void removeMatch(Match m) {
		this.repository.removeLink(this.getId(), MATCH_LINK_ID, m);
	}
	
	public Collection<Object> getEntities() {
		return this.repository.getInstances(this, ENTITY_LINK_ID);
	}
	public void addEntity(Entity e) {
		this.repository.addLink(this.getId(), ENTITY_LINK_ID, e);
	}
	
	public void removeEntity(Entity e) {
		this.repository.removeLink(this.getId(), ENTITY_LINK_ID, e);
	}
	
	private PDWorkingCopy repository;
	private GUID id;
	
	public ObjectSchema(PDWorkingCopy repository, GUID id) {
		this.id = id;
		this.repository = repository;
	}
	
	public ObjectSchema(PDWorkingCopy repository, Class<?> c) {
		this.repository = repository;
		PDInstance pi = repository.newInstance(TYPE_ID);
		this.id = pi.getId();
		Entity e = new Entity(repository, "root", c.getCanonicalName());
		this.addEntity(e);
		//TODO: Enable objects and multiplicities as well
		for (Field f : c.getDeclaredFields()) {
			if (f.getAnnotation(PDAttribute.class) != null) { //This is an attribute we wish to connect
				Entity o = new Entity(repository, f.getName(), f.getType().getCanonicalName());
				e.addRelated(o, false);
			}
		}
		repository.commit();
	}
	

	@Override
	public GUID getId() {
		return this.id;
	}

	@Override
	public String getName() throws PDStoreException {
		return this.repository.getName(this.id);
	}

	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.repository;
	}

	@Override
	public GUID getTypeId() {
		return TYPE_ID;
	}

	@Override
	public void removeName() throws PDStoreException {
		this.repository.removeName(id);
		
	}

	@Override
	public void setName(String name) throws PDStoreException {
		this.repository.setName(id, name);
		
	}

}
