package pdintegrate.ASM;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class PDStoreConverter extends ClassAdapter {

	private ClassVisitor cw;
	private String fullClassName, translatedClassName;


	public PDStoreConverter(ClassVisitor cw) {
		super(cw);
		this.cw = cw;
	}

	@Override 
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.fullClassName = name;
		this.translatedClassName = fullClassName + "Rewrite";
		cw.visit(version, access, translatedClassName, signature, superName, interfaces);
	}


	@Override
	public MethodVisitor visitMethod(int access,
			String name,
			String desc,
			String signature,
			String[] exceptions) {

		return new ArrayifierMethodVisitor(cw.visitMethod(access, name, desc, signature, exceptions));
	}


	@Override
	public FieldVisitor visitField(final int access,
			final String name,
			final String desc,
			final String signature,
			final Object value) {

		//When you make a field, instead make something else.
		
		String retrieveMethodName = "retrieve" + name;
		String updateMethodName = "update" + name;
		
		this.retrieveMethodVisit(cw.visitMethod(access, retrieveMethodName , desc, signature, new String[0]), retrieveMethodName, value);

		this.updateMethodVisitor(cw.visitMethod(access, updateMethodName, desc, signature, new String[0]), updateMethodName, value);

		//Maintain the field?
		return new DropFieldVisitor();
	}

	private void updateMethodVisitor(MethodVisitor mv, String fieldName, Object value) {
		// Insert method code here
		
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, this.translatedClassName, fieldName, "[I");
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.IALOAD);
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();

	}

	private void retrieveMethodVisit(MethodVisitor mv, String fieldName, Object value) {
		// Insert retrieve method code here
		
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, this.translatedClassName, fieldName, "[I");
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.IALOAD);
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();

	}


	/**
	 * Method-visitor to trap getfield() and setfield() instructions and turn them into method calls.
	 * @author dbra072
	 */
	class ArrayifierMethodVisitor implements MethodVisitor {

		private MethodVisitor m;

		public ArrayifierMethodVisitor(MethodVisitor m) {
			this.m = m;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			return m.visitAnnotation(desc, visible);
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			return m.visitAnnotationDefault();
		}

		@Override
		public void visitAttribute(Attribute attr) {
			m.visitAttribute(attr);
		}

		@Override
		public void visitCode() {
			m.visitCode();
		}

		@Override
		public void visitEnd() {
			m.visitEnd();
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name,
				String desc) {
			// TODO OVERWRITE THIS!!
			
			//if read (mv.visitFieldInsn(GETFIELD, "pdintegrate/ASMtest/WhiteboardExample/RectangleArrayShape", "data", "[I");
				m.visitVarInsn(Opcodes.ALOAD, 0);
				//m.visitMethodInsn(Opcodes.INVOKESPECIAL, "retrieveHeight", "()I");
				m.visitInsn(Opcodes.IRETURN);
			
			//else if write mv.visitFieldInsn(SETFIELD, "pdintegrate/ASMtest/WhiteboardExample/RectangleArrayShape", "data", "[I"
			
			//else
			
			//do nothing
			
			
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack,
				Object[] stack) {
			m.visitFrame(type, nLocal, local, nStack, stack);
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			m.visitIincInsn(var, increment);
		}

		@Override
		public void visitInsn(int opcode) {
			m.visitInsn(opcode);
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			m.visitIntInsn(opcode, operand);
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			m.visitJumpInsn(opcode, label);
		}

		@Override
		public void visitLabel(Label label) {
			m.visitLabel(label);
		}

		@Override
		public void visitLdcInsn(Object cst) {
			m.visitLdcInsn(cst);
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			m.visitLineNumber(line, start);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature,
				Label start, Label end, int index) {
			m.visitLocalVariable(name, desc, signature, start, end, index);
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			m.visitLookupSwitchInsn(dflt, keys, labels);
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			m.visitMaxs(maxStack, maxLocals);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
				String desc) {
			m.visitMethodInsn(opcode, owner, name, desc);

		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			m.visitMultiANewArrayInsn(desc, dims);

		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter,
				String desc, boolean visible) {
			return m.visitParameterAnnotation(parameter, desc, visible);
		}

		@Override
		public void visitTableSwitchInsn(int min, int max, Label dflt,
				Label[] labels) {
			m.visitTableSwitchInsn(min, max, dflt, labels);
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler,
				String type) {
			m.visitTryCatchBlock(start, end, handler, type);
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			m.visitTypeInsn(opcode, type);
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			m.visitVarInsn(opcode, var);
		}
	}

	/**
	 * Do-nothing class that drops a field.
	 * @author dbra072
	 *
	 */
	class DropFieldVisitor implements FieldVisitor {

		public DropFieldVisitor() {
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			return null;
			// Do nothing
		}

		@Override
		public void visitAttribute(Attribute attr) {
			// Do nothing	
		}

		@Override
		public void visitEnd() {
			// Do nothing
		}

	}

}