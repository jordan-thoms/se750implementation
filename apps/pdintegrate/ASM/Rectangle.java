package pdintegrate.ASM;


public class Rectangle {
	
	private int x, y;
	public int z;
	public static final Integer g = 0;
	
	public Rectangle(int width, int height) {
		this.setWidth(width);
		this.setHeight(height);
	}

	public void setWidth(int width) {
		this.x = width;
	}

	public int getWidth() {
		return this.x;
	}

	public void setHeight(int height) {
		this.y = height;
	}

	public int getHeight() {
		return this.y;
	}

}
