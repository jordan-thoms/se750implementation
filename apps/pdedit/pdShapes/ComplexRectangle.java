package pdedit.pdShapes;

import java.awt.Point;

public class ComplexRectangle {
	
	TriangleDef t1;
	TriangleDef t2;
	
	
	public ComplexRectangle(Point p1, Point p2, Point p3, Point p4){
		t1 = new TriangleDef(p1, p2, p3);
		t2 = new TriangleDef(p1, p3, p4);
	}
	
	
	/**
	 * @param p1 Points of the vertices of the rectangle
	 * @param p2
	 * @param p3
	 * @param p4
	 * @param pointOfInterest
	 */
	public boolean contains(Point pointOfInterest){
		if (t1.containPoint(pointOfInterest)||t2.containPoint(pointOfInterest)){
			return true;
		}
		return false;
	}

}
