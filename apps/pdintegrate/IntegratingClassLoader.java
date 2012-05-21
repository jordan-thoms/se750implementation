package pdintegrate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.sun.tools.internal.ws.processor.model.Model;

import pdintegrate.annotations.PDType;

public class IntegratingClassLoader extends ClassLoader{

	private DomainModel dm;
	ClassLoader oldCL = IntegratingClassLoader.class.getClassLoader();


	public IntegratingClassLoader(DomainModel dm) {
		super(IntegratingClassLoader.class.getClassLoader());
		this.oldCL = IntegratingClassLoader.class.getClassLoader();
		this.dm = dm;
	}

	public void main(String className, String[] args)  {
		try {
			//Before running the main method, transform EVERY DM Class.
			for (ClassInfo ci : dm) {
				this.loadClass(ci.getName().replace('/', '.'), true);
			}
			

			Class<?> c = this.loadClass(className, true);
			Class<?>[] argTypes = new Class[] { String[].class }; 
			Method main = c.getDeclaredMethod("main", argTypes);
			main.invoke(null, (Object)args);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Die to badly written code");
			System.exit(-1);
		}
	}

	@SuppressWarnings("rawtypes")
	public Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

		//Check for previously loaded class
		Class previouslyLoaded = this.findLoadedClass(className);
		if (previouslyLoaded != null) return previouslyLoaded;

		//Skip checking upper level class loader 
		

		try {
			byte data[] = loadClassData(className);
			Class<?> c = defineClass(className, data, 0, data.length);
			if (c == null)
				throw new ClassNotFoundException(className);
			if (resolve) resolveClass(c);
			return c;

		} catch (Exception e) {
			System.out.println("Failing for " + className);
			return this.getParent().loadClass(className);
			
		}
	}

	private byte[] loadClassData (String resourceName) throws Exception {
		ClassReader cr; 
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		try {
			cr = new ClassReader(ClassLoader.getSystemResourceAsStream(resourceName.replace('.', File.separatorChar)));
		} catch (IOException io) {
			cr = new ClassReader(this.getResourceAsStream(resourceName.replace('.', File.separatorChar) + ".class"));
		}
		IntegrationWeaver iw = new IntegrationWeaver(cw, dm);
		System.out.println("Got resource for " + resourceName);

		cr.accept(iw, ClassReader.SKIP_DEBUG);
		return cw.toByteArray();
	}
}
