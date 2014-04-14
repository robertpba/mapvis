package mapvis.liquidvis.model.handler;

import mapvis.liquidvis.model.event.ModelEvent;

public class PeriodicWrapper implements ModelEventListener {


    private final ModelEventListener handler;
    private final int period;

    public PeriodicWrapper(ModelEventListener handler, int period)
    {
        this.handler = handler;
        this.period = period;
    }

    @Override
    public void onEvent(ModelEvent event) {
        if (event.iteration % period == 0)
            handler.onEvent(event);
    }
}
