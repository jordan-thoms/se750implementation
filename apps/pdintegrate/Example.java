package pdintegrate;

import pdintegrate.ASM.PDPoint;
import pdstore.GUID;


public class Example {

	public static void main(String[] args) {

		DomainModel dm = new DomainModel(new GUID("126e55a2bed011dfaaa4005056c00001"));
		IntegratingClassLoader test = new IntegratingClassLoader(dm);
		dm.register(PDPoint.class);
		test.main("pdintegrate.ASM.TestPDRectangle", new String[0]);
	}

}
