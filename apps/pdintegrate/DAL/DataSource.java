package pdintegrate.DAL;

import java.util.NoSuchElementException;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.dal.DALClassRegister;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

/**
 * A DataSource is a representation of an organisation which provides DataUpdates to this PDIntegrator Instance.
 * 
 * DataSources have multiple DataUpdates.
 * 
 * @author Danver Braganza
 *
 */
public class DataSource implements PDInstance {
	
	public static final GUID TYPE_ID = new GUID("126e2e90bed011dfaaa4005056c00001");
	public static final GUID HAS_SCHEMA = new GUID("126e2e99bed011dfaaa4005056c00001");
	static {
		DALClassRegister.addDataClass(TYPE_ID, DataSource.class);
	}
	
	private PDWorkingCopy pdWorkingCopy;
	
	private GUID id;
	
	/**
	 * Convenience method for looking up the ID of a given DataSource by its name.  
	 * The name of a DataSource is a human-readable, meaningful string which
	 * identifies it.  The ID is the GUID of the corresponding entity in the PDStore. 
	 * 
	 * @param name a human-readable, meaningful string which
	 * identifies it
	 * @param p A PDStore instance
	 * @return the GUID of the corresponding entity in the PDStore.
	 * @throws NoSuchElementException when the given source name cannot be found
	 */
	static GUID lookupID(String name, PDStore p) throws NoSuchElementException {
		GUID transaction = p.begin();
		return p.getId(transaction, name);
	}

	/**
	 * Creates a new DataSource object
	 * @param name
	 * @param p
	 */
	public DataSource(PDWorkingCopy cache, String name, GUID sourceGUID) {
		this.pdWorkingCopy = cache;
		this.setName(name);
		this.id = sourceGUID;
	}
	
	public static DataSource load(PDWorkingCopy cache, GUID instanceID) {
		return (DataSource)cache.load(instanceID);
	}
	
	public String getName() {
		return pdWorkingCopy.getName(id);
	}
	
	public void setName(String s) {
		pdWorkingCopy.setName(id, s);
	}
	
	public void addSchema(Schema s) {
		this.pdWorkingCopy.addLink(this.id, HAS_SCHEMA, s);
	}
	
	public void removeSchema(Schema s) {
		this.pdWorkingCopy.removeLink(id, HAS_SCHEMA, s);
	}
	
	public Schema getSchema(Schema s) {
		return (Schema)this.pdWorkingCopy.getInstance(this, HAS_SCHEMA);
	}
	
	@Override
	public GUID getId() {
		return this.id;
	}

	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.pdWorkingCopy;
	}

	@Override
	public GUID getTypeId() {
		return TYPE_ID;
	}

	@Override
	public void removeName() throws PDStoreException {
		pdWorkingCopy.removeName(id);
	}

}
