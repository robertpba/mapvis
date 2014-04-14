package mapvis.liquidvis.model.handler;

import mapvis.liquidvis.model.event.ModelEvent;

public interface ModelEventListener {
    public void onEvent(ModelEvent event);
}
