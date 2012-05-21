package pdstore.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import java.util.List;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.ReverseListIterator;

import pdstore.*;
import pdstore.changeindex.*;
import pdstore.changelog.*;
import pdstore.generic.*;

import pdstore.notify.PDListener;

/**
 * 
 * Analysis: ConcurrentStore has the following two properties - implements
 * GenericStore, albeit fixing the type parameters - offers concurrent access,
 * i.e. full transactional concurrency, hence the name.
 * 
 * ConcurrentStore is with respect to algorithms the top layer of the PDStore
 * hierarchy. The only thing still on top is the convenience class PDStore,
 * which most clients will instantiate. However, most applications use
 * essentially this ConcurrentStore.
 * 
 * Main purpose is to deal with open transactions. The open transactions are
 * lists of GenericChanges and are stored in two ways: - as a list per
 * transactions, in the field idToTransaction. - as a proper double index, in an
 * object of type GenericOpenTransactionStore
 * 
 * The most important isolation level is snapshot-first-committer-wins, see also
 * comments in commit().
 * 
 */
public class ConcurrentStore extends GenericConcurrentStore<GUID, Object, GUID> {

	// static {
	// Debug.err.debugThisClass();
	// }

	private GUID repositoryId;
	// private GUID masterBranchId;

	protected PDCoreI<GUID, Object, GUID> changeLogStore;

	/**
	 * @param coreStore
	 *            must not be null, is usually reference to the ChangeLogStore
	 * @throws PDStoreException
	 */
	public ConcurrentStore(PDCoreI<GUID, Object, GUID> coreStore)
			throws PDStoreException {

		changeLogStore = coreStore;
		repositoryId = changeLogStore.getRepository();

		// alternative without index:
		// queryStore = new LogOnlyStore<GUID, Object, GUID>(changeLogStore);

		// The following assignment is a workaround for a hen/egg problem.
		// the field querystore has to be non-null before the actual object
		// is created. This is because of the USES_ROLE logic, which wants to
		// look in the current index.
		// Therefore we assign an object - this is
		// for convenience reasons the open transaction store.
		// No updates should be performed on it, it serves as an empty store.
		committedTransactionStore = openTransactionStore;

		committedTransactionStore = new LogAndIndexStore<GUID, Object, GUID>(
				new GenericIndexStore<GUID, Object, GUID>(changeLogStore, this),
				changeLogStore);
	}

	protected void finalize() throws Throwable {
		synchronized (this.idToTransaction) {
			this.idToTransaction.clear();
		}
		super.finalize();
	}

	@Override
	public GUID begin() throws PDStoreException {
		throw new UnsupportedOperationException("deprecated, now in PDStore");
	}

	@Override
	public GUID begin(Transaction<GUID, Object, GUID> transaction)
			throws PDStoreException {
		synchronized (idToTransaction) {
			idToTransaction.put(transaction.getId(), transaction);
		}

		// TODO: consider sending down transaction to IndexStore
		//openTransactionStore.begin(transaction);

		// add a link that registers the isolation level
		int ordinal = transaction.getIsolationLevel().ordinal();
		addLink(transaction.getId(), transaction.getId(),
				PDStore.ISOLATIONLEVEL_ROLEID, ordinal);

		return transaction.getId();
	}

	public void addLink(GUID transactionId, Object instance1, GUID roleId2,
			Object instance2) throws PDStoreException {
		PDChange<GUID, Object, GUID> change = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionId, instance1, roleId2,
				instance2);

		addChange(change);
	}

	public GUID getRepository() {
		return this.repositoryId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pdstore.generic.PDCore#commit(java.lang.Comparable)
	 * 
	 * The commit implements snapshot isolation with the "first committer wins"
	 * strategy. In the PDStore definition of snapshot isolation, only deletes
	 * produce conflicts. If a transaction attempts to commit, and it removes a
	 * link, and a transaction has removed that link and committed, then the
	 * transaction aborts.
	 */
	public GUID commit(GUID transactionId) throws PDStoreException {
		Debug.println("commit(" + transactionId + ")", "commit");

		// Single-threaded, so block all other commit operations until done
		Transaction<GUID, Object, GUID> transaction = getTransaction(transactionId);
		if (transaction == null)
			throwTransactionException(transactionId);

		// if there are no writes, return the old transaction Id
		if (transaction.isEmpty())
			return transactionId;
		GUID durableTransaction = null;

		ArrayList<PDChange<GUID, Object, GUID>> conflicts = new ArrayList<PDChange<GUID, Object, GUID>>();

		// TODO revise locking; the locks here may not be necessary
		synchronized (committedTransactionStore) {

			ChangeLogStore.callListeners(interceptorList, this, transaction);
			// checking for conflicts
			if (getLatestDurableTransaction() != null &&
					!transaction.getIsolationLevel().isNotConflictChecking()) {

				/*
				 * Go through the aggregated changes in the transaction in
				 * reverse time order, using a ReverseListIterator and a
				 * AggregationIterator.
				 * 
				 * Local removes are fine and don't create conflicts, therefore
				 * only the aggregated changes are checked for conflicts here.
				 */
				Iterator<PDChange<GUID, Object, GUID>> reverseIterator = new ReverseListIterator<PDChange<GUID, Object, GUID>>(
						transaction);
				Iterator<PDChange<GUID, Object, GUID>> aggregationIterator = new AggregationIterator<GUID, Object, GUID>(
						reverseIterator);
				while (aggregationIterator.hasNext()) {
					PDChange<GUID, Object, GUID> c = aggregationIterator.next();

					Debug.println("  Checking change for conflict: " + c,
							"commit");

					// look only for remove-remove conflicts
					if (c.getChangeType() == ChangeType.LINK_REMOVED
							&& linkEffectivelyRemovedSince(c, transactionId)) {
						Debug.println("  Conflict found!", "commit");
						conflicts.add(c);
					}
				}
			}

			deleteTransaction(transaction);

			if (conflicts.isEmpty()) {
				Debug.println("  Commit succeeded, adding changes...", "commit");
				// Only if there are write operations, the lower layers are
				// called.
				durableTransaction = committedTransactionStore.addTransaction(transaction);
				setLatestDurableTransaction(durableTransaction);
				new DetachedListenerDispatcher<GUID, Object, GUID>(getDetachedListenerList(), 
						this, transaction);
				
			} else {
				Debug.println("  Commit failed", "commit");
				durableTransaction = null;
			}
		}
		synchronized (idToTransaction) {
			idToTransaction.remove(transactionId);
		}
		
		return durableTransaction;
	}

	/**
	 * Checks if the given link has been effectively removed by a committed
	 * transaction since the given sinceTransaction. Effectively removed means
	 * that the link was not inserted again afterwards (possibly by a different
	 * committed transaction).
	 * 
	 * @param link
	 *            the link of interest
	 * @param sinceTransaction
	 *            the ID of the transaction after which effective removes should
	 *            be sought for
	 * @return true iff a transaction that committed after sinceTransaction
	 *         effectively removed the link
	 */
	private boolean linkEffectivelyRemovedSince(
			PDChange<GUID, Object, GUID> link, GUID sinceTransaction) {
		// look for the last committed change on the given link
		PDChange<GUID, Object, GUID> template = new PDChange<GUID, Object, GUID>(
				link);
		template.setTransaction(getLatestDurableTransaction());
		template.setChangeType(ChangeType.LINK_EFFECTIVE);
		Iterator<PDChange<GUID, Object, GUID>> lastCommitedChangeIterator = getChanges(
				template).iterator();

		// We are now testing for conditions that mean the link was NOT
		// effectively removed before sicneTransaction:

		// 1. there has never been a change for this link
		if (!lastCommitedChangeIterator.hasNext())
			return false;

		Debug.println("  Effective change found for link.", "commit");

		// 2. the last committed change is not a remove
		PDChange<GUID, Object, GUID> lastCommitedChange = lastCommitedChangeIterator
				.next();
		if (lastCommitedChange.getChangeType() != ChangeType.LINK_REMOVED)
			return false;

		Debug.println("  Effective remove found for link.", "commit");

		// 3. the last committed change was before the sinceTransaction
		if (lastCommitedChange.getTransaction().compareTo(sinceTransaction) < 0)
			return false;

		Debug.println(
				"  Effective remove after sinceTransaction found for link.",
				"commit");

		return true;
	}

	public void deleteTransaction(Transaction<GUID, Object, GUID> transaction) {
		// delete all changes of the ending transaction from the open
		// Transaction store
		// independent of commit or abort
		for (PDChange<GUID, Object, GUID> c : transaction) {
			openTransactionStore.delete(c);
		}
	}

	public void removeLink(GUID transactionId, Object instance1, GUID roleId2,
			Object instance2) throws PDStoreException {
		PDChange<GUID, Object, GUID> change = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionId, instance1, roleId2,
				instance2);
		addChange(change);
	}

	public Object getInstance(GUID transactionId, Object instance1, GUID roleId2) {
		Collection<Object> result = getInstances(transactionId, instance1,
				roleId2);
		if (result.isEmpty()) {
			return null;

		}
		Object[] orderedResults = result.toArray();
		return orderedResults[orderedResults.length - 1];
	}

	public Collection<Object> getInstances(GUID transactionId,
			Object instance1, GUID roleId2) {

		// TODO rethink this, it is currently not done in getChanges
		// special case: querying type of primitive instance
		if (roleId2 == PDStore.HAS_TYPE_ROLEID && !(instance1 instanceof GUID)) {
			// Collection<Object> result = new HashSet<Object>();
			Collection<Object> result = new LinkedHashSet<Object>();
			result.add(PrimitiveType.typeIdOf(instance1));
			return result;
		}

		Transaction<GUID, Object, GUID> t = idToTransaction.get(transactionId);

		// get instances from committed changes
		GUID usedDurableTransaction = transactionId;
		if (t != null
				&& t.getIsolationLevel().seesLatestDurableTransaction()) {
			// This is wrong, needs to be limited to branch, but this code will soon be deprecated anyway.
			usedDurableTransaction = getLatestDurableTransaction();
		}
		
		
		Collection<PDChange<GUID, Object, GUID>> changes = committedTransactionStore.getChanges((new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_EFFECTIVE, usedDurableTransaction, instance1,
				roleId2, null)));
		
		Collection<Object> result = new ArrayList<Object>();

		Iterator<PDChange<GUID, Object, GUID>> changesIterator = changes
				.iterator();
		while (changesIterator.hasNext()) {

			PDChange<GUID, Object, GUID> next = changesIterator.next();
			if(next.getChangeType().equals(ChangeType.LINK_ADDED))
		        // add only the LINK_ADDED, not the LINK_REMOVED
				result.add(next.getInstance2());
		}
		
		// t is null if transactionId is a historic transaction
		// for historic transactions, no changes of currently open
		// transactions are added.
		if (t == null)
			return result;

		// add changes from uncommitted transactions, depending on the isolation
		// level:
		if (t.getIsolationLevel() == IsolationLevel.SNAPSHOT
				|| t.getIsolationLevel() == IsolationLevel.READ_COMMITTED_NONE_BLOCKED) {
			// add only the uncommitted changes of current transaction
			applyOpenTransaction(transactionId, instance1, roleId2, result);
		} else if (t.getIsolationLevel().isDirtyReading()) {
			// add the uncommitted changes of all open transactions
			// This is wrong, needs to be limited to branch, but this code will soon be deprecated anyway.
			Set<GUID> keys = idToTransaction.keySet();
			for (GUID concurrentTransactionId : keys) {
				applyOpenTransaction(concurrentTransactionId, instance1,
						roleId2, result);
			}
		}

		return result;
	}

	public void applyOpenTransaction(GUID transactionId, Object instance1,
			GUID roleId2, Collection<Object> result) {

		GUID guid6 = new GUID("111decaf123456111111111111111111");

		IndexEntry<GUID, Object, GUID> changes = openTransactionStore
				.getMapEntry(roleId2, instance1);
		if (changes != null) {
			synchronized (changes) {
				for (PDChange<GUID, Object, GUID> c : changes.getChangeList()) {
					// the crucial line
					// making sure that open transactions are isolated:
					if (!c.getTransaction().equals(transactionId))
						continue;

					if (c.getInstance2().equals(guid6))
						Debug.println(" found GUID6 ");

					PDChange<GUID, Object, GUID> normalChange = c
							.getNormalizedChange();
					if (roleId2.isFirst())
						normalChange.attemptChange(instance1, roleId2, result);
					else
						normalChange.getPartnerChange().attemptChange(
								instance1, roleId2, result);
				}
			}
		}
	}

	public boolean instanceExists(GUID transactionId, GUID typeId,
			Object instance) throws PDStoreException {
		return instanceExists(transactionId, instance);
	}

	public boolean instanceExists(GUID transactionId, Object instance)
			throws PDStoreException {

		// if the instance was found in the file, return true
		if (instanceExistsInFile(transactionId, instance))
			return true;

		final Transaction<GUID, Object, GUID> transaction = this
				.getTransaction(transactionId);
		// if the transaction is not open, return false
		if (transaction == null)
			return false;
		// if the transaction is open, then we search its changes
		synchronized (transaction) {
			return instanceExistsInTransaction(transaction, instance);
		}
	}

	private boolean instanceExistsInFile(GUID transactionId, Object instance) {
		return committedTransactionStore.instanceExists(transactionId, instance);
	}

	private boolean instanceExistsInTransaction(
			Transaction<GUID, Object, GUID> transaction, Object instance) {
		for (PDChange<GUID, Object, GUID> c : transaction) {
			if (c.getInstance1().equals(instance)
					|| c.getInstance2().equals(instance)) {
				return true;
			}
		}
		return false;
	}

	private void getAllInstancesFromTransaction(
			Transaction<GUID, Object, GUID> transaction,
			Collection<Object> roles2, Set<Object> result) {
		for (PDChange<GUID, Object, GUID> c : transaction) {
			if (roles2.contains(c.getRole2())) {
				if (c.getChangeType().equals(ChangeType.LINK_ADDED)) {
					result.add(c.getInstance1());
				}
			}

			// check link in other direction
			if (roles2.contains(c.getRole2().getPartner())) {
				if (c.getChangeType().equals(ChangeType.LINK_ADDED)) {
					result.add(c.getInstance2());
				}
			}
		}
	}

	public final void rollback(GUID transactionId) throws PDStoreException {
		Debug.assertTrue(idToTransaction.containsKey(transactionId),
				"transactionId does not refer to an open transaction.");
		Transaction<GUID, Object, GUID> transaction = this
				.getTransaction(transactionId);
		synchronized (idToTransaction) {
			idToTransaction.remove(transactionId);
		}
		deleteTransaction(transaction);
	}

	/***
	 * Utility Methods
	 */

	private void throwTransactionException(GUID transactionId)
			throws PDStoreException {
		throw new PDStoreException("The transaction ID '" + transactionId
				+ "' does not exist.");
	}

	public final Transaction<GUID, Object, GUID> getTransaction(
			GUID transactionId) throws PDStoreException {
		final Transaction<GUID, Object, GUID> transaction;
		synchronized (idToTransaction) {
			transaction = idToTransaction.get(transactionId);
		}

		return transaction;
	}

	public Collection<Object> getAllInstancesInRole(GUID transaction, GUID role)
			throws PDStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addChange(PDChange<GUID, Object, GUID> change)
			throws PDStoreException {
		GUID transactionId = change.getTransaction();
		final Transaction<GUID, Object, GUID> transaction = this
				.getTransaction(transactionId);

		if (transaction == null)
			throwTransactionException(transactionId);
		synchronized (transaction) {
			transaction.add(change);
		}
		openTransactionStore.addChange(change);
	}

	public Iterator<PDChange<GUID, Object, GUID>> iterator() {
		return changeLogStore.iterator();
	}

	public List<PDListener<GUID, Object, GUID>> getDetachedListenerList()
			throws PDStoreException {
		return changeLogStore.getDetachedListenerList();
	}

	private List<PDListener<GUID, Object, GUID>> interceptorList = new ArrayList<PDListener<GUID, Object, GUID>>();

	public List<PDListener<GUID, Object, GUID>> getInterceptorList()
			throws PDStoreException {
		return interceptorList;
	}

	@Override
	public Collection<PDChange<GUID, Object, GUID>> getChanges(
			Object instance1, GUID role2) throws PDStoreException {
		// TODO Bug?: must also return open transactions?
		Collection<PDChange<GUID, Object, GUID>> c = committedTransactionStore.getChanges(
				instance1, role2);
		return c;
	}

	@Override
	public Collection<Object> getInstancesFromThisBranch(GUID transaction,
			Object instance1, GUID role2) throws PDStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PDChange<GUID, Object, GUID>> getChanges(GUID since,
			Object instance1, GUID role2) throws PDStoreException {
		// TODO Unused, delete?? Bug: must also return open transactions
		return committedTransactionStore.getChanges(since, instance1, role2);
	}

	@Override
	public GUID addTransaction(Transaction<GUID, Object, GUID> transaction)
			throws PDStoreException {
		return committedTransactionStore.addTransaction(transaction);
	}
}