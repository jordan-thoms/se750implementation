package pdintegrate.DAL;

import java.util.Collection;
import java.util.NoSuchElementException;

import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;


/* A schema is just a list of names and types.
 */
public class Schema implements PDInstance {
	
	public static GUID TYPE_ID = new GUID("126e2e92bed011dfaaa4005056c00001");
	
	public static GUID MATCH_LINK_ID = new GUID("126e2e97bed011dfaaa4005056c00001");
	
	public static GUID ENTITY_LINK_ID = new GUID("126e2e98bed011dfaaa4005056c00001");
	
	static {
		DALClassRegister.addDataClass(TYPE_ID, Schema.class);
	}
	
	private PDWorkingCopy repository;
	private GUID id;
	
	
	public Schema(PDWorkingCopy repository, GUID id) {
		this.repository = repository;
		this.id = id;
	}
	
	public Schema(PDWorkingCopy repository) {
		this.repository = repository;
		PDInstance pi = repository.newInstance(TYPE_ID);
		this.id = pi.getId();
		repository.commit();
	}
	
	public Collection<Object> getMatches() {
		return this.repository.getInstances(this, MATCH_LINK_ID);
	}
	
	public void addRelation(String name1, String name2, boolean multiple) {
		Entity a = this.getNamedEntity(name1);
		Entity b = this.getNamedEntity(name2);
		a.addRelated(b, multiple);
	}
	
	public Entity getNamedEntity(String name) {
		for (Object o: this.getEntities()) {
			Entity e = (Entity)o;
			if (e.getName().equals(name)) {
				return e;
			}
		}
		throw new NoSuchElementException();
	}
	
	public void addMatch(Match m) {
		this.repository.addLink(this.id, MATCH_LINK_ID, m);
	}
	
	public void removeMatch(Match m) {
		this.repository.removeLink(this.id, MATCH_LINK_ID, m);
	}
	
	
	public Collection<Object> getEntities() {
		return this.repository.getInstances(this, ENTITY_LINK_ID);
	}
	
	public void addEntity(Entity e) {
		this.repository.addLink(this.id, ENTITY_LINK_ID, e);
		this.repository.commit();
	}
	
	public void removeEntity(Entity e) {
		this.repository.removeLink(this.id, ENTITY_LINK_ID, e);
	}
	
	public Match getMatch(ObjectSchema s) {
		for (Object o : this.getMatches()) {
			Match m = (Match) o;
			if (m.getObjectSchema() == s) {
				return m;
			}
		}
		throw new NoSuchElementException();
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
	public PDWorkingCopy getPDWorkingCopy() {
		return this.repository;
	}

	@Override
	public GUID getTypeId() {
		return TYPE_ID;
	}

	@Override
	public void removeName() throws PDStoreException {
		this.repository.removeName(this.getId());
	}

	@Override
	public void setName(String name) throws PDStoreException {
		this.repository.setName(this.getId(), name);
	}

}
