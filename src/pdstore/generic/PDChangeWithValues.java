package pdstore.generic;

import java.util.Collection;
import java.util.Map;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.sparql.Variable;

/**
 * This is a simple implementation of PDChange as a value object.
 * 
 * @author Gerald Weber
 */
public class PDChangeWithValues<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends PDChange<TransactionType, InstanceType, RoleType> {

	/**
	 * version UID for Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	private TransactionType transaction = null;
	private InstanceType instance1 = null;
	private RoleType role2 = null;
	private InstanceType instance2 = null;
	private ChangeType changeType;
	private boolean isLocal = false;

	public PDChangeWithValues(ChangeType type, TransactionType transaction,
			InstanceType instance1, RoleType role2, InstanceType instance2) {
		this.setInstance1(instance1);
		this.setRole2(role2);
		this.setInstance2(instance2);
		this.setTransaction(transaction);
		this.setChangeType(type);
	}

	public PDChangeWithValues() {
	}

	public ChangeType getChangeType() {
		return changeType;
	}
	
	public TransactionType getTransaction() {
		return transaction;
	}

	public void setTransaction(TransactionType transaction) {
		this.transaction = transaction;
	}

	public InstanceType getInstance1() {
		return instance1;
	}

	public RoleType getRole2() {
		return role2;
	}

	public InstanceType getInstance2() {
		return instance2;
	}

	public InstanceType getNormalAccessedInstance() {
		if (getRole2().isFirst())
			return getInstance2();
		return getInstance1();
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	/**
	 * This method is used in computing the partners of object instance1 over
	 * role2. This method assumes that "result" is collecting the partners. This
	 * method encapsulates the semantics of the change object. It applies this
	 * change to "result". This method does not check for transactions, i.e this
	 * method should only be called if the transaction attribute has been taken
	 * into account.
	 * 
	 * @param instance1
	 * @param role2
	 * @param result
	 * @return true if a conflict was detected
	 */
	public boolean attemptChange(Object instance1, GUID role2,
			Collection<Object> result) {
		if (getRole2().equals(role2)) {
			if (getInstance1().equals(instance1)) {
				boolean conflict = applyChangeForInstance2(result);
				return conflict;
			}
		}
		if (getRole2().equals(role2.getPartner())) {
			// check link in other direction
			if (getInstance2().equals(instance1)) {
				boolean conflict = applyChangeForInstance1(result);
				return conflict;
			}
		}
		return true;
	}

	/**
	 * See method applyChangeForInstance1
	 * 
	 * @param result
	 * @return
	 */
	public boolean applyChangeForInstance2(Collection<Object> result) {
		if (getChangeType().equals(ChangeType.LINK_ADDED)) {
			result.add(getInstance2());
			// since only double remove is seen as a conflict:
			// for adds we always return true.
			return true;
		} else if (getChangeType().equals(ChangeType.LINK_REMOVED)) {
			return result.remove(getInstance2());
		}
		return true;
	}

	/**
	 * This method detects conflicts without changing result.
	 * 
	 * @param result
	 * @return true iff there is no conflict
	 */
	public boolean isApplyingForInstance1(Collection<Object> result) {
		if (getChangeType().equals(ChangeType.LINK_ADDED)) {
			// since only double remove is seen as a conflict:
			// for adds we always return true.
			return true;
		} else if (getChangeType().equals(ChangeType.LINK_REMOVED)) {
			return result.contains(getInstance1());
		}
		return false;
	}

	/**
	 * See method isApplyingForInstance1 This method detects conflicts without
	 * changing result.
	 * 
	 * @param result
	 * @return true iff there is no conflict
	 */
	public boolean isApplyingForInstance2(Collection<Object> result) {
		if (getChangeType().equals(ChangeType.LINK_ADDED)) {
			// since only double remove is seen as a conflict:
			// for adds we always return true.
			return true;
		} else if (getChangeType().equals(ChangeType.LINK_REMOVED)) {
			return result.contains(getInstance2());
		}
		return true;
	}

	/**
	 * This method encapsulates the application of changeTypes to the set
	 * "result." It also encapsulates the detection of conflicts. A conflict is
	 * a remove that does not have an effect.
	 * 
	 * @param result
	 * @return true iff there is no conflict
	 */
	public boolean applyChangeForInstance1(Collection<Object> result) {
		if (getChangeType().equals(ChangeType.LINK_ADDED)) {
			result.add(getInstance1());
			// since only double remove is seen as a conflict:
			// for adds we always return true.
			return true;
		} else if (getChangeType().equals(ChangeType.LINK_REMOVED)) {
			return result.remove(getInstance1());
		}
		return false;
	}

	public void setInstance1(InstanceType instance1) {
		this.instance1 = instance1;
	}

	public void setRole2(RoleType role2) {
		this.role2 = role2;
	}

	public void setInstance2(InstanceType instance2) {
		this.instance2 = instance2;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	protected void setPartnerChange(
			PDChangeWithValues<TransactionType, InstanceType, RoleType> partnerChange) {
		this.partnerChange = partnerChange;
	}

	boolean isInLog = true;

	boolean isInIndex = true;

	public void setInLog(boolean isInLog) {
		this.isInLog = isInLog;
	}

	public void setInIndex(boolean isInIndex) {
		this.isInIndex = isInIndex;
	}

	public boolean isInLog() {
		return isInLog;
	}

	public boolean isInIndex() {
		return isInIndex;
	}


}
