package fluid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fluid.util.DataBox;
import fluid.util.ProjectModel;

import pdstore.GUID;
import pdstore.PDStore;

public class NewProjectFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JComponent> dataSeg = new ArrayList<JComponent>();
	private PDStore store;
	private BioXplore x;
	
	public NewProjectFrame(BioXplore xp,PDStore store){
		this.store = store;
		x = xp;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setTitle("New Project");
		this.getContentPane().add(container(setUp(store)));
		pack();
		Point location = new Point(
				(screen.width-this.getWidth())/2,
				(screen.height-this.getHeight())/2
				);
		this.setVisible(true);
		this.setLocation(location);
	}
	
	private ArrayList<JPanel> setUp(PDStore store){
		ArrayList <JPanel> list = new ArrayList<JPanel>();
		list.add(newName());
		JComponent ModelList = getModelList(store);
		list.add(setDefaultModel(ModelList));
		list.add(createOK());
		return list;
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
		JComboBox box = new JComboBox(data.toArray());
		box.setEditable(true);
		box.setPreferredSize(new Dimension(200,25));
		box.setName("DefaultModel");
		return box;
	}
	
	private JPanel container(ArrayList <JPanel> controls){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		for (JPanel c : controls){
			if (c != null){
				panel.add(c);
			}
		}
		return panel;
	}
	
	private JPanel newName(){
		JPanel panel = new JPanel();
		panel.add(new JLabel("Project Name: "));
		JTextField name = new JTextField();
		name.setName("ProjectName");
		name.setPreferredSize(new Dimension(200, 25));
		dataSeg.add(name);
		panel.add(name);
		return panel;
	}
	
	private JPanel setDefaultModel(JComponent Display){
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Default Model: ");
		panel.add(label);
		panel.add(Display);
		dataSeg.add(Display);
		return panel;
	}
	
	private JPanel createOK(){
		JPanel panel = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DataBox boxed = null;
				String name = null;
				for (JComponent c : dataSeg){
					if (c instanceof JTextField && c.getName().equals("ProjectName")){
						name = ((JTextField)c).getText();
					}else if (c instanceof JComboBox && c.getName().equals("DefaultModel")){
						boxed = (DataBox)((JComboBox)c).getSelectedItem();
					}
				}
				ProjectModel.addNewProject(store, name, boxed);
				x.setModel(boxed);
				close();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
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
		this.dispose();
	}
}
