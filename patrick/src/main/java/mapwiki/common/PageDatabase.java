package mapwiki.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class PageDatabase implements TabbedDataListener {
	private Map<String, PageEntry> map = new TreeMap<String, PageEntry>();

	@Override
	public void tabbedValuesRead(TabbedDataEvent event) {
		String[] pageData = event.getData();
		PageEntry pe = new PageEntry();
		pe.pageID = Integer.parseInt(pageData[0]);
		pe.pageTitle = pageData[1];
		pe.category = pageData[2];
		pe.pageLength = Integer.parseInt(pageData[3]);
		map.put(pe.category, pe);
	}
	
	public PageDatabase(InputStream stream) throws IOException {
		new TabbedDataReader(this).readFromStream(stream);
	}
	
	public PageDatabase(String filename) throws IOException {
		this(new FileInputStream(filename));
	}
	
	public PageEntry findEntry(String category) {
		if (!map.containsKey(category))
			return null;
		else
			return map.get(category);
	}
}
