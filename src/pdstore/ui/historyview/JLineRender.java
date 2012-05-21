package pdstore.ui.historyview;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * This class is used to draw lines in the railway diagram
 */
public class JLineRender extends Canvas implements TableCellRenderer {
	// The Offset parameter is used for drawing lines for more than one branch
	int branchCount;
	int activeBranch;
	int numBranches;
	
	/**
	 * Default constructor
	 */
	public JLineRender() {
		numBranches = 1;
		branchCount = 1;
		activeBranch = 0;
	}

	/**
	 * JLineRender Constructor
	 * 
	 * @param totalNumBranches The total number of branches in the PDStore
	 * @param bc The number of branches in a transaction
	 * @param activeBranch Indicates whether current branch is active or not. 0 is normal line, 1 is dotted line.
	 */
	public JLineRender(int totalNumBranches, int bc, int activeBranch) { // 0 normal, 1 dotted
		branchCount = bc;
		this.numBranches = totalNumBranches;
		this.activeBranch = activeBranch;
	}
	
	/**
	 * Draw the dots and lines on given graphics
	 * @param g The graphics to draw
	 */
	public void paint(Graphics g) {
		Dimension d = this.getSize();

		int x = d.width / 10;
		int y = d.height / 2;
		float color[] = new float[3];
		
		for (int i = 0; i < branchCount; i++) {
			color[0] = x;
			color[1] = 1;
			color[2] = 0;
			float realWidth = (float)d.width * 0.9f;
			Color c = Color.getHSBColor(((float)x-d.width/10)/realWidth, 1, 1);
			if (i == activeBranch){
				drawThickLine(g, x, 0, x, 2 * y, 1, c);
			}
			else{
				drawThickLine(g, x, d.height/3, x, (d.height - d.height/3), 2, c);
			}

			x += d.width / (numBranches + 1);
		}
	}

	/**
	 * Draw line function
	 * 
	 * @param g The graphics object
	 * @param x1 Start position of x
	 * @param y1 Start position of y
	 * @param x2 End position of x
	 * @param y2 End position of y
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

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		return this;
	}

}