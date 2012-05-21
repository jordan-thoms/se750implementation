package pdintegrate.DAL;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pdstore.GUID;
import pdstore.PDStore;

import static org.junit.Assert.*;

public class DALTest {


	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test 
	public final void testUniqueGUIDs() {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		HashSet<GUID> ids = new HashSet<GUID>();
		try {
			classes.add(Class.forName("pdintegrate.DAL.DataSource"));
			classes.add(Class.forName("pdintegrate.DAL.Entity"));
			classes.add(Class.forName("pdintegrate.DAL.Mapping"));
			classes.add(Class.forName("pdintegrate.DAL.Match"));
			classes.add(Class.forName("pdintegrate.DAL.ObjectSchema"));
			classes.add(Class.forName("pdintegrate.DAL.Relation"));
			classes.add(Class.forName("pdintegrate.DAL.Schema"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		for (Class<?> c: classes) {
			for (Field f: c.getDeclaredFields()) {
				if (f.getType().equals(GUID.class)) {
					try {
						GUID g = (GUID)f.get(null);
						if (ids.contains(g)) {
							System.out.println("Guid " + g.toString() + " is repeated.");
						}
						assertTrue(!ids.contains(g));
						ids.add(g);
					} catch (IllegalArgumentException e) {
						//Don't care
					} catch (IllegalAccessException e) {
						//Don't care
					}
				}
			}
		}
	}

	@Test
	public final void testInit() {
		PDStore p = new PDStore("pdstore");
		GUID g = p.begin();
		Collection<?> c = p.getAllInstancesOfType(g, PDStore.MODEL_TYPEID);
		for (Object o: c) {
			assertFalse( "Make sure to delete PDStore.pds", DAL.MODEL_ID.equals(o));
		}
		p.commit(g);
		DAL.init(p);
		g = p.begin();
		boolean found = false;
		c = p.getAllInstancesOfType(g, PDStore.MODEL_TYPEID);
		for (Object o: c) {
			if (DAL.MODEL_ID.equals(o)) {
				found = true;
				break;
				}
		}
		assertTrue("Init failed: could not find model", found);
	}

}
