package pdintegrate.DAL;

import java.util.Collection;
import java.util.NoSuchElementException;

import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;


/**
 * Contains a set of mappings between PDTypes and ObjectElements 
 * @author dbra072
 *
 */
public class Match implements PDInstance  {
	
	public static final GUID TYPE_ID = new GUID("126e2e93bed011dfaaa4005056c00001");
	
	public static final GUID MAPPING_LINK_ID = new GUID("126e55a4bed011dfaaa4005056c00001");
	
	
	static {
		DALClassRegister.addDataClass(TYPE_ID, Match.class);
	}
	
	private PDWorkingCopy pw;
	
	private GUID id;
	
	public Match(PDWorkingCopy pw, GUID id) {
		this.id = id;
		this.pw = pw;
	}
	

	public Match(PDWorkingCopy pw, ObjectSchema os, Schema s) {
		this.pw = pw;
		PDInstance pi = pw.newInstance(TYPE_ID);
		this.id = pi.getId();
		os.addMatch(this);
		s.addMatch(this);
		pw.commit();
	}


	@Override
	public GUID getId() {
		return this.id;
	}

	@Override
	public String getName() throws PDStoreException {
		return this.pw.getName(this.id);
	}
	
	public Collection<Object> getMappings() {
		return this.pw.getInstances(this, Match.MAPPING_LINK_ID);
	}

	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.pw;
	}

	@Override
	public GUID getTypeId() {
		return TYPE_ID;
	}
	
	public ObjectSchema getObjectSchema() {
		for (Object o: this.getMappings()) {
			Mapping m = (Mapping) o;
			return m.getObjectSchema();
		}
		throw new NoSuchElementException();
	}

	@Override
	public void removeName() throws PDStoreException {
		this.pw.removeName(id);
		
	}

	@Override
	public void setName(String name) throws PDStoreException {
		this.pw.setName(id, name);
	}


	public void addMapping(String operation, String sourceEntity, String objectEntity) {
		Mapping m = new Mapping(pw, operation, sourceEntity, objectEntity);
		this.pw.addLink(this.getId(), MAPPING_LINK_ID, m);
	}
}
