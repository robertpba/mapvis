package mapvis.liquidvis.model.handler;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.event.VertexMoved;

public class CollectStatistics implements ModelEventListener {

    private MapModel model;
    private int period;

    public CollectStatistics(MapModel model, int period){

        this.model = model;
        this.period = period;
    }

    public void onEvent(ModelEvent event) {
        if (event.iteration % period != 0)
            return;

        /*if (event instanceof VertexMoved) {
            onVertexMoved((VertexMoved) event);
        }*/

        if (event instanceof IterationFinished) {
            onIterationFinished((IterationFinished) event);
        }
    }


    private void onVertexMoved(VertexMoved event)
    {

    }

    private void onIterationFinished(IterationFinished event)
    {
        System.out.println("------------------");
        for (Polygon polygon : model.getPolygons().values()) {
            System.out.printf("d:%5.0f a:%7.0f, m:%7.0f %%:%6.3f\n",
                    (polygon.mass - polygon.area), polygon.area, polygon.mass,
                    (polygon.mass - polygon.area)/ polygon.mass * 100);
        }
        System.out.println("-------" + event.iteration + "---------");
    }
}
