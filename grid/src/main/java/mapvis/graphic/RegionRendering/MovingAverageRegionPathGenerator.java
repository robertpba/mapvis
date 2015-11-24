package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
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
        if(regionIBoundaryShape.size() == 0){
            return;
        }


        for (int shapeIndex = 0; shapeIndex < regionIBoundaryShape.size(); shapeIndex++) {
            IBoundaryShape boundaryShape = regionIBoundaryShape.get(shapeIndex);
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

    }

    @Override
    public void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape) {
        for (List<IBoundaryShape<T>> IBoundaryShapes : regionBoundaryShape) {
            generatePathForBoundaryShape(IBoundaryShapes);
        }
    }

    private static <T> boolean areSameSeparatedRegions(Tuple2<T, T> nodeTupleA, Tuple2<T, T> nodeTupleB){

        if(nodeTupleA.first == null && nodeTupleB.first == null && nodeTupleA.second == null && nodeTupleB.second == null){
            return true;
        }

        if(nodeTupleA.first != null && nodeTupleA.second == null){
            if(nodeTupleA.first.equals(nodeTupleB.first) && nodeTupleB.second == null)
                return true;

            if(nodeTupleA.first.equals(nodeTupleB.second) && nodeTupleB.first == null)
                return true;
            return false;
        }

        if(nodeTupleA.second != null && nodeTupleA.first == null){
            if(nodeTupleA.second.equals(nodeTupleB.second) && nodeTupleB.first == null)
                return true;

            if(nodeTupleA.second.equals(nodeTupleB.first) && nodeTupleB.second == null)
                return true;

            return false;
        }

        if(nodeTupleA.first.equals(nodeTupleB.first) && nodeTupleA.second.equals(nodeTupleB.second))
            return true;

        if(nodeTupleA.second.equals(nodeTupleB.first) && nodeTupleA.first.equals(nodeTupleB.second))
            return true;

        return false;
    }


    public static <T> List<IBoundaryShape<T>> summarizeBoundaryShape(List<IBoundaryShape<T>> regionIBoundaryShape, int maxToShow, Tree2<T> tree) {
        List<List<IBoundaryShape<T>>> summarizedBoundaryShapes = new ArrayList<>();
        List<IBoundaryShape<T>> currentConcatenatedIBoundaryShape = new ArrayList<>();

        Tuple2<T, T> prevSeparatedRegions = null;
        Tuple2<T, T> firstSeparatedRegions = null;
        boolean firstIteration = true;

        List<IBoundaryShape<T>> resultingBoundaryShape = new ArrayList<>();
        IBoundaryShape<T> currBoundaryShape = null;

        for (IBoundaryShape<T> tBoundaryShape : regionIBoundaryShape) {
            Tuple2<T, T> separatedRegions = tBoundaryShape.getSeperatedRegionsID(maxToShow, tree);

            if ( (prevSeparatedRegions != null) && (!areSameSeparatedRegions(prevSeparatedRegions, separatedRegions) ) ){
                resultingBoundaryShape.add(currBoundaryShape);
                currBoundaryShape = tBoundaryShape;
            }else{
                //continue
                if(currBoundaryShape == null){
                    //init new
                    currBoundaryShape = tBoundaryShape;
                }else{
                    //append at existing
                    currBoundaryShape.getXCoords().addAll(tBoundaryShape.getXCoords());
                    currBoundaryShape.getYCoords().addAll(tBoundaryShape.getYCoords());
                }
            }

            prevSeparatedRegions = separatedRegions;
            if(firstIteration){
                firstIteration = false;
                firstSeparatedRegions = separatedRegions;
            }

        }

        if(currBoundaryShape != null){
            if(resultingBoundaryShape.size() > 0 && areSameSeparatedRegions(firstSeparatedRegions, prevSeparatedRegions)){
                currBoundaryShape.getXCoords().addAll(resultingBoundaryShape.get(0).getXCoords());
                currBoundaryShape.getYCoords().addAll(resultingBoundaryShape.get(0).getYCoords());

                resultingBoundaryShape.set(0, currBoundaryShape);
            }else{
                resultingBoundaryShape.add(currBoundaryShape);
            }
        }

        return resultingBoundaryShape;
    }


    public void generatePathForBoundaryShape(List<IBoundaryShape<T>> singleBoundaryShape, int maxToCollect, Tree2<T> tree) {
        if(singleBoundaryShape.size() == 0)
            return;
        double xCoordinateStartpoint = singleBoundaryShape.get(0).getXCoordinateStartpoint();
        double yCoordinateStartpoint = singleBoundaryShape.get(0).getYCoordinateStartpoint();

        double xCoordinateEndpoint = singleBoundaryShape.get(0).getXCoordinateEndpoint();
        double yCoordinateEndpoint = singleBoundaryShape.get(0).getYCoordinateEndpoint();
        List<IBoundaryShape<T>> summarizedBoundaryShape = summarizeBoundaryShape(singleBoundaryShape, maxToCollect, tree);
        generatePathForBoundaryShape(summarizedBoundaryShape);
    }
}
