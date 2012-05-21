package pdedit.pdShapes;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics2D;
import java.awt.Point;

public class Rectangle implements ShapeInterface {
	
	private Point location = new Point(0,0);
	private Color normal = Color.GREEN;
	private Color border = Color.WHITE;
	private Color previous = null;
	private String label = "name";
	private Dimension thisSize = new Dimension(30, 30);
	private boolean isSelected = false;

	public boolean containsPoint(Point p) {
		if (p.x > location.x && (location.x+thisSize.width) > p.x
				&& p.y > location.y && (location.y+thisSize.width) > p.y){
			return true;
		}
		return false;
	}

	public void drawShape(Graphics2D g) {
		g.setColor(normal);
		g.fillRoundRect(location.x, location.y, thisSize.width, thisSize.height, 10, 10);
		g.setColor(border);
		g.drawRoundRect(location.x, location.y, thisSize.width, thisSize.height, 10, 10);
		InformationFloater name = new InformationFloater(label);
		int x = (location.x + thisSize.width/2);
		int y = location.y - 20;
		name.setLocation(new Point(x,y));
		name.drawShape(g);	}

	public Color getBorderColour() {
		return border;
	}

	public Color getColour() {
		return normal;
	}

	public String getLabel() {
		return label;
	}

	public Point getLocation() {
		return location;
	}

	public Color getPrevColour() {
		return previous;
	}

	public ShapeType getShapeType() {
		return ShapeType.Rectangle;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setBorderColour(Color c) {
		this.border = c;
	}

	public void setColour(Color c) {
		this.normal = c;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLocation(Point point) {
		this.location = point;
	}

	public void setPrevColour(Color prev) {
		this.previous = prev;
	}

	public void setSelected(boolean b) {
		isSelected = b;
	}

	public void setSize(Dimension s) {
		thisSize = s;

	}

	public Dimension getSize() {
		return this.thisSize;
	}

}