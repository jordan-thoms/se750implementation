package pdedit.pdGraphWidget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;

import pdedit.pdShapes.InformationFloater;
import pdedit.pdShapes.ShapeInterface;
/**
 * This is a Mouse Adapter
 * @author Ted Yeung
 *Node Type
 */
public class GraphMouseAdapter extends MouseAdapter {
	private GraphWidget widget;
	private boolean mouseDownOnSelected;
	private boolean isMouseDragged;
	private boolean isCreatingLink;
	private boolean hoveringOverNode;
	private boolean linkSelected;
	private boolean isThisDisabled;
	private DiagramElement hoveredElement;

	private Point mousePressedPoint;
	private Color nodeSelectionColor = new Color(0, 139, 225);
	private Color nodeHoverColor = new Color(127,206,255);
	
	public GraphMouseAdapter(GraphWidget w){
		widget = w;
		mousePressedPoint = new Point();
	}

	public void mouseMoved(MouseEvent e){
		Point mouse = e.getPoint();
		mouse = widget.adjustLayout(mouse); //information comes
		if (e.getComponent() instanceof JScrollPane){
			Point temp = ((JScrollPane)e.getComponent()).getViewport().getViewPosition();
			temp = widget.adjustLayout(temp);
			mouse.x += temp.x;
			mouse.y += temp.y;
		}
		if (!isThisDisabled){
			widget.setShowInfo(false);
			widget.setToolTip(null);
			if (isCreatingLink){
				
				trekingTempLink(mouse);
			}else{
				
				hoverTracking(mouse);
			}
			widget.repaint();
		}
	}

	/**
	 * Tracks mouse if hover over node highlight currently on support single hover.
	 * @param e
	 */
	private void hoverTracking(Point mouse) {
		DiagramElement element = findElement(mouse);
		if (element != null && !widget.getSelectedNodes().contains(element) && !linkSelected){
			if (hoveredElement != null){
				clearHoveredElement();
			}
			hoveredElement = element;
			ShapeInterface s = element.getShape();
			InformationFloater showThis = element.getToolTip();
			Point drawAt = new Point(mouse.x + 10
					,mouse.y + 10);
			showThis.setTopLeftCorner(drawAt);
			widget.setShowInfo(true);
			widget.setToolTip(showThis);
			if (s.getPrevColour() == null){
				s.setPrevColour(s.getColour());
				s.setColour(nodeHoverColor);
				hoveringOverNode = true;
				widget.validate();
				widget.refreshPopup();
			}

		}else if(hoveredElement != null && !hoveredElement.isSelected()){
			clearHoveredElement();
			widget.refreshPopup();
		}

	}

	private void clearHoveredElement(){
		hoveringOverNode = false;
		ShapeInterface s = hoveredElement.getShape();
		s.setColour(s.getPrevColour());
		s.setPrevColour(null);
		widget.setShowInfo(false);
		widget.setToolTip(null);
		hoveredElement = null;
	}

	/**
	 * This method tracks the movements of the mouse and draw a line between the the mouse and the node
	 * on mouse moved.
	 * @param e
	 */
	private void trekingTempLink(Point mouse) {
		widget.getTempLine().setEnds(mousePressedPoint, mouse);
		widget.setShowInfo(false);
		widget.setToolTip(null);
		for (DiagramElement n : widget.getElements()){
			ShapeInterface s = n.getShape();
			if (n instanceof DiagramNode){
				if (s.containsPoint(mouse)){
					s.setBorderColour(nodeSelectionColor);
					if (s.getPrevColour() == null){
						s.setPrevColour(s.getColour());
					}
					s.setColour(Color.white);
					widget.getTempLine().setEnds(mousePressedPoint, ((DiagramNode)n).getLocation());
					break;
				}else if (!widget.getSelectedNodes().contains((DiagramNode)n)){
					if (s.getPrevColour()!=null){
						s.setBorderColour(Color.white);
						s.setColour(s.getPrevColour());
						s.setPrevColour(null);
					}
				}
			}
		}
	}

	public void mouseDragged(MouseEvent e){
		if (!isThisDisabled){
			widget.setShowInfo(false);
			widget.setToolTip(null);
			Point mouse = e.getPoint();
			mouse = widget.adjustLayout(mouse); //1. mouse point movement
			if (widget.getSelectedNodes() != null && !widget.getSelectedNodes().isEmpty() && mouseDownOnSelected){
				updateSelectedNodesPositions(mouse);
			}
			isMouseDragged = true;
		}
	}

	public void mousePressed(MouseEvent e){
		if (!isThisDisabled){
			if (isCreatingLink){
				ui_CreatingLinkValidator(e);
			}else{
				Point mouse = e.getPoint();
				mouse = widget.adjustLayout(mouse); // moving node
				if (e.getComponent() instanceof JScrollPane){
					Point temp = ((JScrollPane)e.getComponent()).getViewport().getViewPosition();
					temp = widget.adjustLayout(temp);
					mouse.x += temp.x;
					mouse.y += temp.y;
				}
				DiagramElement element = findSelected(mouse);
				if (element != null){
					mouseDownOnSelected = true;
					mousePressedPoint = e.getPoint();
					mousePressedPoint = widget.adjustLayout(mousePressedPoint); //2. mouse point movement
					hoveringOverNode = true;
					widget.refreshPopup();
				}else{
					hoveringOverNode = false;
					mouseDownOnSelected = false;
					widget.refreshPopup();
				}
			}
		}
	}


	private void ui_CreatingLinkValidator(MouseEvent e) {
		System.out.println("link validator");
		Point mouse = e.getPoint();
		mouse = widget.adjustLayout(mouse);
		if (e.getComponent() instanceof JScrollPane){
			Point temp = ((JScrollPane)e.getComponent()).getViewport().getViewPosition();
			temp = widget.adjustLayout(temp);
			mouse.x += temp.x;
			mouse.y += temp.y;
		}
		DiagramElement element = findElement(mouse);
		if (element != null && element instanceof DiagramNode){
			DiagramNode n = (DiagramNode) element;
			//set connecting element color
			n.getShape().setColour(nodeSelectionColor);
			n.getShape().setBorderColour(Color.white);
			//reset link anchor color
			widget.getLinkAnchor().getShape().setBorderColour(Color.white);
			widget.getLinkAnchor().getShape().setColour(nodeSelectionColor);
			//create link
			widget.createLink(n);
		}else if (widget.getLinkAnchor() != null) {
			ShapeInterface s = widget.getLinkAnchor().getShape();
			s.setBorderColour(Color.white);
			s.setColour(s.getPrevColour());
			s.setPrevColour(null);
			widget.setLinkAnchor(null);
		}
		widget.refreshPopup();
		isCreatingLink = false;
	}

	public void mouseReleased(MouseEvent e){
		if (!isThisDisabled){
			if (isMouseDragged){
				for (DiagramNode n : widget.getSelectedNodes()){
					for(DiagramEventListener l : widget.getDiagramListeners()){
						l.nodeChanged(new DiagramEvent(n));
					}
				}
			}
			
			if (e.getButton() == MouseEvent.BUTTON1 && !isMouseDragged){
				if (!isCreatingLink){
					Point mouse = e.getPoint();
					mouse = widget.adjustLayout(mouse); // selection works
					if (e.getComponent() instanceof JScrollPane){
						Point temp = ((JScrollPane)e.getComponent()).getViewport().getViewPosition();
						temp = widget.adjustLayout(temp);
						mouse.x += temp.x;
						mouse.y += temp.y;
					}
					searchForSelection(mouse);
				}
			}
			
			if (isMouseDragged){
				isMouseDragged = false;
			}
		}
	}
	//---------------------------------------------------------------
	//---------------helper methods:
	//---------------------------------------------------------------

	/**
	 * This method search for mouse point single selection
	 * @param e (MouseEvent Object)
	 */
	private void searchForSelection(Point mouse){
		boolean fireEvent = false;
		DiagramElement element = findElement(mouse);
		if (element != null){
			boolean isNode = element.getElementType() == ElementType.Node;
			ShapeInterface shape = element.getShape();
			if (!element.isSelected()){
				fireEvent = true;
				if (isNode){
					clearSelLink();
					DiagramNode n = (DiagramNode)element;
					shape.setColour(nodeSelectionColor);
					widget.getSelectedNodes().add(n);
					n.setSelected(true);
					hoveredElement = null;
					for(DiagramEventListener d : widget.getDiagramListeners()){
						d.nodeSelected(new DiagramEvent(n));
					}
				}else if (element.getElementType() == ElementType.Link && widget.getSelectedLink() == null){
					clearSelNodes();
					DiagramLink n = (DiagramLink)element;
					shape.setColour(nodeSelectionColor);
					n.setSelected(true);
					widget.setSelectedLink(n);
					hoveredElement = null;
					n.selectNodes(Color.white, nodeSelectionColor);
					linkSelected = true;
					for(DiagramEventListener d : widget.getDiagramListeners()){
						d.linkSelected(new DiagramEvent(n));
					}
				}
			}else {
				if (isNode){
					fireEvent = true;
					DiagramNode n = (DiagramNode)element;
					shape.setColour(shape.getPrevColour());
					widget.getSelectedNodes().remove(n);
					n.setSelected(false);
					hoveredElement = null;
					for(DiagramEventListener d : widget.getDiagramListeners()){
						d.nodeSelected(new DiagramEvent(n));
					}
				}
			}
		}else {
			//clearSelectedElements
			fireEvent = true;
			clearSelectedElements();
			hoveringOverNode = false;
			linkSelected = false;
		}
		
		if (fireEvent){
			widget.fireSelectionChanged();
		}
		
		widget.repaint();
		widget.refreshPopup();
	}

	/**
	 * Update the positions of the nodes when dragged
	 * @param e
	 */

	private void updateSelectedNodesPositions(Point e){
		//e = widget.adjustLayout(e);
		for (DiagramNode n : widget.getSelectedNodes()){
			Point mouseToCenter = new Point(
					mousePressedPoint.x - n.getLocation().x,
					mousePressedPoint.y- n.getLocation().y
			
			);
			n.setLocation(new Point(
					e.x-mouseToCenter.x,
					e.y-mouseToCenter.y
			));
			widget.updateSize(n.getLocation(), widget.d_size);
		}
		mousePressedPoint = e;
		//mousePressedPoint = widget.adjustLayout(mousePressedPoint);
		widget.repaint();
	}

	public Color getNodeSelectionColor() {
		return nodeSelectionColor;
	}

	public void setNodeSelectionColor(Color nodeSelectionColor) {
		this.nodeSelectionColor = nodeSelectionColor;
	}

	public boolean isCreatingLink() {
		return isCreatingLink;
	}

	public void setCreatingLink(boolean isCreatingLink) {
		this.isCreatingLink = isCreatingLink;
	}

	public void sentPopPoint(Point p){
		mousePressedPoint = p;
		//mousePressedPoint = widget.adjustLayout(mousePressedPoint);
		widget.getTempLine().setColour(nodeSelectionColor);
		widget.getTempLine().setEnds(p,p);
	}

	public boolean isHovingOverNode() {
		return hoveringOverNode;
	}


	public void setHovingOverNode(boolean hovingOverNode) {
		this.hoveringOverNode = hovingOverNode;
	}

	public void disableListener(boolean b){
		isThisDisabled = b;
	}

	private DiagramElement findElement(Point p){
		for (DiagramElement n : widget.getElements()){
			ShapeInterface s = n.getShape();
			if (s.containsPoint(p)){
				return n;
			}
		}
		return null;
	}

	private DiagramElement findSelected(Point p){
		for (DiagramElement n : widget.getSelectedNodes()){
			ShapeInterface s = n.getShape();
			if (s.containsPoint(p)){
				return n;
			}
		}
		if (widget.getSelectedLink() != null){
			return widget.getSelectedLink();
		}
		return null;
	}

	private void clearSelectedElements(){
		clearSelNodes();
		clearSelLink();
	}


	private void clearSelLink() {
		if (widget.getSelectedLink() != null){
			widget.getSelectedLink().unselectNodes(nodeSelectionColor,Color.white);
			widget.getSelectedLink().getShape().setSelected(false);
			widget.getSelectedLink().getShape().setColour(Color.white);
			widget.setSelectedLink(null);
		}
	}


	private void clearSelNodes() {
		for (DiagramElement n : widget.getSelectedNodes()){
			if (n.getElementType() == ElementType.Node){
				ShapeInterface s = n.getShape();
				((DiagramNode)n).setSelected(false);
				if (s.getColour() == nodeSelectionColor){
					s.setColour(s.getPrevColour());
					s.setPrevColour(null);
				}
			}	
		}

		widget.getSelectedNodes().clear();
	}

}
