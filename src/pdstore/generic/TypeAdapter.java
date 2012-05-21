package pdstore.generic;

import pdstore.GUID;

public interface TypeAdapter<TransactionType, InstanceType, RoleType> {

	RoleType USES_ROLE_ROLEID();
	RoleType ISOLATIONLEVEL_ROLEID();
	

	
	InstanceType instanceFromRole(RoleType t);
	InstanceType instanceFromTransaction(TransactionType t);
	TransactionType TransactionFromInstance(InstanceType t);

	RoleType RoleFromInstance(InstanceType t);

	/**
	 * Get the branch from a Transaction.
	 * 
	 * @param branch
	 *            TODO
	 * @param t
	 * 
	 * @return
	 */
	TransactionType getBranchID(TransactionType t);
	
	

	/**
	 * Get an open transaction ID, based on a given transaction ID.
	 * 
	 * @param t
	 * @return
	 */
	TransactionType getOpenID(TransactionType t);


	/**
	 * Get a durable transaction ID, based on a given branch ID.
	 * 
	 * @param branch
	 * @param t
	 * 
	 * @return
	 */
	TransactionType getDurableID(TransactionType branch);
	
	/**
	 * Returns a transactionID that is maximally late, i.e. far in the future.
	 * @param branch  The branch on which this transaction should be, on any transaction on that branch.
	 * 					Any transaction can be used to get a maximum transaction.
	 * @return
	 */
	TransactionType maxTransactionId(TransactionType branch);
	
	TransactionType newTransactionId(TransactionType branch);
	
	TransactionType getFirst(TransactionType t);
	
	TransactionType getPartner(TransactionType t);
	
	boolean isPureBranchID(TransactionType branch);
	
	
	boolean isDurable(TransactionType branch);
}
