package mapvis.layouts.epea;

import mapvis.layouts.pea.model.*;
import mapvis.layouts.pea.model.event.VertexMoved;

import java.awt.geom.Point2D;

import static mapvis.utils.PointExtension.*;

public class Manipulator  {
    private MapModel model;

    public Manipulator(MapModel model) {
        this.model = model;
    }

    public boolean moveForth(Vertex srcVertex) {
        Polygon srcPolygon = srcVertex.polygon;
        Point2D srcPos = srcVertex.getPoint();
        Point2D unit = unit(subtract(srcPos, srcPolygon.getPivot()));
        Point2D dstPos = add(srcPos, unit);

        Polygon dstRegion = model.findSurroundingRegion(dstPos, null);

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
        Point2D srcPos = vertex.getPoint();
        Point2D unit = unit(subtract(srcPos, vertex.polygon.getPivot()));

        if (vertex.moveCount <= 1)
            return false;

        Point2D dstPos = subtract(vertex.getPoint(), unit);

        if (!region.contains(dstPos))
            return false;

        vertex.moveCount --;
        region.moveBackCount ++;

        model.fireModelEvent(new VertexMoved(model.iteration, vertex, srcPos, dstPos));

        return true;
    }
}
