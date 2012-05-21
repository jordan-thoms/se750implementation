package pdedit.pdGraphWidget;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;


import pdedit.pdShapes.InformationFloater;
import pdedit.pdShapes.ShapeInterface;

public interface DiagramElement {

	String getName();
	void setName(String name);
	
	String getDescription();
	void setDescription(String description);
	
	ShapeInterface getShape();
	void setShape(ShapeInterface s);
	ElementType getElementType();
	void drawElement(Graphics2D g, DisplayOptions options);
	void drawLabel(Graphics2D g, DisplayOptions options);
	boolean isSelected();
	void setSelected(boolean b);
	boolean containPoint(Point p);
	ArrayList<ElementPropertyInterface> getProperty();
	InformationFloater getToolTip();
}
