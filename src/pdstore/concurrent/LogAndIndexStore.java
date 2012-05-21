/**
 * 
 */
package pdstore.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.PersistenceLevel;
import pdstore.Transaction;
import pdstore.generic.PDStoreI;
import pdstore.generic.GlobalTypeAdapter;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.generic.Pairable;
import pdstore.generic.TypeAdapter;
import pdstore.notify.PDListener;

/**
 * This class is responsible for making sure firstly that updates are forwarded
 * to a persistent Log Storage and secondly that updates are made available in
 * an index.
 * 
 * This class currently uses a main memory index.
 * 
 * Relations can be explicitly excluded from being stored in the index or in the
 * log. This works by setting links to the role that should be excluded.
 * Examples can be seen in the testcases PDStoreTest.testIsInLog() and
 * testIsInIndex().
 * 
 * Excluding relations from the log is useful for data that is only important
 * for the current running of the program. Excluding relations from the index is
 * useful for logging data that can become large and is unlikely to be reviewed
 * soon. Excluding relations from both index and log means that the data is
 * transaction-only; this is useful for temporary computations.
 * 
 * 
 * @author Gerald
 * 
 */
public class LogAndIndexStore<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements PDStoreI<TransactionType, InstanceType, RoleType> {
	
	/**
	 * The adaptor object that encapsulates all operations on generic arguments.
	 */
	public final TypeAdapter<TransactionType, InstanceType, RoleType> typeAdaptor = (TypeAdapter<TransactionType, InstanceType, RoleType>) GlobalTypeAdapter.typeAdapter;


	public LogAndIndexStore(
			PDStoreI<TransactionType, InstanceType, RoleType> index,
			PDCoreI<TransactionType, InstanceType, RoleType> log) {
		this.index = index;
		this.changeLogStore = log;
	}

	private PDStoreI<TransactionType, InstanceType, RoleType> index;
	PDCoreI<TransactionType, InstanceType, RoleType> changeLogStore;

	public InstanceType getInstance(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException {
		return index.getInstance(transaction, instance1, role2);
	}

	public Collection<InstanceType> getInstances(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException {
		return index.getInstances(transaction, instance1, role2);
	}

	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			InstanceType instance1, RoleType role2) throws PDStoreException {
		return index.getChanges(instance1, role2);
	}

	public Collection<InstanceType> getInstancesFromThisBranch(
			TransactionType transaction, InstanceType instance1, RoleType role2)
			throws PDStoreException {
		return index.getInstancesFromThisBranch(transaction, instance1, role2);
	}

	public boolean instanceExists(TransactionType transaction,
			InstanceType instance) throws PDStoreException {
		return index.instanceExists(transaction, instance);
	}

	public Collection<InstanceType> getAllInstancesInRole(
			TransactionType transaction, RoleType role) throws PDStoreException {
		return index.getAllInstancesInRole(transaction, role);
	}

	public List<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			TransactionType since, InstanceType instance1, RoleType role2)
			throws PDStoreException {
		return index.getChanges(since, instance1, role2);
	}

	public TransactionType addTransaction(
			Transaction<TransactionType, InstanceType, RoleType> transaction)
			throws PDStoreException {

		// set the persistence flags of the changes in the transaction
		for (PDChange<TransactionType, InstanceType, RoleType> change : transaction) {
			boolean roleExcludedFromLog = (this.getInstance(change.getTransaction(),
					(InstanceType) change.getRole2().getFirst(),
					(RoleType) PDStore.IS_NOT_IN_LOG_ROLEID) != null);
			change.setInLog((transaction.getPersistenceLevel() == PersistenceLevel.LOG_ONLY || transaction
					.getPersistenceLevel() == PersistenceLevel.LOG_AND_INDEX)
					&& !roleExcludedFromLog);

			boolean roleExcludedFromIndex = (this.getInstance(change.getTransaction(),
					(InstanceType) change.getRole2().getFirst(),
					(RoleType) PDStore.IS_NOT_IN_INDEX_ROLEID) != null);
			change.setInIndex((transaction.getPersistenceLevel() == PersistenceLevel.INDEX_ONLY || transaction
					.getPersistenceLevel() == PersistenceLevel.LOG_AND_INDEX)
					&& !roleExcludedFromIndex);
		}

		// ChangeLogStore.addTransaction() stores only changes that have the
		// inLog-flag set. 
		
		transaction.ensureDurableIdIsSet();

		changeLogStore.addTransaction(transaction);

		// IndexStore.addTransaction() stores only changes that have the
		// inIndex-flag set.
		index.addTransaction(transaction);

		return transaction.getDurableId();
	}

	public TransactionType getRepository() throws PDStoreException {
		return changeLogStore.getRepository();
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getDetachedListenerList()
			throws PDStoreException {
		return changeLogStore.getDetachedListenerList();
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getInterceptorList()
			throws PDStoreException {
		return changeLogStore.getInterceptorList();
	}

	public Iterator<PDChange<TransactionType, InstanceType, RoleType>> iterator() {
		return changeLogStore.iterator();
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			PDChange<TransactionType, InstanceType, RoleType> change)
			throws PDStoreException {
		return index.getChanges(change);
	}

	public TransactionType begin() throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public TransactionType begin(
			Transaction<TransactionType, InstanceType, RoleType> transaction)
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public void rollback(TransactionType transaction) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType branch(TransactionType parentTransaction)
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType merge(TransactionType branchId,
			TransactionType parentTransaction) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public synchronized TransactionType begin(TransactionType branch,
			IsolationLevel isolationLevel) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType commit(TransactionType transaction)
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public void addLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public void removeLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public void addChange(
			PDChange<TransactionType, InstanceType, RoleType> change)
			throws PDStoreException {
		index.addChange(change);
	}

}