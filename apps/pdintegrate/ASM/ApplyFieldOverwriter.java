package pdintegrate.ASM;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


/**
 * 
 * This class's main method is one possible entry point for FieldOverwriteClassVisitor.
 * 
 * The current work-flow is that FieldOverwriteClassVisitor only rewrites a single class, 
 * whereas this "script" or program is called on a collection of classes to rewrite them all
 * in a mutually aware manner.
 * 
 * Basic Strategy:
 * 
 *
 * 
 * @author dbra072
 *
 */
public class ApplyFieldOverwriter {
	public static void rewrite() {

	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ClassVisitationMediator discoveredClasses = new ClassVisitationMediator();
		discoveredClasses.discover(Rectangle.class);

		//1) We have a list of locations of classes we want to be rewritten.  
		//   We put these into a data structure that can associate:
		// A: the set of Classes that have been discovered needing rewriting.

		//2) Apply transformation to each class ci, saving out c'i
		try {
			//
			for(ClassInfo originalClass: discoveredClasses) {
				ClassReader cr = new ClassReader(originalClass.getOldName());
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

				ClassVisitor cv = new PrintlnFieldVisitor(cw, discoveredClasses);

				cr.accept(cv, ClassReader.SKIP_DEBUG);

				FileOutputStream fos = new FileOutputStream("bin/" +originalClass.getNewName() + ".class");

				fos.write(cw.toByteArray());
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			System.err.println("At least one class failed to process.");
			e.printStackTrace();
		}


		//
		//3) Load back the new classes c'i

		try {
			ClassLoader.getSystemClassLoader().loadClass("pdintegrate.ASMtest.RectangleRewrite");
			Class k = Class.forName("pdintegrate.ASMtest.RectangleRewrite");
			Object o = k.getConstructor(Integer.TYPE, Integer.TYPE).newInstance(0, 0);
			k.getMethod("setWidth", Integer.TYPE).invoke(o, 2);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
