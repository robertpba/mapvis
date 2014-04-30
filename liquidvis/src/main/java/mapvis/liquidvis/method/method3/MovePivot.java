package mapvis.liquidvis.method.method3;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vector2D;
import mapvis.liquidvis.model.Vertex;
import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.event.PolygonMoved;
import mapvis.liquidvis.model.handler.ModelEventListener;

public class MovePivot implements ModelEventListener {

    private MapModel model;
    private int      period;
    private Method3  method;

    public MovePivot(Method3 method, int period){
        this.method = method;
        this.model  = method.model;
        this.period = period;
    }

    @Override
    public void onEvent(ModelEvent event) {

        if (!(event instanceof IterationFinished))
            return;

        if (event.iteration % period != 0)
            return;

        for (Object leaf : model.getLeaves()) {
            Polygon polygon = model.getPolygon(leaf);

            Vector2D centroid = polygon.calcCentroid();
            Vector2D pivot = polygon.getOrigin();
            Vector2D d = Vector2D.subtract(centroid , pivot);

            if (d.norm() < Math.sqrt(polygon.area)/3)
                continue;

            d = Vector2D.divide(d,5);

            model.fireModelEvent(new PolygonMoved(event.iteration, polygon, d));
        }
    }
}
