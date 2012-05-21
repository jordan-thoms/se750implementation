package pdedit.pdShapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import pdedit.pdGraphWidget.LineInterface;


public class SimpleLine implements LineInterface {

	private int thickness = 5;
	private int endRadius = 15;
	private Color c = Color.white;
	private Color prev = null;
	private boolean isSelected = false;
	private Point p1,p2;
	private double lineLength = 0.0;

	public SimpleLine(Point e1, Point e2){
		p1 = e1;
		p2 = e2;
		setLineLength();
	}
	
	private void setLineLength(){
		lineLength = Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2))-endRadius;
	}
	
	public SimpleLine(){
		super();
	}

	public boolean containsPoint(Point p) {
		int distanceToLine = (int)disToLine(p);
		if (distanceToLine < 3 && distanceToLine > -3){
			return true;
		}
		
		return false;
	}
	
	private double disToLine(Point p){
		double dx = this.p1.x - p2.x;
		double dy = this.p1.y - p2.y;
		double m1 = dy/dx;
		double m2 = dx/dy * -1;
		double b1 = p1.y - p1.x*m1;
		double b2 = p.y - m2*p.x;
		double xIntersect = (b2-b1)/(m1-m2);
		double yIntersect = m1*xIntersect + b1;
		double dis = Math.sqrt(Math.pow(p.x-xIntersect, 2)+Math.pow(p.y-yIntersect, 2));
		if (positionCheck(xIntersect,yIntersect)){
			return dis;
		}
		return Double.POSITIVE_INFINITY;
	}
	
	private boolean positionCheck(double x, double y){
		double length1 =  Math.sqrt(Math.pow(p1.x-x, 2)+Math.pow(p1.y-y, 2));
		double length2 =  Math.sqrt(Math.pow(x-p2.x, 2)+Math.pow(y-p2.y, 2));
		if (length1 > lineLength || length2 > lineLength){
			return false;
		}
		return true;
	}

	public void drawShape(Graphics2D g) {
		g.setColor(c);
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(thickness));
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		g.setStroke(s);
	}

	public Color getColour() {
		return c;
	}

	public Point getLocation() {
		throw new UnsupportedOperationException("get Location is not a supported operation for line");
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setColour(Color c) {
		this.c = c;
	}


	public void setLocation(Point p) {
		throw new UnsupportedOperationException("Set Location is not a supported operation for line");
	}

	public void setSelected(boolean b) {
		isSelected = b;
	}

	public ShapeType getShapeType() {
		return ShapeType.Line;
	}
	
	public void setBorderColour(Color c) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void setSize(Dimension s) {
		// TODO Auto-generated method stub
		
	}
	
	public Color getBorderColour() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Color getPrevColour() {
		return prev;
	}
	
	public void setPrevColour(Color prev) {
		this.prev = prev;
	}
	public Point getPoint1(){
		return this.p1;
	}
	
	public Point getPoint2() {
		return this.p2;
	}
	
	public void setEnds(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
		setLineLength();
	}

	
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	public Dimension getSize() {
		return new Dimension (this.thickness,0);
	}

}
