package pdtransform;

import pdstore.*;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDSerializerApplication;

/**
 * Using a generator, takes input in the form of PDStore's PDModel data
 * structures and converts it into another representation designated by the
 * generator.
 * 
 * @author Philip Booth (pboo015)
 */
public class PDTransform {
	public static final String defaultDB = "PDTransform";
	public static final String serializer = "_serializer";

	public static void loadClasses() {
		try {
			Class.forName("pdtransform.dal.PDGenerator");
			Class.forName("pdtransform.dal.PDAddressBook");
			Class.forName("pdtransform.dal.PDHTMLTag");
			Class.forName("pdtransform.dal.PDText");
			Class.forName("pdtransform.dal.PDULTag");
			Class.forName("pdtransform.dal.PDLITag");
			Class.forName("pdtransform.dal.PDMap");
			Class.forName("pdtransform.dal.PDContact");
			Class.forName("pdtransform.dal.PDOrder");
			Class.forName("pdtransform.dal.PDOrderedPair");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called with either one or two arguments. Generator Application name is
	 * compulsory, if the database is different from the default the database
	 * address string must be given as the first argument
	 * 
	 * @param args
	 *            - [dbString] GenAppName
	 */
	public static void main(String[] args) {
		loadClasses();
		try {
			String dbString = "";
			String appName = "";

			if (args.length == 2) {
				dbString = args[0];
				appName = args[1];
			} else if (args.length == 1) {
				dbString = PDTransform.defaultDB;
				appName = args[0];
			} else {
				System.out.println("Need Application name as input");
				return;
			}

			PDWorkingCopy cache = new PDSimpleWorkingCopy(new PDStore(dbString));

			if (cache.instanceExists(PDGeneratorApplication.typeId, appName)
					&& !appName.contains(PDTransform.serializer)) {

				GUID appGUID = cache.getId(appName);
				PDGeneratorApplication ga = (PDGeneratorApplication) cache
						.load(PDGeneratorApplication.typeId, appGUID);

				new Transform(cache, ga);
			} else if (cache.instanceExists(PDSerializerApplication.typeId,
					appName) && appName.contains(PDTransform.serializer)) {

				GUID appGUID = cache.getId(appName);
				PDSerializerApplication sa = (PDSerializerApplication) cache
						.load(PDSerializerApplication.typeId, appGUID);

				new Serialize(sa, cache);

				//TODO
				// GUID input = sa.getInput();
				// PDType inputType = sa.getInputType();
				// cache.load(inputType.getId(), input);
			}

			cache.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
