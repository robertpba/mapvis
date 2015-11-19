package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.models.BoundaryShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public class MovingAverageRegionPathGenerator implements IRegionPathGenerator {
    private int averageWindowSize;

    public MovingAverageRegionPathGenerator(int avergeWindowSize){
        this.averageWindowSize = avergeWindowSize;
    }

    @Override
    public List<Point2D[]> generatePathForBoundaryShape(List<BoundaryShape> regionBoundaryShape) {
        List<Point2D[]> result = new ArrayList<>();

        for (int shapeIndex = 0; shapeIndex < regionBoundaryShape.size(); shapeIndex++) {
            BoundaryShape boundaryShape = regionBoundaryShape.get(shapeIndex);
            Point2D[] avgPartOfShape = new Point2D[boundaryShape.getShapeLength()];

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

                boundaryShape.setXCoordinateAtIndex(boundaryStep, sumXValues / numOfValues);
                boundaryShape.setYCoordinateAtIndex(boundaryStep, sumYValues / numOfValues);

                Point2D avgPoint = new Point2D(sumXValues / numOfValues, sumYValues / numOfValues);

                if(boundaryStep == 0 && shapeIndex != 0){
                    Point2D[] prevShape = result.get(shapeIndex - 1);
                    prevShape[prevShape.length - 1] = avgPoint;

                    BoundaryShape prevBoundaryShape = regionBoundaryShape.get(shapeIndex - 1);
                    prevBoundaryShape.setYCoordinateAtIndex(prevBoundaryShape.getShapeLength() - 1, avgPoint.getY());
                    prevBoundaryShape.setXCoordinateAtIndex(prevBoundaryShape.getShapeLength() - 1, avgPoint.getX());
                }
                avgPartOfShape[boundaryStep] = avgPoint;

            }
            //implement overlapping average calculation directly
            result.add(avgPartOfShape);

        }
        Point2D[] firstSec = result.get(0);
        Point2D[] lastSec = result.get(result.size() - 1);
        lastSec[lastSec.length - 1] = firstSec[0];

        BoundaryShape firstBoundaryShape = regionBoundaryShape.get(0);
        BoundaryShape lastBoundaryShape = regionBoundaryShape.get(regionBoundaryShape.size() - 1);
        lastBoundaryShape.setXCoordinateAtIndex(lastBoundaryShape.getShapeLength() - 1, firstBoundaryShape.getXCoordinateAtIndex(0));
        lastBoundaryShape.setYCoordinateAtIndex(lastBoundaryShape.getShapeLength() - 1, firstBoundaryShape.getYCoordinateAtIndex(0));

        return result;
    }
}
