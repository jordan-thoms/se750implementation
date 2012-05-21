package pdtransform;

import pdstore.dal.PDGen;

/**
 * Batch running of PDGen
 * 
 * @author Philip Booth (pboo015)
 */

public class RunPDGen {

	public static void main(String[] arg) {
		String[] args = new String[1];
		args[0] = "PDTransform Model";
		PDGen.main(args);
		args[0] = "Address Book Model";
		PDGen.main(args);
		args[0] = "HTML Model";
		PDGen.main(args);
		// args[0] = "Library Model";
		// PDGen.main(args);
		args[0] = "Serializer Model";
		PDGen.main(args);
		// args[0] = "Code Model";
		// PDGen.main(args);
		// args[0] = "PD MetaModel";
		// PDGen.main(args);
	}
}
