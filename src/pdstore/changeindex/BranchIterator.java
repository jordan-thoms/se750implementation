package pdstore.changeindex;

import java.util.HashMap;
import java.util.Iterator;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.generic.PDStoreI;
import pdstore.generic.GlobalTypeAdapter;
import pdstore.generic.PDChange;
import pdstore.generic.PDFilterIterator;
import pdstore.generic.Pairable;
import pdstore.generic.TypeAdapter;

/**
 * @author gweb017
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public class BranchIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends PDFilterIterator<TransactionType, InstanceType, RoleType> {

	/**
	 * The adaptor object that encapsulates all operations on generic arguments.
	 */
	public final TypeAdapter<TransactionType, InstanceType, RoleType> typeAdaptor = (TypeAdapter<TransactionType, InstanceType, RoleType>) GlobalTypeAdapter.typeAdapter;

	TransactionType transactionId;
	PDStoreI<TransactionType, InstanceType, RoleType> store;
	HashMap<TransactionType, TransactionType> branches = new HashMap<TransactionType, TransactionType>();

	BranchIterator(
			Iterator<PDChange<TransactionType, InstanceType, RoleType>> input,
			PDStoreI<TransactionType, InstanceType, RoleType> store,
			TransactionType transactionId) {
		super(input);
		this.transactionId = transactionId;
		this.store = store;

		getMergedBranches(transactionId, this.branches);
	}

	public boolean filterCondition(
			PDChange<TransactionType, InstanceType, RoleType> change) {
		Debug.println(change, "branchIterator");
		
		// identify branch of the given change
		TransactionType branch = typeAdaptor.getBranchID(change
				.getTransaction());

		// make sure the change is in one of the relevant branches
		if (!branches.containsKey(branch))
			return false;

		// identify last parent transaction of branch of the given change
		TransactionType parentTransaction = branches.get(branch);

		// make sure the change happened before the last parent transaction
		if (change.getTransaction().compareTo(parentTransaction) > 0)
			return false;

		return true;
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
	void getMergedBranches(TransactionType transactionId,
			HashMap<TransactionType, TransactionType> allbranches)
			throws PDStoreException {

		// a hashmap contains all parent merge transactions
		HashMap<TransactionType, TransactionType> parentbranches = new HashMap<TransactionType, TransactionType>();
		if (allbranches.isEmpty()) {
			allbranches.put(
					typeAdaptor.getBranchID(transactionId),
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
					if (parentTransactionId.compareTo(allbranches
							.get(branchId))==1) {
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
	void getParentTransactions(TransactionType transactionId,
			HashMap<TransactionType, TransactionType> allbranches) {
		for (InstanceType i : store.getInstancesFromThisBranch(transactionId,
				typeAdaptor.instanceFromTransaction(typeAdaptor.getBranchID(transactionId)),
				(RoleType) PDStore.PARENTTRANSACTION_CHILDBRANCH_ROLEID
						.getPartner())) {

			// get the transaction from the other branch that was merged
			TransactionType newTransactionId = typeAdaptor.TransactionFromInstance(i);
			
			// get the id of the branch that was merged
			
			TransactionType branchId = typeAdaptor.getBranchID(newTransactionId);


			if (allbranches.containsKey(branchId)) {
				if (1==newTransactionId.compareTo(allbranches
						.get(branchId))) {
					allbranches.put(branchId, newTransactionId);
				}
			} else
				allbranches.put(branchId, newTransactionId);
		}
	}
}
