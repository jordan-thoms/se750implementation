package updatecheck;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAnalyser implements MethodVisitor {
	private final static int STANDARD = 0;
	private final static int READANDDELETE = 1;
	private int currentLineNumber;
	private String fqdn;
	private String name;
	private String desc;
	private ArrayList<Integer[]> operandStack = new ArrayList<Integer[]>(); //Pairs of ints representing objects loaded into the operand stack. The first is the index (unique within a method, so can be used to identify the variable), second is the type (using Opcodes.ALOAD, ILOAD, etc).
	private ArrayList<RWOperation> readOps = new ArrayList<RWOperation>();
	private ArrayList<RWOperation> writeOps = new ArrayList<RWOperation>();
	private HashMap<Integer, Object[]> variables = new HashMap<Integer, Object[]>(); //Key is the index, Object array is {name, type, signature, start label, end label}
	private ArrayList<Object[][]> predecessorSuccessorMethodPairs = new ArrayList<Object[][]>(); //Contains all the predecessor successor pairs discovered in this method. Outer string array: 0 = Predecessor, 1 = Successor, 2 = Array containing the indexes of the variables passed in, 3 = array containing just the line number. Inner String Array: 0 = Name, 1 = Method Parameters

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitCode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitIincInsn(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitInsn(int arg0) {
		// TODO Auto-generated method stub
		// addition etc operations. arg0 = whatever IADD is is addition.
		// apparently a lot of other stuff too
		if(arg0 >= Opcodes.IADD && arg0 <= Opcodes.DDIV){ 
			//Covers addition, subtraction, multiplication and subtraction of integers, floats, doubles and longs
			//removes the two values consumed from the stack and inserts a placeholder
			operandStack.remove(operandStack.size()-1);
			operandStack.remove(operandStack.size()-1);
			operandStack.add(new Integer[]{-1, -1});
		}

	}

	@Override
	public void visitIntInsn(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitJumpInsn(int arg0, Label arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLabel(Label arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLdcInsn(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLineNumber(int arg0, Label arg1) {
		currentLineNumber = arg0;
		//System.out.println("Source Code Line " + arg0);

	}

	@Override
	public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
		// This is run after all the code.
		//		System.out.println("	Local Variable Visited");
		//		System.out.println("	Name = " + arg0); // What the variable was called in the source code
		//		System.out.println("	Desc = " + arg1); // Type
		//		System.out.println("	Signature = " + arg2); // Unknown, usually null
		//		System.out.println("	Start Label = " + arg3); // The label where the variable's scope starts
		//		System.out.println("	End Label = " + arg4); // The label where the variable's scope ends
		//		System.out.println("	Index = " + arg5); // Location it's stored? Matches the number used in ALOAD statements - useful for identifying what they were. Only valid within a method
		Object[] o = { arg0, arg1, arg2, arg3, arg4 };
		variables.put(arg5, o);
	}

	@Override
	public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMaxs(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
		// The operations we want match this visitMethodInsn(INVOKEVIRTUAL, "pdstore/PDStore", "getInstance", 
		// "(Lpdstore/GUID;Ljava/lang/Object;Lpdstore/GUID;)Ljava/lang/Object;");
		if (arg0 == Opcodes.INVOKEVIRTUAL && arg1.equals("pdstore/PDStore")) {
			if (arg2.equals("getInstance")) {
				System.out.println("Read Operation (getInstance) on Source Code Line " + currentLineNumber);
				// arguments should contain the var locations of [PDStore object, transaction object, PD??? 1 ID, PD??? 2 ID]
				// This set of 4 ints should identify operations in the same transaction working on the same object in the database.
				ArrayList<Integer> args = new ArrayList<Integer>();
				//Retrieves the relevant arguments (The 4 most recent ALOADS, which should be the right ones)
				//System.out.println("OSsize="+operandStack.size());
				while(operandStack.size()>0){
					Integer[] a = operandStack.get(operandStack.size()-1);
					operandStack.remove(operandStack.size()-1);
					if(a[1] == Opcodes.ALOAD){
						args.add(a[0]);
					}
					if(args.size() > 3){
						break;
					}
				}				
				//System.out.println(args.get(0) + " " + args.get(1) + " " + args.get(2) + " " + args.get(3));
				readOps.add(new RWOperation(fqdn, name, desc, currentLineNumber, args));//(ArrayList<Integer>) operandStack.clone()));
				//operandStack.clear();
			} else if (arg2.equals("removeLink")) {
				System.out.println("Write Operation Precursor (removeLink) on Source Code Line " + currentLineNumber);
				//operandStack.clear();
				// We don't particularly care about this, it should be followed by an addLink in writes anyway.
			} else if (arg2.equals("addLink")) {
				System.out.println("Write Operation (addLink) on Source Code Line " + currentLineNumber);
				ArrayList<Integer> args = new ArrayList<Integer>();
				//Retrieves the relevant arguments (The 4 most recent ALOADS, which should be the right ones)
				while(operandStack.size()>0){
					Integer[] a = operandStack.get(operandStack.size()-1);
					operandStack.remove(operandStack.size()-1);
					if(a[1] == Opcodes.ALOAD){
						args.add(a[0]);
					}
					if(args.size() > 3){
						break;
					}
				}
				//System.out.println(args.get(0) + " " + args.get(1) + " " + args.get(2) + " " + args.get(3));
				writeOps.add(new RWOperation(fqdn, name, desc, currentLineNumber, args));//(ArrayList<Integer>) operandStack.clone()));
				//operandStack.clear();
			}
		} else if (arg1.equals(fqdn)) { 
			//Catches references to methods from the same .class file
			//This section parses the string which describes the arguments of the method.
			String[] argDescs = arg3.split(";");
			//Stores the indexes of the variables passed to the method in the proper order
			Integer[] varArgRelationship = new Integer[argDescs.length]; 
			int j = 0;
			if (argDescs.length > 0) {
				for (int i = argDescs.length - 1; i >= 0; i--) { 
					//Works backwards because it doesn't know in advance how many matching arguments there will be.
					if (argDescs[i].contains("Lpdstore/GUID") || argDescs[i].contains("Lpdstore/PDStore")) {
						while(operandStack.size()>0){
							Integer[] a = operandStack.get(operandStack.size()-1);
							operandStack.remove(operandStack.size()-1);
							if(a[1] == Opcodes.ALOAD){
								varArgRelationship[i] = a[0];
								break;
							}
						}
						j++;
					}
				}
			}
			
			if (j >= 4) { 
				//For a read and write operation pair to match the PDStore, transactionID, ObjectID and roleID GUID objects must match, so the successor method must be passed these 4 objects.
				Object[][] p = { { name, desc }, { arg2, arg3 }, varArgRelationship , new Object[]{currentLineNumber}};
				predecessorSuccessorMethodPairs.add(p);
			}
		} else {
			//Catches all other method instantiations
			//This part should modify the operandStack as necessary, removing and adding elements.
		}
	}

	@Override
	public void visitMultiANewArrayInsn(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label[] arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTypeInsn(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitVarInsn(int arg0, int arg1) {
		//Covers all the load statements: ILOAD, LLOAD, FLOAD, DLOAD, ALOAD 
		if(arg0 >= Opcodes.ILOAD && arg0 <= Opcodes.ALOAD){ 
			// Picks up the loading of variables for a method and stores them in an arraylist. ALOAD means it's loading a non-primitive
			operandStack.add(new Integer[]{arg1, arg0});
		}else if(arg0 >= Opcodes.ISTORE && arg0 <= Opcodes.ASTORE){
			if(operandStack.size() > 0){
				operandStack.remove(operandStack.size()-1);
			}
		}
	}

	public void giveInfo(String fqdnInput, String nameInput, String descInput) {
		//Used to feed information to the MethodAnalyser from the ClassAnalyser
		fqdn = fqdnInput;
		name = nameInput;
		desc = descInput;
	}

	public ArrayList<Object[][]> getKnownPredecessorSuccessorPairs() {
		return predecessorSuccessorMethodPairs;

	}

	public boolean equals(MethodAnalyser m) {
		if (m.fqdn == fqdn 
				&& m.name == name 
				&& m.desc == desc) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals(String fqdnI, String nameI, String descI) {
		if (fqdnI == fqdn 
				&& nameI == name 
				&& descI == desc) {
			return true;
		} else {
			return false;
		}
	}

	public boolean parentOf(RWOperation op) {
		if (fqdn == op.fqdn 
				&& name == op.methodName 
				&& desc == op.methodDesc) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<RWOperation> getReadOps() {
		return readOps;
	}

	public ArrayList<RWOperation> getWriteOps() {
		return writeOps;
	}

	public HashMap<Integer, Object[]> getVariables() {
		// TODO Auto-generated method stub
		return variables;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public String getDesc() {
		// TODO Auto-generated method stub
		return desc;
	}
}
