package pdstore.concurrent;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import pdstore.*;
import pdstore.generic.*;

/**
 * Implements query operations on a PDCore
 * 
 */
public class LogOnlyStore<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements GenericPDStore<TransactionType, InstanceType, RoleType> {

	PDCore<TransactionType, InstanceType, RoleType> changeLogStore;

	/**
	 * This variable stores a durable transaction ID while a writing transaction
	 * is ongoing.
	 * 
	 * 
	 */
	public TransactionType durableTransaction = null;

	public LogOnlyStore(
			PDCore<TransactionType, InstanceType, RoleType> coreStore)
			throws PDStoreException {
		changeLogStore = coreStore;
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getDetachedListenerList()
			throws PDStoreException {
		// TODO Auto-generated method stub
		return changeLogStore.getDetachedListenerList();
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getInterceptorList()
			throws PDStoreException {
		// TODO Auto-generated method stub
		return changeLogStore.getInterceptorList();
	}

	public void addLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType begin() throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType branch(TransactionType parentTransaction)
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public Collection<InstanceType> getAllInstancesInRole(
			TransactionType transaction, RoleType role) throws PDStoreException {
		throw new UnsupportedOperationException();
	}

	public Collection<InstanceType> getAllInstancesOfType(
			TransactionType transaction, RoleType type) throws PDStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public InstanceType getInstance(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean instanceExists(TransactionType transaction,
			InstanceType instance) throws PDStoreException {
		Iterable<PDChange<TransactionType, InstanceType, RoleType>> branchCore = changeLogStore
				.getBranchCore(transaction);
		for (PDChange<TransactionType, InstanceType, RoleType> c : branchCore) {

			// if we have already read past the current transaction in the log,
			// then abort

			if (((GUID) c.getTransaction()).compareTo((GUID) transaction) > 0)
				break;

			if (c.getInstance1().equals(instance)
					|| c.getInstance2().equals(instance)) {
				return true;
			}
		}
		return false;
	}

	public TransactionType merge(TransactionType branchId,
			TransactionType parentTransaction) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public void removeLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException {
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

	public TransactionType getRepository() throws PDStoreException {
		return changeLogStore.getRepository();
	}

	public Iterator<PDChange<TransactionType, InstanceType, RoleType>> iterator() {
		return changeLogStore.iterator();
	}

	/**
	 * Retrieves all instances from the file. Reads up to the given transaction
	 * ID; if the ID does not exist, reads all transactions in the file and
	 * returns false.
	 * 
	 * @param transactionId
	 * @param instance1
	 * @param role2
	 *            TODO
	 * @param roleId2
	 * @param result
	 * 
	 * @return true if the transaction was found in the file, false otherwise.
	 * @throws IOException
	 */
	public Collection<InstanceType> getInstances(TransactionType transactionId,
			InstanceType instance1, RoleType role2) throws PDStoreException {
		Collection<InstanceType> result = new HashSet<InstanceType>();
		Iterable<PDChange<TransactionType, InstanceType, RoleType>> branchCore = changeLogStore
				.getBranchCore(transactionId);
		for (PDChange<TransactionType, InstanceType, RoleType> c : branchCore) {
			// if we have already read past the timestamp of the requested
			// transaction in the log,
			// then return
			if (((GUID) c.getTransaction()).compareTo((GUID) transactionId) > 0)
				break;

			if (c.getRole2().equals(role2)) {
				if (c.getInstance1().equals(instance1)) {
					if (c.getChangeType().equals(ChangeType.LINK_ADDED)) {
						result.add(c.getInstance2());
					} else if (c.getChangeType().equals(ChangeType.LINK_REMOVED)) {
						result.remove(c.getInstance2());
					}
				}
			}

			// check link in other direction
			if (c.getRole2().equals(role2.getPartner())) {
				if (c.getInstance2().equals(instance1)) {
					if (c.getChangeType().equals(ChangeType.LINK_ADDED)) {
						result.add(c.getInstance1());
					} else if (c.getChangeType().equals(ChangeType.LINK_REMOVED)) {
						result.remove(c.getInstance1());
					}
				}
			}
		}
		return result;
		// new ArrayList<InstanceType>());
	}

	@Override
	public Iterable<PDChange<TransactionType, InstanceType, RoleType>> getBranchCore(
			TransactionType branch) throws PDStoreException {
		return changeLogStore.getBranchCore(branch);
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			InstanceType instance1, RoleType role2) throws PDStoreException {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Collection<InstanceType> getInstancesFromThisBranch(
			TransactionType transaction, InstanceType instance1, RoleType role2)
			throws PDStoreException {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public List<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			TransactionType since, InstanceType instance1, RoleType role2)
			throws PDStoreException {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public TransactionType addTransaction(TransactionType branch,
			List<PDChange<TransactionType, InstanceType, RoleType>> changes)
			throws PDStoreException {	
		return changeLogStore.addTransaction(branch, changes);
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			PDChange<TransactionType, InstanceType, RoleType> change)
			throws PDStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDChange<TransactionType, InstanceType, RoleType> getValidChange(
			PDChange<TransactionType, InstanceType, RoleType> change)
			throws PDStoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
