package pdedit.pdShapes;

import java.awt.Point;

public class TriangleDef {

	private Point p1;
	private Point p2;
	private Point p3;
	private int p1toP2;
	private int p2toP3;
	private int p3toP1;
	
	public TriangleDef(Point p1, Point p2, Point p3) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		caledges();
	}
	
	private void caledges(){
		p1toP2 = (int)Math.sqrt(
				Math.pow((p1.x - p2.x),2.0)
				+
				Math.pow((p1.y - p2.y),2.0)
				);
		p2toP3 = (int)Math.sqrt(
				Math.pow((p2.x - p3.x),2.0)
				+
				Math.pow((p2.y - p3.y),2.0)
				);
		p3toP1 = (int)Math.sqrt(
				Math.pow((p3.x - p1.x),2.0)
				+
				Math.pow((p3.y - p1.y),2.0)
				);
	}
	
	public boolean containPoint(Point p){
		int pToP1 = (int)Math.sqrt(
				Math.pow((p.x - p1.x),2.0)
				+
				Math.pow((p.y - p1.y),2.0)
				);
		int pToP2 = (int)Math.sqrt(
				Math.pow((p.x - p2.x),2.0)
				+
				Math.pow((p.y - p2.y),2.0)
				);
		int pToP3 = (int)Math.sqrt(
				Math.pow((p.x - p3.x),2.0)
				+
				Math.pow((p.y - p3.y),2.0)
				);
		if (pToP1 > p1toP2 || pToP1 > p3toP1){
			return false;
		}
		if (pToP2 > p1toP2 || pToP1 > p2toP3){
			return false;
		}
		if (pToP3 > p2toP3 || pToP1 > p3toP1){
			return false;
		}
		return true;
	}
	
}
