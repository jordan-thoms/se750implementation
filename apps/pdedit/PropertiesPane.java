package pdedit;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pdedit.pdGraphWidget.GraphWidget;

public abstract class PropertiesPane extends JPanel {
	private static final long serialVersionUID = -1810687579247013985L;
	private GraphWidget graph;
	
	public PropertiesPane(GraphWidget graphEditor){
		this.graph = graphEditor;
	}
	
	protected JTextField generateField(String text, final ValueChangedEventListener listener){
		final JTextField field = new JTextField();
		field.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					changeValue(field.getText(), listener);
				}
				public void removeUpdate(DocumentEvent e) {
					changeValue(field.getText(), listener);
				}
				public void insertUpdate(DocumentEvent e) {
					changeValue(field.getText(), listener);
				}
			});
		field.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent arg0) {
			}
			public void focusLost(FocusEvent arg0) {
				ValueChangedEvent event = new ValueChangedEvent(this, field.getText());
				listener.valueCommitted(event);
			}
		});
		field.setText(text);
		return field;
	}
	
	private void changeValue(String newValue, ValueChangedEventListener listener){
		ValueChangedEvent event = new ValueChangedEvent(this, newValue);
		listener.valueChanged(event);
		this.graph.validate();
		this.graph.repaint();
	}
}
