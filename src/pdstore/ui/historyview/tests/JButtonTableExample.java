package pdstore.ui.historyview.tests;

// File: JButtonTableExample.java
/* (swing1.1beta3) */
//package jp.gr.java_conf.tame.swing.examples;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


//import jp.gr.java_conf.tame.swing.table.*;


/**
 * @version 1.0 11/09/98
 */
public class JButtonTableExample extends JFrame {

  public JButtonTableExample(){
    super( "JButtonTable Example" );
    
    DefaultTableModel dm = new DefaultTableModel();
    dm.setDataVector(new Object[][]{{"button 1","foo"},
                                    {"button 2","bar"}},
                     new Object[]{"Button","String"});
           
    JTable table = new JTable(dm);

    JPanelRender jr = new JPanelRender();
    table.getColumn("Button").setCellRenderer(jr);
    
    JScrollPane scroll = new JScrollPane(table);
    getContentPane().add( scroll );
    setSize( 400, 100 );
    setVisible(true);
  }
  
  
  public static void main(String[] args) {
    JButtonTableExample frame = new JButtonTableExample();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }
}