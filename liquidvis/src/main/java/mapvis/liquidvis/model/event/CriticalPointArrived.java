package mapvis.liquidvis.model.event;

public class CriticalPointArrived extends  ModelEvent {
    public String desc;

    public CriticalPointArrived(int iteration, String desc) {
        this.desc = desc;
        this.iteration = iteration;
    }

}
