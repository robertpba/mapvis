package mapwiki.common;

import java.util.EventListener;

public interface TabbedDataListener extends EventListener {
	void tabbedValuesRead(TabbedDataEvent event);
}
