package mapvis.liquidvis.method.method3;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vertex;
import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.handler.ModelEventListener;

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

        for (Polygon polygon : model.getPolygons().values()) {
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
