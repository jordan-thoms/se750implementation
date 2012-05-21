package pdedit.pdGraphWidget;

import java.util.HashMap;

import javax.swing.JComponent;

public interface ElementPropertyInterface {

	String getName();
	String getDescription();
	JComponent getDisplay();
	JComponent getShapeBox();
	JComponent getColorBox();
	JComponent getDesc();
	void addListener(DiagramEventListener l);
	String toString();
	HashMap<Object,Object> getInfo();
	
	void updateProperty(String s);
	String getProperty();
}
