package pdedit.pdShapes;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

public class Triangle implements ShapeInterface {
	
	private Color normal = Color.magenta;
	private Color border = Color.white;
	private Color previous = null;
	
	private String label = "name";
	private boolean isSelected = false;
	private Dimension cSize = new Dimension(30,30);
	
	private Point refLocation = new Point();
	private Point t1 = new Point();
	private Point t2 = new Point();
	private Point t3 = new Point();
	private Polygon t;
	
	public Triangle(){
		trianglePoints();
	}
	
	private void trianglePoints(){
		int h = cSize.width;
		int a = h/2;
		int o = Math.round((float)Math.sqrt(Math.pow(h, 2)-Math.pow(a, 2)));
		t1 = new Point(refLocation.x+a,refLocation.y);
		t2 = new Point(refLocation.x+h,refLocation.y+o);
		t3 = new Point(refLocation.x,refLocation.y+o);
		t = new Polygon();
		t.addPoint(t1.x, t1.y);
		t.addPoint(t2.x, t2.y);
		t.addPoint(t3.x, t3.y);
	}

	public boolean containsPoint(Point p) {
		return t.contains(p);
	}

	public void drawShape(Graphics2D g) {
		g.setColor(normal);
		g.fillPolygon(t);
		g.setColor(border);
		g.drawPolygon(t);
		InformationFloater name = new InformationFloater(label);
		int x = (refLocation.x + cSize.width/2);
		int y = refLocation.y - 20;
		name.setLocation(new Point(x,y));
		name.drawShape(g);

	}

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
		return refLocation;
	}

	public Color getPrevColour() {
		return previous;
	}

	public ShapeType getShapeType() {
		return ShapeType.Triangle;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setBorderColour(Color c) {
		border = c;
	}

	public void setColour(Color c) {
		normal = c;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLocation(Point point) {
		refLocation = point;
		trianglePoints();
	}

	public void setPrevColour(Color prev) {
		previous = prev;
	}

	public void setSelected(boolean b) {
		isSelected = b;
	}

	public void setSize(Dimension s) {
		cSize = s;

	}

	public Dimension getSize() {
		return cSize;
	}

}
