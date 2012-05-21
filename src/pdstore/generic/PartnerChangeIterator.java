package pdstore.generic;

import java.util.Iterator;

public class PartnerChangeIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
implements
Iterator<PDChange<TransactionType, InstanceType, RoleType>> {

	Iterator<PDChange<TransactionType, InstanceType, RoleType>> changes;

	public PartnerChangeIterator(Iterator<PDChange<TransactionType, InstanceType, RoleType>> changes) {
		this.changes = changes;
	}

	@Override
	public boolean hasNext() {
		return changes.hasNext();
	}

	@Override
	public PDChange<TransactionType, InstanceType, RoleType> next() {

		return changes.next().getPartnerChange();
	}

	@Override
	public void remove() {
		changes.remove();
	}
}
