package pdstore.concurrent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.IteratorBasedCollection;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.Transaction;
import pdstore.changeindex.AggregationIterator;
import pdstore.changeindex.GenericIndexStore;
import pdstore.dal.PDTransaction;
import pdstore.generic.GlobalTypeAdapter;
import pdstore.generic.PDStoreI;
import pdstore.generic.PDChange;
import pdstore.generic.Pairable;
import pdstore.generic.TypeAdapter;
import pdstore.sparql.Variable;

public abstract class GenericConcurrentStore<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements PDStoreI<TransactionType, InstanceType, RoleType> {

	/**
	 * The adaptor object that encapsulates all operations on generic arguments.
	 */
	@SuppressWarnings("unchecked")
	public final TypeAdapter<TransactionType, InstanceType, RoleType> typeAdapter = (TypeAdapter<TransactionType, InstanceType, RoleType>) GlobalTypeAdapter.typeAdapter;


	protected PDStoreI<TransactionType, InstanceType, RoleType> committedTransactionStore;
	protected GenericIndexStore<TransactionType, InstanceType, RoleType> openTransactionStore = new GenericIndexStore<TransactionType, InstanceType, RoleType>(
			null, this);

	protected TransactionType latestDurableTransaction = null;
	protected Map<TransactionType, Transaction<TransactionType, InstanceType, RoleType>> idToTransaction = new HashMap<TransactionType, Transaction<TransactionType, InstanceType, RoleType>>();

	public void setLatestDurableTransaction(
			TransactionType latestDurableTransaction) {
		this.latestDurableTransaction = latestDurableTransaction;
	}

	public TransactionType getLatestDurableTransaction() {
		return latestDurableTransaction;
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			PDChange<TransactionType, InstanceType, RoleType> change)
			throws PDStoreException {
		
		PDChange<TransactionType, InstanceType, RoleType> committedStoreTemplate = new PDChange<TransactionType, InstanceType, RoleType>(
				change);
		PDChange<TransactionType, InstanceType, RoleType> openStoreTemplate = new PDChange<TransactionType, InstanceType, RoleType>(
				change);

	    Debug.println("getChanges", "ReadUncommitted");
		TransactionType transaction = change.getTransaction();
		
		// if transaction is unspecified, then look at all committed transactions
		if (transaction == null
				|| transaction instanceof Variable) {
			committedStoreTemplate.setTransaction(getLatestDurableTransaction());
			return committedTransactionStore.getChanges(committedStoreTemplate);
		}

		// if given transaction has already been committed, look only into committedTransactionStore
		if(typeAdapter.isDurable(transaction)){
			// return just the committed transactions
			return  committedTransactionStore.getChanges(change);	
		}
		
        Transaction<TransactionType, InstanceType, RoleType> transactionObject = idToTransaction.get(transaction);
		if (transactionObject==null || transactionObject.getIsolationLevel().seesLatestDurableTransaction()) {
			// use the latest transaction on this branch for querying the committed transaction store.
			Debug.println("seesLatestDurableTransaction", "ReadUncommitted");
			committedStoreTemplate.setTransaction(typeAdapter.maxTransactionId(transaction));
		}
		
		Collection<PDChange<TransactionType, InstanceType, RoleType>> committedChanges = committedTransactionStore
				.getChanges(committedStoreTemplate);
		
		if (transactionObject!=null && transactionObject.getIsolationLevel().isDirtyReading()) {
			// use a maximum transacion ID on the branch for querying the open transaction store.
			Debug.println("isDirtyReading", "ReadUncommitted");
			openStoreTemplate.setTransaction(typeAdapter.maxTransactionId(typeAdapter.getBranchID(transaction)));
		}

		Collection<PDChange<TransactionType, InstanceType, RoleType>> uncommittedChanges = openTransactionStore
				.getChanges(openStoreTemplate);
		if (committedChanges == null)
			return uncommittedChanges;
		if (uncommittedChanges == null)
			return committedChanges;
		uncommittedChanges.addAll(committedChanges);
		if(!change.getChangeType().equals(ChangeType.LINK_EFFECTIVE))
			return uncommittedChanges;
		AggregationIterator<TransactionType,InstanceType,RoleType> aggregationIterator = 
				new AggregationIterator<TransactionType, InstanceType, RoleType>(
				uncommittedChanges.iterator());
		return new IteratorBasedCollection<PDChange<TransactionType, InstanceType, RoleType>>(
				aggregationIterator);
	}

}
