package pdedit;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pdedit.pdGraphWidget.DiagramLink;
import pdedit.pdGraphWidget.GraphWidget;

public class LinkProperties extends PropertiesPane {
	private static final long serialVersionUID = 8463199858505398898L;
	private DiagramLink link;
	private JTextField link1Field;
	private JTextField link2Field;
	
	public LinkProperties(GraphWidget graphEditor, DiagramLink linkToShow){
		super(graphEditor);
		this.link = linkToShow;
		
		this.link1Field = this.generateField(this.link.getRelation2(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event) {
				link.setRelation2(event.getValue());
			}
			public void valueCommitted(ValueChangedEvent event) {
			}			
		});
		this.link2Field = this.generateField(this.link.getRelation1(), new ValueChangedEventListener(){
			public void valueChanged(ValueChangedEvent event) {
				link.setRelation1(event.getValue());
			}			
			public void valueCommitted(ValueChangedEvent event) {
			}			
		});

		this.setLayout(new BorderLayout());
		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new GridLayout(0, 3));
		rootPanel.add(new JLabel(this.link.getNode1Name() + "->"));
		rootPanel.add(link1Field);
		rootPanel.add(new JLabel("->" + this.link.getNode2Name()));
		rootPanel.add(new JLabel(this.link.getNode2Name() + "->"));
		rootPanel.add(link2Field);
		rootPanel.add(new JLabel("->" + this.link.getNode1Name()));
		rootPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.add(rootPanel, BorderLayout.NORTH);
	}
}
