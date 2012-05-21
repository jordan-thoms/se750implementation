package pdstore;

public enum IsolationLevel {
	/**
	 * Can read uncommitted changes.
	 * 
	 * NO standard snapshot conflict check on commit.
	 */
	NONE,

	/**
	 * Can read uncommitted changes.
	 * 
	 * Standard snapshot conflict check on commit.
	 */
	READ_UNCOMMITTED,

	/**
	 * reads always the latest durable transaction. 
	 * Any concurrent changes will be invisible
	 * to the current transaction rather than blocking it.
	 * 
	 * Standard snapshot conflict check on commit.
	 */
	READ_COMMITTED_NONE_BLOCKED,

	/**
	 * TODO: currently unused. Add comment, implementation or remove.
	 */
	REPEATABLE_READS_QUEUE_SPECIFIED,

	/**
	 * Reads no changes of concurrent transactions.
	 * 
	 * Standard snapshot conflict check on commit.
	 */
	SNAPSHOT,

	/**
	 * Reads no changes of concurrent transactions.
	 * 
	 * Needs stronger optimistic locking conflict check on commit: Needs to
	 * check whether reads are repeatable, but does not care about
	 * inserts(longer getChanges results)
	 */
	REPEATABLE_READS,

	/**
	 * Reads no changes of concurrent transactions.
	 * 
	 * Needs stronger optimistic locking conflict check on commit: Needs to
	 * check whether reads are repeatable, does care about inserts, if iterators
	 * were traversed to the end.
	 */
	SERIALIZABLE;

	public static IsolationLevel convert(int code) {
		return values()[code];
	}

	public boolean isNotConflictChecking() {
		return this.equals(NONE);
	}
	
	/**  
	 * Can read uncommitted changes on the same branch.
	 * 
	 * This method captures the main difference between isolation levels
	 * with respect to the OpenTransactionStore.
	 * 
	 * @return
	 */
	public boolean isDirtyReading() {
		return this.equals(NONE) || this.equals(READ_UNCOMMITTED);
	}

	
	public boolean seesLatestDurableTransaction() {
		return this.equals(NONE)  || this.equals(READ_UNCOMMITTED) || this.equals(READ_COMMITTED_NONE_BLOCKED);
	}
}
