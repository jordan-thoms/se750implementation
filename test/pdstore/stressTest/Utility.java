package pdstore.stressTest;

import pdstore.GUID;
import pdstore.PDStore;

public class Utility {
	private static final GUID ROLEID = new GUID();

	public static String getUniqueFilename() {
		return "test-" + new GUID().toString();
	}
	
	public static void generateFullyConnected(PDStore store, int nodes) {
		GUID g = store.begin();
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < i; j++) {
				store.addLink(g, i, ROLEID, j);
			}
		}
		store.commit(g);
	}

	public static void generateLinearlyLinked(PDStore store, int nodes) {
		generateLinearlyLinked(store, nodes, 0);
	}

	public static void generateLinearlyLinked(PDStore store, int nodes, int transactions) {
		GUID g = store.begin();
		for (int i = 1; i < nodes; i++) {
			String obj1 = String.valueOf(i);
			String obj2 = String.valueOf(i - 1);
			if (transactions != 0) {
				obj1 = transactions + obj1;
				obj2 = transactions + obj2;
			}
			store.addLink(g, obj1, ROLEID, obj2);

		}
		store.commit(g);
	}

	public static void generateBipartiteEvenSplit(PDStore store, int nodes) {
		GUID g = store.begin();
		for (int i = 0; i < nodes; i += 2) {
			store.addLink(g, i, ROLEID, i + 1);
		}
		store.commit(g);
	}

	public static void generateBipartiteEvenSplitDeinterleaved(PDStore store, int nodes) {
		GUID g = store.begin();

		for (int i = 0; i < nodes / 2; i++) {
			store.addLink(g, i, ROLEID, i + nodes / 2);
		}
		for (int i = nodes; i >= nodes / 2; i--) {
			store.addLink(g, i, ROLEID, nodes / 2 - i);
		}
		store.commit(g);
	}

	public static void retrieveItems(PDStore store, int nodes, int retrievals) {
		GUID t = store.begin();
		for (int i = 0; i < retrievals; i++) {
			int o = (int) (Math.random() * nodes);
			store.getInstance(t, o, ROLEID);
		}
		store.commit(t);
	}
}
