package pdedit.pdShapes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

public interface ShapeInterface {

	/**
	 * Draw shape
	 * @param g
	 */
	void drawShape(Graphics2D g);
	void setColour(Color c);
	void setBorderColour(Color c);
	Color getColour();
	Color getBorderColour();
	Color getPrevColour();
	
	void setPrevColour(Color prev);
	void setSelected(boolean b);
	boolean isSelected();
	boolean containsPoint(Point p);
	
	void setLocation(Point point);
	Point getLocation();
	
	ShapeType getShapeType();
	Dimension getSize();
	void setSize(Dimension s);
	
	String getLabel();
	void setLabel(String label);
}
