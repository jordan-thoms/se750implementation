package messenger;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import nz.ac.auckland.se.genoupe.tools.Debug;

import com.sun.tools.javac.api.JavacScope;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.ui.treeview.PDTreeView;

public class ExampleMessengerTreeViewer {
	/* Stephen Hood 05/04/12 - class "ExampleTreeViewer" in the package "book" has been
	 * copied and modified to create this tree viewer for the class Messenger.
	 * Messenger must be run before this viewer for its data to be visible within it,
	 * and new messages are currently added to conversations above previous ones. */
	
	public static final String EXAMPLE_MESSENGER_TREE_VIEWER = "ExampleMessengerTreeViewer";

	public static void main(String[] args) {
		Debug.addDebugTopic(EXAMPLE_MESSENGER_TREE_VIEWER);
		PDStore store = new PDStore("MyConversationDatabase");
		
		GUID trans = store.begin();
        Collection<Object> lib = store.getAllInstancesOfType(trans, Messenger.conversationLibraryType);
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
