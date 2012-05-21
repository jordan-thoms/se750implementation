package updatecheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassAnalyser implements ClassVisitor {
	private final static int STANDARD = 0;
	private final static int READANDDELETE = 1;
	private String fqdn;
	private ArrayList<MethodAnalyser> methods = new ArrayList<MethodAnalyser>();
	private ArrayList<Object[][]> predecessorSuccessorMethodPairs = new ArrayList<Object[][]>();
	// predecessorSuccessorMethodPairs - Contains all the predecessor successor
	// pairs discovered in this method. Outer string array: 0 = Predecessor, 1 =
	// Successor. Inner String Array: 0 = Name, 1 = Method Parameters
	private ArrayList<RWOperation> modifications = new ArrayList<RWOperation>();

	@Override
	public void visit(int arg0, int arg1, String arg2, String arg3,
			String arg4, String[] arg5) {
		fqdn = arg2;
		System.out.println("Read Phase");
	}

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitEnd() {
		System.out.println("Read Complete");
		System.out.println("Analysis Phase");
		for (MethodAnalyser m : methods) {
			// collates all the data from the different methods
			predecessorSuccessorMethodPairs.addAll(m
					.getKnownPredecessorSuccessorPairs());
		}
		// To remove duplicates - unsure if this will work with 2D String
		// arrays, it does with just strings.
		HashSet<Object[][]> h = new HashSet<Object[][]>(
				predecessorSuccessorMethodPairs);
		predecessorSuccessorMethodPairs.clear();
		predecessorSuccessorMethodPairs.addAll(h);

		for (MethodAnalyser m : methods) {
			ArrayList<RWOperation> readOps = m.getReadOps();
			ArrayList<RWOperation> writeOps = m.getWriteOps();
			HashMap<Integer, Object[]> variables = m.getVariables();
			// Get successor methods
			ArrayList<Object[][]> successors = new ArrayList<Object[][]>();
			// Makes a list of all the predecessor-successor pairs which the
			// current method is a part of
			for (Object[][] s : predecessorSuccessorMethodPairs) {
				if ((s[0][0] == m.getName() && s[0][1] == m.getDesc())
						|| (s[1][0] == m.getName() && s[1][1] == m.getDesc())) {
					// Object[][] o = {s[1], s[2], s[3]};
					// successors.add(o);
					successors.add(s.clone());
				}
			}
			// Check each read operation against later write operations
			for (int j = 0; j < readOps.size(); j++) {
				RWOperation readOp = readOps.get(j);
				// Check against write ops in current method
				for (int i = 0; i < writeOps.size(); i++) {
					RWOperation writeOp = writeOps.get(i);
					if (((Integer) readOp.lineNo < (Integer) writeOp.lineNo)
							&& readOp.arguments.get(0).equals(
									writeOp.arguments.get(0))
							&& readOp.arguments.get(1).equals(
									writeOp.arguments.get(1))
							&& readOp.arguments.get(2).equals(
									writeOp.arguments.get(2))
							&& readOp.arguments.get(3).equals(
									writeOp.arguments.get(3))) {
						// If the argument for the read and write operations are
						// the same
						modifications.add(readOp);
						System.out.println();
						System.out
								.println("	Write operation on previously read data found.");
						System.out.println("	" + m.getName() + " "
								+ readOp.lineNo + ": "
								+ variables.get(readOp.arguments.get(3))[0]
								+ ".getInstance("
								+ variables.get(readOp.arguments.get(2))[0]
								+ ", "
								+ variables.get(readOp.arguments.get(1))[0]
								+ ", "
								+ variables.get(readOp.arguments.get(0))[0]
								+ ")");
						System.out.println("	" + m.getName() + " "
								+ writeOp.lineNo + ": "
								+ variables.get(writeOp.arguments.get(3))[0]
								+ ".addLink("
								+ variables.get(writeOp.arguments.get(2))[0]
								+ ", "
								+ variables.get(writeOp.arguments.get(1))[0]
								+ ", "
								+ variables.get(writeOp.arguments.get(0))[0]
								+ ")");
						System.out.println();
					}
				}
				// Check against write ops in successor methods
				for (Object[][] successor : successors) {
					for (MethodAnalyser m2 : methods) {
						if (m.getName() == m2.getName()
								&& m.getDesc() == m2.getDesc()) {
							// avoid checking itself
							continue;
						}
						if (m2.getName() == (String) successor[1][0]
								&& m2.getDesc() == (String) successor[1][1]
								&& readOp.lineNo < (Integer) successor[3][0]) {
							// When the second method is called after
							ArrayList<RWOperation> writeOpsSuc = m2
									.getWriteOps();
							for (int i = 0; i < writeOpsSuc.size(); i++) {
								RWOperation writeOp = writeOpsSuc.get(i);
								if (readOp.arguments.get(0).equals(
										successor[2][writeOp.arguments.get(0)])
										&& readOp.arguments.get(1).equals(
												successor[2][writeOp.arguments
														.get(1)])
										&& readOp.arguments.get(2).equals(
												successor[2][writeOp.arguments
														.get(2)])
										&& readOp.arguments.get(3).equals(
												successor[2][writeOp.arguments
														.get(3)])) {
									// If the arguments for the read and write
									// operations are the same
									modifications.add(readOp);
									HashMap<Integer, Object[]> m2Variables = m2
											.getVariables();
									System.out.println();
									System.out
											.println("	Write operation on previously read data found across two methods");
									System.out.println("	"
											+ m.getName()
											+ " "
											+ readOp.lineNo
											+ ": "
											+ variables.get(readOp.arguments
													.get(3))[0]
											+ ".getInstance("
											+ variables.get(readOp.arguments
													.get(2))[0]
											+ ", "
											+ variables.get(readOp.arguments
													.get(1))[0]
											+ ", "
											+ variables.get(readOp.arguments
													.get(0))[0] + ")");
									System.out.println("	"
											+ m2.getName()
											+ " "
											+ writeOp.lineNo
											+ ": "
											+ m2Variables.get(writeOp.arguments
													.get(3))[0]
											+ ".addLink("
											+ m2Variables.get(writeOp.arguments
													.get(2))[0]
											+ ", "
											+ m2Variables.get(writeOp.arguments
													.get(1))[0]
											+ ", "
											+ m2Variables.get(writeOp.arguments
													.get(0))[0] + ")");
									System.out.println();
								}
							}
						} else if (m.getName() == successor[1][0]
								&& m.getDesc() == successor[1][1]) {
							// When the first method is the called method, and
							// the other is the caller
							ArrayList<RWOperation> writeOpsSuc = m2
									.getWriteOps();
							for (int i = 0; i < writeOpsSuc.size(); i++) {
								RWOperation writeOp = writeOpsSuc.get(i);
								if (writeOp.lineNo > (Integer) successor[3][0]
										&& readOp.arguments.get(0).equals(
												successor[2][writeOp.arguments
														.get(0)])
										&& readOp.arguments.get(1).equals(
												successor[2][writeOp.arguments
														.get(1)])
										&& readOp.arguments.get(2).equals(
												successor[2][writeOp.arguments
														.get(2)])
										&& readOp.arguments.get(3).equals(
												successor[2][writeOp.arguments
														.get(3)])) {
									modifications.add(readOp);
									HashMap<Integer, Object[]> m2Variables = m2
											.getVariables();
									System.out.println();
									System.out
											.println("	Write operation on previously read data found across two methods");
									System.out.println("	"
											+ m.getName()
											+ " "
											+ readOp.lineNo
											+ ": "
											+ variables.get(readOp.arguments
													.get(3))[0]
											+ ".getInstance("
											+ variables.get(readOp.arguments
													.get(2))[0]
											+ ", "
											+ variables.get(readOp.arguments
													.get(1))[0]
											+ ", "
											+ variables.get(readOp.arguments
													.get(0))[0] + ")");
									System.out.println("	"
											+ m2.getName()
											+ " "
											+ writeOp.lineNo
											+ ": "
											+ m2Variables.get(writeOp.arguments
													.get(3))[0]
											+ ".addLink("
											+ m2Variables.get(writeOp.arguments
													.get(2))[0]
											+ ", "
											+ m2Variables.get(writeOp.arguments
													.get(1))[0]
											+ ", "
											+ m2Variables.get(writeOp.arguments
													.get(0))[0] + ")");
									System.out.println();
								}
							}

						}
					}
				}
			}
		}
	}

	@Override
	public FieldVisitor visitField(int arg0, String arg1, String arg2,
			String arg3, Object arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
			String arg3, String[] arg4) {
		// We want to look into all methods, so we make a MethodVistor and
		// return it to the ClassReader
		System.out.println("Visiting method '" + arg1 + "'");
		MethodVisitor mv = new MethodAnalyser();
		((MethodAnalyser) mv).giveInfo(fqdn, arg1, arg2);
		methods.add((MethodAnalyser) mv);
		return mv;
	}

	@Override
	public void visitOuterClass(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSource(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public RWOperation[] getModifications() {
		return modifications.toArray(new RWOperation[modifications.size()]);
	}

}
