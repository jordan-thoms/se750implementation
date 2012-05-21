package pdedit;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

public class PDEditSplash extends JWindow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PDImagePanel p;

	public PDEditSplash(){
		loadImage();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-500)/2;
        int y = (screen.height-300)/2;
        setBounds(x,y,500,300);
        pack();
	}
	
	private void loadImage(){
		try {
			String filename = "apps/pdedit/images/PDedit.gif";
			BufferedImage img = ImageIO.read(new File(filename).getAbsoluteFile());
			p = new PDImagePanel(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getContentPane().add(p);
	}
	
	public void barPercent(double d){
		p.setBarPercent(d);
	}
	

}
