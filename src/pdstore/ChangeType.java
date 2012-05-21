/**
 * 
 */
package pdstore;

public enum ChangeType {
	LINK_ADDED, LINK_REMOVED, 

	/**
	 * Used only in Changes that are Query templates. Means that only effective
	 * changes are returned.
	 * 
	 */
	LINK_EFFECTIVE,
	/**
	 * Used only in Changes that are Query templates. Means that transactions
	 * have to match exactly.
	 * 
	 */
	LINK_NOW_ADDED,
	/**
	 * Used only in Changes that are Query templates. Means that transactions
	 * have to match exactly.
	 * 
	 */
	LINK_NOW_DELETED;

	public static String toPrettyString(ChangeType changeType) {
		if (changeType == ChangeType.LINK_ADDED)
			return "+";
		else if (changeType == ChangeType.LINK_REMOVED)
			return "-";
		else if (changeType == ChangeType.LINK_EFFECTIVE)
			return "EFFECTIVE";
		else if (changeType == null)
			return "null";
		else
			return changeType.toString();
	}
}