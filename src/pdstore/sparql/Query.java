package pdstore.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.FilterIterator;

import pdstore.GUID;

import pdstore.generic.PDChange;

import pdstore.generic.PDStoreI;

public class Query {

	protected PDStoreI<GUID, Object, GUID> store;
	List<Variable> select;
	List<PDChange<GUID, Object, GUID>> where;
	FilterExpression filter;
	List<Query> optionals;
	private Iterator<ResultElement<GUID, Object, GUID>> cartesianIterator;
	private OptionalIterator<GUID, Object, GUID> optionalIterator;
	private Query finalQuery;

	/**
	 * 
	 * Standard semantics of query execution is:
	 * 
	 * 1. equijoin, natural join Result: Net of Links Possible further
	 * decomposition: 1.1. Cartesian product 1.2. Exclude tuples not joining
	 * 
	 * 2. step: apply filters (in relational algebra: selection)
	 * 
	 * 3. step projection means apply the select clause.
	 * 
	 * select ?name where ?person gender "male" ?person hasChild "Pat" ?person
	 * lastName ?name
	 * 
	 * 
	 * select ?person where ?person gender "male" ?person hasChild "Pat"
	 * 
	 * @param select
	 * @param where
	 * @param store
	 *            TODO
	 */
	public Query(List<Variable> select,
			List<PDChange<GUID, Object, GUID>> where, FilterExpression filter,
			List<Query> optionals, PDStoreI<GUID, Object, GUID> store) {
		Debug.assertTrue(store != null, "The store must not be null");
		if (select == null)
			this.select = new ArrayList<Variable>();
		else
			this.select = select;
		this.where = where;
		this.filter = filter;
		this.optionals = optionals;
		this.store = store;
	}

	/**
	 * @param queries
	 * This constructor is called when we have sub queries to be executed.
	 * This constructor merges all queries to a single query and return a single query object.
	 */
	public Query(Query... queries) {
		this(null, null, null, null,queries[0].store);//store is common.We Don't use distribute architecture now.
		merge(queries);
	}

	/**
	 * @param queries
	 * This is used merge all sub queries into the main query.
	 */
	public void merge(Query... queries) {

		for (int loopcnt = queries.length - 1; loopcnt >= 0; loopcnt--) {
			
			if (queries[loopcnt].select.size() > 0)
				this.select.addAll(queries[loopcnt].select);
			//this is looped to add only the where pdchange's in sub query that is not already 
			//contained in the main query or other sub queries.
			if(this.where!=null)
			for(PDChange<GUID,Object, GUID> p: queries[loopcnt].where)
			{
				
				if(!this.where.contains(p))
				{
					this.where.add(p);
				}
			}
			else
				this.where=queries[loopcnt].where;
			
			if (queries[loopcnt].filter != null) {
				if(this.filter!=null)
				{
				FilterExpression fe = new AndExpression(
						this.filter, queries[loopcnt].filter);
				this.filter = fe;
				}
				else
					this.filter=queries[loopcnt].filter;	
			}
			if (queries[loopcnt].optionals != null)
			{
				if(this.optionals != null){
				this.optionals
						.addAll(queries[loopcnt].optionals);
				}
				else
				{
					this.optionals =queries[loopcnt].optionals;
				}
			}
		}
	}

	public void reorderOptionals() {
		double totalvariable = 0;
		double resolvedVariable = 0;
		double probability = 0;
		Set<Variable> resolveOptional = new HashSet<Variable>();
		Set<Variable> resolveAll = new HashSet<Variable>();
		List<Set<Variable>> lstresovedOptionals = new ArrayList<Set<Variable>>();
		for (PDChange<GUID, Object, GUID> w : this.where) {
			if (w.getInstance1() instanceof Variable
					&& !resolveAll.contains((Variable) w.getInstance1()))
				resolveAll.add((Variable) w.getInstance1());

			if (w.getRole2() instanceof Variable
					&& !resolveAll.contains((Variable) w.getRole2()))
				resolveAll.add((Variable) w.getRole2());

			if (w.getInstance2() instanceof Variable
					&& !resolveAll.contains((Variable) w.getInstance2()))
				resolveAll.add((Variable) w.getInstance2());
		}

		for (int optcnt = this.optionals.size() - 1; optcnt >= 0; optcnt--) {
			double minProbabilitySoFar = 1;
			int indexOfMinComplexity = 0;
			resolveOptional.clear();
			for (int j = optcnt; j >= 0; j--) {
				resolveOptional.clear();
				Query queryOptional = optionals.get(j);
				for (PDChange<GUID, Object, GUID> w : queryOptional.where) {

					if (w.getInstance1() instanceof Variable) {
						totalvariable++;
						if (resolveAll.contains((Variable) w.getInstance1()))
							resolvedVariable++;
						else
							resolveOptional.add((Variable) w.getInstance1());
					}
					if (w.getRole2() instanceof Variable) {
						totalvariable++;
						if (resolveAll.contains((Variable) w.getRole2()))
							resolvedVariable++;
						else
							resolveOptional.add((Variable) w.getRole2());
					}
					if (w.getInstance2() instanceof Variable) {
						totalvariable++;
						if (resolveAll.contains((Variable) w.getInstance2()))
							resolvedVariable++;
						else
							resolveOptional.add((Variable) w.getInstance2());
					}
				}
				// calculate the resolving value
				if (totalvariable == 0)
					probability = 0;
				else if (resolvedVariable == 0)
					probability = 1;
				else
					probability = resolvedVariable / totalvariable;

				if (probability < minProbabilitySoFar) {
					minProbabilitySoFar = probability;
					indexOfMinComplexity = j;
				}
				lstresovedOptionals.add(resolveOptional);
			}
			resolveAll.addAll(lstresovedOptionals.get(indexOfMinComplexity));
			lstresovedOptionals.clear();
			Query tempOptional = this.optionals.get(optcnt);
			this.optionals
					.set(optcnt, this.optionals.get(indexOfMinComplexity));
			this.optionals.set(indexOfMinComplexity, tempOptional);
		}
	}

	public Query(List<Variable> select,
			List<PDChange<GUID, Object, GUID>> where, FilterExpression filter,
			PDStoreI<GUID, Object, GUID> store) {
		this(select, where, filter, new ArrayList<Query>(), store);
	}

	public Query(List<Variable> select,
			List<PDChange<GUID, Object, GUID>> where,
			PDStoreI<GUID, Object, GUID> store) {
		this(select, where, null, store);
	}

	/**
	 * Creates a query object from a SPARQL query string.
	 * 
	 * @param queryString
	 */
	public Query(String queryString) {
		// TODO: not implemented yet
		throw new UnsupportedOperationException();
	}

	/**
	 * Gives the SPARQL query as a formatted String.
	 */
	@Override
	public String toString() {
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT" + " ");
		if (select.size() == 0)
			queryString.append("*");
		else {
			for (Variable variable : select) {
				queryString.append(variable.toString() + " ");
			}
		}
		queryString.append("\r\n");
		queryString.append("WHERE" + "\r\n");
		queryString.append("{" + "\r\n");
		for (PDChange<GUID, Object, GUID> change : where) {

			queryString.append(change.getChangeType() + " "
					+ change.getTransaction().toString() + " "
					+ change.getInstance1().toString() + " "
					+ change.getRole2().toString() + " "
					+ change.getInstance2().toString() + " . ");

		}
		if (optionals.size() != 0) {
			queryString.append("\r\n");
			queryString.append("OPTIONAL { ");
			for (Query optional : optionals) {
				for (PDChange<GUID, Object, GUID> change : optional.where) {
					queryString.append(change.getChangeType() + " "
							+ change.getTransaction().toString() + " "
							+ change.getInstance1().toString() + " "
							+ change.getRole2().toString() + " "
							+ change.getInstance2().toString() + " . ");
				}
			}
			queryString.append("}" + "\r\n");
		}
		if (filter != null) {
			queryString.append("\r\n");
			queryString.append("FILTER  " + filter.toString());
		}
		queryString.append("\r\n" + "}");
		return queryString.toString();
	}

	public Iterator<ResultElement<GUID, Object, GUID>> execute(
			GUID transactionId) {
		return execute(transactionId, null);
	}

	@SuppressWarnings("unchecked")
	public Iterator<ResultElement<GUID, Object, GUID>> execute(
			GUID transactionId, Map<Variable, Object> incomingAssignment) {
		Debug.println("Executing query...", "SPARQL");

		// TODO: Separate index iterator from optional Iterator
		cartesianIterator = new CartesianIndexIterator<GUID, Object, GUID>(
				store, transactionId, where, this.filter, incomingAssignment);
		if (this.optionals == null)
			return cartesianIterator;

		reorderOptionals();
		return new OptionalIterator<GUID, Object, GUID>(
				(CartesianIndexIterator<GUID, Object, GUID>) cartesianIterator,
				this);
	}

	@SuppressWarnings("unchecked")
	public String getStatistics() {
		return ((CartesianIndexIterator<GUID, Object, GUID>) cartesianIterator)
				.getStatistics();
	}
}
