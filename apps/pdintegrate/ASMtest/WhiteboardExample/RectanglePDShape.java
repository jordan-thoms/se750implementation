package pdintegrate.ASMtest.WhiteboardExample;

import java.awt.Color;
import java.awt.Graphics;

import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

public class RectanglePDShape implements Shape, PDInstance {
	
	private static final GUID X_ROLE_GUID = new GUID();
	private static final GUID Y_ROLE_GUID = new GUID();
	private static final GUID WIDTH_ROLE_GUID = new GUID();
	private static final GUID HEIGHT_ROLE_GUID = new GUID();

	private GUID GUID;
	
	private PDWorkingCopy pw;

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
		return (Integer)pw.getInstance(this, RectanglePDShape.X_ROLE_GUID);
	}

	private void persistX(int x) {
		pw.setLink(this.GUID, X_ROLE_GUID, x);
		pw.commit();
	}

	private int retrieveY() {
		return (Integer)pw.getInstance(this, RectanglePDShape.Y_ROLE_GUID);
	}

	private void persistY(int y) {
		pw.setLink(this.GUID, Y_ROLE_GUID, y);
		pw.commit();
	}

	private int retrieveWidth() {
		return (Integer)pw.getInstance(this, RectanglePDShape.WIDTH_ROLE_GUID);
	}

	private void persistWidth(int width) {
		pw.setLink(this.GUID, WIDTH_ROLE_GUID, width);
		pw.commit();
	}

	private int retrieveHeight() {
		 return (Integer)pw.getInstance(this, RectanglePDShape.HEIGHT_ROLE_GUID);
	}

	private void persistHeight(int height) {
		pw.setLink(this.GUID, HEIGHT_ROLE_GUID, height);
		pw.commit();
	}
	
	public RectanglePDShape(GUID g, int x, int y, int width, int height) {
		this.GUID = g;
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

	@Override
	public GUID getId() {
		return this.GUID;
	}

	@Override
	public String getName() throws PDStoreException {
		return null;
	}

	@Override
	public PDWorkingCopy getPDWorkingCopy() {
		return this.pw;
	}

	@Override
	public GUID getTypeId() {
		return null;
	}

	@Override
	public void removeName() throws PDStoreException {
	}

	@Override
	public void setName(String name) throws PDStoreException {
	} 

}
