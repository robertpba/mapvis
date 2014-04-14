package mapwiki.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WikiTree implements TabbedDataListener {
	private WikiTreeEntry root;
	private Map<Byte, List<WikiTreeEntry>> lookupByLevel = new HashMap<Byte, List<WikiTreeEntry>>();
	private Map<Integer, WikiTreeEntry> lookupByID = new TreeMap<Integer, WikiTreeEntry>();
	
	public List<WikiTreeEntry> findByLevel(byte level) {
		if (lookupByLevel.containsKey(level))
			return Collections.unmodifiableList(lookupByLevel.get(level));
		else
			return Collections.emptyList();
	}
	
	public WikiTreeEntry findByPageID(int id) {
		if (lookupByID.containsKey(id))
			return lookupByID.get(id);
		else
			return null;
	}
	
	public WikiTree(InputStream stream) throws IOException {
		new TabbedDataReader(this).readFromStream(stream);
	}
	
	public WikiTree(String filename) throws IOException {
		this(new FileInputStream(filename));
	}
	
	public WikiTreeEntry getRoot() {
		return root;
	}

	@Override
	public void tabbedValuesRead(TabbedDataEvent event) {
		String[] fieldValues = event.getData();
		// Parse data for the entry attributes.
		WikiTreeEntry entry = new WikiTreeEntry();
		entry.setPageID(Integer.parseInt(fieldValues[0]));
		entry.setPageTitle(fieldValues[1]);
		entry.setLevel(Byte.parseByte(fieldValues[2]));
		entry.setArticleCount(Integer.parseInt(fieldValues[5]));
		entry.setAltitude(Float.parseFloat(fieldValues[6]));
		if (entry.getLevel() == 0) {
			// Set root node.
			entry.setParent(null);
			if (this.root != null)
				throw new IllegalStateException("Duplicated root was found.");
			this.root = entry;
		} else {
			int parentID = Integer.parseInt(fieldValues[3]);
			if (lookupByID.containsKey(parentID)) {
				// Find its parent and append to parent's reference.
				WikiTreeEntry parent = lookupByID.get(parentID);
				entry.setParent(parent);
				parent.getChildren().add(entry);
			}
		}
		
		// Add the entry into lookup maps.
		lookupByID.put(entry.getPageID(), entry);
		if (!lookupByLevel.containsKey(entry.getLevel()))
			lookupByLevel.put(entry.getLevel(), new LinkedList<WikiTreeEntry>());
		lookupByLevel.get(entry.getLevel()).add(entry);
	}
}
