package diagrameditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import pdstore.GUID;

import diagrameditor.dal.PDHistory;
import diagrameditor.dal.PDOperation;
import diagrameditor.filters.DependencyFilter;
import diagrameditor.filters.Filter;
import diagrameditor.filters.NoFilter;
import diagrameditor.filters.ShapeFilter;

/**
 * Class to set up the menu bar for the diagram editor and
 * listen to actions on it
 */
public class Menu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 3554253198683513656L;

	private DiagramEditor editor;
	private JMenu fileMenu, filtersMenu;
	private JMenuItem newItem, openItem, saveItem;
	private JRadioButtonMenuItem dependency, none, shape;
	private HistoryManager openDialog, saveDialog;
	
	protected Filter selectedFilter, noFilter, dependencyFilter, shapeFilter;

	protected String selectedShape;
	protected PDOperation selectedOp;
	
	/**
	 * Constructor
	 * @param editor, the diagram editor
	 */
	public Menu(DiagramEditor editor) {
		this.editor = editor;
		
		// Create "File" menu.
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		newItem = new JMenuItem("New", KeyEvent.VK_N);
		newItem.addActionListener(this);
		fileMenu.add(newItem);
		
		openItem = new JMenuItem("Open", KeyEvent.VK_O);
		openItem.addActionListener(this);
		fileMenu.add(openItem);
		
		saveItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);
		
		// Create "Filters" menu.
		filtersMenu = new JMenu("Filters");
		filtersMenu.setMnemonic(KeyEvent.VK_T);
		ButtonGroup filtersGroup = new ButtonGroup();
	
		dependency = new JRadioButtonMenuItem("Filter by dependency tree");
		dependency.setMnemonic(KeyEvent.VK_D);
		dependency.addActionListener(this);
		filtersGroup.add(dependency);
		filtersMenu.add(dependency);
		
		shape = new JRadioButtonMenuItem("Filter by shape");
		shape.setMnemonic(KeyEvent.VK_S);
		shape.addActionListener(this);
		filtersGroup.add(shape);
		filtersMenu.add(shape);

		filtersMenu.addSeparator();
		none = new JRadioButtonMenuItem("None");
		none.setSelected(true);
		none.setMnemonic(KeyEvent.VK_N);
		none.addActionListener(this);
		filtersGroup.add(none);
		filtersMenu.add(none);
		
		add(fileMenu);
		add(filtersMenu);

		createFilters();
	}

	/**
	 * Method to initialise the filters
	 */
	private void createFilters() {
		noFilter = new NoFilter();
		shapeFilter = new ShapeFilter();
		dependencyFilter = new DependencyFilter();
		selectedFilter = noFilter;
	}

	/**
	 * Method to listen to actions on the menu
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//create a new diagram
		if (e.getSource().equals(newItem)) {
			PDHistory history = PDHistory.load(editor.workingCopy, new GUID());
			history.setName("");
			editor.workingCopy.commit();
			selectedFilter = noFilter;
			none.setSelected(true);
			editor.loadHistory(history.getId());
		//open a existing diagram
		} else if (e.getSource().equals(openItem)) {
			if (openDialog == null)
				openDialog = new HistoryManager(editor, HistoryManager.DialogType.OPEN);
			else
				openDialog.refresh();
		//save the current diagram	
		} else if (e.getSource().equals(saveItem)) {
			if (saveDialog == null)
				saveDialog = new HistoryManager(editor, HistoryManager.DialogType.SAVE);
			else
				saveDialog.refresh();
		//selected a filter	
		} else if (e.getSource().equals(none)) {
			selectedFilter = noFilter;
		} else if (e.getSource().equals(dependency)) {
			selectedFilter = dependencyFilter;
			selectedFilter.setSelectedOp((PDOperation)editor.historyPanel.list.getSelectedValue());
		} else if(e.getSource().equals(shape)){
			selectedFilter = shapeFilter;
			selectedFilter.setSelectedOp((PDOperation)editor.historyPanel.list.getSelectedValue());
		}
	}

}
