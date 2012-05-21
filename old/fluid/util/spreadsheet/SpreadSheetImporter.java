package fluid.util.spreadsheet;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class SpreadSheetImporter {

	static PDSWorkbook wb;
	static PDSSheetPaser currentSheet;
	static JTextField field;
	static JTextField com;
	static TitledBorder currentlabel;
	static JComboBox numbers;
	static JLabel currentlabelCount;
	static JPanel content;
	static JPanel currentWorkbook;
	static JFrame frame;
	static ElementContainer results;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadNimbus();
		frame = new JFrame("Spreadsheet Importer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		content = new JPanel();
		BoxLayout experimentLayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(experimentLayout);
		content.add(workbookPanel());
		content.add(currentWorkbook());
		content.add(consolePanel());
		frame.getContentPane().add(content);
		frame.setJMenuBar(addMenuBar());
		frame.pack();
		frame.setVisible(true);
	}

	private static JMenuBar addMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("File");
		filemenu.setMnemonic('F');

		JMenuItem newProj = new JMenuItem("Open Workbook");
		newProj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OpenDialog();
			}
		});
		newProj.setMnemonic('O');
		newProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK, true));

		JMenuItem importMenu = new JMenuItem("Load Commands from File");
		importMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(null);
			}
		});
		importMenu.setMnemonic('L');
		importMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK, true));

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		exit.setMnemonic('X');

		filemenu.add(newProj);
		filemenu.add(importMenu);
		filemenu.add(exit);
		menubar.add(filemenu);

		return menubar;
	}

	private static JPanel consolePanel(){
		Date date = new Date(System.currentTimeMillis());
		String[] list = (date.toString().split(" "));
		String out = list[0]+" "+list[1]+" "+list[2]+" "+list[5];
		TitledBorder title;
		System.out.println(date.toString());
		title = BorderFactory.createTitledBorder("Console - "+out);

		JPanel panel = new JPanel();
		panel.setBorder(title);
		BoxLayout experimentLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(experimentLayout);
		final JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setColumns(40);
		area.setLineWrap(false);
		area.setRows(10);
		JScrollPane scroll = new JScrollPane(area);
		com = new JTextField();
		com.setEditable(false);
		com.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {


			}

			@SuppressWarnings("unchecked")
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					JTextField cc = (JTextField)e.getComponent();
					Date date = new Date(System.currentTimeMillis());
					String out = "["+(date.toString().split(" "))[3]+"]   ";
					if (results !=null)
						System.out.println("Size: "+results.getSize());
						
					results = CommandUtil.execute(new PDSSheetPaser(wb.getSheet((String)(numbers.getSelectedItem()))),
							cc.getText(), results);
					
					if (results !=null)
						System.out.println("Size: "+results.getSize());
					
					String commandBox = cc.getText();
					if (results == null){
						commandBox = "Error in executing command";
					}else{
						System.out.println();
						for (Object o : results){
							if (o instanceof Row){
								// number of cells does not take into account blanks
								// Therefore a new getNumberof cells was created
								for (int i = 0; i < results.getNumberOfCellsInItem(o); i ++){
									if (((Row)o).getCell(i) != null){
										System.out.print(((Row)o).getCell(i).toString()+" : ");
									}
								}
							}
							else{
								ArrayList<Cell> c = (ArrayList<Cell>)o;
								for (Cell cell : c ){
									if (cell != null){
										System.out.print(cell.toString()+" : ");
									}else{
										System.out.print(": -----Blank----- ");
									}
								}
							}
							System.out.println();
						}
						commandBox += " --- results loaded.";
					}

					if (area.getText().isEmpty()){
						out += commandBox;
					}else{
						out += commandBox+"\n"+area.getText();
					}
					area.setText(out);
					cc.setText("");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		com.setPreferredSize(new Dimension(250, 30));
		panel.add(com);
		panel.add(scroll);
		return panel;
	}
	private static JPanel workbookPanel() {
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Open Workbook");

		JPanel panel = new JPanel();
		panel.setBorder(title);
		JLabel label = new JLabel("Workbook: ");
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				OpenDialog();
			}
		});
		field = new JTextField();
		field.setPreferredSize(new Dimension(300, 30));
		panel.add(label);
		panel.add(field);
		panel.add(browse);
		return panel;
	}
	private static JPanel currentWorkbook() {
		currentlabel = BorderFactory.createTitledBorder("Current Workbook: ");
		currentWorkbook = new JPanel();
		currentWorkbook.setBorder(currentlabel);
		GridLayout experimentLayout = new GridLayout(1,3);
		currentWorkbook.setLayout(experimentLayout);
		currentlabelCount = new JLabel(" Total Number of Sheets: ");
		numbers = new JComboBox();
		JLabel select = new JLabel("Curent SpreadSheet:");
		select.setHorizontalAlignment(JLabel.RIGHT);
		select.setHorizontalTextPosition(JLabel.RIGHT);
		currentWorkbook.add(currentlabelCount);
		currentWorkbook.add(select);
		currentWorkbook.add(numbers);
		return currentWorkbook;
	}

	public static void OpenDialog(){
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		if (fc.getSelectedFile() != null){
			field.setText(fc.getSelectedFile().getAbsolutePath());
			currentlabel = BorderFactory.createTitledBorder("Current Workbook: "+fc.getSelectedFile().getName());
			currentWorkbook.setBorder(currentlabel);
			try {
				com.setEditable(true);
				numbers.removeAllItems();
				wb = new PDSWorkbook(fc.getSelectedFile().getAbsolutePath());
				currentlabelCount.setText(" Total Number of Sheets: "+wb.getNumberOfSheets());
				for (int i = 0; i < wb.getNumberOfSheets(); i ++){
					numbers.addItem(wb.getSheet(i).getSheetName());
				}
				content.invalidate();
				content.validate();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadNimbus(){
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
