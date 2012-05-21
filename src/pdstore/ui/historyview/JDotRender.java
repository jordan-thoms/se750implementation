package pdstore.ui.historyview;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/*
 * This class draws dots (represents a transaction) on the graph
 */
public class JDotRender extends Canvas implements TableCellRenderer {

	int branchCount;
	int activeBranch;
	int branchPositions[];
	int radius;
	int numBranches;
	public JDotRender(){
		numBranches = 1;
		branchCount = 1;
		activeBranch = 0;
		branchPositions = null;
	}
	
	/**
	 * The constructor of JDotRender
	 * 
	 * @param totalNumBranches The total number of branches
	 * @param numBranches Number of branches seen so far by this transaction
	 * @param branchPositions A list of branch positions
	 * @param activeBranch The current branch
	 */
	public JDotRender(int totalNumBranches, int numBranches, int[] branchPositions, int activeBranch) {
		branchCount = numBranches;
		
		this.numBranches = totalNumBranches;
		this.activeBranch = activeBranch;
		this.branchPositions = branchPositions;
		
	}
	
	/**
	 * Draws a dot centred at (x,y)
	 * 
	 * @param g Graphics object to draw on
	 * @param x X coordinate in Graphics object
	 * @param y Y coordinate in Graphics object
	 */
	private void drawNode(Graphics g, int x, int y ){
		Dimension d = this.getSize();
		
		radius=(int) ((d.width < d.height) ? 0.24 * d.width
				: 0.24 * d.height);
		g.setColor(Color.blue);
		g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
		g.setColor(Color.black);
		g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}
	
	/**
	 * Paints dots and lines for a cell dependent on their position in the graph
	 * 
	 * @param g Graphic object
	 */
	public void paint(Graphics g) {

		Dimension d = this.getSize();
		int x,y;
		Color c;
		int offset = d.width/10;
		x = offset;
		y = d.height/ 2;
		float realWidth = (float)d.width * 0.9f;
		
		for (int i = 0; i < branchCount; i++) {
			x = offset + i *(d.width/(numBranches+1));
			c = Color.getHSBColor(((float)x-d.width/10)/realWidth, 1, 1);
			drawThickLine(g, x, 0, x, 2*y, 2, c);
			if (i == activeBranch){
				drawNode(g, x, y);
			}				
		}
		
		if (branchPositions != null){	//Draw Branch
			int x1, x2;
			x1 = offset + branchPositions[0]*(d.width/(numBranches+1));
			x2 = x1 + (d.width/(numBranches+1));
			c = Color.getHSBColor(((float)x2-d.width/10)/realWidth, 1, 1);
			g.setColor(c);
			g.drawLine(x1+radius+1, y+radius/4, x2, 2*y);
		}

	}

	/**
	 * Draws a thick line form (x1,y1) to (x2,y2).  Must be vertically or horizontally aligned.
	 * 
	 * @param g Graphics object
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @param thickness Thickness of the line
	 * @param c Color of the line
	 */
	public void drawThickLine(Graphics g, int x1, int y1, int x2, int y2,
			int thickness, Color c) {
		// The thick line is in fact a filled polygon
		g.setColor(c);

		int dX = x2 - x1;
		int dY = y2 - y1;
		// line length
		double lineLength = Math.sqrt(dX * dX + dY * dY);

		double scale = (double) (thickness) / (2 * lineLength);

		// The x,y increments from an endpoint needed to create a rectangle...
		double ddx = -scale * (double) dY;
		double ddy = scale * (double) dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		int dx = (int) ddx;
		int dy = (int) ddy;

		// Now we can compute the corner points...
		int xPoints[] = new int[4];
		int yPoints[] = new int[4];

		xPoints[0] = x1 + dx;
		yPoints[0] = y1 + dy;
		xPoints[1] = x1 - dx;
		yPoints[1] = y1 - dy;
		xPoints[2] = x2 - dx;
		yPoints[2] = y2 - dy;
		xPoints[3] = x2 + dx;
		yPoints[3] = y2 + dy;

		g.fillPolygon(xPoints, yPoints, 4);
	}

	/**
	 * Returns this table cell renderer
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		return this;
	}

}