package updatecheck;

import java.util.ArrayList;

import org.objectweb.asm.*;

public class MethodModifier extends MethodAdapter {
	private final static int STANDARD = 0;
	private final static int READANDDELETE = 1;
	private int strategy;
	private int currentLineNumber;
	private final String SELECTFORUPDATE = "getInstanceCHANGED"; //Since SELECT FOR UPDATE isn't implemented it's going to be left like this for now
	private ArrayList<Integer> arguments = new ArrayList<Integer>(); // Will always be size 4 or less.
	private RWOperation[] modifications;

	public MethodModifier(MethodVisitor arg0, RWOperation[] modifications, int strategy) {
		super(arg0);
		this.modifications = modifications;
		this.strategy = strategy;
		// TODO Auto-generated constructor stub
	}

	public void visitLineNumber(final int line, final Label start) {
		currentLineNumber = line;
		mv.visitLineNumber(line, start);
	}

	public void visitVarInsn(final int opcode, final int var) {
		if (opcode == Opcodes.ALOAD) { // Picks up the loading of variables for a method and stores them in an arraylist.
			arguments.add(var);
			if (arguments.size() > 4) {
				arguments.remove(0);
			}
		}
		mv.visitVarInsn(opcode, var);
	}

	public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
		String n = name;
		if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("pdstore/PDStore") && name.equals("getInstance")) {
			//Checks the line number and arguments of the getInstance method against the ones from the methods to be modified
			for(RWOperation op : modifications){ 
				if(op.lineNo == currentLineNumber 
						&& op.arguments.get(0) == arguments.get(0) 
						&& op.arguments.get(1) == arguments.get(1) 
						&& op.arguments.get(2) == arguments.get(2) 
						&& op.arguments.get(3) == arguments.get(3)){
					n = SELECTFORUPDATE;
				}
			}
		}
		mv.visitMethodInsn(opcode, owner, n, desc);
	}
}
