package pdstore.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

/**
 * This sort implementation assumes the following: 1) the elements in the
 * unsorted collection have a total order 2) the collection only contains
 * elements for a single ordered list
 * 
 * @author Sarah
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public class SortByOrder<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements PDSorter<TransactionType, InstanceType, RoleType> {

	TransactionType transaction;
	RoleType nextRole;

	public SortByOrder(TransactionType transaction, RoleType nextRole) {
		this.transaction = transaction;
		this.nextRole = nextRole;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends InstanceType> List<E> sort(Collection<E> c) {
		List<E> sorted = new ArrayList<E>();
		if (c.size() != 0) {
			E element = c.iterator().next();
			sorted.add(element);

			PDWorkingCopy workingCopy = ((PDInstance) element)
					.getPDWorkingCopy();

			// Sort forwards from first InstanceType.
			E next;
			while ((next = (E) workingCopy.getInstance(
					(PDInstance) sorted.get(sorted.size() - 1),
					(GUID) this.nextRole)) != null) {
				sorted.add(next);
			}

			// Sort backwards from first InstanceType.
			RoleType previousRole = this.nextRole.getPartner();
			E previous;
			while ((previous = (E) workingCopy.getInstance(
					(PDInstance) sorted.get(0), (GUID) previousRole)) != null) {
				sorted.add(0, previous);
			}
		}
		return sorted;
	}
}
