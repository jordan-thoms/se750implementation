package pdstore.sparql;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EqualExpression implements FilterExpression {

	public Object arg1, arg2;
	
	public EqualExpression(Object arg1, Object arg2) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	@Override
	public boolean evaluate(Map<Variable, Object> assignment) {
		if (arg1 instanceof Variable && arg2 instanceof Variable) {
			Variable var1 = (Variable) arg1;
			Variable var2 = (Variable) arg2;
			if (!assignment.containsKey(var1) || !assignment.containsKey(var2)
					|| !MatchIterator.equals(assignment.get(var1), assignment.get(var2)))
				return false;
		} else if (arg1 instanceof Variable
				&& !(arg2 instanceof Variable)) {
			Variable var1 = (Variable) arg1;
			if (!assignment.containsKey(var1)
					|| !MatchIterator.equals(assignment.get(var1), arg2))
				return false;
		} else if (!(arg1 instanceof Variable)
				&& arg2 instanceof Variable) {
			Variable var2 = (Variable) arg2;
			if (!assignment.containsKey(var2)
					|| !MatchIterator.equals(assignment.get(var2), arg1))
				return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return (arg1.toString() + " = " + arg2.toString());
	}	
	
	@Override
	public Set<Variable> getVaraibles() {
		Set<Variable> variables = new HashSet<Variable>();
		if (this.arg1 instanceof Variable){
			variables.add((Variable)this.arg1);
		}
		if (this.arg2 instanceof Variable){
			variables.add((Variable) this.arg2);
		}
		return variables;
	}	
}
