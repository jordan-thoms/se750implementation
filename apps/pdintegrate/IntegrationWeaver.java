package pdintegrate;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import pdintegrate.annotations.PDAttribute;
import pdintegrate.annotations.PDType;
import pdstore.GUID;


/**
 * This class performs the integration weaving when a class is being loaded.
 * 
 * @author dbra072
 *
 */
public class IntegrationWeaver  extends ClassAdapter {

	private ClassVisitor cw;
	private DomainModel model;
	private String classname;
	
	public static final String CLEAN_SET = "cleanSet";

	/** Given a Domain Model, perform the instantiation of an integration layer.
	 * 
	 * @param cw A class visitor, usually a class writer
	 */
	public IntegrationWeaver(ClassVisitor cw, DomainModel model) {
		super(cw);
		this.cw = cw;
		this.model = model;
	}

	@Override 
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.classname = name;
		cw.visit(version, access, name, signature, superName, interfaces);


	}

	@Override
	public AnnotationVisitor visitAnnotation(String annotationName, boolean visible){
		if (annotationName.equals("L"+PDType.class.getCanonicalName().replace('.', '/') + ";")) {
			this.model.register(this.classname);
			this.model.lookup(this.classname).setExplicit(true);
		}
		return cw.visitAnnotation(annotationName, visible);
	}


	@Override
	public MethodVisitor visitMethod(int access,
			String name,
			String desc,
			String signature,
			String[] exceptions) {

		return new IntegratingMethodVisitor(cw.visitMethod(access, name, desc, signature, exceptions));
	}

	/**
	 * This method is called at the very end of processing the bytecode of the class.  We take this opportunity to emit 
	 * the bytecode for the clean-get methods to be added to this class instead of the fields.
	 */
	@Override
	public void visitEnd() {
		
		if (!model.discovered(classname.replace('.', '/'))) return;

		//Create the new fields
		FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "PDModelID", "Lpdstore/GUID;", null, null);
		fv.visitEnd();
		fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "PDTypeID", "Lpdstore/GUID;", null, null);
		fv.visitEnd();
		fv = cw.visitField(Opcodes.ACC_PUBLIC, "PDIdentity", "Lpdstore/GUID;", null, null);
		fv.visitEnd();

		// Create the code to initialise this class
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();

		/*
		 * 	public static GUID PDModelID = new GUID(ModelID);
		 */
		mv.visitTypeInsn(Opcodes.NEW, "pdstore/GUID");
		mv.visitInsn(Opcodes.DUP);
		mv.visitLdcInsn(model.ModelID.toString());
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "pdstore/GUID", "<init>", "(Ljava/lang/String;)V");
		mv.visitFieldInsn(Opcodes.PUTSTATIC, classname.replace('.', '/'), "PDModelID", "Lpdstore/GUID;");

		/*
		 * public static GUID PDTypeID = new GUID(TypeID);
		 */
		mv.visitTypeInsn(Opcodes.NEW, "pdstore/GUID");
		mv.visitInsn(Opcodes.DUP);
		mv.visitLdcInsn(model.getTypeID(classname));
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "pdstore/GUID", "<init>", "(Ljava/lang/String;)V");
		mv.visitFieldInsn(Opcodes.PUTSTATIC, classname.replace('.', '/'), "PDTypeID", "Lpdstore/GUID;");

		/*
		 * PDDataMapper.registerType(<classname>.class)
		 */
		mv.visitLdcInsn(Type.getType("L" + classname.replace('.', '/') + ";"));
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "pdintegrate/PDDataMapper", "registerType", "(Ljava/lang/Class;)V");
		
		for (FieldInfo f: model.lookup(classname.replace('.', '/')).getUniFields().values()) {

			mv.visitLdcInsn(Type.getType("L" + classname.replace('.', '/') + ";"));
			mv.visitLdcInsn(f.name);

			mv.visitTypeInsn(Opcodes.NEW, "pdstore/GUID");
			mv.visitInsn(Opcodes.DUP);

			mv.visitLdcInsn(f.getRoleID().toString());
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "pdstore/GUID", "<init>", "(Ljava/lang/String;)V");

			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "pdintegrate/PDDataMapper", "registerRole", "(Ljava/lang/Class;Ljava/lang/String;Lpdstore/GUID;)V");
		}


		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(4, 0);
		mv.visitEnd();
	}

	@Override
	public FieldVisitor visitField(final int access,
			final String name,
			final String desc,
			final String signature,
			final Object value) {

		FieldVisitor retval = new IntegratingFieldVisitor(cw, access, name, desc, signature, value);
		return retval;
	}


	class IntegratingFieldVisitor implements FieldVisitor {
		private FieldVisitor innerVisitor;
		private boolean annotated = false;
		private int access;
		private String name, desc, signature;
		private GUID roleID = null;

		public IntegratingFieldVisitor(ClassVisitor cw, int access, String name, String desc, String signature, Object value) {
			innerVisitor = cw.visitField(access, name, desc, signature, value);
			this.access = access;
			this.name = name;
			this.desc = desc;
			this.signature = signature;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (desc.equals("L"+PDAttribute.class.getCanonicalName().replace('.', '/') + ";")) {
				this.annotated = true;
			}

			return new FieldIntegratingAnnotationVisitor(innerVisitor.visitAnnotation(desc, visible));
		}

		@Override
		public void visitAttribute(Attribute attr) {
			innerVisitor.visitAttribute(attr);
		}
		
		protected void updateMethodVisit(MethodVisitor mv, FieldInfo f, String newFieldName,
				Object value) {
			
			boolean statik = (f.access & Opcodes.ACC_STATIC) != 0;
			int variable_register = 1;
			if (statik) variable_register--;
			
			mv.visitCode();
			
			if (!statik)  mv.visitVarInsn(Opcodes.ALOAD, 0);
			
			if (f.desc.length() == 1) {
			  try {
				mv.visitVarInsn(Opcodes.class.getField(f.desc.replace('J', 'L') + "LOAD").getInt(null), variable_register);
			} catch (Exception e) {
				e.printStackTrace();
			}
			} else {
				mv.visitVarInsn(Opcodes.ALOAD, variable_register);
			}
			if (!statik) {
				mv.visitFieldInsn(Opcodes.PUTFIELD, classname, name, desc);
				mv.visitVarInsn(Opcodes.ALOAD, 0);
			} else {
				mv.visitFieldInsn(Opcodes.GETSTATIC, classname, "PDTypeID", "Lpdstore/GUID;");
			}
				mv.visitTypeInsn(Opcodes.NEW, "pdstore/GUID");
				mv.visitInsn(Opcodes.DUP);
				mv.visitLdcInsn(f.getRoleID().toString());
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "pdstore/GUID", "<init>", "(Ljava/lang/String;)V");
				if (f.desc.length() == 1) {
				  try {
					mv.visitVarInsn(Opcodes.class.getField(f.desc.replace('J', 'L') + "LOAD").getInt(null), variable_register);
				} catch (Exception e) {
					e.printStackTrace();
				}
				} else {
					mv.visitVarInsn(Opcodes.ALOAD, variable_register);
				}
			
			//Boxing
			this.Box(f.desc, mv);

			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "pdintegrate/PDDataMapper", "addLink", "(Ljava/lang/Object;Lpdstore/GUID;Ljava/lang/Object;)V");
			
			
			if (f.desc.length() == 1) {
				  try {
					mv.visitVarInsn(Opcodes.class.getField(f.desc + "LOAD").getInt(null), variable_register);
				} catch (Exception e) {
					e.printStackTrace();
				}
				} else {
					mv.visitVarInsn(Opcodes.ALOAD, variable_register);
				}
			
			
			
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();

			
		}
		
	

		private void Box(String desc, MethodVisitor mv) {
			//			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			if (desc.equals("I")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			} else if (desc.equals("L")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(L)Ljava/lang/Long;");
			} else if(desc.equals("S")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			} else if(desc.equals("B")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(B)Ljava/lang/Boolean;");
			} else if(desc.equals("C")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			} else if(desc.equals("F")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			} else if(desc.equals("D")) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			} 
			
			
		}

		@Override
		public void visitEnd() {
			if ((access & Opcodes.ACC_FINAL) == 0 && 		    // Not a final field
					(access & Opcodes.ACC_TRANSIENT) == 0 &&    // Not a transient field
					model.discovered(classname)           &&    // Belongs to a Discovered Domain Model Object
					(!model.lookup(classname).isExplicit()) ||  // Not in an explicit class OR
						annotated) {                            // Explicitly Annotated
					//Make a note that this field is special		
					FieldInfo f = new FieldInfo(access,         //(access | Opcodes.ACC_PUBLIC) &~ Opcodes.ACC_PROTECTED &~ Opcodes.ACC_PRIVATE,
							name, desc, signature, new String[0]);
					f.roleID = roleID;
					model.lookup(classname).addUniField(f);
					this.innerVisitor.visitEnd();
					
					
					
					//Now's our chance to create a put method!
					String updateMethodName = CLEAN_SET + name;
					this.updateMethodVisit(cw.visitMethod(f.access, updateMethodName, "(" + f.desc + ")V", f.signature, new String[0]), f,  updateMethodName, f.value);
				} else {
					this.innerVisitor.visitEnd();
			}
		}
		
		
		class FieldIntegratingAnnotationVisitor implements AnnotationVisitor {
			
			private AnnotationVisitor innerVisitor;
			
			FieldIntegratingAnnotationVisitor(AnnotationVisitor innerVisitor) {
				this.innerVisitor = innerVisitor;
			}

			@Override
			public void visit(String name, Object value) {
				if (name.equals("Role_GUID")) {
					roleID = new GUID((String) value);
				}
				
				innerVisitor.visit(name, value);
			}

			@Override
			public void visitEnum(String name, String desc, String value) {
				innerVisitor.visitEnum(name, desc, value);
			}

			@Override
			public AnnotationVisitor visitAnnotation(String name, String desc) {
				return innerVisitor.visitAnnotation(name, desc);
			}

			@Override
			public AnnotationVisitor visitArray(String name) {
				return innerVisitor.visitArray(name);
			}

			@Override
			public void visitEnd() {
			}
		}
	}



	/**
	 * Method-visitor to add onto PUT instructions.
	 * @author dbra072
	 */
	class IntegratingMethodVisitor extends MethodAdapter {
		public IntegratingMethodVisitor(MethodVisitor m) {
			super(m);
		}

		@Override
		/**
		 * This method follows up <i>access to a shadowed field<i> with calls to a logging method.
		 * 
		 */
		public void visitFieldInsn(int opcode, String owner, String name,
				String desc) {
			try {
				if (model.discovered(owner)) {
					ClassInfo ownerClass = model.lookup(owner);
					if (ownerClass.getUniFields().containsKey(name) && opcode == Opcodes.PUTFIELD) {
						
						super.mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, CLEAN_SET + name,"(" + desc + ")V");
						
//						mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//						mv.visitLdcInsn("Update ");
//						mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
					} else if (ownerClass.getUniFields().containsKey(name) && opcode == Opcodes.PUTSTATIC) {
						super.mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, CLEAN_SET + name,"(" + desc + ")V");
					} else{
						// Field doesn't need replacing
						mv.visitFieldInsn(opcode, owner, name, desc);
					}
				} else {
					// Nothing needs updating.
					mv.visitFieldInsn(opcode, owner, name, desc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
				String desc) {
			if (model.discovered(owner)) 
				super.mv.visitMethodInsn(opcode, owner, name, desc);
			else 
				super.mv.visitMethodInsn(opcode, owner, name, desc);

		}*/
	}
}




