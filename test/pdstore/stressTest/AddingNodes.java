package pdstore.stressTest;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pdstore.PDStore;

public class AddingNodes {
	private static final int NODES = 400;
	private static final int NODES_MAX = 8000;
	private static final int STEP = 2; 

	private PDStore p;
	
	private Utility u = new Utility();




	@Before
	public void setUp() {
		p = new PDStore(Utility.getUniqueFilename());

	}

	@After
	public void tearDown() {
		p.close();
		p = null;
	}
	
	public void timeTests(Method m, int nodeCount) {
		ArrayList<Long> times = new ArrayList<Long>();
		int nodes = NODES;
		int nodesMax = NODES_MAX;
		if (nodeCount>0) {
			nodesMax = nodeCount*3;
			nodes = nodeCount;
			
		}
		while (nodes < nodesMax) { 
			long t0 = System.currentTimeMillis();
			try {
				m.invoke(u, p, nodes
						);
			} catch (Exception e) {
				assert(false);
			}
			times.add(System.currentTimeMillis() - t0);
			nodes *= STEP;
		}
		
		long mem0 = Runtime.getRuntime().totalMemory() -
			      Runtime.getRuntime().freeMemory();
		System.out.println(m.toString() + " has runtimes of " + times);
		System.out.println(m.toString() + " has memory consumption of " + mem0);
	}


	@Test
	public void testLinearlyLinkedAdd() {
		try {
			timeTests(Utility.class.getMethod("generateLinearlyLinked", PDStore.class, Integer.TYPE), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBipartiteEvenSplitAdd() {
		try {
			timeTests(Utility.class.getMethod("generateBipartiteEvenSplit", PDStore.class, Integer.TYPE), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBipartiteEvenSplitDeinterleaved() {
		try {
			timeTests(Utility.class.getMethod("generateBipartiteEvenSplitDeinterleaved", PDStore.class, Integer.TYPE), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testFullyConnectedAdd() {
			try {
				timeTests(Utility.class.getDeclaredMethod("generateFullyConnected", PDStore.class, Integer.TYPE),  5
						);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
