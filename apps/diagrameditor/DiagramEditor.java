package diagrameditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.Rectangle;
import pdedit.pdShapes.ShapeInterface;
import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import diagrameditor.dal.PDHistory;
import diagrameditor.dal.PDOperation;
import diagrameditor.ops.EditorOperation;
/**
 * Class to set up and run the history-based diagram editor prototype.
 *
 */
public class DiagramEditor extends JFrame{

	private static final long serialVersionUID = -8330250391234385835L;
	
	public static final boolean NETWORK_ACCESS = false;
	
	//Variables
	private static PDStore mainStore;
	public PDWorkingCopy workingCopy;
	protected String username;
	
	private GUID historyID;
	private PDHistory history;
	private OperationList operationList;
	public Hashtable<String, ShapeInterface> diagramList = new Hashtable<String, ShapeInterface>();
	public Hashtable<String, Circle> circHashtable = new Hashtable<String, Circle>();
	public Hashtable<String, Rectangle> rectHashtable = new Hashtable<String, Rectangle>();
	
	public JComboBox selectBox;
	public StatusBar statusBar;
	protected Menu menuBar;
	protected DrawPanel drawPanel;
	protected HistoryPanel historyPanel;
	public OperationPanel operationPanel;
	
	protected GUID getHistoryID() {
		return historyID;
	}

	protected void setHistoryID(GUID historyID) {
		this.historyID = historyID;
	}

	public OperationList getOperationList() {
		return operationList;
	}

	public void setOperationList(OperationList operationList) {
		this.operationList = operationList;
	}

	/**
	 * Constructor
	 * @param username, name of user
	 * @param workingCopy, cache of PDStore
	 * @param historyID
	 * @throws HeadlessException
	 */
	public DiagramEditor(String username, PDWorkingCopy workingCopy,
			GUID historyID) throws HeadlessException {
		super("Diagram Editor _ " + username);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 700);
		this.setMinimumSize(new Dimension(100, 10));
		this.setLayout(new BorderLayout());

		selectBox = new JComboBox();

		this.username = username;
		this.workingCopy = workingCopy;
		fetchHistory(historyID);

		// Create bars.
		menuBar = new Menu(this);
		statusBar = new StatusBar();
		
		// Create the different panels.
		drawPanel = new DrawPanel(this);
		historyPanel = new HistoryPanel(this);
		operationPanel = new OperationPanel(this);

		// Create the HistoryPanel-OperationPanel splitpane.
		JSplitPane histOpeSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				historyPanel, operationPanel);
		histOpeSplitPane.setOneTouchExpandable(true);
		histOpeSplitPane.setDividerLocation(332);
		Dimension miniSize = new Dimension(175, 150);
		historyPanel.setMinimumSize(miniSize);
		operationPanel.setMinimumSize(new Dimension(175, 270));
		operationPanel.setMaximumSize(new Dimension(175, 270));
		operationPanel.setPreferredSize(new Dimension(175, 270));

		// Create a new splitpane with DrawPanel.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				histOpeSplitPane, drawPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(178);
		
		// Add them to the DiagramEditor.
		Container contentPane = this.getContentPane();
		contentPane.add(menuBar, BorderLayout.NORTH);
		contentPane.add(splitPane, BorderLayout.CENTER);
		contentPane.add(statusBar, BorderLayout.SOUTH);

		RepaintListener listener1 = new RepaintListener(this);
        PDChange<GUID, Object, GUID> changeTemplate = 
        		new PDChange<GUID, Object, GUID>
                     (null, null, null, PDHistory.roleOperationId, null);
		workingCopy.getStore().getListenerDispatcher().addListener(listener1, changeTemplate);
		
		
		this.setVisible(true);
	}
	
	/**
	 * Method to load a history from the database
	 * @param historyID, ID of the history
	 */
	public void loadHistory(GUID historyID) {
		fetchHistory(historyID);
		
//		setTitle("Diagram Editor _ " + username + " - " + history.getName());
		repaint();
	}

	private void fetchHistory(GUID historyID) {
		this.setHistoryID(historyID);
		history = PDHistory.load(workingCopy, historyID);
		setOperationList(new OperationList(PDOperation.class, history, PDHistory.roleOperationId,
				PDOperation.typeId, PDOperation.roleNextId, this));
	}
	
	/**
	 * Replaces 
	 * @param pdOperation TODO
	 * @return
	 */
	public EditorOperation instantiateCommand(PDOperation pdOperation) {
		try{
			String className = (String)workingCopy.getInstance(pdOperation, PDOperation.roleCommandId);
			Class c = Class.forName("diagrameditor.ops." + className);
			Class consParType = Class.forName("diagrameditor.DiagramEditor");
			Constructor cons = c.getConstructor(consParType);
			EditorOperation op = (EditorOperation) cons.newInstance(this);
			return op;
		} catch (Throwable e){
			System.err.println(e);
		}
		return null;
	}

	/**
	 * Main method which is executed when we run the diagram editor
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Class.forName("diagrameditor.dal.PDHistory");
			Class.forName("diagrameditor.dal.PDOperation");
			Class.forName("diagrameditor.dal.PDSimpleSpatialInfo");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		/*
		// Set the application's look and feel.
		try {
			UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		 */
		
		PDWorkingCopy workingCopy1;
		PDWorkingCopy workingCopy2;
		GUID historyID;
		
		if (NETWORK_ACCESS) {
			mainStore = PDStore.connectToServer(null);
			workingCopy1 = new PDSimpleWorkingCopy(mainStore);
			workingCopy2 = new PDSimpleWorkingCopy(mainStore);
			historyID = new GUID("dc7e77c073b711e08b9d7c6d628d2cd8");
		} else {
			mainStore = new PDStore("DiagramEditor");
			workingCopy1 = new PDSimpleWorkingCopy(mainStore);
			workingCopy2 = workingCopy1;
			historyID = new GUID();
		}

		//PDHistory history = new PDHistory(workingCopy1, historyID);
		workingCopy1.commit();
//
		DiagramEditor editor1 = new DiagramEditor("Bob", workingCopy1, historyID);
		DiagramEditor editor2 = new DiagramEditor("Ann", workingCopy2, historyID);
	
		

		
	}

}
