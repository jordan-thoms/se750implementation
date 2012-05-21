package pdstore.sparql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import nz.ac.auckland.se.genoupe.tools.Debug;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;
import static org.junit.Assert.*;

public class MatchTest extends TestCase {
	
	PDStoreI<GUID, Object, GUID> store;
	
	@Before
	public void setUp() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		store = new PDStore("PDSparqlTest-" + dateFormat.format(date));
	}

	@After
	public void tearDown() throws Exception {
	}

	/**tuples in where clause: (1) reading kind XRI. writing kind: IRI (2)
	 * reading kind IRI. writing kind: XRI
	 * input: removed t Ernie hasBrother Bert,removed t Ernie likes
	 * donuts where: removed t ?x hasBrother Bert, removed t ?x likes donuts
	 */
	public final void testXRI_XRI() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Ernie", likes,
				"Donuts");
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);

		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, varX, hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, varX, likes, "Donuts");

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");

	}

	/**tuples in where clause: (1) reading kind IRX. writing kind: XRI (2)
	 * reading kind IRI. writing kind: XRI  input: added t Ernie hasBrother Bert, removed t Bert likes donuts
	 * where: added t ?x hasBrother ?y, removed t ?y likes donuts
	 */
	public final void testXRY_YRI() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Bert", likes, "Donuts");

		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasBrother, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, varY, likes, "Donuts");

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);

		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
		Debug.assertTrue(results.contains("Bert"), "should return Bert.");
	}

	/**
	 *  input: added t Ernie hasBrother Bert, added t Ernie likes donuts,
	 * added t Ernie hasBrother Bob where: added t ?x hasBrother Bert, added t
	 * ?x likes donuts, added t ?x hasBrother Bob
	 */
	public final void testXRI_XRI_XRI() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		GUID transactionID =store.begin();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", likes, "Donuts");
		PDChange<GUID, Object, GUID> l3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bob");

		store.addChange(l1);
		store.addChange(l2);
		store.addChange(l3);
		transactionID = store.commit(transactionID);
		
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasBrother, "Bert");
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, likes, "Donuts");
		PDChange<GUID, Object, GUID> w3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasBrother, "Bob");

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.where.add(w3);

		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
	}

	/**
	 *input: added t Ernie hasBrother Bert, added t Bert likes donuts,
	 * removed t donuts isCookedBy Bob where: added t ?x hasBrother ?y, added t
	 * ?y likes ?z, removed t ?z isCookedBy Bob
	 */
	public final void testXRY_YRZ_ZRI() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		GUID transactionID = store.begin();
		GUID isCookedBy = new GUID();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Bert", likes, "Donuts");
		PDChange<GUID, Object, GUID> l3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Donuts", isCookedBy,
				"Bob");
		store.addChange(l1);
		store.addChange(l2);
		store.addChange(l3);
		transactionID = store.commit(transactionID);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasBrother, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, likes, varZ);
		PDChange<GUID, Object, GUID> w3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, varZ, isCookedBy, "Bob");

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.where.add(w3);
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
		Debug.assertTrue(results.contains("Bert"), "should return Bert.");
		Debug.assertTrue(results.contains("Donuts"), "should return Donuts.");
	}

	/**
	 * test equal filter transactionID as variable input: added t 2
	 * role2 5 where: added ?t ?x role2 5 filter ?t == transactionID
	 */
	public final void testXRI_F() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		GUID role2 = new GUID();
		int a = 5;
		int b = 2;
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, b, role2, a);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, a);
		
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new EqualExpression(varT, transactionID);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains(b), "should return 2.");
	}

	/**
	 * case16: test equal filter , comparing 2 variables input: added t 2 role2
	 * 5, added t 3 role2 5 where: added t ?x role2 5, added t ?y role2 5 filter
	 * t ?x = ?y should return null
	 */
	public final void testXRI_YRI_F() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
	
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varY, role2, 5);
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = new EqualExpression(varT, new GUID());
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 * test notEqual filter transactionID as variable input: added t 2
	 * role2 5 where: added ?t ?x role2 5 filter ?t != new GUID(), ?x != 4
	 * 
	 */
	public final void testXRI_FF() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new AndExpression(new NotEqualExpression(varT,
				new GUID()), new NotEqualExpression(varX, 4));
	
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
	}

	/**
	 *  test lessThan filter input: added t 2 role2 5 where: added t ?x
	 * role2 5 filter t ?x < 3
	 */
	public final void testXRI_F1() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new LessThanExpression(varX, 3);
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
	}

	/**
	 *  test lessThan filter input: added t 2 role2 5 where: added t ?x
	 * role2 5 filter t ?x < 0.1 should return null
	 */
	public final void testXRI_F2() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new LessThanExpression(varX, 0.1);
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 * test lessThan filter input: added t 2 role2 5 where: added ?t ?x
	 * role2 5 filter t ?t < new GUID(), ?x < 4
	 */
	public final void testXRI_FF1() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new AndExpression(new LessThanExpression(varT,
				new GUID()), new LessThanExpression(varX, 4));
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
	}

	/**
	 *  test lessThan filter input: added t 2 role2 5 where: added ?t ?x
	 * role2 5 filter t ?t < new GUID(), ?x < 2 should return null
	 */
	public final void tesXRI_FF() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new AndExpression(new LessThanExpression(varT,
				new GUID()), new LessThanExpression(varX, 2));
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 *  test lessThan filter input: added t 2 role2 5 where: added ?t ?x
	 * role2 5 filter t new GUID() < ?t should return null
	 */
	public final void testXRI_F3() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new LessThanExpression(new GUID(), varT);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 *test greaterThan filter input: added t 2 role2 5 where: added ?t
	 * ?x role2 5 filter t ?t > new GUID() should return null
	 */
	public final void testXRI_FF2() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new GreaterThanExpression(varT, new GUID());
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 * case24: test greaterThan filter input: added t 2 role2 5 where: added ?t
	 * ?x role2 5 filter t new GUID() > ?t, 4 > ?x
	 */
	public final void testXRI_FF3() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.filter = new AndExpression(new GreaterThanExpression(new GUID(),
				varT), new GreaterThanExpression(4, varX));
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
	}
	

	/**
	 * case25: test filter input: added t 2 role2 5 , added t 3 role2 6 where:
	 * added ?t ?x role2 5, added t ?y role2 ?z filter: t new GUID() > ?t, 4 >
	 * ?x, ?z!= 4, ?y < 4, ?y > ?x
	 */
	public final void testXRI_YRZ_FFFF(){
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3, role2, 6);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, role2, varZ);
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = new AndExpression(new GreaterThanExpression(new GUID(),
				varT), new GreaterThanExpression(4, varX),
				new NotEqualExpression(varZ, 4),
				new LessThanExpression(varY, 4), new GreaterThanExpression(
						varY, varX));
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
		Debug.assertTrue(results.contains(6), "should return 6.");
		Debug.assertTrue(results.contains(3), "should return 3.");
	}

	/**
	 * test filter input: added t 2 role2 5 , added t 3 role2 6 where:
	 * added ?t ?x role2 5, added t ?y role2 ?z filter: t new GUID() > ?t, 4 >
	 * ?x, ?z!= 4, ?y < 4, ?y > ?z should return null (y is less than z)
	 */
	public final void testXRI_YRZ_FFFF1() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3, role2, 6);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, role2, varZ);
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = new AndExpression(new GreaterThanExpression(new GUID(),
				varT), new GreaterThanExpression(4, varX),
				new NotEqualExpression(varZ, 4),
				new LessThanExpression(varY, 4), new GreaterThanExpression(
						varY, varZ));

		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 * case27: test filter input: added t 2 role2 5 , added t 3 role2 6 where:
	 * added ?t ?x role2 5, added t ?y role2 ?z filter: ?x > 3 or ?y > 2
	 */
	public final void testXRI_YRZ_FF() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3, role2, 6);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, role2, varZ);
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = new OrExpression(new GreaterThanExpression(varX, 3),
				new GreaterThanExpression(varY, 2));
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
		Debug.assertTrue(results.contains(6), "should return 6.");
		Debug.assertTrue(results.contains(3), "should return 3.");
	}

	/**
	 * case28: test filter input: added t 2 role2 5 , added t 3 role2 6 where:
	 * added ?t ?x role2 5, added t ?y role2 ?z filter: ?x >3 or ?y > 3
	 */
	public final void testXRI_YRZ_FF1() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3, role2, 6);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, role2, varZ);
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = new OrExpression(new GreaterThanExpression(varX, 3),
				new GreaterThanExpression(varY, 3));
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");
	}

	/**
	 * case29: test filter input: added t 2 role2 5 , added t 3 role2 6 where:
	 * added ?t ?x role2 5, added t ?y role2 ?z filter: (?x >3 or ?y > 3) and ?z
	 * < 9
	 * 
	 * filter: (?x >3 or ?y > 3) and ?z > 9
	 * 
	 * filter: (?x >3 or ?y > 3) and (?z > 9 or ?x != 9)
	 */
	public final void testF() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID role2 = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		Variable varT = new Variable("t");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2, role2, 5);
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3, role2, 6);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, role2, 5);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, role2, varZ);
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = new AndExpression(new OrExpression(
				new GreaterThanExpression(varX, 3), new GreaterThanExpression(
						varY, 3), new LessThanExpression(varZ, 9)));
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
		Debug.assertTrue(results.contains(6), "should return 6.");
		Debug.assertTrue(results.contains(3), "should return 3.");

		query.filter = new AndExpression(new OrExpression(
				new GreaterThanExpression(varX, 3), new GreaterThanExpression(
						varY, 3), new GreaterThanExpression(varZ, 9)));
		 assignmentIterator = query.execute(null);

		Debug.assertTrue(!assignmentIterator.hasNext(), "should return nothing.");

		query.filter = new AndExpression(new OrExpression(
				new GreaterThanExpression(varX, 3), new GreaterThanExpression(
						varY, 3), new OrExpression(new GreaterThanExpression(
						varZ, 9), new NotEqualExpression(varX, 9))));
		assignmentIterator = query.execute(null);

		results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains(2), "should return 2.");
		Debug.assertTrue(results.contains(6), "should return 6.");
		Debug.assertTrue(results.contains(3), "should return 3.");
	}

	/**
	 *  input: added t Ernie hasBrother Bert where: added t ?x ?y Bert
	 */
	public final void testXYI() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID hasBrother = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, varY, "Bert");
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
		}
		Debug.assertTrue(results.contains(hasBrother), "should return hasBrother.");
	}

	/**
	 *  input: added t Ernie hasBrother Bert where: added t ?x ?y ?z
	 */
	public final void testXYZ() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID hasBrother = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, varY, varZ);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
		Debug.assertTrue(results.contains(hasBrother), "should return hasBrother.");
		Debug.assertTrue(results.contains("Bert"), "should return Bert.");
	}

	/**
	 *  input: added t Ernie hasBrother Bert where: added t "Ernie" ?y ?z
	 */
	public final void testIYZ() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID hasBrother = new GUID();
		GUID transactionID = store.begin();
		Variable varY = new Variable("y");
		Variable varZ = new Variable("z");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", varY, varZ);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varY));
			results.add(result.get(varZ));
		}
		Debug.assertTrue(results.contains(hasBrother), "should return hasBrother.");
		Debug.assertTrue(results.contains("Bert"), "should return Bert.");
	}
	
	/**
	 *input: added t Ernie hasBrother Bert where: added t ?x hasBrother
	 * null
	 * 
	 */
	public final void testXRnull() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID hasBrother = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasBrother, null);
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
	}

	/**
	 *  input: added t Ernie hasBrother Bert where: added t2 ?x null Bert
	 */
	public final void testXnullI() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;
		
		GUID hasBrother = new GUID();
		GUID transactionID = store.begin();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, null, varX, null, "Bert");
		store.addChange(l1);
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
	}
	
	/**
	 * : test when transaction ids in input and where-clause are different
	 * input: removed t Ernie hasBrother Bert,removed t Ernie likes
	 * donuts where: removed t ?x hasBrother Bert, removed t ?x likes donuts
	 * 
	 * 
	 */
	public final void testRetrievingHistoricalData() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		Variable varX = new Variable("x");
		GUID transactionID = store.begin();
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED,transactionID, "Ernie", likes,
				"Donuts");
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		transactionID = store.begin();
		transactionID = store.commit(transactionID);
		query = new Query(select, where, null, null, store);
		
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, varX, hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, varX, likes, "Donuts");
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");

	}
	
	/**
	 * case34: 
	 * input: LINK_REMOVED t Ernie hasBrother Bert,LINK_REMOVED t Ernie likes
	 * donuts where: LINK_NOW_DELETED t ?x hasBrother Bert, LINK_NOW_DELETED t ?x likes donuts
	 * 
	 * 
	 */
	public final void testMatch34() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		GUID transactionID = store.begin();
		
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_REMOVED, transactionID, "Ernie", likes,
				"Donuts");
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);
		
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_NOW_DELETED, transactionID, varX, hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_NOW_DELETED, transactionID, varX, likes, "Donuts");

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
	}
	
	/**
	 * case34: 
	 * input: LINK_ADDED t Ernie hasBrother Bert,LINK_ADDED t Ernie likes
	 * donuts where: LINK_NOW_ADDED t ?x hasBrother Bert, LINK_NOW_ADDED t ?x likes donuts
	 * 
	 * 
	 */
	public final void testMatch35() {
		Query query;
		List<Variable> select;
		List<PDChange<GUID, Object, GUID>> where;

		GUID hasBrother = new GUID();
		GUID likes = new GUID();
		Variable varX = new Variable("x");
		select = new ArrayList<Variable>();
		where = new ArrayList<PDChange<GUID, Object, GUID>>();
		GUID transactionID = store.begin();
		
		
		PDChange<GUID, Object, GUID> l1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> l2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, "Ernie", likes,
				"Donuts");
		store.addChange(l1);
		store.addChange(l2);
		transactionID = store.commit(transactionID);

		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_NOW_ADDED, transactionID, varX, hasBrother,
				"Bert");
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_NOW_ADDED, transactionID, varX, likes, "Donuts");

		query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(
				null);

		List<Object> results = new ArrayList<Object>();
		while (assignmentIterator.hasNext()) {
			ResultElement<GUID, Object, GUID> result = assignmentIterator.next();
			results.add(result.get(varX));
		}
		Debug.assertTrue(results.contains("Ernie"), "should return Ernie.");
	}

}
