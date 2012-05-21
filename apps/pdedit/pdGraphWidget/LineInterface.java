package pdedit.pdGraphWidget;

import java.awt.Point;

import pdedit.pdShapes.ShapeInterface;

public interface LineInterface extends ShapeInterface{
	Point getPoint1();
	Point getPoint2();
	void setEnds(Point p1, Point p2);
}
