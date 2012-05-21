package pdstore.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Starter extends Thread {
	
	Method m;
	Object callee;

	public Starter(Object callee, String methodName) {

		this.callee = callee;
		try {
			m = callee.getClass().getMethod(methodName, (Class<?>[])null);
		} catch (Exception e) {
           throw new RuntimeException(e);
		}
		this.start();
		
	}

	@Override
	public void run() {
		try {
			m.invoke(callee, (Object[])null);
		} catch (Exception e) {
	           throw new RuntimeException(e);
			}
	}

}
