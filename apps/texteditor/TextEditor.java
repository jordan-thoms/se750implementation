package texteditor;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.Rectangle;
import pdedit.pdShapes.ShapeInterface;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import texteditor.dal.*;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

//import diagrameditor.dal.PDHistory;
//import diagrameditor.dal.PDOperation;
//import diagrameditor.dal.PDSimpleSpatialInfo;

public class TextEditor extends JFrame implements MouseListener {

	private static final long serialVersionUID = -8330250391234385835L;

	public static final String COPY = "COPY";
	public static final String DELETE = "DELETE";
	public static final String INSERT = "INSERT";
	public static final String CUT = "CUT";
	
	public static PDHistory history;
	public TextPanel textPanel;
	public HistoryPanel historyPanel;
	public OperationPanel operationPanel;
	public static String userName = null;

	//public int circle_count;
	//public int rectangle_count;

	public static PDStore mainStore = new PDStore("TextEditor");
	public static PDWorkingCopy store;
	public static GUID guid1 = null;
	
	public static PDWord originator;
	public ArrayList<PDWord> clipboard = new ArrayList<PDWord>();
	public static String actionStatus = "NONE"; //sets to COPY or CUT when the action is performed.

	public JLabel status = new JLabel("Status");
	public JLabel x_coordinate;
	public JLabel y_coordinate;
	public JMenuBar menuBar;
	public JMenu menu, newshape_menu, select_menu;
	public JMenuItem menuItem;
	public JCheckBox ResizeButton;

	//public JComboBox SelectBox = new JComboBox();

	//public Hashtable<String, ShapeInterface> DiagramList = new Hashtable<String, ShapeInterface>();
	//public Hashtable<String, Circle> circHashtable = new Hashtable<String, Circle>();
	//public Hashtable<String, Rectangle> rectHashtable = new Hashtable<String, Rectangle>();
	public ArrayList<WordAndPos> wordList = new ArrayList<WordAndPos>(); //Stores the current state of the text field

	public static DefaultListModel listModel = new DefaultListModel();

	public TextEditor(String arg0, PDWorkingCopy workingCopy, GUID id)
			throws HeadlessException {
		super("Text Editor _ " + arg0);
		userName = arg0;
		store = workingCopy;
		PDHistory.load(workingCopy, id);
		final JFrame frame = this;
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 500);

		frame.setLayout(new BorderLayout());
		/*
		JMenuItem menu_circle = new JMenuItem("Circle");
		menu_circle.addActionListener(new newShape("New Shape"));
		JMenuItem menu_rectangle = new JMenuItem("Rectangle");
		menu_rectangle.addActionListener(new newShape("New Shape"));
		newshape_menu = new JMenu("New Shape");
		newshape_menu.add(menu_circle);
		newshape_menu.add(menu_rectangle);
		
		menuBar = new JMenuBar();
		menuBar.add(newshape_menu);

		ResizeButton = new JCheckBox("Resize");
		ResizeButton.setSelected(false);
		menuBar.add(ResizeButton);
		
		x_coordinate = new JLabel("X");
		y_coordinate = new JLabel("Y");
		x_coordinate.setSize(30, 200);
		y_coordinate.setSize(30, 200);
		JSplitPane XYSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				x_coordinate, y_coordinate);
		XYSplitPane.setDividerLocation(200);
		menuBar.add(XYSplitPane);
		*/
		textPanel = new TextPanel(this, workingCopy, id);		
		historyPanel = new HistoryPanel(this, workingCopy, id);
		operationPanel = new OperationPanel(this, workingCopy, id);
		
		JSplitPane histOpeSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				historyPanel, operationPanel);
		histOpeSplitPane.setOneTouchExpandable(true);
		histOpeSplitPane.setDividerLocation(200);
		Dimension miniSize = new Dimension(150, 150);
		historyPanel.setMinimumSize(miniSize);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				histOpeSplitPane, textPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);
		
		Container contentPane = frame.getContentPane();
//		contentPane.add(menuBar, BorderLayout.NORTH);
		contentPane.add(splitPane, BorderLayout.CENTER);
		contentPane.add(status, BorderLayout.SOUTH);

		frame.setVisible(true);

	}
/*  
 *  Previously used newshape class by diagram editor.
 * 
	class newShape implements ActionListener {

		private String status_msg;

		public newShape(String msg) {
			status.setText("Draw New Shape");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			status.setText(status_msg);
			DrawNewShape(e.getActionCommand().toString());
			validate();
		}

		public void DrawNewShape(String shape_type) {
			ShapeInterface shape = null;
			String shape_name = "shape";

			if (shape_type.equalsIgnoreCase("Circle")) {
				shape = new Circle();
				circle_count++;
				System.out.println(circle_count);
				int shapeID = circHashtable.size() + 1;
				shape_name = "Cir_" + shapeID;
				circHashtable.put(shape_name, (Circle) shape);

			}
			if (shape_type.equalsIgnoreCase("Rectangle")) {
				shape = new Rectangle();
				rectangle_count++;
				System.out.println("rec" + rectangle_count);
				int shapeID = rectHashtable.size() + 1;
				shape_name = "Rec_" + shapeID;
				rectHashtable.put(shape_name, (Rectangle) shape);

			}

			int total_count = rectHashtable.size() + circHashtable.size();
			Point center = new Point(total_count * 50, total_count * 50);
			shape.setSize(new Dimension(100, 100));
			shape.setLocation(center);
			shape.setLabel(shape_name);
			DiagramList.put(shape_name, shape);

			PDOperation p = new PDOperation(store);
			PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(store);
			p.setCommand("New");
			Date d = new Date();
			p.setTimeStamp(d.getTime());
			pds.setShapeID(shape_name);
			pds.setHeight((long) 100);
			pds.setWidth((long) 100);
			pds.setX((long) shape.getLocation().x);
			pds.setY((long) shape.getLocation().y);
			p.setSuperParameter(pds);
			history.addOperation(p);
			store.commit();
		}

	}
*/

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

	public static void main(String[] args) {
		PDWorkingCopy workingCopy1 = new PDSimpleWorkingCopy(mainStore);
		PDWorkingCopy workingCopy2 = new PDSimpleWorkingCopy(mainStore);

		guid1 = new GUID();
		history = new PDHistory(workingCopy1, guid1);	
		history.removeOperation();
		
		originator = new PDWord(workingCopy1);
		originator.setText("");
		workingCopy1.commit();

		Frame frame1 = new TextEditor("Bob", workingCopy1, guid1);
		Frame frame2 = new TextEditor("Alice", workingCopy1, guid1);

		mainStore.getDetachedListenerList().add(
				new pdstore.applications.ComponentRoleListener(
						PDHistory.roleOperationId, frame1));
		mainStore.getDetachedListenerList().add(
				new pdstore.applications.ComponentRoleListener(
						PDHistory.roleOperationId, frame2));
	}

}
