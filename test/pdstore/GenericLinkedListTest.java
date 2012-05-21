package pdstore;

import junit.framework.TestCase;

import org.junit.Before;

import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.GenericLinkedList;
import diagrameditor.dal.PDHistory;
import diagrameditor.dal.PDOperation;

public class GenericLinkedListTest extends TestCase {

	public PDStore store;
	PDWorkingCopy workingCopy;

	PDOperation op1, op2, op3, op4, op5;
	PDHistory history;
	GUID histGUID;
	GUID transaction;

	GenericLinkedList<GUID, Object, GUID, PDOperation> list;

	@Before
	public void setUp() {
		store = new PDStore("DiagramEditor");
		transaction = store.begin();

		workingCopy = new PDSimpleWorkingCopy(store);
		histGUID = new GUID();
		history = new PDHistory(workingCopy, histGUID);
		history.setName("History");
		history.removeOperation();

		op1 = new PDOperation(workingCopy);
		op2 = new PDOperation(workingCopy);
		op3 = new PDOperation(workingCopy);
		op4 = new PDOperation(workingCopy);
		op5 = new PDOperation(workingCopy);

		op1.setNext(op2);
		op2.setNext(op3);
		op3.setNext(op4);
		op4.setNext(op5);

		op1.setName("op1");
		op2.setName("op2");
		op3.setName("op3");
		op4.setName("op4");
		op5.setName("op5");


		history.addOperation(op1);
		history.addOperation(op2);
		history.addOperation(op3);
		history.addOperation(op4);
		history.addOperation(op5);
		workingCopy.commit();

		list = new GenericLinkedList<GUID, Object, GUID, PDOperation>(PDOperation.class, history, PDHistory.roleOperationId, PDOperation.typeId, PDOperation.roleNextId);
	}

	public final void testListSize() {
		assertEquals(list.size(), 5);
	}

	public final void testIterator() {

		int count = 1;
		for(PDInstance i : list){
			assertEquals("op" + count, i.getName());
			if(count < 6){
				assertEquals(true, list.iterator().hasNext());
			}else {
				assertEquals(false, list.iterator().hasNext());
			}
			count ++;
		}
	}

	public final void testListAdd() {
		PDOperation newOperation = new PDOperation(workingCopy);
		//add a unique operation
		try {
			list.add(newOperation);
		} catch (Exception e) {
			fail();
		}

		assertEquals(list.size(), 6);
		assertEquals(list.get(5).getId(), newOperation.getId());

		//add same object again
		try {
			list.add(newOperation);
			fail();
		}catch (Exception e) {
			assertEquals(list.size(), 6);
			assertEquals(list.get(5).getId(), newOperation.getId());
		}
	}

	public final void testListAddAtIndex() {
		PDOperation newOperation1 = new PDOperation(workingCopy);
		PDOperation newOperation2 = new PDOperation(workingCopy);
		PDOperation newOperation3 = new PDOperation(workingCopy);
		PDOperation newOperation4 = new PDOperation(workingCopy);

		// Add to the front of the linked list
		try {
			list.add(0, newOperation1);
			assertEquals(list.size(), 6);
			assertEquals(list.get(0).getId(), newOperation1.getId());
		}catch (Exception e) {
			fail();
		}

		// Add to the middle of the linked list
		try {
			list.add(2, newOperation2);
			assertEquals(list.size(), 7);
			assertEquals(list.get(2).getId(), newOperation2.getId());
		} catch (Exception e) {
			fail();
		}

		// Add to the end of the linked list
		try {
			list.add(list.size(), newOperation3);
			assertEquals(list.size(), 8);
			assertEquals(list.get(7).getId(), newOperation3.getId());
		} catch (Exception e) {
			fail();
		}

		// Should throw an exception
		try {
			list.add(4, newOperation1); // Add the same element twice
			fail();
		} catch (Exception e) {	}	//TODO: use the specific exception

		try {
			list.add(-1, newOperation4);
			fail();
		} catch (Exception e) {	}

		try {
			list.add(20, newOperation4);
			fail();
		} catch (Exception e) {	}

		try {
			list.add(3, null);
			fail();
		} catch (Exception e) { }


	}

	public final void testSort() {
		// Test that the list is sorted and the get method work
		assertEquals(list.get(0).getId(), op1.getId());
		assertEquals(list.get(1).getId(), op2.getId());
		assertEquals(list.get(2).getId(), op3.getId());
		assertEquals(list.get(3).getId(), op4.getId());
		assertEquals(list.get(4).getId(), op5.getId());
	}

	public final void testRemoveByIndex() {
		assertFalse(list.isEmpty());

		// Remove a middle element
		list.remove(1);
		assertEquals(list.size(), 4);
		assertEquals(list.get(0).getId(), op1.getId());
		assertEquals(list.get(1).getId(), op3.getId());
		assertEquals(list.get(2).getId(), op4.getId());
		assertEquals(list.get(3).getId(), op5.getId());

		// Remove the first element
		list.remove(0);
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).getId(), op3.getId());
		assertEquals(list.get(1).getId(), op4.getId());
		assertEquals(list.get(2).getId(), op5.getId());

		// Remove the last element
		list.remove(2);
		assertEquals(list.size(), 2);
		assertEquals(list.get(0).getId(), op3.getId());
		assertEquals(list.get(1).getId(), op4.getId());

		// Remove all remaining elements
		list.remove(0);
		list.remove(0);
		assertEquals(list.size(), 0);
		assertTrue(list.isEmpty());

		// Remove invalid indexes
		// Throws exception
		// list.remove(-1);
		// list.remove(10);
	}

	public final void testRemoveByElement() {
		assertFalse(list.isEmpty());

		// Remove a middle element
		list.remove(op2);
		assertEquals(list.size(), 4);
		assertEquals(list.get(0).getId(), op1.getId());
		assertEquals(list.get(1).getId(), op3.getId());
		assertEquals(list.get(2).getId(), op4.getId());
		assertEquals(list.get(3).getId(), op5.getId());

		// Remove the first element
		list.remove(op1);
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).getId(), op3.getId());
		assertEquals(list.get(1).getId(), op4.getId());
		assertEquals(list.get(2).getId(), op5.getId());

		// Remove the last element
		list.remove(op5);
		assertEquals(list.size(), 2);
		assertEquals(list.get(0).getId(), op3.getId());
		assertEquals(list.get(1).getId(), op4.getId());

		// Remove all remaining elements
		list.remove(op3);
		list.remove(op4);
		assertEquals(list.size(), 0);
		assertTrue(list.isEmpty());

		// Remove invalid elements
		// Throws exception
		// list.remove(null);
		//TODO what happens if you try removing something thats not in the list?
	}

	public final void testContains() {
		assertTrue(list.contains(op4));

		list.remove(op4);
		assertFalse(list.contains(op4));
	}
	
	public final void testGet(){
		assertEquals(list.get(0).getId(), op1.getId());
		
		try{
			list.get(10);
			fail();
		}catch(Exception e){}
	}
}

