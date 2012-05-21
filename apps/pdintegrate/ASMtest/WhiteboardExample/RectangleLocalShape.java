package pdintegrate.ASMtest.WhiteboardExample;

import java.awt.Color;
import java.awt.Graphics;

public class RectangleLocalShape implements Shape {
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	private int x, y, width, height;
	
	public RectangleLocalShape(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.getHSBColor((float)Math.random(), (float)(Math.random() / 4 + 0.75), (float)(Math.random() / 4 + 0.75)));
		g.drawRect(this.x, this.y, this.width, this.height);
		
		g.setColor(c);
		
	}

	@Override
	public void randomize() {
		this.setX((int)(Math.random() * 400));
		this.setY((int)(Math.random() * 400));
		this.setWidth(400 - (int)(Math.random() * 200));
		this.setHeight(400 - (int)(Math.random() * 200));
		
	} 

}
