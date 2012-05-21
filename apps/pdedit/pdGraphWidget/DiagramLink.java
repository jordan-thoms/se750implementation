package pdedit.pdGraphWidget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import pdedit.pdShapes.InformationFloater;
import pdedit.pdShapes.ShapeInterface;
import pdstore.GUID;

public class DiagramLink implements DiagramElement,DiagramMotionListener{
	private String name = "Accessible Role";
	private String description = "Default Description";
	private LineInterface line;
	private DiagramNode node1;
	private DiagramNode node2;
	private DiagramLink role;
	private String relation1 = "<can access>";
	private String relation2 = "<can access>";
	private GUID id;

	public DiagramLink(String name,DiagramNode node1, DiagramNode node2  ,LineInterface s){
		this.node1 = node1;
		this.node2 = node2;
		this.name = name;
//		this.description = description;
		this.line = s;
		line.setEnds(node1.getLocation(), node2.getLocation());
		
	}

	public ElementType getElementType() {
		return ElementType.Link;
	}

	public String getName() {
		return name;
	}
	
	public String getDescription(){
		return description;
	}

	public ShapeInterface getShape() {
		return line;
	}

	public void setShape(ShapeInterface s) {
		line = (LineInterface)s;
	}

	public void updateLine(){
		line.setEnds(node1.getLocation(), node2.getLocation());
	}
	
	public String getRelation1(){
		return relation1;
	}
	
	public String getRelation2(){
		return relation2;
	}
	
	public String getNode1Name(){
		return node1.getName();
	}
	
	public String getNode2Name(){
		return node2.getName();
	}
	
	public String getNode1Description(){
		return node1.getDescription();
	}
	
	public String getNode2Description(){
		return node2.getDescription();
	}
	
	public DiagramNode getNode1(){
		return node1;
	}
	
	public DiagramNode getNode2(){
		return node2;
	}

	public void drawElement(Graphics2D g, DisplayOptions options) {
		line.drawShape(g);
	}

	public void drawLabel(Graphics2D g, DisplayOptions options) {
		if (!options.showRoleLabels){
			return;
		}
		String label1 = this.relation2;
		String label2 = this.relation1;
		String label3 = this.description;
		Point p1 = this.line.getPoint1();
		
		Point p2 = this.line.getPoint2();
	
		System.out.println("2nd point");
		System.out.println(p2.x);
		System.out.println(p2.y);
		if (p1.x > p2.x) {
			Point temp = p1;
			p1 = p2;
			p2 = temp;
			
			label1 = this.relation1;
			label2 = this.relation2;
			label3 = this.description;
		}
		
		FontMetrics font = g.getFontMetrics();
		int width = font.stringWidth(relation1);
		
		double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
		long xOffset = Math.round(Math.cos(angle) * 25);
		long yOffset = Math.round(Math.sin(angle) * 25);
		long x1 = p1.x + xOffset;
		long y1 = p1.y + yOffset;
		long x2 = p2.x - xOffset - width;
		long y2 = p2.y - yOffset;
		
		g.setColor(Color.BLACK);
		g.drawString(label1, x1, y1);		
		g.setColor(Color.BLACK);
		g.drawString(label2, x2, y2);
	}


	public void updatePosition(DiagramEvent e) {
		updateLine();
	}
	

	public boolean hasNodes(DiagramNode n1, DiagramNode n2){
		if (node1.equals(n1) && node2.equals(n2)||
				node1.equals(n2) && node2.equals(n1)){
			return true;
		}
		return false;
	}

	public boolean hasNode(DiagramNode n){
		if (node1.equals(n) || node2.equals(n)){
			return true;
		}
		return false;
	}

	public boolean isSelected() {
		return line.isSelected();
	}

	public void setSelected(boolean b) {
		line.setSelected(b);
	}

	public void disconnectLink(){
		node1.removingLink(this);
		node2.removingLink(this);
	}

	public void selectNodes(Color c, Color border){
		if (node1.isSelected()){
			node1.getShape().setColour(c);
			node1.getShape().setBorderColour(border);
		}else{
			node1.getShape().setPrevColour(node1.getShape().getColour());
			node1.getShape().setColour(c);
			node1.getShape().setBorderColour(border);
		}
		if (node2.isSelected()){
			node2.getShape().setColour(c);
			node2.getShape().setBorderColour(border);
		}else{
			node2.getShape().setPrevColour(node2.getShape().getColour());
			node2.getShape().setColour(c);
			node2.getShape().setBorderColour(border);
		}


	}
	public void unselectNodes(Color c, Color border){
		if (node1.isSelected()){
			node1.getShape().setColour(c);
			node1.getShape().setBorderColour(border);
		}else{
			node1.getShape().setColour(node1.getShape().getPrevColour());
			node1.getShape().setBorderColour(border);
			node1.getShape().setPrevColour(null);
		}
		if (node2.isSelected()){
			node2.getShape().setColour(c);
			node2.getShape().setBorderColour(border);
		}else{
			node2.getShape().setColour(node2.getShape().getPrevColour());
			node2.getShape().setBorderColour(border);
			node2.getShape().setPrevColour(null);
		}
	}


	public void setName(String name) {
	}

	public boolean containPoint(Point p) {
		return line.containsPoint(p);
	}
	
	public void setRelation1(String r){
		relation1 = r;
	}
	
	public void setRelation2(String r){
		relation2 = r;
	}

	public void setDescription(String r){
		description = r;
	}
	
	public ArrayList<ElementPropertyInterface> getProperty() {
		return new ArrayList<ElementPropertyInterface>();
	}

	public InformationFloater getToolTip() {
		InformationFloater t = new InformationFloater(
				"Roles:\n"+
				"1) "+node1.getName()+" "+relation2+" "+node2.getName()
				+"\n2) "+node2.getName()+" "+relation1+" "+node1.getName());
		t.setColour(new Color(0, 0, 0, 200));
		return t;
	}
	
	public GUID getId() {
		return id;
	}

	public void setId(GUID id) {
		this.id = id;
	}


//	public String getDescription() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	public void setDescription(String description) {
//		// TODO Auto-generated method stub
//		
//	}
}
