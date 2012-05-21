package tools;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.sun.tools.javac.util.List;

import junit.framework.TestCase;
import nz.ac.auckland.se.genoupe.parallel.AbstractForEach;
import nz.ac.auckland.se.genoupe.parallel.Applicable;
import nz.ac.auckland.se.genoupe.parallel.ParallelForEach;
import nz.ac.auckland.se.genoupe.tools.Stopwatch;

public abstract class ForEachTest extends TestCase {
	
	AbstractForEach<StringBuffer> p;

	public void testForEach() {
		ArrayList<StringBuffer> l = new ArrayList<StringBuffer>(10);
		SuffixAdd s = new SuffixAdd();
		for(int i=0; i<10;++i)
	    	l.add(new StringBuffer("S"+i));
		Object o = p.forEach(l , s);
		
		// The following is necessary to give asynchronous listener
		// time to finish
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=1; i<9;++i)
	    	System.err.println(l.get(i));

	}

	public void testInnerClass() {
		ArrayList<StringBuffer> l = new ArrayList<StringBuffer>(10);
		for(int i=21; i<31;++i)
			l.add(new StringBuffer("S"+i));
		
		p.forEach(l , 
				new Applicable<StringBuffer>() {

			@Override
			public void apply(StringBuffer hullo) {
				hullo.append("Hello");
			}
		});

		// The following is necessary to give asynchronous listener
		// time to finish
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=1; i<9;++i)
	    	System.err.println(l.get(i));

	}

}
