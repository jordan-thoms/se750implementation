package pdintegrate.model;

import java.util.Date;

import pdstore.GUID;
import pdstore.dal.PDWorkingCopy;

public abstract class DataSource {
	
	/**
	 * True if the DataSource has updates since time t.
	 * 
	 * Although discouraged, some subclasses are allowed to always return true.
	 * 
	 * @param t Time to check since.
	 * @return true if the DataSource has changed since time t.  true or false otherwise.
	 */
	public boolean updatedSince(Date t) {
		return true;
	}
	
	private pdintegrate.DAL.DataSource dal;
	
	public DataSource(PDWorkingCopy cache, String name, GUID SourceID) {
		this.dal = new pdintegrate.DAL.DataSource(cache, name, SourceID);
	}
	
	public abstract DataUpdate getUpdate(Date t);
	
	
}
