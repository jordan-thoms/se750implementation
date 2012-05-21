package pdstore.changelog;

import java.util.HashMap;
import java.util.Iterator;

import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDFilterIterator;
import pdstore.generic.Pairable;

/**
 * @author gweb017
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public class OldBranchIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends PDFilterIterator<TransactionType, InstanceType, RoleType> {
	HashMap<TransactionType, TransactionType> branches;

	/**
	 * @param baseIterator
	 * @param branches
	 *            null means: all branches are considered
	 */
	public OldBranchIterator(
			Iterator<PDChange<TransactionType, InstanceType, RoleType>> baseIterator,
			HashMap<TransactionType, TransactionType> branches) {
		super(baseIterator);
		this.branches = branches;
	}

	public boolean filterCondition(
			PDChange<TransactionType, InstanceType, RoleType> nextChangeInBase) {
		if (branches == null)
			return true;
		if (this.branches
				.containsKey((TransactionType) ((GUID) nextChangeInBase
						.getTransaction()).getBranchID())
				&& (nextChangeInBase.getTransaction().compareTo(
						this.branches
								.get((TransactionType) ((GUID) nextChangeInBase
										.getTransaction()).getBranchID())) <= 0)) {
			return true;
		}
		return false;
	}

}
