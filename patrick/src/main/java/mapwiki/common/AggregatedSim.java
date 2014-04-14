package mapwiki.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AggregatedSim implements TabbedDataListener {
	private Map<String, Double> map = new HashMap<String, Double>();
	
	public AggregatedSim(InputStream stream) throws IOException {
		new TabbedDataReader(this).readFromStream(stream);
	}
	
	public AggregatedSim(String filename) throws IOException {
		this(new FileInputStream(filename));
	}

	@Override
	public void tabbedValuesRead(TabbedDataEvent event) {
		String[] data = event.getData();
		String key = mapKey(data[0], data[1]);
		double sim = Double.parseDouble(data[2]);
		map.put(key, sim);
	}
	
	public double findSimilarity(String c1, String c2) {
		String key = mapKey(c1, c2);
		return map.containsKey(key) ? map.get(key) : 0.0;
	}
	
	protected String mapKey(String a, String b) {
		if (a.compareTo(b) <= 0)
			return a + "|" + b;
		else
			return b + "|" + a;
	}
}
