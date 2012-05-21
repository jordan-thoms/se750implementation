package pdedit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ListWidget extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> stringList = null;
	private boolean isString;
	private int numberOfElement = 0;
	private JLabel selected = null;
	private ArrayList<DialogListener> listener = new ArrayList<DialogListener>();
	private Color abcHightlight = new Color(26, 45, 56);
	private boolean displayLabels = true;

	public ListWidget(ArrayList<String> list, boolean showAbcs) {
		super();
		this.displayLabels = showAbcs;
		isString = true;
		this.stringList = list;
		setup();
		this.setLayout(new GridLayout(numberOfElement, 1));
		this.setBackground(Color.white);
	}
	
	public void addDialogListener(DialogListener l){
		listener.add(l);
	}

	private void setup(){
		if (isString){
			panelPropagator(stringHandler());
		}
	}
	
	private void panelPropagator(ArrayList<JLabel> ls){
		for (JLabel lab : ls){
			lab.setPreferredSize(new Dimension(150,30));
			this.add(lab);
			numberOfElement++;
		}
	}
	
	public JLabel findClosestLabel(String name){
		String match;
		for (Component c : this.getComponents()){
			JLabel l = (JLabel)c;
			match = l.getText().trim();
			if (match.toLowerCase().startsWith(name.toLowerCase())
					&& match.length() >1){
				if (selected != null){
					unSelectElement(selected);
				}
				selected = l;
				selected.setForeground(Color.white);
				selected.setBackground(new Color(0,76,255));
				return l;
			}
		}
		return null;
	}

	private ArrayList<JLabel> stringHandler() {
		String [] names = stringArraylistToStringArray(stringList);
		Arrays.sort(names, new Comparator<String>(){
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
		});
		
		ArrayList <Character> abc = new ArrayList<Character>();
		HashMap<Character, ArrayList <String>> seta = new HashMap<Character, ArrayList <String>>();
		ArrayList<JLabel> labels = new ArrayList<JLabel>();

		for (String s : names){
			if (!abc.contains(s.toUpperCase().charAt(0))){
				abc.add(s.toUpperCase().charAt(0));
				seta.put(s.toUpperCase().charAt(0), new ArrayList<String>());
				seta.get(s.toUpperCase().charAt(0)).add(s);
			}else{
				seta.get(s.toUpperCase().charAt(0)).add(s);
			}
		}
		int count = 1;
		for (Character c : abc){
			if (this.displayLabels){
				labels.add(generateABCLabel(c));
			}
			
			for (String s : seta.get(c)){
				labels.add(generateLabel(s));
				count++;
			}
			count++;
		}
		return labels;

	}
	
	private String[] stringArraylistToStringArray(ArrayList<String> list){
		String [] temp = new String[list.size()];
		for (int i = 0; i < list.size(); i++){
			temp[i] = list.get(i).substring(0, list.get(i).indexOf("|"));
		}
		return temp;
	}

	private JLabel generateABCLabel(Character s) {
		JLabel l = new JLabel();
		l.setText(""+s);
		l.setHorizontalAlignment( JLabel.CENTER );
		l.setOpaque(true);
		l.setForeground(Color.white);
		l.setBackground(abcHightlight);
		return l;
	}
	
	private JLabel generateLabel(String s) {
		JLabel l = new JLabel();
		l.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				JLabel l = (JLabel)e.getComponent();
				if (selected != null && l != selected){
					unSelectElement(l);
				}else if (selected == null){
					unSelectElement(l);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if (selected != null){
					unSelectElement(selected);
				}
				selected = (JLabel) e.getComponent();
				for(DialogListener d : listener){
					d.selected(getSelectedTrimed());
				}
				selected.setForeground(Color.white);
				selected.setBackground(new Color(0,76,255));
			}
		
			public void mouseEntered(MouseEvent e) {
				JLabel l = (JLabel)e.getComponent();
				if (selected != null && l != selected){
					selectElement(l);
				}else if (selected == null){
					selectElement(l);
				}
				
			}
			
		});
		l.setText("\t"+s);
		l.setOpaque(true);
		l.setForeground(Color.black);
		l.setBackground(Color.white);
		return l;
	}
	
	private void unSelectElement(JLabel l) {
		l.setForeground(Color.black);
		l.setBackground(Color.white);
	}
	
	private void selectElement(JLabel l) {
		l.setForeground(Color.white);
		l.setBackground(new Color(0,123,255));
	}
	
	public String getSelected (){
		if (selected != null){
			return findFromSelected();
		}else{
			return null;
		}
		
	}
	
	public String getSelectedTrimed(){
		if (selected != null){
			return selected.getText().trim();
		}else{
			return null;
		}
		
	}
	
	private String findFromSelected(){
		for(String s : stringList){
			if (s.startsWith(selected.getText().trim())){
				return s;
			}
		}
		return null;
	}

}
