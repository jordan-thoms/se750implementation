package pdedit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PDImagePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private BufferedImage bufferImg;
	private int fill = 452;
	private int current = 0;

	public PDImagePanel(BufferedImage b){
		img = b;
		this.setPreferredSize(new Dimension(500, 300));
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = createGraphics2D();
		g2d.setStroke(new BasicStroke(5));
		g2d.drawImage(img, 0, 0, null);
		g2d.setColor(new Color(255, 255, 255, 200));
		g2d.drawRoundRect(20, 220, 460, 21, 5, 5);
		g2d.setColor(new Color(255, 255, 255, 50));
		g2d.fillRoundRect(24, 224, current, 14, 0, 0);
		g2d.dispose();
		g.drawImage(bufferImg, 0, 0, this);
		System.out.println("Paint Method");
	}
	
	public void setBarPercent(double d){
		current = (int)(fill*d);
		repaint();
	}
	public Graphics2D createGraphics2D() {
		Graphics2D g2 = null;
		if (bufferImg == null || bufferImg.getWidth() != this.getWidth() || bufferImg.getHeight() != this.getHeight()) {
			bufferImg = (BufferedImage) createImage(this.getWidth(), this.getHeight());
		} 
		g2 = bufferImg.createGraphics();
		g2.setBackground(getBackground());
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		return g2;
	}

	
}
