package pdstore;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import nz.ac.auckland.se.genoupe.tools.Stopwatch;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pdstore.PDStore;
import pdstore.concurrent.ConcurrentStore;

public class PDStoreStressTest2 extends PDStoreTest {

	final int MAX_RUNS = 800;
	ArrayList<GUID> guidArray;

	@Before
	public void setUp() {
		final int MAX_ADDS = MAX_RUNS;

		guidArray = new ArrayList<GUID>(MAX_ADDS);

		for (int i = 1; i < MAX_ADDS; ++i) {
			guidArray
					.add(new GUID(
							("debac123" + i + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
									.substring(0, 32)));
		}
		for (int i = 1; i < MAX_ADDS - 2; ++i) {
			GUID transaction = store.begin();
			store.addLink(transaction, guidArray.get(i), roleA, guidArray
					.get(i + 1));
			store.addLink(transaction, guid1, roleB, guidArray.get(i));
			store.commit(transaction);
		}

	}

	public final void testReadManyLinksInSameTransaction() {

		GUID transaction = store.begin();
		int error = -1;
		for (int i = 1; i < MAX_RUNS - 2; ++i) {
			Collection<Object> result = store.getInstances(transaction,
					guidArray.get(i), roleA);

			if (!result.contains(guidArray.get(i + 1)))
				error = i;
		}
		assertTrue(error == -1);
		store.commit(transaction);

	}

	public final void testReadManyLinksInSeveralTransactions() {

		int error = -1;
		Stopwatch watchDemarcation = new Stopwatch();
		Stopwatch watchOperation = new Stopwatch();
		watchDemarcation.start();
		for (int i = 1; i < MAX_RUNS - 2; ++i) {
			GUID transaction = store.begin();
			watchDemarcation.stop();
			watchOperation.start();
			Collection<Object> result = store.getInstances(transaction,
					guidArray.get(i), roleA);

			if (!result.contains(guidArray.get(i + 1)))
				error = i;
			watchOperation.stop();
			watchDemarcation.start();
			store.commit(transaction);
		}
		watchDemarcation.stop();
		System.err.println("Trans: " + watchDemarcation.nanoSeconds()
				+ " OPs: " + watchOperation.nanoSeconds());

		assertTrue(error == -1);

	}

}
