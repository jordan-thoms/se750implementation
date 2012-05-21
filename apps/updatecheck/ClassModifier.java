package updatecheck;

import java.util.ArrayList;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassModifier extends ClassAdapter {
	private final static int STANDARD = 0;
	private final static int READANDDELETE = 1;
	private int strategy;
	private RWOperation[] modifications;
	private ArrayList<String> methodsToModify = new ArrayList<String>();

	public ClassModifier(ClassVisitor arg0, RWOperation[] modifications, int strategy) {
		super(arg0);
		this.modifications = modifications;
		for(RWOperation o : modifications){
			methodsToModify.add(o.getMethodName());
		}
		this.strategy = strategy;
	}

	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if(methodsToModify.contains(name)){ 
			// Only use the MethodModifier if there is a modification to do in this method
			mv = new MethodModifier(mv, modifications, strategy);
		}
		return mv;
	}

}
