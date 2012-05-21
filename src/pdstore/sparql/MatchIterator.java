package pdstore.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.ac.auckland.se.genoupe.tools.StatefulIterator;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;

public class MatchIterator extends StatefulIterator<Map<Variable, Object>> {
	
	Iterator<List<PDChange<GUID, Object, GUID>>> input;
	Query query;	
	boolean isexecuted=true;
	
	public MatchIterator(Iterator<List<PDChange<GUID, Object, GUID>>> input, 
			Query query) {
		this.input = input;
		this.query = query;
		
	}

	protected Map<Variable, Object> computeNext() { 
		List<PDChange<GUID, Object, GUID>> candidateSolution;		
		
		Map<Variable, Object> assignment;
		do {
			if (!input.hasNext())
				return null;
			candidateSolution = input.next();
			assignment = match(candidateSolution);
		} while (assignment == null);
		
		return assignment;
	}

	/**
	 * Checks if the given links match the "where" links in the query. If yes,
	 * then returns assignment of variables, otherwise null. E.g. input = <(t1,
	 * "Ernie", "has brother", "Bert"), (t2, "Ernie", "likes", "cookies")> where
	 * = <(*, ?x, "has brother", "Bert"), (*, ?x, "likes", "cookies")> would
	 * return <(?x, "Ernie")>.
	 * 
	 * input = <(t1, "Ernie", "has brother", "Bert"), (t2, "Ernie", "likes",
	 * "cookies")> where = <(*, ?x, "has brother", "Bert"), (*, ?x, "likes",
	 * ?y)> would return <(?x, "Ernie"), (?y, "cookies") >.
	 * 
	 * @param input
	 * @return
	 */
	public Map<Variable, Object> match(
			List<PDChange<GUID, Object, GUID>> input) {
		Map<Variable, Object> assignment = new HashMap<Variable, Object>();
		
		Iterator<PDChange<GUID, Object, GUID>> loopWhere = query.where
				.iterator();
		Iterator<PDChange<GUID, Object, GUID>> loopInput = input
				.iterator();

		while (loopInput.hasNext()) {
			PDChange<GUID, Object, GUID> inputChange = loopInput.next();
			if (loopWhere.hasNext()) {
				PDChange<GUID, Object, GUID> whereChange = loopWhere
						.next();

				// check transaction
				if (whereChange.getTransaction() instanceof Variable) {
					Variable var = (Variable) whereChange.getTransaction();
					if (assignment.containsKey(var)
							&& !assignment.get(var).equals(
									inputChange.getTransaction()))
						return null;
					else if (!assignment.containsKey(var))
						assignment.put(var, inputChange.getTransaction());
				} else if(whereChange.getChangeType() == ChangeType.LINK_NOW_ADDED
						|| whereChange.getChangeType() == ChangeType.LINK_NOW_DELETED)
					{
						if (whereChange.getTransaction() != null
							&& !inputChange.getTransaction().equals(
									whereChange.getTransaction()))
						return null;
					}

				// check changeType in the change
				if (!(whereChange.getChangeType() == ChangeType.LINK_EFFECTIVE)){
					if (whereChange.getChangeType() == ChangeType.LINK_NOW_ADDED){
						if (inputChange.getChangeType() != ChangeType.LINK_ADDED
								&& inputChange.getChangeType() != ChangeType.LINK_NOW_ADDED)
							return null;
					}
					else if (whereChange.getChangeType() == ChangeType.LINK_NOW_DELETED){
						if (inputChange.getChangeType() != ChangeType.LINK_REMOVED
								&& inputChange.getChangeType() != ChangeType.LINK_NOW_DELETED)
							return null;
					}
					else if (!whereChange.getChangeType().equals(inputChange.getChangeType()))
						return null;
				}
				// check if instance1 is a variable which either has the right
				// value or is unassigned
				if (whereChange.getInstance1() instanceof Variable) {
					Variable var = (Variable) whereChange.getInstance1();
					if (assignment.containsKey(var)
							&& !equals(assignment.get(var), inputChange
									.getInstance1()))
						return null;
					else if (!assignment.containsKey(var))
						// if var is unassigned, the assign it
						assignment.put(var, inputChange.getInstance1());
				} else if (whereChange.getInstance1() != null
						&& !equals(inputChange.getInstance1(), whereChange
								.getInstance1()))
					return null;

				// check if role2 is same
				if (whereChange.getRole2() instanceof Variable) {
					Variable var = (Variable) whereChange.getRole2();
					if (assignment.containsKey(var)
							&& !assignment.get(var).equals(
									inputChange.getTransaction()))
						return null;
					else if (!assignment.containsKey(var))
						assignment.put(var, inputChange.getRole2());
				} else if (whereChange.getRole2() != null
						&& !inputChange.getRole2().equals(
								whereChange.getRole2()))
					return null;

				// check if instance2 is a variable which either has the right
				// value
				// or is unassigned
				if (whereChange.getInstance2() instanceof Variable) {
					Variable var = (Variable) whereChange.getInstance2();
					if (assignment.containsKey(var)
							&& !equals(assignment.get(var), inputChange
									.getInstance2()))
						return null;
					else if (!assignment.containsKey(var))
						// if var is unassigned, the assign it
						assignment.put(var, inputChange.getInstance2());
				} else if (whereChange.getInstance2() != null
						&& !equals(inputChange.getInstance2(), whereChange
								.getInstance2()))
					return null;
			}
		}

		if (query.filter != null){
			if (!query.filter.evaluate(assignment))
				return null;
		}

		return assignment;
	}
	
	static Object normalizeValue(Object value) {
		if (value instanceof Integer || value instanceof Float
				|| value instanceof Long)
			value = new Double(value.toString());
		return value;
	}

	static boolean equals(Object a, Object b) {
		// use equals for the common cases
		if (a.equals(b))
			return true;

		// normalize values so that equals works
		return normalizeValue(a).equals(normalizeValue(b));
	}

	static int compareTo(Object a, Object b) {
		// use equals for the common cases
		try {
			int result = extracted(a).compareTo(b);
			return result;
		} catch (ClassCastException e) {
		}

		return (extracted(normalizeValue(a))).compareTo(normalizeValue(b));
	}

	@SuppressWarnings("unchecked")
	static Comparable<Object> extracted(Object a) {
		return (Comparable<Object>) a;
	}
}
