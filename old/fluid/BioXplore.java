package fluid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fluid.ExperimentialpdTable.PDTable2;
import fluid.table.PDTableModel;
import fluid.util.DataBox;
import fluid.util.ProjectModel;
import fluid.util.spreadsheet.SimpleImporter;

import aim.tablewidget.DataHandler;
import aim.tablewidget.DefaultTableModelHandler;
import aim.tablewidget.TableWidget;

import pdedit.PDEdit;
import pdstore.GUID;

public class BioXplore {

	private static JFrame frame;
	private static BioPDPortal portal;
	private static PDEdit editor;
	private static boolean showEditor = true;
	private static BioXplore self;
	private static TableWidget tablewidget;
	private static SplashThread s;
	private static String store;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		self = new BioXplore();
		self.start();
	}


	public BioXplore() {
		osCheck();
	}

	private void start(){
		store = evaluationSetup();
		s = new SplashThread();
		s.start();
		setupMainWindow();
		setupPDEdit();
		showXplorer();
		s.close();
	}


	private static void osCheck(){
		if (System.getProperty("os.name").contains("Mac")){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PDXplorer");
		}
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
	}

	private static void setupMainWindow() {
		frame = new JFrame("BDXplorer for Scienific Data");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 500);
		frame.setJMenuBar(addMenuBar());
	}

	private static void showXplorer(){
		frame.setVisible(true);
		frame.toFront();
		showPDEdit();
	}

	/**
	 * Setup PDEdit
	 */
	private static void setupPDEdit(){
		portal = new BioPDPortal(store);
		//portal = new BioPDPortal();
		editor = new PDEdit(portal);
		// load model to save sessions TODO
		ProjectModel.Load(portal.getPDStore());
		GUID t = portal.getPDStore().begin();
		PDTableModel tableModel = new PDTableModel(portal.getPDStore().getId(t, "River"), null, portal.getPDStore());
		DataHandler handler = new DefaultTableModelHandler(tableModel);
		tablewidget = new TableWidget(handler);
		portal.getPDStore().commit(t);
		portal.setTable(tablewidget);
		frame.getContentPane().add(tablewidget);
		frame.pack();
	}
	
	private static String evaluationSetup(){
		Object[] evaluation = {"PDXplorer(1)","PDXplorer(2)", "PDXplorer(3)","PDXplorer(4)"};
		String st = (String) JOptionPane.showInputDialog(null,
				"Evalaution Setting:",
				"Evaluation Customisation",
				JOptionPane.QUESTION_MESSAGE,
				null,
				evaluation,
				"PDXplorer");
		return st;
	}

	/**
	 * Adds the menu bar, menus and all the menu items for those respective
	 * menus in PDEdit
	 * 
	 * @return
	 */
	private static JMenuBar addMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("File");
		filemenu.setMnemonic('F');

		/*JMenuItem newProj = new JMenuItem("New Project");
		newProj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("new project");
				NewProjectFrame np = new NewProjectFrame(self,portal.getPDStore());
			}
		});
		newProj.setMnemonic('N');
		newProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK, true));


		JMenuItem open = new JMenuItem("Open Project");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("open project");
			}
		});
		open.setMnemonic('O');
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK, true));
		 */
		JMenuItem importMenu = new JMenuItem("Import");
		importMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//SimpleImporter sm = 
				new SimpleImporter(portal.getPDStore());
			}
		});
		importMenu.setMnemonic('I');
		importMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK, true));

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		exit.setMnemonic('X');

		//filemenu.add(newProj);
		//filemenu.add(open);
		filemenu.add(importMenu);
		filemenu.add(exit);
		menubar.add(filemenu);

		JMenu editmenu = new JMenu("View");
		editmenu.setMnemonic('V');

		JMenuItem view = new JMenuItem("Show Data Model");
		view.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (showEditor){
					editor.closePDModelWindow();
					showEditor = false;
				}else{
					showPDEdit();
				}
			}
		});
		view.setMnemonic('S');
		view.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK, true));
		editmenu.add(view);

		menubar.add(editmenu);
		return menubar;
	}

	public void setModel(DataBox box){
		//PDTableBuffer b = new PDTableBuffer(portal.getPDStore(), box.getID());
		//PDTable2 b = 
		new PDTable2(portal.getPDStore(), box.getID());
	}


	private static void showPDEdit() {
		editor.showPDModelWindow();
		//hard coding model name need to be removed TODO
		GUID t = portal.getPDStore().begin();
		editor.clearPanel();
		editor.setOpenedModelName("RiverModel");
		editor.loadModel(portal.getPDStore().getId(t, "RiverModel").toString());
		editor.initalized();
		showEditor = true;
		portal.getPDStore().commit(t);
	}


}
