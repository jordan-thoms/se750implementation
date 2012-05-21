package swiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import pdstore.GUID;
import pdstore.PDStore;

/**
 * Abstraction class to provide an easier interface between Swiki and PDStore
 */
public class SwikiStore implements Runnable {
	private static final Logger LOG = Logger.getLogger(SwikiStore.class);

	private static final String PAGETITLEFILE = "titles.dat";
	private static final String METAMODELFILE = "metamodel.dat";

	private static final GUID MODELID = new GUID("4b6812818e2511df864b001bfc4f09fd");
	private static final GUID MODEL_PAGE_ROLEID = new GUID("4b6812808e2511df864b001bfc4f09fd");
	private static final GUID TYPE_TYPEINSTANCE_ROLEID = new GUID(
			"4b68127f8e2511df864b001bfc4f09fd");
	private static final GUID TYPEINSTANCE_TYPE_ROLEID = new GUID(
			"4b68127d8e2511df864b001bfc4f09fd");
	private static final GUID PAGE_TYPEINSTANCE_ROLEID = new GUID(
			"4b68127e8e2511df864b001bfc4f09fd");

	private BlockingQueue<Object> queue;
	private BufferedWriter titleStore;

	private PDStore store;
	private GUID transaction;

	MetaInfo metaInfo;

	private int noEntries;

	public SwikiStore(BlockingQueue<Object> queue) {
		this.queue = queue;
		metaInfo = new MetaInfo();

		// Init page title store
		File titleFile = new File(PAGETITLEFILE);
		if (titleFile.exists()) {
			titleFile.delete();
		}
		try {
			titleFile.createNewFile();
			titleStore = new BufferedWriter(new FileWriter(titleFile));
		} catch (IOException e) {
			LOG.error(e, e);
		}

		// Init PDStore
		noEntries = 0;
		store = new PDStore("pdstore");
		transaction = store.begin();

		if (!store.instanceExists(transaction, MODELID)) {
			store.setName(transaction, MODELID, "Wikipedia");
		}
	}

	public void commit() {
		if (titleStore != null) {
			try {
				titleStore.close();
			} catch (IOException e) {
				LOG.error(e, e);
			}
		}

		if (metaInfo != null) {
			try {
				metaInfo.writeToFile();
			} catch (IOException e) {
				LOG.error(e, e);
			}
		}

		if (store != null) {
			store.commit(transaction);
		}
	}

	private void flush() {
		if (store != null) {
			store.commit(transaction);
			transaction = store.begin();
		}
	}

	private void storePage(PageInfo info) throws IOException {
		GUID pageId = createPage(info);

		Map<String, Map<String, String>> types = info.getTypes();
		for (String type : types.keySet()) {
			GUID typeId = metaInfo.addType(type);
			GUID typeInst = linkTypeToPage(pageId, typeId);
			Map<String, String> attrs = types.get(type);
			for (String attr : attrs.keySet()) {
				GUID attrId = metaInfo.addAttribute(attr);
				store.addLink(transaction, typeInst, attrId, attrs.get(attr));
			}
		}

		noEntries++;
	}

	private GUID linkTypeToPage(GUID pageId, GUID typeId) {
		GUID i = new GUID();
		store.addLink(transaction, pageId, PAGE_TYPEINSTANCE_ROLEID, i);
		store.addLink(transaction, i, TYPEINSTANCE_TYPE_ROLEID, typeId);
		store.addLink(transaction, typeId, TYPE_TYPEINSTANCE_ROLEID, i);
		return i;
	}

	private GUID createPage(PageInfo info) throws IOException {
		// Store page to PDStore
		GUID pageId = new GUID();
		store.setName(transaction, pageId, info.title);
		store.addLink(transaction, MODELID, MODEL_PAGE_ROLEID, pageId);

		// Write to file for checking
		String t = info.title;
		if (info.redirect != null)
			t += " -> " + info.redirect;
		titleStore.write(String.format("{%s} %s\n", pageId, t));

		LOG.debug("Page created => " + t);

		return pageId;
	}

	public void run() {
		try {
			while (true) {
				Object obj = queue.take();
				if (obj instanceof PageInfo) {
					storePage((PageInfo) obj);
				}

				if (noEntries % 1000 == 0) {
					flush();
				}
			}
		} catch (InterruptedException e) {
			LOG.debug("Shutting down SwikiStore");
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	private class MetaInfo {
		private Map<String, GUID> types;
		private Map<String, GUID> attributes;

		public MetaInfo() {
			types = new HashMap<String, GUID>();
			attributes = new HashMap<String, GUID>();
		}

		public GUID addType(String typeName) {
			GUID typeId = getTypeId(typeName);
			if (typeId == null) {
				typeId = new GUID();
				store.setName(transaction, typeId, typeName);
				types.put(typeName, typeId);
				LOG.debug("Type created => " + typeName);
			}

			return typeId;
		}

		public GUID getTypeId(String typeName) {
			return types.get(typeName);
		}

		public GUID addAttribute(String attributeName) {
			GUID attrId = getAttributeId(attributeName);
			if (attrId == null) {
				attrId = new GUID();
				store.setName(transaction, attrId, attributeName);
				attributes.put(attributeName, attrId);
			}
			return attrId;
		}

		public GUID getAttributeId(String attributeName) {
			return attributes.get(attributeName);
		}

		public void writeToFile() throws IOException {
			File metaFile = new File(METAMODELFILE);
			if (metaFile.exists()) {
				metaFile.delete();
			}
			metaFile.createNewFile();
			BufferedWriter metaStore = new BufferedWriter(new FileWriter(metaFile));

			for (String type : types.keySet()) {
				GUID id = types.get(type);
				metaStore.write(String.format("{%s} %s\n", id, type));
			}

			for (String attr : attributes.keySet()) {
				GUID id = attributes.get(attr);
				metaStore.write(String.format("-{%s} %s\n", id, attr));
			}

			metaStore.close();
		}
	}
}
