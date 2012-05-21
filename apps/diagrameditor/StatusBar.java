package diagrameditor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * Class to set up the status bar for the diagram editor.
 */
public class StatusBar extends JPanel {
	private static final long serialVersionUID = 4688838996973487417L;

	private JLabel status;
	private JLabel shape;
	private JLabel xCoord;
	private JLabel yCoord;
	private JTextField spacer;
	
	/**
	 * Constructor
	 */
	public StatusBar() {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(new EtchedBorder());
		
		status = new JLabel();
		
		shape = new JLabel("");
		shape.setPreferredSize(new Dimension(50, 20));
		
		xCoord = new JLabel();
		xCoord.setPreferredSize(new Dimension(50, 20));
		yCoord = new JLabel();
		yCoord.setPreferredSize(new Dimension(50, 20));
		clearCoordinates();
		
		spacer = new JTextField();
		spacer.setEnabled(false);
		spacer.setBackground(getBackground());
		spacer.setBorder(BorderFactory.createEmptyBorder());
		
		add(Box.createHorizontalStrut(5));
		add(status);
		add(spacer);
		add(new JSeparator(SwingConstants.VERTICAL));
		add(Box.createHorizontalStrut(5));
		add(xCoord);
		add(Box.createHorizontalStrut(5));
		add(new JSeparator(SwingConstants.VERTICAL));
		add(Box.createHorizontalStrut(5));
		add(yCoord);
	}
	
	/**
	 * Method to set a status message
	 * @param msg, the message to set
	 */
	public void setStatus(String msg) {
		status.setForeground(Color.DARK_GRAY);
		status.setText(msg);
	}

	/**
	 * Method to set an error message in the status bar
	 * @param msg, the message to set
	 */
	public void setError(String msg) {
		status.setForeground(Color.RED);
		status.setText(msg);
	}
	
	/**
	 * Method to set coordinates in the status bar
	 * @param x, string of x coordinate
	 * @param y, string of y coordinate
	 */
	public void setCoordinates(String x, String y) {
		xCoord.setForeground(Color.DARK_GRAY);
		yCoord.setForeground(Color.DARK_GRAY);
		xCoord.setText("X : " + x);
		yCoord.setText("Y : " + y);
	}
	
	/**
	 * Method to clear the status bar of text
	 */
	public void clearStatus() {
		status.setText("");
	}
	
	/**
	 * Method to clear the status bar of coordinates
	 */
	public void clearCoordinates() {
		xCoord.setForeground(Color.GRAY);
		yCoord.setForeground(Color.GRAY);
		xCoord.setText("X : ");
		yCoord.setText("Y : ");
	}
}
