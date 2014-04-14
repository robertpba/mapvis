package mapvis.liquidvis.model.event;

public abstract class ModelEvent {
    public int iteration;

    protected ModelEvent()
    {}
    protected ModelEvent(int iteration) {
        this.iteration = iteration;
    }
}
