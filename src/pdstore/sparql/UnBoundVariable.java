package pdstore.sparql;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnBoundVariable implements FilterExpression {
	public Variable var;
	

	public UnBoundVariable(Variable var) {
		super();
		this.var = var;
	}


	@Override
	public boolean evaluate(Map<Variable, Object> assignment) {
		if ( assignment.containsKey(var))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return ("!bound(" + var.toString()+")");
	}	
	
	@Override
	public Set<Variable> getVaraibles() {
		Set<Variable> variables = new HashSet<Variable>();
		if (this.var instanceof Variable){
			variables.add((Variable)this.var);
		}
		return variables;
	}	
}
