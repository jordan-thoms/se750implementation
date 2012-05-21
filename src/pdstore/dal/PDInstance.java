package pdstore.dal;


import pdstore.GUID;
import pdstore.PDStoreException;

/**
 * The interface implemented by all Java objects that represent 
 * PDInstances.
 * Types that inherit this interface are generally not implemented
 * by framework users, but only by developers of new PDCaches.
 * 
 * More explanations of this interface are given in PDWorkingCopy.
 *
 * 
 * @author Gerald
 *
 */
public interface PDInstance {
	
	public PDWorkingCopy getPDWorkingCopy();
	public GUID getTypeId();
	public GUID getId();

	public String getName() throws PDStoreException;
	public void setName(String name) throws PDStoreException;
	public void removeName() throws PDStoreException;
	
	//public Set<Object> getInstances(GUID roleId) throws PDStoreException;
	//public void addInstance(GUID roleId, Object o) throws PDStoreException;
	//public void removeInstance(GUID roleId, Object o) throws PDStoreException;
}
