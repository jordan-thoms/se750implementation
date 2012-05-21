package pdstore.notify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.generic.Pairable;

/**
 * An adapter class for PDListener.
 * For the list of matchingTemplates it creates an object.
 * 
 * @author gweb017
 *
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public class PDListenerAdapter<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>> implements
		PDListener<TransactionType, InstanceType, RoleType> {
	
	Set<PDChange<TransactionType, InstanceType, RoleType>> matchingTemplates
	   = new HashSet<PDChange<TransactionType, InstanceType, RoleType>>();

	@Override
	public void transactionCommitted(
			List<PDChange<TransactionType, InstanceType, RoleType>> transaction,
			List<PDChange<TransactionType, InstanceType, RoleType>> matchedChanges,
			PDCoreI<TransactionType, InstanceType, RoleType> core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<PDChange<TransactionType, InstanceType, RoleType>> getMatchingTemplates() {
		// TODO Auto-generated method stub
		return matchingTemplates;
	}

}
