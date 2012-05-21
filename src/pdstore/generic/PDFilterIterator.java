package pdstore.generic;

import java.util.Iterator;

import nz.ac.auckland.se.genoupe.tools.FilterIterator;

/**
 * This is a generic iterator that implements a filter.
 * 
 * @author Gerald
 * 
 */
public class PDFilterIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends
		FilterIterator<PDChange<TransactionType, InstanceType, RoleType>> {

	public PDFilterIterator(
			Iterator<PDChange<TransactionType, InstanceType, RoleType>> baseIterator) {
		super(baseIterator);
	}

}
