package pdedit.pdGraphWidget;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class NodeDescription {
	
	private JTextField desInput;
	private JPanel container = new JPanel();
	private JPanel inputPanel = new JPanel();
	
	
	private void description(){
		
		JLabel description = new JLabel("Description:");
		desInput = new JTextField("<Description of Model>");
		desInput.setPreferredSize(new Dimension(200,100));
		desInput.setSelectionStart(0);
		
		desInput.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
			JTextField t_des = (JTextField)e.getComponent();
			t_des.setSelectionStart(0);
			t_des.setSelectionEnd(t_des.getText().length());			
		    }
		});
		
		inputPanel.add(description);
		inputPanel.add(desInput);
		container.add(inputPanel);
		
	}
}
