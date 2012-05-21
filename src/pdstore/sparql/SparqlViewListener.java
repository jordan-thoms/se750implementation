package pdstore.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.IteratorBasedCollection;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.ListenerDispatcher;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

/**   
 * A listener that will use a SPARQL Query to construct its results.
 * @author gweb017
 *
 */
public class SparqlViewListener extends PDListenerAdapter<GUID, Object, GUID> {
	
	
	private static final Variable ROLE2VAR2 = new Variable("pred");
	private static final Variable INSTANCE2VAR2 = new Variable("obj");
	private static final Variable INSTANCE1VAR2 = new Variable("subj");
	public  Variable instance1var;
	public  Variable instance2var ;
	public  Variable role2var ;
	
	Query query;
	private PDChange<GUID, Object, GUID> changeTemplate;
	private boolean instance1IsSet;
	private boolean instance2IsSet;
	private boolean role2IsSet;

	public SparqlViewListener(Variable instance1var, Variable instance2var,
			Variable role2var, Query query,
			PDChange<GUID, Object, GUID> changeTemplate) {
		super();
		this.instance1var = instance1var;
		this.instance2var = instance2var;
		this.role2var = role2var;
		this.query = query;
		this.changeTemplate = changeTemplate;
	}


	public SparqlViewListener(Query query, PDChange<GUID, Object, GUID> change) {
		this(INSTANCE1VAR2, INSTANCE2VAR2, ROLE2VAR2,
				query, change);
		this.query = query;
		this.changeTemplate = change;
		
		// Analyze the change template for the later processing:
		Object instance1 = changeTemplate.getInstance1();
		instance1IsSet = instance1!=null;
		Object instance2 = changeTemplate.getInstance2();
		instance2IsSet = instance2!=null;
		Object role2 = changeTemplate.getRole2();
		role2IsSet = role2!=null;
		
		// Three known positions seem largely pointless and are not supported by addMySelf()
		Debug.assertTrue(!(instance1IsSet||instance2IsSet||role2IsSet), "not intended to be used with three known positions");
		
	}


	public void transactionCommitted(
			List<PDChange<GUID, Object, GUID>> transaction,
			List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
			Debug.println("SparqlViewListener", "PDStore");
		for (PDChange<GUID, Object, GUID> change: matchedChanges) {
			Map<Variable, Object> variableAssignment = new HashMap<Variable, Object>();
			
			// Enforce consistency with the changeTemplate provided:
			// The variables are preset:
			Object instance1 = changeTemplate.getInstance1();
			if(instance1IsSet)
				variableAssignment.put(instance1var, instance1);
			
			Object instance2 = changeTemplate.getInstance2();
			if(instance2IsSet)
				variableAssignment.put(instance2var, instance2);
				
			Object role2 = changeTemplate.getRole2();
			if(instance2IsSet)
				variableAssignment.put(instance2var, role2);
				
			/*
			 * TODO: support transactions as variables
			 * if(changeTemplate.getTransaction()()!=null)
			 * variableAssignment.put(this.transaction,
			 * changeTemplate.getTransaction());
			 */
			if(change.getInstance1()!=null)
				variableAssignment.put(instance1var, change.getInstance1());
			if(change.getRole2()!=null)
				variableAssignment.put(role2var, change.getRole2());
			if(change.getInstance2()!=null)
				variableAssignment.put(instance2var, change.getInstance1());
			Iterator<ResultElement<GUID, Object, GUID>> resultIterator = query.execute(change.getTransaction(), variableAssignment);
			IteratorBasedCollection<ResultElement<GUID, Object, GUID>> result = 
					new IteratorBasedCollection<ResultElement<GUID, Object, GUID>>(
					resultIterator);
			for(ResultElement<GUID, Object, GUID> row : result ){
				Map<Variable, Object> rowAssignment = row.getVariableAssignment();
				transaction.add(new PDChange<GUID, Object, GUID>(change.getChangeType(), change.getTransaction(), 
						rowAssignment.get(instance1var), (GUID) rowAssignment.get(role2var), rowAssignment.get(instance2var)));		
			}
		}
	}
	
	/**
	 * Adding ViewListeners is a bit more tricky than adding other Listeners.
	 * Assume a ViewListener with a changeTemplate that has
	 * role2=studiesAt, instance2= UniAuckland,
	 * which computes all current students as instance1.
	 * 
	 * This ViewListener is as usual entered under that change template.
	 * A getChanges  request with the same change template will correctly
	 * get all students.
	 * 
	 * However, assume now a getChanges request with a changeTemplate:
	 *   null, null, null, null, UniAuckland.
	 * This will not search the index with role2=studiesAt.
	 * Hence it would not find the listener.
	 * 
	 * Unfortunately it is not generally efficient enough to 
	 * just change the matching procedure in the dispatcher.
	 * At first glance it seems enough to proceed as follows:
	 * if role = null, search not only the index for role=null,
	 * but all other indices for role=constantX.
	 * However, if there are 10000 roles entered, this is not
	 * practical anymore.
	 * 
	 * Hence the listener must be added a second time in this example,
	 * for change template null, null, null, null, UniAuckland.
	 * 
	 * Fortunately this can be limited to a second adding for the following
	 * reasons:
	 *   - Viewer with three known positions are not supported.
	 *   If there are two known positions, then only the first one requires
	 *   this treatment.
	 * 
	 * TODO: this might move to ListenerDispatcher.
	 * 
	 * @param dispatcher
	 */
	public void addMyself(ListenerDispatcher<GUID, Object, GUID> dispatcher) {
		// The usual add command that is compatible with general listeners.
		dispatcher.addListener(this, changeTemplate);

		// analyze template structure further:
		// The following code is a bit repetitive, but cannot be easily
		// generalized
		// since it deals with different fields of PDChange.
		if (instance2IsSet && instance1IsSet) {
			// instance1 needs to be added a second time
			PDChange<GUID, Object, GUID> generalizedChange = new PDChange<GUID, Object, GUID>(
					changeTemplate);
			generalizedChange.setInstance1(null);
			dispatcher.addListener(this, generalizedChange);
		}
		if (role2IsSet && (instance1IsSet || instance2IsSet)) {
			// role2 needs to be added a second time
			PDChange<GUID, Object, GUID> generalizedChange = new PDChange<GUID, Object, GUID>(
					changeTemplate);
			generalizedChange.setRole2(null);
			dispatcher.addListener(this, generalizedChange);
		}
	}



}
