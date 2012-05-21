package pdedit.pdGraphWidget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.InformationFloater;
import pdedit.pdShapes.ShapeInterface;
import pdedit.pdShapes.ShapeType;
import pdedit.pdShapes.SimpleLine;
import pdstore.GUID;

public class GraphWidget extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Listeners ID
	public static final int NodeCreated = 1;
	public static final int LinkCreated = 2;
	public static final int NodeChange= 3;
	public static final int LinkChange= 4;

	private ArrayList<DiagramEventListener> diagramListeners;
	private ArrayList<SelectionChangedEventListener> selectedChangedListeners;

	private BufferedImage bufferImg;
	private Timer timer = new Timer();

	private ArrayList<DiagramElement> elements = new ArrayList<DiagramElement>();
	private ArrayList<DiagramNode> selectedNodes = new ArrayList<DiagramNode>();
	private DiagramLink selectedLink;

	private ArrayList<JMenuItem> userDefinedPopMenuItems = new ArrayList<JMenuItem>();
	private String nodeSubject = "Node";
	private String linkSubject = "Link";
	Point popPoint = new Point();

	private GraphMouseAdapter graphMagicMouseListener;
	private SimpleLine tempLine = new SimpleLine();
	private MouseListener popupListener;
	private DiagramPopMenu popupMenu;
	private DiagramNode linkAnchor = null;
	public Dimension d_size = new Dimension(1000,1000);

	//Zoom In and Zoom Out Variables
	public double zoom = 1.0;  //original Zoom
	public double percentage;  // By what percentage model zooms;

	//Method to get original size;
	public void originalSize() {
		zoom = 1;
	}

	//Method to increase the scale
	public void zoomIn() {
		zoom  = zoom + percentage;
		d_size.width +=100;
		d_size.height+=100;
		System.out.println(zoom);
	}

	//Method to decrease the scale
	public void zoomOut() {
		zoom -= percentage;

		if (zoom < percentage) {
			if (percentage > 1.0) {
				zoom = 1.0;
			} else {
				zoomIn();
			}
		}
	}
	public Point adjustLayout(Point p)
	{
		p.x = p.x / (int)zoom;
		p.y = p.y / (int)zoom;
		return p;
	}
	
	
	boolean greyOut;
	private boolean showInfo;

	private JComponent myParent;

	private InformationFloater tooltip;

	public final static Color EngineeringPurple = new Color(59,41,78);
	public final static Color ScienceBlue = new Color(46,57,89);
	public final static Color lightBrown = new Color(150, 150, 150);

	public DisplayOptions options = new DisplayOptions();

	public GraphWidget(){
		
		graphMagicMouseListener = new GraphMouseAdapter(this);
		this.addMouseMotionListener(graphMagicMouseListener);
		this.addMouseListener(graphMagicMouseListener);

		popupMenu = new DiagramPopMenu(this);
		popupListener = new PopupListener(popupMenu,this);
		this.addMouseListener(popupListener);

		setup();
	}

	public GraphWidget(JComponent parent, double zoomPercentage){
		myParent = parent;
		percentage = zoomPercentage/100;
		graphMagicMouseListener = new GraphMouseAdapter(this);

		myParent.addMouseMotionListener(graphMagicMouseListener);
		myParent.addMouseListener(graphMagicMouseListener);

		popupMenu = new DiagramPopMenu(this);
		popupListener = new PopupListener(popupMenu,this);
		myParent.addMouseListener(popupListener);
		setup();
	}

	public void addMouseInterface(GraphMouseAdapter gr) {
		this.graphMagicMouseListener = gr;
	}
	public void addMouseInterface(DiagramPopMenu pop) {
		this.popupMenu = pop;
	}
	public void addMouseInterface(MouseListener poplist) {
		this.popupListener = poplist;
	}

	private void setup() {
		diagramListeners = new ArrayList<DiagramEventListener>();
		selectedChangedListeners = new ArrayList<SelectionChangedEventListener>();
		this.tooltip = new InformationFloater("");

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				updateWidgetSize(d_size);
			}
		});
		//this.setBackground(new Color(75, 75, 75));
		//this.setBackground(EngineeringPurple);
		this.setBackground(lightBrown);
		//this.setBackground(ScienceBlue);
	}

	public void setToolTip(InformationFloater tooltip){
		this.tooltip = tooltip;
	}

	public void updateWidgetSize(Dimension d){
		this.setSize(d_size);
		this.setPreferredSize(d_size);
		this.invalidate();
		this.validate();
	}

	public void updateSize(Point p,Dimension buffer){
		if ((p.x + buffer.width) > this.getWidth()){
			updateWidgetSize(new Dimension(p.x + buffer.width,this.getHeight()));
		}
		if ((p.y + buffer.height)> this.getHeight()){
			updateWidgetSize(new Dimension(this.getWidth(),p.y + buffer.height));
		}
		
	}

	public GraphMouseAdapter getGraphMagicMouseListener() {
		return graphMagicMouseListener;
	}

	/**
	 * Run Animation step wise and pushes away nodes
	 */
	public void runAnimation(){
		timer.scheduleAtFixedRate(new TimerTask() { 
			public void run() {
				step();
			} 
		}, 20, 20); 
	}

	/**
	 * stop animation
	 */
	public void stopAnimation(){
		timer.cancel();
	}

	public ArrayList<DiagramNode> getSelectedNodes(){
		return selectedNodes;
	}

	public void refreshPopup(){
		popupMenu.createMenu();
	}


	public DiagramNode getLinkAnchor() {
		return linkAnchor;
	}

	public void createLink(DiagramNode n){
		if (linkAnchor != null){
			DiagramLink temp;
			boolean found = false;
			for (DiagramElement l : getElements(ElementType.Link)){
				temp = (DiagramLink)l;
				if(temp.hasNodes(linkAnchor, n)){
					found = true;
					break;
				}
			}

			//if link does not exist create it
			if (!found){
				DiagramLink link = new DiagramLink("Role", linkAnchor, n, new SimpleLine());
				selectedNodes.add(n);
				linkAnchor.addLink(link);
				n.addLink(link);
				linkAnchor = null;
				elements.add(0, link);
				PropertyWindow p = new PropertyWindow(link,this);
				p.setCreation(true);
				p.pack();
				p.setVisible(true);
			}
		}
	}

	public void createLink(GUID nodeA, boolean isNodeAPrimitive, String role1, String role2, GUID nodeB, boolean isNodeBPrimitive, GUID roleId){
		if (!containLink(roleId) && !containLink(roleId.getPartner())){
			DiagramNode node1 = findNode(nodeA);
			if (isNodeAPrimitive){
				node1 = findNodeByRole(roleId);
			}
			DiagramNode node2 = findNode(nodeB);
			if (isNodeBPrimitive){
				node2 = findNodeByRole(roleId);
			}
			try{
				DiagramLink l = new DiagramLink("Role", node1, node2, new SimpleLine());
				l.setId(roleId);
				l.setRelation1(role1);
				l.setRelation2(role2);
				node1.addLink(l);
				node2.addLink(l);
				elements.add(0, l);
			}catch (Exception e) {
				//e.printStackTrace();
				System.err.println("[GraphWidget]-createLink: ...--...  ...--...  ...--... No Link to create");
			}
		}else{
			System.err.println("Link Exist: Skipping");
		}
	}

	private boolean containLink(GUID id){
		for (DiagramElement e : elements){
			if (e instanceof DiagramLink && ((DiagramLink)e).getId().equals(id)){
				return (true);
			}
		}
		return false;
	}

	private DiagramNode findNode(GUID id){
		for (DiagramElement e : elements){
			if (e instanceof DiagramNode &&((DiagramNode)e).getId().equals(id)){
				return (DiagramNode)e;
			}
		}
		return null;
	}

	private DiagramNode findNodeByRole(GUID id){
		for (DiagramElement e : elements){
			if (e instanceof DiagramNode && ((DiagramNode)e).getRoleID() != null &&
					((DiagramNode)e).getRoleID().equals(id)){
				return (DiagramNode)e;
			}
		}
		return null;
	}

	public ArrayList<DiagramElement> getElements(ElementType type){
		ArrayList<DiagramElement> e = new ArrayList<DiagramElement>();
		for(DiagramElement temp : elements){
			if (temp.getElementType() == type){
				e.add(temp);
			}
		}
		return e;
	}
	private void step(){
		for (DiagramElement n : elements){
			ShapeInterface s = n.getShape();
			if (s.getShapeType() != ShapeType.Line){
				Point p = (Point)s.getLocation();
				if (p.x > 600){
					p.x = 0;
				}
				p.setLocation(p.x+2, p.y);
				s.setLocation(p);
			}
		}
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = createGraphics2D();
		g2d.scale(zoom, zoom);
		g2d.setStroke(new BasicStroke(5));
		if (graphMagicMouseListener.isCreatingLink()){
			tempLine.drawShape(g2d);
		}
		// Draw the shapes
		for (DiagramElement n : elements){
			n.drawElement(g2d, this.options);
		}

		// Add the labels
		for (DiagramElement n : elements){
			n.drawLabel(g2d, this.options);
		}

		if(greyOut){

			g2d.setColor(new Color(255,255,255,100));
			g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
			tooltip = new InformationFloater("Graph: Waiting . . .");
			tooltip.setLocation(new Point(this.getLocation().x+this.getWidth()/2, 
					this.getLocation().y+this.getHeight()/2));
			tooltip.setColour(new Color(0, 0, 0, 200));
			tooltip.drawShape(g2d);
		}else if (showInfo){
			tooltip.drawShape(g2d);
		}
		g2d.dispose();
		g.drawImage(bufferImg, 0, 0, this);
	}

	public void addDiagramEventListener(DiagramEventListener d){
		diagramListeners.add(d);
	}

	public void removeDiagramEventListener(DiagramEventListener d){
		diagramListeners.remove(d);
	}

	public void addSelectionChangedEventListener(SelectionChangedEventListener d){
		selectedChangedListeners.add(d);
	}

	public void removeSelectionChangedEventListener(SelectionChangedEventListener d){
		selectedChangedListeners.remove(d);
	}

	public ArrayList<DiagramElement> getElements(){
		return elements;
	}

	/**
	 * Generates a snapshot of the current mode.
	 * @param snapshotName
	 * 	The name of the file to save the snapshot in.
	 */
	public void takeSnapshot(String snapshotName){
		// Calculate the dimensions
		Point bottomRight = new Point(0, 0);
		Point topLeft = new Point(2000000000, 2000000000);
		for (DiagramElement element: elements){
			if (element instanceof DiagramNode){
				ShapeInterface shape = element.getShape();
				Point location = shape.getLocation();
				if (location.x < topLeft.x) {
					topLeft.x = location.x;
				}
				
				if (location.y < topLeft.y) {
					topLeft.y = location.y;
				}
				
				Dimension size = shape.getSize();
				location = new Point(location.x + size.width, location.y + size.height);
				if (location.x > bottomRight.x) {
					bottomRight.x = location.x;
				}
				
				if (location.y > bottomRight.y) {
					bottomRight.y = location.y;
				}
			}
		}
		
		final int border = 20;
		try {
			// Save the snapshot
			BufferedImage temp = (BufferedImage)createImage(
					bottomRight.x - topLeft.x + border * 2,
					bottomRight.y - topLeft.y + border * 2);
			Graphics2D tempGraphics = temp.createGraphics();
			tempGraphics.drawImage(this.bufferImg,
					0, 0, temp.getWidth(), temp.getHeight(),
					topLeft.x - border, topLeft.y - border, bottomRight.x + border, bottomRight.y + border, 
					null);
			FileOutputStream output = new FileOutputStream(snapshotName);
			ImageIO.write(temp, "png", output);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
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

	public String getNodeDescription() {
		return nodeSubject;
	}

	public void setNodeDescription(String nodeSubject) {
		this.nodeSubject = nodeSubject;
		refreshPopup();
	}

	public String getLinkDescription() {
		return linkSubject;
	}

	public void setLinkDescription(String linkSubject) {
		this.linkSubject = linkSubject;
		refreshPopup();
	}

	public void addCustomPopMenuItem(JMenuItem jmi){
		userDefinedPopMenuItems.add(jmi);
	}

	public ArrayList<JMenuItem> getCustomPopMenuItem(){
		return userDefinedPopMenuItems;
	}

	public ArrayList<DiagramEventListener> getDiagramListeners() {
		return diagramListeners;
	}

	public ArrayList<SelectionChangedEventListener> getSelectionChangedListeners() {
		return selectedChangedListeners;
	}

	public DiagramLink getSelectedLink() {
		return selectedLink;
	}

	public void setSelectedLink(DiagramLink selectedLink) {
		this.selectedLink = selectedLink;
	}

	public Point getPopPoint() {
		popPoint = adjustLayout(popPoint);
		return popPoint;
	}

	public void setPopPoint(Point popPoint) {
		this.popPoint = popPoint;
	}

	public void setLinkAnchor(DiagramNode linkAnchor) {
		this.linkAnchor = linkAnchor;
	}

	public SimpleLine getTempLine() {
		return tempLine;
	}

	public void setTempLine(SimpleLine tempLine) {
		this.tempLine = tempLine;
	}

	public void disableWiget(boolean b){
		this.greyOut = b;
		graphMagicMouseListener.disableListener(b);
		this.repaint();
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}

	public void reset(){
		elements.clear();
		selectedNodes.clear();
		selectedLink = null;
		this.repaint();
	}

	public void createNode(String name, String description, String type, GUID id, GUID role, Point p){
		System.out.println("start creating node");
		DiagramNode n = new DiagramNode(name, description, new Circle(), p);
		System.out.println("Created Node: "+name);
		System.out.println("Node Description: "+description);
		n.setId(id);
		n.setKind(type);
		n.setRoleID(role);
		elements.add(n);
		updateSize(p, d_size);
	}

	/**
	 * Fire the selection changed event.
	 */
	public void fireSelectionChanged(){
		SelectionChangedEvent event;
		if (this.selectedLink == null) {
			event = new SelectionChangedEvent(this, selectedNodes);
		} else {
			event = new SelectionChangedEvent(this, this.selectedLink);
		}
		Iterator<SelectionChangedEventListener> listeners = this.selectedChangedListeners.iterator();
		while (listeners.hasNext()){
			listeners.next().SelectionChanged(event);
		}
	}
}
