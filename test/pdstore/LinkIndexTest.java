package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import nz.ac.auckland.se.genoupe.tools.Debug;

import org.junit.Before;
import org.junit.Test;

import pdstore.changeindex.LinkIndex;
import pdstore.generic.PDChange;

public class LinkIndexTest extends TestCase {

	public LinkIndex<GUID, Object, GUID> index = new LinkIndex<GUID, Object, GUID>();

	public void testLinkHash1() {
		GUID instance1a = new GUID();
		GUID instance1b = instance1a;
		
		GUID instance2a = new GUID();
		GUID instance2b = instance2a;
		
		GUID instance3a = new GUID("7e048df034dd11e18f0f00224300a31a");
		GUID instance3b = new GUID("7e048df134dd11e18f0f00224300a31a");
		
		GUID instance4a = new GUID("7e048df034dd11e18f0f00224300a31a");
		GUID instance4b = new GUID("7e048df234dd11e18f0f00224300a31a");
		
		GUID instance5a = new GUID("6fd72a4434e611e1a39d00224300a31a");
		GUID instance5b = new GUID("6fd72a4734e611e1a39d00224300a31a");
		
		GUID instance6a = new GUID("6fd72a4734e611e1a39d00224300a31a");
		GUID instance6b = new GUID("6fd72a5034e611e1a39d00224300a31a");

		PDChange<GUID,Object,GUID> change1a = new PDChange
				<GUID,Object,GUID>(null, null, instance1a, new GUID(), instance1b);
		PDChange<GUID,Object,GUID> change1b = new PDChange
				<GUID,Object,GUID>(null, null, instance1b, new GUID(), instance1a);
		
		PDChange<GUID,Object,GUID> change2a = new PDChange
				<GUID,Object,GUID>(null, null, instance2a, new GUID(), instance2b);
		PDChange<GUID,Object,GUID> change2b = new PDChange
				<GUID,Object,GUID>(null, null, instance2b, new GUID(), instance2a);
		
		PDChange<GUID,Object,GUID> change3a = new PDChange
				<GUID,Object,GUID>(null, null, instance3a, new GUID(), instance3b);
		PDChange<GUID,Object,GUID> change3b = new PDChange
				<GUID,Object,GUID>(null, null, instance3b, new GUID(), instance3a);
		
		PDChange<GUID,Object,GUID> change4a = new PDChange
				<GUID,Object,GUID>(null, null, instance4a, new GUID(), instance4b);
		PDChange<GUID,Object,GUID> change4b = new PDChange
				<GUID,Object,GUID>(null, null, instance4b, new GUID(), instance4a);
		
		PDChange<GUID,Object,GUID> change5a = new PDChange
				<GUID,Object,GUID>(null, null, instance5a, new GUID(), instance5b);
		PDChange<GUID,Object,GUID> change5b = new PDChange
				<GUID,Object,GUID>(null, null, instance5b, new GUID(), instance5a);
		
		PDChange<GUID,Object,GUID> change6a = new PDChange
				<GUID,Object,GUID>(null, null, instance6a, new GUID(), instance6b);
		PDChange<GUID,Object,GUID> change6b = new PDChange
				<GUID,Object,GUID>(null, null, instance6b, new GUID(), instance6a);
		
		assertEquals(index.linkHash(change1a), index.linkHash(change1b));
		assertEquals(index.linkHash(change2a), index.linkHash(change2b));
		assertEquals(index.linkHash(change3a), index.linkHash(change3b));
		assertEquals(index.linkHash(change4a), index.linkHash(change4b));
		assertEquals(index.linkHash(change5a), index.linkHash(change5b));
		assertEquals(index.linkHash(change6a), index.linkHash(change6b));

		assertTrue(index.linkHash(change1a) != index.linkHash(change2a));
		assertTrue(index.linkHash(change1a) != index.linkHash(change3a));
		assertTrue(index.linkHash(change1a) != index.linkHash(change4a));
		assertTrue(index.linkHash(change1a) != index.linkHash(change5a));
		assertTrue(index.linkHash(change1a) != index.linkHash(change6a));

		assertTrue(index.linkHash(change2a) != index.linkHash(change3a));
		assertTrue(index.linkHash(change2a) != index.linkHash(change4a));
		assertTrue(index.linkHash(change2a) != index.linkHash(change5a));
		assertTrue(index.linkHash(change2a) != index.linkHash(change6a));
		
		assertTrue(index.linkHash(change3a) != index.linkHash(change4a));
		assertTrue(index.linkHash(change3a) != index.linkHash(change5a));
		assertTrue(index.linkHash(change3a) != index.linkHash(change6a));
		
		assertTrue(index.linkHash(change4a) != index.linkHash(change5a));
		assertTrue(index.linkHash(change4a) != index.linkHash(change6a));
		
		assertTrue(index.linkHash(change5a) != index.linkHash(change6a));
	}
}
