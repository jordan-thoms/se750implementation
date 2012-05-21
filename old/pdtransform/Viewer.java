package pdtransform;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.swing.JFrame;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGenerator;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDMap;
import pdtransform.dal.PDSerializerApplication;

/**
 * This class traverses the PDModel and using a JTreeView creates as graphical
 * representation of PDModels
 * 
 * @author Philip Booth (pboo015)
 */

public class Viewer {

	private TreeView treeView;
	private TreeWalker walker = new TreeWalker();
	private JFrame frame = new JFrame("PDViewer");
	private Container container = frame.getContentPane();

	public Viewer(PDInstance startElement, PDWorkingCopy cache,
			PDInstance application) {

		// Output Instance
		run(startElement, cache, application);

		container.setLayout(new FlowLayout());
		treeView.showGUI();

	}

	public void run(PDInstance startElement, PDWorkingCopy cache,
			PDInstance application) {
		// Set up tree
		treeView = new JTreeView(startElement, cache, application);

		// Traverse output template
		walker.gaOutputCursor.push(startElement);
		traverse_output(startElement, null, cache, application);

		treeView.expand();
		container.add((Component) treeView.getTree());

	}

	public void showGUI() {

		// Create and set up the window.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.

		frame.setLocation(100, 100);
		// frame.setSize(new Dimension(300, 500));

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Main recursive method, traverses through the output template cloning the
	 * element then checking if it has an associated mapping, executing that
	 * mapping if it does and then moves to the children by calling this method
	 * on them. This approach results in a depth first recursion. <br>
	 * <br>
	 * 
	 * @param element
	 *            - PDInstance of the output template instance to be processed
	 * @param parentRole
	 *            - Relation the instance "element" is to its parent, this is
	 *            used in the cloning process
	 * @param cache
	 *            - PDCache instance associated with this Generator Application
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 */
	private void traverse_output(PDInstance element, PDRole parentRole,
			PDWorkingCopy cache, PDInstance application) {
		if (parentRole != null)
			addNode(element, parentRole, walker.gaOutputCursor.peek());

		if (application instanceof PDGeneratorApplication) {
			ArrayList<PDMap> maps = checkMapping(element,
					(PDGeneratorApplication) application);
			addMaps(maps, element);
		}

		ArrayList<PDRole> roles = walker.getRoles(element, cache);

		for (PDRole role : roles) {
			Collection<Object> children = cache.getInstances(element,
					role.getId());

			if (!children.isEmpty()) {
				// System.out.println(children);
				for (Object c : children) {
					if (c instanceof PDInstance) {
						PDInstance child = (PDInstance) c;
						traverse_output(child, role, cache, application);
					} else {
						addNode(c, role, element);
					}
				}
			} else {
				addEmptyRole(role, element);
			}

		}

		walker.gaOutputCursor.pop();
	}

	private void addNode(Object element, PDRole role, PDInstance parent) {
		// Add node to tree
		treeView.addNode(parent, element, role);
		if (element instanceof PDInstance)
			walker.gaOutputCursor.push((PDInstance) element);
	}

	private void addMaps(ArrayList<PDMap> maps, PDInstance parent) {
		// Add map to tree
		treeView.addMaps(maps, parent);
	}

	private void addEmptyRole(PDRole role, PDInstance parent) {
		treeView.addEmptyRole(role, parent);
	}

	private ArrayList<PDMap> checkMapping(PDInstance element,
			PDGeneratorApplication ga) {
		PDGenerator gen = ga.getGenerator();
		Collection<PDMap> maps = gen.getMaps();
		ArrayList<PDMap> mapped = new ArrayList<PDMap>();

		for (PDMap map : maps) {
			GUID outputInstanceID = map.getOutputInstance();

			if (outputInstanceID.equals(element.getId())) {
				// ArrayList<PDInstance> mapped = executeMapping(map, element,
				// ga, walker);
				mapped.add(map);
			}
		}
		return mapped;
	}

	public static void loadFiles() {
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

	public static void main(String[] args) {
		loadFiles();

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
			PDGeneratorApplication ga = (PDGeneratorApplication) cache.load(
					PDGeneratorApplication.typeId, appGUID);

			PDGenerator gen = ga.getGenerator();
			GUID output_template = gen.getOutputTemplate();
			PDType outputType = gen.getOutputType();
			PDInstance element = cache
					.load(outputType.getId(), output_template);

			new Viewer(element, cache, ga);

		}

		else if (cache.instanceExists(PDSerializerApplication.typeId, appName)
				&& appName.contains(PDTransform.serializer)) {
			
			GUID appGUID = cache.getId(appName);
			PDSerializerApplication sa = (PDSerializerApplication) cache.load(
					PDSerializerApplication.typeId, appGUID);

			GUID input = sa.getInput();
			PDType inputType = sa.getInputType();
			PDInstance startElement = cache.load(inputType.getId(), input);

			new Viewer(startElement, cache, sa);
		} else
			System.out.println(appName
					+ " does not exist in the database, please commit");
	}
}
