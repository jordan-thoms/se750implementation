package pdtransform;

import pdstore.*;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDHTMLTag;
import pdtransform.dal.PDLITag;
import pdtransform.dal.PDPrintInstruction;
import pdtransform.dal.PDSerializer;
import pdtransform.dal.PDSerializerApplication;
import pdtransform.dal.PDULTag;

public class TransformAndHTMLSerialize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			String dbString = "";
			String appName = "";

			String[] transformArgs;
			
			if (args.length == 2) {
				dbString = args[0];
				appName = args[1];
				
				transformArgs = new String[2];
				transformArgs[0] = dbString;
				transformArgs[1] = appName;
			} else if (args.length == 1) {
				dbString = PDTransform.defaultDB;
				appName = args[0];
				
				transformArgs = new String[1];
				transformArgs[0] = appName;
			} else {
				System.out.println("Need Application name as input");
				return;
			}
			
			PDTransform.main(transformArgs);

			PDWorkingCopy cache = new PDSimpleWorkingCopy(new PDStore(dbString));
			
			GUID appGUID = cache.getId(appName);
			PDGeneratorApplication ga = (PDGeneratorApplication) cache.load(
					PDGeneratorApplication.typeId, appGUID);

			PDSerializer serializer = (PDSerializer)cache.newInstance(PDSerializer.typeId);

			PDPrintInstruction htmlInstruction = (PDPrintInstruction)cache.newInstance(PDPrintInstruction.typeId);
			PDPrintInstruction ulInstruction = (PDPrintInstruction)cache.newInstance(PDPrintInstruction.typeId);
			PDPrintInstruction liInstruction = (PDPrintInstruction)cache.newInstance(PDPrintInstruction.typeId);

			htmlInstruction.setPrintBefore("<html>");
			htmlInstruction.setPrintAfter("</html>");
			htmlInstruction.setType(PDHTMLTag.typeId);

			ulInstruction.setPrintBefore("<ul>");
			ulInstruction.setPrintAfter("</ul>");
			ulInstruction.setType(PDULTag.typeId);

			liInstruction.setPrintBefore("<li>");
			liInstruction.setPrintAfter("</li>");
			liInstruction.setType(PDLITag.typeId);

			serializer.addInstruction(htmlInstruction);
			serializer.addInstruction(ulInstruction);
			serializer.addInstruction(liInstruction);

			PDSerializerApplication sa = (PDSerializerApplication)cache.newInstance(PDSerializerApplication.typeId);
			sa.setSerializer(serializer);
			sa.setInput(ga.getOutput());
			sa.setInputType(ga.getGenerator().getOutputType());
			sa.setOrder(ga.getGenerator().getOrder());

			sa.setName(appName + PDTransform.serializer);

			cache.commit();

			transformArgs[0] = appName + PDTransform.serializer;

			PDTransform.main(transformArgs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
