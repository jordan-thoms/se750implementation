package pdstore.sparql;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AndExpression implements FilterExpression {
	
	public FilterExpression[] args;
	
	public AndExpression(FilterExpression... args) {
		super();
		this.args = args;
	}
	
	@Override
	public boolean evaluate(Map<Variable, Object> assignment) {
		for (FilterExpression expr : args)
			if(!expr.evaluate(assignment))
				return false;
		return true; 
	}
	
	@Override
	public String toString(){
		String andString;
		andString = "( ";
		for (int i =0; i< args.length; i++){
			andString+= args[i].toString();
			if (i == args.length-1)
				break;
			andString+= " && ";
		}
		andString += " ) ";
		return andString;
	}

	@Override
	public Set<Variable> getVaraibles() {
		Set<Variable> variables = new HashSet<Variable>();
		for (FilterExpression expr : args){
			variables.addAll(expr.getVaraibles());
		}
		return variables;
	}
	
}
