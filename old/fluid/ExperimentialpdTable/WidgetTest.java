package fluid.ExperimentialpdTable;

import javax.swing.JFrame;

public class WidgetTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("SimpleTableDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String[] columnNames = {"First Name",
				"Last Name",
				"Sport",
				"# of Years",
		"Vegetarian"};

		Object[][] data = {
				{"Kathy", "Smith",
					"Snowboarding", Integer.valueOf(5), false},
					{"John", "Doe",
						"Rowing", Integer.valueOf(3), true},
						{"Sue", "Black",
							"Knitting", Integer.valueOf(2), false},
							{"Jane", "White",
								"Speed reading", Integer.valueOf(20), true},
								{"Joe", "Brown",
									"Pool", Integer.valueOf(10), false}
		};



		//Create and set up the content pane.
		PDMJTableWidget newContentPane = new PDMJTableWidget(data, columnNames);
		
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}


}
