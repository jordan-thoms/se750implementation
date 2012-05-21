package pdstore.changelog;

import java.util.HashMap;
import java.util.Iterator;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.generic.PDStoreI;
import pdstore.generic.PDChange;
import pdstore.generic.Pairable;

public class OldBranchIterable<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements Iterable<PDChange<TransactionType, InstanceType, RoleType>> {

	TransactionType transactionId;
	HashMap<TransactionType, TransactionType> branches = null;
	Iterable<PDChange<TransactionType, InstanceType, RoleType>> input;
	PDStoreI<TransactionType, InstanceType, RoleType> store;

	public OldBranchIterable(
			Iterable<PDChange<TransactionType, InstanceType, RoleType>> input,
			PDStoreI<TransactionType, InstanceType, RoleType> store,
			TransactionType transactionId) {
		this.transactionId = transactionId;
		this.input = input;
		this.store = store;
		if (transactionId == null)
			return;
		this.branches = new HashMap<TransactionType, TransactionType>();
		getMergedBranches(transactionId, this.branches);
	}

	@Override
	public Iterator<PDChange<TransactionType, InstanceType, RoleType>> iterator() {
		return new OldBranchIterator(input.iterator(), branches);
	}

	/**
	 * Find all branches, which were merged into current branch (transitive
	 * closure) before transaction t, including current branch, and when they
	 * were merged
	 * 
	 * @param transactionId
	 *            , ID of an existing transaction
	 * @param HashMap
	 *            <BranchID, TransactionID>
	 * @throws PDStoreException
	 */
	public void getMergedBranches(TransactionType transactionId,
			HashMap<TransactionType, TransactionType> allbranches)
			throws PDStoreException {

		// a hashmap contains all parent merge transactions
		HashMap<TransactionType, TransactionType> parentbranches = new HashMap<TransactionType, TransactionType>();
		if (allbranches.isEmpty()) {
			allbranches.put(
					(TransactionType) ((GUID) transactionId).getBranchID(),
					transactionId);
		}
		getParentTransactions(transactionId, parentbranches);

		// iterate the parent merge transactions check whether the branch is
		// exist
		// in the allbranches hashmap, if not exists store it, otherwise compare
		// the value
		// and store the latest transactionId
		if (!parentbranches.isEmpty()) {
			Iterator<TransactionType> iteratorKey = parentbranches.keySet()
					.iterator();
			while (iteratorKey.hasNext()) {
				TransactionType branchId = iteratorKey.next();
				if (!allbranches.containsKey(branchId)) {
					allbranches.put(branchId, parentbranches.get(branchId));
				} else {
					TransactionType parentTransactionId = parentbranches
							.get(branchId);
					if (((GUID) parentTransactionId).later((GUID) allbranches
							.get(branchId))) {
						allbranches.put(branchId, parentTransactionId);
					}
				}
			}
			// iterate the merge transactions, recursive call
			// getParentBranches() by the merge transactions.
			Iterator<TransactionType> iteratorValue = parentbranches.values()
					.iterator();
			while (iteratorValue.hasNext()) {
				TransactionType parentTransactionId = iteratorValue.next();
				getMergedBranches(parentTransactionId, allbranches);
			}
		}
	}

	/**
	 * Find all parent branches, which were merged into current branch
	 * (transitive closure) before transaction t, and when they were merged
	 * 
	 * @param transactionId
	 *            , ID of an existing transaction
	 * @param HashMap
	 *            <BranchID, TransactionID>
	 * @throws PDStoreException
	 */
	public void getParentTransactions(TransactionType transactionId,
			HashMap<TransactionType, TransactionType> allbranches) {
		for (InstanceType i : store.getInstancesFromThisBranch(transactionId,
				(InstanceType) ((GUID) transactionId).getBranchID(),
				(RoleType) PDStore.PARENTTRANSACTION_CHILDBRANCH_ROLEID
						.getPartner())) {

			// get the id of the branch that was merged
			TransactionType branchId = (TransactionType) ((GUID) i)
					.getBranchID();

			// get the transaction from the other branch that was merged
			TransactionType newTransactionId = (TransactionType) i;

			if (allbranches.containsKey(branchId)) {
				if (((GUID) newTransactionId).later((GUID) allbranches
						.get(branchId))) {
					allbranches.put(branchId, newTransactionId);
				}
			} else
				allbranches.put(branchId, newTransactionId);
		}
	}

}
