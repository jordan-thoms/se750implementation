/**
 * 
 */
package pdstore.changeindex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.se.genoupe.tools.ConcatenationIterator;
import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.FilterIterator;
import nz.ac.auckland.se.genoupe.tools.IteratorBasedCollection;
import nz.ac.auckland.se.genoupe.tools.MapIterator;
import nz.ac.auckland.se.genoupe.tools.ReverseListIterator;

import pdstore.*;
import pdstore.generic.*;
import pdstore.notify.PDListener;

import pdstore.changelog.OldBranchIterable;

/**
 * A genericPDStore that caches result sets in Maps. Stores the history of
 * changes and can therefore answer historic queries correctly. This class is
 * usually accessed through the class LogAndIndexStore, so most of the relevant
 * documentation can be found there.
 * 
 * @author Christof, Gerald
 */
public class GenericIndexStore<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements PDStoreI<TransactionType, InstanceType, RoleType> {

	/**
	 * The adaptor object that encapsulates all operations on generic arguments.
	 */
	@SuppressWarnings("unchecked")
	public final TypeAdapter<TransactionType, InstanceType, RoleType> typeAdapter = (TypeAdapter<TransactionType, InstanceType, RoleType>) GlobalTypeAdapter.typeAdapter;

	/**
	 * A two-level index mapping role2 to instance1 to the corresponding
	 * changes.
	 */
	private Map<RoleType, Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>>> changeIndex = new HashMap<RoleType, Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>>>();

	/**
	 * A one-level index mapping (instance1, instance2) to the corresponding
	 * changes.
	 */
	private LinkIndex<TransactionType, InstanceType, RoleType> linkIndex = new LinkIndex<TransactionType, InstanceType, RoleType>();

	/**
	 * An empty collection which is used to return empty iterators in the case
	 * of index misses.
	 */
	final private Collection<PDChange<TransactionType, InstanceType, RoleType>> emptyChangeCollection = new ArrayList<PDChange<TransactionType, InstanceType, RoleType>>(
			0);

	private PDStoreI<TransactionType, InstanceType, RoleType> concurrentStore;

	/**
	 * Pre-loads the indices with all changes from the given core.
	 * 
	 * @param core
	 *            the core whose changes will be added to the indices
	 * @param fullStore
	 *            TODO
	 */
	public GenericIndexStore(
			PDCoreI<TransactionType, InstanceType, RoleType> core,
			PDStoreI<TransactionType, InstanceType, RoleType> fullStore) {

		concurrentStore = fullStore;

		// load the indices with the changes from the given core
		if (core == null)
			return;
		for (PDChange<TransactionType, InstanceType, RoleType> c : core) {
			addChange(c);
		}
	}

	/**
	 * Adds the given change to all the indices, if it is not flagged as being
	 * log-only.
	 * 
	 * @param change
	 *            the change to add to the indices
	 */
	public void addChange(
			PDChange<TransactionType, InstanceType, RoleType> change) {

		if (!typeAdapter.isDurable(change.getTransaction())) {
		} else {
			Debug.warningAssertTrue(change.isInIndex(),
					"Change should not have reached here");
		}

		addChangeToIndices(change);

		// The following part of the method ensures that it is registered
		// that instance 1 has a link using role2.
		// This is done in the PS/PO index, using the role constant
		// typeAdapter.USES_ROLE_ROLEID()
		// This logic currently ignores deletions, since the USES_ROLE_ROLEID
		// links
		// are currently not deleted.
		// Currently the code is not optimal but on the safe side.

		// TODO:
		// The check for inserting USES_ROLE links should be
		// only done in the open Transaction store.
		// One possible alternative would be to add the USES_ROLE links
		// to the transaction object. Then they would also be deleted
		// correctly from the open transaction store.
		// Then the following line should be correct here:
		// if(typeAdapter.isDurable(change.getTransaction())) return;

		// a recursive use of plain addChange would lead
		// to the following problem of recursion: A link
		// Bert isFriendOf Ernie
		// leads to the following links being inserted:
		// Bert USES_ROLE isFriendOf
		// Ernie USES_ROLE ~isFriendof (the partner Role).
		// but without stopping recursion, insertion of these
		// links would lead to the following links being
		// inserted as well:
		// Bert USES_ROLE USES_ROLE
		// Ernie USES_ROLE USES_ROLE
		// isFriendOf USES_ROLE ~USES_ROLE
		// ~isFriendOf USES_ROLE ~USES_ROLE
		// and the first time even:
		// USES_ROLE USES_ROLE ~USES_ROLE
		// ~USES_ROLE USES_ROLE ~USES_ROLE
		// USES_ROLE USES_ROLE USES_ROLE
		// ~USES_ROLE USES_ROLE USES_ROLE
		// To stop this we insert the following check:

		// look if role2 is already registered, and register it if not
		PDChange<TransactionType, InstanceType, RoleType> role2registered = new PDChange<TransactionType, InstanceType, RoleType>(
				ChangeType.LINK_ADDED, // TODO should be LINK_EFFECTIVE?
				change.getTransaction(), change.getInstance1(),
				typeAdapter.USES_ROLE_ROLEID(),
				typeAdapter.instanceFromRole(change.getRole2()));
		Collection<PDChange<TransactionType, InstanceType, RoleType>> result = concurrentStore
				.getChanges(role2registered);
		if (result.isEmpty())
			addChangeToIndices(role2registered);

		// look if role1 is already registered, and register it if not
		PDChange<TransactionType, InstanceType, RoleType> role1registered = new PDChange<TransactionType, InstanceType, RoleType>(
				ChangeType.LINK_ADDED, // TODO should be LINK_EFFECTIVE?
				change.getTransaction(), change.getInstance2(),
				typeAdapter.USES_ROLE_ROLEID(),
				typeAdapter.instanceFromRole(change.getRole2().getPartner()));
		result = concurrentStore.getChanges(role1registered);
		if (result.isEmpty())
			addChangeToIndices(role1registered);
	}

	private void addChangeToIndices(
			PDChange<TransactionType, InstanceType, RoleType> change) {
		// normalize the change, i.e. make sure role2 is the numerically first
		// role of the pair of roles
		PDChange<TransactionType, InstanceType, RoleType> normalizedChange = change
				.getNormalizedChange();

		// add change to the changeIndex in both directions for bidirectional
		// lookup
		IndexEntry<TransactionType, InstanceType, RoleType> indexEntry = getOrCreateMapEntry(normalizedChange);
		indexEntry.add(normalizedChange);

		PDChange<TransactionType, InstanceType, RoleType> partnerChange = normalizedChange
				.getPartnerChange();
		IndexEntry<TransactionType, InstanceType, RoleType> partnerEntry = getOrCreateMapEntry(partnerChange);
		partnerEntry.add(partnerChange);

		// also add it to the link index, which only has the link instances as
		// key (i.e. the key is insensitive wrt. the role or direction used)
		linkIndex.add(normalizedChange);
	}

	/**
	 * The entry returned by this method should be editable, therefore if a
	 * container in the map is missing this has to be created, in contrast to
	 * getNonEditableMapEntry().
	 * 
	 * @param change
	 * @return a non-null entry of the map
	 */
	public IndexEntry<TransactionType, InstanceType, RoleType> getOrCreateMapEntry(
			PDChange<TransactionType, InstanceType, RoleType> change) {
		IndexEntry<TransactionType, InstanceType, RoleType> result;
		RoleType role2 = change.getRole2();
		InstanceType instance1 = change.getInstance1();
		Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> roleMap = changeIndex
				.get(role2);
		if (roleMap == null) {
			roleMap = new HashMap<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>>();
			changeIndex.put(role2, roleMap);
		}

		result = roleMap.get(instance1);
		if (result == null) {
			result = new IndexEntry<TransactionType, InstanceType, RoleType>();
			roleMap.put(instance1, result);
		}
		return result;
	}

	/**
	 * 
	 * @param change
	 * @return null if there is no entry in the map
	 */
	public IndexEntry<TransactionType, InstanceType, RoleType> getMapEntry(
			RoleType role2, InstanceType instance1) {
		IndexEntry<TransactionType, InstanceType, RoleType> result;
		Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> roleMap = changeIndex
				.get(role2);
		if (roleMap == null) {
			return null;
		}

		result = roleMap.get(instance1);
		if (result == null) {
			return null;
		}
		return result;
	}

	@Override
	public void addLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException {
		addChange(new PDChange<TransactionType, InstanceType, RoleType>(
				ChangeType.LINK_ADDED, transaction, instance1, role2, instance2));
	}

	@Override
	public void removeLink(TransactionType transaction, InstanceType instance1,
			RoleType role2, InstanceType instance2) throws PDStoreException {
		addChange(new PDChange<TransactionType, InstanceType, RoleType>(
				ChangeType.LINK_REMOVED, transaction, instance1, role2,
				instance2));
	}

	public Collection<InstanceType> getInstances(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException {

		Collection<InstanceType> result = new ArrayList<InstanceType>();
		Collection<PDChange<TransactionType, InstanceType, RoleType>> indexEntry = getChanges(
				instance1, role2);

		// if there is no indexEntry, then the result is empty
		if (indexEntry == null)
			return result;

		Iterable<PDChange<TransactionType, InstanceType, RoleType>> changes = new OldBranchIterable<TransactionType, InstanceType, RoleType>(
				indexEntry, this, transaction);

		for (PDChange<TransactionType, InstanceType, RoleType> c : changes) {

			// if we have already read past the timestamp of the requested
			// transaction in the log, then break
			// if the transaction is null, return the current persistent state
			if (transaction != null
					&& ((GUID) c.getTransaction())
							.compareTo((GUID) transaction) > 0)
				break;

			c.applyChangeForInstance2((Collection<Object>) result);
		}
		return result;
	}

	public InstanceType getInstance(TransactionType transaction,
			InstanceType instance1, RoleType role2) throws PDStoreException {
		ArrayList<InstanceType> result = (ArrayList<InstanceType>) getInstances(
				transaction, instance1, role2);
		if (result.size() == 0)
			return null;
		return result.get(result.size() - 1);
	}

	public boolean instanceExists(TransactionType transaction,
			InstanceType instance) throws PDStoreException {
		for (Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> instanceMap : changeIndex
				.values()) {
			if (instanceMap.containsKey(instance))
				return true;
		}
		return false;
	}

	public Collection<InstanceType> getAllInstancesInRole(
			TransactionType transaction, RoleType role) throws PDStoreException {
		Collection<InstanceType> result = new ArrayList<InstanceType>();
		Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> instanceMap = changeIndex
				.get(role);

		if (instanceMap == null)
			return result;

		Collection<IndexEntry<TransactionType, InstanceType, RoleType>> changes = instanceMap
				.values();

		for (IndexEntry<TransactionType, InstanceType, RoleType> indexEntry : changes) {
			if (indexEntry == null)
				continue;
			for (PDChange<TransactionType, InstanceType, RoleType> c : indexEntry
					.getChangeList()) {
				// TODO: what is this comment???
				// then abort
				if (((GUID) c.getTransaction()).compareTo((GUID) transaction) > 0)
					break;

				result.add(c.getPartnerChange().getNormalAccessedInstance());
				// TODO: This looks like a bug: why is here a break????
				break;
			}
		}
		return result;
	}

	public final Collection<InstanceType> getAllInstancesOfType(
			TransactionType transactionId, RoleType typeId)
			throws PDStoreException {
		// get all the roles that the type owns
		Collection<InstanceType> roles2 = getInstances(transactionId,
				typeAdapter.instanceFromRole(typeId),
				(RoleType) PDStore.OWNED_ROLE_ROLEID);

		// look for instances in the underlying store
		Set<InstanceType> result = new HashSet<InstanceType>();
		for (InstanceType role2 : roles2)
			result.addAll(getAllInstancesInRole(transactionId,
					typeAdapter.RoleFromInstance(role2)));

		return result;
	}

	@Override
	public Collection<InstanceType> getInstancesFromThisBranch(
			TransactionType transaction, InstanceType instance1, RoleType role2)
			throws PDStoreException {
		Collection<InstanceType> result = new ArrayList<InstanceType>();
		Collection<PDChange<TransactionType, InstanceType, RoleType>> indexEntry = getChanges(
				instance1, role2);

		// if there is no indexEntry, then the result is empty
		if (indexEntry == null)
			return result;

		for (PDChange<TransactionType, InstanceType, RoleType> c : indexEntry) {

			// ignore all changes that are not from this branch
			if (!((GUID) c.getTransaction()).getBranchID().equals(
					((GUID) transaction).getBranchID()))
				continue;

			// if we have already read past the timestamp of the requested
			// transaction in the log, then break
			if (((GUID) c.getTransaction()).compareTo((GUID) transaction) > 0)
				break;

			c.applyChangeForInstance2((Collection<Object>) result);
		}
		return result;
	}

	@Override
	public List<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			TransactionType since, InstanceType instance1, RoleType role2)
			throws PDStoreException {
		Collection<PDChange<TransactionType, InstanceType, RoleType>> allChanges = getChanges(
				instance1, role2);
		List<PDChange<TransactionType, InstanceType, RoleType>> result = new ArrayList<PDChange<TransactionType, InstanceType, RoleType>>();
		for (PDChange<TransactionType, InstanceType, RoleType> change : allChanges)
			if (change.getTransaction().compareTo(since) >= 0)
				result.add(change);
		return result;

	}

	@Override
	public TransactionType addTransaction(
			Transaction<TransactionType, InstanceType, RoleType> transaction)
					throws PDStoreException {
		if (transaction.isEmpty())
			return transaction.getId();

		// Transaction must be added to index.
		for (PDChange<TransactionType, InstanceType, RoleType> c : transaction) {
			// Changes that do not belong into index are skipped
			if (!c.isInIndex())
				continue;
			addChange(c);
		}
		return transaction.getId();
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			InstanceType instance1, RoleType role2) {
		Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> map = changeIndex
				.get(role2);
		if (map == null) {
			return emptyChangeCollection;
		}

		IndexEntry<TransactionType, InstanceType, RoleType> indexEntry = map
				.get(instance1);
		if (indexEntry == null)
			return emptyChangeCollection;

		return indexEntry.getChangeList();
	}

	public void delete(PDChange<TransactionType, InstanceType, RoleType> change) {
		PDChange<TransactionType, InstanceType, RoleType> normalizedChange = change
				.getNormalizedChange();

		// remove change from changeIndex
		IndexEntry<TransactionType, InstanceType, RoleType> indexEntry1 = getOrCreateMapEntry(normalizedChange);
		indexEntry1.remove(normalizedChange);

		// remove partner change from changeIndex
		IndexEntry<TransactionType, InstanceType, RoleType> indexEntry2 = getOrCreateMapEntry(normalizedChange
				.getPartnerChange());
		indexEntry2.remove(normalizedChange.getPartnerChange());

		// remove change from linkIndex
		IndexEntry<TransactionType, InstanceType, RoleType> indexEntry3 = linkIndex
				.get(normalizedChange);
		indexEntry3.remove(normalizedChange);
	}

	@Override
	public TransactionType begin() throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public TransactionType begin(
			Transaction<TransactionType, InstanceType, RoleType> transaction)
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType commit(TransactionType transaction)
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public void rollback(TransactionType transaction) throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public TransactionType getRepository() throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public Iterator<PDChange<TransactionType, InstanceType, RoleType>> iterator() {
		/*
		 * Collect reverse iterators for the change lists in all the
		 * IndexEntry's in the linkIndex.
		 * 
		 * Note that this achieves only partial reverse time order, i.e. the
		 * instances with the same instance1 and instance2 are ordered
		 * correctly. However, this is enough for the AggregationIterator that
		 * is used to get only the effective changes: it requires that changes
		 * referring to the same link (i.e. instance1, instance2 and also role2)
		 * are ordered in reverse time.
		 */
		Collection<IndexEntry<TransactionType, InstanceType, RoleType>> indexEntries = linkIndex
				.values();
		List<Iterator<PDChange<TransactionType, InstanceType, RoleType>>> changeIterators = new ArrayList<Iterator<PDChange<TransactionType, InstanceType, RoleType>>>(
				indexEntries.size());
		for (IndexEntry<TransactionType, InstanceType, RoleType> indexEntry : indexEntries) {
			if (indexEntry == null)
				continue;
			List<PDChange<TransactionType, InstanceType, RoleType>> changeList = indexEntry
					.getChangeList();
			Iterator<PDChange<TransactionType, InstanceType, RoleType>> reverseIterator = new ReverseListIterator<PDChange<TransactionType, InstanceType, RoleType>>(
					changeList);
			changeIterators.add(reverseIterator);
		}
		return new ConcatenationIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeIterators);
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getDetachedListenerList()
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	public List<PDListener<TransactionType, InstanceType, RoleType>> getInterceptorList()
			throws PDStoreException {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public Collection<PDChange<TransactionType, InstanceType, RoleType>> getChanges(
			PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {

		// classify the change template
		ChangeTemplateKind templateKind = ChangeTemplateKind
				.getKind(changeTemplate);

		Debug.println(new Object[] { "GetChanges(", changeTemplate, "), case ",
				templateKind }, "getChanges");
		Debug.printCallStack(3, "getChanges");

		// get the matching information from the change template
		final ChangeType changeType = changeTemplate.getChangeType();
		final TransactionType transaction = changeTemplate.getTransaction();
		final InstanceType instance1 = changeTemplate.getInstance1();
		final RoleType role2 = changeTemplate.getRole2();
		final InstanceType instance2 = changeTemplate.getInstance2();
		
		final boolean isMaximumBranchID = transaction.equals(typeAdapter.maxTransactionId(typeAdapter.getBranchID(transaction)));
		if (isMaximumBranchID){
		    Debug.println("starting with pure branchID", "ReadUncommitted");
			
		}


		/*
		 * Get the changes that match the instance1-role2-instance2 pattern of
		 * the template.
		 * 
		 * Aggregation condition: the changeIterator returns changes referring
		 * to the same link in reverse time order. This is satisfied because all
		 * iterators are obtained as reverse iterators from IndexEntry's in
		 * indexes, where changes referring to the same link are added to the
		 * same IndexEntry in time-order.
		 */
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> changeIterator = null;
		switch (templateKind) {
		case XXX:
			changeIterator = iterator();
			break;
		case IXX:
			changeIterator = getIXX(transaction, instance1);
			break;
		case XRX:
			changeIterator = getXRX(role2);
			break;
		case IRX:
			changeIterator = getIRX(instance1, role2);
			break;
		case XXI:
			changeIterator = getIXX(transaction, instance2);
			break;
		case IXI:
			changeIterator = getIXI(changeTemplate);
			break;
		case XRI:
			changeIterator = getIRX(instance2, role2.getPartner());
			break;
		case IRI:
			changeIterator = getIRI(changeTemplate);
			break;
		default:
			throw new UnsupportedOperationException("Case " + templateKind
					+ " not supported.");
		}

		//TODO think about the case transaction == null
		
		// filter the changes that are already committed or part of the given
		// transaction (which may not be committed yet)
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> openTransactionIterator = new FilterIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeIterator) {
			public boolean filterCondition(
					PDChange<TransactionType, InstanceType, RoleType> change) {
				Debug.println(change, "openTransactionIterator");

				// TODO: needs isolationlevel, currently assumes snapshot
				TransactionType changeTransaction = change.getTransaction();

				// committed changes are always ok, the branchiterator should
				// deal with the differences of the transactionIsolationLevels.
				if (typeAdapter.isDurable(changeTransaction))
					return true;

				// if the open transaction store is queried with a pure branch ID, it returns
				// all open transactions.
				// This is used in the isolationlevels with isDirtyReading()== true
				// Set in GenericConcurrentStore.getchanges()
			    Debug.println("testPureBranchId", "ReadUncommitted");
				if (isMaximumBranchID){
				    Debug.println("isPureBranchId", "ReadUncommitted");
					return true;
					
				}
				
				// Snapshot isolation: the only open transaction permitted is
				// the one given in the changeTemplate
				if (changeTransaction.equals(transaction))
					return true;

				return false;
			}
		};

		// filter the changes that belong to the requested branch, up to the
		// requested transaction
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> branchIterator = new BranchIterator<TransactionType, InstanceType, RoleType>(
				openTransactionIterator, this, transaction);

		// denormalize the changes, depending on the changeTemplate
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> denormalizeIterator = null;
		switch (templateKind) {
		case XXX:
			// no denormalization necessary
			denormalizeIterator = branchIterator;
			break;
		case IXX:
		case IXI:
			// denormalize by checking instance1
			denormalizeIterator = new MapIterator<PDChange<TransactionType, InstanceType, RoleType>, PDChange<TransactionType, InstanceType, RoleType>>(
					branchIterator) {
				public PDChange<TransactionType, InstanceType, RoleType> map(
						PDChange<TransactionType, InstanceType, RoleType> change) {
					Debug.println(change, "denormalizeIterator");

					// return changes which are already in the right direction
					if (change.getInstance1().equals(instance1))
						return change;

					// change direction of changes that are in the wrong
					// direction
					return change.getPartnerChange();
				}
			};
			break;
		case XRX:
		case IRX:
		case XRI:
		case IRI:
			// denormalize by checking role2
			denormalizeIterator = new MapIterator<PDChange<TransactionType, InstanceType, RoleType>, PDChange<TransactionType, InstanceType, RoleType>>(
					branchIterator) {
				public PDChange<TransactionType, InstanceType, RoleType> map(
						PDChange<TransactionType, InstanceType, RoleType> change) {
					Debug.println(change, "denormalizeIterator");

					// return changes which are already in the right direction
					if (change.getRole2().equals(role2))
						return change;

					// change direction of changes that are in the wrong
					// direction
					return change.getPartnerChange();
				}
			};
			break;
		case XXI:
			// denormalize by checking instance2
			denormalizeIterator = new MapIterator<PDChange<TransactionType, InstanceType, RoleType>, PDChange<TransactionType, InstanceType, RoleType>>(
					branchIterator) {
				public PDChange<TransactionType, InstanceType, RoleType> map(
						PDChange<TransactionType, InstanceType, RoleType> change) {
					Debug.println(change, "denormalizeIterator");

					// return changes which are already in the right direction
					if (change.getInstance2().equals(instance2))
						return change;

					// change direction of changes that are in the wrong
					// direction
					return change.getPartnerChange();
				}
			};
			break;
		default:
			throw new UnsupportedOperationException("Case " + templateKind
					+ " not supported.");
		}

		// aggregate or filter the changes considering the changeType of the
		// template
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> aggregationIterator = null;
		if (changeType == null) {
			// don't filter or aggregate the changes
			aggregationIterator = denormalizeIterator;
		} else if (changeType == ChangeType.LINK_ADDED
				|| changeType == ChangeType.LINK_REMOVED) {
			aggregationIterator = new FilterIterator<PDChange<TransactionType, InstanceType, RoleType>>(
					denormalizeIterator) {
				public boolean filterCondition(
						PDChange<TransactionType, InstanceType, RoleType> change) {
					Debug.println(change, "aggregationIterator");

					// return only the changes with the template ChangeType
					return change.getChangeType() == changeType;
				}
			};
		} else if (changeType == ChangeType.LINK_EFFECTIVE) {
			// Return only the most recent change for each link (could be an add
			// or a remove). These changes are non-redundant, i.e. only changes
			// that have a final effect on the database state remain.
			aggregationIterator = new AggregationIterator<TransactionType, InstanceType, RoleType>(
					denormalizeIterator);
		} else {
			throw new UnsupportedOperationException("ChangeType " + changeType
					+ " not supported.");
		}

		// return the changes as an IteratorBasedCollection
		return new IteratorBasedCollection<PDChange<TransactionType, InstanceType, RoleType>>(
				aggregationIterator);
	}

	private Iterator<PDChange<TransactionType, InstanceType, RoleType>> getIXX(
			TransactionType transaction, InstanceType instance1) {
		// get iterator for all changes that state that the given instance uses
		// a certain role
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> roleChangeIterator = getIRX(
				instance1, typeAdapter.USES_ROLE_ROLEID());

		// collect the iterators of the changes that use the given instance and
		// any of the reportedly used roles
		List<Iterator<PDChange<TransactionType, InstanceType, RoleType>>> changeIterators = new ArrayList<Iterator<PDChange<TransactionType, InstanceType, RoleType>>>();
		while (roleChangeIterator.hasNext()) {
			// get the next change linking the given instance to a role
			PDChange<TransactionType, InstanceType, RoleType> roleChange = roleChangeIterator
					.next();

			// extract the role2 that the instance reportedly uses
			RoleType role2;
			if (roleChange.getRole2().equals(typeAdapter.USES_ROLE_ROLEID()))
				role2 = typeAdapter.RoleFromInstance(roleChange.getInstance2());
			else
				role2 = typeAdapter.RoleFromInstance(roleChange.getInstance1());

			// add the iterator for the changes that use the instance1 and the
			// role2
			changeIterators.add(getIRX(instance1, role2));
		}

		// concatenate all the collected change iterators into on big iterator
		return new ConcatenationIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeIterators);
	}

	private Iterator<PDChange<TransactionType, InstanceType, RoleType>> getXRX(
			RoleType role) {
		Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> instanceMap = changeIndex
				.get(role);
		if (instanceMap == null)
			return emptyChangeCollection.iterator();

		Collection<IndexEntry<TransactionType, InstanceType, RoleType>> indexEntries = instanceMap
				.values();

		List<Iterator<PDChange<TransactionType, InstanceType, RoleType>>> changeIterators = new ArrayList<Iterator<PDChange<TransactionType, InstanceType, RoleType>>>(
				indexEntries.size());
		for (IndexEntry<TransactionType, InstanceType, RoleType> indexEntry : indexEntries) {
			if (indexEntry == null)
				continue;
			List<PDChange<TransactionType, InstanceType, RoleType>> changeList = indexEntry
					.getChangeList();
			Iterator<PDChange<TransactionType, InstanceType, RoleType>> reverseIterator = new ReverseListIterator<PDChange<TransactionType, InstanceType, RoleType>>(
					changeList);
			changeIterators.add(reverseIterator);
		}

		return new ConcatenationIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeIterators);
	}

	private Iterator<PDChange<TransactionType, InstanceType, RoleType>> getIRX(
			InstanceType instance1, RoleType role2) {
		Map<InstanceType, IndexEntry<TransactionType, InstanceType, RoleType>> instanceMap = changeIndex
				.get(role2);
		if (instanceMap == null)
			return emptyChangeCollection.iterator();

		IndexEntry<TransactionType, InstanceType, RoleType> indexEntry = instanceMap
				.get(instance1);
		if (indexEntry == null)
			return emptyChangeCollection.iterator();

		List<PDChange<TransactionType, InstanceType, RoleType>> changeList = indexEntry
				.getChangeList();
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> reverseIterator = new ReverseListIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeList);
		return reverseIterator;
	}

	private Iterator<PDChange<TransactionType, InstanceType, RoleType>> getIXI(
			PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {
		List<PDChange<TransactionType, InstanceType, RoleType>> changeList = linkIndex
				.getNonEmpty(changeTemplate).getChangeList();
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> reverseIterator = new ReverseListIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeList);
		return reverseIterator;
	}

	private Iterator<PDChange<TransactionType, InstanceType, RoleType>> getIRI(
			PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {
		// get all the changes matching instance1 and instance2 from the
		// linkIndex
		List<PDChange<TransactionType, InstanceType, RoleType>> changeList = linkIndex
				.getNonEmpty(changeTemplate).getChangeList();
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> reverseIterator = new ReverseListIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeList);

		// filter out all the changes that also match normalized role2
		final RoleType role2 = changeTemplate.getRole2().getFirst();
		Iterator<PDChange<TransactionType, InstanceType, RoleType>> iriChangeIterator = new FilterIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				reverseIterator) {
			public boolean filterCondition(
					PDChange<TransactionType, InstanceType, RoleType> change) {
				return change.getRole2().equals(role2);
			}
		};
		return iriChangeIterator;
	}
}