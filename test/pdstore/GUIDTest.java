package pdstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.TestCase;

import nz.ac.auckland.se.genoupe.tools.Debug;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GUIDTest extends TestCase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public final void testNewGUID() {
		for (int i = 0; i < 10000; i++) {
			GUID oldid = new GUID("00000000000000000000713997cadc7e");
			GUID newid = GUID.newTransactionId(oldid.getBranchID());
			assertTrue(newid.getLongTimestamp() > 0);
		}
	}
	
	public final void testSetBranchId() {
		GUID transaction = new GUID();
		GUID branch = GUID.newBranchId();
		GUID.setBranchId(transaction, branch);
		assertTrue(transaction.getBranchID().equals(branch));  // branchId should be earliest transactionID
	}
	
	public final void testMaxTransactionId() {
		GUID transactionId = new GUID();
		GUID max = GUID.maxTransactionId();
		assertTrue(max.compareTo(transactionId)==1);
		GUID branch = GUID.newBranchId();
		GUID.setBranchId(max, branch);
		assertTrue(max.compareTo(transactionId)==1);
		assertTrue(max.compareTo(branch)==1);  // branchId should be earliest transactionID
	}

	public final void testInverse() {
		final GUID firstId = new GUID("abaad460670211dfadad002170295281");
		final GUID resultId = firstId.getPartner();
		assertEquals(resultId.getPartner().equals(firstId), true);
	}


	public final void testGetFirst() {
		final GUID firstId = new GUID();
		final GUID partnerId = firstId.getPartner();
		assertEquals(partnerId.getFirst().equals(firstId.getFirst()), true);
	}

	public final void testIsFirst() {
		final GUID firstId = new GUID();
		final GUID partnerId = firstId.getPartner();
		assertEquals(partnerId.isFirst(), !firstId.isFirst());
	}

	public final void earlierLater() {
		final GUID id1 = new GUID();
		final GUID id2 = new GUID();
		assertTrue(id1.earlier(id2));
		assertTrue(id2.later(id1));
	}

	public final void testReverseFunction() {
		long time = 1;
		for (int i = 1; i < 65; ++i) {
			Debug.println(" Bit:  " + i);
			Debug.println(" In:  "
					+ Long.toBinaryString(time | (~0xDFFFFFFFFFFFFFFFL)));
			Debug.println(" Out: "
					+ Long.toBinaryString(GUID
							.bitShufflingForGUIDDecoding(time)
							| (~0xDFFFFFFFFFFFFFFFL)));
			Debug.println("                                      ^");
			Debug.println("      1098765432109876543210987654321098765432109876543210987654321");
			time = time << 1;
		}
	}
	
	public final void testLoad() {
		final GUID firstId = new GUID("abaad460670211dfadad002170295281");
		final GUID secndId = new GUID("abaad460670211dfadad002170295281");
		assertTrue(firstId!=secndId);
		assertTrue(firstId.equals(secndId));
		final GUID id3 = GUID.load("abaad460670211dfadad002170295281");
		final GUID id4 = GUID.load("abaad460670211dfadad002170295281");
		assertTrue(id3==id4);
	}
	
	public final void testFirst() {
		final GUID sampleId = new GUID();
		assertTrue(sampleId.getFirst().isFirst());
		assertFalse(sampleId.getFirst().getPartner().isFirst());
	}
}
