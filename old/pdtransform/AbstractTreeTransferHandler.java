package pdtransform;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.image.*;

/**
 * Creates a template that enables dragging and dropping of nodes in a Jtree.
 * The cursor image changes depending on how and where a node is being to give
 * visual feedback to the user
 * 
 * @author Gyurme Dahdul
 * 
 */
public abstract class AbstractTreeTransferHandler implements
		DragGestureListener, DragSourceListener, DropTargetListener {

	private DNDTree tree;
	private DragSource dragSource;
	private static DefaultMutableTreeNode draggedNode;
	private DefaultMutableTreeNode draggedNodeParent;
	private static BufferedImage image = null;
	private Rectangle rect2D = new Rectangle();
	private boolean drawImage;

	protected AbstractTreeTransferHandler(DNDTree tree, int action,
			boolean drawIcon) {
		this.tree = tree;
		drawImage = drawIcon;
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(tree, action, this);
	}

	/* Methods for DragSourceListener */
	public void dragDropEnd(DragSourceDropEvent dsde) {
		if (dsde.getDropSuccess()
				&& dsde.getDropAction() == DnDConstants.ACTION_MOVE
				&& draggedNodeParent != null) {
			((DefaultTreeModel) tree.getModel())
					.nodeStructureChanged(draggedNodeParent);
		}
	}

	/*
	 * The following methods define how cursor image is changed depending on
	 * where and how a node is being dragged
	 */
	public final void dragEnter(DragSourceDragEvent dsde) {
		int action = dsde.getDropAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		} else {
			if (action == DnDConstants.ACTION_MOVE) {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveDrop);
			} else {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveNoDrop);
			}
		}
	}

	public final void dragOver(DragSourceDragEvent dsde) {
		int action = dsde.getDropAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		} else {
			if (action == DnDConstants.ACTION_MOVE) {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveDrop);
			} else {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveNoDrop);
			}
		}
	}

	public final void dropActionChanged(DragSourceDragEvent dsde) {
		int action = dsde.getDropAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		} else {
			if (action == DnDConstants.ACTION_MOVE) {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveDrop);
			} else {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveNoDrop);
			}
		}
	}

	public final void dragExit(DragSourceEvent dse) {
		dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
	}

	/**
	 * Method for the DragGestureListener that enables the ghost image while
	 * dragging
	 * 
	 * @param - DragGesture Event
	 * */
	public final void dragGestureRecognized(DragGestureEvent dge) {
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			draggedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			draggedNodeParent = (DefaultMutableTreeNode) draggedNode
					.getParent();
			if (drawImage) {
				// Get path bounds of selection path
				Rectangle pathBounds = tree.getPathBounds(path);
				// Returning the label
				JComponent lbl = (JComponent) tree.getCellRenderer()
						.getTreeCellRendererComponent(
								tree,
								draggedNode,
								false,
								tree.isExpanded(path),
								((DefaultTreeModel) tree.getModel())
										.isLeaf(path.getLastPathComponent()),
								0, false);
				lbl.setBounds(pathBounds);
				// Buffered image reference passing the label's ht and width
				image = new BufferedImage(lbl.getWidth(), lbl.getHeight(),
						java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);
				// Create the graphics for dragging image
				Graphics2D graphics = image.createGraphics();
				// Sets the Composite for the Graphics2D context
				graphics.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.5f));
				lbl.setOpaque(false);
				lbl.paint(graphics);
				graphics.dispose();
			}
			dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop, image,
					new Point(0, 0), new TransferableNode(draggedNode), this);
		}
	}

	/*
	 * Methods for DropTargetListener that check whether drag and drop
	 * operations are valid before executing or rejecting them. Validity is
	 * checked by the abstract canPerformAction method. What occurs when a drop
	 * is executed is determined by the abstract canExecuteDropMethod
	 */
	public final void dragEnter(DropTargetDragEvent dtde) {
		Point pt = dtde.getLocation();
		int action = dtde.getDropAction();
		if (drawImage) {
			paintImage(pt);
		}
		if (canPerformAction(tree, draggedNode, action, pt)) {
			dtde.acceptDrag(action);
		} else {
			dtde.rejectDrag();
		}
	}

	public final void dragExit(DropTargetEvent dte) {
		if (drawImage) {
			clearImage();
		}
	}

	public final void dragOver(DropTargetDragEvent dtde) {
		Point pt = dtde.getLocation();
		int action = dtde.getDropAction();
		tree.autoscroll(pt);
		if (drawImage) {
			paintImage(pt);
		}
		if (canPerformAction(tree, draggedNode, action, pt)) {
			dtde.acceptDrag(action);
		} else {
			dtde.rejectDrag();
		}
	}

	public final void dropActionChanged(DropTargetDragEvent dtde) {
		Point pt = dtde.getLocation();
		int action = dtde.getDropAction();
		if (drawImage) {
			paintImage(pt);
		}
		if (canPerformAction(tree, draggedNode, action, pt)) {
			dtde.acceptDrag(action);
		} else {
			dtde.rejectDrag();
		}
	}

	public final void drop(DropTargetDropEvent dtde) {
		try {
			if (drawImage) {
				clearImage();
			}
			int action = dtde.getDropAction();
			Transferable transferable = dtde.getTransferable();
			Point pt = dtde.getLocation();
			if (canPerformAction(tree, draggedNode, action, pt)) {
				TreePath pathTarget = tree.getPathForLocation(pt.x, pt.y);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) transferable
						.getTransferData(TransferableNode.NODE_FLAVOR);
				DefaultMutableTreeNode newParentNode = (DefaultMutableTreeNode) pathTarget
						.getLastPathComponent();
				if (executeDrop(tree, node, newParentNode, action)) {
					dtde.acceptDrop(action);
					dtde.dropComplete(true);
					return;
				}
			}
			dtde.rejectDrop();
			dtde.dropComplete(false);
		} catch (Exception e) {
			System.out.println(e);
			dtde.rejectDrop();
			dtde.dropComplete(false);
		}
	}

	private final void paintImage(Point pt) {
		tree.paintImmediately(rect2D.getBounds());
		rect2D.setRect((int) pt.getX(), (int) pt.getY(), image.getWidth(),
				image.getHeight());
		tree.getGraphics().drawImage(image, (int) pt.getX(), (int) pt.getY(),
				tree);
	}

	private final void clearImage() {
		tree.paintImmediately(rect2D.getBounds());
	}

	/**
	 * Determines if drag and drop operation is valid
	 * 
	 * @param target
	 *            - the Jtree
	 * @param draggedNode
	 *            - the node being dragged
	 * @param action
	 *            - the type of action
	 * @param location
	 *            - the point where it is dropped
	 * @return - true if action is possible else false
	 */
	public abstract boolean canPerformAction(DNDTree target,
			DefaultMutableTreeNode draggedNode, int action, Point location);

	/**
	 * Defines what happens when a drop is successfully executed
	 * 
	 * @param tree
	 *            - the Jtree
	 * @param draggedNode
	 *            - the node being dragged
	 * @param newParentNode
	 *            - the node that is being dropped on
	 * @param action
	 *            - the type of action
	 * @return - true if drop can be executed else false
	 */
	public abstract boolean executeDrop(DNDTree tree,
			DefaultMutableTreeNode draggedNode,
			DefaultMutableTreeNode newParentNode, int action);
}
