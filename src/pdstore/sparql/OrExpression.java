package pdstore.sparql;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OrExpression implements FilterExpression {

public FilterExpression[] args;
	
	public OrExpression(FilterExpression... args) {
		super();
		this.args = args;
	}
	
	@Override
	public boolean evaluate(Map<Variable, Object> assignment) {
		for (FilterExpression expr : args)
			if(expr.evaluate(assignment))
				return true;
		return false; 
	}

	@Override
	public String toString(){
		String orString;
		orString = "( ";
		for (int i =0; i< args.length; i++){
			orString+= args[i].toString();
			if (i == args.length-1)
				break;
			orString+= " || ";
		}
		orString += " )";
		return orString;
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
