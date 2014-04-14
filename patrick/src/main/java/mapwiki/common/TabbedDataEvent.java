package mapwiki.common;

import java.util.EventObject;

public class TabbedDataEvent extends EventObject {
	private static final long serialVersionUID = 6815127176319362976L;
	
	private String[] data;

	public TabbedDataEvent(Object source, String[] data) {
		super(source);
		this.data = data;
	}

	public String[] getData() {
		return data;
	}
}
