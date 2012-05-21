package pdedit.pdGraphWidget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class PropertyWindow extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DiagramElement element;
	private JPanel container = new JPanel();
	private JPanel controlPanel = new JPanel();
	private GraphWidget widget;
	private JButton ok = new JButton("Ok");
	private JButton cancel = new JButton("Cancel");
	private boolean creation = false;
	
	
	/**
	 * Initialize The Property window for the element
	 * @param element
	 * @param widget
	 */
	public PropertyWindow(DiagramElement element, GraphWidget widget){
		String title = "Property Window";
		this.widget = widget;
		if (element != null){
			this.setTitle(title+": "+element.getName());
			this.element = element;
			setup();
		}else{
			this.setTitle(title);
		}
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				cleanup();
			}
		});
		widget.disableWiget(true);
		this.setBackground(new Color(00,00,73));
	}


	/**
	 * It first set the window to the center of the screen then
	 * set the visibility of the window.
	 */
	public void setVisible(boolean isVisible){
		this.validate();
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		int x = s.width/2 - this.getWidth()/2;
		int y = s.height/2 - this.getHeight()/2;
		this.setLocation(x, y);
		super.setVisible(isVisible);
	}

	/**
	 * Initial setup
	 */
	private void setup(){
		if (element != null){
			container.setSize(200, 200);
			//container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
			container.setLayout(new BorderLayout());
			if (element.getElementType() != ElementType.Node){
				for (JComponent c : linkDefaultProperty()){
					container.add(c);
				}
			}

			//Set OK button behavior
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (creation){
						int count = 0;
						for(DiagramElement w : widget.getElements()){
							if (w.getName().equals(element.getName())&& w.getElementType()==ElementType.Node){
								count ++;
								System.out.println(count);
							}
						}
						String type = "";
						if (element instanceof DiagramNode){
							System.out.println(" instance of digram node hi hai");
							type = (String)element.getProperty().get(0).getInfo().get("Type");	
						}
						if (count > 1 && type.equals("PDStore.ComplexType")){
							Object[] options = {"Yes, please",
							"No way!"};
							int n = JOptionPane.showOptionDialog(null,
									"There is already a \"type\" with the name "+element.getName()+".\n" +
									"Do you wish to continue?",
									"Name Conflict",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE,
									null,     
									options,  
									options[0]);
							if (JOptionPane.YES_OPTION == n){
								finalProperties();
							}
						}else if (!element.getName().contains("<") && !element.getName().contains(">")&&
								!element.getName().isEmpty()){
							System.out.println("abc");
							finalProperties();
						}else{
							JOptionPane.showMessageDialog(null,
								    "Invalid Name For Type.",
								    "Naming Error",
								    JOptionPane.WARNING_MESSAGE);

						}
					}else{
						finalProperties();
					}
				}
			});

			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelModifcation();
				}
			});
			controlPanel.setBackground(Color.LIGHT_GRAY);
			controlPanel.add(ok);
			controlPanel.add(cancel);
			for (ElementPropertyInterface e :element.getProperty()){
				JPanel CombinePanel = new JPanel(new BorderLayout());
				JPanel property = new JPanel(new FlowLayout());
				JPanel shape_Panel = new JPanel(new FlowLayout());
				JPanel color_Panel = new JPanel(new FlowLayout());
				JPanel desc_LabelPanel = new JPanel(new FlowLayout());
				JPanel desc_fieldPanel = new JPanel();
				JPanel desc_Panel = new JPanel();
				shape_Panel.setBackground(Color.LIGHT_GRAY);
				color_Panel.setBackground(Color.LIGHT_GRAY);
				desc_LabelPanel.setBackground(Color.LIGHT_GRAY);
				property.setBackground(Color.LIGHT_GRAY);
				
				desc_fieldPanel.setBackground(Color.LIGHT_GRAY);
				desc_LabelPanel.setBackground(Color.LIGHT_GRAY);
				desc_Panel.setBackground(Color.LIGHT_GRAY);
				JLabel label = new JLabel(e.getName());
				label.setForeground(Color.BLACK);
				JLabel shape_label = new JLabel("Shape");
				shape_label.setForeground(Color.BLACK);
				JLabel Color_Label = new JLabel("Color");
				Color_Label.setForeground(Color.BLACK);
				JLabel desc_Label = new JLabel("Description");
				desc_Label.setForeground(Color.BLACK);
				property.add(label);
				property.add(e.getDisplay());
				property.add(shape_label);
				property.add(e.getShapeBox());
				property.add(Color_Label);
				property.add(e.getColorBox());
				desc_LabelPanel.add(desc_Label);
				desc_fieldPanel.add(new JScrollPane(e.getDesc()));
				desc_Panel.add(desc_Label);
				desc_Panel.add(desc_fieldPanel);
				CombinePanel.add(property,BorderLayout.NORTH);
				CombinePanel.add(desc_Panel,BorderLayout.CENTER);
				
				//controlPanel.add(ok);
				//controlPanel.add(cancel);
				//CombinePanel.add(controlPanel,BorderLayout.SOUTH);
				container.add(CombinePanel,BorderLayout.CENTER);
				e.addListener(new DiagramEventAdapter(){
					public void nodeChanged(DiagramEvent e){
						DiagramNode node = (DiagramNode)e.getSource();
						updateTitle(node.getName());
						updateGUI();
					
					}

					public void linkChanged(DiagramEvent e){
						updateGUI();
					}
				});
			}
			container.add(controlPanel,BorderLayout.SOUTH);
			this.getContentPane().add(container);
		}
	}

	private void cancelModifcation() {
		if (isCreation()){
			widget.getElements().remove(element);
			widget.repaint();
		}
		closeThis();
	}

	private ArrayList<JPanel> linkDefaultProperty(){
		
		ArrayList<JPanel> li = new ArrayList<JPanel>();
		JPanel panel = new JPanel();
		panel.setBackground(new Color(54,65,73));
		JLabel name1 = new JLabel(((DiagramLink)element).getNode1Name() +" -");
		name1.setForeground(Color.white);
		((DiagramLink)element).setRelation2(((DiagramLink)element).getNode2Name().substring(0,1).toLowerCase()+((DiagramLink)element).getNode2Name().substring(1));
		JTextField nameField1 = new JTextField(((DiagramLink)element).getRelation2().replace('.', '_'));
		
		nameField1.setPreferredSize(new Dimension(150, 30));
		nameField1.setSelectionStart(0);
		nameField1.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				JTextField f = (JTextField)e.getComponent();
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					if (f.getText().contains("<") || f.getText().contains(">"))
						return;
					finalProperties();
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT ||
						e.getKeyCode() == KeyEvent.VK_RIGHT ){
					return;
				}
				String name = null;
				if(f.getText().length()>1){
					name = f.getText().substring(0,1).toLowerCase()+ f.getText().substring(1);
				}else{
					name = f.getText().toLowerCase();
				}
				((DiagramLink)element).setRelation2(name);
				f.setText(name);
				f.select(name.length(), name.length());
			}

		});
		nameField1.addFocusListener(new FocusAdapter() {
			
			public void focusGained(FocusEvent e) {
				JTextField t = (JTextField)e.getComponent();
				t.setSelectionStart(0);
				t.setSelectionEnd(t.getText().length());
			}
		});
		
		
		JLabel name1a = new JLabel("-> "+((DiagramLink)element).getNode2Name());
		name1a.setForeground(Color.white);
		panel.add(name1);
		panel.add(nameField1);
		panel.add(name1a);
		li.add(panel);

		panel = new JPanel();
		panel.setBackground(new Color(54,65,73));
		
		
		JLabel name2 = new JLabel(((DiagramLink)element).getNode2Name()+" -");
		name2.setForeground(Color.white);
		((DiagramLink)element).setRelation1(((DiagramLink)element).getNode1Name().substring(0,1).toLowerCase()+((DiagramLink)element).getNode1Name().substring(1));
		JTextField nameField2 = new JTextField(((DiagramLink)element).getRelation1().replace('.', '_'));
		
		nameField2.setPreferredSize(new Dimension(150, 30));
		nameField2.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				JTextField f = (JTextField)e.getComponent();
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					if (f.getText().contains("<") || f.getText().contains(">"))
						return;

					finalProperties();
				}
				
				if (e.getKeyCode() == KeyEvent.VK_LEFT ||
						e.getKeyCode() == KeyEvent.VK_RIGHT ){
					return;
				}
				
				String name = null;
				if(f.getText().length()>1){
					name = f.getText().substring(0,1).toLowerCase() + f.getText().substring(1);
				}else{
					name = f.getText().toLowerCase();
				}
				((DiagramLink)element).setRelation1(name);
				f.setText(name);
				f.select(name.length(), name.length());
			}

		});
		nameField2.addFocusListener(new FocusAdapter() {
			
			public void focusGained(FocusEvent e) {
				JTextField t = (JTextField)e.getComponent();
				t.setSelectionStart(0);
				t.setSelectionEnd(t.getText().length());
			}
		});
		JLabel name2a = new JLabel("-> "+((DiagramLink)element).getNode1Name());
		name2a.setForeground(Color.white);
		panel.add(name2);
		panel.add(nameField2);
		panel.add(name2a);

		int labelLength = name1.getPreferredSize().width;
		if (labelLength < name2.getPreferredSize().width){
			labelLength = name2.getPreferredSize().width;
		}
		name1.setPreferredSize(new Dimension(labelLength, name1.getPreferredSize().height));
		name2.setPreferredSize(new Dimension(labelLength, name2.getPreferredSize().height));
		int labelLength2 = name1a.getPreferredSize().width;
		if (labelLength2 < name2a.getPreferredSize().width){
			labelLength2 = name2a.getPreferredSize().width;
		}
		name1a.setPreferredSize(new Dimension(labelLength2, name1a.getPreferredSize().height));
		name2a.setPreferredSize(new Dimension(labelLength2, name2a.getPreferredSize().height));

		li.add(panel);
		return li;
	}


	private void updateGUI(){
		
		widget.repaint();
		System.out.println("Array Panle");
		
	}

	public void finalProperties() {
		System.out.println("final property");
		// this may have to spin off new processing threads
		for(DiagramEventListener d : widget.getDiagramListeners()){
			ListenerThread t = null;
			if (creation){
				if (element.getElementType() == ElementType.Node){
					t = new ListenerThread(d, element, GraphWidget.NodeCreated);
				}else{
					t = new ListenerThread(d, element, GraphWidget.LinkCreated);
				}
			}else{
				if (element.getElementType() == ElementType.Node){
					t = new ListenerThread(d, element, GraphWidget.NodeChange);
				}else{
					t = new ListenerThread(d, element, GraphWidget.LinkChange);
				}
			}
			if (t != null)
				t.start();
		}
		widget.repaint();
		closeThis();
	}

	private void updateTitle(String name){
		this.setTitle("Properties Window: "+name);
	}

	private void closeThis(){
		cleanup();
		this.dispose();
	}

	private void cleanup(){
		widget.disableWiget(false);
		widget.repaint();
	}

	public boolean isCreation() {
		return creation;
	}


	public void setCreation(boolean creation) {
		this.creation = creation;
	}

	private class ListenerThread extends Thread {

		DiagramEventListener d;
		int event;
		DiagramElement e;

		public ListenerThread(DiagramEventListener d,DiagramElement e, int event) {
			this.d = d;
			this.e = e;
			this.event = event;
		}

		public void run() {
			switch (event){
			case GraphWidget.NodeCreated:
				d.nodeCreated(new DiagramEvent(e));
				break;
			case GraphWidget.NodeChange:
				d.nodeChanged(new DiagramEvent(e));
				break;
			case GraphWidget.LinkCreated:
				d.linkCreated(new DiagramEvent(e));
				break;
			case GraphWidget.LinkChange:
				d.linkChanged(new DiagramEvent(e));
				break;
			default:
				break;
			}
		}

	}

}
