package pdstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import nz.ac.auckland.se.genoupe.tools.Debug;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OperationTest extends TestCase implements OperationI{

	private String fileName;
	private PDStore store;

	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		fileName = "OperationTest-" + dateFormat.format(date);
		store = new PDStore(fileName);
	}

	public final void testIdentityOperation() {
		GUID transaction = store.begin();
		GUID myOperation = new GUID();
		store.addLink(transaction, myOperation, PDStore.OPERATION_IMPLEMENTATION_ROLEID, "pdstore.OperationTest");
		assertEquals("Hello", store.applyOperation(transaction, myOperation, "Hello"));
		assertFalse("X".equals(store.applyOperation(transaction, myOperation, "Hi")));
	}

	@Override
	public Object apply(PDStore store, GUID transaction, Object parameter) {
		return parameter;
	}
}
