package pdedit.pdGraphWidget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JLabel;

import pdedit.pdShapes.InformationFloater;
import pdedit.pdShapes.ShapeInterface;
import pdstore.GUID;

public class DiagramNode implements DiagramElement{

	// Name and GUID of node.
	private String name;
	private String description;
	private GUID id;
	
	//if the id equals a primitive then node will have roleID
	private GUID roleID;

	private ShapeInterface shape;
	private Point location; // centre of node
	private ArrayList<DiagramMotionListener> links;
	private boolean isSelected;
	private ArrayList<ElementPropertyInterface> property;
	private boolean isComplex = false;

	public DiagramNode(String name, String description, ShapeInterface shape, Point location) {
		super();
		this.name = name;
		this.shape = shape;
		this.description = description;
		this.location = location;
		links = new ArrayList<DiagramMotionListener>();
		property = new ArrayList<ElementPropertyInterface>();
		System.out.println("sourabh Gupta upi -sgup028");
		property.add(new NodeKind(this));
		updateLocation();
	}
	
	public void setKind(String type){
		this.isComplex = "PDStore.ComplexType".equalsIgnoreCase(type);
		for (ElementPropertyInterface l : property){
			if (l instanceof NodeKind){
				l.updateProperty(type);
			}
		}
	}

	public String getKind(){
		for (ElementPropertyInterface l : property){
			if (l instanceof NodeKind){
				return l.getProperty();
			}
		}
		
		return "";
	}

	public void setSelected(boolean b){
		isSelected = b;
		shape.setSelected(b);
	}

	public ArrayList<DiagramMotionListener> getLinks() {
		return links;
	}

	public void addLink(DiagramMotionListener l) {
		links.add(l);
	}

	public boolean removingLink(DiagramMotionListener l) {
		return links.remove(l);
	}

	private void updateLocation(){
		shape.setLocation(new Point(location.x-(shape.getSize().width/2),location.y-(shape.getSize().width/2)));
	}

	public String getName() {
		return name;
	}
	
	public String getDescription(){
		return "";
	}

	public void setName(String name) {
		this.name = name;
		this.shape.setLabel(name);
	}
	
	public void setDescription(String description){
		this.description = description;
	}

	public ShapeInterface getShape() {
		return shape;
	}

	public void setShape(ShapeInterface shape) {
		this.shape = shape;
		shape.setLocation(new Point(location.x-(shape.getSize().width/2),location.y-(shape.getSize().width/2)));
		this.shape.setLabel(name);
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
		updateLocation();
		for(DiagramMotionListener d : links){
			d.updatePosition(new DiagramEvent(this));
		}
	}

	public ElementType getElementType() {
		return ElementType.Node;
	}
	
	public void getTooltip(){
		JLabel tip = new JLabel();
		tip.setText("Type: To be implemented");
		tip.setOpaque(true);
	}

	public void drawElement(Graphics2D g, DisplayOptions options) {
		shape.drawShape(g);
	}
	
	public void drawLabel(Graphics2D g, DisplayOptions options){
		if (!options.showTypeLabels){
			return;
		}
				
		InformationFloater name = new InformationFloater(this.name, this.description, !this.isComplex);
		int x = (location.x + shape.getSize().width/2) - 15;
		int y = location.y - 35;
		name.setLocation(new Point(x,y));
		name.drawShape(g);
	}
	
	public boolean containPoint(Point p){
		return shape.containsPoint(p);
	}
	
	public ArrayList<ElementPropertyInterface> getProperty() {
		return property;
	}

	public void setProperty(ArrayList<ElementPropertyInterface> property) {
		this.property = property;
	}

	public boolean isSelected() {
		return shape.isSelected() || isSelected;
	}

	public InformationFloater getToolTip() {
		String outName = "Name: "+name+"\nDescription: "+description;
		for (ElementPropertyInterface e : property){
			outName += "\n"+e.toString();
		}
		InformationFloater t = new InformationFloater(outName);
		t.setColour(new Color(0, 0, 0, 200));
		return t;
	}
	
	
	public GUID getId() {
		for(ElementPropertyInterface e : property){
			if (e instanceof NodeKind){
				if ( !e.getInfo().get("Type").equals("PDStore.ComplexType")){
					return (GUID)e.getInfo().get("GUID");
				}
				break;
			}
		}
		return id;
	}

	public void setId(GUID id) {
		this.id = id;
	}
	
	public GUID getRoleID() {
		return roleID;
	}

	public void setRoleID(GUID roleID) {
		this.roleID = roleID;
	}

}
