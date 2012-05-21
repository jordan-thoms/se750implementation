package fluid.dummy;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fluid.BioPDPortal;
import fluid.evaluation.TaskOne;

import pdedit.PDEdit;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;

public class Dummy {
	static Dummy runner;
	static PDEdit editor;
	static boolean test = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runner = new Dummy(false);
		runner.createDummy("CreationTest");
	}
	
	public Dummy(boolean type){
		test = type;
	}

	public JFrame continueWindow(){
		final JFrame frame = new JFrame("Dummy");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JPanel panel = new JPanel();
		final JButton done = new JButton("Done");
		done.setEnabled(false);
		done.setPreferredSize(new Dimension(200,50));
		done.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.closePDModelWindow();
				frame.dispose();
				TaskOne.stopAll();
			}
		});
		JButton button = new JButton("Continue");
		button.setPreferredSize(new Dimension(200,50));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JButton thisButton = (JButton)arg0.getSource();
				thisButton.setEnabled(false);
				editor.closePDModelWindow();
				inputData();
				testGetData();
				System.out.println("--------------DONE------------------");
				if (!test){
					frame.dispose();
				}else{
					done.setEnabled(true);
				}
			}
		});
		JButton close = new JButton("Close");
		close.setPreferredSize(new Dimension(200,50));
		close.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.closePDModelWindow();
				frame.dispose();
				System.exit(0);
			}
		});
		
		
		
		JButton other = close;
		System.out.println(test);
		if (test){
			other = done;
		}
		panel.add(button);
		panel.add(other);
		frame.getContentPane().add(panel);
		frame.pack();
		return frame;
	}

	public void createDummy(String name){
		//fileClean(name);
		editor = new PDEdit(new BioPDPortal(name));
		editor.setEnablePDEdit(false);
		continueWindow().setVisible(true);
		editor.showPDModelWindow();
	}


	private void inputData(){
		openfile();
		PDStore s = editor.getStore();
		GUID t = s.begin();
		GUID p = null;
		GUID hn = null;
		GUID n = null;
		GUID fn = null;
		GUID ln = null;
		int count = 0;
		
		while ((p==null || hn == null || n == null || fn == null|| ln == null) && count < 10){
			
			p = getGUID(s, DummyPersonData.person);
			System.out.println("p: "+p);
			hn =  getGUID(s, DummyPersonData.hasName);
			System.out.println("hn: "+hn);
			n =  getGUID(s, DummyPersonData.name);
			System.out.println("n: "+n);
			fn =  getGUID(s, DummyPersonData.FirstName);
			System.out.println("fn: "+fn);
			ln =  getGUID(s, DummyPersonData.LastName);
			System.out.println("ln: "+ln);
		}
		DummyPersonData.GUIDMap.put(DummyPersonData.person, p);
		DummyPersonData.GUIDMap.put(DummyPersonData.hasName,hn);
		DummyPersonData.GUIDMap.put(DummyPersonData.name,n);
		DummyPersonData.GUIDMap.put(DummyPersonData.FirstName,fn);
		DummyPersonData.GUIDMap.put(DummyPersonData.LastName,ln);
		s.commit(t);

		t = s.begin();
		count = 0;
		for (String[] person: DummyPersonData.data){
			GUID personInstance = new GUID();
			GUID nameInstance = new GUID();
			System.out.println("New Person Instance:"+personInstance.toString());
			s.addLink(
					t, 
					personInstance, 
					DummyPersonData.GUIDMap.get(DummyPersonData.hasName), 
					nameInstance
			);
			s.addLink(
					t, 
					nameInstance, 
					DummyPersonData.GUIDMap.get(DummyPersonData.FirstName), 
					person[0]
			);
			s.addLink(
					t, 
					nameInstance, 
					DummyPersonData.GUIDMap.get(DummyPersonData.LastName), 
					person[1]
			);
			count++;
		}
		System.out.println("Number of records added: "+count);
		s.commit(t);
	}
	
	private void openfile() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            System.out.println(file.getName());
        } else {
        }


	}

	private GUID getGUID(PDStore s, String name){
		GUID t = s.begin();
		GUID temp = s.getId(t,name);
		int count = 0;
		if (temp == null)
			System.out.print("> Searching for GUID ");
		while (temp == null && count < 25){
			try {
				s.commit(t);
				Thread.sleep(1000+count*200);
				t = s.begin();
				temp = s.getId(t, name);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(count+". ");
			count++;
		}
		System.out.println();
		if (temp == null){
			System.err.println("Get GUID failed");
		}
		if (count > 0)
			System.out.println("> Number of tries used to get "+name+": "+count);
		return temp;
	}


	private void testGetData(){
		System.out.println("Try to get Data Back!");
		PDStore s = editor.getStore();
		GUID t = s.begin();
		System.out.println("GUID of Person: "+DummyPersonData.GUIDMap.get(DummyPersonData.person));
		Collection<Object> list = s.getAllInstancesOfType(t, DummyPersonData.GUIDMap.get(DummyPersonData.person));
		System.out.println(list.size());
		for (Object o : list){
			GUID person = (GUID)o;
			GUID name = (GUID)s.getInstance(t, person, DummyPersonData.GUIDMap.get(DummyPersonData.hasName));
			String first = (String)s.getInstance(t, name, DummyPersonData.GUIDMap.get(DummyPersonData.FirstName));
			String last = (String)s.getInstance(t, name, DummyPersonData.GUIDMap.get(DummyPersonData.LastName));
			System.out.println(first +" "+last);
		}
		
		s.commit(t);
		System.out.println("Doom");
	}


	private void fileClean(String name) {
		name = "pddata/"+name+".pds";
		File file = new File(name);
		if (file.exists()){
			int count = 0;
			while (file.exists()&& count < 10){
				boolean b = file.delete();
				if (b)
					System.out.println("Delete Successful");
				else
					System.out.println("Delete Not Successful");
				try {
					Thread.sleep(1000+count*200);
				} catch (InterruptedException e) {
				}
				count++;
			}
		}else{
			System.out.println(name + " does not exist");
		}
	}

}
