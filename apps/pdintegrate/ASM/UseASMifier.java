package pdintegrate.ASM;

import org.objectweb.asm.util.ASMifierClassVisitor;


public class UseASMifier {
	public static void main(String[] args) {
		String[] original = {"bin\\pdintegrate\\ASM\\PDRectangle.class"};
		String[] mutated = {"bin\\pdintegrate\\TestPDExtractionLayer.class"};
		
		System.out.println("test");
		
		try {
			ASMifierClassVisitor.main(original);
			System.out.println();
			System.out.println("***********************************");
			System.out.println();
			ASMifierClassVisitor.main(mutated);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
