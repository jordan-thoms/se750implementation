package texteditor;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import texteditor.dal.PDHistory;
import texteditor.dal.PDOperation;
//import texteditor.dal.PDSimpleSpatialInfo;
import pdstore.GUID;
import pdstore.dal.PDWorkingCopy;

public class HistoryPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JList list;

	//public JComboBox SelectBox;
	private static final String upString = "Move up";
	private static final String downString = "Move down";
	private TextEditor editor;
	private PDWorkingCopy store = null;
	private PDHistory history;

	private JButton upButton;
	private JButton downButton;
	private JButton deleteButton;

	public HistoryPanel(TextEditor ne, PDWorkingCopy workingCopy, GUID id) {
		super(new BorderLayout());
		editor = ne;
		store = workingCopy;
		history = PDHistory.load(store, id);
	//	SelectBox = editor.SelectBox;
		this.setMinimumSize(new Dimension(this.getPreferredSize().width, 100));
		list = editor.textPanel.list;
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(list);
		ImageIcon icon = createImageIcon("up");
		if (icon != null) {
			upButton = new JButton(icon);
			upButton.setSize(5, 5);
			upButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			upButton = new JButton("UP");
		}
		upButton
				.setToolTipText("Move the currently selected list item higher.");
		upButton.setActionCommand(upString);
		upButton.addActionListener(new UpDownListener());

		icon = createImageIcon("down");
		if (icon != null) {
			downButton = new JButton(icon);
			downButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			downButton = new JButton("Down");
		}
		downButton
				.setToolTipText("Move the currently selected list item lower.");
		downButton.setActionCommand(downString);
		downButton.addActionListener(new UpDownListener());

		icon = createImageIcon("delete");
		if (icon != null) {
			deleteButton = new JButton(icon);
			deleteButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			deleteButton = new JButton("Delete");
		}
		deleteButton.setToolTipText("Delete seleted operation.");
		deleteButton.setActionCommand("Delete");
		deleteButton.addActionListener(new DeleteActionListener());

		JPanel upDownPanel = new JPanel(new GridLayout(1, 3));
		upDownPanel.add(upButton);
		upDownPanel.add(downButton);
		upDownPanel.add(deleteButton);

		JPanel buttonPane = new JPanel();
		buttonPane.add(upDownPanel);

		add(buttonPane, BorderLayout.NORTH);
		add(listScrollPane, BorderLayout.CENTER);
		//add(SelectBox, BorderLayout.SOUTH);
	}

	class UpDownListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equals(upString)) {
				// UP ARROW BUTTON
				PDOperation curOperation = (PDOperation) list
						.getSelectedValue();
				PDOperation preOperation;
				PDOperation prePreOperation;
				int selectedIndex = list.getSelectedIndex();

				// selectedIndex is 0 which means the operation is the first
				// operation
				if (selectedIndex == 0) {
					String message = "Top operation! Can not move up again!";
					generateMsg(message);
				}

				// selectedIndex is 1 which means the operation is the second
				// operation
				if (selectedIndex == 1) {
					preOperation = (PDOperation) list.getModel()
							.getElementAt(0);

					// implement swap semantics
					// invalid swap
					if (checkUpValidOperation(curOperation, preOperation)) {
						String message = "Cannot do operations before shape is created!";
						generateMsg(message);
					} else if (checkUpValidCopy(curOperation, preOperation)) {
						String message = "Cannot Copy before create!";
						generateMsg(message);
					}

					// valid swap, can create corresponding PDOperation
					else {
						curOperation
								.setTimeStamp(preOperation.getTimeStamp() - 100);
						PDOperation p = new PDOperation(store);
						p.setCommand("swap");
						Date d = new Date();
						p.setTimeStamp(d.getTime());
						//PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(store);
						//pds.setShapeID("swapshape");
						//p.setSuperParameter(pds);
						history.addOperation(p);
						store.commit();
					}

				}

				// other operations
				if (selectedIndex > 1) {
					preOperation = (PDOperation) list.getModel().getElementAt(
							selectedIndex - 1);
					prePreOperation = (PDOperation) list.getModel()
							.getElementAt(selectedIndex - 2);

					// invalid swap
					if (checkUpValidOperation(curOperation, preOperation)) {
						if (preOperation.getCommand().equals("New")) {
							String message = "Cannot do operations before shape is created!";
							generateMsg(message);
						} else {
							curOperation.setTimeStamp((preOperation
									.getTimeStamp() + prePreOperation
									.getTimeStamp()) / 2);
						//	PDSimpleSpatialInfo curInfo = (PDSimpleSpatialInfo) curOperation
						//			.getSuperParameter();
						//	PDSimpleSpatialInfo preInfo = (PDSimpleSpatialInfo) preOperation
						//			.getSuperParameter();
						//	curInfo.setShapeID(preInfo.getTargetID());

							PDOperation p = new PDOperation(store);
							p.setCommand("swap");
							Date d = new Date();
							p.setTimeStamp(d.getTime());
						//	PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(
						//			store);
						//	//pds.setShapeID("swapshape");
						//	p.setSuperParameter(pds);
							history.addOperation(p);
							store.commit();
						}

					} else if (checkUpValidCopy(curOperation, preOperation)) {
						String message = "Cannot Copy before create!";
						generateMsg(message);
					}

					// valid swap
					else {
						curOperation
								.setTimeStamp((preOperation.getTimeStamp() + prePreOperation
										.getTimeStamp()) / 2);
						//PDOperation p = new PDOperation(store);
						//p.setCommand("swap");
						//Date d = new Date();
					//	p.setTimeStamp(d.getTime());
					//	PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(store);
					//	pds.setShapeID("swapshape");
					//	p.setSuperParameter(pds);
					//	history.addOperation(p);
						store.commit();
					}

				}

				/**************** DOWN ARROW BUTTON *******************************************/
			} else {
				// DOWN ARROW BUTTON
				PDOperation curOperation = (PDOperation) list
						.getSelectedValue();
				PDOperation postOperation;
				PDOperation postPostOperation;
				int listSize = list.getModel().getSize();
				int selectedIndex = list.getSelectedIndex();

				if (selectedIndex == listSize - 1) {
					String message = "Bottom operation! Can not move down again!";
					generateMsg(message);
				}

				if (curOperation != null && selectedIndex < listSize - 1) {
					postOperation = (PDOperation) list.getModel().getElementAt(
							selectedIndex + 1);
					if (checkDownValidOperation(curOperation, postOperation)) {

						String message = "Cannot do operations before shape is created!";
						generateMsg(message);

					} else if (checkDownValidCopy(curOperation, postOperation)) {
						String message = "Cannot Copy before create!";
						generateMsg(message);
					} else {
						if (selectedIndex == listSize - 2) {
							curOperation.setTimeStamp(postOperation
									.getTimeStamp() + 100);
						}

						else {
							postPostOperation = (PDOperation) list.getModel()
									.getElementAt(selectedIndex + 2);
							curOperation.setTimeStamp((postOperation
									.getTimeStamp() + postPostOperation
									.getTimeStamp()) / 2);
						}
					//	PDSimpleSpatialInfo curInfo = (PDSimpleSpatialInfo) curOperation
					//			.getSuperParameter();
					//	PDSimpleSpatialInfo postInfo = (PDSimpleSpatialInfo) postOperation
						//		.getSuperParameter();
					//	if (curOperation.getCommand().equals("Copy")) {
					//		postInfo.setShapeID(curInfo.getTargetID());
					//	} else {
							// do nothing
					//	}
					//	PDOperation p = new PDOperation(store);
					//	p.setCommand("swap");
					//	Date d = new Date();
					//	p.setTimeStamp(d.getTime());
					//	PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(store);
					//	pds.setShapeID("swapshape");
					//	p.setSuperParameter(pds);
					//	history.addOperation(p);
						store.commit();
					}

				}

			}
		}
	}

	class DeleteActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			PDOperation selectedOperation = (PDOperation) list
					.getSelectedValue();
			if (selectedOperation != null) {
				history.removeOperation(selectedOperation);
				//PDOperation p = new PDOperation(store);
				//PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(store);
				//p.setCommand("Delete");
				//Date d = new Date();
				//p.setTimeStamp(d.getTime());
				/*//if (selectedOperation.getCommand().equals("Copy")
				//		|| selectedOperation.getCommand().equals("New")) {
				//	pds.setShapeID(((PDSimpleSpatialInfo) selectedOperation
							.getSuperParameter()).getShapeID());
				} else {
					pds.setShapeID("NotDeleteShape");
				}*/
				//p.setSuperParameter(pds);
				//history.addOperation(p);
				store.commit();

			}

			else {
				String message = "Please select one operation to delete";
				generateMsg(message);
			}
		}
	}

	private boolean checkUpValidOperation(PDOperation curOperation,
			PDOperation preOperation) {
		//TODO: empty method, code commented out : revise or remove.
				return false;
		//boolean returnValue;
	
		//PDSimpleSpatialInfo preSuperPara = (PDSimpleSpatialInfo) preOperation
		//		.getSuperParameter();
		//PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) curOperation
		//		.getSuperParameter();
	/*	if (preOperation.getCommand() == "New"
				&& preSuperPara.getShapeID().equals(curSuperPara.getShapeID())) {
			returnValue = true;

		} else if (preOperation.getCommand() == "Copy"
				&& preSuperPara.getShapeID().equals(curSuperPara.getShapeID())) {
			returnValue = true;
		} else {
			returnValue = false;
		}*/
		//return returnValue;
	}

	private boolean checkUpValidCopy(PDOperation curOperation,
			PDOperation preOperation) {
		//TODO: empty method, code commented out : revise or remove.
				return false;
	//	boolean returnValue;
	/*	PDSimpleSpatialInfo preSuperPara = (PDSimpleSpatialInfo) preOperation
				.getSuperParameter();
		PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) curOperation
				.getSuperParameter();
		if (preOperation.getCommand() == "New"
				&& preSuperPara.getShapeID().equals(curSuperPara.getTargetID())) {
			returnValue = true;

		} else if (preOperation.getCommand() == "Copy"
				&& preSuperPara.getShapeID().equals(curSuperPara.getTargetID())) {
			returnValue = true;
		} else {
			returnValue = false;
		}
		return returnValue;*/
	}

	private boolean checkDownValidOperation(PDOperation curOperation,
			PDOperation postOperation) {
		//TODO: empty method, code commented out : revise or remove.
				return false;
		//boolean returnValue = false;
	/*	PDSimpleSpatialInfo postSuperPara = (PDSimpleSpatialInfo) postOperation
				.getSuperParameter();
		PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) curOperation
				.getSuperParameter();
		if (curOperation.getCommand().equals("New")
				&& curSuperPara.getShapeID().equals(postSuperPara.getShapeID())) {
			returnValue = true;
		}

		// else if (curOperation.getCommand().equals("Copy")
		// && curSuperPara.getShapeID().equals(postSuperPara.getShapeID())) {
		// returnValue = true;
		// }

		else {
			returnValue = false;
		}
		return returnValue;*/
	}

	private boolean checkDownValidCopy(PDOperation curOperation,
			PDOperation postOperation) {
		//TODO: empty method, code commented out : revise or remove.
				return false;
/*
		boolean returnValue;
		PDSimpleSpatialInfo postSuperPara = (PDSimpleSpatialInfo) postOperation
				.getSuperParameter();
		PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) curOperation
				.getSuperParameter();
		if (curOperation.getCommand().equals("Copy")
				&& curSuperPara.getShapeID()
						.equals(postSuperPara.getTargetID())) {
			returnValue = true;
		}

		else if (curOperation.getCommand().equals("New")
				&& curSuperPara.getShapeID()
						.equals(postSuperPara.getTargetID())) {
			returnValue = true;
		}

		else {
			returnValue = false;
		}

		return returnValue;*/
	}

	@SuppressWarnings("deprecation")
	private void generateMsg(String msg) {
		String message = msg;
		JOptionPane pane = new JOptionPane(message);
		JDialog dialog = pane.createDialog(new JFrame(), "Warning");
		dialog.show();
	}

	protected static ImageIcon createImageIcon(String imageName) {
		String imgLocation = imageName + ".jpg";
		java.net.URL imageURL = HistoryPanel.class.getResource(imgLocation);

		if (imageURL == null) {
			System.err.println("Resource not found: " + imgLocation);
			return null;
		} else {
			return new ImageIcon(imageURL);
		}
	}

}
