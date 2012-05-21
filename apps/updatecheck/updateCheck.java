package updatecheck;

import java.io.*;

import org.objectweb.asm.*;

public class updateCheck {
	private final static int STANDARD = 0;
	private final static int READANDDELETE = 1;
	private static int strategy;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		strategy = STANDARD;
	
		String fqClassName = "updatecheck.ChangeBalance";
		
		//PARSE & ANALYSIS PHASE
		//System.out.println("Analysis Phase");
		RWOperation[] modifications = AnalyseClass(fqClassName);
		
		//MODIFICATIONS PHASE
		if (modifications.length > 0) { 
			System.out.println("Modifications Phase");
			//ModifyClass(fqClassName, modifications); //Disabled at the moment
		}
		System.out.println("Finished");
	}
	
	private static RWOperation[] AnalyseClass(String fqClassName){
		ClassReader cr;
		try {
			cr = new ClassReader(fqClassName);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("\nIOException when loading class.");
			return new RWOperation[0];
		}
		ClassAnalyser analyser = new ClassAnalyser();
		cr.accept(analyser, 0);
		RWOperation[] m = analyser.getModifications(); 
		System.out.println("Analysis Complete: " + m.length + " modification(s) needed.");
		return m;
	}
	
	private static void ModifyClass (String fqClassName, RWOperation[] modifications){
		ClassReader cr;
		ClassWriter cw = new ClassWriter(0);
		ClassModifier ca = new ClassModifier(cw, modifications, strategy); 
		// Events: cr -> ca(modifications) -> cw
		try {
			cr = new ClassReader(fqClassName);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("\nIOException when loading class.");
			return;
		}
		cr.accept(ca, 0);
		System.out.println("Modifications Complete");
	}
}
