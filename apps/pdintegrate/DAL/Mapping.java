package pdintegrate.DAL;

import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

public class Mapping implements PDInstance {
	
	public static final GUID TYPE_ID = new GUID("126e2e94bed011dfaaa4005056c00001");

	public static final GUID OPERATION_LINK_ID = new GUID("126e55a5bed011dfaaa4005056c00001");
	public static final GUID OBJECT_ENTITY_LINK_ID = new GUID("12707880bed011dfaaa4005056c00001");
	public static final GUID SOURCE_ENTITY_LINK_ID = new GUID("12707881bed011dfaaa4005056c00001");
	
	
	
	static {
		DALClassRegister.addDataClass(TYPE_ID, Mapping.class);
	}
	
	private PDWorkingCopy pw;
	private GUID id;
	
	public Mapping(PDWorkingCopy cache, GUID sourceGUID) {
		this.pw = cache;
		this.id = sourceGUID;
	}
	
	Mapping(PDWorkingCopy cache, String operation, String sourceEntity, String objectEntity) {
		this.pw = cache;
		PDInstance pi = pw.newInstance(TYPE_ID);
		this.id = pi.getId();
		this.setOperation(operation);
		this.setObjectEntity(objectEntity);
		this.setSourceEntity(sourceEntity);
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
	
	public String getOperation() {
		return (String)this.pw.getInstance(this, OPERATION_LINK_ID);
	}
	
	public void setOperation(String e) {
		this.pw.addLink(this.id, OPERATION_LINK_ID, e);
		this.pw.commit();
	}
	
	
	public String getObjectEntity() {
		return (String)this.pw.getInstance(this, SOURCE_ENTITY_LINK_ID);
	}
	
	public void setObjectEntity(String e) {
		this.pw.addLink(this.id, OBJECT_ENTITY_LINK_ID, e);
		this.pw.commit();
	}
	
	
	public String getSourceEntity() {
		return (String)this.pw.getInstance(this, SOURCE_ENTITY_LINK_ID);
	}
	
	public void setSourceEntity(String e) {
		this.pw.addLink(this.id, SOURCE_ENTITY_LINK_ID, e);
		this.pw.commit();
	}

	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.pw;
	}

	@Override
	public GUID getTypeId() {
		return TYPE_ID;
	}

	@Override
	public void removeName() throws PDStoreException {
		this.pw.removeName(id);
		
	}

	@Override
	public void setName(String name) throws PDStoreException {
		this.pw.setName(id, name);
		
	}

	public ObjectSchema getObjectSchema() {
		// TODO Auto-generated method stub
		return null;
	}

}
