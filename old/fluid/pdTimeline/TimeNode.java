package fluid.pdTimeline;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

import pdedit.pdShapes.ShapeInterface;

public class TimeNode extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color normal;
	private Color hover;
	public Color getHover() {
		return hover;
	}

	public void setHover(Color hover) {
		this.hover = hover;
	}

	private Color selected;
	
	private boolean isHover;
	public boolean isHover() {
		return isHover;
	}

	public void setHover(boolean isHover) {
		this.isHover = isHover;
	}

	private ShapeInterface shape;
	private boolean isSelected;
	
	public TimeNode(ShapeInterface s){
		shape = s;
		normal = s.getColour();
	}
	
	public void draw(Graphics2D g, Point p){
		if (isSelected){
			shape.setColour(selected);
		}else if (isHover){
			shape.setColour(hover);
		}else{
			shape.setColour(normal);
		}
		this.setLocation(p);
		shape.drawShape(g);
	}
	
	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
	}
	
	public boolean getSelected(){
		return isSelected;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	}
	
	public boolean contains(Point p){
		return shape.containsPoint(p);
	}
	
	public void setSelected(Color selected) {
		this.selected = selected;
	}

}
