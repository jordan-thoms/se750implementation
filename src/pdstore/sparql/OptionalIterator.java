package pdstore.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pdstore.GUID;
import pdstore.generic.ChangeTemplateKind;
import pdstore.generic.PDChange;
import pdstore.generic.Pairable;
import nz.ac.auckland.se.genoupe.tools.StatefulIterator;

/**
 * 
 * @author Ganesh This iterator will provide the optional data for each where
 *         tuples output. If no optional or partial optional is only found it
 *         appends null for other unmatched optional value. It returns a element
 *         type of resultelement which will have where tuples, optional tuples
 *         and variable assignment.
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */

public class OptionalIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends
		StatefulIterator<ResultElement<TransactionType, InstanceType, RoleType>> {

	CartesianIndexIterator<TransactionType, InstanceType, RoleType> whereIterator;
	List<PDChange<TransactionType, InstanceType, RoleType>> currentWhere;
	Query query;
	List<PDChange<TransactionType, InstanceType, RoleType>> OptionalWhere;
	int optionalSize = 0;
	int reIterateIndexCount = 0;
	private Map<Variable, Object> variableAssignment = new HashMap<Variable, Object>();
	List<Iterator<ResultElement<TransactionType, InstanceType, RoleType>>> lstResultIerator = new ArrayList<Iterator<ResultElement<TransactionType, InstanceType, RoleType>>>();
	List<List<PDChange<TransactionType, InstanceType, RoleType>>> OptionalTuples = new ArrayList<List<PDChange<TransactionType, InstanceType, RoleType>>>();

	public OptionalIterator(
			CartesianIndexIterator<TransactionType, InstanceType, RoleType> whereIterator,
			Query query) {
		this.whereIterator = whereIterator;
		this.query = query;
		init();
	}

	private void init() {
		if (this.query.optionals != null) {
			lstResultIerator.clear();
			OptionalTuples.clear();
			variableAssignment.clear();
			for (int i = 0; i < query.optionals.size(); i++) {
				lstResultIerator.add(null);
				OptionalTuples.add(null);
			}
		}
	}

	/**
	 * this method does validation as if an current where tuple contains further
	 * more optional's or it loads the new where tuple.
	 */
	@SuppressWarnings("unchecked")
	protected ResultElement<TransactionType, InstanceType, RoleType> computeNext() {
		ResultElement<TransactionType, InstanceType, RoleType> resultElement = null;
		optionalSize = query.optionals.size();
		// Check to continue below only if we dont have any optional records
		if (!furtherOptionalsExist(lstResultIerator)) {
			if (!whereIterator.hasNext())
				return null;
			init();
			currentWhere = (List<PDChange<TransactionType, InstanceType, RoleType>>) (Object) whereIterator
					.next().whereTuples;
			assignVariables((Map<Variable, InstanceType>) whereIterator
					.getVariableAssignment());
			if (loadoptional()) {
				resultElement = iterateOptionals(optionalSize, true,
						resultElement);
			} else
				resultElement = computeWhere();
		} else {
			resultElement = iterateOptionals(optionalSize, false, resultElement);
		}
		return (ResultElement<TransactionType, InstanceType, RoleType>) resultElement;
	}

	/**
	 * This method checks whether for a given where tuple any further optional
	 * are found. If no optional is found it returns false.
	 * 
	 * @param lstResultIerator2
	 * @return
	 */
	private boolean furtherOptionalsExist(
			List<Iterator<ResultElement<TransactionType, InstanceType, RoleType>>> lstResultIerator2) {
		for (int i = 0; i < lstResultIerator2.size(); i++) {
			if (lstResultIerator2.get(i) != null
					&& lstResultIerator2.get(i).hasNext())
				return true;
		}
		return false;
	}

	/**
	 * This method is called only once whenever a new where tuple is processed.
	 * This load the optional data for the where collection and for preceding
	 * optional if they are linked
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean loadoptional() {
		boolean haveOptional = false;
		for (int i = optionalSize - 1; i >= 0; i--) {
			Query q = new Query(query.optionals.get(i).select,
					query.optionals.get(i).where,
					query.optionals.get(i).filter,
					query.optionals.get(i).optionals,
					query.optionals.get(i).store);
			lstResultIerator
					.set(i,
							(Iterator<ResultElement<TransactionType, InstanceType, RoleType>>) (Object) q
									.execute(null, this.getVariableAssignment()));
			// resolve the second optional with variables from the first
			if (lstResultIerator.get(i).hasNext()) {
				assignVariables(((StatefulIterator<ResultElement<TransactionType, InstanceType, RoleType>>) lstResultIerator
						.get(i)).getNext().getVariableAssignment());
				haveOptional = true;
			}
		}
		return haveOptional;
	}

	/**
	 * This method loops through the retrieved optional data. For the first time
	 * it computes from n-th index to 0-th index of optionals. For consecutive
	 * calls it loops only in 0-th index. If no data is found in 0-th index then
	 * reloadOptionalIterator is called to load the previous optional data. When
	 * a next optional is found for a index, again the control is returned to
	 * iterateOptionals.
	 * 
	 * @param indexCount
	 * @param isFirst
	 * @param resultElement
	 * @return
	 */

	private ResultElement<TransactionType, InstanceType, RoleType> iterateOptionals(
			int indexCount, boolean isFirst,
			ResultElement<TransactionType, InstanceType, RoleType> resultElement) {
		// will iterate from least significant index
		for (int i = 0; i <= indexCount - 1; i++) {
			if (lstResultIerator.get(i).hasNext()) {
				List<PDChange<TransactionType, InstanceType, RoleType>> optionalChange;
				optionalChange = lstResultIerator.get(i).next().whereTuples;
				ResultElement<TransactionType, InstanceType, RoleType> next = ((StatefulIterator<ResultElement<TransactionType, InstanceType, RoleType>>) lstResultIerator
						.get(i)).getCurrent();
				Map<Variable, InstanceType> variableAssignment2 = next
						.getVariableAssignment();
				assignVariables(variableAssignment2);
				OptionalTuples.set(i, optionalChange);
				if (!isFirst && i == 0)
					break;// we got the least significant index change
			} else {
				if (!isFirst)
					if (ReloadOptionalIterator()) {
						iterateOptionals(reIterateIndexCount, true,
								resultElement);
						break;
					} else {
						for (int J = 0; J <= indexCount - 1; J++)
							OptionalTuples.set(J, null);
						break;
					}
				OptionalTuples.set(i, null);
			}
		}
		resultElement = new ResultElement<TransactionType, InstanceType, RoleType>(
				currentWhere, OptionalTuples, variableAssignment);

		return resultElement;
	}

	/**
	 * This functions loads the optionals for the current index with the
	 * variables from previous index variables assignment.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean ReloadOptionalIterator() {
		boolean returnvalue = false;
		// iteration starts from 1 as we call this function only we dont have
		// data for the 0th index
		for (int i = 1; i < optionalSize; i++) {
			if (lstResultIerator.get(i).hasNext()) {
				for (int loopcnt = i; loopcnt > 0; loopcnt--) {
					assignVariables(((StatefulIterator<ResultElement<TransactionType, InstanceType, RoleType>>) lstResultIerator
							.get(loopcnt)).getCurrent().getVariableAssignment());
					Query q = new Query(
							query.optionals.get(loopcnt - 1).select,
							query.optionals.get(loopcnt - 1).where,
							query.optionals.get(loopcnt - 1).filter,
							query.optionals.get(loopcnt - 1).optionals,
							query.optionals.get(loopcnt - 1).store);
					lstResultIerator
							.set(loopcnt - 1,
									(Iterator<ResultElement<TransactionType, InstanceType, RoleType>>) (Object) q
											.execute(
													null,
													(Map<Variable, Object>) ((StatefulIterator<ResultElement<TransactionType, InstanceType, RoleType>>) lstResultIerator
															.get(loopcnt))
															.getCurrent()
															.getVariableAssignment()));
				}
				reIterateIndexCount = i + 1;// the index at which next value was
											// found
				returnvalue = true;
				break;
			}
		}
		return returnvalue;
	}

	/**
	 * This method will be called, if for a where tuple result no optional data
	 * is found. This functions returns the where result as such.
	 * 
	 * @return
	 */
	private ResultElement<TransactionType, InstanceType, RoleType> computeWhere() {
		return new ResultElement<TransactionType, InstanceType, RoleType>(
				currentWhere, null, variableAssignment);

	}

	private void assignVariables(Map<Variable, InstanceType> assignedVariables) {
		// Get assigned variables to resolve optional query
		this.variableAssignment.putAll(assignedVariables);
	}

	public Map<Variable, Object> getVariableAssignment() {
		// TODO: get reviewed for this method
		Map<Variable, Object> temp = new HashMap<Variable, Object>();
		for (Map.Entry<Variable, Object> entry : this.variableAssignment
				.entrySet()) {
			temp.put(entry.getKey(), entry.getValue());
		}
		return temp;

	}

}
