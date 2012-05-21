package nz.ac.auckland.se.genoupe.parallel;


public class ApplyThread<Element> extends Thread {
	

	public ApplyThread(Applicable<Element> a, Element e,
			AbstractForEach<Element> caller) {
		super();
		this.a = a;
		this.e = e;
		this.caller = caller;
	}


	Applicable<Element> a;
	Element e;
	AbstractForEach<Element> caller;
	


	@Override
	public void run() {
		a.apply(e);
	}
	

}
