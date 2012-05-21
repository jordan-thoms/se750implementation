package BerlinSparqlBenchMark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.auckland.se.genoupe.tools.Stopwatch;

import org.junit.After;
import org.junit.Before;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDStoreI;
import pdstore.sparql.EqualExpression;
import pdstore.sparql.FilterExpression;
import pdstore.sparql.GreaterThanExpression;
import pdstore.sparql.Query;
import pdstore.sparql.ResultElement;
import pdstore.sparql.Variable;
import junit.framework.TestCase;

public class BerlinSparqlBenchmark_ScaleFacor_666 extends TestCase {
	// Out of 12 BSBM queries only two queries are used here
	// BSBM queries available at
	// http://www4.wiwiss.fu-berlin.de/bizer/BerlinSPARQLBenchmark/V2/spec/index.html#querymixes
	// Average execution time results-
	// http://www4.wiwiss.fu-berlin.de/bizer/BerlinSPARQLBenchmark/V1/results/index.html#results
	PDStoreI<GUID, Object, GUID> store = new PDStore(
			"PDStorebenchmark_ScaleFactor_666");

	private Variable varA;
	private Variable varB;
	private Variable varC;
	private Variable varD;
	private Variable varE;
	private Variable varF;
	private Variable varG;
	private Variable varH;
	private Variable varI;
	private Variable varJ;
	private Variable varK;
	private Variable varL;
	private Variable varM;
	private Variable varN;
	private Variable varO;
	private Variable varP;
	private Variable varQ;
	private Variable varR;
	private Variable varS;
	private Variable varT;
	private Variable varU;
	private Variable varV;
	private Variable varW;
	private Variable varX;
	private Variable varY;
	private Variable varZ;
	// Initializing GUID
	private GUID type;
	private GUID label;
	private GUID comment;
	private GUID publisher;
	private GUID date;
	private GUID subClassOf;
	private GUID homepage;
	private GUID country;
	private GUID Producer;
	private GUID productPropertyNumeric1;
	private GUID productPropertyNumeric2;
	private GUID productPropertyNumeric3;
	private GUID productPropertyNumeric5;
	private GUID productPropertyTextual1;
	private GUID productPropertyTextual2;
	private GUID productPropertyTextual3;
	private GUID productPropertyTextual4;
	private GUID productFeature;
	private GUID productPropertyNumeric6;
	private GUID productPropertyTextual6;
	private GUID productPropertyNumeric4;
	private GUID productPropertyTextual5;
	private GUID product;
	private GUID vendor;
	private GUID price;
	private GUID validFrom;
	private GUID validTo;
	private GUID deliveryDays;
	private GUID offerWebpage;
	private GUID name;
	private GUID mbox_sha1sum;
	private GUID reviewFor;
	private GUID reviewer;
	private GUID reviewDate;
	private GUID title;
	private GUID text;
	private GUID rating2;
	private GUID rating3;
	private GUID rating1;
	private GUID rating4;

	@Before
	public void setUp() throws Exception {

		varA = new Variable("a");
		varB = new Variable("b");
		varC = new Variable("C");
		varD = new Variable("d");
		varE = new Variable("e");
		varF = new Variable("f");
		varG = new Variable("g");
		varH = new Variable("h");
		varI = new Variable("i");
		varJ = new Variable("j");
		varK = new Variable("k");
		varL = new Variable("l");
		varM = new Variable("m");
		varN = new Variable("n");
		varO = new Variable("o");
		varP = new Variable("p");
		varQ = new Variable("q");
		varR = new Variable("r");
		varS = new Variable("s");
		varT = new Variable("t");
		varU = new Variable("u");
		varV = new Variable("v");
		varW = new Variable("w");
		varX = new Variable("x");
		varY = new Variable("y");
		varZ = new Variable("Z");

		// Initializing Guids for predicate

		type = new GUID("fb1951608f5811e1922a441ea1dc5b4f");
		label = new GUID("fb19eda08f5811e1922a441ea1dc5b4f");
		comment = new GUID("fb19eda18f5811e1922a441ea1dc5b4f");
		publisher = new GUID("fb1a14b08f5811e1922a441ea1dc5b4f");
		date = new GUID("fb1a3bc08f5811e1922a441ea1dc5b4f");
		subClassOf = new GUID("fb1c37908f5811e1922a441ea1dc5b4f");
		homepage = new GUID("fea0f1808f5811e1922a441ea1dc5b4f");
		country = new GUID("fea0f1818f5811e1922a441ea1dc5b4f");
		Producer = new GUID("fea118908f5811e1922a441ea1dc5b4f");
		productPropertyNumeric1 = new GUID("fea13fa08f5811e1922a441ea1dc5b4f");
		productPropertyNumeric2 = new GUID("fea13fa18f5811e1922a441ea1dc5b4f");
		productPropertyNumeric3 = new GUID("fea166b08f5811e1922a441ea1dc5b4f");
		productPropertyNumeric5 = new GUID("fea166b18f5811e1922a441ea1dc5b4f");
		productPropertyTextual1 = new GUID("fea18dc08f5811e1922a441ea1dc5b4f");
		productPropertyTextual2 = new GUID("fea1dbe08f5811e1922a441ea1dc5b4f");
		productPropertyTextual3 = new GUID("fea202f08f5811e1922a441ea1dc5b4f");
		productPropertyTextual4 = new GUID("fea202f18f5811e1922a441ea1dc5b4f");
		productFeature = new GUID("fea202f28f5811e1922a441ea1dc5b4f");
		productPropertyNumeric6 = new GUID("fea2c6408f5811e1922a441ea1dc5b4f");
		productPropertyTextual6 = new GUID("fea2ed508f5811e1922a441ea1dc5b4f");
		productPropertyNumeric4 = new GUID("fea314608f5811e1922a441ea1dc5b4f");
		productPropertyTextual5 = new GUID("fea3fec08f5811e1922a441ea1dc5b4f");
		product = new GUID("0e7a68758f5911e1922a441ea1dc5b4f");
		vendor = new GUID("0e7a8f808f5911e1922a441ea1dc5b4f");
		price = new GUID("0e7adda08f5911e1922a441ea1dc5b4f");
		validFrom = new GUID("0e7adda18f5911e1922a441ea1dc5b4f");
		validTo = new GUID("0e7b04b08f5911e1922a441ea1dc5b4f");
		deliveryDays = new GUID("0e7b04b18f5911e1922a441ea1dc5b4f");
		offerWebpage = new GUID("0e7b04b28f5911e1922a441ea1dc5b4f");
		name = new GUID("35231b218f5911e1922a441ea1dc5b4f");
		mbox_sha1sum = new GUID("35231b228f5911e1922a441ea1dc5b4f");
		reviewFor = new GUID("35231b238f5911e1922a441ea1dc5b4f");
		reviewer = new GUID("352342308f5911e1922a441ea1dc5b4f");
		reviewDate = new GUID("352342318f5911e1922a441ea1dc5b4f");
		title = new GUID("352342328f5911e1922a441ea1dc5b4f");
		text = new GUID("352342338f5911e1922a441ea1dc5b4f");
		rating2 = new GUID("352369408f5911e1922a441ea1dc5b4f");
		rating3 = new GUID("352369418f5911e1922a441ea1dc5b4f");
		rating1 = new GUID("352369428f5911e1922a441ea1dc5b4f");
		rating4 = new GUID("352390508f5911e1922a441ea1dc5b4f");

	}

	@After
	public void tearDown() throws Exception {
	}

	public final void test_dataimport() {
		// triples generated 40177
		List<Variable> select = new ArrayList<Variable>();
		List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();

		PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, varT, varX, varY, varZ);
		// FilterExpression fe1 =new AndExpression(new EqualExpression(varX,
		// "ProductType1"),new EqualExpression(varZ, "ProductType"));
		Query query1 = new Query(select, where, null, null, store);
		where.add(w1);

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();

		Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query1
				.execute(null);
		int count = 0;
		while (assignmentIterator.hasNext()) {
			assignmentIterator.next();
			count++;
		}
		stopwatch.stop();
		System.out.println("execution time is " + stopwatch.nanoSeconds()
				/ 1000000 + " miliseconds");
		System.out.println("Total number of records processed " + count);
	}

	// Query 1 (Query number 2 in BSBM v2.0)
	/*
	 * SELECT ?label ?comment ?producer ?productFeature ?propertyTextual1
	 * ?propertyTextual2 ?propertyTextual3 ?propertyNumeric1 ?propertyNumeric2
	 * ?propertyTextual4 ?propertyTextual5 ?propertyNumeric4 WHERE {
	 * %ProductXYZ% rdfs:label ?label .-- %ProductXYZ% rdfs:comment ?comment .--
	 * %ProductXYZ% bsbm:producer ?p .-- ?p rdfs:label ?producer .--
	 * %ProductXYZ% dc:publisher ?p . -- %ProductXYZ% bsbm:productFeature ?f .--
	 * ?f rdfs:label ?productFeature .-- %ProductXYZ%
	 * bsbm:productPropertyTextual1 ?propertyTextual1 .-- %ProductXYZ%
	 * bsbm:productPropertyTextual2 ?propertyTextual2 .-- %ProductXYZ%
	 * bsbm:productPropertyTextual3 ?propertyTextual3 .-- %ProductXYZ%
	 * bsbm:productPropertyNumeric1 ?propertyNumeric1 .-- %ProductXYZ%
	 * bsbm:productPropertyNumeric2 ?propertyNumeric2 .-- OPTIONAL {
	 * %ProductXYZ% bsbm:productPropertyTextual4 ?propertyTextual4 }-- OPTIONAL
	 * { %ProductXYZ% bsbm:productPropertyTextual5 ?propertyTextual5 }--
	 * OPTIONAL { %ProductXYZ% bsbm:productPropertyNumeric4 ?propertyNumeric4
	 * }-- }
	 */
	public final void test_BSBM_query2() {
		int loopcnt = 0;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		while (loopcnt < 1) {// 128// Query 2 used 6 times in a single query
								// mix.
								// Query mix is called 128 times.128*6=768

			loopcnt = loopcnt + 1;

			List<Variable> select = new ArrayList<Variable>();
			List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalWhere1 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalWhere2 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalWhere3 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<Query> lstoptional = new ArrayList<Query>();

			FilterExpression fe = new EqualExpression(varP,
					"dataFromProducer3Product121");// randomly choosen
			Query query1 = new Query(select, where, fe, lstoptional, store);

			PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, label, varL);
			where.add(w1);

			PDChange<GUID, Object, GUID> w2 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, comment, varC);
			where.add(w2);

			PDChange<GUID, Object, GUID> w3 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, Producer, varA);// VarA=
																		// ?p
			where.add(w3);

			PDChange<GUID, Object, GUID> w4 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varA, label, varB);// ?producer
																	// =VarB
			where.add(w4);

			PDChange<GUID, Object, GUID> w5 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, publisher, varA);// VarA=
																		// ?p
			where.add(w5);

			PDChange<GUID, Object, GUID> w6 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productFeature, varF);// VarF=
																				// ?f
			where.add(w6);

			PDChange<GUID, Object, GUID> w7 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varF, label, varZ);// VarZ=
																	// ?productFeature
			where.add(w7);

			PDChange<GUID, Object, GUID> w8 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyTextual1,
					varD);// VarD= ?PropertyTextual1
			where.add(w8);

			PDChange<GUID, Object, GUID> w9 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyTextual2,
					varE);// VarD= ?PropertyTextual2
			where.add(w9);

			PDChange<GUID, Object, GUID> w10 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyTextual3,
					varG);// VarG= ?PropertyTextual3
			where.add(w10);

			PDChange<GUID, Object, GUID> w11 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyNumeric1,
					varH);// VarH= ?PropertyNumeric1
			where.add(w11);

			PDChange<GUID, Object, GUID> w12 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyNumeric2,
					varI);// VarI= ?PropertyNumeric2
			where.add(w12);

			Query optionalsQuery1 = new Query(select, optionalWhere1, null,
					null, store);

			PDChange<GUID, Object, GUID> ow1 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyTextual4,
					varJ);// VarJ= ?PropertyTextual4
			optionalWhere1.add(ow1);
			lstoptional.add(optionalsQuery1);

			Query optionalsQuery2 = new Query(select, optionalWhere2, null,
					null, store);
			PDChange<GUID, Object, GUID> ow2 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyTextual5,
					varK);// VarK= ?PropertyTextual5
			optionalWhere2.add(ow2);
			lstoptional.add(optionalsQuery2);

			Query optionalsQuery3 = new Query(select, optionalWhere3, null,
					null, store);
			PDChange<GUID, Object, GUID> ow3 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, productPropertyNumeric4,
					varM);// VarM= ?PropertyNumeric4
			optionalWhere3.add(ow3);
			lstoptional.add(optionalsQuery3);

			Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query1
					.execute(null);
			int count = 0;
			while (assignmentIterator.hasNext()) {
				Object b = assignmentIterator.next();
				count++;
			}
			System.out.println("Total number of records processed " + count);
		}
		stopwatch.stop();
		System.out.println("execution time for Query 2 in BSBM V2.0 is "
				+ stopwatch.nanoSeconds() / 1000000 + " miliseconds");
		// System.out.println("Total number of records processed " + count +
		// "\n");
		// TODO:statistics and memory
	}

	// Query 2 (Query number 7 in BSBM v2.0)
	/*
	 * PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rev:
	 * <http://purl.org/stuff/rev#> PREFIX foaf: <http://xmlns.com/foaf/0.1/>
	 * PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>
	 * PREFIX dc: <http://purl.org/dc/elements/1.1/>
	 * 
	 * SELECT ?productLabel ?offer ?price ?vendor ?vendorTitle ?review ?revTitle
	 * ?reviewer ?revName ?rating1 ?rating2 WHERE { %ProductXYZ% rdfs:label
	 * ?productLabel . OPTIONAL { ?offer bsbm:product %ProductXYZ% . ?offer
	 * bsbm:price ?price . ?offer bsbm:vendor ?vendor . ?vendor rdfs:label
	 * ?vendorTitle . ?vendor bsbm:country
	 * <http://downlode.org/rdf/iso-3166/countries#DE> . ?offer dc:publisher
	 * ?vendor . ?offer bsbm:validTo ?date . FILTER (?date > %currentDate% ) }
	 * OPTIONAL { ?review bsbm:reviewFor %ProductXYZ% . ?review rev:reviewer
	 * ?reviewer . ?reviewer foaf:name ?revName . ?review dc:title ?revTitle .
	 * OPTIONAL { ?review bsbm:rating1 ?rating1 . } OPTIONAL { ?review
	 * bsbm:rating2 ?rating2 . } } }
	 */
	public final void test_BSBM_query7() {
		int loopcnt = 0;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		while (loopcnt < 1) {// 128//512
			loopcnt = loopcnt + 1;

			List<Variable> select = new ArrayList<Variable>();
			List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalWhere1 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalWhere2 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalNestedWhere1 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<PDChange<GUID, Object, GUID>> optionalNestedWhere2 = new ArrayList<PDChange<GUID, Object, GUID>>();
			List<Query> lstoptional = new ArrayList<Query>();
			List<Query> lstNestedoptional = new ArrayList<Query>();

			FilterExpression fe = new EqualExpression(varP, "ProductFeature610");// Chosen
																					// randomly
			Query query1 = new Query(select, where, fe, lstoptional, store);

			// PDChange<GUID, Object, GUID> w0 = new PDChange<GUID, Object,
			// GUID>(
			// ChangeType.LINK_ADDED, varT, varP, type, "ProductType");
			// where.add(w0);

			PDChange<GUID, Object, GUID> w1 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varP, label, varL);// VarL =
																	// ?productlabel
			where.add(w1);

			// Optional 1
			FilterExpression fe1 = new GreaterThanExpression(varD, "2008-09-11");
			Query Optionalquery1 = new Query(select, optionalWhere1, fe1, null,
					store);// add filter and check

			PDChange<GUID, Object, GUID> ow1 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varO, product, varP);// VarO =
																		// ?Offer
			optionalWhere1.add(ow1);

			PDChange<GUID, Object, GUID> ow2 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varO, price, varA);// VarA =
																	// ?price
			optionalWhere1.add(ow2);

			PDChange<GUID, Object, GUID> ow3 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varO, vendor, varV);// VarV =
																		// ?Vendor
			optionalWhere1.add(ow3);

			PDChange<GUID, Object, GUID> ow4 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varV, label, varE);// VarE =
																	// ?Vendortitle
			optionalWhere1.add(ow4);

			PDChange<GUID, Object, GUID> ow5 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varV, country, "countries#DE");
			optionalWhere1.add(ow5);

			PDChange<GUID, Object, GUID> ow6 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varO, publisher, varV);
			optionalWhere1.add(ow6);

			PDChange<GUID, Object, GUID> ow7 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varO, validTo, varD);// varD=?date
			optionalWhere1.add(ow7);
			lstoptional.add(Optionalquery1);

			// Optionalquery2
			Query Optionalquery2 = new Query(select, optionalWhere2, null,
					lstNestedoptional, store);

			PDChange<GUID, Object, GUID> ow8 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varR, reviewFor, varP);// varR=?review
			optionalWhere2.add(ow8);

			PDChange<GUID, Object, GUID> ow9 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varR, reviewer, varZ);// varZ=?reviewer
			optionalWhere2.add(ow9);

			PDChange<GUID, Object, GUID> ow10 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varZ, name, varY);// varY=?revName
			optionalWhere2.add(ow10);

			PDChange<GUID, Object, GUID> ow11 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varR, title, varX);// varX=?revtitle
			optionalWhere2.add(ow11);
			lstoptional.add(Optionalquery2);

			// Nested optional 1
			Query OptionalNestedquery1 = new Query(select,
					optionalNestedWhere1, null, null, store);

			PDChange<GUID, Object, GUID> ow12 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varR, rating1, varB);// VarB =
																		// ?rating1
			optionalNestedWhere1.add(ow12);
			lstNestedoptional.add(OptionalNestedquery1);

			// Nested optional 2
			Query OptionalNestedquery2 = new Query(select,
					optionalNestedWhere2, null, null, store);

			PDChange<GUID, Object, GUID> ow13 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, varT, varR, rating2, varC);// VarC =
																		// ?rating2
			optionalNestedWhere2.add(ow13);
			lstNestedoptional.add(OptionalNestedquery2);

			// Execution

			Iterator<ResultElement<GUID, Object, GUID>> assignmentIterator = query1
					.execute(null);
			int count = 0;
			while (assignmentIterator.hasNext()) {
				Object b = assignmentIterator.next();
				count++;
			}
			System.out.println("Total number of records processed " + count);
		}
		stopwatch.stop();
		System.out.println("execution time for Query 7 in BSBM V2.0 is  "
				+ stopwatch.nanoSeconds() / 1000000 + " miliseconds");

		// TODO:statistics and memory
	}
}
