package pdstore.sparql;

import java.util.List;
import java.util.Map;

import pdstore.generic.PDChange;
import pdstore.generic.Pairable;

public class ResultElement<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>> {
	List<PDChange<TransactionType, InstanceType, RoleType>> whereTuples;
	List<List<PDChange<TransactionType, InstanceType, RoleType>>> optionalTuples;
	public Map<Variable, InstanceType> getVariableAssignment() {
		return variableAssignment;
	}

	Map<Variable, InstanceType> variableAssignment;

	public ResultElement(List<PDChange<TransactionType, InstanceType, RoleType>> whereTuples,
	List<List<PDChange<TransactionType, InstanceType, RoleType>>> optionalTuples,
	Map<Variable, Object> variableAssignment2) {
		this.whereTuples=whereTuples;
		this.optionalTuples=optionalTuples;
		this.variableAssignment=(Map<Variable, InstanceType>) variableAssignment2;
	}

	public Object get(Object typeName) {
		return this.variableAssignment.get(typeName);		
	}
}
