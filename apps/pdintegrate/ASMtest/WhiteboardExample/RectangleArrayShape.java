package pdintegrate.ASMtest.WhiteboardExample;

import java.awt.Color;
import java.awt.Graphics;

public class RectangleArrayShape implements Shape {
	

	private int[] data;

	public int getX() {
		return this.retrieveHeight();
	}

	public void setX(int x) {
		this.persistX(x);
	}

	public int getY() {
		return this.retrieveY();
	}

	public void setY(int y) {
		this.persistY(y);
	}

	public int getWidth() {
		return this.retrieveWidth();
	}

	public void setWidth(int width) {
		this.persistWidth(width);
	}

	public int getHeight() {
		return this.retrieveHeight();
	}

	public void setHeight(int height) {
		this.persistHeight(height);
	}
	
	private int retrieveX() {
		return data[0];
	}

	private void persistX(int x) {
		this.data[0] = x;
	}

	private int retrieveY() {
		return data[1];
	}

	private void persistY(int y) {
		this.data[1] = y;
	}

	private int retrieveWidth() {
		return this.data[2];
	}

	private void persistWidth(int width) {
		this.data[2] = width;
	}

	private int retrieveHeight() {
		return this.data[3];
	}

	private void persistHeight(int height) {
		this.data[3] = height;
	}
	
	public RectangleArrayShape(int x, int y, int width, int height) {
		this.data = new int[4];
		
		this.persistX(x);
		this.persistY(y);
		this.persistWidth(width);
		this.persistHeight(height);
	}

	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.getHSBColor((float)Math.random(), (float)(Math.random() / 4 + 0.75), (float)(Math.random() / 4 + 0.75)));
		g.drawRect(this.retrieveX(), this.retrieveY(), this.retrieveWidth(), this.retrieveHeight());
		
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
