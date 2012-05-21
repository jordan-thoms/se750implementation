package pdstore.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nz.ac.auckland.se.genoupe.tools.ConcatenationIterator;
import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.IteratorBasedCollection;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.Transaction;
import pdstore.concurrent.ConcurrentStore;
import pdstore.notify.ListenerDispatcher;
import pdstore.notify.PDListener;

public class GenericPDStore<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
implements PDStoreI<TransactionType, InstanceType, RoleType> {

	/**
	 * A PDStore object that can be used for debugging code.
	 */
	protected static PDStore debugStore;

	/**
	 * Gets a static PDStore that can be used for debugging, e.g.
	 * pretty-printing instances with their names, if no other PDStore is at
	 * hand.
	 * 
	 * The debug store is the last PDStore that was instantiated from a file (as
	 * opposed to a store instantiated from another store object), or null.
	 * 
	 * @return a PDStore object that can be read from when printing debug output
	 */
	public static PDStore getDebugStore() {
		return debugStore;
	}

	/**
	 * The adaptor object that encapsulates all operations on generic arguments.
	 */
	@SuppressWarnings("unchecked")
	public final TypeAdapter<TransactionType, InstanceType, RoleType> typeAdapter = (TypeAdapter<TransactionType, InstanceType, RoleType>) GlobalTypeAdapter.typeAdapter;
	
	protected PDStoreI<TransactionType, InstanceType, RoleType> store;
	private ListenerDispatcher<TransactionType, InstanceType, RoleType> viewDispatcher = new ListenerDispatcher<TransactionType, InstanceType, RoleType>();

	protected ListenerDispatcher<TransactionType, InstanceType, RoleType> listenerDispatcher = new ListenerDispatcher<TransactionType, InstanceType, RoleType>();

	protected ListenerDispatcher<TransactionType, InstanceType, RoleType> interceptorDispatcher = new ListenerDispatcher<TransactionType, InstanceType, RoleType>();

	protected ListenerDispatcher<TransactionType, InstanceType, RoleType> immediateDispatcher = new ListenerDispatcher<TransactionType, InstanceType, RoleType>();

	protected ListenerDispatcher<TransactionType, InstanceType, RoleType> readDispatcher = new ListenerDispatcher<TransactionType, InstanceType, RoleType>();

	/**
	 * Not used directly, but necessary as a superconstructor for the subclass constructors.
	 */
	public GenericPDStore() {
	}
	
	/**
	 * This constructor creates a GenericPDStore just as a facade to use the
	 * convenience functions, does not do the initPDStore().
	 * 
	 * @param store
	 */
	public GenericPDStore(PDStoreI<TransactionType, InstanceType, RoleType> store) {
		this.store = store;
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(PDChange<TransactionType, InstanceType, RoleType> change) throws PDStoreException {
		ArrayList<PDChange<TransactionType, InstanceType, RoleType>> viewResult = new ArrayList<PDChange<TransactionType, InstanceType, RoleType>>();
		List<PDChange<TransactionType, InstanceType, RoleType>> matchedChanges = new ArrayList<PDChange<TransactionType, InstanceType, RoleType>>();
		matchedChanges.add(change);
		
		getViewDispatcher().transactionCommitted(viewResult, matchedChanges , store);
		Collection<PDChange<TransactionType, InstanceType, RoleType>> changes = store.getChanges(change);
		
		if(viewResult.isEmpty()) return changes;
		ArrayList<Iterator<PDChange<TransactionType, InstanceType, RoleType>>> iteratorlist = 
				new ArrayList<Iterator<PDChange<TransactionType,InstanceType,RoleType>>>();
		iteratorlist.add(changes.iterator());
		iteratorlist.add(viewResult.iterator());
		return new IteratorBasedCollection<PDChange<TransactionType,InstanceType,RoleType>>(new ConcatenationIterator(iteratorlist));
		
	}
	
	public ListenerDispatcher<TransactionType,InstanceType,RoleType> getViewDispatcher() {
		return viewDispatcher;
	}

	public Collection<InstanceType> getInstances(TransactionType transaction, InstanceType instance1, RoleType role2)
			throws PDStoreException {
				Debug.assertTrue(instance1 != null, "instance1 must not be null.");
				Debug.assertTrue(role2 != null, "role2 must not be null.");
			
				Collection<PDChange<TransactionType,InstanceType,RoleType>> changes = store
						.getChanges(new PDChange<TransactionType,InstanceType,RoleType>(
								ChangeType.LINK_EFFECTIVE, transaction, instance1,
								role2, null));
			    Debug.println("getInstances", "ReadUncommitted");
			
				Collection<InstanceType> result = new ArrayList<InstanceType>();
			
				Iterator<PDChange<TransactionType,InstanceType,RoleType>> changesIterator = changes
						.iterator();
				while (changesIterator.hasNext()) {
			
					PDChange<TransactionType,InstanceType,RoleType> next = changesIterator.next();
					if(next.getChangeType().equals(ChangeType.LINK_ADDED))
				        // add only the LINK_ADDED, not the LINK_REMOVED
						result.add(next.getInstance2());
				}
				return result;
			}

	public InstanceType getInstance(TransactionType transaction, InstanceType instance1, RoleType role2)
			throws PDStoreException {
				Debug.assertTrue(instance1 != null, "instance1 must not be null.");
				Debug.assertTrue(role2 != null, "role2 must not be null.");
			
				Collection<PDChange<TransactionType, InstanceType, RoleType>> changes = store
						.getChanges(new PDChange<TransactionType, InstanceType, RoleType>(
								ChangeType.LINK_EFFECTIVE, transaction, instance1,
								role2, null));
			
				Iterator<PDChange<TransactionType, InstanceType, RoleType>> changesIterator = changes
						.iterator();
				while (changesIterator.hasNext()) {
					PDChange<TransactionType, InstanceType, RoleType> next = changesIterator.next();
					if(next.getChangeType().equals(ChangeType.LINK_ADDED))
				        // add only the LINK_ADDED, not the LINK_REMOVED
						return next.getInstance2();
				}
				return null;
			}

	public TransactionType getRepository() throws PDStoreException {
		return store.getRepository();
	}

	public TransactionType begin() throws PDStoreException {
		return begin(IsolationLevel.SNAPSHOT);
	}

	public TransactionType begin(IsolationLevel isolationLevel) throws PDStoreException {
		return begin(typeAdapter.getBranchID(getRepository()), isolationLevel);
	}

	public TransactionType begin(TransactionType branch) throws PDStoreException {
		return begin(branch, IsolationLevel.SNAPSHOT);
	}

	public TransactionType begin(TransactionType branch, IsolationLevel isolationLevel) throws PDStoreException {
		// make sure open transaction is non-first GUID
		TransactionType transactionId = typeAdapter.getPartner(
				typeAdapter.getFirst(typeAdapter.newTransactionId(branch)));
		Transaction<TransactionType, InstanceType, RoleType> transaction = 
				new Transaction<TransactionType, InstanceType, RoleType>(
				transactionId);
		transaction.setIsolationLevel(isolationLevel);
		return begin(transaction);
	}

	@Override
	public TransactionType begin(Transaction<TransactionType, InstanceType, RoleType> transaction) throws PDStoreException {
		return store.begin(transaction);
	}

	public TransactionType commit(TransactionType transaction) throws PDStoreException {
		return store.commit(transaction);
	}

	public void rollback(TransactionType transaction) throws PDStoreException {
		store.rollback(transaction);
	}

	public void addLink(TransactionType transaction, InstanceType instance1, RoleType role2, InstanceType instance2)
			throws PDStoreException {
				Debug.assertTrue(instance1 != null, "instance1 must not be null.");
				Debug.assertTrue(role2 != null, "role2 must not be null.");
				Debug.assertTrue(instance2 != null, "instance2 must not be null.");
			
				store.addLink(transaction, instance1, role2, instance2);
			}

	@Override
	public void addChange(PDChange<TransactionType, InstanceType, RoleType> change) throws PDStoreException {
		store.addChange(change);
	}

	/**
	 * Gets the ListenerDispatcher of this store, i.e. the object that performs
	 * template matching on the incoming changes and calls change listeners. You
	 * need to use this object to register new change listeners.
	 * 
	 * @return the ListenerDispatcher of this store
	 */
	public ListenerDispatcher<TransactionType, InstanceType, RoleType> getListenerDispatcher() {
		return listenerDispatcher;
	}

	/**
	 * Analogous to getListenerDispatcher(), works for interceptors. See javadoc
	 * in getListenerDispatcher() and in getInterceptorList() for further
	 * explanations.
	 * 
	 * @return
	 */
	public ListenerDispatcher<TransactionType, InstanceType, RoleType> getInterceptorDispatcher() {
		return interceptorDispatcher;
	}

	/**
	 * @return the immediateDispatcher
	 */
	public ListenerDispatcher<TransactionType, InstanceType, RoleType> getImmediateDispatcher() {
		return immediateDispatcher;
	}

	/**
	 * @return the readDispatcher
	 */
	public ListenerDispatcher<TransactionType, InstanceType, RoleType> getReadDispatcher() {
		return readDispatcher;
	}

	public void setLink(TransactionType transaction, InstanceType instance1, RoleType role2, InstanceType instance2) {
		InstanceType oldInstance = getInstance(transaction, instance1, role2);
	
		if (oldInstance != null) {
			// if link is already set, nothing to do
			if (oldInstance.equals(instance2))
				return;
	
			// if different link is set, remove it
			removeLink(transaction, instance1, role2, oldInstance);
		}
	
		addLink(transaction, instance1, role2, instance2);
	}

	/**
	 * This method ensures that a link exists, i.e. if it is not added or is
	 * deleted, it adds it.
	 * 
	 * @param transaction
	 * @param instance1
	 * @param role2
	 * @param instance2
	 */
	public void ensureAddedLink(TransactionType transaction, InstanceType instance1, RoleType role2,
			InstanceType instance2) {
			
				if (linkExists(transaction, instance1, role2, instance2))
					return;
			
				PDChange<TransactionType, InstanceType, RoleType> change2 = new PDChange<TransactionType, InstanceType, RoleType>(
						ChangeType.LINK_ADDED, transaction, instance1, role2, instance2);
				store.addChange(change2);
			}

	public boolean linkExists(TransactionType transaction, InstanceType instance1, RoleType role2, InstanceType instance2) {
		boolean linkExists;
		PDChange<TransactionType, InstanceType, RoleType> change = new PDChange<TransactionType, InstanceType, RoleType>(
				ChangeType.LINK_EFFECTIVE, transaction, instance1, role2,
				instance2);
		Collection<PDChange<TransactionType, InstanceType, RoleType>> list = store
				.getChanges(change);
		linkExists = !list.isEmpty()
				&& list.iterator().next().getChangeType() == ChangeType.LINK_ADDED;
		return linkExists;
	}

	public Collection<InstanceType> getAllInstancesInRole(TransactionType transaction, RoleType role)
			throws PDStoreException {
				return store.getAllInstancesInRole(transaction, role);
			}

	public boolean instanceExists(TransactionType transaction, InstanceType instance)
			throws PDStoreException {
				Debug.assertTrue(instance != null,
						"instance to look for in instanceExists must not be null.");
			
				return store.instanceExists(transaction, instance);
			}

	public void removeLink(TransactionType transaction, InstanceType instance1, RoleType role2, InstanceType instance2)
			throws PDStoreException {
				Debug.assertTrue(instance1 != null, "instance1 must not be null.");
				Debug.assertTrue(role2 != null, "role2 must not be null.");
				Debug.assertTrue(instance2 != null, "instance2 must not be null.");
			
				store.removeLink(transaction, instance1, role2, instance2);
			}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getDetachedListenerList() throws PDStoreException {
		return store.getDetachedListenerList();
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getInterceptorList() throws PDStoreException {
		return store.getInterceptorList();
	}

	@Override
	public TransactionType addTransaction(Transaction<TransactionType, InstanceType, RoleType> transaction) throws PDStoreException {
		return store.addTransaction(transaction);
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(InstanceType instance1, RoleType role2) throws PDStoreException {
		return store.getChanges(instance1, role2);
	}

	@Override
	public List<PDChange<TransactionType, InstanceType, RoleType>> getChanges(TransactionType since, InstanceType instance1, RoleType role2)
			throws PDStoreException {
				return store.getChanges(since, instance1, role2);
			}

	public Iterator<PDChange<TransactionType, InstanceType, RoleType>> iterator() {
		return store.iterator();
	}

	@Override
	public Collection<InstanceType> getInstancesFromThisBranch(TransactionType transaction, InstanceType instance1,
			RoleType role2) throws PDStoreException {
				throw new UnsupportedOperationException("not yet implemented");
			}

}