package mapvis.layouts.pea.model.handler;

import mapvis.layouts.pea.model.Polygon;
import mapvis.layouts.pea.model.event.ModelEvent;
import mapvis.layouts.pea.model.event.VertexMoved;

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
