package pdintegrate.ASM;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * This abstract class does almost all the work for overwriting fields in another class.
 * 
 * Only overwrite the two abstract methods to set how each private field of the rewritten class is
 * written and how it is read.
 * 
 * 
 * @author dbra072
 *
 */
public abstract class FieldOverwriteClassVisitor extends ClassAdapter {
	
	public static final String CLEAN_SET = "cleanSet";
	public static final String CLEAN_GET = "cleanGet";

	private ClassVisitor cw;
	private ClassInfo cI;
	private ClassVisitationMediator mediator;


	/**
	 * 
	 * @param cw
	 */
	public FieldOverwriteClassVisitor(ClassVisitor cw, ClassVisitationMediator mediator) {
		super(cw);
		this.cw = cw;
		this.mediator = mediator;
	}

	@Override 
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		
			

			try {
				this.mediator.discover(Class.forName(name.replace('/', '.')));
			} catch (ClassNotFoundException e) {
				//Cannot find a given class.  This MUST cause a failure, but the signature prohibits exceptions
				//Therefore we throw an error.
				throw new Error(e);
			}

		this.cI = this.mediator.lookup(name);
		cw.visit(version, access, cI.getNewName(), signature, superName, interfaces);
	}


	@Override
	public MethodVisitor visitMethod(int access,
			String name,
			String desc,
			String signature,
			String[] exceptions) {

		return new FieldOverwriteMethodVisitor(cw.visitMethod(access, name, desc, signature, exceptions));
	}

	/**
	 * This method is called at the very end of processing the bytecode of the class.  We take this opportunity to emit 
	 * the bytecode for the clean-get methods to be added to this class instead of the fields.
	 */
	@Override
	public void visitEnd() {
		for (FieldInfo f : this.cI.getVictimFields().values()) {
			String retrieveMethodName = CLEAN_GET + f.name;
			String updateMethodName = CLEAN_SET + f.name;

			this.retrieveMethodVisit(cw.visitMethod(f.access, retrieveMethodName , "()" + f.desc, f.signature, new String[0]), f, retrieveMethodName, f.value);

			this.updateMethodVisit(cw.visitMethod(f.access, updateMethodName, "(" + f.desc + ")V", f.signature, new String[0]), f,  updateMethodName, f.value);
		}


	}

	@Override
	public FieldVisitor visitField(final int access,
			final String name,
			final String desc,
			final String signature,
			final Object value) {

		//When you visit a field, we prepare to note it for rewriting.

		
		if (access != Opcodes.ACC_FINAL) {

			//Make a note to rewrite this field.  Note that visit() is going to be called once and only once.
			this.cI.addVictimField(new FieldInfo(access, name, desc, signature, new String[0]));

			//This version does not maintain the field.
			return null;
		} else {
			//Continuing on
			return cw.visitField(access, name, desc, signature, value);
		}


	}

	protected abstract void updateMethodVisit(MethodVisitor mv, FieldInfo oldField, String newFieldName, Object value);

	protected abstract void retrieveMethodVisit(MethodVisitor mv, FieldInfo oldField, String newFieldName, Object value);

	/**
	 * Method-visitor to trap getfield() and setfield() instructions and turn them into method calls.
	 * @author dbra072
	 */
	class FieldOverwriteMethodVisitor extends MethodAdapter {



		public FieldOverwriteMethodVisitor(MethodVisitor m) {
			super(m);
		}

		@Override
		/**
		 * This method replaces <i>ANY NON-FINAL<i> field accesses with calls to a clean-get or a clean-set method.
		 * 
		 * Warning: this method should only replace "owner" with translated className if it must.
		 */
		public void visitFieldInsn(int opcode, String owner, String name,
				String desc) {
			try {
				if (mediator.discovered(owner)) {
					ClassInfo ownerClass = mediator.lookup(owner);
					
					if (ownerClass.getVictimFields().containsKey(name)) {

						if (opcode == Opcodes.GETFIELD) {
							//if read and toChange
							super.mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerClass.getNewName(), CLEAN_GET + name,  "()" + desc);


						} else if (opcode == Opcodes.PUTFIELD ) {
							//else if write and toChange
							super.mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerClass.getNewName(), CLEAN_SET + name,"(" + desc + ")V");

						} 
					} else if (owner == ownerClass.getOldName()){
						// Field doesn't need replacing, but the owner name needs updating
						super.mv.visitFieldInsn(opcode, ownerClass.getNewName(), name, desc);
					}
				} else {
					// Nothing needs updating.
					super.mv.visitFieldInsn(opcode, owner, name, desc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
				String desc) {
			if (mediator.discovered(owner)) 
				super.mv.visitMethodInsn(opcode, mediator.lookup(owner).getNewName(), name, desc);
			else 
				super.mv.visitMethodInsn(opcode, owner, name, desc);

		}

	}

}