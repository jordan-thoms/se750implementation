package pdintegrate.ASM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;


import org.objectweb.asm.*;


public class MutateRectangle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Rectangle r = new Rectangle(2, 3);
		
		System.out.println("R has width " + r.getWidth() + ", and height " + r.getHeight());
		
		/**
		 * Change definition of rectangle here
		 */
		try {
			ClassReader reader = new ClassReader("pdintegrate.ASMtest.Rectangle");
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			reader.accept(writer, 0);
			FileOutputStream fos = new FileOutputStream("bin\\pdintegrate\\ASMtest\\MutatedRectangle.class");
			
			fos.write(writer.toByteArray());
			fos.flush();
			fos.close();
			
		    // Convert File to a URL
		    URL url = new File("bin\\pdintegrate\\ASMtest\\MutatedRectangle.class").toURL();          // file:/c:/myclasses/
		    URL[] urls = new URL[]{url};

		    // Create a new class loader with the directory
		    ClassLoader cl = new URLClassLoader(urls);

		    // Load in the class; MyClass.class should be located in
		    // the directory file:/c:/myclasses/com/mycompany
		    Class cls = cl.loadClass("pdintegrate.ASMtest.MutatedRectangle");
		    
		    Object r2 = cls.getConstructor(Integer.class, Integer.class).newInstance(2, 3);
		    
		    int a = (Integer) cls.getDeclaredMethod("getWidth", null).invoke(r2, null);
		    
		    int b = (Integer) cls.getDeclaredMethod("getHeight", null).invoke(r2, null);
		    
			System.out.println("R has width " + r.getWidth() + ", and height " + r.getHeight());
		    
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
