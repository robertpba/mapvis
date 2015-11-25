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
    private int averageWindowSize;

    public MovingAverageRegionPathGenerator(int averageWindowSize, GraphicsContext g){
        super(g);
        this.averageWindowSize = averageWindowSize;
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
        List<Double> averagedXCoordinates = new ArrayList<>();
        List<Double> averagedYCoordinates = new ArrayList<>();

        averagedXCoordinates.add(boundaryShape.getXCoordinateStartpoint());
        averagedYCoordinates.add(boundaryShape.getYCoordinateStartpoint());

        for (int boundaryStep = 0; boundaryStep < boundaryShape.getShapeLength() - 1; boundaryStep++) {
            double sumXValues = 0;
            double sumYValues = 0;
            int numOfValues = 0;

            for (int windowsIndex = 0; windowsIndex < 2; windowsIndex++) {
                int currIndex = boundaryStep + windowsIndex;
                sumXValues += boundaryShape.getXCoordinateAtIndex(currIndex);
                sumYValues += boundaryShape.getYCoordinateAtIndex(currIndex);

                numOfValues++;
            }

            averagedXCoordinates.add(sumXValues / numOfValues);
            averagedYCoordinates.add(sumYValues / numOfValues);
        }

        averagedXCoordinates.add(boundaryShape.getXCoordinateEndpoint());
        averagedYCoordinates.add(boundaryShape.getYCoordinateEndpoint());

        boundaryShape.setXCoords(averagedXCoordinates);
        boundaryShape.setYCoords(averagedYCoordinates);
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
