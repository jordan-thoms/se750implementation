package pdstore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;

import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.PDSorter;
import pdstore.generic.SortByOrder;
import diagrameditor.dal.PDHistory;
import diagrameditor.dal.PDOperation;

/**
 * Testing SortByOrder Implementation of PDSorter. Gets an unordered collection
 * of PDStore instances and sorts them on a specific role type which links them
 * together in a list.
 * 
 * @author cbue001
 * 
 */
public class SortByOrderTest extends TestCase {

	public PDStore store;
	PDWorkingCopy workingCopy;

	PDOperation op1, op2, op3, op4, op5;
	GUID histGUID;
	GUID transaction;

	static final String[] OP_NAMES = { "Operation 1", "Operation 2",
			"Operation 3", "Operation 4", "Operation 5" };

	@Before
	public void setUp() {
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
//		Date date = new Date();
//		// create the usual store including the metamodel by default
//		store = new PDStore("PDStoreTest-" + dateFormat.format(date));
		store = new PDStore("DiagramEditor");

		transaction = store.begin();

		workingCopy = new PDSimpleWorkingCopy(store);
		histGUID = new GUID();
		PDHistory history = new PDHistory(workingCopy, histGUID);
		history.setName("History");
		history.removeOperation();

		op1 = new PDOperation(workingCopy);
		op2 = new PDOperation(workingCopy);
		op3 = new PDOperation(workingCopy);
		op4 = new PDOperation(workingCopy);
		op5 = new PDOperation(workingCopy);

		op1.setName(OP_NAMES[0]);
		op2.setName(OP_NAMES[1]);
		op3.setName(OP_NAMES[2]);
		op4.setName(OP_NAMES[3]);
		op5.setName(OP_NAMES[4]);

		op1.setNext(op2);
		op2.setNext(op3);
		op3.setNext(op4);
		op4.setNext(op5);

		history.addOperation(op1);
		history.addOperation(op2);
		history.addOperation(op3);
		history.addOperation(op4);
		history.addOperation(op5);

		workingCopy.commit();
	}

	public final void testSortByOrder() {
		PDHistory hist = (PDHistory) workingCopy.load(PDHistory.typeId,
				histGUID);
		Collection<PDOperation> unordered = hist.getOperations();

		PDSorter<GUID, Object, GUID> sorter = new SortByOrder<GUID, Object, GUID>(
				transaction, PDOperation.roleNextId);
		List<PDOperation> sorted = sorter.sort(unordered);

		for (int i = 0; i < sorted.size(); i++) {
			assertEquals(OP_NAMES[i], sorted.get(i).getName());
		}
	}
}
