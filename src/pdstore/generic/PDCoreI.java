package pdstore.generic;

import java.util.List;

import pdstore.*;
import pdstore.notify.PDListener;

/**
 * Generic PDCore is the most fundamental and frugal interface representing a
 * database based on the PDStore paradigm.
 * 
 * The PDStore paradigm is a triplestore paradigm that introduces the following
 * important additional features: The elements of the triple can be typed.
 * Subjects and Objects have the same type. Roles (predicates) can have a
 * distinct type from instances (Subjects and Objects), and must implement a
 * specific interface Pairable. PDStore follows an approach of symmetrization of
 * triples, i.e. subjects and objects are interchangeable in the following
 * sense: Each role must have a partner role, and this must be a symmetric
 * relation. If r and p are mutual partner roles, then inserting a triple (a r
 * b) has the same effect as inserting a triple (b p a). we call these
 * commutative triples in the following links.
 * 
 * The store is historic and stores insertions and deletions of triples, i.e.
 * the fundamental unit of information stored is rather a change than a triple.
 * 
 * The store is transactional; insertion and deletion have to be performed in
 * transactions. The transaction ID serves at the same time as information about
 * time and branch, meaning the store is temporal, and it can store arbitrarily
 * many branches. Branches can be forked off at any point in time and can be
 * merged.
 * 
 * The store is always active, i.e. changes can be susbscribed to. This way
 * clients can react to changes.
 * 
 * 
 * @author Gerald
 * @author Christof
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public interface PDCoreI<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends Iterable<PDChange<TransactionType, InstanceType, RoleType>> {

	/**
	 * Gets the ID of the repository.
	 * 
	 * @return ID of the repository
	 * @throws PDStoreException
	 */
	public TransactionType getRepository() throws PDStoreException;

	/**
	 * An atomic transaction is performed on the core, applying the given
	 * changes.
	 * 
	 * @return the durable transaction ID, its timestamp is consistent with the
	 *         serialization history. null if the transaction failed to commit.
	 * @throws PDStoreException
	 */
	public abstract TransactionType addTransaction(
			Transaction<TransactionType, InstanceType, RoleType> transaction)
			throws PDStoreException;

	/**
	 * The function used to manage co-transactional on-commit intrusive
	 * subscribers.
	 * 
	 * PDCore follows a simplified listener and interceptor architecture. In
	 * order not to duplicate all the access methods for the listener
	 * collection, this method simply returns the interceptor list to the caller
	 * that wants to modify the interceptor list. Interceptors can be added and
	 * removed with the java list operations.
	 * 
	 * Interceptors are more heavyweight than Detached Listeners and should be
	 * used with care, but they are also in other ways powerful. They are not
	 * strictly better, since there are functions that would be a logical error
	 * if done with interceptors, such as sending external emails.
	 * 
	 * Interceptors are co-transactional, meaning they can make changes in the
	 * same transaction as the triggering changes. Co-transactional listeners
	 * are sensitive code in any case, since they can prevent the principal
	 * agent of change from executing its agenda. The principal agent of change
	 * would be the code that does the initial change. Since the point of a
	 * listener architecture is the separation of concerns, this code should be
	 * oblivious of the listeners. The listeners in the same transaction
	 * (non-detached listeners) can endanger the stability of the principal
	 * agent by taking too long, hence leading to an abort, or by willfully
	 * enforcing an abort.
	 * 
	 * Interceptors are on-commit, since they are only called after all other
	 * operations have finished in the transaction.
	 * 
	 * Interceptors are intrusive subscribers, since they can completely modify
	 * the effect of the transaction.
	 * 
	 * Interceptors are called in a strict sequential manner; every interceptor
	 * gets the list of changes modified by the previous interceptor. In
	 * distributed PDStores, however, there is no guarantee with regard to the
	 * order of interceptors at different remote sites. Moreover, since remote
	 * interceptors are much more problematic for system stability,
	 * implementations of PDStore are not required to support remote
	 * interceptors, while they are strictly required to support detached
	 * listeners.
	 * 
	 * @param listener
	 * @throws PDStoreException
	 */
	public abstract List<PDListener<TransactionType, InstanceType, RoleType>> getInterceptorList()
			throws PDStoreException;

	/**
	 * PDCore follows a simplified listener architecture. It has only a single
	 * listener type for updates. Making use of the semantics of transactions,
	 * this listener is only invoked on commit. Furthermore, in order not to
	 * duplicate all the access methods for the listener collection, the
	 * listener methods simply pass through the listener collection.
	 * 
	 * 
	 * Detached listeners are nonintrusive subscribers to events. Detached
	 * listeners are listeners that are triggered by changes in a transaction,
	 * but the listeners run in a separate transaction that is started after the
	 * first one finished. The term detached is a technical term that is used in
	 * the area of active databases for one of the execution modes for triggers.
	 * Detached listeners are particularly important for at least two reasons.
	 * First they are the correct place to generate external effects such as
	 * sending notification emails, since they know that the changes they are
	 * listening to are durable. Secondly detached listeners do not lengthen the
	 * transaction execution and are therefore safer, and can be distributed
	 * without concerns. Listeners in the same transaction, in contrast, can be
	 * a concern since they can prevent the principal agent of change from
	 * executing its agenda. Such listeners are subsumed under Interceptors, and
	 * are discussed in the getInterceptorList function.
	 * 
	 * @param listener
	 * @throws PDStoreException
	 */
	public abstract List<PDListener<TransactionType, InstanceType, RoleType>> getDetachedListenerList()
			throws PDStoreException;
}