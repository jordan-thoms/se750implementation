package book;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import nz.ac.auckland.se.genoupe.tools.Debug;

import com.sun.tools.javac.api.JavacScope;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.ui.treeview.PDTreeView;

public class ExampleTreeViewer {
	public static final String EXAMPLE_TREE_VIEWER = "ExampleTreeViewer";

	public static void main(String[] args) {
		Debug.addDebugTopic(EXAMPLE_TREE_VIEWER);
		PDStore store = new PDStore("MyBookDatabase");
		
		GUID trans = store.begin();
        Collection<Object> lib = store.getAllInstancesOfType(trans, BookExample.libraryType);
		Object[] array = lib.toArray();
		PDTreeView treeView = new PDTreeView(store, array);
        
		JScrollPane scrollPane = new JScrollPane(treeView);
		
        JFrame frame = new JFrame("Treeview");
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}

}
