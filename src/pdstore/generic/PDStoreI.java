package pdstore.generic;

import java.util.Collection;
import java.util.List;

import pdstore.*;

/**
 * GenericPDStore is the interface that introduces three aspects of higher
 * sophistication, if compared to PDCore: - updates can be performed one-by-one
 * using a typical transactional interface with begin/commit. - additionally the
 * PDStore innovations of branch management (branch and merge) are available. -
 * the updates can be performed based on individual parameters, instead of using
 * change objects. - basic queries can be performed in a form that lends itself
 * to index-based implementations.
 * 
 * @author gweb017
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public interface PDStoreI<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends PDCoreI<TransactionType, InstanceType, RoleType> {

	/**
	 * Begins a new transaction. All the parameters are given in the Transaction
	 * object. The transaction gets a temporary ID that is representing the
	 * begin timestamp.
	 * 
	 * @param transaction
	 *            object containing all transaction parameters
	 * @return the begin-ID of the new transaction
	 * @throws PDStoreException
	 */
	public abstract TransactionType begin(
			Transaction<TransactionType, InstanceType, RoleType> transaction)
			throws PDStoreException;

	// TODO consider using class Transaction for all methods such as addLink,
	// commit to identify transaction instead of GUID (will also simplify
	// transaction management)

	/**
	 * Begins a new transaction using default parameters for branch, isolation
	 * level etc.
	 * 
	 * @return the begin-ID of the new transaction
	 * @throws PDStoreException
	 */
	public abstract TransactionType begin() throws PDStoreException;

	/**
	 * Commits the current transaction and starts a new transaction. Until
	 * commit() is called no other transactions can see the changes of the
	 * current transaction. If commit() is not called at all, the changes are
	 * lost. Only at commit the durable transaction ID can be returned, since
	 * this transaction ID contains a timestamp. The order of the durable
	 * transaction ID is consistent with the serialization history, that is a
	 * transaction only sees changes of earlier transactions, and later
	 * transactions see the changes of this transaction.
	 * 
	 * PDCore does not allow empty transactions to be committed, the transaction
	 * will fail.
	 * 
	 * @return the durable transaction ID, its timestamp is consistent with the
	 *         serialization history. null if the transaction failed to commit.
	 * @throws PDStoreException
	 */
	public abstract TransactionType commit(TransactionType transaction)
			throws PDStoreException;

	/**
	 * Rolls the transaction with the given ID back.
	 * 
	 * @param transaction
	 *            ID of transaction to roll back
	 * @throws PDStoreException
	 */
	public void rollback(TransactionType transaction) throws PDStoreException;

	/**
	 * gets instances connected to an instance through a one-to-many
	 * relationship
	 * 
	 * @param transaction
	 *            - current transaction id
	 * @param instance1
	 *            - the instance of which to get connected instances
	 * @param roleId2
	 *            - role of the instances to get
	 * @return a set of objects corresponding to instances connected to the
	 *         given instance through the given role
	 */
	Collection<InstanceType> getInstances(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException;

	/**
	 * gets the instance connected to this instance through a one-to-one
	 * relationship
	 * 
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId1
	 *            - the instance of which to get connected instance
	 * @param role2
	 *            - role of the instances to get
	 * @param instanceId1
	 * @param role2
	 * @return
	 */
	InstanceType getInstance(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException;
	
	

	public abstract void addChange(
			PDChange<TransactionType, InstanceType, RoleType> change)
		throws PDStoreException;


	/**
	 * Link an instance to another through a role
	 * 
	 * @param transaction
	 *            - current transaction id
	 * @param instance1
	 *            - accessor instance
	 * @param roleId2
	 *            - role to link with
	 * @param instance2
	 *            - object to be linked to
	 */
	void addLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException;

	/**
	 * remove a link between two instances @see PDStore.addLink
	 * 
	 * @param transaction
	 *            - current transaction id
	 * @param instance1
	 * @param roleId2
	 * @param instance2
	 */
	void removeLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException;

	/**
	 * Gets all the instances that appear in this role, e.g. have been instance2
	 * in an addLink(instance1, role, instance2) in the database.
	 * 
	 * @param transaction
	 *            transaction id on which to search
	 * @param typeid
	 *            ID of the type
	 * @return all stored instances of that type
	 * @throws PDStoreException
	 */
	Collection<InstanceType> getAllInstancesInRole(TransactionType transaction,
			RoleType role) throws PDStoreException;

	/**
	 * Returns true if the given instance exists in the database
	 * 
	 * @param transaction
	 *            - current transaction id
	 * @param instance
	 * @return
	 * @throws PDStoreException
	 */
	boolean instanceExists(TransactionType transaction, InstanceType instance)
			throws PDStoreException;

	/**
	 * TODO: add transactionID to method it should have a transactionID, as it
	 * needs to also return the uncommitted changes of the current transaction
	 * 
	 * @param instance1
	 * @param role2
	 * @return the result can be null
	 * @throws PDStoreException
	 */
	Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			InstanceType instance1, RoleType role2) throws PDStoreException;

	/**
	 * Returns all changes since a certain transaction.
	 * 
	 * @param since
	 *            transaction from which onwards the changes are returned
	 * @param instance1
	 * @param role2
	 * @return
	 * @throws PDStoreException
	 */
	List<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			TransactionType since, InstanceType instance1, RoleType role2)
			throws PDStoreException;

	/**
	 * @param transaction
	 * @param instance1
	 * @param role2
	 * @return the result can be null
	 * @throws PDStoreException
	 */
	Collection<InstanceType> getInstancesFromThisBranch(
			TransactionType transaction, InstanceType instance1, RoleType role2)
			throws PDStoreException;

	/**
	 * Queries for changes in the database that match the given change template.
	 * 
	 * @param change
	 *            a PDChange acting as template for matching the triples returned by this method
	 * @return a collection of the matching triples in the database
	 * @throws PDStoreException
	 */
	Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			PDChange<TransactionType, InstanceType, RoleType> change)
			throws PDStoreException;
}
