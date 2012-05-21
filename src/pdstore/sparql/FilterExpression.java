package pdstore.sparql;

import java.util.Map;
import java.util.Set;

public interface FilterExpression {
	boolean evaluate(Map<Variable, Object> assignment);
	String toString();
	Set<Variable> getVaraibles();
}
