package pdstore.sparql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;
import nz.ac.auckland.se.genoupe.tools.FilterIterator;

public class SubQueryTest extends TestCase {
	PDStoreI<GUID, Object, GUID> store = new PDStore("PDStoreSPARQLTest"
			+ Math.random());
	GUID hasName, hasAge, phoneNo, likes, livesin, akl, hml;
	private Variable varZ;
	private Variable varX;
	private Variable varY;
	private Variable varT;
	private Variable varA;
	private Variable varB;
	private Variable varC;

	@Before
	public void setUp() throws Exception {
		hasName = new GUID("9c1d831183ef11dfbce200217029671e");
		hasAge = new GUID("9c1d831183ef11dfbce200217029671f");
		phoneNo = new GUID("6c1d831083ef11dfbce200297029675e");
		livesin = new GUID("6c1d831083ef11dfbce200217029675f");
		likes = new GUID("6c1d831083ef11dfbce200217029675e");
		akl = new GUID("6c1d831083ef11dfbce200217349675f");
		hml = new GUID("9c1d831183ef11dfbce200217029637f");
		varZ = new Variable("z");
		varX = new Variable("x");
		varY = new Variable("y");
		varT = new Variable("t");
		varA = new Variable("a");
		varB = new Variable("b");
		varC = new Variable("c");
	}

	@After
	public void tearDown() throws Exception {
	}

	public final void testsimpleSubquery_test() {

		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		List<PDChange<GUID, Object, GUID>> SubQuery1_1 = new ArrayList<PDChange<GUID, Object, GUID>>();
		List<PDChange<GUID, Object, GUID>> SubQuery1_2 = new ArrayList<PDChange<GUID, Object, GUID>>();

		GUID transactionID = store.begin();
		store.addLink(transactionID, "p1", hasName, "Becky Smith");
		store.addLink(transactionID, "p2", hasName, "Sarah Jones");
		store.addLink(transactionID, "p3", hasName, "John Smith");
		store.addLink(transactionID, "p4", hasName, "Matt Jones");
		store.addLink(transactionID, "p4", hasAge, 23);
		store.addLink(transactionID, "p3", hasAge, 25);
		store.addLink(transactionID, "p1", hasAge, 21);
		store.addLink(transactionID, "p3", livesin, "akl");
		store.addLink(transactionID, "p4", livesin, "akl");

		transactionID = store.commit(transactionID);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasName, varY);
		Query query1 = new Query(select, where, null, null, store);
		where.add(w1);
		
		PDChange<GUID, Object, GUID> sw1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasAge, varZ);
		Query query2 = new Query(select, SubQuery1_1, null, null, store);
		SubQuery1_1.add(sw1);
		
		PDChange<GUID, Object, GUID> sw2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasName, varY);
		
		Query query3 = new Query(select, SubQuery1_2, null, null, store);
		SubQuery1_2.add(sw2);
		
		Query mergeQuery= new Query(query1,query2,query3);
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = mergeQuery
				.execute(null);
		int count = 0;
		while (assignmentIterator.hasNext()) {
			assignmentIterator.next();
			count++;
		}
		assertEquals(3, count);

	}
	
	public final void testSubQueryfilter_test()
	{


		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		List<PDChange<GUID, Object, GUID>> SubQuery1_1 = new ArrayList<PDChange<GUID, Object, GUID>>();

		GUID transactionID = store.begin();
		store.addLink(transactionID, "p1", hasName, "Becky Smith");
		store.addLink(transactionID, "p2", hasName, "Becky Smith");
		store.addLink(transactionID, "p3", hasName, "John Smith");
		store.addLink(transactionID, "p4", hasName, "Matt Jones");
		store.addLink(transactionID, "p4", hasAge, 23);
		store.addLink(transactionID, "p3", hasAge, 25);
		store.addLink(transactionID, "p1", hasAge, 21);
		store.addLink(transactionID, "p3", livesin, "akl");
		store.addLink(transactionID, "p4", livesin, "akl");

		transactionID = store.commit(transactionID);
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasName, varY);
		FilterExpression fe = new EqualExpression(varY, "Becky Smith");
		Query query1 = new Query(select, where, fe, null, store);
		where.add(w1);
		
		PDChange<GUID, Object, GUID> sw1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, hasAge, varZ);
		FilterExpression fe1 = new EqualExpression(varX, "p1");
		Query query2 = new Query(select, SubQuery1_1, fe1, null, store);
		SubQuery1_1.add(sw1);
		
		Query mergeQuery= new Query(query1,query2);
		
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = mergeQuery
				.execute(null);
		int count = 0;
		while (assignmentIterator.hasNext()) {
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}
	
	public final void testSubQueryOptional_test()
	{List<Variable> select = new ArrayList<Variable>();
	List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
	List<PDChange<GUID, Object, GUID>> subwhere = new ArrayList<PDChange<GUID, Object, GUID>>();
	List<PDChange<GUID, Object, GUID>> optionalWhere1 = new ArrayList<PDChange<GUID, Object, GUID>>();
	List<Query> lstoptional = new ArrayList<Query>();

	GUID transactionID = store.begin();
	store.addLink(transactionID, "p1", hasName, "Becky Smith");
	store.addLink(transactionID, "p2", hasName, "Sarah Jones");
	store.addLink(transactionID, "p3", hasName, "John Smith");
	store.addLink(transactionID, "p4", hasName, "Matt Jones");
	store.addLink(transactionID, "p4", hasAge, 23);
	store.addLink(transactionID, "p3", hasAge, 25);
	store.addLink(transactionID, "p1", hasAge, 21);
	store.addLink(transactionID, "p3", livesin, "akl");
	store.addLink(transactionID, "p4", livesin, "akl");

	transactionID = store.commit(transactionID);
	
	PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
			ChangeType.LINK_ADDED, transactionID, varX, hasName, varY);
	Query query1 = new Query(select, where, null, null, store);
	where.add(w1);
	
	PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
			ChangeType.LINK_ADDED, transactionID, varX, hasAge, varZ);
	Query subquery1 = new Query(select, subwhere, null, lstoptional, store);
	subwhere.add(w2);
	
	PDChange<GUID, Object, GUID> ow2 = new PDChange<GUID, Object, GUID>(
			ChangeType.LINK_ADDED, transactionID, varX, livesin, varA);
	
	Query optional1 = new Query(select, optionalWhere1, null, null, store);
	optionalWhere1.add(ow2);
	lstoptional.add(optional1);
	List<Query> lstfinal = new ArrayList<Query>();
	lstfinal.add(query1);
	lstfinal.add(subquery1);
	
	Query mergeQuery = new Query(lstfinal.toArray(new Query[lstfinal.size()-1]));
	
	Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = mergeQuery
			.execute(null);
	int count = 0;
	while (assignmentIterator.hasNext()) {
		assignmentIterator.next();
		count++;
	}
	assertEquals(3, count);
}
}
