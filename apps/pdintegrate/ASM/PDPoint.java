package pdintegrate.ASM;

import pdintegrate.annotations.PDAttribute;
import pdintegrate.annotations.PDType;

@PDType(Entity_GUID = "126e2e93bed011dfaaa4005056c00001")
public class PDPoint {

	@PDAttribute(Role_GUID = "126e55a5bed011dfaaa4005056c00001")
	private int[] x_location = new int[2];
	
	@PDAttribute(Role_GUID = "12707880bed011dfaaa4005056c00001")
	private int y_location;
	
	public PDPoint(int x, int y) {
		this.x_location[0] = x;
		this.y_location = y;
	}
	
	public void setX(int x) {
		this.x_location[0] = x;
	}

	public int getX() {
		return this.x_location[0];
	}

	public void setY(int y) {
		this.y_location = y;
	}

	public int getY() {
		return this.y_location;
	}
	
}
