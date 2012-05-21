package pdedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import pdedit.dal.PDNode;
import pdedit.dal.util.ModelAccessor;
import pdedit.dal.util.PDEditPortal;
import pdedit.pdGraphWidget.DiagramElement;
import pdedit.pdGraphWidget.DiagramEvent;
import pdedit.pdGraphWidget.DiagramEventListener;
import pdedit.pdGraphWidget.DiagramLink;
import pdedit.pdGraphWidget.DiagramNode;
import pdedit.pdGraphWidget.ElementType;
import pdedit.pdGraphWidget.GraphWidget;
import pdedit.pdGraphWidget.PropertyWindow;
import pdedit.pdGraphWidget.SelectionChangedEvent;
import pdedit.pdGraphWidget.SelectionChangedEventListener;
import pdedit.pdShapes.Circle;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.concurrent.ConcurrentStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;


public class PDEdit implements ActionListener, SelectionChangedEventListener, DialogListener {
	private static JFrame frame;
	private static GraphWidget v;
	private static JPanel start;
	private static PDEdit app;
	private static PDStore store;
	private ArrayList<PDEditPortal> friends = new ArrayList<PDEditPortal>();
	private JScrollPane scroll;
	private boolean started;
	private ApplicationCommands commands = new ApplicationCommands();
	private final double zoomPercentage = 100.0;
	private int x = 50;
	private int y = 50;

	// Output pane
	private JSplitPane outputSplitPane;
	private JPanel outputPanel;
	private JTextArea output;
	private boolean outputVisible;
	private int outputHeight = 100;

	// Properties pane
	private JSplitPane propertiesSplitPane;
	private JPanel propertiesPanel;
	private JPanel propertiesViewer;
	private boolean propertiesVisible;
	private int propertiesWidth = 200;

	private static ModelAccessor editor;
	private static String modelName="";
	private static boolean isInit = false; 
	//private boolean doCreateDiagramModel = false;
	private PDEditSplash splash;
	private ListWidget openWidget;
	private final static String version = "Beta 1.3.0";
	private final static String pdeditDescription = "PDEdit: PDModeling Tool\nVersion: " +
	version + 
	"\nRecreated By Ted Yeung for The PDStore Group" +
	"\nModified by Yin (Chris) Bai, Sourabh Gupta and Craig Sutherland" + 
	"\nIcons by Yusuke Kamiyamane (http://p.yusukekamiyamane.com)";

	static {
		System.out.println(pdeditDescription);
		PDEditLogger.setHeader(pdeditDescription);
	}
	/**
	 * Runs stand alone app
	 */
	public PDEdit(){}
	public PDEdit(String storeName){
		PDEditLogger.startNewSession();
		oSCheck();
		splash.setVisible(true);
		splash.barPercent(1.0/7.0);
		startup(null,storeName);
		//Startpanel
		startPanelSetup();			
		editor.start();
		splash.barPercent(6.0/7.0);
		loadFrame();
		showPDModelWindow();
		splash.dispose();
		System.out.println(PDEditLogger.getLog());
		PDEditLogger.printOut();
		addOutputMessage(pdeditDescription);
	}

	private void startPanelSetup() {
		PDEditLogger.addToLog("Init start Bar . . .");
		v.disableWiget(true);
		v.add(addStartButtons());
		v.validate();

		frame.getContentPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (!started){
					isInit = true;
					v.add(addStartButtons());
				}
			}
		});
		PDEditLogger.appendDurationToLastMassage();
		PDEditLogger.newLine();
	}
	private void startup(String appName, String storeName) {
		PDEditLogger.addToLog("PDEdit Startup . . .");
		PDEditLogger.newLine();
		long time = System.currentTimeMillis();
		splash.barPercent(2.0/7.0);
		if (appName == null){
			appName = "PDEdit: PDModel Editor";
		}
		if (storeName == null){
			storeName = "pdedit";
		}
		intialSetup(appName,storeName);
		splash.barPercent(3.0/7.0);
		setupGraphWidget();
		splash.barPercent(4.0/7.0);
		diagramModelCheck();
		splash.barPercent(5.0/7.0);
		PDEditLogger.addToLog("PDEdit Startup . . . ");
		PDEditLogger.appendDurationToLastMassage(time);
		PDEditLogger.newLine();
	}

	private void loadFrame() {
		PDEditLogger.addToLog("Load Window . . .");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.out.println("Closing");
				PDEditLogger.printOut();
			}
		});
		frame.getContentPane().add(propertiesSplitPane);
		this.commands.initialise(this);
		frame.setJMenuBar(this.commands.getMenuBar());
		frame.getContentPane().add(this.commands.getToolBar(), BorderLayout.NORTH);
		splash.barPercent(7.0/7.0);
		frame.pack();
		frame.setSize(700, 600);
		PDEditLogger.appendDurationToLastMassage();
		PDEditLogger.newLine();
	}

	public void showPDModelWindow() {
		editor.start();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setVisible(true);
		Point location = new Point(
				(screen.width-frame.getWidth())/2,
				(screen.height-frame.getHeight())/2
		);
		frame.setLocation(location);
		frame.toFront();
	}

	public void closePDModelWindow() {
		frame.setVisible(false);
		frame.dispose();
	}

	private void diagramModelCheck() {
		GUID t = store.begin();
		if (store.getName(t, ModelAccessor.DiagramModelGUID)== null){
			editor.createDiagramModel();
		}

		ModelAccessor.LoadPDEditDAL();
	}

	private void setupGraphWidget() {
		scroll = new JScrollPane();
		v = new GraphWidget(scroll,zoomPercentage);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setViewportView(v);
		
		v.addSelectionChangedEventListener(this);

		v.addDiagramEventListener(new DiagramHandler(editor,friends));
		v.setLayout(null);
		v.setNodeDescription("Type");
		v.setLinkDescription("Relation");

		// Generate the output pane and wrap it in a scroller so it will scroll nicely
		output = new JTextArea();
		output.setEditable(false);
		outputPanel = new JPanel();
		outputPanel.setLayout(new BorderLayout());
		outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
		outputPanel.add(new JScrollPane(output),BorderLayout.CENTER);
		outputPanel.setVisible(false);
		
		outputSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, outputPanel);
		outputSplitPane.setResizeWeight(1.0);

		// Generate the properties pane
		propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new BorderLayout());
		propertiesPanel.setVisible(false);
		propertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
		propertiesViewer = new JPanel();
		propertiesViewer.setLayout(new BorderLayout());
		propertiesPanel.add(new JScrollPane(propertiesViewer));
		clearProperties("Nothing selected");
		propertiesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outputSplitPane, propertiesPanel);
		propertiesSplitPane.setResizeWeight(1.0);
	}

	/**
	 * Adds a message to the output pane.
	 * @param message
	 */
	public void addOutputMessage(String message){
		output.append(message + "\n");
		output.setCaretPosition(output.getDocument().getLength() - 1);
	}

	private void oSCheck() {
		PDEditLogger.addToLog("OS Check . . .");
		if (System.getProperty("os.name").contains("Mac")){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PDEdit");
		}
		splash = new PDEditSplash();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		PDEditLogger.appendDurationToLastMassage();
		PDEditLogger.newLine();
	}

	private void intialSetup(String nameApp, String editStore) {
		frame = new JFrame(nameApp);
		if(store == null){
			store = new PDStore(editStore);
		}

		List<PDListener<GUID,Object,GUID>> listeners = store.getDetachedListenerList();
		listeners.add(new PDListenerAdapter<GUID,Object,GUID>(){
			public void transactionCommitted(
					List<PDChange<GUID, Object, GUID>> transaction,
					List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
				System.out.println("=== START: Transaction Committed ===");
				for (PDChange<GUID, Object, GUID> change : transaction){
					System.out.println("==> " + change.toString());
				}
				System.out.println("=== FINISH: Transaction Committed ===");
			}
		});

		editor = new ModelAccessor(store);
		app = this;
	}

	/**
	 * Runs app with a pdstore other then the default
	 */
	public PDEdit(PDStore store){
		oSCheck();
		PDEdit.store = store;
		startup(null,null);
		startPanelSetup();
		loadFrame();
	}

	/**
	 * Runs pdedit with communitation interface
	 */
	public PDEdit(PDEditPortal portal){
		oSCheck();
		PDEdit.store = portal.getPDStore();
		friends.add(portal);
		startup(portal.getAppName(),portal.getStoreName());
		v.disableWiget(true);
		loadFrame();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String storeName = null;
		if (args.length > 0){
			storeName = args[0];
		}

		app = new PDEdit(storeName);
	}

	/**
	 * Enables the interface panel and remove the start bar
	 */
	public void initalized(){
		v.disableWiget(false);
		if (start != null){
			v.remove(start);
		}
	}

	/**
	 * Adds Start Bar to the graph widget
	 * @return A JPanel containing the startup buttons 
	 */
	public JPanel addStartButtons(){
		//remove old panel if exist
		if (start != null){
			start.setVisible(false);
			v.remove(start);
			v.validate();
			v.repaint();
		}
		// Create Panel
		start = new JPanel();
		start.setLayout(new BorderLayout());
		start.setBorder(new EmptyBorder(5, 5, 5, 5));
		start.setVisible(isInit);
		start.setBackground(GraphWidget.lightBrown);
		start.add(new JLabel("Welcome to PDEdit"), BorderLayout.PAGE_START);

		JPanel commands = new JPanel();
		commands.setBackground(GraphWidget.lightBrown);
		commands.setLayout(new BoxLayout(commands, BoxLayout.PAGE_AXIS));
		generateStartButton(commands, "Start New", 'N', "newModel", "Create a new PDModel");
		generateStartButton(commands, "Open Existing", 'O', "openModel", "Open an existing PDModel");
		// generateStartButton(commands, "Import Existing", 'I', "importModel", "Import an existing PDModel");

		JPanel actions = new JPanel();
		actions.setBackground(GraphWidget.lightBrown);
		ArrayList<String> list = ModelStore.retrieveModelNames(PDEdit.store);
		this.openWidget = new ListWidget(list, false);
		this.openWidget.addDialogListener(this);
		JScrollPane widgetScroll = new JScrollPane(this.openWidget);
		widgetScroll.setPreferredSize(new Dimension(200, 160));
		actions.add(widgetScroll);

		actions.add(commands);
		start.add(actions, BorderLayout.CENTER);

		JPanel exitPanel = new JPanel();
		exitPanel.setBackground(GraphWidget.lightBrown);
		exitPanel.setLayout(new BoxLayout(exitPanel, BoxLayout.LINE_AXIS));
		exitPanel.add(Box.createHorizontalGlue());
		generateStartButton(exitPanel, "Exit", 'N', "exit", "Exit PDEdit");
		start.add(exitPanel, BorderLayout.PAGE_END);

		Dimension startD = start.getPreferredSize();
		start.setSize(startD);
		Point centerOfWindow = new Point(frame.getContentPane().getSize().width/2, 
				frame.getContentPane().getSize().height/2);
		Point startPanelPos = new Point(centerOfWindow.x-startD.width/2, centerOfWindow.y-startD.height/2);
		start.setLocation(startPanelPos);

		return start;
	}

	/**
	 * Generates a button on the start panel. 
	 * @param parent
	 * @param caption
	 * @param mnemonic
	 * @param command
	 * @param toolTip
	 * @return
	 */
	private JButton generateStartButton(JPanel parent, String caption, char mnemonic, String command, String toolTip){
		ImageIcon icon = loadImage(command);
		JButton button = new JButton(caption, icon);
		button.addActionListener(this);
		button.setActionCommand(command);
		button.setToolTipText(toolTip);
		button.setMnemonic(mnemonic);
		parent.add(button);
		return button;
	}

	/**
	 * Clean up and close the application
	 */
	private void closeApp() {
		PDEditLogger.printOut();
		frame.dispose();
		System.exit(0);
	}

	/**
	 * Disables the interface and open dialog for the user to set the name for the
	 * new model
	 */
	private void createNewModel() {
		v.disableWiget(true);
		MenuDialogBox m = new MenuDialogBox(app,store, MenuDialogBox.NewModel);

		m.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e){
				if (editor.getModelName() != null){
					frame.setTitle("PDModel: "+editor.getModelName());
				}else{
					frame.setTitle("PDModel: No Model");
				}
				v.reset();
				v.invalidate();
				v.validate();
			}
		});
		m.pack();
		m.setVisible(true);
	}


	/**
	 * Clears the panel of all nodes
	 */
	public void clearPanel(){
		v.reset();
		v.invalidate();
		v.validate();
	}

	/**
	 * Open existing model
	 */
	private void openExistingModel() {
		v.disableWiget(true);
		MenuDialogBox m = new MenuDialogBox(app,store, MenuDialogBox.OpenModel);
		m.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e){
				if (editor.getModelName() != null){
					frame.setTitle("PDModel: "+editor.getModelName());
					startEditor();
				}else{
					frame.setTitle("PDModel: No Model");
				}
			}
		});
		m.setSize(350,200);
		m.setVisible(true);
	}

	/**
	 * Starts the UI around the editor.
	 */
	private void startEditor(){
		if (!started){
			started = true;
			this.commands.setCommandsState(true);
			if (!outputVisible){
				showHideOutput();
			}

			if (!propertiesVisible) {
				showHideProperties();
			}
		}
	}

	public void setNewModel(String name){
		if (name != null){
			frame.setTitle("PDModel "+name);
			modelName = name;
			Date n = new Date(System.currentTimeMillis());
			PDEditLogger.addToLog("["+n.toString()+"] PDEdit.setModel: Model Name - "+name+" Model GUID"+editor.createModel(modelName));
			PDEditLogger.newLine();
			startEditor();
			v.reset();
		}else{
			System.err.println("PDEdit> Model Name is null . . . skipping");
		}
	}

	public void setOpenedModelName(String name){
		if (name != null){
			frame.setTitle("PDModel "+name);
			modelName = name;
		}else{
			System.err.println("PDEdit> Model Name is null . . . skipping");
		}
	}

	public void loadModel(String guid){
		Collection<Object> list = editor.loadModel(guid);
		GUID t = store.begin();
		recreateElements(list, t);
	}

	private void recreateElements(Collection<Object> list, GUID t) {
		//First create the nodes
		for(Object o : list){
			if (o == null)
				continue;
			loadFromPDNode(t, o, null);
		}

		//then create the links
		for(Object o : list){
			if (o == null)
				continue;
			GUID id = new GUID(((PDNode)o).getHasInstance());
			Collection <GUID> accessibleRoles = store.getAccessibleRoles(t, id);
			for (Object a :accessibleRoles){
				GUID nodeA = store.getAccessorType(t, (GUID)a);
				GUID nodeB = store.getAccessorType(t, ((GUID)a).getPartner());

				//Check for primitives
				boolean nodeAisPrim = (Boolean)store.getInstance(t, nodeA, PDStore.ISPRIMITIVE_ROLEID);
				boolean nodeBisPrim = (Boolean)store.getInstance(t, nodeB, PDStore.ISPRIMITIVE_ROLEID);

				if (nodeAisPrim){
					Object n = editor.getDnd().getNodesByGUID((GUID)a);
					if (n == null)
						n = editor.getDnd().getNodesByGUID(((GUID)a).getPartner());
					loadFromPDNode(t, n,(GUID)a);
				}
				if (nodeBisPrim){
					Object n = editor.getDnd().getNodesByGUID(((GUID)a).getPartner());
					if (n == null)
						n = editor.getDnd().getNodesByGUID(((GUID)a));
					loadFromPDNode(t, n,(GUID)a);
				}

				String role2 = (String)store.getInstance(t, a, PDStore.NAME_ROLEID);
				String role1 = (String)store.getInstance(t,((GUID)a).getPartner(),PDStore.NAME_ROLEID);

				//Create Link
				v.createLink(nodeA,nodeAisPrim, role1, role2, nodeB,nodeBisPrim, (GUID)a);
			}
		}
	}

	public String getCurrentModelName(){
		return editor.getModelName();
	}

	/**
	 * Load information from PDNode
	 * @param t
	 * @param o
	 * @param role
	 */
	private void loadFromPDNode(GUID t, Object o,GUID role) {
		try{
			String name = (String)store.getInstance(t, new GUID(((PDNode)o).getHasInstance()), PDStore.NAME_ROLEID);
			String description = (String)store.getInstance(t, new GUID(((PDNode)o).getHasInstance()), PDStore.DESCRIPTION_ROLEID);
			String type = ((PDNode)o).getHasType();
			int x = (int)Math.round(((PDNode)o).getHasX());
			int y = (int)Math.round(((PDNode)o).getHasY());
			Point p = new Point(x,y);
			GUID id = new GUID(((PDNode)o).getHasInstance());
			if (role !=null){
				id = editor.getGUIDofType(type);
				//name = (String)store.getInstance(t, id, PDStore.NAME_ROLEID);
				name = (String) store.getInstance(t, id, PDStore.NAME_ROLEID);
				description = (String)store.getInstance(t, id, PDStore.DESCRIPTION_ROLEID);
			}
			v.createNode(name, description, type, id,role, p);
		}catch (Exception e) {
			//e.printStackTrace();
			System.err.println("[PDEdit]-loadFromPDNode: ...--...  ...--...  ...--... No PDNode to load");
		}
	}

	public PDStore getStore() {
		return store;
	}

	public void setEnablePDEdit(boolean enable){
		v.disableWiget(!enable);
		v.repaint();
	}

	/***
	 * Handles the various actions within the application.
	 */
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action == "properties") {
			showHideProperties();
		} else if (action == "connect") {
			this.startConnectNodes();
		} else if (action == "delete") {
			this.removeSelection();
		} else if (action == "undo") {
			JOptionPane.showMessageDialog(null, "TODO: Undo");
		} else if (action == "redo") {
			JOptionPane.showMessageDialog(null, "TODO: Redo");
		} else if (action == "layout") {
			layoutDiagram();
		} else if (action == "zoomOut") {
			zoomOut();			
		} else if (action == "zoomIn") {
			zoomIn();
		} else if (action == "zoomFit") {
			zoomFit();
		} else if (action == "generateDal") {
			generateDal();
		} else if (action == "newModel"){
			createNewModel();
		} else if (action == "openModel"){
			openExistingModel();
		} else if (action == "importModel"){
			importExistingModel();
		} else if (action == "exit"){
			closeApp();
		} else if (action == "showHideOutput"){
			showHideOutput();
		} else if (action == "toggleRoles") {
			toggleRoles();
		} else if (action == "toggleTypes") {
			toggleTypes();
		} else if (action.startsWith("insertCustom")) {
			this.insertCustomNode();
		} else if (action.startsWith("insert"))	{
			insertNode(action.substring(6));
		} else if (action == "takeSnapshot") {
			takeSnapshot();
		}
	}
	
	/**
	 * Displays the dialog for inserting a new custom node.
	 */
	private void insertCustomNode() {
		System.out.println("creating node");
		Point p1 = new Point(117, 99);
		DiagramNode n = new DiagramNode("<Default Node>", "<Default Description>", new Circle(), p1);
		System.out.println("crea" +
				"tion has been done");
		PropertyWindow p = new PropertyWindow(n,v);
		p.setCreation(true);
		p.pack();
		p.setVisible(true);
		v.getElements().add(n);
		v.repaint();
		v.setPopPoint(new Point());
	}

	/**
	 * Starts the UI for connecting two nodes together.
	 */
	private void startConnectNodes() {
		ArrayList<DiagramNode> selectedNodes = v.getSelectedNodes();
		if (selectedNodes.size() == 1){
			DiagramElement n = selectedNodes.get(0);
			v.setLinkAnchor((DiagramNode)n);
			n.getShape().setBorderColour(v.getGraphMagicMouseListener().getNodeSelectionColor());
			n.getShape().setColour(Color.white);
			v.repaint();
			v.getGraphMagicMouseListener().sentPopPoint(((DiagramNode)n).getLocation());
			v.getGraphMagicMouseListener().setCreatingLink(true);
		}
	}

	private Point givePoint()
	{
		Point p1 = new Point(x,y);
		x = x+50;
		y=y+50;
		return p1;

	}
	/**
	 * Inserts a new node.
	 * @param type
	 */
	private void insertNode(String type){
		Point p1 = givePoint();
		System.out.println(type);
		if(type.equals("String"))
		{
			String finalString = "pdstore."+type;
			DiagramNode n = new DiagramNode(finalString, "<String Type>", new Circle(),p1);
			v.getElements().add(n);
			v.repaint();
		}
		else if(type.equals("Integer"))
		{
			String finalString = "PDStore."+type;
			DiagramNode n = new DiagramNode(finalString, "<String Type>", new Circle(),p1);
			v.getElements().add(n);
			v.repaint();
		}
		else if(type.equals("Boolean"))
		{
			String finalString = "PDStore."+type;
			DiagramNode n = new DiagramNode(finalString, "<String Type>", new Circle(),p1);
			v.getElements().add(n);
			v.repaint();
		}
		else if(type.equals("Guid"))
		{
			String finalString = "PDStore."+type;
			DiagramNode n = new DiagramNode(finalString, "<String Type>", new Circle(),p1);
			v.getElements().add(n);
			v.repaint();
		} else {
			String finalString = "PDStore."+type;
			DiagramNode n = new DiagramNode(finalString, "<String Type>", new Circle(),p1);
			v.getElements().add(n);
			v.repaint();
		}
	}

	/**
	 * Toggles the output pane.
	 */
	private void showHideOutput(){
		if (outputVisible){
			// Store the old location so we can restore it later
			outputHeight = outputSplitPane.getSize().height - outputSplitPane.getInsets().bottom - outputSplitPane.getDividerSize() - outputSplitPane.getDividerLocation();
		}

		// Set the actual visibility of the panel
		outputVisible = !outputVisible;
		outputPanel.setVisible(outputVisible);
		this.commands.setOutputPaneState(outputVisible);

		if (outputVisible){
			// Need to set the divider location otherwise the bottom panel will not display
			int newLocation = outputSplitPane.getSize().height - outputSplitPane.getInsets().bottom - outputSplitPane.getDividerSize() - outputHeight;
			outputSplitPane.setDividerLocation(newLocation);
		}
	}

	/**
	 * Toggles the properties pane.
	 */
	private void showHideProperties(){
		if (propertiesVisible){
			// Store the old location so we can restore it later
			propertiesWidth = propertiesSplitPane.getSize().width - propertiesSplitPane.getInsets().left - propertiesSplitPane.getDividerSize() - propertiesSplitPane.getDividerLocation();
		}

		// Set the actual visibility of the panel
		propertiesVisible = !propertiesVisible;
		propertiesPanel.setVisible(propertiesVisible);
		this.commands.setPropertiesPaneState(propertiesVisible);

		if (propertiesVisible){
			// Need to set the divider location otherwise the bottom panel will not display
			int newLocation = propertiesSplitPane.getSize().width - propertiesSplitPane.getInsets().left - propertiesSplitPane.getDividerSize() - propertiesWidth;
			propertiesSplitPane.setDividerLocation(newLocation);
		}
	}

	/**
	 * Performs an automatic layout on the diagram.
	 */
	private void layoutDiagram(){
		PDEditLogger.addToLog("PDEdit.JMenuItem(Run Layout Engine): Layout Engine Not Yet Implemented |[PDEdit Warning]");
		PDEditLogger.newLine();
		System.out.println(PDEditLogger.getLog());
		JOptionPane.showMessageDialog(null,
				"PDEdit Message: \nThis version of PDEdit does not have a working layout engine!",
				"PDEdit Warning",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Generates the DAL.
	 */
	private void generateDal(){
		try {
			if (modelName == null || modelName.trim().isEmpty()){
				modelName = editor.getModelName();
			}
			Date startDate = new Date();
			addOutputMessage("Generating DAL for model to 'apps'...");
			PDGen.generateModel(modelName, "apps", new PDSimpleWorkingCopy(store), "pdedit.generated");
			Date endDate = new Date();
			long duration = endDate.getTime() - startDate.getTime();
			addOutputMessage("...DAL generated (" + duration + "ms)");
		} catch (Exception e) {
			addOutputMessage("...an error occurred!");
			addOutputMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper method for loading an icon.
	 * @param imageName The name of the icon.
	 * @return The loaded icon.
	 */
	private ImageIcon loadImage(String imageName){
		String fileName = "apps/pdedit/images/" + imageName + ".png";
		File file = new File(fileName);
		if (file.exists()){
			ImageIcon image = new ImageIcon(file.getAbsolutePath());
			return image;
		} else {
			return null;
		}
	}

	/**
	 * zoom in the image of model
	 */

	private void zoomIn(){	
		v.zoomIn();
		v.repaint();
	}

	/**
	 * zoom out the image of model
	 */
	private void zoomOut(){
		v.zoomOut();
		v.repaint();
	}

	/**
	 * zoom fit the image of model
	 */
	private void zoomFit(){
		v.originalSize();
		v.repaint();
	}

	/**
	 * Handle when the selection changes.
	 * @param event
	 */
	public void SelectionChanged(SelectionChangedEvent event) {
		ArrayList<DiagramElement> elements = event.getSelectedElements();
		int size = elements.size();
		if (size == 1) {
			DiagramElement diagramElement = elements.get(0);
			if (diagramElement instanceof DiagramNode){
				DiagramNode node = (DiagramNode)diagramElement;
				this.updateProperties(new NodeProperties(v, node));
			} else if (diagramElement instanceof DiagramLink){
				DiagramLink link = (DiagramLink)diagramElement;
				this.updateProperties(new LinkProperties(v, link));
			}
		} else if (size == 0) {
			clearProperties("Nothing selected");
		} else{
			clearProperties("Multiple nodes selected");
		}

		this.commands.changeItemSelected(size);
}

	/**
	 * Clears the properties pane.
	 * @param message The message to display in the pane.
	 */
	private void clearProperties(String message){
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		messagePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		messagePanel.add(new JLabel(message), BorderLayout.NORTH);
		updateProperties(messagePanel);
	}

	/**
	 * Updates the properties pane.
	 * @param viewer The component to display in the properties pane.
	 */
	private void updateProperties(JComponent viewer){
		propertiesViewer.removeAll();
		propertiesViewer.add(viewer);
		propertiesViewer.invalidate();
		propertiesViewer.validate();
		propertiesViewer.repaint();
	}

	/**
	 * Toggles the role name labels.
	 */
	private void toggleRoles() {
		v.options.showRoleLabels = !v.options.showRoleLabels;
		this.commands.setRolesVisibilityState(v.options.showRoleLabels);
		v.validate();
		v.repaint();
	}

	/**
	 * Toggles the type name labels.
	 */
	private void toggleTypes(){
		v.options.showTypeLabels = !v.options.showTypeLabels;
		this.commands.setTypesVisibilityState(v.options.showTypeLabels);
		v.validate();
		v.repaint();
	}

	/**
	 * Removes the currently selected items.
	 */
	public void removeSelection() {
		DiagramLink selectedLink = v.getSelectedLink();
		ArrayList<DiagramElement> elements = v.getElements();
		ArrayList<DiagramNode> removedNodes = new ArrayList<DiagramNode>();
		ArrayList<DiagramLink> removedLinks = new ArrayList<DiagramLink>();
		if (selectedLink == null){
			ArrayList<DiagramNode> selectedNodes = v.getSelectedNodes();
			ArrayList<DiagramElement> allLinks = v.getElements(ElementType.Link);
			for (DiagramNode node: selectedNodes){
				removedNodes.add(node);
				for (DiagramElement link: allLinks){
					DiagramLink actual = (DiagramLink)link;
					if (actual.hasNode(node)) {
						elements.remove(link);
						if (!removedLinks.contains(actual)){
							removedLinks.add(actual);
						}
					}
				}
			}
			elements.removeAll(selectedNodes);
		}else{
			elements.remove(selectedLink);		
			removedLinks.add(selectedLink);
		}

		for (DiagramEventListener d :v.getDiagramListeners()){
			for (DiagramNode el: removedNodes){
				d.nodeRemoved(new DiagramEvent(el));
			}

			for (DiagramLink el: removedLinks){
				d.linkRemoved(new DiagramEvent(el));
			}
		}

		// Update the editor
		v.setSelectedLink(null);
		v.getSelectedNodes().clear();
		v.fireSelectionChanged();
		v.validate();
		v.repaint();
	}

	/**
	 * Imports an existing model from another location.
	 */
	private void importExistingModel(){
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()){
					return true;
				}

				return f.getName().endsWith(".pds");
			}

			public String getDescription() {
				return "PDStore Models";
			}
		});
		int retVal = chooser.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION){
			JOptionPane.showMessageDialog(null, "TODO: Import model");
		}
	}

	/**
	 * Takes a snapshot of the current editor display.
	 */
	private void takeSnapshot(){
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new ImageFilter());
		int retVal = chooser.showSaveDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION){
			String snapshotFile = chooser.getSelectedFile().getAbsolutePath();
			if (!snapshotFile.toLowerCase().endsWith(".png")){
				snapshotFile = snapshotFile + ".png";
			}
			
			v.takeSnapshot(snapshotFile);
		}
	}

	/**
	 * Handles when an item is selected in the pick list.
	 */
	public void selected(String name) {
		String namePlusIdentifier = this.openWidget.getSelected();
		String modelName = namePlusIdentifier.substring(0, namePlusIdentifier.indexOf("|"));
		this.addOutputMessage("Loading model '" + modelName + "'...");
		Date startDate = new Date();
		this.clearPanel();
		this.setOpenedModelName(modelName);
		
		String identifier = namePlusIdentifier.substring(namePlusIdentifier.indexOf("|")+1);
		this.loadModel(identifier);
		Date endDate = new Date();
		long duration = endDate.getTime() - startDate.getTime();
		this.addOutputMessage("...model loaded (" + duration + "ms)");
		
		this.initalized();
		this.startEditor();
	}
}
