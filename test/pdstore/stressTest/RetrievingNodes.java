package pdstore.stressTest;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import pdstore.PDStore;

public class RetrievingNodes {
	private static final int NODES = 500;
	private static final int RETRIEVALS = 1000;
	private static final int RET_MAX = 10000;
	private static final int TRANSACTIONS = 10;
	private static final int STEP = 2;

	private static PDStore pFullyConnected;
	private static PDStore pLinearlyLinked;
	private static PDStore pBipartiteEvenSplit;
	private static PDStore pLinearlyLinkedMultiTransaction;
	private static PDStore pBipartiteEvenSplitDeinterleaved;

	@BeforeClass
	public static void beforeClass() {
		pFullyConnected = new PDStore(Utility.getUniqueFilename());
		Utility.generateFullyConnected(pFullyConnected, NODES);

		pLinearlyLinked = new PDStore(Utility.getUniqueFilename());
		Utility.generateLinearlyLinked(pLinearlyLinked, NODES);

		pBipartiteEvenSplit = new PDStore(Utility.getUniqueFilename());
		Utility.generateBipartiteEvenSplit(pBipartiteEvenSplit, NODES);

		pBipartiteEvenSplitDeinterleaved = new PDStore(Utility.getUniqueFilename());
		Utility.generateBipartiteEvenSplitDeinterleaved(pBipartiteEvenSplitDeinterleaved, NODES);

		pLinearlyLinkedMultiTransaction = new PDStore(Utility.getUniqueFilename());
		Utility.generateLinearlyLinked(pLinearlyLinkedMultiTransaction, NODES, TRANSACTIONS);

	}
	
	public void timeTests(PDStore p) {
		ArrayList<Long> times = new ArrayList<Long>();
		int retrievals = RETRIEVALS;
		while (retrievals < RET_MAX) { 
			long t0 = System.currentTimeMillis();

			Utility.retrieveItems(p, retrievals / 2, retrievals);

			times.add(System.currentTimeMillis() - t0);
			retrievals *= STEP;
		}
		System.out.println("runtimes of " + times);
	}

	@Test
	public void testFullyConnectedRetrieval() {
		try {
			timeTests(pFullyConnected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testLinearlyLinkedRetrieval() {
		try {
			timeTests(pLinearlyLinked);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testBipartiteEvenSplitRetrieval() {
		try {
			timeTests(pBipartiteEvenSplit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBipartiteEvenSplitDeinterleavedRetrieval() {
		timeTests(pBipartiteEvenSplitDeinterleaved);
	}

	@Test
	public void testLinearlyLinkedMultipleTransactionRetrieval() {
		timeTests(pLinearlyLinkedMultiTransaction);
	}
}
