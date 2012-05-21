package pdstore.ui.treeview;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class PDTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private Icon instanceIcon, linkIcon, primitiveIcon, leafIcon, recurseIcon, homeIcon;

    public PDTreeRenderer() {
    	instanceIcon = new ImageIcon("icons/nuvola_selected/ledblue.png");
        linkIcon = new ImageIcon("icons/nuvola_selected/folder_green.png");
        recurseIcon = new ImageIcon("icons/nuvola_selected/forward.png");
        primitiveIcon = new ImageIcon("icons/nuvola_selected/kdf.png");
        leafIcon = new ImageIcon("icons/nuvola_selected/ledgreen.png");
        homeIcon = new ImageIcon("icons/nuvola_selected/folder_home.png");
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                        boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (value instanceof ComplexNode) {
        	ComplexNode node = (ComplexNode)value;
        	setIcon(node.isLeaf() ? recurseIcon : instanceIcon);
    		setToolTipText(String.valueOf(node.getValue()));
        }
        else if (value instanceof PrimitiveRoleNode) {
        	setIcon(primitiveIcon);
        }
        else if (value instanceof ComplexRoleNode) {
        	ComplexRoleNode node = (ComplexRoleNode)value;
        	setIcon(linkIcon);
    		setToolTipText(String.valueOf(node.getRole()));
        }
        else if (value instanceof PDRootNode) {
        	setIcon(homeIcon);
        }
        else if (leaf) {
        	setIcon(leafIcon);
        }
        
        return this;
    }
}