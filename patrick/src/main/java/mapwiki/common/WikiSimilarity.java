package mapwiki.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class WikiSimilarity implements TabbedDataListener {
	private Map<Long, WikiSimilarityEntry> map = new TreeMap<Long, WikiSimilarityEntry>();
	
	public WikiSimilarityEntry findEntry(int pageID1, int pageID2) {
		long key = pageID1 <= pageID2 ? mapKey(pageID1, pageID2) : mapKey(pageID2, pageID1);
		if (map.containsKey(key))
			return map.get(key);
		else
			return null;
	}
	
	public double findSimilarity(int pageID1, int pageID2) {
		WikiSimilarityEntry entry = findEntry(pageID1, pageID2);
		return entry == null ? 0.0 : entry.getSimilarity();
	}
	
	public int findCoCount(int pageID1, int pageID2) {
		WikiSimilarityEntry entry = findEntry(pageID1, pageID2);
		return entry == null ? 0 : entry.getCoCount();
	}
	
	public WikiSimilarity(InputStream stream) throws IOException {
		new TabbedDataReader(this).readFromStream(stream);
	}
	
	public WikiSimilarity(String filename) throws IOException {
		this(new FileInputStream(filename));
	}
	
	protected long mapKey(int i, int j) {
		return (long)i << 32 | (long)j;
	}
	
	@Override
	public void tabbedValuesRead(TabbedDataEvent event) {
		String[] fieldValues = event.getData();
		int id1 = Integer.parseInt(fieldValues[0]);
		int id2 = Integer.parseInt(fieldValues[1]);
		WikiSimilarityEntry entry = new WikiSimilarityEntry(id1, id2);
		entry.setSimilarity(Double.parseDouble(fieldValues[2]));
		entry.setCoCount(Integer.parseInt(fieldValues[3]));
		map.put(mapKey(entry.getPageID1(), entry.getPageID2()),	entry);
	}
}
