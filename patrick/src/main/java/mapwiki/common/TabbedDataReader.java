package mapwiki.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TabbedDataReader {
	private TabbedDataListener listener;
	private int currentLineNo;
	
	public TabbedDataReader(TabbedDataListener listener) {
		this.listener = listener;
	}
	
	public void readFromStream(InputStream stream) throws IOException, IllegalStateException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		try {
			String s;
			currentLineNo = 0;
			while ((s = br.readLine()) != null) {
				currentLineNo++;
				String[] data = processLine(s);
				if (data != null)
					listener.tabbedValuesRead(new TabbedDataEvent(this, data));
			}
		} catch (Exception ex) {
			throw new IllegalStateException(String.format("Parse error in line %d: %s",
					currentLineNo, ex.getMessage()), ex);
		} finally {
			br.close();
		}
	}
	
	protected String[] processLine(String line) {
		if ("".equals(line))
			return null;
		else
			return line.split("\t");
	}
	
	protected int getCurrentLineNo() {
		return currentLineNo;
	}
}
