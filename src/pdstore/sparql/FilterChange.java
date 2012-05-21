package pdstore.sparql;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pdstore.generic.ChangeTemplateKind;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;
import pdstore.generic.Pairable;

public class FilterChange<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>> extends
		PDChange<TransactionType, InstanceType, RoleType> implements FilterExpression{
	
	PDStoreI<TransactionType, InstanceType, RoleType> store;

	public FilterChange(
			PDChange<TransactionType, InstanceType, RoleType> template, PDStoreI<TransactionType, InstanceType, RoleType> store) {
		super(template);
		this.store = store;
 	}


	@Override
	public boolean evaluate(Map<Variable, Object> assignment) {
		PDChange<TransactionType, InstanceType, RoleType> change = 
				substituteVariables(assignment, ChangeTemplateKind.getKind(this));
		// If still variables are left, then the usage of this change as a filter was wrong
		if(!ChangeTemplateKind.getKind(change).equals(ChangeTemplateKind.IRI) )
				// TODO: Throw warning or exception
				return false;
		return store.getChanges(change).iterator().hasNext();
	}

	@Override
	public Set<Variable> getVaraibles() {
		Set<Variable> variables = new HashSet<Variable>();
		if (this.getTransaction() instanceof Variable){
			variables.add((Variable)(this.getTransaction()));
		}
		if (this.getInstance1() instanceof Variable){
			variables.add((Variable)(this.getInstance1()));
		}
		if (this.getRole2() instanceof Variable){
			variables.add((Variable)(this.getRole2()));
		}
		if (this.getInstance2() instanceof Variable){
			variables.add((Variable)(this.getInstance2()));
		}
		return variables;
	}	
}
