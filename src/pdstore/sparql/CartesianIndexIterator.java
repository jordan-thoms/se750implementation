package pdstore.sparql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.FilterIterator;

import pdstore.ChangeType;
import pdstore.generic.ChangeTemplateKind;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;
import pdstore.generic.Pairable;

public class CartesianIndexIterator<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends
		CartesianPowerIterator<PDChange<TransactionType, InstanceType, RoleType>> {
	PDStoreI<TransactionType, InstanceType, RoleType> store;
	TransactionType transactionId;

	List<PDChange<TransactionType, InstanceType, RoleType>> where;
	ChangeTemplateKind[] readFromAssignment;
	ChangeTemplateKind[] writeToAssignment;
	FilterExpression[] filtersToEvaluate;
	// the query.filter is decomposed into singleFilters
	List<FilterExpression> singleFilters;
	private Map<Variable, Object> variableAssignment;

	int[] casesOfWhereTriples = new int[100];

	public CartesianIndexIterator(
			PDStoreI<TransactionType, InstanceType, RoleType> store,
			TransactionType transactionId,
			List<PDChange<TransactionType, InstanceType, RoleType>> where,
			FilterExpression filter, Map<Variable, Object> incomingAssignment) {
		this.store = store;
		this.transactionId = transactionId;
		this.where = where;
		this.exponent = where.size();
		if (incomingAssignment == null)
			setVariableAssignment(new HashMap<Variable, Object>());
		else
			setVariableAssignment(incomingAssignment);

		// only for evaluating query
		super.where = where;

		readFromAssignment = new ChangeTemplateKind[where.size()];
		writeToAssignment = new ChangeTemplateKind[where.size()];
		this.filtersToEvaluate = new FilterExpression[where.size()];

		singleFilters = decomposeFilter(filter);
		analyzeWhere();

		init();
	}

	/**
	 * decompose the query filter filters that are not AndExpressions.
	 */
	private List<FilterExpression> decomposeFilter(
			FilterExpression filterExpression) {

		List<FilterExpression> singleFilters = new ArrayList<FilterExpression>();
		if (filterExpression == null)
			return singleFilters;
		if (!(filterExpression instanceof AndExpression)) {
			singleFilters.add(filterExpression);
		} else {

			FilterExpression[] filters = ((AndExpression) filterExpression).args;
			for (int i = 0; i < filters.length; i++) {
				singleFilters.addAll(decomposeFilter(filters[i]));
			}
		}
		return singleFilters;
	}

	/**
	 * This method does currently a large proportion of the query optimization.
	 * 
	 * 
	 * Sets up the lists readFromAssignment and writeToAssignment, which
	 * describe for each where-tuple which variables in the tuple are set by
	 * reading from the existing assignment, and which variable values are
	 * written from the tuple to the assignment.
	 * 
	 * Example: (x,y,2), (x,z,2) read from assignment: nothing IRI write to
	 * assignment: x and z XXI read from assignment: x XRI write to assignment:
	 * y IXI
	 */
	private void analyzeWhere() {

		Debug.println("Analyzing the where-clause...", "SPARQL");

		Set<Variable> assignedVariables = new HashSet<Variable>();

		if (getVariableAssignment() != null) {
			for (Map.Entry<Variable, Object> asgnment : getVariableAssignment()
					.entrySet()) {
				if (!assignedVariables.contains(asgnment.getKey())) {
					assignedVariables.add(asgnment.getKey());
				}
			}
		}

		// Loop for processing triples in the where clause
		for (int i = exponent - 1; i >= 0; i--) {
			// Each loop body execution deals with one position in the
			// where clause. It will put the (probably) optimal triple
			// at that position.

			// Reshuffle:

			// Find best where triple
			int minComplexitySoFar = Integer.MAX_VALUE; // This will be helpful
														// when statistics will
														// be used
			int indexOfMinComplexity = i;
			int j;

			for (j = i; j >= 0; j--) {
				// find element with min complexity
				int changeTemplate = where.get(j).costInWhereClause(
						assignedVariables);
				if (changeTemplate < minComplexitySoFar) {
					minComplexitySoFar = changeTemplate;
					indexOfMinComplexity = j;
				}
			}

			// Swap this triple to the current position (i):
			// Swap where.get(i) with where.get(indexOfMinComplexity);
			PDChange<TransactionType, InstanceType, RoleType> tempChange = where
					.get(i);
			where.set(i, where.get(indexOfMinComplexity));
			where.set(indexOfMinComplexity, tempChange);

			InstanceType instance1 = where.get(i).getInstance1();
			RoleType role2 = where.get(i).getRole2();
			InstanceType instance2 = where.get(i).getInstance2();
			
			switch (ChangeTemplateKind.getKind(where.get(i))) {

			case IRI:
				readFromAssignment[i] = ChangeTemplateKind.IRI;
				writeToAssignment[i] = ChangeTemplateKind.IRI;
				break;

			case IRX:
				// case x is already written
				if (assignedVariables.contains((Variable) instance2)) {
					readFromAssignment[i] = ChangeTemplateKind.IRX;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else {
					assignedVariables.add((Variable) instance2);
					readFromAssignment[i] = ChangeTemplateKind.IRI;
					writeToAssignment[i] = ChangeTemplateKind.IRX;
				}
				break;

			case XRI:
				if (assignedVariables.contains((Variable) instance1)) {
					readFromAssignment[i] = ChangeTemplateKind.XRI;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else {
					assignedVariables.add((Variable) instance1);
					readFromAssignment[i] = ChangeTemplateKind.IRI;
					writeToAssignment[i] = ChangeTemplateKind.XRI;
				}
				break;

			case XRX:
				if (assignedVariables.contains((Variable) instance1)
						&& assignedVariables.contains((Variable) instance2)) {
					readFromAssignment[i] = ChangeTemplateKind.XRX;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else if (assignedVariables.contains((Variable) instance1)) {
					assignedVariables.add((Variable) instance2);
					readFromAssignment[i] = ChangeTemplateKind.XRI;
					writeToAssignment[i] = ChangeTemplateKind.IRX;
				} else if (assignedVariables.contains((Variable) instance2)) {
					assignedVariables.add((Variable) instance1);
					readFromAssignment[i] = ChangeTemplateKind.IRX;
					writeToAssignment[i] = ChangeTemplateKind.XRI;
				} else {
					assignedVariables.add((Variable) instance1);
					assignedVariables.add((Variable) instance2);
					readFromAssignment[i] = ChangeTemplateKind.IRI;
					writeToAssignment[i] = ChangeTemplateKind.XRX;
				}
				break;

			case IXI:
				if (assignedVariables.contains((Variable) role2)) {
					readFromAssignment[i] = ChangeTemplateKind.IXI;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else {
					assignedVariables.add((Variable) role2);
					readFromAssignment[i] = ChangeTemplateKind.IRI;
					writeToAssignment[i] = ChangeTemplateKind.IXI;
				}
				break;

			case XXI:
				if (assignedVariables.contains((Variable) role2)
						&& assignedVariables.contains((Variable) instance1)) {
					readFromAssignment[i] = ChangeTemplateKind.XXI;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else if (assignedVariables.contains((Variable) instance1)) {
					assignedVariables.add((Variable) role2);
					readFromAssignment[i] = ChangeTemplateKind.XRI;
					writeToAssignment[i] = ChangeTemplateKind.IXI;
				} else if (assignedVariables.contains((Variable) role2)) {
					assignedVariables.add((Variable) instance1);
					readFromAssignment[i] = ChangeTemplateKind.IXI;
					writeToAssignment[i] = ChangeTemplateKind.XRI;
				} else {
					assignedVariables.add((Variable) instance1);
					assignedVariables.add((Variable) role2);
					readFromAssignment[i] = ChangeTemplateKind.IRI;
					writeToAssignment[i] = ChangeTemplateKind.XXI;
				}
				break;
				
			case IXX:
				if (assignedVariables.contains((Variable) role2)
						&& assignedVariables.contains((Variable) instance2)) {
					readFromAssignment[i] = ChangeTemplateKind.IXX;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else if (assignedVariables.contains((Variable) instance2)) {
					assignedVariables.add((Variable) role2);
					readFromAssignment[i] = ChangeTemplateKind.IRX;
					writeToAssignment[i] = ChangeTemplateKind.IXI;
				} else if (assignedVariables.contains((Variable) role2)) {
					assignedVariables.add((Variable) instance2);
					readFromAssignment[i] = ChangeTemplateKind.IXI;
					writeToAssignment[i] = ChangeTemplateKind.IRX;
				} else {
					assignedVariables.add((Variable) instance2);
					assignedVariables.add((Variable) role2);
					readFromAssignment[i] = ChangeTemplateKind.IRI;
					writeToAssignment[i] = ChangeTemplateKind.IXX;
				}
				break;

			case XXX:
				if (assignedVariables.contains((Variable) role2)
						&& assignedVariables.contains((Variable) instance1)
						&& assignedVariables.contains((Variable) instance2)) {
					readFromAssignment[i] = ChangeTemplateKind.XXX;
					writeToAssignment[i] = ChangeTemplateKind.IRI;
				} else if (assignedVariables.contains((Variable) role2)
						&& assignedVariables.contains((Variable) instance2)) {
					assignedVariables.add((Variable) instance1);
					readFromAssignment[i] = ChangeTemplateKind.IXX;
					writeToAssignment[i] = ChangeTemplateKind.XRI;
				} else if (assignedVariables.contains((Variable) role2)
						&& assignedVariables.contains((Variable) instance1)) {
					assignedVariables.add((Variable) instance2);
					readFromAssignment[i] = ChangeTemplateKind.XXI;
					writeToAssignment[i] = ChangeTemplateKind.IRX;
				} else if (assignedVariables.contains((Variable) instance2)
						&& assignedVariables.contains((Variable) instance1)) {
					assignedVariables.add((Variable) role2);
					readFromAssignment[i] = ChangeTemplateKind.XRX;
					writeToAssignment[i] = ChangeTemplateKind.IXI;
				} else if (assignedVariables.contains((Variable) instance1)) {
					assignedVariables.add((Variable) role2);
					assignedVariables.add((Variable) instance2);
					readFromAssignment[i] = ChangeTemplateKind.XRI;
					writeToAssignment[i] = ChangeTemplateKind.IXX;
				} else if (assignedVariables.contains((Variable) instance2)) {
					assignedVariables.add((Variable) role2);
					assignedVariables.add((Variable) instance1);
					readFromAssignment[i] = ChangeTemplateKind.IRX;
					writeToAssignment[i] = ChangeTemplateKind.XXI;
				} else {
					assignedVariables.add((Variable) instance2);
					assignedVariables.add((Variable) instance1);
					readFromAssignment[i] = ChangeTemplateKind.IXI;
					writeToAssignment[i] = ChangeTemplateKind.XRX;
				}
				break;

			default:
				break;
			}
			
			//Adding transaction variables in assigned variables to make them used in filter
			if(where.get(i).getTransaction() instanceof Variable)
			assignedVariables.add((Variable)where.get(i).getTransaction());
			//End
			
			// Find Filter Expressions that can be evaluated.
			// join them to one expression with AND operator.
			// store in filtersToEvaluate [i+1]
			// its i+1 since they should be evaluated at the start
			// of the next step.

			if (singleFilters != null) {
				Iterator<FilterExpression> filterIterator = singleFilters
						.iterator();
				List<FilterExpression> filterForATuple = new ArrayList<FilterExpression>();

				// find the filters can be evaluated for where.get(i)
				// put them in an array so a single AndExpression can combine
				// all of them
				// then set filtersToEvaluate [i] as this AndExpression
				while (filterIterator.hasNext()) {
					FilterExpression singleFilter = filterIterator.next();

					if (matchFilterAndPDChange(singleFilter, assignedVariables)) {
						filterForATuple.add(singleFilter);
						filterIterator.remove();
					}
				}

				FilterExpression[] filterForATupleArray;
				if (filterForATuple.iterator().hasNext()) {

					filterForATupleArray = new FilterExpression[filterForATuple
							.size()];
					for (int k = 0; k < filterForATuple.size(); k++) {
						filterForATupleArray[k] = filterForATuple.get(k);
					}

					AndExpression andExpression = new AndExpression(
							filterForATupleArray);
					filtersToEvaluate[i] = andExpression;
				}
			}

		}// end for i

		if (Debug.isDebugging("SPARQL")) {
			for (int i = exponent - 1; i >= 0; i--) {
				Debug.println("  Tuple " + where.get(i) + " has reading kind "
						+ readFromAssignment[i] + ". has writing kind: "
						+ writeToAssignment[i], "SPARQL");
			}
		}
	}

	/**
	 * test if all variables of the single filter can be found in
	 * assignedVariables
	 */
	private boolean matchFilterAndPDChange(FilterExpression singleFilter,
			Set<Variable> assignedVariables) {

		return assignedVariables.containsAll(singleFilter.getVaraibles());
	}

	@Override
	public Iterator<PDChange<TransactionType, InstanceType, RoleType>> getInputIterator(
			int i) {

		PDChange<TransactionType, InstanceType, RoleType> currentWhereElement = where
				.get(i);
		TransactionType transaction = currentWhereElement.getTransaction();
		if (transaction == null || transaction instanceof Variable) {
			transaction = this.transactionId;
		}

		PDChange<TransactionType, InstanceType, RoleType> tempChange;
		ChangeTemplateKind changeTemplateKind = readFromAssignment[i];

		tempChange = currentWhereElement.substituteVariables(
				getVariableAssignment(), changeTemplateKind);

		// getChanges method doesn't support LINK_NOW_DELETED and LINK_NOW_ADDED
		if (tempChange.getChangeType() == ChangeType.LINK_NOW_ADDED)
			tempChange.setChangeType(ChangeType.LINK_ADDED);
		if (tempChange.getChangeType() == ChangeType.LINK_NOW_DELETED)
			tempChange.setChangeType(ChangeType.LINK_REMOVED);

		Iterator<PDChange<TransactionType, InstanceType, RoleType>> changeIterator = store
				.getChanges(tempChange).iterator();

		final int filterIndex = i;
		FilterIterator<PDChange<TransactionType, InstanceType, RoleType>> filterIterator = new FilterIterator<PDChange<TransactionType, InstanceType, RoleType>>(
				changeIterator) {
			public boolean filterCondition(
					PDChange<TransactionType, InstanceType, RoleType> change) {
				if (filtersToEvaluate[filterIndex] == null)
					return true;

				assignVariable(change, filterIndex);
				return filtersToEvaluate[filterIndex]
						.evaluate(getVariableAssignment());
			}
		};
		return filterIterator;

	}

	PDChange<TransactionType, InstanceType, RoleType> nextInProduct(int i) {
		PDChange<TransactionType, InstanceType, RoleType> next = product.get(i)
				.next();
		assignVariable(next, i);
		return next;
	}

	public void assignVariable(
			PDChange<TransactionType, InstanceType, RoleType> pdchange, int i) {
		// if (i == 0)
		// return;

		ChangeTemplateKind templateKind = ChangeTemplateKind.getKind(where
				.get(i));

		switch (templateKind) {
		case XXX:
		case XXI:
		case XRI:
		case XRX:
			// assign first variable
			getVariableAssignment().put(
					(Variable) (where.get(i).getInstance1()),
					pdchange.getInstance1());
			break;
		default:
			break;

		}
		switch (templateKind) {
		case XXX:
		case IXX:
		case IXI:
		case XXI:
			// assign role2
			getVariableAssignment().put((Variable) (where.get(i).getRole2()),
					pdchange.getRole2());
			break;
		default:
			break;

		}

		switch (templateKind) {

		case XXX:
		case IXX:
		case XRX:
		case IRX:
			// assign instance2
			getVariableAssignment().put(
					(Variable) (where.get(i).getInstance2()),
					pdchange.getInstance2());
			break;
		default:
			break;
		}
		//Adding transaction variables in variables assignment  to make them used in projection
		if(where.get(i).getTransaction() instanceof Variable)
		getVariableAssignment().put(
				(Variable) (where.get(i).getTransaction()),
				pdchange.getTransaction());
	}

	public Map<Variable, Object> getVariableAssignment() {
		return variableAssignment;
	}

	public void setVariableAssignment(Map<Variable, Object> variableAssignment) {
		this.variableAssignment = variableAssignment;
	}
}
