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
    public void generatePathForBoundaryShape(List<IBoundaryShape<T>> regionIBoundaryShape) {
        if(regionIBoundaryShape.size() == 0){
            return;
        }

        for (IBoundaryShape<T> boundaryShape : regionIBoundaryShape) {
            createPathForBoundaryShape(boundaryShape);
        }
    }

    @Override
    public void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape) {
        for (List<IBoundaryShape<T>> IBoundaryShapes : regionBoundaryShape) {
            generatePathForBoundaryShape(IBoundaryShapes);
        }
    }


    @Override
    void createPathForBoundaryShape(IBoundaryShape<T> boundaryShape) {
        if(boundaryShape.getShapeLength() < 2)
            return;

        List<Point2D> averagedCoordinates = new ArrayList<>();
//        List<Double> averagedYCoordinates = new ArrayList<>();

        averagedCoordinates.add(boundaryShape.getStartPoint());
//        averagedYCoordinates.add(boundaryShape.getYCoordinateStartpoint());

        Iterator<Point2D> currPointIterator = boundaryShape.iterator();
        Iterator<Point2D> nextPointIterator = boundaryShape.iterator();
        nextPointIterator.next();

        while (nextPointIterator.hasNext()){
            Point2D currPoint = currPointIterator.next();
            Point2D nextPoint = nextPointIterator.next();
            Point2D averagePoint = currPoint.add(nextPoint).multiply(0.5);

            averagedCoordinates.add(averagePoint);
//            averagedYCoordinates.add(averagePoint.getY());
        }

        averagedCoordinates.add(boundaryShape.getEndPoint());
//        averagedYCoordinates.add(boundaryShape.getYCoordinateEndpoint());
        boundaryShape.setCoordinates(averagedCoordinates);
//        boundaryShape.setXCoords(averagedXCoordinates);
//        boundaryShape.setYCoords(averagedYCoordinates);
        boundaryShape.setCoordinatesNeedToBeReversed(false);
    }


    //    private Point2D getRoundedMidOfBoundaryShape(IBoundaryShape<T> boundaryShapeStep) {
//
//        Point2D midPoint = null;
//        if( (boundaryShapeStep.getShapeLength() % 2) != 0){
//            int midPointIndex = boundaryShapeStep.getShapeLength()/2;
//            Point2D midPointRound = new Point2D(
//                    boundaryShapeStep.getXCoordinateAtIndex(midPointIndex),
//                    boundaryShapeStep.getYCoordinateAtIndex(midPointIndex)
//            );
//            Point2D midPointRoundNext = new Point2D(
//                    boundaryShapeStep.getXCoordinateAtIndex(midPointIndex + 1),
//                    boundaryShapeStep.getYCoordinateAtIndex(midPointIndex + 1)
//            );
//            midPoint = new Point2D( (midPointRound.getX() + midPointRoundNext.getX()) / 2,
//                    (midPointRound.getY() + midPointRoundNext.getY()) / 2);
//        }else{
//            int midPointIndex = boundaryShapeStep.getShapeLength()/2;
//            midPoint = new Point2D(
//                    boundaryShapeStep.getXCoordinateAtIndex(midPointIndex),
//                    boundaryShapeStep.getYCoordinateAtIndex(midPointIndex)
//            );
//        }
//        int midPointIndex = boundaryShapeStep.getShapeLength()/2;
//        midPoint = new Point2D(
//                boundaryShapeStep.getXCoordinateAtIndex(midPointIndex),
//                boundaryShapeStep.getYCoordinateAtIndex(midPointIndex)
//        );
//
//
//        return LeafRegion.roundToCoordinatesTo4Digits(midPoint);
//    }
//    private Point2D calcEndPointOfBoundaryShapeList(IBoundaryShape<T> singleBoundaryShape) {
//        double xCoordinateEndpoint = singleBoundaryShape.get(singleBoundaryShape.size() - 1).getXCoordinateEndpoint();
//        double yCoordinateEndpoint = singleBoundaryShape.get(singleBoundaryShape.size() - 1).getYCoordinateEndpoint();
//        return new Point2D(xCoordinateEndpoint, yCoordinateEndpoint);
//    }
//
//    private Point2D calcStartPointOfBoundaryShapeList(List<IBoundaryShape<T>> singleBoundaryShape) {
//        double xCoordinateStartpoint = singleBoundaryShape.get(0).getXCoordinateStartpoint();
//        double yCoordinateStartpoint = singleBoundaryShape.get(0).getYCoordinateStartpoint();
//        return new Point2D(xCoordinateStartpoint, yCoordinateStartpoint);
//    }
}

