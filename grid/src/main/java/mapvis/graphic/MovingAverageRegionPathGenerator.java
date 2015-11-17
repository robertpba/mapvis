package mapvis.graphic;

import javafx.geometry.Point2D;
import mapvis.models.LeafRegion;
import mapvis.models.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public class MovingAverageRegionPathGenerator implements IRegionPathGenerator {
    private int averWindowSize;

    public MovingAverageRegionPathGenerator(int averWindowSize){
        this.averWindowSize = averWindowSize;
    }

    @Override
    public List<Point2D[]> drawRegionPaths(List<LeafRegion.BoundaryShape> regionBoundaryShape) {
        List<Point2D[]> result = new ArrayList<>();

        for (int shapeIndex = 0; shapeIndex < regionBoundaryShape.size(); shapeIndex++) {
            LeafRegion.BoundaryShape boundaryShape = regionBoundaryShape.get(shapeIndex);
            Point2D[] avgPartOfShape = new Point2D[boundaryShape.getShapeLength()];

            for (int boundaryStep = 0; boundaryStep < boundaryShape.getShapeLength() - 1; boundaryStep++) {
                double sumXValues = 0;
                double sumYValues = 0;
                int numOfValues = 0;

                for (int windowsIndex = 0; windowsIndex < 2; windowsIndex++) {
                    int currIndex = boundaryStep + windowsIndex;
                    sumXValues += boundaryShape.getXValueAtIndex(currIndex);
                    sumYValues += boundaryShape.getYValueAtIndex(currIndex);

                    numOfValues++;
                }
                Point2D avgPoint = new Point2D(sumXValues / numOfValues, sumYValues / numOfValues);
                avgPartOfShape[boundaryShape.getShapeLength() - 1] = new Point2D(0, 0);
                if(boundaryStep == 0 && shapeIndex != 0){
                    Point2D[] prevShape = result.get(shapeIndex - 1);
                    prevShape[prevShape.length - 1] = avgPoint;
                }
                avgPartOfShape[boundaryStep] = avgPoint;

            }

            //implement overlapping average calculation directly
            result.add(avgPartOfShape);
        }
        Point2D[] firstSec = result.get(0);
        Point2D[] lastSec = result.get(result.size() - 1);
        lastSec[lastSec.length - 1] = firstSec[0];


        return result;
    }
}
