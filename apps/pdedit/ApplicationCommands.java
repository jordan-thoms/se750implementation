package pdedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * Encapsulates the UI for exposing various commands.
 * @author csut017
 *
 */
public class ApplicationCommands {
	// Menus
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu insertMenu;
	private JMenu viewMenu;
	private JMenuItem generateDalMenu;
	private JMenuItem takeSnapshotMenu;
	private JMenuItem connectNodesMenu;
	private JMenuItem deleteMenu;
	
	// Toggle commands
	private JCheckBoxMenuItem propertiesMenu;
	private JCheckBoxMenuItem outputMenu;
	private JCheckBoxMenuItem rolesVisibleMenu;
	private JCheckBoxMenuItem typesVisibleMenu;
	
	// Tool bar
	private JToolBar mainToolBar;
	private JButton insertButton;
	private JButton connectButton;		
	private JButton deleteButton;
//	private JButton undoButton;
//	private JButton redoButton;
	private JButton layoutButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton zoomFitButton;
	private JToggleButton typesToggleButton;
	private JToggleButton rolesToggleButton;
	private JButton generateDalButton;
	
	private int numberOfItemsSelected = 0;
	
	/**
	 * Initialises the commands
	 */
	public void initialise(ActionListener listener){
		this.generateMenuBar(listener);
		this.generateToolBar(listener);
		this.setCommandsState(false);
	}
	
	/**
	 * Retrieves the main menu bar.
	 * @return The main menu bar for the application.
	 */
	public JMenuBar getMenuBar(){
		return this.mainMenuBar;
	}
	
	/**
	 * Retrieves the main tool bar.
	 * @return The main tool bar for the application.
	 */
	public JToolBar getToolBar(){
		return this.mainToolBar;
	}
	
	/**
	 * Changes the visibility of the commands.
	 * @param fullMode True if the full command set is to be displayed.
	 */
	public void setCommandsState(boolean fullMode){
		this.editMenu.setEnabled(fullMode);
		this.insertMenu.setEnabled(fullMode);
		this.viewMenu.setEnabled(fullMode);
		this.generateDalMenu.setEnabled(fullMode);
		this.takeSnapshotMenu.setEnabled(fullMode);
		this.insertButton.setEnabled(fullMode);
		this.connectNodesMenu.setEnabled(fullMode && (this.numberOfItemsSelected == 1));
		this.deleteMenu.setEnabled(fullMode && (this.numberOfItemsSelected > 0));
		this.connectButton.setEnabled(fullMode && (this.numberOfItemsSelected == 1));
		this.deleteButton.setEnabled(fullMode && (this.numberOfItemsSelected > 0));
//		this.undoButton.setEnabled(fullMode);
//		this.redoButton.setEnabled(fullMode);
		this.layoutButton.setEnabled(fullMode);
		this.zoomInButton.setEnabled(fullMode);
		this.zoomOutButton.setEnabled(fullMode);
		this.zoomFitButton.setEnabled(fullMode);
		this.typesToggleButton.setEnabled(fullMode);
		this.rolesToggleButton.setEnabled(fullMode);
		this.generateDalButton.setEnabled(fullMode);
	}
	
	/**
	 * Sets the state of the output pane commands.
	 * @param isOn Whether the command is on or not.
	 */
	public void setOutputPaneState(boolean isOn){
		this.outputMenu.setSelected(isOn);
	}
	
	/**
	 * Sets the state of the types visibility commands.
	 * @param isOn Whether the command is on or not.
	 */
	public void setTypesVisibilityState(boolean isOn){
		this.typesToggleButton.setSelected(isOn);
		this.typesVisibleMenu.setSelected(isOn);
	}
	
	/**
	 * Sets the state of the roles visibility commands.
	 * @param isOn Whether the command is on or not.
	 */
	public void setRolesVisibilityState(boolean isOn){
		this.rolesToggleButton.setSelected(isOn);
		this.rolesVisibleMenu.setSelected(isOn);
	}
	
	/**
	 * Sets the state of the properties pane commands.
	 * @param isOn Whether the command is on or not.
	 */
	public void setPropertiesPaneState(boolean isOn){
		this.propertiesMenu.setSelected(isOn);
	}

	private void generateMenuBar(ActionListener listener) {
		this.mainMenuBar = new JMenuBar();
		
		// Generate the file menu
		this.fileMenu = generateMenu(this.mainMenuBar, "PDStore", 'P');
		generateMenuItemWithImage(listener, this.fileMenu, "Start New PDModel", 'N', "newModel");
		generateMenuItemWithImage(listener, this.fileMenu, "Open Existing PDModel", 'O', "openModel");
		// generateMenuItemWithImage(listener, this.fileMenu, "Import Existing PDModel", 'I', "importModel");
		this.fileMenu.addSeparator();
		this.takeSnapshotMenu = generateMenuItemWithImage(listener, this.fileMenu, "Take snapshot", 'T', "takeSnapshot");
		this.generateDalMenu = generateMenuItemWithImage(listener, this.fileMenu, "Generate DAL", 'G', "generateDal");
		this.fileMenu.addSeparator();
		generateMenuItemWithImage(listener, this.fileMenu, "Exit", 'x', "exit");

		// Generate the edit menu
		this.editMenu = generateMenu(this.mainMenuBar, "Edit", 'E');
		this.connectNodesMenu = generateMenuItemWithImage(listener, this.editMenu, "Connect", 'C', "connect");
		this.deleteMenu = generateMenuItemWithImage(listener, this.editMenu, "Delete", 'D', "delete", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.SHIFT_MASK));
		this.editMenu.addSeparator();
		generateMenuItemWithImage(listener, this.editMenu, "Layout", 'L', "layout");
//		this.editMenu.addSeparator();
//		generateMenuItemWithImage(listener, this.editMenu, "Undo", 'U', "undo");
//		generateMenuItemWithImage(listener, this.editMenu, "Redo", 'R', "redo");
		
		// Generate the insert menu
		this.insertMenu = generateMenu(this.mainMenuBar, "Insert", 'I');	
		generateMenuItemWithImage(listener, insertMenu, "Custom", 'C', "insertCustom");
		insertMenu.addSeparator();
		generateMenuItemWithImage(listener, insertMenu, "Blob", 'l', "insertBlob");
		generateMenuItemWithImage(listener, insertMenu, "Boolean", 'B', "insertBoolean");
		generateMenuItemWithImage(listener, insertMenu, "Char", 'h', "insertChar");
		generateMenuItemWithImage(listener, insertMenu, "Double", 'D', "insertDouble");
		generateMenuItemWithImage(listener, insertMenu, "GUID", 'G', "insertGuid");
		generateMenuItemWithImage(listener, insertMenu, "Image", 'm', "insertImage");
		generateMenuItemWithImage(listener, insertMenu, "Integer", 'I', "insertInteger");
		generateMenuItemWithImage(listener, insertMenu, "Object", 'O', "insertObject");
		generateMenuItemWithImage(listener, insertMenu, "String", 'S', "insertString");
		generateMenuItemWithImage(listener, insertMenu, "Time", 'T', "insertTime");
		
		// Generate the view menu
		this.viewMenu = generateMenu(this.mainMenuBar, "View", 'V');
		generateMenuItemWithImage(listener, this.viewMenu, "Zoom In", 'I', "zoomIn", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, ActionEvent.CTRL_MASK));
		generateMenuItemWithImage(listener, this.viewMenu, "Zoom Out", 'O', "zoomOut", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, ActionEvent.CTRL_MASK));
		generateMenuItemWithImage(listener, this.viewMenu, "Zoom to Fit", 'F', "zoomFit");
		this.viewMenu.addSeparator();
		this.propertiesMenu = generateCheckMenuItem(listener, this.viewMenu, "Properties", 'P', "properties", false);
		this.outputMenu = generateCheckMenuItem(listener, this.viewMenu, "Output", 'u', "showHideOutput", false);
		this.viewMenu.addSeparator();
		this.rolesVisibleMenu = generateCheckMenuItem(listener, this.viewMenu, "Role Names", 'R', "toggleRoles", true);
		this.typesVisibleMenu = generateCheckMenuItem(listener, this.viewMenu, "Type Names", 'U', "toggleTypes", true);
	}
	
	private JCheckBoxMenuItem generateCheckMenuItem(ActionListener listener, JMenu parent, String text, char mnemonic, String command, boolean isOn){
		JCheckBoxMenuItem newMenu = new JCheckBoxMenuItem(text);
		newMenu.addActionListener(listener);
		newMenu.setMnemonic(mnemonic);
		newMenu.setActionCommand(command);
		newMenu.setSelected(isOn);
		parent.add(newMenu);
		return newMenu;
	}
	
	private JMenu generateMenu(JMenuBar parent, String text, char mnemonic){
		JMenu newMenu = new JMenu(text);
		newMenu.setMnemonic(mnemonic);
		parent.add(newMenu);
		return newMenu;
	}
	
	private JMenuItem generateMenuItemWithImage(ActionListener listener, JMenu parent, String text, char mnemonic, String command, KeyStroke shortCut) {
		JMenuItem newMenu = this.generateMenuItemWithImage(listener, parent, text, mnemonic, command);
		newMenu.setAccelerator(shortCut);
		return newMenu;
	}

	private JMenuItem generateMenuItemWithImage(ActionListener listener, JMenu parent, String text, char mnemonic, String command) {
		ImageIcon image = loadImage(command);
		JMenuItem newMenu = new JMenuItem(text, image);
		newMenu.addActionListener(listener);
		newMenu.setMnemonic(mnemonic);
		newMenu.setActionCommand(command);
		parent.add(newMenu);
		return newMenu;
	}
	
	private void generateToolBar(ActionListener listener) {
		this.mainToolBar = new JToolBar("Editor", JToolBar.HORIZONTAL);
		
		// Editor actions
		this.insertButton = addButtonToToolBar(listener, this.mainToolBar, "insertCustom", "Add a new custom node");
		this.connectButton = addButtonToToolBar(listener, this.mainToolBar, "connect", "Connect the current node to another");		
		this.deleteButton = addButtonToToolBar(listener, this.mainToolBar, "delete", "Delete the current item");
		System.out.println("abcdefghijk");
		this.mainToolBar.addSeparator();
		
		// Undo/Redo
//		this.undoButton = addButtonToToolBar(listener, this.mainToolBar, "undo", "Undo the last action");
//		this.redoButton = addButtonToToolBar(listener, this.mainToolBar, "redo", "Redo the last undo action");
//		this.mainToolBar.addSeparator();
		
		// Layout
		this.layoutButton = addButtonToToolBar(listener, this.mainToolBar, "layout", "Automatically layout the diagram with the current layout engine");
		this.mainToolBar.addSeparator();
		
		// Zoom percent/in/out
		this.zoomInButton = addButtonToToolBar(listener, this.mainToolBar, "zoomOut", "Zoom in the display");
		this.zoomOutButton = addButtonToToolBar(listener, this.mainToolBar, "zoomIn", "Zoom out the display");
		this.zoomFitButton = addButtonToToolBar(listener, this.mainToolBar, "zoomFit", "Zoom the display to fit the whole model");
		this.mainToolBar.addSeparator();
		
		// Add the display selectors
		this.typesToggleButton = addToggleToToolBar(listener, this.mainToolBar, "toggleTypes", "Toggle the type names");
		this.rolesToggleButton = addToggleToToolBar(listener, this.mainToolBar, "toggleRoles", "Toggle the role names");
		this.mainToolBar.addSeparator();
		
		// Generate DAL
		this.generateDalButton = addButtonToToolBar(listener, this.mainToolBar, "generateDal", "Generate the DAL");
	}
	
	private JButton addButtonToToolBar(ActionListener listener,
			JToolBar toolBar, 
			String buttonName,
			String toolTip){
		ImageIcon image = loadImage(buttonName);
		JButton button = new JButton(image);
		button.setActionCommand(buttonName);
		button.setToolTipText(toolTip);
		button.addActionListener(listener);
		toolBar.add(button);
		return button;
	}
	
	private JToggleButton addToggleToToolBar(ActionListener listener,
			JToolBar toolBar, 
			String buttonName,
			String toolTip){
		ImageIcon image = loadImage(buttonName);
		JToggleButton button = new JToggleButton(image, true);
		button.setActionCommand(buttonName);
		button.setToolTipText(toolTip);
		button.addActionListener(listener);
		toolBar.add(button);
		return button;
	}
	
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
	
	public void changeItemSelected(int numberOfItems) {
		this.numberOfItemsSelected = numberOfItems;
		this.connectButton.setEnabled(this.numberOfItemsSelected == 1);
		this.deleteButton.setEnabled(this.numberOfItemsSelected >= 0);
		this.connectNodesMenu.setEnabled(this.numberOfItemsSelected == 1);
		this.deleteMenu.setEnabled(this.numberOfItemsSelected > 0);
	}
}
