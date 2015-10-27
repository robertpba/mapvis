package mapvis.layouts.epea;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
import mapvis.layouts.Epea;
import mapvis.layouts.pea.model.MapModel;
import mapvis.layouts.pea.model.Polygon;
import mapvis.layouts.pea.model.event.IterationFinished;
import mapvis.layouts.pea.model.event.ModelEvent;
import mapvis.layouts.pea.model.event.PolygonMoved;
import mapvis.layouts.pea.model.handler.ModelEventListener;

import java.awt.geom.Point2D;

import static mapvis.utils.PointExtension.*;

public class MovePivot implements ModelEventListener {

    private MapModel model;
    private int      period;
    private Epea method;

    public MovePivot(Epea method, int period){
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

        for (INode leaf : model.getLeaves()) {
            Polygon polygon = model.getPolygon(leaf);

            Point2D centroid = polygon.calcCentroid();
            Point2D pivot = polygon.getPivot();
            Point2D d = subtract(centroid, pivot);

            if (length(d) < Math.sqrt(polygon.area)/3)
                continue;

            d = divide(d, 5);

            model.fireModelEvent(new PolygonMoved(event.iteration, polygon, d));
        }
    }
}
