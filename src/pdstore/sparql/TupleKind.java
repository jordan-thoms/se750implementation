package pdstore.sparql;

public enum TupleKind {
	/**
	 * SPO are all constant
	 */
	CCC,

	/**
	 * the object is a variable that gets assigned in a previous tuple
	 */
	CCX_XASSIGNED,

	/**
	 * the object is an unassigned variable
	 */
	CCX_XUNASSIGNED,

	/**
	 * the subject is a variable that gets assigned in a previous tuple
	 */
	XCC_XASSIGNED,

	/**
	 * the subject is an unassigned variable
	 */
	XCC_XUNASSIGNED,

	/**
	 * the subject and object are variables that get assigned in previous tuples
	 */
	XCY_XYASSIGNED,

	/**
	 * the subject and object are variables, the subject gets assigned in a
	 * previous tuple
	 */
	XCY_XASSIGNED_YUNASSIGNED

	// ...
}
