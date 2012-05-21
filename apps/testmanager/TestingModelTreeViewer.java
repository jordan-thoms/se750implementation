package testmanager;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import nz.ac.auckland.se.genoupe.tools.Debug;

import book.BookExample;

import com.sun.tools.javac.api.JavacScope;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.ui.treeview.PDTreeView;
import testmanager.dal.PDTestRepository;

public class TestingModelTreeViewer {
	
	public static void main(String[] args) {
		PDStore store = new PDStore("TestManager");
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
			
	
		GUID rootID = copy.getId("Rahul's test repo");
		PDTestRepository repo = PDTestRepository.load(copy, rootID);
		PDTreeView treeView = new PDTreeView(store, new Object[]{ repo } );
		JScrollPane scrollPane = new JScrollPane(treeView);
	
		/* this code would show all repos in the tree view
		GUID trans = store.begin();
        Collection<Object> lib = store.getAllInstancesOfType(trans, CreateTestingModel.TEST_REPO_TYPEID);
		Object[] array = lib.toArray();
		PDTreeView treeView = new PDTreeView(store, array);     
		JScrollPane scrollPane = new JScrollPane(treeView);
		*/
		
        JFrame frame = new JFrame("Testing Tree View");
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}

}
