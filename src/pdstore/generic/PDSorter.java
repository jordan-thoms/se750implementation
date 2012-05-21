package pdstore.generic;

import java.util.Collection;
import java.util.List;

import pdstore.dal.PDInstance;

public interface PDSorter<TransactionType, InstanceType, RoleType extends Pairable<RoleType>> {
	/**
	 * Sorts the given collection according to some (partial) order.
	 * @param c a collection, probably unsorted.
	 * @return a list of sorted instances.
	 */
	<E extends InstanceType> List<E> sort(Collection<E> c);
}