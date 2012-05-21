package pdstore.generic;

/**
 * 
 * Interface for Identifiers that can give a Partner ID, such as the new GUIDs.
 * 
 * @author Gerald
 *
 * @param <T>
 */
public interface Pairable<T> {
	
	T getPartner();
	
	
	/**
	 * Returns if this is the representative of the two partners.
	 * 
	 * @return
	 */
	boolean isFirst();
	
	/**
	 * Returns one of the partners as representative
	 * 
	 * @return the representative
	 */	
	T getFirst();
}
