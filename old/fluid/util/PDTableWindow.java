package fluid.util;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import fluid.table.PDTableModel;

import aim.tablewidget.DefaultTableModelHandler;
import aim.tablewidget.TableWidget;

import pdstore.GUID;
import pdstore.PDStore;

public class PDTableWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PDStore store;
	private GUID instanceType;
	private Collection<Object> instancesOfType;
	private String tableName = "";
	
	public PDTableWindow (PDStore s, GUID type,Collection<Object> Instances){
		setTable(s,type,Instances);
	}
	
	public void setTableName(String name){
		tableName = name;
	}
	
	public void setTable(PDStore s, GUID type,Collection<Object> Instances){
		store = s;
		instanceType = type;
		instancesOfType = Instances;
		GUID transaction = store.begin();
		this.setTitle(""+store.getName(transaction, type));
		store.commit(transaction);
		clearPane();
	}
	
	private void clearPane(){
		getContentPane().removeAll();
	}
	
	public void setVisible(boolean isVis){
		PDTableModel table = new PDTableModel(instanceType, instancesOfType, store);
		DefaultTableModelHandler handler = new DefaultTableModelHandler(table);
		TableWidget widget = new TableWidget(handler);
		widget.setTitleLabel(tableName);
		getContentPane().add(widget);
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		super.setVisible(isVis);
		this.pack();
		if(this.getSize().width>500){
			this.setSize(500, this.getSize().height);
		}
		if(this.getSize().height > 300){
			this.setSize(this.getSize().width,300);
		}
	}
	

}
