package pdstore.generic;

import java.util.Collection;
import java.util.Map;

import nz.ac.auckland.se.genoupe.tools.Debug;
import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.changelog.OldBranchIterable;
import pdstore.sparql.FilterExpression;
import pdstore.sparql.Variable;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

/**
 * Analysis: This class represents a change item in PDStore and is a fundamental
 * propositional unit of information that is stored in PDStore. Changes are
 * always stored as part of a transaction. There are two types of changes: link
 * addition or link removal.
 * 
 * Due to the symmetric approach of PDStore (see PDCore) there is always a
 * partner change with the roles interchanged.
 * 
 * Implementation: This class is the top class of a class hierarchy with
 * different implementations. This ensures that information is not duplicated.
 * 
 * @author gweb017
 * @author clut017
 * @author Daniel DENG
 */
public class PDChange<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>> 
 {

	/**
	 * version UID for Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	protected PDChange<TransactionType, InstanceType, RoleType> partnerChange = null;

	public PDChange(ChangeType type, TransactionType transaction,
			InstanceType instance1, RoleType role2, InstanceType instance2) {
		partnerChange = new PDChangeWithValues<TransactionType, InstanceType, RoleType>();
		partnerChange.setPartnerChange(this);
		this.setInstance1(instance1);
		this.setRole2(role2);
		this.setInstance2(instance2);
		this.setTransaction(transaction);
		this.setChangeType(type);
	}

	/**
	 * Copy constructor
	 * 
	 * @param template
	 *            The object that is copied
	 */
	public PDChange(PDChange<TransactionType, InstanceType, RoleType> template) {
		this(template.getChangeType(), template.getTransaction(), template
				.getInstance1(), template.getRole2(), template.getInstance2());
	}

	public PDChange() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		PDStore debugStore = PDStore.getDebugStore();

		// if there is no debug store yet, fall back to un-prettified output
		if (debugStore == null) {
			return "PDChange(transaction=" + getTransaction() + ", " + getChangeType()
					+ ", " + "instance1=" + getInstance1() + ", role2="
					+ getRole2() + ", instance2=" + getInstance2() + ")";
		}

		GUID transaction = debugStore.begin();
		// TODO why was the the code below used?
		// it causes a NullpointerException in GenericConcurrentStore.getChanges 
		// because the transactionid is not registered in the idToTransaction map
		// GUID.newTransactionId(debugStore.getRepository()
		//	.getBranchID()); 
		return toString(debugStore, transaction);
	}

	/**
	 * Constructs a best-effort human-readable name for a PDChange.
	 * 
	 * @param store
	 *            store to read names from
	 * @param transaction
	 *            transaction to read the names on
	 * @return a best-effort human-readable name for this change
	 */
	public String toString(PDStore store, GUID transaction) {
		/*
		 * TODO The use of PDStore here reduces the genericity of this class.
		 * The potentially generic methods of PDStore, such as getName which is
		 * used here, should be moved into a generic superclass of PDStore, e.g.
		 * PDStoreBase, and this superclass should be used here instead of
		 * PDStore.
		 */
		String result = "PDChange(";
		result += "transaction="
				+ store.getLabel(transaction, getTransaction()) + ", ";
		result += getChangeType() + ", ";
		result += "instance1=" + store.getLabel(transaction, getInstance1())
				+ ", ";
		result += "role2=" + store.getLabel(transaction, getRole2()) + ", ";
		result += "instance2=" + store.getLabel(transaction, getInstance2())
				+ ")";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getChangeType() == null) ? 0 : getChangeType().hashCode());
		result = prime
				* result
				+ ((getTransaction() == null) ? 0 : getTransaction().hashCode());
		result = prime * result
				+ ((getInstance1() == null) ? 0 : getInstance1().hashCode());
		result = prime * result
				+ ((getRole2() == null) ? 0 : getRole2().hashCode());
		result = prime * result
				+ ((getInstance2() == null) ? 0 : getInstance2().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PDChange<?, ?, ?>))
			return false;
		PDChange<?, ?, ?> other = (PDChange<?, ?, ?>) obj;
		if (getTransaction() == null) {
			if (other.getTransaction() != null)
				return false;
		} else if (!getTransaction().equals(other.getTransaction()))
			return false;
		if (getChangeType() == null) {
			if (other.getChangeType() != null)
				return false;
		} else if (!getChangeType().equals(other.getChangeType()))
			return false;
		if (getInstance1() == null) {
			if (other.getInstance1() != null)
				return false;
		} else if (!getInstance1().equals(other.getInstance1()))
			return false;
		if (getRole2() == null) {
			if (other.getRole2() != null)
				return false;
		} else if (!getRole2().equals(other.getRole2()))
			return false;
		if (getInstance2() == null) {
			if (other.getInstance2() != null)
				return false;
		} else if (!getInstance2().equals(other.getInstance2()))
			return false;
		return true;
	}

	public ChangeType getChangeType() {
		return getPartnerChange().getChangeType();
	}

	public PDChange<TransactionType, InstanceType, RoleType> getPartnerChange() {
		return partnerChange;
	}

	public PDChange<TransactionType, InstanceType, RoleType> getNormalizedChange() {
		// if there is no role given, the change is already normalized
		if (getRole2() == null)
			return this;

		if (getRole2().isFirst())
			return this;
		return getPartnerChange();
	}

	public TransactionType getTransaction() {
		return getPartnerChange().getTransaction();
	}

	public void setTransaction(TransactionType transaction) {
		getPartnerChange().setTransaction(transaction);
	}

	public InstanceType getInstance1() {
		return getPartnerChange().getInstance2();
	}

	public RoleType getRole2() {
		RoleType role2 = getPartnerChange().getRole2();
		RoleType partner;
		if (role2 == null)
			partner = null;
		else
			partner = role2.getPartner();

		return partner;
	}

	public InstanceType getInstance2() {
		return getPartnerChange().getInstance1();
	}

	public InstanceType getNormalAccessedInstance() {
		if (getRole2().isFirst())
			return getInstance2();
		return getInstance1();
	}

	public boolean isLocal() {
		return getPartnerChange().isLocal();
	}

	public void setLocal(boolean isLocal) {
		getPartnerChange().setLocal(isLocal);
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
		getPartnerChange().setInstance2(instance1);
	}

	public void setRole2(RoleType role2) {
		RoleType partner;
		if (role2 == null)
			partner = null;
		else
			partner = role2.getPartner();

		getPartnerChange().setRole2(partner);
	}

	public void setInstance2(InstanceType instance2) {
		getPartnerChange().setInstance1(instance2);
	}

	public void setChangeType(ChangeType changeType) {
		getPartnerChange().setChangeType(changeType);
	}

	protected void setPartnerChange(
			PDChange<TransactionType, InstanceType, RoleType> partnerChange) {
		this.partnerChange = partnerChange;
	}

	public void setInLog(boolean isInLog) {
		getPartnerChange().setInLog(isInLog);
	}

	public void setInIndex(boolean isInIndex) {
		getPartnerChange().setInIndex(isInIndex);
	}

	public boolean isInLog() {
		return getPartnerChange().isInLog();
	}

	public boolean isInIndex() {
		return getPartnerChange().isInIndex();
	}

	/**
	 * This function is computing a complexity of a triple for a position in the where clause.
	 * Currently based only on number of variables, 
	 * in future based on statistics.
	 *  
	 * @param variableAssignment
	 * @return
	 */
	public int costInWhereClause(Collection<Variable> assignedVariables) {
		return 
		+costBasedOnBeingSpecified(getInstance1(), assignedVariables)
		+costBasedOnBeingSpecified(getRole2(), assignedVariables)
		+costBasedOnBeingSpecified(getInstance2(), assignedVariables);
	}

	private static int costBasedOnBeingSpecified(Object o, Collection<Variable> assignedVariables) {
	    // null values are like variables, but don't bring benefit,
		// should be done as late as possible.
		if(o==null) return 100;
		
	    // constants incur no cost, but not as good as using an assigned variable:
		// might have low selectivity? Therefore some cost.
		if(!(o instanceof Variable)) return 10;
		
		
		// using an assigned variable seems to be a positive thing.
		// It ensures that previous assigmnents are put to good use as the 
		// query processing goes on.
		// so we set the cost to the lowest possible:
		if(assignedVariables.contains(o)) return 0;
		
		// an unassigned variable incurs cost, but brings hope to be assigned,
		// so the cost is lower than for a null value:
		return 90;
	}

	public PDChange<TransactionType, InstanceType, RoleType> substituteVariables(
			Map<Variable, Object> variableAssignment,
			ChangeTemplateKind changeTemplateKind) {
		PDChange<TransactionType, InstanceType, RoleType> tempChange;
		ChangeType changeType = getChangeType();
		InstanceType instance1 = getInstance1();
		RoleType role2 = getRole2();
		InstanceType instance2 = getInstance2();

		switch (changeTemplateKind) {
		case XXX:
		case XXI:
		case XRI:
		case XRX:
			instance1 = (InstanceType) variableAssignment
			.get((Variable) instance1);
			break;
		default:
			break;

		}
		switch (changeTemplateKind) {
		case XXX:
		case IXX:
		case IXI:
		case XXI:
			role2 = (RoleType) variableAssignment.get((Variable) role2);
			break;
		default:
			break;

		}

		switch (changeTemplateKind) {

		case XXX:
		case IXX:
		case XRX:
		case IRX:
			instance2 = (InstanceType) variableAssignment
			.get((Variable) instance2);
			break;
		default:
			break;

		}
		tempChange = new PDChange<TransactionType, InstanceType, RoleType>(
				changeType, getTransaction(), instance1, role2, instance2);
		return tempChange;
	}


}
