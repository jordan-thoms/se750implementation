package pdedit.pdGraphWidget;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pdedit.PDEdit;
import pdedit.pdShapes.Circle;
import pdedit.pdShapes.ShapeInterface;

public class DiagramPopMenu extends JPopupMenu {

	/* This is the class because of which on the widget a popMenu generates at the run time. Options in the 
	   menu changed according to the present situation of a widget. This class has been used in GraphWidget.java
	 */
	private static final long serialVersionUID = 1L;

	private GraphWidget widget;
	private PDEdit pd;

	public DiagramPopMenu(GraphWidget widget) {
		super();
		this.widget = widget;
		createMenu();
	}

	// CreateMenu() is the method which is responsible to create popMenu
	public void createMenu(){
		this.removeAll();
		JMenuItem menuItem;
		
		//Condition 1. Two or more nodes are selected and need to create new relation between them.
		if (widget.getElements().size()>1 && widget.getSelectedNodes().size() > 0 
				&& widget.getSelectedLink() == null && widget.getGraphMagicMouseListener().isHovingOverNode()){
			
			menuItem = new JMenuItem("Create New "+widget.getLinkDescription());
			menuItem.addActionListener(new ActionListener() {


				public void actionPerformed(ActionEvent e) {
					DiagramElement n = DiagramUtility.findElement(widget, widget.getPopPoint());
					if (n != null && n instanceof DiagramNode){
						widget.setLinkAnchor((DiagramNode)n);
						n.getShape().setBorderColour(widget.getGraphMagicMouseListener().getNodeSelectionColor());
						n.getShape().setColour(Color.white);
						widget.repaint();
						widget.getGraphMagicMouseListener().sentPopPoint(((DiagramNode)n).getLocation());
					}
					widget.getGraphMagicMouseListener().setCreatingLink(true);
				}
			});
			this.add(menuItem);
		}
		//Condition 2. No hoving on any node and want to create new node.
		else if (!widget.getGraphMagicMouseListener().isHovingOverNode()){
			menuItem = new JMenuItem("Create New "+widget.getNodeDescription());
			menuItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					System.out.println("creating node");
					DiagramNode n = new DiagramNode("<Default Node>", "<Default Description>", new Circle(), widget.getPopPoint());
					System.out.println("creation has been done");
					PropertyWindow p = new PropertyWindow(n,widget);
					p.setCreation(true);
					p.pack();
					p.setVisible(true);
					widget.getElements().add(n);
					widget.repaint();
					widget.setPopPoint(new Point());
				}
			});
			this.add(menuItem);
		}
		// Condition 3. A node is selected and want to remove or to check the properties
		if (widget.getElements().size()>0 && widget.getGraphMagicMouseListener().isHovingOverNode()){
			if (widget.getSelectedLink() == null){
				menuItem = new JMenuItem("Remove "+widget.getNodeDescription());
				menuItem.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						/*if (widget.getGraphMagicMouseListener().isHovingOverNode()){
							DiagramElement el = null;
							for(DiagramElement ele : widget.getElements()){
								ShapeInterface s = ele.getShape();
								if (s.containsPoint(widget.getPopPoint())){
									el = ele;
									break;
								}
							}
							if(el != null){
								DiagramLink link;
								for(DiagramElement ele : widget.getElements(ElementType.Link)){
									link = (DiagramLink)ele;
									if(link.hasNode((DiagramNode)el)){
										widget.getElements().remove(ele);
									}
								}
								widget.getElements().remove(el);
								widget.getGraphMagicMouseListener().setHovingOverNode(false);
								widget.refreshPopup();
								for (DiagramEventListener d :widget.getDiagramListeners()){
									d.nodeRemoved(new DiagramEvent(el));
								}
								widget.repaint();
							}
						}*/
						pd = new PDEdit();
						pd.removeSelection();
					}
				});
				this.add(menuItem);
			}
			//Condition 4. More than one node and a link is selected and want to remove that link or to check the properties
			else{
				menuItem = new JMenuItem("Remove "+widget.getLinkDescription());
				menuItem.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						DiagramLink link = widget.getSelectedLink();
						Color select = widget.getGraphMagicMouseListener().getNodeSelectionColor();
						link.unselectNodes(select, Color.white);
						link.disconnectLink();
						widget.getElements().remove(link);;
						widget.setSelectedLink(null);
						widget.repaint();
						for (DiagramEventListener d :widget.getDiagramListeners()){
							d.linkRemoved(new DiagramEvent(link));
						}
					}
				});
				this.add(menuItem);
			}
		}

		if(widget.getCustomPopMenuItem().size() > 0){
			this.addSeparator();
			for (JMenuItem j : widget.getCustomPopMenuItem()){
				this.add(j);
			}
		}
	}


}
