package diagrameditor.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;

import diagrameditor.DiagramEditor;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;

import junit.framework.TestCase;

public class testCommutative extends TestCase {

	private DiagramEditor editor;
	
	@Before
	public void setUp(){
		PDStore mainStore = new PDStore("DiagramEditor");
		PDSimpleWorkingCopy workingCopy = new PDSimpleWorkingCopy(mainStore);
		GUID historyID = new GUID();
		editor = new DiagramEditor("Ann", workingCopy, historyID);
		
	}
	
	@After
	public void tearDown(){
		editor.dispose();
	}
	
	/**
	 * Test operations are being added to the operation list
	 */
	public final void testListSize(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op = new PDOperation(editor.workingCopy);
		op.setCommand("New");
		op.setSuperParameter(pds);
		this.editor.getOperationList().add(op);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_1");
		pds.setShapeID("Cir_2");
		op = new PDOperation(editor.workingCopy);
		op.setCommand("Copy");
		op.setSuperParameter(pds);
		editor.getOperationList().add(op);
		
		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		op = new PDOperation(editor.workingCopy);
		op.setCommand("Color");
		op.setSuperParameter(pds);
		editor.getOperationList().add(op);
		
		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		op = new PDOperation(editor.workingCopy);
		op.setCommand("Color");
		op.setSuperParameter(pds);
		editor.getOperationList().add(op);
		
		assertEquals(4, editor.getOperationList().size());
	}
	
	/**
	 * Test commutativity two copy operations on the same new 
	 * operation
	 */
	public final void testTwoCopiesOnSameShape(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op1 = new PDOperation(editor.workingCopy);
		op1.setCommand("New");
		op1.setSuperParameter(pds);
		this.editor.getOperationList().add(op1);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_1");
		pds.setShapeID("Cir_2");
		PDOperation op2 = new PDOperation(editor.workingCopy);
		op2.setCommand("Copy");
		op2.setSuperParameter(pds);
		editor.getOperationList().add(op2);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_1");
		pds.setShapeID("Cir_3");
		PDOperation op3 = new PDOperation(editor.workingCopy);
		op3.setCommand("Copy");
		op3.setSuperParameter(pds);
		editor.getOperationList().add(op3);

		//test commutativity with first operation, New(Cir_1).
		List<PDOperation> com = editor.getOperationList().commutative(op1);
		assertEquals(0, com.size());
		com.clear();
		
		//test commutativity with second operation, Copy(Cir_1, Cir_2).
		com = editor.getOperationList().commutative(op2);
		assertEquals(1, com.size());
		assertEquals(op3.getId(), com.get(0).getId());
		com.clear();
		
		//test commutativity with third operation, Copy(Cir_1, Cir_3).
		com = editor.getOperationList().commutative(op3);
		assertEquals(1, com.size());
		assertEquals(op2.getId(), com.get(0).getId());
		com.clear();
	}
	
	/**
	 * Test commutativity two copy operations on different new 
	 * operations
	 */
	public final void testTwoTypeDisjointCopy(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op1 = new PDOperation(editor.workingCopy);
		op1.setCommand("New");
		op1.setSuperParameter(pds);
		this.editor.getOperationList().add(op1);
		
		//add rectangle operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Rec_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op2 = new PDOperation(editor.workingCopy);
		op2.setCommand("New");
		op2.setSuperParameter(pds);
		this.editor.getOperationList().add(op2);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_1");
		pds.setShapeID("Cir_2");
		PDOperation op3 = new PDOperation(editor.workingCopy);
		op3.setCommand("Copy");
		op3.setSuperParameter(pds);
		editor.getOperationList().add(op3);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Rec_1");
		pds.setShapeID("Rec_2");
		PDOperation op4 = new PDOperation(editor.workingCopy);
		op4.setCommand("Copy");
		op4.setSuperParameter(pds);
		editor.getOperationList().add(op4);
		
		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		PDOperation op5 = new PDOperation(editor.workingCopy);
		op5.setCommand("Color");
		op5.setSuperParameter(pds);
		editor.getOperationList().add(op5);
		
		//test commutativity with first operation, New(Cir_1).
		List<PDOperation> com = editor.getOperationList().commutative(op1);
		assertEquals(1, com.size());
		assertEquals(op2.getId(), com.get(0).getId());
		com.clear();
		
		//test commutativity with second operation, New(Rec_1).
		com = editor.getOperationList().commutative(op2);
		assertEquals(2, com.size());	
		assertEquals(op1.getId(), com.get(0).getId());
		assertEquals(op3.getId(), com.get(1).getId());
		com.clear();
		
		//test commutativity with third operation, Copy(Cir_1, Cir_2).
		com = editor.getOperationList().commutative(op3);
		assertEquals(2, com.size());
		assertEquals(op2.getId(), com.get(0).getId());
		assertEquals(op4.getId(), com.get(1).getId());
		com.clear();
		
		//test commutativity with fourth operation, Copy(Cir_1, Cir_2).
		com = editor.getOperationList().commutative(op4);
		assertEquals(2, com.size());
		assertEquals(op3.getId(), com.get(0).getId());
		assertEquals(op5.getId(), com.get(1).getId());
		com.clear();
		
		//test commutativity with fifth operation, Colour(Cir_1, #ff2233).
		com = editor.getOperationList().commutative(op5);
		assertEquals(1, com.size());
		assertEquals(op4.getId(), com.get(0).getId());
		com.clear();
	}
	
	/**
	 * Test commutativity with two linking copy operations,
	 * a copy on a copy
	 */
	public final void testCopyOnCopy(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op1 = new PDOperation(editor.workingCopy);
		op1.setCommand("New");
		op1.setSuperParameter(pds);
		this.editor.getOperationList().add(op1);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_1");
		pds.setShapeID("Cir_2");
		PDOperation op2 = new PDOperation(editor.workingCopy);
		op2.setCommand("Copy");
		op2.setSuperParameter(pds);
		editor.getOperationList().add(op2);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_2");
		pds.setShapeID("Cir_3");
		PDOperation op3 = new PDOperation(editor.workingCopy);
		op3.setCommand("Copy");
		op3.setSuperParameter(pds);
		editor.getOperationList().add(op3);
		
		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		PDOperation op4 = new PDOperation(editor.workingCopy);
		op4.setCommand("Color");
		op4.setSuperParameter(pds);
		editor.getOperationList().add(op4);

		//test commutativity with first operation, New(Cir_1).
		List<PDOperation> com = editor.getOperationList().commutative(op1);
		assertEquals(0, com.size());
		com.clear();
		
		//test commutativity with second operation, Copy(Cir_1, Cir_2).
		com = editor.getOperationList().commutative(op2);
		assertEquals(0, com.size());
		com.clear();
		
		//test commutativity with third operation, Copy(Cir_2, Cir_3).
		com = editor.getOperationList().commutative(op3);
		assertEquals(1, com.size());
		assertEquals(op4.getId(), com.get(0).getId());
		com.clear();
		
		//test commutativity with fourth operation, Color(Cir_1, #ff2233).
		com = editor.getOperationList().commutative(op4);
		assertEquals(1, com.size());
		assertEquals(op3.getId(), com.get(0).getId());
		com.clear();
	}
	
	/**
	 * Test commutativity with only one copy operation.
	 */
	public final void testOneCopy(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op1 = new PDOperation(editor.workingCopy);
		op1.setCommand("New");
		op1.setSuperParameter(pds);
		this.editor.getOperationList().add(op1);
		
		//add copy operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID("Cir_1");
		pds.setShapeID("Cir_2");
		PDOperation op2 = new PDOperation(editor.workingCopy);
		op2.setCommand("Copy");
		op2.setSuperParameter(pds);
		editor.getOperationList().add(op2);
		
		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		PDOperation op3 = new PDOperation(editor.workingCopy);
		op3.setCommand("Color");
		op3.setSuperParameter(pds);
		editor.getOperationList().add(op3);
		
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setX((long)36.00);
		pds.setY((long)145);
		pds.setShapeID("Cir_1");
		PDOperation op4 = new PDOperation(editor.workingCopy);
		op4.setCommand("Move");
		op4.setSuperParameter(pds);
		editor.getOperationList().add(op4);
		
		//test commutativity with first operation, New(Cir_1).
		List<PDOperation> com = editor.getOperationList().commutative(op1);
		assertEquals(0, com.size());
		com.clear();
		
		//test commutativity with second operation, Copy(Cir_1, Cir_2).
		com = editor.getOperationList().commutative(op2);
		assertEquals(0, com.size());
		com.clear();
		
		//test commutativity with third operation, Color(Cir_1, #ff2233).
		com = editor.getOperationList().commutative(op3);
		assertEquals(1, com.size());
		assertEquals(op4.getId(), com.get(0).getId());
		com.clear();
		
		//test commutativity with fourth operation, Move(Cir_1, 36, 145).
		com = editor.getOperationList().commutative(op4);
		assertEquals(1, com.size());
		assertEquals(op3.getId(), com.get(0).getId());
		com.clear();
		
	}
	
	/**
	 * Test shape disjointness with no copy operations
	 */
	public final void testShapeDisjointNonCopy(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op1 = new PDOperation(editor.workingCopy);
		op1.setCommand("New");
		op1.setSuperParameter(pds);
		this.editor.getOperationList().add(op1);
		
		//add rectangle operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Rec_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op2 = new PDOperation(editor.workingCopy);
		op2.setCommand("New");
		op2.setSuperParameter(pds);
		this.editor.getOperationList().add(op2);

		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		PDOperation op3 = new PDOperation(editor.workingCopy);
		op3.setCommand("Color");
		op3.setSuperParameter(pds);
		editor.getOperationList().add(op3);
		
		//add move operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setX((long)36.00);
		pds.setY((long)145);
		pds.setShapeID("Rec_1");
		PDOperation op4 = new PDOperation(editor.workingCopy);
		op4.setCommand("Move");
		op4.setSuperParameter(pds);
		editor.getOperationList().add(op4);
		
		//add move operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setX((long)36.00);
		pds.setY((long)145);
		pds.setShapeID("Cir_1");
		PDOperation op5 = new PDOperation(editor.workingCopy);
		op5.setCommand("Move");
		op5.setSuperParameter(pds);
		editor.getOperationList().add(op5);
		
		//add move operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setX((long)360);
		pds.setY((long)145);
		pds.setShapeID("Cir_1");
		PDOperation op6 = new PDOperation(editor.workingCopy);
		op6.setCommand("Move");
		op6.setSuperParameter(pds);
		editor.getOperationList().add(op6);
		
		//test commutativity with first operation, New(Cir_1).
		List<PDOperation> com = editor.getOperationList().commutative(op1);
		assertEquals(1, com.size());
		assertEquals(op2.getId(), com.get(0).getId());
		com.clear();
		
		//test commutativity with second operation, New(Rec_1).
		com = editor.getOperationList().commutative(op2);
		assertEquals(2, com.size());
		assertEquals(op1.getId(), com.get(0).getId());
		assertEquals(op3.getId(), com.get(1).getId());
		com.clear();
		
		//test commutativity with third operation, Color(Cir_1, #ff2233).
		com = editor.getOperationList().commutative(op3);
		assertEquals(4, com.size());
		assertEquals(op2.getId(), com.get(0).getId());
		assertEquals(op4.getId(), com.get(1).getId());
		assertEquals(op5.getId(), com.get(2).getId());
		assertEquals(op6.getId(), com.get(3).getId());
		com.clear();
		
		//test commutativity with fourth operation, Move(Rec_1, 36, 145).
		com = editor.getOperationList().commutative(op4);
		assertEquals(3, com.size());
		assertEquals(op3.getId(), com.get(0).getId());
		assertEquals(op5.getId(), com.get(1).getId());
		assertEquals(op6.getId(), com.get(2).getId());
		com.clear();
		
		//test commutativity with fifth operation, Move(Cir_1, 36, 145).
		com = editor.getOperationList().commutative(op5);
		assertEquals(3, com.size());
		assertEquals(op4.getId(), com.get(0).getId());
		assertEquals(op3.getId(), com.get(1).getId());
		assertEquals(op2.getId(), com.get(2).getId());
		com.clear();
		
		//test commutativity with fifth operation, Move(Cir_1, 360, 145).
		com = editor.getOperationList().commutative(op6);
		assertEquals(0, com.size());
		com.clear();
	}
	
	/**
	 * Test type disjointness with no copy operations
	 */
	public final void testTypeDisjointNonCopy(){
		//add circle operation
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID("Cir_1");
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) 30);
		pds.setY((long) 30);
		PDOperation op1 = new PDOperation(editor.workingCopy);
		op1.setCommand("New");
		op1.setSuperParameter(pds);
		this.editor.getOperationList().add(op1);

		//add colour operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor("#ff2233");
		pds.setShapeID("Cir_1");
		PDOperation op2 = new PDOperation(editor.workingCopy);
		op2.setCommand("Color");
		op2.setSuperParameter(pds);
		editor.getOperationList().add(op2);
		
		//add move operation
		pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setX((long)36.00);
		pds.setY((long)145);
		pds.setShapeID("Cir_1");
		PDOperation op3 = new PDOperation(editor.workingCopy);
		op3.setCommand("Move");
		op3.setSuperParameter(pds);
		editor.getOperationList().add(op3);
		
		//test commutativity with first operation, New(Cir_1).
		List<PDOperation> com = editor.getOperationList().commutative(op1);
		assertEquals(0, com.size());
		com.clear();
		
		//test commutativity with second operation, Color(Cir_1, #ff2233).
		com = editor.getOperationList().commutative(op2);
		assertEquals(1, com.size());
		assertEquals(op3.getId(), com.get(0).getId());
		com.clear();
		
		//test commutativity with second operation, Move(Cir_1, 36, 145).
		com = editor.getOperationList().commutative(op3);
		assertEquals(1, com.size());
		assertEquals(op2.getId(), com.get(0).getId());
		com.clear();
	}
	
}
