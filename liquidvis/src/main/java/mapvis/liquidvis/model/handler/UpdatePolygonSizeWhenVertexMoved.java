package mapvis.liquidvis.model.handler;

import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.event.VertexMoved;

public class UpdatePolygonSizeWhenVertexMoved implements ModelEventListener{
        @Override
        public void onEvent(ModelEvent event) {
            if (! (event instanceof VertexMoved))
                return;

            VertexMoved vertexMoved = (VertexMoved) event;
            Polygon polygon = vertexMoved.polygon;
            polygon.area = polygon.polygonArea();
        }
}
