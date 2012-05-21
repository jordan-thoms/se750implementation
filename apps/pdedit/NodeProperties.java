package pdedit;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pdedit.pdGraphWidget.DiagramEvent;
import pdedit.pdGraphWidget.DiagramEventListener;
import pdedit.pdGraphWidget.DiagramNode;
import pdedit.pdGraphWidget.GraphWidget;

public class NodeProperties extends PropertiesPane {
	private static final long serialVersionUID = 8463199858505398898L;
	private DiagramNode node;
	private JTextField nameField;
	private JTextField typeField;
	private JTextField typefield_1;
	private JTextField shapeField;
	private JTextField ColorField;
	
	public NodeProperties(final GraphWidget graphEditor, final DiagramNode nodeToShow){
		super(graphEditor);
		this.node = nodeToShow;
		
		this.nameField = this.generateField(this.node.getName(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event) {
				node.setName(event.getValue());
			}
		
			public void valueCommitted(ValueChangedEvent event) {
				for(DiagramEventListener d : graphEditor.getDiagramListeners()){
					d.nodeChanged(new DiagramEvent(nodeToShow));
				}
			}			
		});
		
		this.typeField = this.generateField(this.node.getKind(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event) {
			}
		
			public void valueCommitted(ValueChangedEvent event) {
			}			
		});
		this.typeField.setEditable(false);
		
		this.typefield_1 = this.generateField(this.node.getDescription(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event){
				node.setDescription(event.getValue());
			}
			public void valueCommitted(ValueChangedEvent event){
				for(DiagramEventListener d : graphEditor.getDiagramListeners()){
					d.nodeChanged(new DiagramEvent(nodeToShow));
				}
			}
		});
		
		this.ColorField = this.generateField(this.node.getKind(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event) {
			}
		
			public void valueCommitted(ValueChangedEvent event) {
			}			
		});
		this.ColorField.setEditable(false);
		
		this.shapeField = this.generateField(this.node.getKind(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event) {
			}
		
			public void valueCommitted(ValueChangedEvent event) {
			}			
		});
		this.shapeField.setEditable(false);
		this.setLayout(new BorderLayout());
		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new GridLayout(0, 2));
		rootPanel.add(new JLabel("Name:"));
		rootPanel.add(this.nameField);
		rootPanel.add(new JLabel("Type:"));
		rootPanel.add(this.typeField);
		rootPanel.add(new JLabel("Description:"));
		rootPanel.add(this.typefield_1);
		rootPanel.add(new JLabel("Shape"));
		rootPanel.add(this.shapeField);
		rootPanel.add(new JLabel("Color"));
		rootPanel.add(this.ColorField);
		rootPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.add(rootPanel, BorderLayout.NORTH);
	}
}
