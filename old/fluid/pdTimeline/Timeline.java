package fluid.pdTimeline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.ShapeInterface;

public class Timeline extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<GregorianCalendar> dates = new ArrayList<GregorianCalendar>();
	private int sizeOfNode = 40;
	private int spacing = 20;
	private ArrayList<TimeNode> nodes = new ArrayList<TimeNode>();
	private BufferedImage bufferImg;
	private TimelineOrientation direction = TimelineOrientation.Horizontial;
	private boolean initComplete = false;
	private ArrayList<TimelineListener> listeners;
	

	public Timeline(){
		this.setPreferredSize(new Dimension(1000, 300));
		addListeners();
	}

	public Timeline(TimelineOrientation d){
		direction = d;
		this.setPreferredSize(new Dimension(1000, 300));
		addListeners();
	}

	private void addListeners(){
		listeners = new ArrayList<TimelineListener>();
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e){
				for(TimeNode t : nodes){
					if(t.contains(e.getPoint())){
						t.setHover(true);
					}else{
						t.setHover(false);
					}
				}
				repaint();
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				TimeNode n = null;
				boolean found = false;
				for(TimeNode t : nodes){
					if(t.contains(e.getPoint())){
						n = t;
						t.setSelected(true);
						found = true;
						break;
					}
				}
				if (found){
					for(TimeNode t : nodes){
						if(!t.equals(n)){
							t.setHover(false);
							t.setSelected(false);
						}
					}
				}
				repaint();
				for(TimelineListener l : listeners){
					l.nodeSelected(new TimelineEvent(n));
				}
			}
		});

		this.addComponentListener(new ComponentListener() {

			
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			
			public void componentResized(ComponentEvent e) {
				createNodes();
				initComplete = true;
				repaint();
			}

			
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
	public void setDates(ArrayList<GregorianCalendar> e){
		dates = e;
		if (direction == TimelineOrientation.Horizontial){
			this.setPreferredSize(new Dimension(dates.size()*(sizeOfNode+spacing), this.getHeight()));
		}else{
			this.setPreferredSize( new Dimension(300,dates.size()*(sizeOfNode+spacing)));
		}
		createNodes();
	}

	private void createNodes(){
		nodes.clear();
		if (direction == TimelineOrientation.Horizontial){
			for (int i = 0; i < dates.size();i++){
				setupNode(i);
			}
		}else{
			for (int i = dates.size()-1; i >= 0;i--){
				setupNode(i);
			}
		}

	}
	
	public void addTimelineListener(TimelineListener t){
		listeners.add(t);
	}

	private void setupNode(int i) {
		ShapeInterface s = new Circle();
		s.setSize(new Dimension(sizeOfNode, sizeOfNode));
		if (direction == TimelineOrientation.Horizontial){
			s.setLocation(new Point(i*(sizeOfNode+spacing)+10, 300/2-20));
		}else{
			int j = (dates.size()-1)-i;
			s.setLocation(new Point(this.getWidth()/2-sizeOfNode/2 , j*(sizeOfNode+spacing)+10));
		}
		if (i == 0){
			s.setColour(Color.orange);
		}else if (i == (dates.size()-1)){
			s.setColour(Color.GREEN);
		}else{
			s.setColour(Color.YELLOW);
		}
		TimeNode n = new TimeNode(s);
		n.setSelected(Color.blue);
		n.setHover(Color.cyan);
		nodes.add(n);
		s.setBorderColour(Color.white);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (initComplete){
			Graphics2D g2d = createGraphics2D();
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(Color.white);
			if (TimelineOrientation.Horizontial == direction ){
				g2d.drawLine(sizeOfNode/2, this.getHeight()/2, this.getWidth()-sizeOfNode, this.getHeight()/2);
			}else{
				g2d.drawLine(this.getWidth()/2, sizeOfNode/2, this.getWidth()/2, this.getHeight()-sizeOfNode);
			}

			int i =0;
			for (TimeNode n : nodes){
				n.draw(g2d, new Point(i*(sizeOfNode+spacing), this.getHeight()/2-20));
			}
			g2d.dispose();

			g.drawImage(bufferImg, 0, 0, this);
		}
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
