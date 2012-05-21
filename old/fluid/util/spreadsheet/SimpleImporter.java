package fluid.util.spreadsheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import fluid.table.PDTableModel;
import fluid.util.DataBox;

import aim.tablewidget.AIMTableEventListener;
import aim.tablewidget.DefaultTableModelHandler;
import aim.tablewidget.TableWidget;

import pdstore.GUID;
import pdstore.PDStore;

public class SimpleImporter {

	private PDStore store;
	private ArrayList<JComponent> dataSeg = new ArrayList<JComponent>();
	private JFrame frame;
	private JButton ok;
	private String fullPathFileName;
	private String fullName;
	private JTextField newRole;
	private JTextField newInst;
	private JComboBox modelBox;
	private JComboBox typeComplexBox;
	private JComboBox typebox;
	private LinkTo linkListenLinkTo;

	public SimpleImporter(PDStore store) {
		this.store = store;
		frame = new JFrame("Simple Excel Importer");
		frame.getContentPane().add(container(setUp()));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.pack();
		Point location = new Point(
				(screen.width-frame.getWidth())/2,
				(screen.height-frame.getHeight())/2
		);
		frame.setVisible(true);
		frame.setLocation(location);
	}

	private JPanel container(ArrayList <JPanel> controls){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setLayout(new GridLayout(0, 1));
		for (JPanel c : controls){
			if (c != null){
				panel.add(c);
			}
		}
		return panel;
	}

	private ArrayList<JPanel> setUp(){
		ArrayList <JPanel> list = new ArrayList<JPanel>();
		list.add(createComboboxPanel("Select the Model you wish to import data to:                   ",getModelList(store)));
		list.add(fileChoose());
		list.add(createComboboxPanel("Select a type to represent whole spreadsheet:               ",getComplexType(store)));
		list.add(createInputBox2());
		list.add(createComboboxPanel("Select a type to represent rows from the spreadsheet:  ",getTypeList(store)));

		list.add(createInputBox());
		list.add(createOK());
		return list;
	}

	private JPanel createInputBox(){
		JPanel panel = new JPanel();
		panel.add(new JLabel("Select a role to link row to spreadsheet instance:           "));
		newRole = new JTextField();
		newRole.setPreferredSize(new Dimension(210,25));
		panel.add(newRole);
		return panel;
	}

	private JPanel createInputBox2(){
		JPanel panel = new JPanel();
		panel.add(new JLabel("Instance of the type that represent the spreadsheet:   "));
		newInst = new JTextField();
		newInst.setPreferredSize(new Dimension(210,25));
		newInst.setEditable(false);
		panel.add(newInst);
		return panel;
	}

	private JComponent getModelList(PDStore store){
		GUID transaction = store.begin();
		Collection <Object> list = store.getAllInstancesOfType(transaction, PDStore.MODEL_TYPEID);
		ArrayList<DataBox> data = new ArrayList<DataBox>();
		for (Object o : list){
			String name = (String)store.getInstance(transaction, o, PDStore.NAME_ROLEID);
			if (!(name.equals("PDEditDiagram") || name.equals("PD Metamodel"))){
				data.add(new DataBox(name, (GUID)o));
			}
		}
		modelBox = new JComboBox(data.toArray());
		modelBox.setEditable(true);
		modelBox.setPreferredSize(new Dimension(210,25));
		modelBox.setName("Choose Model");
		modelBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateTypeList();
			}
		});
		return modelBox;
	}

	private void updateTypeList(){
		getComplexType(this.store);
	}

	private JComponent getComplexType(PDStore store){
		GUID transaction = store.begin();
		Collection <Object> list = store.getAllInstancesOfType(transaction, PDStore.TYPE_TYPEID);
		ArrayList<DataBox> data = new ArrayList<DataBox>();
		data.add(new DataBox("--------", null));
		for (Object o : list){
			Object isPrimitive = store.getInstance(transaction, o,PDStore.ISPRIMITIVE_ROLEID);

			if (isPrimitive != null && !(Boolean)isPrimitive){
				Object modelID = store.getInstance(transaction, o, PDStore.MODELTYPE_ROLEID.getPartner());
				if (modelID != null){
					if(((DataBox)modelBox.getSelectedItem()).getID().equals(modelID)){
						String name = (String)store.getInstance(transaction, o, PDStore.NAME_ROLEID);
						data.add(new DataBox(name, (GUID)o));
					}
				}
			}
		}

		if (typeComplexBox == null){
			typeComplexBox = new JComboBox(data.toArray());
			typeComplexBox.setEditable(true);
			typeComplexBox.setPreferredSize(new Dimension(210,25));
			typeComplexBox.setName("List Type");

		}else{
			typeComplexBox.removeActionListener(linkListenLinkTo);
			typeComplexBox.removeAllItems();
			for (DataBox d : data){
				typeComplexBox.addItem(d);
			}
		}
		linkListenLinkTo = new LinkTo();
		typeComplexBox.addActionListener(linkListenLinkTo);

		return typeComplexBox;

	}

	private JComponent getTypeList(PDStore store){
		GUID transaction = store.begin();
		Collection <Object> list = store.getAllInstancesOfType(transaction, PDStore.TYPE_TYPEID);
		ArrayList<DataBox> data = new ArrayList<DataBox>();
		data.add(new DataBox("<New Type>", null));
		data.add(new DataBox("--------", null));
		for (Object o : list){
			Object isPrimitive = store.getInstance(transaction, o,PDStore.ISPRIMITIVE_ROLEID);
			if (isPrimitive != null && !(Boolean)isPrimitive){
				String name = (String)store.getInstance(transaction, o, PDStore.NAME_ROLEID);
				data.add(new DataBox(name, (GUID)o));
			}

		}

		typebox = new JComboBox(data.toArray());
		typebox.setEditable(true);
		typebox.setPreferredSize(new Dimension(210,25));
		typebox.setName("Type");
		typebox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
				JComboBox b = (JComboBox)arg0.getSource();
				newRole.setText(((DataBox)b.getSelectedItem()).getElement().toString().toLowerCase());
				}catch(Exception e){
					
				}
			}
		});
		return typebox;
	}

	private JPanel createComboboxPanel(String labelTxt, JComponent Display){
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel(labelTxt);
		panel.add(label);
		panel.add(Display);
		dataSeg.add(Display);
		return panel;
	}


	private JPanel fileChoose(){
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Choose Excel File to import:                                              ");

		JComboBox browse = new JComboBox();
		browse.addItem("--------");
		browse.addItem("Browse");
		browse.setEditable(true);
		browse.setPreferredSize(new Dimension(210,25));
		browse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox b = (JComboBox)e.getSource();
				String s = (String)b.getSelectedItem();
				if (s.equals("Browse")){
					JFileChooser fc = new JFileChooser();
					fc.showOpenDialog(null);
					if (fc.getSelectedFile() != null){
						ArrayList<Object> obj = new ArrayList<Object>();
						for (int i = 0; i < b.getItemCount(); i ++){
							obj.add(b.getItemAt(i));
						}
						b.removeAllItems();
						b.addItem(fc.getSelectedFile().getPath());
						for (Object o : obj){
							b.addItem(o);
						}
						fullPathFileName = fc.getSelectedFile().getPath();
						fullName = fc.getSelectedFile().getName();
						if (ok != null)
							ok.setEnabled(true);

						JComboBox bx = (JComboBox)getComponent("Type");
						obj = new ArrayList<Object>();
						for (int i = 0; i < bx.getItemCount(); i ++){
							obj.add(bx.getItemAt(i));
						}
						bx.removeAllItems();
						bx.addItem(fullName.substring(0, fullName.lastIndexOf(".")));
						for (Object o : obj){
							bx.addItem(o);
						}

					}
				}
			}
		});

		panel.add(label);
		panel.add(browse);
		return panel;
	}

	private JComponent getComponent(String name ){
		for (JComponent c : dataSeg){
			if (c.getName() != null && c.getName().equalsIgnoreCase(name)){
				return c;
			}
		}
		return null;
	}
	private JPanel createOK(){
		JPanel panel = new JPanel();
		ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ext = fullPathFileName.substring(fullPathFileName.lastIndexOf("."));
				if (isExcel(ext)){
					processSpreadSheet();
				}
				close();
			}
		});
		ok.setEnabled(false);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		panel.add(ok);
		panel.add(cancel);
		panel.setBackground(Color.darkGray);
		return panel;
	}

	private void close(){
		frame.dispose();
	}

	private void processSpreadSheet(){
		try {
			GUID modelId = null;
			//String nameOfType = fullName.substring(0, fullName.lastIndexOf("."));
			String nameOfType = null;
			try{
				nameOfType = (String)((DataBox)typebox.getSelectedItem()).getElement();
			}catch(java.lang.ClassCastException c){
				nameOfType = (String)typebox.getSelectedItem();
			}
			
			for (JComponent c : dataSeg){
				if (c.getName() != null && c.getName().equalsIgnoreCase("Choose Model") && c instanceof JComboBox){
					DataBox box = (DataBox)((JComboBox)c).getSelectedItem();
					modelId = box.getID();
				}
			}
			
			// link to instance
			PDSWorkbook wb = new PDSWorkbook(fullPathFileName);
			PDSSheetPaser sheet = new PDSSheetPaser(wb.getSheet(0));
			Iterator<Cell> cells = sheet.getRows(2, 2).get(0).cellIterator();
			ArrayList<String> links = new ArrayList<String>();


			// link header
			while (cells.hasNext()){
				Cell c = cells.next();
				String value = sheet.evaluateCell(c).getStringValue();
				System.out.println(value);
				links.add(value);
			}
			ArrayList<GUID> test = new ArrayList<GUID>(links.size());
			ArrayList<Row> rows = sheet.getRows(3, sheet.getNumberOfRows());
			//Scan thru once for type
			//test = scanForTypes(test, rows);
			//Create Relations to types
			GUID transaction = store.begin();
			GUID typeId = store.getId(transaction, nameOfType);
			//If Null create the new type
			if (typeId == null){
				typeId = new GUID();
				store.createType(transaction, modelId, typeId, nameOfType);
			}
			//if Null create new Role
			GUID roledID = store.getId(transaction, newRole.getText().trim());
			System.out.println(newRole.getText());
			if (roledID == null){
				roledID = new GUID();
				GUID parentId = ((DataBox)typeComplexBox.getSelectedItem()).getID();
				String name = store.getName(transaction, parentId);
				store.createRelation(transaction, parentId, name, 
						newRole.getText().trim(), roledID, typeId);
				
			}
			HashMap<String, GUID> roles = new HashMap<String, GUID>();
			for (int i = 0; i < links.size(); i++){
				GUID r = store.getId(transaction, links.get(i));
				if (r != null){
					roles.put(links.get(i), r);
				}else{
					roles.put(links.get(i), new GUID());
					store.createRelation(transaction, typeId, "", links.get(i), roles.get(links.get(i)), PDStore.STRING_TYPEID);
				}
			}
			store.commit(transaction);
			//Add instances
			for (Row r : rows){
				GUID t = null;
				if (r == null){
					continue;
				}
				GUID newRowInst = new GUID();
				Iterator<Cell> testCells = r.cellIterator();
				t = store.begin();
				store.addLink(t, new GUID(newInst.getText()), roledID, newRowInst);
				System.out.println(newInst.getText());
				store.commit(t);
				while (testCells.hasNext()){
					t = store.begin();
					Cell c = testCells.next();
					String link = links.get(c.getColumnIndex());
					GUID role2 = roles.get(link);
					Object instance = castCellToValue(c);
					System.out.println(newRowInst);
					System.out.println(instance);
					System.out.println(role2);
					if (instance != null){
						store.addLink(t, newRowInst, role2, c.toString());
						store.commit(t);
					}
					
				}
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private ArrayList<GUID> scanForTypes(ArrayList<GUID> test, ArrayList<Row> rows) {
		for (Row r : rows){
			if (r == null){
				continue;
			}
			Iterator<Cell> testCells = r.cellIterator();
			while (testCells.hasNext()){
				Cell c = testCells.next();
				try{
					Integer.parseInt(c.toString());
					test.add(c.getColumnIndex(), PDStore.INTEGER_TYPEID);
					continue;
				}catch (Exception e) {
					//System.out.println("Not Integer");
				}
				try{
					Double.parseDouble(c.toString());
					test.add(c.getColumnIndex(), PDStore.DOUBLE_PRECISION_TYPEID);
					continue;
				}catch (Exception e) {
					//System.out.println("Not Double");
				}
				try{
					if(c.toString().contains("-")){
						//DateFormat d = DateFormat.getInstance();
						test.add(c.getColumnIndex(), PDStore.TIMESTAMP_TYPEID);
					}
					//System.out.println(c.toString());
					continue;
				}catch (Exception e) {
					//System.out.println("Not Date");
				}

			}
		}
		return test;
	}
	
	private Object castCellToValue(Cell c){
		Object ret = null;
		//System.out.println(c.toString());
		try{
			ret = Integer.parseInt(c.toString());
		}catch (Exception e) {
			//System.out.println("Not Integer");
		}
		try{
			ret = Double.parseDouble(c.toString());
		}catch (Exception e) {
			//System.out.println("Not Double");
		}
		try{
			if(c.toString().contains("-")){
				DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
				Date d = df.parse(c.toString());   
				ret = d;
	            //System.out.println("Today = " + df.format(d));
			}
			//System.out.println(c.toString());
		}catch (Exception e) {
			//System.out.println("Not Date");
		}
		if (ret == null){
			ret = c.toString();
		}
		//System.out.println();
		return ret;
	}

	private void disable(){
		frame.setEnabled(false);
		frame.setVisible(false);
	}
	private void enable(){
		frame.setEnabled(true);
		frame.setVisible(true);
		frame.toFront();
	}

	//------------------------------------------------------------------

	private boolean isExcel(String ext){
		return (ext.equalsIgnoreCase(".xls") || ext.equalsIgnoreCase(".xlsx"));
	}

	/*private boolean isImage(String ext){
		return (ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".png"));
	}*/

	private class LinkTo implements ActionListener{
		JFrame nw = new JFrame();
		private JButton ok2;
		private JComboBox box;
		private JComboBox parent;
		PDTableModel model;
		private String name = "";
		public LinkTo(){
			nw.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent arg0) {
					enable();

				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					enable();
				}

			});

		}

		private void openWindow(){
			nw.pack();
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			Point location = new Point(
					(screen.width-frame.getWidth())/2,
					(screen.height-frame.getHeight())/2
			);
			nw.setVisible(true);
			nw.setLocation(location);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			parent = (JComboBox)e.getSource();
			name = (String)((DataBox)parent.getSelectedItem()).getElement();
			nw.setTitle("Link to: "+((DataBox)parent.getSelectedItem()).getElement());
			DataBox box = (DataBox)parent.getSelectedItem();
			if (!((String)box.getElement()).equals("--------") || !((String)box.getElement()).equals("<New Type>")){
				System.out.println(box.getID());
				if(box.getID() != null){
					model = new PDTableModel(box.getID(),null, store);

					JPanel container = new JPanel();
					container.setLayout(new GridLayout(0, 1));
					container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
					container.add(instanceListPane(box.getID()));
					container.add(createTable());
					container.add(createOK());
					nw.getContentPane().add(container);

					openWindow();
					disable();
				}
			}
		}

		private JPanel instanceListPane(GUID id){
			GUID transaction = store.begin();
			JPanel panel = new JPanel();
			panel.setBackground(Color.darkGray);
			Collection <Object> list = store.getAllInstancesOfType(transaction, id);
			ArrayList<DataBox> data = new ArrayList<DataBox>();
			data.add(new DataBox("--------", null));
			for (Object o : list){
				data.add(new DataBox(o.toString(), (GUID)o));
			}
			JLabel label = new JLabel("Type Instance:");
			label.setForeground(Color.white);
			box = new JComboBox(data.toArray());
			box.setEditable(true);
			box.setPreferredSize(new Dimension(210,25));
			box.setName("Instance");
			box.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					JComboBox b = (JComboBox)arg0.getSource();
					if (!((String)((DataBox)b.getSelectedItem()).getElement()).equals("--------")){
						ok2.setEnabled(true);
					}

				}
			});
			panel.add(label);
			panel.add(box);
			return panel;
		}

		private TableWidget createTable(){
			//JPanel panel = new JPanel();
			TableWidget widget = new TableWidget(new DefaultTableModelHandler(model));
			widget.addAIMTableEventListener(new AIMTableEventListener() {

				@Override
				public void rowSelected(Object source, int index) {
					TableWidget w = (TableWidget)source;
					if (w.getSelectedRow() instanceof JComponent){
						JComponent b = (JComponent)w.getSelectedRow();
						GUID instance = new GUID(b.getToolTipText());
						GUID role = new GUID(b.getName());
						GUID transaction = store.begin();
						GUID accessor = (GUID)store.getInstance(transaction, instance, role.getPartner());
						box.setSelectedItem(new DataBox(accessor.toString(), (GUID)accessor));

					}else{
						String name = model.getColumnName(1);
						GUID transaction = store.begin();
						GUID role = store.getId(transaction, name);
						System.out.println(w.getSelectedRow());
						GUID instance = (GUID)store.getInstance(transaction, w.getSelectedRow(), role.getPartner());
						box.setSelectedItem(new DataBox(instance.toString(), (GUID)instance));
					}
					ok2.setEnabled(true);

				}

				@Override
				public void deselectRows(Object source) {
					// TODO Auto-generated method stub

				}

				@Override
				public void colSelected(int index) {
					// TODO Auto-generated method stub

				}
			});
			widget.setTitleLabel(name);
			//panel.add(widget);
			return widget;
		}

		private JPanel createOK(){
			JPanel panel = new JPanel();
			ok2 = new JButton("Ok");
			ok2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					//DataBox sel = (DataBox)parent.getSelectedItem();
					DataBox bo = (DataBox)box.getSelectedItem();
					newInst.setText((String)bo.getElement());
					/*parent.removeItem(sel);
					String newStr = (String)sel.getElement() +":"+(String)bo.getElement();
					DataBox newBox = new DataBox(newStr, sel.getID());
					parent.addItem(newBox);
					parent.setSelectedItem(newBox);*/
					closeWindow();
				}
			});
			ok2.setEnabled(false);
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					closeWindow();
				}
			});
			panel.add(ok2);
			panel.add(cancel);
			panel.setBackground(Color.darkGray);
			return panel;
		}

		private void closeWindow(){
			nw.dispose();
		}

	}
}
