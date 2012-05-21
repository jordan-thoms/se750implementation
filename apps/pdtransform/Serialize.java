package pdtransform;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pdstore.*;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDOrder;
import pdtransform.dal.PDPrintInstruction;
import pdtransform.dal.PDSerializer;
import pdtransform.dal.PDSerializerApplication;

/**
 * Uses a serializerApplication to serialize a model into a textual
 * representation
 * 
 * @author Philip Booth (pboo015)
 */

public class Serialize {

	private FileWriter fStream;
	protected BufferedWriter out;

	/**
	 * (TESTING PURPOSES) Used for indenting the printed output
	 * 
	 * @throws IOException
	 */
	// private static String indent = "";

	public Serialize(PDSerializerApplication sa, PDWorkingCopy cache)
			throws IOException {
		fStream = new FileWriter("c:/addressBook.html");
		out = new BufferedWriter(fStream);
		TreeWalker walker = new TreeWalker();
		serialize(sa, cache, walker);

	}

	private void serialize(PDSerializerApplication sa, PDWorkingCopy cache,
			TreeWalker walker) {
		try {
			fStream = new FileWriter("addressBook.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		out = new BufferedWriter(fStream);
		GUID input = null;
		// sa.getInput();

		if (input == null) {
			GUID guid = cache.getId(sa.getName().substring(0,
					sa.getName().lastIndexOf("_")));
			PDGeneratorApplication ga = (PDGeneratorApplication) cache.load(
					PDGeneratorApplication.typeId, guid);
			input = ga.getOutput();
			sa.setInput(input);
		}

		PDType inputType = sa.getInputType();

		PDInstance startElement = cache.load(inputType.getId(), input);

		process_serializer(startElement, sa, cache, walker);

		// Write to file? Need a stream?
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void process_serializer(PDInstance element,
			PDSerializerApplication sa, PDWorkingCopy cache, TreeWalker walker) {
		PDSerializer serializer = sa.getSerializer();
		Collection<PDPrintInstruction> instructions = serializer
				.getInstructions();

		try {

			PDPrintInstruction instruction = null;

			for (PDPrintInstruction i : instructions) {
				if (i.getType().getId().equals(element.getTypeId())) {
					instruction = i;
				}
			}

			if (instruction != null) {
				out.write(instruction.getPrintBefore());
				System.out.println(instruction.getPrintBefore());
			}

			ArrayList<Object> children = new ArrayList<Object>();
			Set<PDRole> useableRoles = new HashSet<PDRole>();

			PDType elementType = (PDType) cache.load(PDType.typeId,
					element.getTypeId());
			Collection<PDRole> roles = elementType.getAccessibleRoles();
			PDOrder order = sa.getOrder();

			// Get rid of roles without a name
			for (PDRole role : roles) {
				if (role.getName() != null)
					useableRoles.add(role);
			}

			if (useableRoles.size() > 1)
				children = walker.getOrderedChildren(useableRoles, element,
						order);
			else
				children = walker.getChildren(element, cache);

			// System.out.println(children);

			for (Object child : children) {
				if (child instanceof PDInstance)
					process_serializer((PDInstance) child, sa, cache, walker);
				else {
					out.write(child.toString());
					System.out.println(child);
				}
			}

			if (instruction != null) {
				out.write(instruction.getPrintAfter());
				System.out.println(instruction.getPrintAfter());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
