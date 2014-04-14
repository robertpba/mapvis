package mapvis.liquidvis.method.method3;

import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.event.VertexMoved;

public class Manipulator  {
    private MapModel model;

    public Manipulator(MapModel model) {
        this.model = model;
    }

    public boolean moveForth(Vertex srcVertex) {
        Polygon srcPolygon = srcVertex.polygon;
        Vector2D srcPos = srcVertex.getPoint();
        Vector2D unit = Vector2D.subtract(srcPos, srcPolygon.getOrigin()).unit();
        Vector2D dstPos = Vector2D.add(srcPos, unit);

        Polygon dstRegion = model.findSurroundingRegion(dstPos, srcPolygon.node.parent.parent, null);

        // move to a free location
        if (dstRegion == null) {
            srcVertex.moveCount++;
            srcVertex.momentum = 0;

            srcPolygon.moveForwardCount ++;

            model.fireModelEvent(new VertexMoved(model.iteration, srcVertex, srcPos, dstPos));

            return true;
        }

        // collide or bounded back
        Vertex nearestVertex = model.findNearestVertex(srcPos, dstRegion);
        if (moveBack(nearestVertex)){
            nearestVertex.momentum = - (int)(srcPolygon.mass - srcPolygon.area) - 10;
            srcVertex.momentum = - (int)(dstRegion.mass - dstRegion.area) - 10;
            return true;
        }
        return false;


    }

    public boolean moveBack(Vertex vertex) {
        Polygon region = vertex.polygon;
        Vector2D srcPos = vertex.getPoint();
        Vector2D unit = Vector2D.subtract(srcPos, vertex.polygon.getOrigin()).unit();

        if (vertex.moveCount <= 1)
            return false;

        Vector2D dstPos = Vector2D.subtract(vertex.getPoint(), unit);

        if (!region.contains(dstPos))
            return false;

        vertex.moveCount --;
        region.moveBackCount ++;

        model.fireModelEvent(new VertexMoved(model.iteration, vertex, srcPos, dstPos));

        return true;
    }
}
