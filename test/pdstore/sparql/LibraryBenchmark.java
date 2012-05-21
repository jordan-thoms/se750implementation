package pdstore.sparql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

import org.junit.After;
import org.junit.Before;

import benchmark.CreateLibraryBenchmark;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;

/**
 * class LibraryBenchmark is to evaluate the performance of data which is
 * created by class CreateLibraryBenchmark
 * 
 * the tests print out the statistical analysis of the queries
 * 
 * It takes 380M memory to load a dataset which contains 55k tuples. In 
 *  32 bit system, to load 55k tuples safely, at least 512M memory should be allocated 
 *  to java VM. To do that, in "Run configurations" -> "Arguments"->"VM arguments", type in
 *  "-Xmx512m"
 *  
 * a 64 bit system with 8G memory can successfully load 111k tuples with default setting.
 */
public class LibraryBenchmark extends TestCase {

	PDStoreI<GUID, Object, GUID> store = new PDStore("PDStoreLibrary");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		System.gc();
		long mem0 = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println(" Last run has memory consumption of " + mem0);

	}

	/**
	 * This query is query 1 in Mark's thesis
	 * 
	 * testPDStoreLibrary1 is to test a single triple query
	 * 
	 * testing PDStoreLibrary query: select ?books where {General library, CS,
	 * ?books}
	 */

	public final void testIRX() {
		Variable varT = new Variable("t");
		Variable varBooks = new Variable("books");
		Query query;

		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, "General library", CreateLibraryBenchmark.CS_ROLEID, varBooks);
		where.add(w1);
		query = new Query(null, where, null, null, store);

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		
		assertTrue(assignmentIterator.hasNext());
		while(assignmentIterator.hasNext()){
			assignmentIterator.next();
		}
		stopwatch.stop();
		System.out.println("execution time is " + stopwatch.nanoSeconds()
				/ 1000000 + " miliseconds");
		System.out.println(query.getStatistics());
	}

	/**
	 * This query is query 2 in Mark's thesis
	 * 
	 * testPDStoreLibrary2 is to test a two-triple query. both objects of the
	 * triples are the variable ?book. this test is to see the execution speed
	 * when the query has shared variable in both of the triples.
	 * 
	 * This is the case of (XRX, IRX)
	 * 
	 * testing PDStoreLibrary query: select ?person ?books where {?person
	 * borrowedBook ?books . "General library", CS, ?books}
	 */

	public final void testXRX_IRX() {
		Variable varBooks = new Variable("books");
		Variable varPerson = new Variable("person");
		Variable varT = new Variable("t");
		Query query;

		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varPerson, CreateLibraryBenchmark.BORROW_ROLEID, varBooks);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, "General library", CreateLibraryBenchmark.CS_ROLEID, varBooks);
		query = new Query(null, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		assertTrue(assignmentIterator.hasNext());
		while(assignmentIterator.hasNext()){
			assignmentIterator.next();
		}
		stopwatch.stop();
		System.out.println("execution time is " + stopwatch.nanoSeconds()
				/ 1000000 + " miliseconds");

		System.out.println(query.getStatistics());
	}

	/**
	 * This query is query 3 in Mark's thesis
	 * 
	 * testPDStoreLibrary3 is to test a two-triple query. The variable ?library
	 * is the object of the first triple and the subject of the second triple.
	 * this test is to see the execution speed when the query has shared
	 * varialbe in both of the triples.
	 * 
	 * case of (XRX, XRI)
	 * 
	 * testing PDStoreLibrary query: select ?person ?books where {?person
	 * worksFor ?library . ?library CS "(random string)"}
	 */

	public final void testXRX_XRI() {
		Variable varLibrary = new Variable("library");
		Variable varPerson = new Variable("person");
		Variable varT = new Variable("t");
		Query query;

		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varPerson, CreateLibraryBenchmark.WORKSFOR_ROLEID, varLibrary);
		PDChange<GUID, Object, GUID> w2;
		Iterator<PDChange<GUID, Object, GUID>> i = store.getChanges(
				"General library", CreateLibraryBenchmark.CS_ROLEID).iterator();
		if (i.hasNext()) {
			w2 = new PDChange<GUID, Object, GUID>(ChangeType.LINK_ADDED,
					new Variable("vart"), varLibrary, CreateLibraryBenchmark.CS_ROLEID, i.next()
					.getInstance2().toString());
		}
		//else set up a random string
		else {
			w2 = new PDChange<GUID, Object, GUID>(ChangeType.LINK_ADDED, varT,
					varLibrary, CreateLibraryBenchmark.CS_ROLEID, "ti ta h");
		}
		query = new Query(null, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		assertTrue(assignmentIterator.hasNext());
		while(assignmentIterator.hasNext()){
			assignmentIterator.next();
		}
		stopwatch.stop();
		System.out.println("execution time is " + stopwatch.nanoSeconds()
				/ 1000000 + " miliseconds");
		System.out.println(query.getStatistics());
	}

	/**
	 * This query is query 4 in Mark's thesis
	 * 
	 * testPDStoreLibrary4 is to test a three-triple query. the triples are
	 * linked by shared variables
	 * 
	 * case of (IRX, XRX, XRI)
	 * 
	 * testing PDStoreLibrary query: select ?person ?books where {
	 * "General library", CS, ?books . ?person borrowedBook ?books . ?person
	 * hasAge 30}
	 */

	public final void testIRX_XRX_XRI() {
		Variable varBooks = new Variable("books");
		Variable varPerson = new Variable("person");
		Variable varT = new Variable("t");
		Query query;

		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, "General library", CreateLibraryBenchmark.CS_ROLEID, varBooks);
		PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varPerson, CreateLibraryBenchmark.BORROW_ROLEID, varBooks);
		PDChange<GUID, Object, GUID> w3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varPerson, CreateLibraryBenchmark.HASAGE_ROLEID, 30L);
		query = new Query(null, where, null, null, store);
		query.where.add(w1);
		query.where.add(w2);
		query.where.add(w3);
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		assertTrue(assignmentIterator.hasNext());
		while(assignmentIterator.hasNext()){
			assignmentIterator.next();
		}
		stopwatch.stop();
		System.out.println("execution time is " + stopwatch.nanoSeconds()
				/ 1000000 + " miliseconds");
		System.out.println(query.getStatistics());
	}

	/**
	 * This query is query 5 in Mark's thesis
	 * 
	 * testPDStoreLibrary3 is to test a two-triple query with a filter.
	 * 
	 * case of (XRX, XRX)
	 * 
	 * testing PDStoreLibrary query: select ?person ?books where { ?person
	 * borrowedBook ?books . ?person hasAge ?age filter (?age = 30)}
	 */

	public final void testXRX_XRX_F() {
		Variable varAge = new Variable("age");
		Variable varBooks = new Variable("books");
		Variable varPerson = new Variable("person");
		Variable varT = new Variable("t");
		Query query;

		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varPerson, CreateLibraryBenchmark.BORROW_ROLEID, varBooks);
		PDChange<GUID, Object, GUID> w3 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varPerson, CreateLibraryBenchmark.HASAGE_ROLEID, varAge);
		query = new Query(null, where, null, null, store);
		query.where.add(w1);
		query.where.add(w3);
		query.filter = new AndExpression(new EqualExpression(varAge, 30L));
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query.execute(null);
		assertTrue(assignmentIterator.hasNext());
		while(assignmentIterator.hasNext()){
			assignmentIterator.next();
		}
		stopwatch.stop();
		System.out.println("execution time is " + stopwatch.nanoSeconds()
				/ 1000000 + " miliseconds");
		System.out.println(query.getStatistics());
	}

}
