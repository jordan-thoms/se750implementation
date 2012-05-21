package pdedit.pdGraphWidget;

import java.awt.Point;

public class DiagramUtility {

	public static DiagramElement findElement(GraphWidget widget,Point p){
		for(DiagramElement e : widget.getElements()){
			if (e.containPoint(p)){
				return e;
			}
		}
		return null;
	}
}
