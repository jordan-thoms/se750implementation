package pdstore.changeindex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
 * Iterator that gets a base iterator of changes, which must be in reverse time
 * order, and filters and returns only the non-redundant changes (i.e. those
 * that actually have an effect on the final state.
 * 
 * IPORTANT: This Iterator works only if the changes are really sorted in
 * reverse time order by their transaction timestamp in the base Iterator.
 * 
 * @author Christof
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public class AggregationIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends PDFilterIterator<TransactionType, InstanceType, RoleType> {

	/**
	 * Set to keep track of the links that have already been written (added or
	 * deleted) when going through the changes in reverse time order (i.e. from
	 * newer to older).
	 */
	Set<PDChange<TransactionType, InstanceType, RoleType>> writtenLinks = new HashSet<PDChange<TransactionType, InstanceType, RoleType>>();

	public AggregationIterator(
			Iterator<PDChange<TransactionType, InstanceType, RoleType>> baseIterator) {
		super(baseIterator);
	}

	public boolean filterCondition(
			PDChange<TransactionType, InstanceType, RoleType> change) {
		Debug.println(change, "aggregationIterator");

		// Create the link corresponding to the given change. The link is also
		// represented as a PDChange, but the transaction and changetype are set
		// to null so that only instance1, role2, instance2 remain.
		PDChange<TransactionType, InstanceType, RoleType> link = new PDChange<TransactionType, InstanceType, RoleType>(
				change);
		link.setChangeType(null);
		link.setTransaction(null);
		
		//TODO change semantics of LINK_EFFECTIVE so that removes are generally not returned anymore

		// If this link was written more recently, then an earlier write has no effect
		// and should not be passed on (no matter if the write was an add or a
		// remove).
		if (writtenLinks.contains(link))
			return false;

		// If the link was not written more recently, then this is the first change
		// affecting that link, and hence the effective (i.e.
		// non-redundant) change for that link we are looking for.
		writtenLinks.add(link);
		return true;
	}
}
