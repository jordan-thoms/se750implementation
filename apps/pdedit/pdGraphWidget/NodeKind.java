package pdedit.pdGraphWidget;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextListener;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.omg.CORBA.PUBLIC_MEMBER;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.RoundRectangle;
import pdedit.pdShapes.ShapeInterface;
import pdedit.pdShapes.Triangle;

import pdstore.GUID;
import pdstore.PDStore;


public class NodeKind implements ElementPropertyInterface{
	
	private final String name = "Node Type";
	private int kind = 0;
	private int kind_shape = 0;
	private int kind_Color = 0;
	private JComboBox box;
	private JComboBox boxShape ;
	private JComboBox boxColor;
	
	private final Color defaultBorderColor = Color.white; 
	private Color borderColor = null; 
	
	private final Color defaultColor = Color.white; 
	private Color color = null; 
	
	private Color highlighted = new Color(127,206,255);
	
	private HashMap<String, Integer> kindToIndexMap = new HashMap<String, Integer>();
	private HashMap<Integer, String> IndexMap = new HashMap<Integer, String>();
	private HashMap<Integer, Color> kindToColor = new HashMap<Integer, Color>();
	private HashMap<Integer, ShapeInterface> kindToShape = new HashMap<Integer, ShapeInterface>();
	private HashMap<Integer, GUID> kindToGUID = new HashMap<Integer, GUID>();
	private HashMap<String, Integer> kindToIndexShape = new HashMap<String, Integer>();
	private HashMap<String, Integer> kindToIndexColor = new HashMap<String, Integer>();
	
	private ShapeInterface shape;
	private DiagramElement element;
	private boolean editing = true;
	
	public JTextArea description_field;
	private String desc_String;
	
	
	private ArrayList<DiagramEventListener> listener = new ArrayList<DiagramEventListener>();
	
	
	public NodeKind(final DiagramElement element){
		setUp();
		this.element = element;

		description_field = new JTextArea();
		description_field.setColumns(20);
		description_field.setRows(10);	
		
		//JComboBox settings for shapes
		boxShape = new JComboBox();
		Object[] values_Shapes = kindToIndexShape.keySet().toArray();
		Arrays.sort(values_Shapes, new Comparator<Object>(){
			public int compare(Object arg0, Object arg1) {
				return ((String)arg0).compareToIgnoreCase((String)arg1);
			}
		});
		for (Object value: values_Shapes) {
				String text = (String)value;
				
				boxShape.addItem(text);
		}
		boxShape.setSelectedItem(null);
		boxShape.setEditable(true);
		
		boxShape.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String selected = (String)cb.getSelectedItem();
				System.out.println(selected);
				kind_shape = kindToIndexShape.get(selected);
				System.out.println(kind_shape);
				String name = null;
				String description = null;
				updateThis(name,description);
			}
		});
		
		//JComboBox settings for colors
		boxColor = new JComboBox();
		Object[] values_Color = kindToIndexColor.keySet().toArray();
		Arrays.sort(values_Color, new Comparator<Object>(){
			public int compare(Object arg0, Object arg1) {
				return ((String)arg0).compareToIgnoreCase((String)arg1);
			}
		});
		for (Object value: values_Color) {
				String text = (String)value;
				
				boxColor.addItem(text);
		}
		boxColor.setSelectedItem(null);
		boxColor.setEditable(true);
		
		boxColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String selected = (String)cb.getSelectedItem();
				System.out.println(selected);
				kind_Color = kindToIndexColor.get(selected);
				System.out.println(kind_Color);
				String name = null;
				String description = null;
				updateThis(name,description);
			}
		});
		
		box = new JComboBox();
		Object[] values = kindToIndexMap.keySet().toArray();
		Arrays.sort(values, new Comparator<Object>(){
			public int compare(Object arg0, Object arg1) {
				return ((String)arg0).compareToIgnoreCase((String)arg1);
			}
		});
		
		for (Object value: values) {
			if (value != "PDStore.ComplexType"){
				String text = (String)value;	
				box.addItem(text.substring(8));
			}
		}
		box.setSelectedItem(null);
		box.setEditable(true);
		
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String selected = "PDStore." + (String)cb.getSelectedItem();
				String name = null;
				String description = null;
				if (kindToIndexMap.get(selected) != null){
					kind = kindToIndexMap.get(selected);
					editing = false;
				}else{
					kind = 0;
				}
				updateThis(name,description);
			}
		});
		box.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				JTextField f = (JTextField)e.getSource();
				if (!editing){
					String t = f.getText().substring(f.getText().length()-1).toUpperCase();
					String s = f.getText().substring(f.getText().length()-1).toUpperCase();
					f.setText(t);
					f.setText(s);
					editing = true;
					//kind = 0;
					updateThis(t,s);
					return;
				}
				String selected = f.getText();
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					if (selected.contains("<") || selected.contains(">"))
						return;
					element.setName(name);
				}
				String name = null;
				if(selected.length()>1){
					name = selected.substring(0,1).toUpperCase() + selected.substring(1);
				}else{
					name = selected.toUpperCase();
				}
				element.setName(name);
				for(DiagramEventListener l : listener){
					l.nodeChanged(new DiagramEvent(element));
				}
			}

		});
		shape = kindToShape.get(kind_shape);
	}
	
	
	public void addListener(DiagramEventListener l){
		listener.add(l);
	}
	
	public void updateProperty(String type){
		kind = kindToIndexMap.get(type); 
		updateThis(null,null);
	}
	
	private void updateThis(String name, String description){
		System.out.println(kind_shape);
		shape = kindToShape.get(kind_shape);
		shape.setColour(kindToColor.get(kind_Color));
		element.setShape(shape);
		for(DiagramEventListener l : listener){
			if (element.getElementType() == ElementType.Node){
				if (kind != 0){
					element.setName(IndexMap.get(kind));
				}else if (name != null){
					element.setName(name);
				}
				l.nodeChanged(new DiagramEvent(element));
			}else if (element.getElementType() == ElementType.Link){
				l.linkChanged(new DiagramEvent(element));
			}
			element.setDescription(description_field.getText());
		}
	}
	
	private void setUp(){
		kindToIndexMap.put("PDStore.ComplexType", 0);
		kindToIndexMap.put("PDStore.Blob", 1);
		kindToIndexMap.put("PDStore.Image", 2);
		kindToIndexMap.put("PDStore.String", 3);
		kindToIndexMap.put("PDStore.Char", 4);
		kindToIndexMap.put("PDStore.Boolean", 5);
		kindToIndexMap.put("PDStore.Double", 6);
		kindToIndexMap.put("PDStore.Integer", 7);
		kindToIndexMap.put("PDStore.Time", 8);
		kindToIndexMap.put("PDStore.GUID", 9);
		kindToIndexMap.put("PDStore.ObjectType", 10);
		
		IndexMap.put(0,"PDStore.ComplexType");
		IndexMap.put(1,"PDStore.Blob");
		IndexMap.put(2,"PDStore.Image");
		IndexMap.put(3,"PDStore.String");
		IndexMap.put(4,"PDStore.Char");
		IndexMap.put(5,"PDStore.Boolean");
		IndexMap.put(6,"PDStore.Double");
		IndexMap.put(7,"PDStore.Integer");
		IndexMap.put(8,"PDStore.Time");
		IndexMap.put(9,"PDStore.GUID");
		IndexMap.put(10,"PDStore.ObjectType");
		
		kindToGUID.put(0,PDStore.GUID_TYPEID);
		kindToGUID.put(1,PDStore.BLOB_TYPEID);
		kindToGUID.put(2,PDStore.IMAGE_TYPEID);
		kindToGUID.put(3,PDStore.STRING_TYPEID);
		kindToGUID.put(4,PDStore.CHAR_TYPEID);
		kindToGUID.put(5,PDStore.BOOLEAN_TYPEID);
		kindToGUID.put(6,PDStore.DOUBLE_PRECISION_TYPEID);
		kindToGUID.put(7,PDStore.INTEGER_TYPEID);
		kindToGUID.put(8,PDStore.TIMESTAMP_TYPEID);
		kindToGUID.put(9,PDStore.GUID_TYPEID);
		kindToGUID.put(10,PDStore.OBJECT_TYPEID);
		
		kindToIndexColor.put("Orange", 0);
		kindToIndexColor.put("Red", 1);
		kindToIndexColor.put("Blue", 2);
		kindToIndexColor.put("Green", 3);
		kindToIndexColor.put("Magenta", 4);
		kindToIndexColor.put("Pink", 5);
		kindToIndexColor.put("Light Gray", 6);
		kindToIndexColor.put("Gray", 7);
		kindToIndexColor.put("Cyan", 8);
		kindToIndexColor.put("Dark Gray", 9);
		kindToIndexColor.put("Black", 10);
		
		kindToColor.put(0,Color.ORANGE);
		kindToColor.put(1,Color.RED);
		kindToColor.put(2,Color.BLUE);
		kindToColor.put(3,Color.GREEN);
		kindToColor.put(4,Color.MAGENTA);
		kindToColor.put(5,Color.PINK);
		kindToColor.put(6,Color.lightGray);
		kindToColor.put(7,Color.GRAY);
		kindToColor.put(8,Color.CYAN);
		kindToColor.put(9,Color.DARK_GRAY);
		kindToColor.put(10,Color.BLACK);
		
		kindToIndexShape.put("Circle", 0);
		kindToIndexShape.put("Round Rectangle",1);
		//kindToIndexShape.put("Rectangle", 2);
		kindToIndexShape.put("Triangle", 3);
		
		kindToShape.put(0,new Circle());
		kindToShape.put(1,new RoundRectangle());
		//kindToShape.put(2,new Rectangle());
		kindToShape.put(3,new Triangle());
		//kindToShape.put(4,new Ellipse());
		//kindToShape.put(5,new Circle());
		//kindToShape.put(6,new Circle());
		//kindToShape.put(7,new Triangle());
		//kindToShape.put(8,new RoundRectangle());
		//kindToShape.put(9,new RoundRectangle());
		//kindToShape.put(10,new Circle());
		
		
	}

	public Color borderColor() {
		if (borderColor == null){
			return defaultBorderColor;
		}
		return borderColor;
	}

	public void borderColor(Color color) {
		borderColor = color;
	}

	public Color color() {
		if (color == null){
			return defaultColor;
		}
		return color;
	}

	public void color(Color color) {
		this.color = color;
	}

	public JComponent getDisplay() {
		return box;
	}
	public JComponent getShapeBox(){
		return boxShape;
	}
	public JComponent getColorBox(){
		return boxColor;
	}
	public JComponent getDesc(){
		return description_field;
	}

	public String getName() {
		return name;
	}

	public ShapeInterface getShape() {
		return shape;
	}

	public Color highlightColor() {
		return highlighted;
	}

	public void highlightColor(Color color) {
		highlighted = color;
	}
	
	public String toString(){
		String ret = IndexMap.get(kind);
		if (ret.startsWith("PDStore.")) {
			ret = ret.substring(8);
		}
		
		return name+": "+ ret;
	}

	public HashMap<Object,Object> getInfo() {
		HashMap<Object, Object> info = new HashMap<Object, Object>();
		info.put("Type", IndexMap.get(kind));
		info.put("GUID", kindToGUID.get(kind));
		return info;
	}

	@Override
	public String getDescription() {
		return description_field.getText();
	}

	public String getProperty() {
		return IndexMap.get(this.kind);
	}

}
	