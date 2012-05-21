package pdstore;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GUIDLongrunningTest extends TestCase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public final void testLongTermForInversion() {

		// GUID newid = oldid;
		GUID oldid = new GUID();
		for (int i = 0; i < 10000000; i++) {
			try {
				Thread.sleep(12);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			GUID newid = new GUID();
			int compare = newid.compareTo(oldid);
			// should be +1
			if (compare <= 0) {
				long timeStampOld = oldid.getLongTimestamp();
				long timeStampNew = newid.getLongTimestamp();

				System.err.println("Inversion! comp=" + compare + " Nr: " + i
						+ " Old: " + timeStampOld + " New: " + timeStampNew);
				System.err.println(" Old: " + Long.toBinaryString(timeStampOld)
						+ " New: " + timeStampNew);
				System.err
						.println(" New: " + Long.toBinaryString(timeStampNew));
				System.err.println("                                      ^");
				System.err
						.println("      1098765432109876543210987654321098765432109876543210987654321");
			}

			oldid = newid;

		}
	}

}
