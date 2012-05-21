package pdedit.pdShapes;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;


public class Circle implements ShapeInterface {

	private Color normal = Color.orange;
	private Color prev = null;
	private String label = "name";
	private Color border = Color.WHITE;
	private boolean isSelect = false;
	private Point location = new Point(100,100);
	private Dimension cSize = new Dimension(30,30);
	private Point center = new Point();

	public Circle(){
		updateCenter();
	}

	private void updateCenter(){
		center.x = location.x + cSize.width/2;
		center.y = location.y + cSize.height/2;
	}

	public void drawShape(Graphics2D g) {
		g.setColor(normal);
		g.fillOval(location.x, location.y, cSize.width, cSize.height);
		g.setColor(border);
		g.drawOval(location.x, location.y, cSize.width, cSize.height);
		
		InformationFloater name = new InformationFloater(label);
		int x = (location.x + cSize.width/2);
		int y = location.y - 20;
		name.setLocation(new Point(x,y));
		name.drawShape(g);
	}
	
	public Color getColour() {
		return normal;
	}
	
	public boolean isSelected() {
		return isSelect;
	}

	public void setColour(Color c) {
		this.normal = c;
	}

	public void setSelected(boolean b) {
		isSelect = b;
	}

	public boolean containsPoint(Point p) {
		double b = Math.abs(p.x - center.x);
		double c = Math.abs(p.y - center.y);
		double a = Math.sqrt(Math.pow(b,2)+Math.pow(c,2));
		if (a <= cSize.width/2){
			return true;
		}
		return false;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point p) {
		if (p instanceof Point){
			location = (Point)p;
			updateCenter();
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.Circle;
	}

	
	public void setBorderColour(Color c) {
		this.border = c;
	}

	
	public void setSize(Dimension s) {
		cSize = s;
		updateCenter();
	}
	
	public Color getPrevColour() {
		return prev;
	}

	public void setPrevColour(Color prev) {
		this.prev = prev;
	}

	
	public Color getBorderColour() {
		return border;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Dimension getSize(){
		return this.cSize;
	}

}
