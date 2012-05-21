package pdstore;

import static org.junit.Assert.*;
import pdstore.generic.PDChange;
import junit.framework.TestCase;

import nz.ac.auckland.se.genoupe.tools.Debug;

public class PDChangeTest extends TestCase {

	public void testGetPartnerChange() {
		for (int i = 0; i < 100; i++) {
			PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, new GUID(), new GUID(), new GUID(),
					new GUID());
			PDChange<GUID, Object, GUID> change1b = change1.getPartnerChange();

			assertEquals(change1.getChangeType(), change1b.getChangeType());
			assertEquals(change1.getTransaction(), change1b.getTransaction());
			assertEquals(change1.getInstance1(), change1b.getInstance2());
			assertEquals(change1.getRole2(), change1b.getRole2().getPartner());
			assertEquals(change1.getInstance2(), change1b.getInstance1());
		}
	}
	
	public void testEqualsAndHashCode() {
		for (int i = 0; i < 100; i++) {
			PDChange<GUID, Object, GUID> change1 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, new GUID(), new GUID(), new GUID(),
					new GUID());
			PDChange<GUID, Object, GUID> change2 = new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, new GUID(), new GUID(), new GUID(),
					new GUID());
			
			assertTrue(change1.equals(change1));
			assertFalse(change1.equals(change2));
			assertFalse(change2.equals(change1));
			assertFalse(change1.equals(change1.getPartnerChange()));
			assertTrue(change1.getPartnerChange().equals(change1.getPartnerChange()));
			
			assertTrue(change1.hashCode() == change1.hashCode());
			assertTrue(change1.hashCode() != change2.hashCode());
			assertTrue(change1.hashCode() != change1.getPartnerChange().hashCode());
		}
	}

}
