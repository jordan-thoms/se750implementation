package pdedit.pdShapes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import pdedit.pdGraphWidget.GraphWidget;

public class InformationFloater implements ShapeInterface {

	private String tooltipText="Default Floater ";
	private String tooltipText1 = "Default Floater ";
	private Point pointCenter = new Point();
	private Point topLeftCorner = new Point();
	private Point textLocation = new Point();
	private int boxWidth = 100;
	private int boxHeight = 50;
	private int margin = 5;
	private Color top = new Color(255, 255, 255, 50);
	private Color bottom = new Color(0, 0, 0, 160);
	private boolean updateCorner = false;
	private boolean useItalicText;

	public InformationFloater(String tooltipText) {
		super();
		this.tooltipText = tooltipText;
	}

	public InformationFloater(String tooltipText, String tooltipText1, boolean useItalic) {
		super();
		this.tooltipText = tooltipText;
		this.tooltipText1 = tooltipText1;
		this.useItalicText = useItalic;
	}

	public void useItalic(){
		this.useItalicText = true;
	}

	public boolean containsPoint(Point p) {
		return false;
	}


	public void drawShape(Graphics2D g) {
		String[] lines = findSize(g);
		g.setColor(bottom);
		g.fillRoundRect(topLeftCorner.x, topLeftCorner.y, boxWidth, boxHeight, 12, 12);
		g.setColor(top);
		g.fillRoundRect(topLeftCorner.x, topLeftCorner.y, boxWidth, boxHeight, 12, 12);
		g.setColor(new Color(255, 255, 255));
		int startingHeight = topLeftCorner.y+g.getFontMetrics().getHeight()+2;
		Font currentFont = g.getFont();
		if (this.useItalicText) {
			Font newFont = currentFont.deriveFont(Font.ITALIC);
			g.setFont(newFont);
		}
		
		if (lines != null){
			for (int i = 0; i < lines.length; i++){
				g.drawString(lines[i], textLocation.x-1, startingHeight+(i*g.getFontMetrics().getHeight()));
			}
		}
		
		g.setFont(currentFont);
	}

	private String[] findSize(Graphics2D g){
		if (tooltipText != null){
			String[] lines = tooltipText.split("\n");
			FontMetrics f = g.getFontMetrics();
			boxWidth = findLongestLineLenght(lines,f)+(margin*2);
			boxHeight = (f.getHeight()*lines.length)+(margin*2);
			if (updateCorner)
				updateCorner();
			return lines;
		}
		return null;
	}

	private int findLongestLineLenght(String[] lines, FontMetrics f ){
		int lenght = 0;
		for (String s : lines){
			if (f.stringWidth(s)> lenght){
				lenght = f.stringWidth(s);
			}
		}
		return lenght;
	}

	private void updateCorner(){
		double topX = pointCenter.x - boxWidth/2;
		double topY = pointCenter.y - boxHeight/2;
		topLeftCorner.setLocation(topX, topY);
		textLocation.setLocation(topX+6, pointCenter.y+5);
	}


	public Color getBorderColour() {
		return null;
	}


	public Color getColour() {
		return bottom;
	}


	public String getLabel() {
		System.out.println(tooltipText);
		return tooltipText;
	}


	public Point getLocation() {
		return pointCenter;
	}


	public Color getPrevColour() {
		return null;
	}


	public ShapeType getShapeType() {
		return null;
	}


	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean isSelected() {
		return false;
	}


	public void setBorderColour(Color c) {
	}


	public void setColour(Color c) {
		bottom = c;
	}


	public void setLabel(String label) {
		tooltipText = label;
	}


	public void setLocation(Point point) {
		pointCenter = point;
		updateCorner = true;
		updateCorner();
	}

	private void updateCenter(){
		double topX = topLeftCorner.x + boxWidth/2 + 5;
		double topY = topLeftCorner.y + boxHeight/2;
		pointCenter.setLocation(topX, topY);
		textLocation.setLocation(topLeftCorner.x+6, pointCenter.y+5);
	}

	public void setTopLeftCorner(Point point) {
		topLeftCorner = point;
		updateCorner = false;
		updateCenter();
	}


	public void setPrevColour(Color prev) {}


	public void setSelected(boolean b) {}


	public void setSize(Dimension s) {
		// TODO Auto-generated method stub

	}

}
