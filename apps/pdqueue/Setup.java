package pdqueue;

import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

import pdqueue.concurrency.QueueOperation;
import pdqueue.dal.PDItem;
import pdqueue.dal.PDQueue;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.dal.PDGenericInstance;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;

public class Setup {
	
	

	static GUID queueGUID = new GUID("cd3208000f1a11e183d00024e80616c7");
	public static void main(String args[]) {
		insertQueueInstance();
		verification();
	}
	
	public static void insertQueueInstance() {
		PDStore store = new PDStore("QueueBase");
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		
		GUID t = store.begin(store.getRepository(), IsolationLevel.READ_UNCOMMITTED);
		copy.setTransactionId(t);
		PDQueue requestQueue = new PDQueue(copy);
		PDQueue replyQueue = new PDQueue(copy);
		requestQueue.setName("Request");
		requestQueue.setServiceType("Request");
		replyQueue.setName("Reply");
		replyQueue.setServiceType("Reply");
		//copy.commit();
		store.commit(t);
	}
	
	public static void verification() { 
		QueueOperation qo = new QueueOperation();
		
	}
	
}
