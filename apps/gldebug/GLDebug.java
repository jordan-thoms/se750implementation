package gldebug;

import gldebug.DebuggerProtocol.DebuggerStatus;
import gldebug.DebuggerProtocol.TimeStamp;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;

import java.sql.Timestamp;
import java.text.DateFormat;

import java.util.List;
import java.util.Collection;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.regex.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import pdstore.*;
import pdstore.generic.PDChange;

public class GLDebug {
	public final static GUID glStateModel = new GUID("27c594c0521011e186b450e5495bfcc0");
	
	public final static GUID sessionType = new GUID("6868b8207ed611e1855d50e5495bfcc0");
	public final static GUID stateType = new GUID("27c5bbd0521011e186b450e5495bfcc0"); // Represents a state made up of a number of state variables, sits at the root of the state tree that doesn't actually hold any state data
	public final static GUID stateVariableType = new GUID("4a0a79c0876611e1a67950e5495bfcc0"); // Represents generalised state variables
	public final static GUID glCallType = new GUID("efa3f2a0951011e1a0f950e5495bfcc0");
	
	public final static GUID stateRole = new GUID("7f3857af7f2211e1a8c750e5495bfcc0");
	
	public final static GUID transactionIDRole = new GUID("f9eaf6a07e5911e189a950e5495bfcc0"); // TODO: document
	public final static GUID firstTransactionIDRole = new GUID("fc0ce4008b8511e1861750e5495bfcc0"); // Role for pointing to the first transactionID
	public final static GUID lastTransactionIDRole = new GUID("fc0d0b118b8511e1861750e5495bfcc0"); // Role for pointing to the current final transactionID
	public final static GUID nextTransactionIDRole = new GUID("fc0d0b108b8511e1861750e5495bfcc0"); // Role for indicating the next transaction ID given one
	
	public final static GUID glCallRole = new GUID("efa419b0951011e1a0f950e5495bfcc0");
	public final static GUID firstGlCallRole = new GUID("efa419b1951011e1a0f950e5495bfcc0");
	public final static GUID lastGlCallRole = new GUID("efa419b2951011e1a0f950e5495bfcc0");
	public final static GUID nextGlCallRole = new GUID("efa419b3951011e1a0f950e5495bfcc0");
	
	public final static GUID firstGlCallOrTransactionRole = new GUID("efa419b4951011e1a0f950e5495bfcc0");
	public final static GUID lastGlCallOrTransactionRole = new GUID("efa419b5951011e1a0f950e5495bfcc0");
	public final static GUID nextGlCallOrTransactionRole = new GUID("efa419b6951011e1a0f950e5495bfcc0");
	
	public final static GUID timeStampRole = new GUID("686954917ed611e1855d50e5495bfcc0");
	
	public final static GUID stateVariableValueRole = new GUID("5efd4c6087be11e1817150e5495bfcc0");
	public final static GUID stateVariableNonuniqueNameRole = new GUID("c88cb8a087c811e195ee50e5495bfcc0");
	public final static GUID childStateVariableRole = new GUID("4a0a79c1876611e1a67950e5495bfcc0");
	
	/* PDStore related varibales */
	private static PDStore mainStore;
	public PDStore store;
	protected GUID historyID;
	
	/* Debugger vars */
	// Store collections of these in the future, me thinks, so we can have multiple connections at any one time
	Socket clientSocket;
	DebuggerProtocol debugProtocol;
	DebuggerListenerThread debugListenerThread;
	int seq;
	
	protected String runningSessionName;
	protected Timestamp runningSessionStartTime;
	
	/* GUI variables */
	EventHandler eventHandler;
	JFrame windowFrame;
	JPanel 
		singleViewButtonsPane,
		singleViewTopLevelPane, // Top level pane for the singleView view, contains everything in the view EXCEPT the status bar
		singleViewLeftPane, // Contains all the items on the left of the single view, all the state selection and filtering area
		singleViewSessionSelectPane, // Sub pane to SingleViewLeftPane, contains drop boxes for filtering the states shown
		singleViewStatesListPane, // Pane containing a list of the recieved states and radio buttons to select them
		singleViewMiddlePane, // Pane containing the elements of the SingleView view that belong in the middle of the window
		singleViewStateVariablePane, // Pane containing the tree, the state variable value lists, and the comparison split pane for viewing variable values in depth
		singleViewTreeDataPane, // Pane containing the state variable tree
		singleViewFilterRadioButtonPane,
		singleViewStateVariableListPane, // Pane containing the lists showing the values of state variables
		singleViewStatusBarPane;
	JScrollPane singleViewStateListScrollPane, singleViewTreeScrollPane, singleViewValueArea1ScrollPane, singleViewValueArea2ScrollPane;
	JSplitPane singleViewValueSplitPane;
	JTabbedPane topLevelTabbedPane;
	JButton singleViewRunButton, singleViewStopButton, singleViewContinueButton,
		singleViewRequestStateButton, singleViewKillButton, singleViewConnectButton, singleViewBreakpointButton;
	JTextField singleViewIPField, singleViewPortField, singleViewFilterField;
	JTextArea singleViewValueArea1, singleViewValueArea2;
	JComboBox singleViewSessionComboBox;
	StateJTree singleViewStateTree;
	JList singleViewStateValueList1, singleViewStateValueList2;
	JLabel statusBarLabel;
	JRadioButton singleViewFilterToStateRadioButton, singleViewFilterToCallRadioButton, singleViewFilterToAllRadioButton;
	
	BreakpointDialog breakpointDialog;
	
	FlowLayout singleViewButtonsLayout;
	 // The treeData layout is used for the tree and data panel, while the variable view layout is a top level layout to make sure spacing is correct
	BorderLayout singleViewTopLevelLayout, singleViewLeftLayout,
		singleViewTreeDataLayout, singleViewVariableViewLayout;
	BoxLayout singleViewSessionSelectLayout, singleViewFilterRadiobuttonLayout, singleViewStateListLayout,
		singleViewMiddleLayout, singleViewStateVariableListLayout, singleViewStatusBarLayout;	
	
	protected String statusBarText;
	
	// The currently selected session name the table will show data for
	protected String selectedSessionName; 
	protected String filterString;
	// The GUIDS of the 2 currently selected transactions, null if a selection has not yet been made
	protected GUID selectedTransactionID1, selectedTransactionID2;
	
	// These reflect if the selected transactions are from the current session,
	// if they are we can do additional comparisons
	protected boolean selectedTransaction1IsCurrent, selectedTransaction2IsCurrent; 
	
	private boolean ignoreActionEvents;
	
	protected class StateRadioButton extends JRadioButton
	{
		protected GUID associtedTransactionID;
		protected int radioButtonColumn;
		protected boolean belongsToSelectedSession;
		
		public StateRadioButton(GUID associatedGUID, int col, boolean current)
		{
			associtedTransactionID = associatedGUID;
			radioButtonColumn = col;
			belongsToSelectedSession = current;
		}
	}
	
	/*
	 * Need a node class that can store extra information, as the querying done to PDStore by swing becomes an
	 * EXTREME bottleneck if the node simply holds a GUID and the name (or other such things) must be queried based on the GUID
	 */
	protected class StateTreeNode extends DefaultMutableTreeNode 
	{
		protected String stateVariableName;
		
		public StateTreeNode(Object obj)
		{
			super(obj);
		}
		
		public StateTreeNode(Object obj, String name)
		{
			super(obj);
			stateVariableName = name;
		}
		
		public void setStateVariableName(String newName)
		{
			stateVariableName = newName;
		}
		
		public String getStateVariableName()
		{
			return stateVariableName;
		}
	}
	
	
	/*
	 * This class needs to be used with only the StateTreeNode class, because of its convertValueToText member
	 */
	protected class StateJTree extends JTree
	{
		public StateJTree()
		{
			//Set a null root to avoid the silly Swing defaults
			super(new DefaultTreeModel(null));
		}
		
		public String convertValueToText(Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus)
		{
			StateTreeNode node;
			
			try
			{
				node = (StateTreeNode)value;
			}
			catch(ClassCastException e)
			{
				System.err.println("Error casting tree node while printing tree data");
				return "Error";
			}
			
			return node.getStateVariableName();
		}
	}
	
	protected class ListItem
	{
		public String text;
		public Color colour;
		
		public ListItem(String text, Color colour)
		{
			this.text = text;
			this.colour = colour;
		}
	}
	
	protected class EventHandler implements ActionListener, TreeExpansionListener, TreeSelectionListener,
		DebuggerProtocol.StateTreeRecievedListener, DebuggerProtocol.CallRecievedListener,
		DebuggerProtocol.ProcessRunningListener, DebuggerProtocol.StatusChangedListener,
		DebuggerProtocol.BreakpointListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(ignoreActionEvents)
				return;
			
			if(event.getSource() == singleViewRunButton)
			{
				if(debugProtocol != null)
				{
					updateStatusBar("Sent run request");
					debugProtocol.sendRun(seq++);
				}
				else
				{
					updateStatusBar("Error, debugger not connected");
				}
			}
			else if(event.getSource() == singleViewStopButton)
			{
				if(debugProtocol != null)
				{
					updateStatusBar("Sent stop request");
					debugProtocol.sendAsync(seq++);
				}
				else
				{
					updateStatusBar("Error, debugger not connected");
				}
			}
			else if(event.getSource() == singleViewContinueButton)
			{
				if(debugProtocol != null)
				{
					updateStatusBar("Sent continue request");
					debugProtocol.sendContinue(seq++);
				}
				else
				{
					updateStatusBar("Error, debugger not connected");
				}
			}
			else if(event.getSource() == singleViewRequestStateButton)
			{
				if(debugProtocol != null)
				{
					updateStatusBar("Sent state tree request");
					debugProtocol.sendStateTree(seq++);
					//debugProtocol.sendDataFramebuffer(id++, 0, 0 /* constant for window-system defined frame buffer */, 0x0404
					//		/* GL_FRONT */, 0x1908 /*GL_RGBA */ , 0x1406 /* GL_FLOAT */); // Removed for now
				}
				else
				{
					updateStatusBar("Error, debugger not connected");
				}
			}
			else if(event.getSource() == singleViewKillButton)
			{
				if(debugProtocol != null)
				{
					updateStatusBar("Sent kill request");
					debugProtocol.sendQuit(seq++);
					try
					{
						debugListenerThread.cease(); // Doesn't work at the mo
						clientSocket.close();
					}
					catch(IOException e)
					{
						updateStatusBar("Wanring, couldn't close client socket");
					}
					debugProtocol = null;
					debugListenerThread = null;
					clientSocket = null;
					seq = 0;
				}
				else
				{
					updateStatusBar("Error, debugger not connected");
				}
			}
			else if(event.getSource() == singleViewBreakpointButton)
			{
				if(debugProtocol != null)
					breakpointDialog.showBreakpointDialog();
				else
					updateStatusBar("Cannot set breakpoints: debugger not connected");
			}
			else if(event.getSource() == singleViewConnectButton)
			{
				updateStatusBar("Connecting to " + singleViewIPField.getText() + ":" + singleViewPortField.getText());
				try
				{
					ignoreActionEvents = true;
					clientSocket = new Socket(InetAddress.getByName(singleViewIPField.getText()), Integer.parseInt(singleViewPortField.getText()));
					debugProtocol = new DebuggerProtocol(clientSocket);
					debugProtocol.addStateTreeRecievedListener(eventHandler);
					debugProtocol.addCallRecievedLister(eventHandler);
					debugProtocol.addProcessRunningListener(eventHandler);
					debugProtocol.addStatusChangedListener(eventHandler);
					debugProtocol.addBreakpointListener(eventHandler);
					debugListenerThread = new DebuggerListenerThread(debugProtocol);
					debugListenerThread.start();
					statusChanged(debugProtocol.getStatus());
					updateStatusBar("Debugger connected to " + singleViewIPField.getText() + ":" + singleViewPortField.getText());
				}
				catch(NumberFormatException e)
				{
					updateStatusBar("Couldn't parse that port as a number, you should probably make it a number\n");
				}
				catch(IOException e)
				{
					updateStatusBar("Error, could not connect to host\n");
				}
				finally
				{
					ignoreActionEvents = false;
				}
			}
			else if(event.getSource() == singleViewSessionComboBox)
			{
				if(singleViewSessionComboBox.getSelectedItem() == null)
				{
					selectedSessionName = null;
				}
				else
				{
					selectedSessionName = singleViewSessionComboBox.getSelectedItem().toString();
				}
				
				if(!java.awt.EventQueue.isDispatchThread())
				{	
					try {
						java.awt.EventQueue.invokeAndWait(new Runnable()
						{
							public void run()
							{  	
								swingRefreshStateList();
								swingRefreshStateVariableTree();
							}
						});
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return;
				}
				
				swingRefreshStateList();
				swingRefreshStateVariableTree();	
			}
			else if(event.getSource() == singleViewFilterField)
			{
				filterString = singleViewFilterField.getText();
				swingRefreshStateVariableTree();
			}
			else if(
				event.getSource() == singleViewFilterToStateRadioButton ||
				event.getSource() ==singleViewFilterToCallRadioButton ||
				event.getSource() == singleViewFilterToAllRadioButton)
			{
				swingRefreshStateList();
			}
			else if(event.getSource() instanceof StateRadioButton)
			{
				if(((StateRadioButton)event.getSource()).radioButtonColumn == 1)
				{
					selectedTransactionID1 = ((StateRadioButton)event.getSource()).associtedTransactionID;
					selectedTransaction1IsCurrent = ((StateRadioButton)event.getSource()).belongsToSelectedSession;
				}
				else // if(StateRadioButton)event.getSource()).radioButtonColumn == 2)
				{
					selectedTransactionID2 = ((StateRadioButton)event.getSource()).associtedTransactionID;
					selectedTransaction2IsCurrent = ((StateRadioButton)event.getSource()).belongsToSelectedSession;
				}		
				if(!java.awt.EventQueue.isDispatchThread())
				{	
					try {
						java.awt.EventQueue.invokeAndWait(new Runnable()
						{
							public void run()
							{  	
								swingRefreshStateVariableTree();
							}
						});
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return;
				}
				
				swingRefreshStateVariableTree();	
			}
		}
	
		public void treeExpanded(TreeExpansionEvent event) {
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshStateVariableValueLists();
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
			
			swingRefreshStateVariableValueLists();
		}
	
		public void treeCollapsed(TreeExpansionEvent event) {
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshStateVariableValueLists();
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
			
			swingRefreshStateVariableValueLists();
		}
		
		public void valueChanged(TreeSelectionEvent e) {
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshValueAreaText();
						}
					});
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				return;
			}
			
			swingRefreshValueAreaText();
		}
	
		public void stateTreeRecieved(State stateRoot, TimeStamp timeStamp) {
			if(stateRoot != null)
				updateDatabaseState(stateRoot, timeStamp.time, runningSessionName);
			else
				System.err.println("Error, could not update database, null state given");
			
		}
	
		public void processRunningRecieved(String processName, TimeStamp timeStamp)
		{
			// TODO: alter this to create a more nicely formatted string later, also add IP or hostname
			
			runningSessionStartTime = new Timestamp(timeStamp.time * 1000); // Multiply by 1000 as timeStamp.time is in seconds and Timestamp() expects milliseconds
			runningSessionName = processName + " " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(runningSessionStartTime);
		}

		public void statusChanged(DebuggerStatus status)
		{
			updateEnabledButtons(status);
			updateStatusBar(null);
		}

		public void breakpointHit(String functionName)
		{
			updateStatusBar("Breakpoint hit on function: " + functionName);
		}

		public void callRecieved(String callDump, TimeStamp timeStamp)
		{
			updateDatabaseCall(callDump, timeStamp.time, runningSessionName);
		}
	}

	protected class BreakpointDialog extends JDialog implements ActionListener
	{
		protected JScrollPane breakpointListScrollPane;
		protected JPanel breakpointTopLevelPane, breakpointListPane, buttonPanel;
		protected BoxLayout breakpointListLayout, buttonLayout;
		protected BorderLayout breakpointTopLevelLayout;
		protected JButton addBreakpointButton, okayButton;
		
		protected class BreakpointCheckbox extends JCheckBox
		{
			public String associatedFunctionName;
			
			public BreakpointCheckbox(String functionName)
			{
				super();
				associatedFunctionName = functionName;
			}
		}
		
		protected class BreakpointDeleteButton extends JButton
		{
			String associatedFunctionName;
			
			public BreakpointDeleteButton(String functionName)
			{
				associatedFunctionName = functionName;
			}
		}
		
		public BreakpointDialog(JFrame parent)
		{
			super(parent, "Breakpoints", false);
			
			breakpointTopLevelPane = new JPanel();
			breakpointTopLevelLayout = new BorderLayout();
			breakpointTopLevelPane.setLayout(breakpointTopLevelLayout);
			
			breakpointListPane = new JPanel();
			breakpointListLayout = new BoxLayout(breakpointListPane, BoxLayout.Y_AXIS);
			breakpointListPane.setLayout(breakpointListLayout);
			
			breakpointListScrollPane = new JScrollPane(breakpointListPane);
			
			okayButton = new JButton("Okay");
			okayButton.addActionListener(this);
			
			addBreakpointButton = new JButton("Add Breakpoint");
			addBreakpointButton.addActionListener(this);
			
			buttonPanel = new JPanel();
			buttonLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
			buttonPanel.setLayout(buttonLayout);
			
			buttonPanel.add(addBreakpointButton);
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.add(okayButton);
			
			breakpointTopLevelPane.add(buttonPanel, BorderLayout.SOUTH);
			
			breakpointTopLevelPane.add(breakpointListScrollPane, BorderLayout.CENTER);
			
			getContentPane().add(breakpointTopLevelPane);
		}
		
		public void showBreakpointDialog()
		{
			setSize(200, 480);
			setVisible(true);
			this.validate();
			//pack();
			
		}

		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource() == okayButton)
			{
				setVisible(false);
				dispose();
			}
			else if(e.getSource() == addBreakpointButton)
			{
				String breakOnFunction = (String)JOptionPane.showInputDialog(
	                    this,
	                    "Enter the identifier of an OpenGL function to break on",
	                    "New Breakpoint",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    "");
				
				if(breakOnFunction == null)
					return;
				
				debugProtocol.setBreak(seq++, breakOnFunction, true);
				refreshBreakpointList();
			}
			else if(e.getSource() instanceof BreakpointCheckbox)
			{
				BreakpointCheckbox checkbox = (BreakpointCheckbox)e.getSource();
				
				debugProtocol.setBreak(seq++, checkbox.associatedFunctionName, checkbox.isSelected());
			}
		}
		
		public void refreshBreakpointList()
		{
			breakpointListPane.removeAll();
			
			JPanel breakpointPane;
			BoxLayout breakpointLayout;
			BreakpointCheckbox checkbox;
			
			Enumeration<String> keys = debugProtocol.getBreakOn().keys();
			
			while(keys.hasMoreElements())
			{
				String key = keys.nextElement();
				
				breakpointPane = new JPanel();
				breakpointLayout = new BoxLayout(breakpointPane, BoxLayout.X_AXIS);
				breakpointPane.setLayout(breakpointLayout);
				
				checkbox = new BreakpointCheckbox(key);
				checkbox.setSelected(true);
				checkbox.addActionListener(this);
				
				breakpointPane.add(checkbox);
				breakpointPane.add(new JLabel(key));
				breakpointPane.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				breakpointListPane.add(breakpointPane);
			}
			this.validate();
		}
	}
	
	protected void swingRefreshStateList()
	{		
		ignoreActionEvents = true;
		
		selectedTransaction1IsCurrent = false;
		selectedTransaction2IsCurrent = false;
		
		singleViewStatesListPane.removeAll();
		
		if(selectedSessionName == null || selectedSessionName.equals(""))
		{
			singleViewStatesListPane.validate();
			ignoreActionEvents = false;
			return;
		}
		
		GUID transaction = store.begin();
		
		if(singleViewFilterToStateRadioButton.isSelected())
		{
			GUID currentTransaction = (GUID)
					store.getInstance(transaction,
							store.getId(transaction, selectedSessionName),
							firstTransactionIDRole
				);
			
			int i = 0;
			
			JPanel pane, topPane1 = null, topPane2 = null;
			BoxLayout boxLayout;
			JLabel stateTimestampLabel, sessionLabel1 = null, sessionLabel2 = null;
			StateRadioButton radioButton1, radioButton2; // Only one variable is required, but 2 makes code clearer
			ButtonGroup buttonGroup1, buttonGroup2; 
			
			buttonGroup1 = new ButtonGroup();
			buttonGroup2 = new ButtonGroup();
			
			// Create buttons at the top of the list for already selected states
			// these will remain in a different colour if they do not belong to the selected session
			// or will be removed later in the algo if they belong to the selected session
			if(selectedTransactionID1 != null)
			{
				topPane1 = new JPanel();
				boxLayout = new BoxLayout(topPane1, BoxLayout.X_AXIS);
				topPane1.setLayout(boxLayout);
				
				GUID sessionGUID = (GUID)store.getInstance(transaction, selectedTransactionID1, transactionIDRole.getPartner());
				Timestamp stamp = (Timestamp)
						store.getInstance(selectedTransactionID1,
						store.getInstance(selectedTransactionID1, sessionGUID, stateRole),
						timeStampRole);
				
				sessionLabel1 = new JLabel(store.getName(transaction, sessionGUID));
				stateTimestampLabel = new JLabel(stamp.toString() + "-State");
				
				sessionLabel1.setForeground(Color.MAGENTA);
				stateTimestampLabel.setForeground(Color.MAGENTA);
				
				radioButton1 = new StateRadioButton(selectedTransactionID1, 1, false);
				radioButton2 = new StateRadioButton(selectedTransactionID1, 2, false);
				
				radioButton1.addActionListener(eventHandler);
				radioButton2.addActionListener(eventHandler);
				
				buttonGroup1.add(radioButton1);
				buttonGroup2.add(radioButton2);
				
				radioButton1.setSelected(true);
				if(selectedTransactionID1.equals(selectedTransactionID2))
					radioButton2.setSelected(true);
				
				topPane1.add(stateTimestampLabel);
				topPane1.add(radioButton1);
				topPane1.add(radioButton2);
				
				topPane1.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				singleViewStatesListPane.add(sessionLabel1);
				singleViewStatesListPane.add(topPane1);
			}
			// If the second transaction exists and is not the same as the first create a second set of check buttons
			if(selectedTransactionID2 != null && !selectedTransactionID1.equals(selectedTransactionID2))
			{
				topPane2 = new JPanel();
				boxLayout = new BoxLayout(topPane2, BoxLayout.X_AXIS);
				topPane2.setLayout(boxLayout);
				
				GUID sessionGUID = (GUID)store.getInstance(transaction, selectedTransactionID2, transactionIDRole.getPartner());
				Timestamp stamp = (Timestamp)
						store.getInstance(selectedTransactionID2,
						store.getInstance(selectedTransactionID2, sessionGUID, stateRole),
						timeStampRole);
				
				sessionLabel2 = new JLabel(store.getName(transaction, sessionGUID));
				stateTimestampLabel = new JLabel(stamp.toString() + "-State");
				
				sessionLabel2.setForeground(Color.MAGENTA);
				stateTimestampLabel.setForeground(Color.MAGENTA);
				
				radioButton1 = new StateRadioButton(selectedTransactionID2, 1, false);
				radioButton2 = new StateRadioButton(selectedTransactionID2, 2, false);
				
				radioButton1.addActionListener(eventHandler);
				radioButton2.addActionListener(eventHandler);
				
				buttonGroup1.add(radioButton1);
				buttonGroup2.add(radioButton2);
				
				radioButton2.setSelected(true);
				
				topPane2.add(stateTimestampLabel);
				topPane2.add(radioButton1);
				topPane2.add(radioButton2);
				
				topPane2.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				singleViewStatesListPane.add(sessionLabel2);
				singleViewStatesListPane.add(topPane2);
			}
			
			while(currentTransaction != null)
			{
				pane = new JPanel();
				boxLayout = new BoxLayout(pane, BoxLayout.X_AXIS);
				pane.setLayout(boxLayout);
				
				GUID sessionGUID = store.getId(transaction, selectedSessionName);
				Timestamp stamp = (Timestamp)
						store.getInstance(currentTransaction,
						store.getInstance(currentTransaction, sessionGUID, stateRole),
						timeStampRole);
				
				stateTimestampLabel = new JLabel(stamp.toString() + "-State");
				
				radioButton1 = new StateRadioButton(currentTransaction, 1, true);
				radioButton2 = new StateRadioButton(currentTransaction, 2, true);
				
				radioButton1.addActionListener(eventHandler);
				radioButton2.addActionListener(eventHandler);
				
				buttonGroup1.add(radioButton1);
				buttonGroup2.add(radioButton2);
				
				if(currentTransaction.equals(selectedTransactionID1))
				{
					// If the additional check boxes and labels belong to the current session, remove them
					if(topPane1 != null)
					{
						singleViewStatesListPane.remove(sessionLabel1);
						singleViewStatesListPane.remove(topPane1);
					}
					radioButton1.setSelected(true);
					selectedTransaction1IsCurrent = true;
				}
				if(currentTransaction.equals(selectedTransactionID2))
				{
					if(topPane2 != null)
					{
						singleViewStatesListPane.remove(sessionLabel2);
						singleViewStatesListPane.remove(topPane2);
					}
					radioButton2.setSelected(true);
					selectedTransaction2IsCurrent = true;
				}
				
				pane.add(stateTimestampLabel);
				pane.add(radioButton1);
				pane.add(radioButton2);
				
				pane.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				singleViewStatesListPane.add(pane);
				
				currentTransaction = (GUID)store.getInstance(transaction, currentTransaction, nextTransactionIDRole);
				++i;
			}
		}
		else if(singleViewFilterToCallRadioButton.isSelected())
		{
			GUID currentCall = (GUID)
					store.getInstance(transaction,
							store.getId(transaction, selectedSessionName),
							firstGlCallRole
				);
			
			int i = 0;
			
			JPanel pane;
			BoxLayout boxLayout;
			JLabel label;
			
			while(currentCall != null)
			{
				
				label = new JLabel(store.getName(transaction, currentCall));
				
				label.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				singleViewStatesListPane.add(label);
				
				currentCall = (GUID)store.getInstance(transaction, currentCall, nextGlCallRole);
				++i;
			}
		}
		else //if(singleViewFilterToAllRadioButton.isSelected())
		{
			GUID currentGlCallOrTransaction = (GUID)
					store.getInstance(transaction,
							store.getId(transaction, selectedSessionName),
							firstGlCallOrTransactionRole
				);
			
			int i = 0;
			
			JPanel pane, topPane1 = null, topPane2 = null;
			BoxLayout boxLayout;
			JLabel stateTimestampLabel, sessionLabel1 = null, sessionLabel2 = null;
			StateRadioButton radioButton1, radioButton2; // Only one variable is required, but 2 makes code clearer
			ButtonGroup buttonGroup1, buttonGroup2; 
			
			buttonGroup1 = new ButtonGroup();
			buttonGroup2 = new ButtonGroup();
			
			// Create buttons at the top of the list for already selected states
			// these will remain in a different colour if they do not belong to the selected session
			// or will be removed later in the algo if they belong to the selected session
			if(selectedTransactionID1 != null)
			{
				topPane1 = new JPanel();
				boxLayout = new BoxLayout(topPane1, BoxLayout.X_AXIS);
				topPane1.setLayout(boxLayout);
				
				GUID sessionGUID = (GUID)store.getInstance(transaction, selectedTransactionID1, transactionIDRole.getPartner());
				Timestamp stamp = (Timestamp)
						store.getInstance(selectedTransactionID1,
						store.getInstance(selectedTransactionID1, sessionGUID, stateRole),
						timeStampRole);
				
				sessionLabel1 = new JLabel(store.getName(transaction, sessionGUID));
				stateTimestampLabel = new JLabel(stamp.toString() + "-State");
				
				sessionLabel1.setForeground(Color.MAGENTA);
				stateTimestampLabel.setForeground(Color.MAGENTA);
				
				radioButton1 = new StateRadioButton(selectedTransactionID1, 1, false);
				radioButton2 = new StateRadioButton(selectedTransactionID1, 2, false);
				
				radioButton1.addActionListener(eventHandler);
				radioButton2.addActionListener(eventHandler);
				
				buttonGroup1.add(radioButton1);
				buttonGroup2.add(radioButton2);
				
				radioButton1.setSelected(true);
				if(selectedTransactionID1.equals(selectedTransactionID2))
					radioButton2.setSelected(true);
				
				topPane1.add(stateTimestampLabel);
				topPane1.add(radioButton1);
				topPane1.add(radioButton2);
				
				topPane1.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				singleViewStatesListPane.add(sessionLabel1);
				singleViewStatesListPane.add(topPane1);
			}
			// If the second transaction exists and is not the same as the first create a second set of check buttons
			if(selectedTransactionID2 != null && !selectedTransactionID1.equals(selectedTransactionID2))
			{
				topPane2 = new JPanel();
				boxLayout = new BoxLayout(topPane2, BoxLayout.X_AXIS);
				topPane2.setLayout(boxLayout);
				
				GUID sessionGUID = (GUID)store.getInstance(transaction, selectedTransactionID2, transactionIDRole.getPartner());
				Timestamp stamp = (Timestamp)
						store.getInstance(selectedTransactionID2,
						store.getInstance(selectedTransactionID2, sessionGUID, stateRole),
						timeStampRole);
				
				sessionLabel2 = new JLabel(store.getName(transaction, sessionGUID));
				stateTimestampLabel = new JLabel(stamp.toString() + "-State");
				
				sessionLabel2.setForeground(Color.MAGENTA);
				stateTimestampLabel.setForeground(Color.MAGENTA);
				
				radioButton1 = new StateRadioButton(selectedTransactionID2, 1, false);
				radioButton2 = new StateRadioButton(selectedTransactionID2, 2, false);
				
				radioButton1.addActionListener(eventHandler);
				radioButton2.addActionListener(eventHandler);
				
				buttonGroup1.add(radioButton1);
				buttonGroup2.add(radioButton2);
				
				radioButton2.setSelected(true);
				
				topPane2.add(stateTimestampLabel);
				topPane2.add(radioButton1);
				topPane2.add(radioButton2);
				
				topPane2.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				singleViewStatesListPane.add(sessionLabel2);
				singleViewStatesListPane.add(topPane2);
			}
			
			while(currentGlCallOrTransaction != null)
			{
				if(store.getInstance(transaction, currentGlCallOrTransaction, timeStampRole) != null)
				// We have a glCall
				{
					stateTimestampLabel = new JLabel(store.getName(transaction, currentGlCallOrTransaction));
					
					stateTimestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
					
					singleViewStatesListPane.add(stateTimestampLabel);
					
					currentGlCallOrTransaction = (GUID)store.getInstance(transaction, currentGlCallOrTransaction, nextGlCallOrTransactionRole);
					++i;
				}
				else
				// We have a transaction
				{
					pane = new JPanel();
					boxLayout = new BoxLayout(pane, BoxLayout.X_AXIS);
					pane.setLayout(boxLayout);
					
					GUID sessionGUID = store.getId(transaction, selectedSessionName);
					Timestamp stamp = (Timestamp)
							store.getInstance(currentGlCallOrTransaction,
							store.getInstance(currentGlCallOrTransaction, sessionGUID, stateRole),
							timeStampRole);
					
					stateTimestampLabel = new JLabel(stamp.toString() + "-State");
					radioButton1 = new StateRadioButton(currentGlCallOrTransaction, 1, true);
					radioButton2 = new StateRadioButton(currentGlCallOrTransaction, 2, true);
					
					radioButton1.addActionListener(eventHandler);
					radioButton2.addActionListener(eventHandler);
					
					buttonGroup1.add(radioButton1);
					buttonGroup2.add(radioButton2);
					
					if(currentGlCallOrTransaction.equals(selectedTransactionID1))
					{
						radioButton1.setSelected(true);
						selectedTransaction1IsCurrent = true;
					}
					if(currentGlCallOrTransaction.equals(selectedTransactionID2))
					{
						radioButton2.setSelected(true);
						selectedTransaction2IsCurrent = true;
					}
					
					pane.add(stateTimestampLabel);
					pane.add(radioButton1);
					pane.add(radioButton2);
					
					pane.setAlignmentX(Component.LEFT_ALIGNMENT);
					
					singleViewStatesListPane.add(pane);
					
					currentGlCallOrTransaction = (GUID)store.getInstance(transaction, currentGlCallOrTransaction, nextGlCallOrTransactionRole);
					++i;
				}
			}
		}
		
		store.commit(transaction);
		
		singleViewStateListScrollPane.validate();
		singleViewStateListScrollPane.repaint();
		
		ignoreActionEvents = false;
	}
	
	protected void recusiveAddStateToDatabase(State state, GUID stateVariableGUID, String concatStateVariableName, TreeSet<GUID> visitedNodes, GUID transaction)
	{
		String currentStateVariableName = concatStateVariableName + "-" + state.name;
		
		// Add this node to the set of visited nodes
		visitedNodes.add(stateVariableGUID);
		
		if(!state.data.equals(store.getInstance(transaction, stateVariableGUID, stateVariableValueRole)))
		{ // If data has changed
			store.setLink(transaction, stateVariableGUID, stateVariableValueRole, state.data);
		}
		
		for(State s : state.children)
		{
			GUID childStateVariableGUID;
			
			// If child's role doesn't exist yet
			if((childStateVariableGUID = (GUID)store.getId(transaction, currentStateVariableName + "-" + s.name)) == null)
			{ 
				// Create, name, and link the child stateVariable
				childStateVariableGUID = new GUID();
				store.setType(transaction, childStateVariableGUID, stateVariableType);
				store.setName(transaction, childStateVariableGUID, currentStateVariableName + "-" + s.name);
				store.addLink(transaction, childStateVariableGUID, stateVariableNonuniqueNameRole, s.name);
			}
			// Make sure there's a link between a type and its child variables
			store.ensureAddedLink(transaction, stateVariableGUID, childStateVariableRole, childStateVariableGUID);
			
			recusiveAddStateToDatabase(s, childStateVariableGUID, currentStateVariableName, visitedNodes, transaction);
		}
	}
	
	protected void recursivePruneDatabaseState(GUID stateVariableGUID, TreeSet<GUID> visitedNodes, GUID transaction)
	{
		GUID[] childGUIDs = store.getInstances(transaction, stateVariableGUID, childStateVariableRole).toArray(new GUID[0]);
		
		for(GUID guid: childGUIDs)
		{
			if(!visitedNodes.contains(guid))
			{
				// if this node wasn't part of the state then we remove the link
				store.removeLink(transaction, stateVariableGUID, childStateVariableRole, guid);
			}
			else
			{
				 recursivePruneDatabaseState(guid, visitedNodes, transaction);
			}
		}
	}
	
	protected void updateDatabaseState(State stateRoot, long timeStamp, String sessionName)
	{
		boolean newSession = false;
		
		GUID transaction = store.begin();
		
		GUID sessionGUID;
		GUID stateGUID;
		
		// This set is used to store the GUIDs of nodes as they're visited
		// during adding the state to the database. The set is then used
		// to prune nodes if they are not in the list
		TreeSet<GUID> visitedNodes = new TreeSet<GUID>();
		
		/*
		 * Update the PDStore database with the State data
		 */
		
		// If we don't have this session in our database yet we create an entry
		if((sessionGUID = store.getId(transaction, sessionName)) == null)
		{ 
			sessionGUID = new GUID();
			// Setup the new session
			store.setType(transaction, sessionGUID, sessionType);
			
			store.setName(transaction, sessionGUID, sessionName);
				
			// Setup a new state for the session
			stateGUID = new GUID();	//Create a new state for the incoming state
			
			store.setType(transaction, stateGUID, stateType);
			
			store.setLink(transaction, sessionGUID, stateRole, stateGUID);
			
			store.addLink(transaction, sessionGUID, timeStampRole, runningSessionStartTime); // Set the sessions start time
			
			store.setName(transaction, stateGUID, "StateRoot");
			
			newSession = true;
		}
		// Otherwise the session already exists...
		else
		{ 
			// Check to see if it has a state in it already, if not: create one
			if((stateGUID = (GUID)store.getInstance(transaction, sessionGUID, stateRole)) == null)
			{
				stateGUID = new GUID();	//Create a new state for the incoming state
				
				store.setType(transaction, stateGUID, stateType);
				
				store.setLink(transaction, sessionGUID, stateRole, stateGUID);
				
				store.addLink(transaction, sessionGUID, timeStampRole, runningSessionStartTime); // Set the sessions start time
				
				store.setName(transaction, stateGUID, "StateRoot");
			}
		}
		
		// Add the state root an all it's children to the database.
		recusiveAddStateToDatabase(stateRoot, stateGUID,  "StateRoot", visitedNodes, transaction);
		
		Timestamp stamp = new Timestamp(timeStamp * 1000); // Have to create this here, a call to new in setLink seems to result in failure as of 2012.04.22
		
		store.setLink(transaction, stateGUID, timeStampRole, stamp); // Multiply by 1000, as java has to be different and can't just take an epoch seconds stamp
		
		store.commit(transaction);
		
		/*
		 * Prune nodes not exisiting in the previous state
		 * TODO: this may be movable to the previous commit block, but was having issues so moved it to its own one
		 */
		transaction = store.begin();
		
		recursivePruneDatabaseState(stateGUID, visitedNodes, transaction);
		
		GUID transactionID = store.commit(transaction);
		
		/*
		 * Store the transaction ID of the state committing transaction
		 * Create relationships between the transactionIDs as appropriate
		 */
		
		transaction = store.begin();	
		
		store.addLink(transaction, sessionGUID, transactionIDRole, transactionID);
		
		// If this is the first transactionID
		if(store.getInstance(transaction, sessionGUID, firstTransactionIDRole) == null)
		{
			store.addLink(transaction, sessionGUID, firstTransactionIDRole, transactionID);
		}
		// Otherwise it becomes the tail
		else
		{
			GUID previousTransactionID;
			previousTransactionID = (GUID)store.getInstance(transaction, sessionGUID, lastTransactionIDRole);
			store.setLink(transaction, previousTransactionID, nextTransactionIDRole, transactionID);
		}
		store.setLink(transaction, sessionGUID, lastTransactionIDRole, transactionID);
		
		// If this is the first transactionID or glCall
		if(store.getInstance(transaction, sessionGUID, firstGlCallOrTransactionRole) == null)
		{
			store.addLink(transaction, sessionGUID, firstGlCallOrTransactionRole, transactionID);
		}
		// Otherwise it becomes the tail
		else
		{
			GUID previousGlCallOrTransactionID;
			previousGlCallOrTransactionID = (GUID)store.getInstance(transaction, sessionGUID, lastGlCallOrTransactionRole);
			store.setLink(transaction, previousGlCallOrTransactionID, nextGlCallOrTransactionRole, transactionID);
		}
		store.setLink(transaction, sessionGUID, lastGlCallOrTransactionRole, transactionID);
		
		store.commit(transaction);
		
		/*
		 * Refresh the GUI elements as required
		 */
		
		if(newSession)
		{
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshSessionComboBox();
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

				return;
			}
			
			swingRefreshSessionComboBox();
		}
		else
		{
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshStateList();
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
			
			swingRefreshStateList();
		}
	}
	
	protected void updateDatabaseCall(String callDump, long timeStamp, String sessionName)
	{
		boolean newSession = false;
		
		GUID transaction = store.begin();
		
		GUID sessionGUID;
		GUID callGUID;
		
		// If we don't have this session in our database yet we create an entry
		if((sessionGUID = store.getId(transaction, sessionName)) == null)
		{ 
			sessionGUID = new GUID();
			// Setup the new session
			store.setType(transaction, sessionGUID, sessionType);
			
			store.setName(transaction, sessionGUID, sessionName);
			
			newSession = true;
		}
		
		callGUID = new GUID();
		Timestamp stamp = new Timestamp(timeStamp);
		
		store.setName(transaction, callGUID, callDump);
		store.addLink(transaction, callGUID, timeStampRole, stamp);
		
		// TODO: comment
		if(store.getInstance(transaction, sessionGUID, firstGlCallRole) == null)
		{
			store.addLink(transaction, sessionGUID, firstGlCallRole, callGUID);
		}
		else
		{
			GUID previousGlCallGUID;
			previousGlCallGUID = (GUID)store.getInstance(transaction, sessionGUID, lastGlCallRole);
			store.setLink(transaction, previousGlCallGUID, nextGlCallRole, callGUID);
		}
		store.setLink(transaction, sessionGUID, lastGlCallRole, callGUID);
		
		if(store.getInstance(transaction, sessionGUID, firstGlCallOrTransactionRole) == null)
		{
			store.addLink(transaction, sessionGUID, firstGlCallOrTransactionRole, callGUID);
		}
		else
		{
			GUID previousGlCallOrTransactionID;
			previousGlCallOrTransactionID = (GUID)store.getInstance(transaction, sessionGUID, lastGlCallOrTransactionRole);
			store.setLink(transaction, previousGlCallOrTransactionID, nextGlCallOrTransactionRole, callGUID);
		}	
		store.setLink(transaction, sessionGUID, lastGlCallOrTransactionRole, callGUID);
		
		store.commit(transaction);
		
		
		if(newSession)
		{
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshSessionComboBox();
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
			
			swingRefreshSessionComboBox();
		}
		else
		{
			if(!java.awt.EventQueue.isDispatchThread())
			{	
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable()
					{
						public void run()
						{  	
							swingRefreshStateList();
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return;
			}
			
			swingRefreshStateList();
		}
	}
	
	
	protected void swingRefreshSessionComboBox()
	{
		String previousSessionName = (String)singleViewSessionComboBox.getSelectedItem();
		
		ignoreActionEvents = true;
		
		singleViewSessionComboBox.removeAllItems();
		
		GUID[] sessionGUIDs;
		
		GUID transaction = store.begin();
		
		sessionGUIDs = store.getAllInstancesOfType(transaction, sessionType).toArray(new GUID[0]); // Get the session data of the correct index
		
		int i = 0;
		int selectedIndex = -1;
		String sessionName;
		for(GUID guid: sessionGUIDs)
		{
			sessionName = (String)store.getName(transaction, guid);
			singleViewSessionComboBox.addItem(sessionName);
			
			if(sessionName.equals(previousSessionName))
				selectedIndex = i;
			
			++i;
		}
		
		store.commit(transaction);
		
		ignoreActionEvents = false;
		
		singleViewSessionComboBox.setSelectedIndex(selectedIndex);
		
	}
	
	// Creates the root of the tree and recursively creates the child nodes
	protected MutableTreeNode createTreeRoot()
	{
		if(selectedTransactionID1 == null && selectedTransactionID2 == null)
			return null;
			
		GUID sessionGUID;	
		GUID stateGUID;
		
		GUID transaction = store.begin();
		if(selectedTransactionID1 == null && selectedTransactionID2 != null) // If a selection is made from the second column but not one from the first
		{
			// Get the appropriate session from the selected transactionID
			sessionGUID = (GUID)store.getInstance(transaction, selectedTransactionID2, transactionIDRole.getPartner());
			store.commit(transaction);
			stateGUID = (GUID)store.getInstance(selectedTransactionID2, sessionGUID, stateRole);
			return recursivelyCreateTreeNodes(stateGUID, selectedTransactionID2);
		}
		else
		{
			// Get the appropriate session from the selected transactionID
			sessionGUID = (GUID)store.getInstance(transaction, selectedTransactionID1, transactionIDRole.getPartner());
			store.commit(transaction);
			stateGUID = (GUID)store.getInstance(selectedTransactionID1, sessionGUID, stateRole);
			return recursivelyCreateTreeNodes(stateGUID, selectedTransactionID1);
		}
	}
	
	protected StateTreeNode recursivelyCreateTreeNodes(GUID stateVariableGUID, GUID transaction)
	{
		StateTreeNode currentNode = new StateTreeNode(stateVariableGUID, (String)store.getInstance(transaction, stateVariableGUID, stateVariableNonuniqueNameRole));
		
		Collection<GUID> childStateVariables = (Collection<GUID>)(Object)store.getInstances(transaction, stateVariableGUID, childStateVariableRole);
		
		for(GUID guid: childStateVariables)
		{
			currentNode.add(recursivelyCreateTreeNodes(guid, transaction));
		}
		
		return currentNode;
	}

	protected void swingRefreshStateVariableTree()
	{	
	
		((DefaultTreeModel)(singleViewStateTree.getModel())).setRoot(createTreeRoot());
			  		
		swingRefreshStateVariableValueLists();
	
	}
	
	/*
	 * Returns a colour to represent the changes taken between states
	 * @return
	 * in order of precidence (lower values overwrite higher values)
	 * Color.BLACK means no recorded change has taken place
	 * Color.MAGENTA means that while the values are now the same, a recorded change did take place
	 * Color.RED means that the two states currently have a change
	 */
	protected Color recursivelyCheckStateVariableValueListColour(DefaultMutableTreeNode treeNode, GUID transaction1, GUID transaction2)
	{
		Color returnColour;
				
		GUID treeNodeStateVariableGUID = (GUID)treeNode.getUserObject();
		
		String variableString1;
		String variableString2;
		
		if(transaction1.equals(transaction2))
		{
			returnColour = Color.BLACK;
		}
		else if(transaction1.getTime() > transaction2.getTime()) // transaction1 occured later
		{
			
			List<PDChange<GUID, Object, GUID>> changes = store.getChanges(transaction2, treeNodeStateVariableGUID, stateVariableValueRole);
			if(changes.size() > 0)
			{
				variableString1 = (String)store.getInstance(transaction1, treeNodeStateVariableGUID, stateVariableValueRole);
				variableString2 = (String)store.getInstance(transaction2, treeNodeStateVariableGUID, stateVariableValueRole);
				
				if(variableString1.equals(variableString2))
					returnColour = Color.MAGENTA;
				else
					returnColour = Color.RED;
			}
			else
			{
				returnColour = Color.BLACK;
			}
		}
		else // transaction2 occured later
		{
			List<PDChange<GUID, Object, GUID>> changes = store.getChanges(transaction1, treeNodeStateVariableGUID, stateVariableValueRole);
			if(changes.size() > 0)
			{
				variableString1 = (String)store.getInstance(transaction1, treeNodeStateVariableGUID, stateVariableValueRole);
				variableString2 = (String)store.getInstance(transaction2, treeNodeStateVariableGUID, stateVariableValueRole);
				
				if(variableString1.equals(variableString2))
					returnColour = Color.MAGENTA;
				else
					returnColour = Color.RED;
			}
			else
			{
				returnColour = Color.BLACK;
			}
		}
		
		Color childColour;
		
		Enumeration<DefaultMutableTreeNode> children = treeNode.children();
		
		while(children.hasMoreElements())
		{
			childColour = recursivelyCheckStateVariableValueListColour(children.nextElement(), transaction1, transaction2);
			if(childColour.equals(Color.RED))
				return childColour;
			else if(childColour.equals(Color.MAGENTA))
				returnColour = Color.MAGENTA;
		}
		
		return returnColour;
	}
	
	protected void recursivelyFillStateVariableValueLists(DefaultMutableTreeNode treeNode,
			DefaultListModel listModel1, GUID transaction1,
			DefaultListModel listModel2, GUID transaction2)
	{
		final int maxValueLength = 50;
		
		GUID treeNodeStateVariableGUID = (GUID)treeNode.getUserObject();
		
		String variableString1 = (String)store.getInstance(transaction1, treeNodeStateVariableGUID, stateVariableValueRole);
		String variableString2 = (String)store.getInstance(transaction2, treeNodeStateVariableGUID, stateVariableValueRole);
		
		if(variableString1 == null || variableString1.equals(""))
		{
			listModel1.addElement(new ListItem("<Empty>", Color.BLACK));
		}
		else
		{
			if(variableString1.length() > maxValueLength)
			{
				variableString1 = variableString1.substring(0, maxValueLength);
				variableString1 += "...";
			}
			listModel1.addElement(new ListItem(variableString1, Color.BLACK));
		}
		
		if(variableString2 == null || variableString2.equals(""))
		{
			Color elementColour = recursivelyCheckStateVariableValueListColour(treeNode, transaction1, transaction2);
			if(!selectedTransaction1IsCurrent || !selectedTransaction2IsCurrent)
			{
				if(elementColour.equals(Color.MAGENTA))
					elementColour = Color.BLACK;
			}
			listModel2.addElement(new ListItem("<Empty>", elementColour));
		}
		else
		{
			if(variableString2.length() > maxValueLength)
			{
				variableString2 = variableString2.substring(0, maxValueLength);
				variableString2 += "...";
			}
			
			// Comparison
			if(transaction1.equals(transaction2))
			{
				listModel2.addElement(new ListItem(variableString2, Color.BLACK));
			}
			else if(transaction1.getTime() > transaction2.getTime()) // transaction1 occured later
			{
				Color elementColour = recursivelyCheckStateVariableValueListColour(treeNode, transaction1, transaction2);
				if(!selectedTransaction1IsCurrent || !selectedTransaction2IsCurrent)
				{
					if(elementColour.equals(Color.MAGENTA))
						elementColour = Color.BLACK;
				}
				listModel2.addElement(new ListItem(variableString2, elementColour));
			}
			else // transaction2 occured later
			{
				Color elementColour = recursivelyCheckStateVariableValueListColour(treeNode, transaction1, transaction2);
				if(!selectedTransaction1IsCurrent || !selectedTransaction2IsCurrent)
				{
					if(elementColour.equals(Color.MAGENTA))
						elementColour = Color.BLACK;
				}
				listModel2.addElement(new ListItem(variableString2, elementColour));
			}
			
		}
		
		// If this node is not expanded we cannot see and do not deal with its children
		if(!singleViewStateTree.isExpanded(new TreePath(treeNode.getPath())))
			return;
		
		Enumeration<DefaultMutableTreeNode> children = treeNode.children();
		
		while(children.hasMoreElements())
		{
			recursivelyFillStateVariableValueLists(children.nextElement(), listModel1, transaction1, listModel2, transaction2);
		}
		
		
	}
	
	/*
	 * Semi-Depreciated, now use "recursivelyFillStateVariableValueLists", which fills both lists at the same time, thus allowing comparison
	 */
	protected void recursivelyFillStateVariableValueList(DefaultMutableTreeNode treeNode, DefaultListModel listModel, GUID transaction)
	{
		final int maxValueLength = 50;
		String variableString;
		
		variableString = (String)store.getInstance(transaction, treeNode.getUserObject(), stateVariableValueRole);
		if(variableString == null || variableString.equals(""))
		{
			listModel.addElement(new ListItem("<Empty>", Color.BLACK)); // Need to add a string as the list culls empty ones TODO: maybe alter in future
		}
		else
		{
			if(variableString.length() > maxValueLength)
			{
				variableString = variableString.substring(0, maxValueLength);
				variableString += "...";
			}
			listModel.addElement(new ListItem(variableString, Color.BLACK));
		}
		
		
		// If this node is not expanded we cannot see and do not deal with its children
		if(!singleViewStateTree.isExpanded(new TreePath(treeNode.getPath())))
			return;
		
		Enumeration<DefaultMutableTreeNode> children = treeNode.children();
		
		while(children.hasMoreElements())
		{
			recursivelyFillStateVariableValueList(children.nextElement(), listModel, transaction);
		}
	}
	
	protected void swingRefreshStateVariableValueLists()
	{
		if(!java.awt.EventQueue.isDispatchThread())
		{
			System.err.println("Refresh state variable value lists called from non dispatch thread!");
		}
		
		if(selectedTransactionID1 != null && selectedTransactionID2 != null)
		{
			DefaultListModel newModel1 = new DefaultListModel();
			DefaultListModel newModel2 = new DefaultListModel();
			
			singleViewStateValueList1.setModel(newModel1);
			singleViewStateValueList2.setModel(newModel2);
			
			recursivelyFillStateVariableValueLists((DefaultMutableTreeNode)singleViewStateTree.getModel().getRoot(),
					newModel1, selectedTransactionID1, newModel2, selectedTransactionID2);
		}
		else
		{
			if(selectedTransactionID1 != null)
			{
				DefaultListModel newModel = new DefaultListModel();
				singleViewStateValueList1.setModel(newModel);
				recursivelyFillStateVariableValueList((DefaultMutableTreeNode)singleViewStateTree.getModel().getRoot(), newModel, selectedTransactionID1);
			}
			else if(selectedTransactionID2 != null)
			{
				DefaultListModel newModel = new DefaultListModel();
				singleViewStateValueList2.setModel(newModel);
				recursivelyFillStateVariableValueList((DefaultMutableTreeNode)singleViewStateTree.getModel().getRoot(), newModel, selectedTransactionID2);
			}
		}
	}
	
	protected void swingRefreshValueAreaText()
	{
		String variableValue;
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)singleViewStateTree.getLastSelectedPathComponent();

		if (treeNode == null)
			return;
		
		GUID transaction = store.begin();
		
		variableValue = (String)store.getInstance(selectedTransactionID1, treeNode.getUserObject(), stateVariableValueRole);
		if(variableValue == null || variableValue.equals(""))
		{
			singleViewValueArea1.setText("<Empty>");
		}
		else
		{
			singleViewValueArea1.setText(variableValue);
		}
		
		variableValue = (String)store.getInstance(selectedTransactionID2, treeNode.getUserObject(), stateVariableValueRole);
		if(variableValue == null || variableValue.equals(""))
		{
			singleViewValueArea2.setText("<Empty>");
		}
		else
		{
			singleViewValueArea2.setText(variableValue);
		}
		
		store.commit(transaction);
	}
	
	private String getStateName(GUID transaction, GUID stateVariableGUID)
	{
		if(transaction == null || stateVariableGUID == null)
			return null;
		
		String nameString = "";
		
		nameString += store.getName(transaction, stateVariableGUID);
		
		String parentName = getStateParentName(transaction, stateVariableGUID);
		
		if(parentName != null)
			nameString = parentName + nameString;
		
		return nameString;
	}
	
	// Does not check to see if parent exists
	private String getStateParentName(GUID transaction, GUID stateVariableGUID)
	{
		if(store.getInstance(transaction, stateVariableGUID, childStateVariableRole.getPartner()) != null)
			return getStateName(transaction, (GUID)store.getInstance(transaction, stateVariableGUID, childStateVariableRole.getPartner()));
		
		return null;
	}
	
	protected void updateStatusBar(String newStatus)
	{		
		if(newStatus != null)
		{
			statusBarText = newStatus;
		}
		
		String debuggerStatusString;
		if(debugProtocol == null)
		{
			debuggerStatusString = " | Debugger Not Connected";
		}
		else
		{
			DebuggerProtocol.DebuggerStatus debuggerStatus = debugProtocol.getStatus();
			
			if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_DEAD)
			{
				debuggerStatusString = " | Debugger Dead";
			}
			else if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_RUNNING)
			{
				debuggerStatusString = " | Debugger Running";
			}
			else if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_STARTED)
			{
				debuggerStatusString = " | Debugger Started";
			}
			else if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_STOPPED)
			{
				debuggerStatusString = " | Debugger Stopped";
			}
			else // (debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_INITIALISED)
			{
				debuggerStatusString = " | Debugger Initialised";
			}
		}

		//statusBarLabel.setText(testString);
		// This fucks up if the text changes from what it was previously and causes sizing issues with the other GUI components
		// it does this even if the label isn't added to the frame... WHY!?!!?!?!?
		statusBarLabel.setText(statusBarText + debuggerStatusString);
	}
	
	protected void updateEnabledButtons(DebuggerProtocol.DebuggerStatus status)
	{
		if(debugProtocol == null)
		{
			singleViewRunButton.setEnabled(false);
			singleViewStopButton.setEnabled(false);
			singleViewContinueButton.setEnabled(false);
			singleViewRequestStateButton.setEnabled(false);
			singleViewKillButton.setEnabled(true);
			singleViewBreakpointButton.setEnabled(false);
		}
		else
		{
			DebuggerProtocol.DebuggerStatus debuggerStatus = debugProtocol.getStatus();
			
			if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_DEAD)
			{
				singleViewRunButton.setEnabled(false);
				singleViewStopButton.setEnabled(false);
				singleViewContinueButton.setEnabled(false);
				singleViewRequestStateButton.setEnabled(false);
				singleViewKillButton.setEnabled(true);
				singleViewBreakpointButton.setEnabled(false);
			}
			else if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_RUNNING)
			{
				singleViewRunButton.setEnabled(false);
				singleViewStopButton.setEnabled(true);
				singleViewContinueButton.setEnabled(false);
				singleViewRequestStateButton.setEnabled(false);
				singleViewKillButton.setEnabled(true);
				singleViewBreakpointButton.setEnabled(true);
			}
			else if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_STARTED)
			{
				singleViewRunButton.setEnabled(false);
				singleViewStopButton.setEnabled(true);
				singleViewContinueButton.setEnabled(false);
				singleViewRequestStateButton.setEnabled(false);
				singleViewKillButton.setEnabled(true);
				singleViewBreakpointButton.setEnabled(true);
			}
			else if(debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_STOPPED)
			{
				singleViewRunButton.setEnabled(false);
				singleViewStopButton.setEnabled(false);
				singleViewContinueButton.setEnabled(true);
				singleViewRequestStateButton.setEnabled(true);
				singleViewKillButton.setEnabled(true);
				singleViewBreakpointButton.setEnabled(true);
			}
			else // (debuggerStatus == DebuggerProtocol.DebuggerStatus.DEBUGGER_STATUS_INITIALISED)
			{
				singleViewRunButton.setEnabled(true);
				singleViewStopButton.setEnabled(false);
				singleViewContinueButton.setEnabled(false);
				singleViewRequestStateButton.setEnabled(false);
				singleViewKillButton.setEnabled(true);
				singleViewBreakpointButton.setEnabled(true);
			}
		}
	}
	
	protected void setupSingleViewLeftPane()
	{
		singleViewLeftPane = new JPanel();
		singleViewSessionSelectPane = new JPanel();
		singleViewFilterRadioButtonPane = new JPanel();
		singleViewStatesListPane = new JPanel();

		
		singleViewSessionComboBox = new JComboBox();
		singleViewSessionComboBox.setPrototypeDisplayValue("IAmASessionThatHasSelectedAVeryLongName"); // Set value to allow for long session names
		
		ButtonGroup filterButtonGroup = new ButtonGroup();
		singleViewFilterToStateRadioButton = new JRadioButton("State");
		singleViewFilterToCallRadioButton = new JRadioButton("GL Calls");
		singleViewFilterToAllRadioButton = new JRadioButton("All");
		singleViewFilterToStateRadioButton.addActionListener(eventHandler);
		singleViewFilterToCallRadioButton.addActionListener(eventHandler);
		singleViewFilterToAllRadioButton.addActionListener(eventHandler);
		singleViewFilterToStateRadioButton.setSelected(true);
		filterButtonGroup.add(singleViewFilterToStateRadioButton);
		filterButtonGroup.add(singleViewFilterToCallRadioButton);
		filterButtonGroup.add(singleViewFilterToAllRadioButton);
		
		singleViewFilterField = new JTextField("", 20);
		
		// Create the layout for the left view pane
		singleViewLeftLayout = new BorderLayout();
		singleViewLeftPane.setLayout(singleViewLeftLayout);
		
		// Create the session selection pane and layout
		singleViewSessionSelectLayout = new BoxLayout(singleViewSessionSelectPane, BoxLayout.Y_AXIS);
		singleViewSessionSelectPane.setLayout(singleViewSessionSelectLayout);
		
		// Add session drop box to left hand pane
		JLabel label = new JLabel("Session:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		singleViewSessionSelectPane.add(label);
		
		singleViewSessionComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		singleViewSessionSelectPane.add(singleViewSessionComboBox);
		
		singleViewFilterRadiobuttonLayout = new BoxLayout(singleViewFilterRadioButtonPane, BoxLayout.X_AXIS);
		singleViewFilterRadioButtonPane.setLayout(singleViewFilterRadiobuttonLayout);
		
		singleViewFilterRadioButtonPane.add(singleViewFilterToStateRadioButton);
		singleViewFilterRadioButtonPane.add(singleViewFilterToCallRadioButton);
		singleViewFilterRadioButtonPane.add(singleViewFilterToAllRadioButton);
		
		label = new JLabel("Filter by type:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		singleViewSessionSelectPane.add(label);
		singleViewSessionSelectPane.add(singleViewFilterRadioButtonPane);
		
		label = new JLabel("Filter Expression (regex):");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		singleViewSessionSelectPane.add(label);
		
		singleViewFilterField.setAlignmentX(Component.CENTER_ALIGNMENT);
		singleViewSessionSelectPane.add(singleViewFilterField);	
		
		singleViewStateListScrollPane = new JScrollPane(singleViewStatesListPane);
		singleViewStateListLayout = new BoxLayout(singleViewStatesListPane, BoxLayout.Y_AXIS);
		singleViewStatesListPane.setLayout(singleViewStateListLayout);
		// Need to set the bounds on the scrollPane otherwise Java ends up making the scroll pane vastly too large when it has large string in it
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		if(screenDimensions.getWidth() < 600)
		{
			// really small screens like smaller bounds
			singleViewStateListScrollPane.setPreferredSize(new Dimension(200, 400));
		}
		else
		{
			singleViewStateListScrollPane.setPreferredSize(new Dimension(300, 800));
		}
		
		singleViewLeftPane.add(singleViewSessionSelectPane, BorderLayout.NORTH);
		singleViewLeftPane.add(singleViewStateListScrollPane, BorderLayout.CENTER);
	}
	
	protected void setupSingleViewMiddlePane()
	{
		singleViewMiddlePane = new JPanel(); // Top level pane for all data in the middle of the view
		singleViewStateVariablePane = new JPanel(); // Pane containing the tree area and detailed value view areas below it
		singleViewTreeDataPane = new JPanel();
		singleViewStateVariableListPane = new JPanel();
		
		// Create the pane for the elemnts in the middle of the view
		singleViewMiddleLayout = new BoxLayout(singleViewMiddlePane, BoxLayout.Y_AXIS);
		singleViewMiddlePane.setLayout(singleViewMiddleLayout);
		

		singleViewStateTree = new StateJTree();
		singleViewStateValueList1 = new JList();
		singleViewStateValueList2 = new JList();
		
		// Make list elements not selectable	 
		singleViewStateValueList1.setCellRenderer(new DefaultListCellRenderer()
		{
		    public Component getListCellRendererComponent(JList list, Object value, int index,
		            boolean isSelected, boolean cellHasFocus) {
		    	ListItem item = (ListItem)value;
		    	
		        super.getListCellRendererComponent(list, item.text, index, false, false);
		    	setForeground(item.colour);
		 
		        return this;
		    }
		});
		
		singleViewStateValueList2.setCellRenderer(new DefaultListCellRenderer()
		{
		    public Component getListCellRendererComponent(JList list, Object value, int index,
		            boolean isSelected, boolean cellHasFocus) {
		    	ListItem item = (ListItem)value;
		    	
		        super.getListCellRendererComponent(list, item.text, index, false, false);
		        setForeground(item.colour);
		 
		        return this;
		    }
		});
		
		singleViewStateVariableListLayout = new BoxLayout(singleViewStateVariableListPane, BoxLayout.X_AXIS);
		singleViewStateVariableListPane.setLayout(singleViewStateVariableListLayout);
		singleViewStateVariableListPane.setBackground(Color.WHITE);
		singleViewStateVariableListPane.add(singleViewStateValueList1);
		singleViewStateVariableListPane.add(singleViewStateValueList2);
			
		// Add the action listeners to the selectors and the tree
		singleViewSessionComboBox.addActionListener(eventHandler);
		singleViewFilterField.addActionListener(eventHandler);
		singleViewStateTree.addTreeExpansionListener(eventHandler);
		singleViewStateTree.addTreeSelectionListener(eventHandler);
		
		// Set up the value areas at the bottom of the central pane
		singleViewValueArea1 = new JTextArea();
		singleViewValueArea1.setRows(5);
		singleViewValueArea2 = new JTextArea();
		singleViewValueArea2.setRows(5);
		singleViewValueArea1ScrollPane = new JScrollPane(singleViewValueArea1);
		singleViewValueArea2ScrollPane = new JScrollPane(singleViewValueArea2);
		singleViewValueSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, singleViewValueArea1ScrollPane, singleViewValueArea2ScrollPane);
		singleViewValueSplitPane.setResizeWeight(0.5); // Extra space is allocated evenly between the split components
		
		singleViewTreeDataLayout = new BorderLayout();
		singleViewTreeDataPane.setLayout(singleViewTreeDataLayout);
		
		singleViewTreeDataPane.add(singleViewStateTree, BorderLayout.WEST);
		singleViewTreeDataPane.add(singleViewStateVariableListPane, BorderLayout.CENTER);
		
		singleViewTreeScrollPane = new JScrollPane(singleViewTreeDataPane);
		singleViewTreeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		singleViewVariableViewLayout = new BorderLayout();
		singleViewStateVariablePane.setLayout(singleViewVariableViewLayout);
		
		singleViewStateVariablePane.add(singleViewTreeScrollPane, BorderLayout.CENTER);
		singleViewStateVariablePane.add(singleViewValueSplitPane, BorderLayout.SOUTH);
		
		singleViewMiddlePane.add(singleViewStateVariablePane);
	}
	
	protected void setupSingleView()
	{
		singleViewTopLevelPane = new JPanel();
		
		// Create the layout for the top level pane
		singleViewTopLevelLayout = new BorderLayout();
		singleViewTopLevelPane.setLayout(singleViewTopLevelLayout);	
		
		singleViewButtonsPane = new JPanel();
		singleViewButtonsPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		singleViewRunButton = new JButton("Run");
		singleViewStopButton = new JButton("Stop");
		singleViewContinueButton = new JButton("Continue");
		singleViewRequestStateButton = new JButton("Request State");
		singleViewBreakpointButton = new JButton("Breakpoints");
		singleViewKillButton = new JButton("Kill");
		singleViewConnectButton =  new JButton("Connect");
		
		singleViewIPField = new JTextField("", 14);
		singleViewPortField = new JTextField("", 8 );
		
		singleViewButtonsLayout = new FlowLayout();
		singleViewButtonsPane.setLayout(singleViewButtonsLayout);
		singleViewButtonsPane.add(singleViewRunButton);
		singleViewButtonsPane.add(singleViewStopButton);
		singleViewButtonsPane.add(singleViewContinueButton);
		singleViewButtonsPane.add(singleViewRequestStateButton);
		singleViewButtonsPane.add(singleViewKillButton);
		singleViewButtonsPane.add(singleViewBreakpointButton);
		singleViewButtonsPane.add(new JLabel("Address:"));
		singleViewButtonsPane.add(singleViewIPField);
		singleViewButtonsPane.add(new JLabel(":"));
		singleViewButtonsPane.add(singleViewPortField);
		singleViewButtonsPane.add(singleViewConnectButton);
		
		singleViewRunButton.addActionListener(eventHandler);
		singleViewStopButton.addActionListener(eventHandler);
		singleViewContinueButton.addActionListener(eventHandler);
		singleViewRequestStateButton.addActionListener(eventHandler);
		singleViewBreakpointButton.addActionListener(eventHandler);
		singleViewKillButton.addActionListener(eventHandler);
		singleViewConnectButton.addActionListener(eventHandler);
		
		setupSingleViewLeftPane();
		setupSingleViewMiddlePane();
		
		singleViewStatusBarPane = new JPanel();
		singleViewStatusBarLayout = new BoxLayout(singleViewStatusBarPane, BoxLayout.X_AXIS);
		singleViewStatusBarPane.setLayout(singleViewStatusBarLayout);
		statusBarLabel = new JLabel();
		statusBarLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		singleViewStatusBarPane.add(statusBarLabel);
		
		statusBarLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		// Add the middle pane to the top level pane
		singleViewTopLevelPane.add(singleViewButtonsPane, BorderLayout.NORTH);
		singleViewTopLevelPane.add(singleViewMiddlePane, BorderLayout.CENTER);
		singleViewTopLevelPane.add(singleViewLeftPane, BorderLayout.WEST);
		singleViewTopLevelPane.add(singleViewStatusBarPane, BorderLayout.SOUTH);
		singleViewStatusBarPane.setMaximumSize(new Dimension(400, 1080));
		//singleViewMiddlePane.add(singleViewStatusBarPane);
		
		
		if(!java.awt.EventQueue.isDispatchThread())
		{	
			try {
				java.awt.EventQueue.invokeAndWait(new Runnable()
				{
					public void run()
					{  	
						updateStatusBar("GLDebug Started");
						updateEnabledButtons(null);
						swingRefreshSessionComboBox();
						swingRefreshStateVariableTree();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}
		
		updateStatusBar("GLDebug Started");
		updateEnabledButtons(null);
		swingRefreshSessionComboBox();
		swingRefreshStateVariableTree();	
	}

	public GLDebug(PDStore store, GUID historyID)
	{
		this.store = store;
		
		runningSessionName = null;
		runningSessionStartTime = null;
		
		ignoreActionEvents = false;
		
		debugProtocol = null;
		debugListenerThread = null;
		clientSocket = null;
		seq = 0;
		
		statusBarText = "";
		
		selectedTransactionID1 = null;
		selectedTransactionID2 = null;
		
		eventHandler = new EventHandler();
		
		windowFrame = new JFrame("GLDebug");
		windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setupSingleView();
		
		windowFrame.getContentPane().add(singleViewTopLevelPane);
		
		breakpointDialog = new BreakpointDialog(windowFrame);

		// Prefer setting the window size, as pack makes the window rather small due to empty and scrollable components
		windowFrame.setSize(1024, 600);
		windowFrame.setVisible(true);
	}

	public static void main(String[] args)
	{
		try {
			Class.forName("diagrameditor.dal.PDHistory");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		
		GUID historyID;
		
		mainStore = new PDStore("MyGlStateDatabase");
		historyID = new GUID();

		GLDebug debug1 = new GLDebug(mainStore, historyID);
		//GLDebug debug2 = new GLDebug("B", workingCopy2, historyID);
	}
}
