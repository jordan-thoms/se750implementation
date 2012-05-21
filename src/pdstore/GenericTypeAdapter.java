package pdstore;

import pdstore.generic.TypeAdapter;

/**
 * A generic superclass for type adaptors, which can hold for example two kinds of method implementations:
 * 
 * Convenience functions which should not be overridden in subclasses.
 * 
 * Typical implementations of methods, such as isPureBranchID, which can be overridden in subclasses.
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public abstract class GenericTypeAdapter<TransactionType, InstanceType, RoleType> implements TypeAdapter<TransactionType, InstanceType, RoleType> {

	@Override
	public boolean isPureBranchID(TransactionType branch) {
		return branch!=null && branch.equals(this.getBranchID(branch));
	}

}