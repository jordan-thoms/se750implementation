package pdintegrate.ASM;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class PrintlnFieldVisitor extends FieldOverwriteClassVisitor {

	
	public PrintlnFieldVisitor(ClassVisitor cw, ClassVisitationMediator mediator) {
		super(cw, mediator);
	}

	@Override
	protected void retrieveMethodVisit(MethodVisitor mv, FieldInfo f, String newFieldName,
			Object value) {
		mv.visitCode();
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("Retrieve " + f.name);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		
		if ("I".contains(f.desc)) {
			mv.visitInsn(Opcodes.ICONST_0);
			mv.visitInsn(Opcodes.IRETURN);
		} else if ("C".equals(f.desc)) {
			mv.visitInsn(Opcodes.ICONST_0);
			mv.visitInsn(Opcodes.IRETURN);
		} else if ("F".contains(f.desc)) {
			mv.visitInsn(Opcodes.FCONST_0);
			mv.visitInsn(Opcodes.FRETURN);
		} else if ("D".contains(f.desc)) {
			mv.visitInsn(Opcodes.DCONST_0);
			mv.visitInsn(Opcodes.DRETURN);
		} else if ("J".contains(f.desc)) {
			mv.visitInsn(Opcodes.LCONST_0);
			mv.visitInsn(Opcodes.LRETURN);
		} else { // Assume object reference
			mv.visitInsn(Opcodes.ACONST_NULL);
			mv.visitInsn(Opcodes.ARETURN);
		}
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	@Override
	protected void updateMethodVisit(MethodVisitor mv, FieldInfo f, String newFieldName,
			Object value) {
		System.out.println(f.desc);
		mv.visitCode();
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("Update " + f.name);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

	}

}
