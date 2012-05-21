/*
 * MainFrame.java
 *
 * Created on 4/03/2011, 12:29:53 AM
 */

package pdstore.ui.historyview;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.OutlineModel;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;
import pdstore.notify.PDListenerAdapter;

/**
 * This class defines the main GUI window of PDStore
 *
 */
public class PDHistoryView extends javax.swing.JFrame implements PropertyChangeListener{

	final static String iconPath = "icons/historyview/"; 
	
	DefaultListModel eventLogModel = new javax.swing.DefaultListModel();
	TreeTableModel treeMdl = new TreeTableModel(); 


	/**
     * The default parameterless constructor of MainFrame.
     *
     */
    public PDHistoryView() {
        initComponents();
      //Add the Outline object to the JScrollPane:

        jScrollPane1.setViewportView(outline1);
        
    }

     /**
      * This method is called from within the constructor to initialize the form.
      */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    	//Outline (TreeTable)Stuff
    	       
        OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, new TreeRowModel(outline1), true, "PDStore");
        outline1 = new MultiOutline();
        outline1.setModel(mdl);
        outline1.setRootVisible(false);

        jDialog1 = new javax.swing.JDialog();
        EventLogFrame = new javax.swing.JFrame();
        EventLogScrollPane = new javax.swing.JScrollPane();
        EventLog = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        Open = new javax.swing.JButton();
        New = new javax.swing.JButton();
        Commit = new javax.swing.JButton();
        Datamine = new javax.swing.JButton();
        Recovery = new javax.swing.JButton();
        Serve = new javax.swing.JButton();
        Shelve = new javax.swing.JButton();
        LoadMore = new javax.swing.JButton();
        LoadAll = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        //outline1 = new org.netbeans.swing.outline.Outline();
        outline1 = new MultiOutline();
        jMenuBar1 = new javax.swing.JMenuBar();

		fileMenu 		= new javax.swing.JMenu("File");
		newMenuItem 	= new javax.swing.JMenuItem("New");
		openMenuItem 	= new javax.swing.JMenuItem("Open");
		exitMenuItem 	= new javax.swing.JMenuItem("Exit");
        
        Tools = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        View = new javax.swing.JMenu();
        ToolBarCheckBox = new javax.swing.JCheckBoxMenuItem();
        SyncBarCheckBox = new javax.swing.JCheckBoxMenuItem();
        FilterBarCheckBox = new javax.swing.JCheckBoxMenuItem();
        EventLogCheckBox = new javax.swing.JCheckBoxMenuItem();
        Navigate = new javax.swing.JMenu();
        Synchronize = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        Filter = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem6 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem7 = new javax.swing.JRadioButtonMenuItem();
        Help = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        EventLogFrame.setTitle("Event Log");
        EventLogFrame.setMinimumSize(new java.awt.Dimension(300, 600));

        EventLogScrollPane.setMinimumSize(new java.awt.Dimension(300, 600));
        EventLogScrollPane.setPreferredSize(new java.awt.Dimension(300, 600));

        EventLog.setModel(eventLogModel);
        EventLog.setMinimumSize(new java.awt.Dimension(300, 600));
        EventLog.setPreferredSize(new java.awt.Dimension(300, 600));
        EventLogScrollPane.setViewportView(EventLog);

        javax.swing.GroupLayout EventLogFrameLayout = new javax.swing.GroupLayout(EventLogFrame.getContentPane());
        EventLogFrame.getContentPane().setLayout(EventLogFrameLayout);
        EventLogFrameLayout.setHorizontalGroup(
            EventLogFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(EventLogScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
        );
        EventLogFrameLayout.setVerticalGroup(
            EventLogFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(EventLogScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PDStore Repository Explorer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                EventLogClosed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setAlignmentX(0.0F);
        jPanel1.setAlignmentY(0.0F);

        Open.setIcon(new javax.swing.ImageIcon(iconPath + "OpenPDStore.png")); // NOI18N
        Open.setText("Open");
        Open.setToolTipText("Open Exsiting PD Store Data File");
        Open.setAlignmentY(0.0F);
        Open.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Open.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Open.setMaximumSize(new java.awt.Dimension(49, 67));
        Open.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenActionPerformed(evt);
            }
        });
        
        New.setIcon(new javax.swing.ImageIcon(iconPath + "NewPDStore.png")); // NOI18N
        New.setText("New");
        New.setToolTipText("Create New PD Store");
        New.setAlignmentY(0.0F);
        New.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        New.setMargin(new java.awt.Insets(0, 0, 0, 0));
        New.setMaximumSize(new java.awt.Dimension(49, 67));
        New.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        New.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewActionPerformed(evt);
            }
        });

        Commit.setIcon(new javax.swing.ImageIcon(iconPath + "Commit.png")); // NOI18N
        Commit.setText("Commit");
        Commit.setToolTipText("Commit");
        Commit.setAlignmentY(0.0F);
        Commit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Commit.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Commit.setMaximumSize(new java.awt.Dimension(49, 67));
        Commit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Commit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CommitActionPerformed(evt);
            }
        });

        Datamine.setIcon(new javax.swing.ImageIcon(iconPath + "Datamine.png")); // NOI18N
        Datamine.setText("Datamine");
        Datamine.setToolTipText("Datamine");
        Datamine.setAlignmentY(0.0F);
        Datamine.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Datamine.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Datamine.setMaximumSize(new java.awt.Dimension(49, 67));
        Datamine.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Datamine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DatamineActionPerformed(evt);
            }
        });

        Recovery.setIcon(new javax.swing.ImageIcon(iconPath + "Recovery.png")); // NOI18N
        Recovery.setText("Recovery");
        Recovery.setToolTipText("Recovery");
        Recovery.setAlignmentY(0.0F);
        Recovery.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Recovery.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Recovery.setMaximumSize(new java.awt.Dimension(49, 67));
        Recovery.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Recovery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RecoveryActionPerformed(evt);
            }
        });

        Serve.setIcon(new javax.swing.ImageIcon(iconPath + "Serve.png")); // NOI18N
        Serve.setText("Serve");
        Serve.setToolTipText("Serve");
        Serve.setAlignmentY(0.0F);
        Serve.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Serve.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Serve.setMaximumSize(new java.awt.Dimension(49, 67));
        Serve.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Serve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ServeActionPerformed(evt);
            }
        });

        Shelve.setIcon(new javax.swing.ImageIcon(iconPath + "Shelve.png")); // NOI18N
        Shelve.setText("Shelve");
        Shelve.setToolTipText("Shelve");
        Shelve.setAlignmentY(0.0F);
        Shelve.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Shelve.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Shelve.setMaximumSize(new java.awt.Dimension(49, 67));
        Shelve.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Shelve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShelveActionPerformed(evt);
            }
        });

        LoadMore.setIcon(new javax.swing.ImageIcon(iconPath + "Loadmore.png")); // NOI18N
        LoadMore.setText("Load More");
        LoadMore.setToolTipText("123");
        LoadMore.setAlignmentY(0.0F);
        LoadMore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LoadMore.setMargin(new java.awt.Insets(0, 0, 0, 0));
        LoadMore.setMaximumSize(new java.awt.Dimension(49, 67));
        LoadMore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LoadMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadMoreActionPerformed(evt);
            }
        });

        LoadAll.setIcon(new javax.swing.ImageIcon(iconPath + "Loadall.png")); // NOI18N
        LoadAll.setText("Load All");
        LoadAll.setToolTipText("123");
        LoadAll.setAlignmentY(0.0F);
        LoadAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LoadAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
        LoadAll.setMaximumSize(new java.awt.Dimension(49, 67));
        LoadAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LoadAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(Open, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(New, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Commit, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Datamine, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Recovery, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Serve, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Shelve, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                .addComponent(LoadMore, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LoadAll, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Open, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(New, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Commit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Datamine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Recovery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Serve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Shelve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LoadAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LoadMore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("List what files have changed here");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addContainerGap(300, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel2)
                .addContainerGap(314, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setToolTipText("");

        jLabel1.setText("Changeset details go here");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addContainerGap(332, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addContainerGap(314, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(outline1);

        Tools.setText("Tools");

        jMenuItem1.setText("Repository Explorer");
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem1MouseClicked(evt);
            }
        });
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        Tools.add(jMenuItem1);

        jMenuItem2.setText("Commit");
        Tools.add(jMenuItem2);

        jMenuItem6.setText("Datamine");
        Tools.add(jMenuItem6);

        jMenuItem7.setText("Recovery");
        Tools.add(jMenuItem7);

        jMenuItem8.setText("Serve");
        Tools.add(jMenuItem8);

        jMenuItem9.setText("Shelve");
        Tools.add(jMenuItem9);

        jMenuItem10.setText("Synchronize");
        Tools.add(jMenuItem10);

        jMenuItem11.setText("Settings");
        Tools.add(jMenuItem11);

        // Following block added.  Builds "File" menu.  - Moiz
        jMenuBar1.add(fileMenu);		
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(exitMenuItem);
        
        newMenuItem.setToolTipText("Create New PD Store");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewActionPerformed(evt);
            }
        });

        openMenuItem.setToolTipText("Open Existing PD Store Data File");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenActionPerformed(evt);
            }
        });

        exitMenuItem.setToolTipText("Close PD Store");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		for (Frame frame : Frame.getFrames())
        		{
        			if (frame.isActive())
        			{
        				WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
        				frame.dispatchEvent(windowClosing);
        			}
        		}
            }
        });
        
        

       
        jMenuBar1.add(Tools);

        View.setText("View");

        ToolBarCheckBox.setSelected(true);
        ToolBarCheckBox.setText("Tool bar");
        View.add(ToolBarCheckBox);

        SyncBarCheckBox.setSelected(true);
        SyncBarCheckBox.setText("Sync bar");
        View.add(SyncBarCheckBox);

        FilterBarCheckBox.setSelected(true);
        FilterBarCheckBox.setText("Filter bar");
        View.add(FilterBarCheckBox);

        EventLogCheckBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        EventLogCheckBox.setText("Event Log");
        EventLogCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EventLogCheckBoxActionPerformed(evt);
            }
        });
        View.add(EventLogCheckBox);

        jMenuBar1.add(View);

        Navigate.setText("Navigate");
        jMenuBar1.add(Navigate);

        Synchronize.setText("Synchronize");

        jMenuItem12.setText("Incoming");
        Synchronize.add(jMenuItem12);

        jMenuItem13.setText("Pull");
        Synchronize.add(jMenuItem13);

        jMenuItem14.setText("Outgoing");
        Synchronize.add(jMenuItem14);

        jMenuItem15.setText("Push");
        Synchronize.add(jMenuItem15);

        jMenuItem16.setText("Email");
        Synchronize.add(jMenuItem16);

        jMenuItem17.setText("Import");
        Synchronize.add(jMenuItem17);

        jMenuItem18.setText("Add bundle");
        Synchronize.add(jMenuItem18);

        jMenuBar1.add(Synchronize);

        Filter.setText("Filter");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("All");
        Filter.add(jRadioButtonMenuItem1);

        jRadioButtonMenuItem2.setText("Tagged");
        Filter.add(jRadioButtonMenuItem2);

        jRadioButtonMenuItem3.setText("Ancestry");
        Filter.add(jRadioButtonMenuItem3);

        jRadioButtonMenuItem4.setText("Parents");
        Filter.add(jRadioButtonMenuItem4);

        jRadioButtonMenuItem5.setText("Heads");
        Filter.add(jRadioButtonMenuItem5);

        jRadioButtonMenuItem6.setText("Merges");
        Filter.add(jRadioButtonMenuItem6);

        jRadioButtonMenuItem7.setText("Branch");
        Filter.add(jRadioButtonMenuItem7);

        jMenuBar1.add(Filter);

        Help.setText("Help");

        jMenuItem3.setText("Content");
        Help.add(jMenuItem3);

        jMenuItem4.setText("Index");
        Help.add(jMenuItem4);

        jMenuItem5.setText("About");
        Help.add(jMenuItem5);

        jMenuBar1.add(Help);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    /**
     * The event gets fired when Open button is clicked
     */
    private void OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenActionPerformed
        eventLogModel.addElement("OPEN "+ new Timestamp(new java.util.Date().getTime()));
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Open PDStore Files...");
        
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileChooserFilter());

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
        	// Debug log...
        	eventLogModel.addElement("Opened file :  " + fileChooser.getSelectedFile() + " " + new Timestamp(new java.util.Date().getTime()));
        	
        	PDStore store = new PDStore(Utils.RemoveFileExtension(fileChooser.getSelectedFile().getName()));

        	treeMdl = new TreeTableModel(store);
        	treeMdl.addPropertyChangeListener(this);
        	treeMdl.execute();
        	outline1.tmodel = treeMdl;
            OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, new TreeRowModel(outline1), true, "PDStore");
            outline1.setModel(mdl);
            
            //Add the Outline object to the JScrollPane:
            jScrollPane1.setViewportView(outline1);
    		
    		// add detached listener
    		List<PDListener<GUID,Object,GUID>> listeners = store.getDetachedListenerList();
    		listeners.add(new PDListenerAdapter<GUID,Object,GUID>(){
    			public void transactionCommitted(
    					List<PDChange<GUID, Object, GUID>> transaction,
    					List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core){
    				for (PDChange<GUID, Object, GUID> change : transaction)
    					System.out.println(change);
    			}
    		});		
    		
    		// create a new branch for testing purposes.....
    		// store.branch(new GUID("00000000000000000000d560593fbaec"));
        }
        
        else 
        {
        	// Debug log...
        	eventLogModel.addElement("User clicked cancel, no files were selected" + " " + new Timestamp(new java.util.Date().getTime()));
        }
    }//GEN-LAST:event_OpenActionPerformed

    /**
     * The event gets fired when New button is clicked
     */
    private void NewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewActionPerformed
    	// Debug log...
        eventLogModel.addElement("New  "+ new Timestamp(new java.util.Date().getTime()));
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Create New PDStore File...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileChooserFilter());

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
        	PDStore store = new PDStore(fileChooser.getSelectedFile().getName());
        	
        	// add detached listener
    		List<PDListener<GUID,Object,GUID>> listeners = store.getDetachedListenerList();
    		listeners.add(new PDListenerAdapter<GUID,Object,GUID>(){
    			public void transactionCommitted(
    					List<PDChange<GUID, Object, GUID>> transaction,
    					List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core){
    				for (PDChange<GUID, Object, GUID> change : transaction)
    					System.out.println(change);
    			}
    		});

        	eventLogModel.addElement("Created PD Store "+ fileChooser.getSelectedFile() + new Timestamp(new java.util.Date().getTime()));
        }
        else
        {
        	// Debug log...
        	eventLogModel.addElement("User clicked cancel, no files were selected" + " " + new Timestamp(new java.util.Date().getTime()));
        }
    }//GEN-LAST:event_NewActionPerformed

    /**
     * The event gets fired when Commit button is clicked
     */
    private void CommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CommitActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("COMMIT "+ new Timestamp(new java.util.Date().getTime()));
        JOptionPane.showMessageDialog(null,"This feature is not implemented yet.");
    }//GEN-LAST:event_CommitActionPerformed

    private void DatamineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DatamineActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("DATA MINE "+ new Timestamp(new java.util.Date().getTime()));
    }//GEN-LAST:event_DatamineActionPerformed

    private void RecoveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecoveryActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("RECOVERY "+ new Timestamp(new java.util.Date().getTime()));
    }//GEN-LAST:event_RecoveryActionPerformed

    private void ServeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ServeActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("SERVE "+ new Timestamp(new java.util.Date().getTime()));
    }//GEN-LAST:event_ServeActionPerformed

    private void ShelveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShelveActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("SHELVE "+ new Timestamp(new java.util.Date().getTime()));
    }//GEN-LAST:event_ShelveActionPerformed

    private void LoadMoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadMoreActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("LOAD MORE "+ new Timestamp(new java.util.Date().getTime()));
    }//GEN-LAST:event_LoadMoreActionPerformed

    private void LoadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadAllActionPerformed
        // TODO add your handling code here:
        eventLogModel.addElement("LOAD ALL "+ new Timestamp(new java.util.Date().getTime()));
    }//GEN-LAST:event_LoadAllActionPerformed

    private void jMenuItem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1MouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void EventLogCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EventLogCheckBoxActionPerformed
        // TODO add your handling code here:
        if (EventLogCheckBox.isSelected()){
            EventLogFrame.setVisible(true);
        }
        else{
            EventLogFrame.setVisible(false);
        }
    }//GEN-LAST:event_EventLogCheckBoxActionPerformed

    private void EventLogClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_EventLogClosed
        // TODO add your handling code here:
        EventLogCheckBox.setSelected(false);
        EventLogCheckBox.setState(false);
    }//GEN-LAST:event_EventLogClosed

    /**
     * The entry main method 
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PDHistoryView().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Commit;
    private javax.swing.JButton Datamine;
    private javax.swing.JList EventLog;
    private javax.swing.JCheckBoxMenuItem EventLogCheckBox;
    private javax.swing.JFrame EventLogFrame;
    private javax.swing.JScrollPane EventLogScrollPane;
    private javax.swing.JMenu Filter;
    private javax.swing.JCheckBoxMenuItem FilterBarCheckBox;
    private javax.swing.JMenu Help;
    private javax.swing.JButton LoadAll;
    private javax.swing.JButton LoadMore;
    private javax.swing.JMenu Navigate;
    private javax.swing.JButton Recovery;
    private javax.swing.JButton Open;
    private javax.swing.JButton New;
    private javax.swing.JButton Serve;
    private javax.swing.JButton Shelve;
    private javax.swing.JCheckBoxMenuItem SyncBarCheckBox;
    private javax.swing.JMenu Synchronize;
    private javax.swing.JCheckBoxMenuItem ToolBarCheckBox;
    private javax.swing.JMenu fileMenu;					//'Menu' convention used - Moiz
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    
    private javax.swing.JMenu Tools;
    private javax.swing.JMenu View;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem7;
    private javax.swing.JScrollPane jScrollPane1;
    //private org.netbeans.swing.outline.Outline outline1;
    private MultiOutline outline1;
    // End of variables declaration//GEN-END:variables

    /**
     * Repaints the main TreeTable Display when changes or transactions are added to the TreeTable model
     */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		jScrollPane1.repaint();	
	}

}
