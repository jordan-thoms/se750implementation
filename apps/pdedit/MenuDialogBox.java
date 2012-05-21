package pdedit;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pdstore.GUID;
import pdstore.PDStore;

public class MenuDialogBox extends JFrame implements DialogListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int NewModel = 0;
	public static final int OpenModel = 1;
	private JPanel container = new JPanel();
	private JPanel inputPanel = new JPanel();
	private JPanel inputPanel_1 = new JPanel();
	private JPanel create_Cancel = new JPanel();
	private PDEdit parentWidget;
	private ListWidget widget; 
	private JScrollPane widgetScrollPane;
	private JTextField nameInput;
	private JTextField desInput;
	private int type;
	private PDStore store;
	private Collection<Object> modelInstances;

	public MenuDialogBox(PDEdit parentWidget,PDStore store, int type) {
		this.parentWidget = parentWidget;
		this.store = store;
		this.modelInstances = ModelStore.retrieveCurrentModels(store);
		this.type = type;
		String okText = "Ok";
		switch (type){
		case NewModel:
			okText = "Create";
			newModelDialog();
			break;
		case OpenModel:
			okText = "Open";
			openModelDialog();
			break;
		default:
			throw new UnsupportedOperationException("Dialog code ("+type+") not currently supported ");
		}
		
		okCancel(okText);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.getContentPane().add(container);
	}

	private void okCancel(String okText){
		
		JButton create = new JButton(okText);
		create.setBounds(0, 120, 20, 20);
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finalProperties();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.setBounds(120, 120, 20, 20);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		create_Cancel.add(create);
		create_Cancel.add(cancel);
		container.add(create_Cancel);
	}

	private void newModelDialog(){

		this.setTitle("New Model");
		JLabel name = new JLabel("Name:");
		JLabel description = new JLabel("Description:");
		nameInput = new JTextField("<Name of Model>");
		desInput = new JTextField("<Description of Model>");
		nameInput.setPreferredSize(new Dimension(200, 30));
		desInput.setPreferredSize(new Dimension(200,100));
		
		nameInput.setSelectionStart(0);
		desInput.setSelectionStart(0);
		
		nameInput.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					finalProperties();
				}
				JTextField f_name = (JTextField)e.getComponent();
				JTextField f_des = (JTextField)e.getComponent();
				if (f_name.getText() != null){
					updateTitle(f_name.getText());
				}
			}
		});
		
		nameInput.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
			JTextField t_name = (JTextField)e.getComponent();
			t_name.setSelectionStart(0);
			t_name.setSelectionEnd(t_name.getText().length());
			}
		});
		desInput.addFocusListener(new FocusAdapter(){
			
			public void focusGained(FocusEvent e) {
			JTextField t_des = (JTextField)e.getComponent();
			t_des.setSelectionStart(0);
			t_des.setSelectionEnd(t_des.getText().length());			
		    }
		});
		
		inputPanel.add(name);
		inputPanel_1.add(description);
		inputPanel.add(nameInput);
		inputPanel_1.add(desInput);
		container.add(inputPanel);
		container.add(inputPanel_1);
	}

/*	private void writeDatatoDB(String str_name, String str_des)throws Exception {

		String strurl="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=apps\\pdedit\\db.mdb";
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        Connection conn=DriverManager.getConnection(strurl);
		
	    Statement stmt = conn.createStatement();
	    stmt.executeUpdate("insert into details values (str_name, str_des)");
		
	    stmt.close();
	    conn.close();
        }
*/
	private void openModelDialog(){
		this.setTitle("Open existing model");
		JLabel name = new JLabel("Search");
		nameInput = new JTextField("<Name>");
		nameInput.setPreferredSize(new Dimension(250, 30));
		nameInput.setSelectionStart(0);
		nameInput.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				JTextField f = (JTextField)e.getComponent();
				//Check for caps at beginning
				// this makes sure that the model begins with a Capital
				if (f.getText().length() > 0){
					int st = f.getSelectionStart();
					int end = f.getSelectionEnd();
					String temp = ""+f.getText().charAt(0);
					temp = temp.toUpperCase();
					if (f.getText().length() > 2){
						String second = ""+f.getText().charAt(1);
						temp += second.toLowerCase();
						temp += f.getText().substring(2);
					}else if (f.getText().length() > 1){
						temp += f.getText().substring(1);
					}
					f.setText(temp);
					f.setSelectionStart(st);
					f.setSelectionEnd(end);
				}
				updateListWidget(f.getText());
			}
		});
		nameInput.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				JTextField t = (JTextField)e.getComponent();
				t.setSelectionStart(0);
				t.setSelectionEnd(t.getText().length());
			}
		});
		inputPanel.add(name);
		inputPanel.add(nameInput);

		// Widget's autoscroll and select as you type are not implemented
		ArrayList<String> list = ModelStore.retrieveModelNames(this.store);
		widget = new ListWidget(list, true);
		widget.addDialogListener(this);
		widgetScrollPane = new JScrollPane(widget);
		container.add(inputPanel);
		container.add(widgetScrollPane);
	}

	private void updateListWidget(String s){
		JComponent c = widget.findClosestLabel(s);
		if (c != null)
			widgetScrollPane.getViewport().setViewPosition(c.getLocation());

	}

	private void updateTitle(String text) {
		this.setTitle("New Model: "+text);
	}

	private void finalProperties() {
		String name = "";
		if (type == NewModel){
			name = nameInput.getText();
			if (name == null || name.isEmpty()){
				JOptionPane.showMessageDialog(null,
					    "The new model has no name!\nPLEASE, enter a name for the model. ",
					    "No Name Warning",
					    JOptionPane.WARNING_MESSAGE);

				return;
			} else {
				String modelName = "";
				GUID t = store.begin();
				for (Object model : modelInstances){
					modelName = (String)store.getInstance(t, model, PDStore.NAME_ROLEID);
					if (modelName == null){
						continue;
					}
					if (name.equalsIgnoreCase(modelName)){
						JOptionPane.showMessageDialog(null,
							    "Duplicate model name - '" + name + "'",
							    "Duplicate Name Warning",
							    JOptionPane.WARNING_MESSAGE);

						return;
					}
				}
			}
			parentWidget.setNewModel(name);
		}else if (type == OpenModel){
			if (widget.getSelected() != null){
				String modelName = widget.getSelected().substring(0,widget.getSelected().indexOf("|"));
				parentWidget.addOutputMessage("Loading model '" + modelName + "'...");
				Date startDate = new Date();
				parentWidget.clearPanel();
				parentWidget.setOpenedModelName(modelName);
				name = widget.getSelected().substring(widget.getSelected().indexOf("|")+1);
				parentWidget.loadModel(name);
				Date endDate = new Date();
				long duration = endDate.getTime() - startDate.getTime();
				parentWidget.addOutputMessage("...model loaded (" + duration + "ms)");
			}
		}

		parentWidget.initalized();
		close();
	}

	private void close(){
		setVisible(false);
		dispose();
	}

	public void setVisible(boolean isVisible){
		this.validate();
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		int x = s.width/2 - this.getWidth()/2;
		int y = s.height/2 - this.getHeight()/2;
		this.setLocation(x, y);
		super.setVisible(isVisible);
	}

	public void selected(String name) {
		nameInput.setText(name);

	}


}
