package mapvis.layouts.pea.method.method3;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.model.MapModel;
import mapvis.layouts.pea.model.Polygon;
import mapvis.layouts.pea.model.Vertex;
import mapvis.layouts.pea.model.event.IterationFinished;
import mapvis.layouts.pea.model.event.ModelEvent;
import mapvis.layouts.pea.model.handler.ModelEventListener;

public class DriveAwayInsideVertices implements ModelEventListener {

    private MapModel model;
    private int      period;
    private Method3  method;

    public DriveAwayInsideVertices(Method3 method, int period){
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

        for (Node leaf : model.getLeaves()) {
            Polygon polygon = model.getPolygon(leaf);
            for (Vertex vertex : polygon.vertices) {
                Polygon surroundingRegion = model.findSurroundingRegion(vertex.getPoint(), vertex.polygon.node);
                if (surroundingRegion!=null)
                {
                    method.manipulator.moveBack(vertex);
                }
            }
        }
    }
}
