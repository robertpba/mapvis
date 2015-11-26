package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.IBoundaryShape;
import mapvis.models.LeafRegion;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public class MovingAverageRegionPathGenerator<T> extends AbstractRegionPathGenerator<T> {

    public MovingAverageRegionPathGenerator(GraphicsContext g){
        super(g);
    }

    @Override
    void createPathForBoundaryShape(IBoundaryShape<T> boundaryShape) {
        if(boundaryShape.getShapeLength() < 2)
            return;

        List<Point2D> averagedCoordinates = new ArrayList<>();

        averagedCoordinates.add(boundaryShape.getStartPoint());

        Iterator<Point2D> currPointIterator = boundaryShape.iterator();
        Iterator<Point2D> nextPointIterator = boundaryShape.iterator();
        nextPointIterator.next();

        while (nextPointIterator.hasNext()){
            Point2D currPoint = currPointIterator.next();
            Point2D nextPoint = nextPointIterator.next();
            Point2D averagePoint = currPoint.add(nextPoint).multiply(0.5);

            averagedCoordinates.add(averagePoint);
        }

        averagedCoordinates.add(boundaryShape.getEndPoint());

        boundaryShape.setCoordinates(averagedCoordinates);
        boundaryShape.setCoordinatesNeedToBeReversed(false);
    }
}

