package pdstore.sparql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;

public class IndexIteratorTest extends TestCase{

	PDStoreI<GUID, Object, GUID> store;
	GUID plus3;
	GUID plus6;
	GUID transactionID;
	@Before
	public void setUp() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		
		// create new store without metamodel; give it a timestamp name
		store = new PDStore("PDStoreSPARQLTest-" + dateFormat.format(date));
		setUpStore();
	}

	void setUpStore(){
	    transactionID = store.begin();
		plus3 = new GUID();
	    plus6 = new GUID();
		store.addLink(transactionID, 2L, plus3, 5L);
		store.addLink(transactionID, 3L, plus3, 6L);
		store.addLink(transactionID, 4L, plus3, 7L);
		store.addLink(transactionID, 5L, plus3, 8L);
		store.addLink(transactionID, 2L, plus6, 8L);
		transactionID = store.commit(transactionID);
	}
	
	@After
	public void tearDown() throws Exception {
	}


	/**		test (?s1, role1, ?o1), (?o1, _, _)

	 *  where:	 * added t ?x plus3 ?y, added ?y plus3 ?z
	 */
	public final void testXRX_XRX() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, plus3, varZ);

		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}
	
	/**		test  when exponent is 1 and is (?s, role, ?o)

	 *  where:	 * added t ?x plus3 ?y
	 */
	public final void testXRX() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);


		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(4, count);
	}
	
	/**		test  (subject, role, _) 


	 *  where:	 * added t 2 plus3 ?y, added 3 plus3 6
	 */
	public final void testIRX_IRI() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 2L, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, 3L, plus3, 6L);

		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}
	
	/**		test : (?s, role, object) 

	 *  where:	 * added t ?x plus3 7, added ?z plus6 8
	 */
	public final void testXRI_XRI() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, 7L);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varZ, plus6, 8L);

		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}
	
	/**		test  (?s1, role1, ?o1), (?s1, _, _)

	 *  where:	 * added t ?x plus3 ?y, added ?x plus3 6
	 */
	public final void testXRX_XRI() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, 6L);

		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}
	
	/**		test (?s1, role1, ?o1), (_, _, ?s1)

	 *  where:	 * added t ?x plus3 ?y, added ?z plus3 ?x
	 */
	public final void testXRX_XRX1() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varZ, plus3, varX);

		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}
	
	/**		test  (?s1, role1, ?o1), (_, _, ?o1)

	 *  where:	 * added t ?x plus3 ?y, added ?z plus6 ?y
	 */
	public final void testXRX_XRX2() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varZ, plus6, varY);

		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}

	/**		test (?s1, role1, ?o1), (?o1, _, _)
	
	 *  where:	 * added t ?x plus3 ?y, added ?y plus3 ?z, filter: added t ?x plus3 ?y
	 */
	public final void testXRX_XRX_F1() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, plus3, varZ);
		FilterChange<GUID, Object, GUID> filter1 = new FilterChange<GUID, Object, GUID>(
				new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY),store);
	
		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = filter1;
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}

	/**		test (?s1, role1, ?o1), (?o1, _, _)
	
	 *  where:	 * added t ?x plus3 ?y, added ?y plus3 ?z, filter: added t ?x plus6 ?z
	 */
	public final void testXRX_XRX_F2() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");

	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, plus3, varZ);
		FilterChange<GUID, Object, GUID> filter1 = new FilterChange<GUID, Object, GUID>(
				new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus6, varZ),store);
	
		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = filter1;
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(1, count);
	}

	/**		test (?s1, role1, ?o1), (?o1, _, _)
	
	 *  where:	 * added t ?x plus3 ?y, added ?y plus3 ?z, filter: added t ?x plus3 ?z
	 */
	public final void testXRX_XRX_F4() {
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		
		Variable varZ = new Variable("z");
		Variable varX = new Variable("x");
		Variable varY = new Variable("y");
	
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varY);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varY, plus3, varZ);
		FilterChange<GUID, Object, GUID> filter1 = new FilterChange<GUID, Object, GUID>(
				new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transactionID, varX, plus3, varZ),store);
	
		Query query = new Query(select, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.filter = filter1;
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		int count =0;
		while (assignmentIterator.hasNext()){
			assignmentIterator.next();
			count++;
		}
		assertEquals(0, count);
	}
	
}
