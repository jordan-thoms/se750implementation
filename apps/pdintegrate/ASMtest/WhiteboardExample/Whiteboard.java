package pdintegrate.ASMtest.WhiteboardExample;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

public class Whiteboard extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Shape> shapes;
	
	public Whiteboard() {
		this.shapes = new ArrayList<Shape>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final Whiteboard jf = new Whiteboard();		
		jf.setBounds(40, 40, 500, 500);
		jf.setBackground(Color.BLACK);

		Random r = new Random(System.currentTimeMillis());

		for (int i = 0; i < 10; i++) {
			jf.shapes.add(new RectangleArrayShape(r.nextInt(jf.getWidth() / 2), r.nextInt(jf.getHeight() / 2), jf.getWidth() - r.nextInt(jf.getWidth() / 2)
					, jf.getHeight() - r.nextInt(jf.getHeight() / 2)));
		}
		
		jf.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				for (Shape s: jf.shapes) {
					s.randomize();
				}
				jf.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// Nothing
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// Nothing
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// Nothing
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Nothing
			}
			
		});
		


		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setVisible(true);
		jf.repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		System.out.println("paint called");
		for (Shape s: this.shapes) {
			s.paint(g);
		}

	}

}
