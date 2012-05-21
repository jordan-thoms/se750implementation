package pdstore.ui.graphview;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.*;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.PDChange;
import pdstore.sparql.Query;
import pdstore.sparql.ResultElement;
import pdstore.sparql.Variable;
import pdstore.ui.PDStore;
import pdstore.ui.graphview.dal.*;

public class GraphView extends JPanel implements MouseListener,
		MouseMotionListener {

	static {
		Debug.addDebugTopic("addNewInstance");
	}

	/**
	 * auto-generated class id for java serialization
	 */
	private static final long serialVersionUID = 4204606997608235219L;

	// GUIDs for the used model, types and their roles
	public final static GUID GRAPHVIEW_MODELID = new GUID(
			"e44bd0301d9211e1a04700235411d565");
	public final static GUID GRAPH_TYPEID = new GUID(
			"e44c1e501d9211e1a04700235411d565");
	public final static GUID NODE_TYPEID = new GUID(
			"e44c1e511d9211e1a04700235411d565");
	public final static GUID GRAPH_NODE_ROLEID = new GUID(
			"e44c1e521d9211e1a04700235411d565");
	public final static GUID NODE_INSTANCE_ROLEID = new GUID(
			"e44c1e531d9211e1a04700235411d565");
	public final static GUID NODE_X_ROLEID = new GUID(
			"e44c1e541d9211e1a04700235411d565");
	public final static GUID NODE_Y_ROLEID = new GUID(
			"e44c1e551d9211e1a04700235411d565");
	public final static GUID DUMMYe44c1e561d9211e1a04700235411d565 = new GUID(
			"e44c1e561d9211e1a04700235411d565");
	public final static GUID DUMMYe44c1e571d9211e1a04700235411d565 = new GUID(
			"e44c1e571d9211e1a04700235411d565");

	/**
	 * The PDStore working copy we are using
	 */
	PDWorkingCopy copy;

	/**
	 * The PDStore we are using
	 */
	PDStore store;

	/**
	 * The graph that is being viewed/edited
	 */
	PDGraph graph;

	public PDGraph getGraph() {
		return graph;
	}

	public void setGraph(PDGraph graph) {
		this.graph = graph;
		this.copy = graph.getPDWorkingCopy();
		repaint();
	}

	/**
	 * The node that is currently selected, or null if no selection.
	 */
	Set<PDNode> selectedNodes = new HashSet<PDNode>();

	/**
	 * The edge that is currently selected, or null if no selection. The edge is
	 * represented as a PDChange since the edges are only visualizing links.
	 */
	PDChange<GUID, Object, GUID> selectedEdge;

	/**
	 * Number of pixels that a user can click away from the middle of an edge
	 * (on both sides) and still select it.
	 */
	static double EDGE_SELECTION_TOLERANCE = 8;

	/**
	 * During a drag operation, this contains the starting point of the drag.
	 */
	Point startDragPoint;

	/**
	 * Map from instance Objects to their node representations. Used in paint().
	 * This is also used to make sure edges are only drawn once, because a node
	 * is put into nodes after its edges have been drawn, and the edges of a
	 * node are only drawn if the target node is not already in nodes (i.e.
	 * edges are only drawn from one node but not the other).
	 */
	Map<Object, PDNode> nodes = new Hashtable<Object, PDNode>();

	/**
	 * Edges drawn in paint() are registered here. It is used in
	 * edgeNearLocation() to iterate through the current edges and determine if
	 * an edge was selected. It could be extended later on to find extra
	 * visualization info about edges (i.e. using some value type other than
	 * Boolean).
	 */
	Map<PDChange<GUID, Object, GUID>, Boolean> edges = new Hashtable<PDChange<GUID, Object, GUID>, Boolean>();

	/**
	 * The last mouse position where a mouse key was pressed, so that it can be
	 * used to display a context menu there.
	 */
	Point mousePosition;

	final GraphView graphView = this;

	/**
	 * The context menus for different right-click locations.
	 */
	JPopupMenu nodeMenu = new JPopupMenu();
	JPopupMenu edgeMenu = new JPopupMenu();
	JPopupMenu canvasMenu = new JPopupMenu();

	public GraphView(PDStore store, Object... instances) {
		this.store = store;

		/*
		 * Internally the view works only with GUIDs. So if a root instance is
		 * given as DAL object (subclass of PDInstance), then use its GUID
		 * instead.
		 */
		for (int i = 0; i < instances.length; i++) {
			if (instances[i] instanceof PDInstance)
				instances[i] = ((PDInstance) instances[i]).getId();
		}

		// create a new graph and nodes for the given instances
		copy = new PDSimpleWorkingCopy(store);
		graph = new PDGraph(copy);
		Random random = new Random();
		for (Object instance : instances) {
			PDNode node = new PDNode(copy);
			// TODO The instance should be of type Object. Also primitive
			// instances should be possible.
			node.addShownInstance((GUID) instance);
			node.addX(new Double(random.nextInt(200)));
			node.addY(new Double(random.nextInt(200)));
			graph.addNode(node);
		}
		copy.commit();

		initUI();
	}

	public GraphView(final PDGraph graph) {
		this.graph = graph;
		this.copy = graph.getPDWorkingCopy();
		this.store = new PDStore(copy.getStore());

		initUI();
	}

	void initUI() {
		JMenuItem addNewInstanceMenuItem = new JMenuItem("Add new instance...");
		addNewInstanceMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// query all the type names from the DB
				// TODO this should be less verbose
				Variable typeVar = new Variable("type");
				Variable typeNameVar = new Variable("type name");

				List<Variable> select = new ArrayList<Variable>();
				select.add(typeNameVar);

				List<PDChange<GUID, Object, GUID>> where = new ArrayList<PDChange<GUID, Object, GUID>>();
				where.add(new PDChange<GUID, Object, GUID>(
						ChangeType.LINK_EFFECTIVE, null, typeVar,
						PDStore.HAS_TYPE_ROLEID, PDStore.TYPE_TYPEID));
				where.add(new PDChange<GUID, Object, GUID>(
						ChangeType.LINK_EFFECTIVE, null, typeVar,
						PDStore.NAME_ROLEID, typeNameVar));

				Query query = new Query(select, where, null, null, store);
				Iterator<ResultElement<GUID, Object, GUID>> resultSet = query
						.execute(copy.getTransaction());

				List<Object> typeNames = new ArrayList<Object>();
				while (resultSet.hasNext()) {
					ResultElement<GUID, Object, GUID> result = resultSet.next();
					typeNames.add(result.get(typeNameVar));
				}

				Object[] choices = typeNames.toArray();
				String typeName = (String) JOptionPane.showInputDialog(null,
						"Please select a type for the new instance:",
						"Add New Instance", JOptionPane.PLAIN_MESSAGE, null,
						choices, null);

				if ((typeName != null) && (typeName.length() > 0)) {
					PDStore store = new PDStore(copy.getStore());
					GUID transaction = copy.getTransaction();

					// create new instance of the given type
					GUID newInstanceID = new GUID();
					GUID typeID = copy.getId(typeName);
					store.setType(transaction, newInstanceID, typeID);

					// add new node
					PDNode newNode = new PDNode(copy);
					newNode.addShownInstance(newInstanceID);
					newNode.addX(mousePosition.getX());
					newNode.addY(mousePosition.getY());
					graph.addNode(newNode);
					graphView.repaint();
				}
			}
		});
		canvasMenu.add(addNewInstanceMenuItem);

		JMenuItem addExistingInstanceMenu = new JMenuItem(
				"Add existing instance...");
		addExistingInstanceMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewNodeDialog d = new NewNodeDialog(mousePosition);
				d.setVisible(true);
			}
		});
		canvasMenu.add(addExistingInstanceMenu);

		JMenuItem addLinkMenuItem = new JMenuItem("Add link...");
		addLinkMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		nodeMenu.add(addLinkMenuItem);

		JMenuItem removeNodeMenuItem = new JMenuItem("Remove node(s)...");
		removeNodeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nodeLabels = "";
				for (PDNode node : selectedNodes) {
					nodeLabels += copy.getLabel(node.getShownInstance()) + "\n";
				}

				int answer = JOptionPane.showConfirmDialog(null,
						"Would you like to remove the nodes for the following instances?"
								+ nodeLabels, "Remove Node(s)",
						JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					graphView.graph.removeNodes(selectedNodes);
					selectedNodes.clear();
					graphView.repaint();
				}
			}
		});
		nodeMenu.add(removeNodeMenuItem);

		JMenuItem renameInstanceMenuItem = new JMenuItem("Rename instance...");
		renameInstanceMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// choose only one of the selected instances -- no power
				// renaming yet
				GUID instance = ((PDNode) selectedNodes.toArray()[0])
						.getShownInstance();
				String instanceName = copy.getName(instance);

				JTextField textField = new JTextField(40);
				if (instanceName != null) {
					textField.setText(instanceName);
					textField.setSelectionStart(0);
					textField.setSelectionEnd(instanceName.length());
				}

				String newName = JOptionPane.showInputDialog(null,
						"New name for " + copy.getLabel(instance) + "\":",
						instanceName);

				if (!newName.equals(instanceName)) {
					copy.setName(instance, newName);
					graphView.repaint();
				}
			}
		});
		nodeMenu.add(renameInstanceMenuItem);

		JMenuItem removeLinkMenuItem = new JMenuItem("Remove link...");
		removeLinkMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(
						null,
						"Would you like to remove the link \n"
								+ copy.toString(selectedEdge) + " ?",
						"Remove Node", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					copy.getStore().removeLink(copy.getTransaction(),
							selectedEdge.getInstance1(),
							selectedEdge.getRole2(),
							selectedEdge.getInstance2());
					graphView.repaint();
				}
			}
		});
		edgeMenu.add(removeLinkMenuItem);

		// install the mouse listeners to handle the mouse operations such as
		// dragging
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * This method paints the GraphView. It is called through repaint() whenever
	 * something changes and the GUI needs repainting.
	 */
	public void paint(Graphics g) {
		Debug.println("Entering paint().", "paint");

		super.paint(g);
		Graphics2D graphics = (Graphics2D) g;

		// This starts a new transaction, ensuring that we get get up-to-date
		// information.
		copy.commit();

		PDStore store = new PDStore(copy.getStore());
		GUID transaction = copy.getTransaction();

		// no nodes or edges have been drawn yet in this call to paint, so clear
		// nodes and edges maps.
		nodes.clear();
		edges.clear();

		/*
		 * Get the changes between the instances of the nodes - they will be
		 * drawn as edges. For each such change, draw the link between the node.
		 * Drawing the edges before drawing the nodes on top of them makes sure
		 * the edges connect precisely to the nodes (painter's algorithm).
		 */
		for (PDNode node1 : graph.getNodes()) {
			Point location1 = new Point(node1.getX().intValue(), (int) node1
					.getY().intValue());
			GUID instance1 = node1.getShownInstance();
			GUID type1 = store.getType(transaction, node1.getShownInstance());

			// add the node to the map so it is found when drawing edges
			nodes.put(instance1, node1);

			Debug.println("Searching edges for " + copy.getLabel(instance1),
					"edges");

			// enable anti-aliasing
			RenderingHints rh = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHints(rh);

			// set drawing parameters
			graphics.setStroke(new BasicStroke(2.5f));

			// go through the accessible roles of the node...
			for (GUID role2 : store.getAccessibleRoles(transaction, type1)) {

				Debug.println("  Traversing " + copy.getLabel(role2), "edges");

				// go through the changes from instance1 using the role...
				for (Object instance2 : store.getInstances(transaction,
						instance1, role2)) {

					// check if the change connects to an instance that is also
					// part of the graph as a node
					if (!nodes.containsKey(instance2))
						continue; // no instance connected in this role

					Debug.println(
							"    Drawing edge to " + copy.getLabel(instance2),
							"edges");

					PDNode node2 = nodes.get(instance2);
					Point location2 = new Point(node2.getX().intValue(),
							(int) node2.getY().intValue());

					// register the edge
					PDChange<GUID, Object, GUID> edge = new PDChange<GUID, Object, GUID>(
							null, null, instance1, role2, instance2);
					edges.put(edge, true);
					Debug.println(
							"Added edge " + edge.toString(store, transaction),
							"selection");

					// draw the edge
					graphics.drawLine(location1.x, location1.y, location2.x,
							location2.y);
				}
			}
		}

		// now draw all the nodes on top of the edges
		for (PDNode node : graph.getNodes()) {

			Point location = new Point(node.getX().intValue(), (int) node
					.getY().intValue());

			// if the node is selected, draw highlight in the background first
			if (selectedNodes.contains(node)) {
				double radius = 12;
				graphics.setPaint(Color.BLUE);
				RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(
						location.x - 1.5 * radius, location.y - 1.5 * radius,
						3 * radius, 3 * radius, 10, 10);
				graphics.draw(roundedRectangle);
			}

			// draw the instance with its associated widget
			// for each widget a local graphics context is created that has its
			// origin at the center of the widget
			Graphics2D widgetGraphics = (Graphics2D) g.create();
			widgetGraphics.translate(location.getX(), location.getY());			
			store.drawInstance(transaction, node.getShownInstance(), widgetGraphics);
		}

		Debug.println("Exiting paint().", "paint");
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * This method is called whenever a mouse button is pressed.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// see if there is a node or edge near the mouse and make it the
		// selected node/edge
		PDNode newSelectedNode = nodeAtLocation(e.getPoint());

		// If the Control key is not pressed (single selection), then clear the
		// previous selection.
		// If the Control key is pressed (multiple selection), then the
		// previously selected nodes remain selected.
		if (!e.isControlDown())
			selectedNodes.clear();

		// add the new selected node to the list of selected nodes
		if (newSelectedNode != null)
			selectedNodes.add(newSelectedNode);

		// see if there is an edge near the mouse and make it the
		// selected edge
		selectedEdge = edgeNearLocation(e.getPoint());

		Debug.println("Newly selected node " + newSelectedNode, "selection");
		Debug.println("Selected edge " + selectedEdge, "selection");

		// in case we are dragging, set the press position as start point for
		// the next drag event
		startDragPoint = e.getPoint();

		repaint();
	}

	/**
	 * Returns a node that is at the given location, or null.
	 * 
	 * @param location
	 * @return a node at the given location
	 */
	PDNode nodeAtLocation(Point location) {
		// TODO get nearest node, not just first one
		for (PDNode node : graph.getNodes()) {
			if (location.x >= node.getX() - 15
					&& location.y >= node.getY() - 15
					&& location.x <= node.getX() + 15
					&& location.y <= node.getY() + 15) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Identifies an edge that is at the given location, or null.
	 * 
	 * @param location
	 * @return a PDChange that is represented by the edge at the given location
	 */
	PDChange<GUID, Object, GUID> edgeNearLocation(Point location) {
		// find the edge closest to the given location
		PDChange<GUID, Object, GUID> closestEdge = null;
		double closestDistance = Double.MAX_VALUE;

		for (PDChange<GUID, Object, GUID> edge : edges.keySet()) {
			PDNode n1 = nodes.get(edge.getInstance1());
			PDNode n2 = nodes.get(edge.getInstance2());
			Point2D.Double p1 = new Point2D.Double(n1.getX(), n1.getY());
			Point2D.Double p2 = new Point2D.Double(n2.getX(), n2.getY());

			double distance = distanceToLineSegment(p1, p2, location);

			Debug.println("Edge " + edge + " has distance " + distance,
					"selection");

			if (distance < closestDistance) {
				closestEdge = edge;
				closestDistance = distance;
			}
		}

		if (closestDistance <= EDGE_SELECTION_TOLERANCE)
			return closestEdge;

		return null;
	}

	private double distanceToLineSegment(Point2D p1, Point2D p2, Point2D p3) {
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();

		// if the line segment is just a point, return the distance between the
		// points
		if (dx == 0 && dy == 0)
			return p1.distance(p3);

		double u = ((p3.getX() - p1.getX()) * dx + (p3.getY() - p1.getY()) * dy)
				/ (dx * dx + dy * dy);

		Point2D closestPoint;
		if (u < 0)
			closestPoint = p1;
		else if (u > 1)
			closestPoint = p2;
		else
			closestPoint = new Point2D.Double(p1.getX() + u * dx, p1.getY() + u
					* dy);

		return closestPoint.distance(p3);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// If no node is being dragged, then there is nothing to do.
		if (selectedNodes.size() == 0)
			return;

		// calculate dragging vector for this dragging event
		double dx = e.getX() - startDragPoint.x;
		double dy = e.getY() - startDragPoint.y;

		// set the starting point for the next dragging event
		startDragPoint.setLocation(e.getPoint());

		// move all the selected nodes by the dragging vector
		for (PDNode node : selectedNodes) {
			node.setX(node.getX() + dx);
			node.setY(node.getY() + dy);
		}
		copy.commit();

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePosition = e.getPoint();

		// check if the right button was released (since isPopupTrigger()
		// doesn't work properly we also check the right button explicitly)
		if (!e.isPopupTrigger() && e.getButton() != MouseEvent.BUTTON3)
			return;

		// From here on we can be sure that a context menu has been activated.
		// Depending on where was clicked, a different menu is shown.
		if (selectedNodes.size() > 0)
			nodeMenu.show(e.getComponent(), e.getX(), e.getY());
		else if (selectedEdge != null)
			edgeMenu.show(e.getComponent(), e.getX(), e.getY());
		else
			canvasMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
