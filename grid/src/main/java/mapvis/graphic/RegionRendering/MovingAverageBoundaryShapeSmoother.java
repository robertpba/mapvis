package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.IBoundaryShape;

import java.util.*;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 * This algorithm smooths the IBoundaryShape by calculating the average of two
 * subsequent coordinates.
 */
public class MovingAverageBoundaryShapeSmoother<T> extends AbstractBoundaryShapeSmoother<T> {

    public MovingAverageBoundaryShapeSmoother(GraphicsContext g){
        super(g);
    }

    @Override
    void smoothBoundaryShape(IBoundaryShape<T> boundaryShape) {
        if(boundaryShape.getShapeLength() < 2)
            return;

        List<Point2D> averagedCoordinates = new ArrayList<>();
        //start and end point of original shape has to stay the same
        //to ensure connectivity to other IBoundaryShapes
        averagedCoordinates.add(boundaryShape.getStartPoint());

        Iterator<Point2D> currPointIterator = boundaryShape.iterator();
        Iterator<Point2D> nextPointIterator = boundaryShape.iterator();
        nextPointIterator.next();

        while (nextPointIterator.hasNext()){
            //calc average coordinate
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

