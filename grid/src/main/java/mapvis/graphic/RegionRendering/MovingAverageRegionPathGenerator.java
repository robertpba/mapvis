package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.models.IBoundaryShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public class MovingAverageRegionPathGenerator<T> implements IRegionPathGenerator<T> {
    private int averageWindowSize;

    public MovingAverageRegionPathGenerator(int averageWindowSize){
        this.averageWindowSize = averageWindowSize;
    }

    @Override
    public void generatePathForBoundaryShape(List<IBoundaryShape<T>> regionIBoundaryShape) {
        for (int shapeIndex = 0; shapeIndex < regionIBoundaryShape.size(); shapeIndex++) {
            IBoundaryShape boundaryShape = regionIBoundaryShape.get(shapeIndex);
            List<Double> averagedXCoordinates = new ArrayList<>();
            List<Double> averagedYCoordinates = new ArrayList<>();

            for (int boundaryStep = 0; boundaryStep < boundaryShape.getShapeLength() - 1; boundaryStep++) {

                if(boundaryStep == 0 && shapeIndex == 0){
                    averagedXCoordinates.add(boundaryShape.getXCoordinateAtIndex(0));
                    averagedYCoordinates.add(boundaryShape.getYCoordinateAtIndex(0));
                }

                double sumXValues = 0;
                double sumYValues = 0;
                int numOfValues = 0;

                for (int windowsIndex = 0; windowsIndex < 2; windowsIndex++) {
                    int currIndex = boundaryStep + windowsIndex;
                    sumXValues += boundaryShape.getXCoordinateAtIndex(currIndex);
                    sumYValues += boundaryShape.getYCoordinateAtIndex(currIndex);

                    numOfValues++;
                }


                Point2D avgPoint = new Point2D(sumXValues / numOfValues, sumYValues / numOfValues);

                averagedXCoordinates.add(avgPoint.getX());
                averagedYCoordinates.add(avgPoint.getY());


                if( (shapeIndex == (regionIBoundaryShape.size() - 1)) && (boundaryStep == boundaryShape.getShapeLength() - 2)){
                    averagedXCoordinates.add(boundaryShape.getXCoordinateEndpoint());
                    averagedYCoordinates.add(boundaryShape.getYCoordinateEndpoint());
                }
            }
            boundaryShape.setXCoords(averagedXCoordinates);
            boundaryShape.setYCoords(averagedYCoordinates);
            boundaryShape.setCoordinatesNeedToBeReversed(false);
        }
    }

    @Override
    public void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape) {
        for (List<IBoundaryShape<T>> IBoundaryShapes : regionBoundaryShape) {
            generatePathForBoundaryShape(IBoundaryShapes);
        }
    }
}
