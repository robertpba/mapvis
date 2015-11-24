package mapvis.graphic.RegionRendering;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.geometry.Point2D;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.*;

import java.util.*;

/**
 * Created by dacc on 11/6/2015.
 */
public class BoundaryShapeUtils {

    public static <T> List<List<IBoundaryShape<T>>> orderBoundaryShapes(List<IBoundaryShape<T>> boundaryShapes) {
        if(boundaryShapes.isEmpty())
            return Collections.emptyList();

        UndirectedEdgeHashMap undirectedEdgeHashMap = new UndirectedEdgeHashMap();
        for (IBoundaryShape boundaryShape : boundaryShapes) {
            undirectedEdgeHashMap.put(boundaryShape);
        }


        List<IBoundaryShape<T>> boundaryShape = new ArrayList<>();

        List<List<IBoundaryShape<T>>> resultingBoundaryShape = new ArrayList<>();

        Point2D currentPoint = null;
        IBoundaryShape<T> currentBoundaryShape = null;
        for (int i = 0; i < boundaryShapes.size(); i++) {
            if(i == 0){
                //get any start edge/boundary shape and initial the current point
                //point to step from one boundary shape end to the start of the next boundary shape
                currentBoundaryShape = undirectedEdgeHashMap.getNext();
                currentPoint = currentBoundaryShape.getStartPoint();
            }else{
                currentBoundaryShape = undirectedEdgeHashMap.getNextEdgeWithPivotPoint(currentPoint, currentBoundaryShape);
            }

            if(currentBoundaryShape == null  // we found a circle since no edge starts/ends with the current point
                    //the boundary shape itself is circual
                    || currentBoundaryShape.getStartPoint().equals(currentBoundaryShape.getEndPoint())
                    ){
                resultingBoundaryShape.add(boundaryShape);
                undirectedEdgeHashMap.remove(currentBoundaryShape);

                boundaryShape = new ArrayList<>();
                if(!undirectedEdgeHashMap.isEmpty()){
                    currentBoundaryShape = undirectedEdgeHashMap.getNext();
                }else{
                    break;
                }

            }

            //the boundary shapes are undirected => check if to continue with end or start point
            if(currentBoundaryShape.getStartPoint().equals(currentPoint)){
                currentPoint = currentBoundaryShape.getEndPoint();
                currentBoundaryShape.setCoordinatesNeedToBeReversed(false);
            }else{
                currentPoint = currentBoundaryShape.getStartPoint();
                currentBoundaryShape.setCoordinatesNeedToBeReversed(true);
            }

            undirectedEdgeHashMap.remove(currentBoundaryShape);
            boundaryShape.add(currentBoundaryShape);
        }
        if(!boundaryShape.isEmpty()){
            resultingBoundaryShape.add(boundaryShape);
        }
        return resultingBoundaryShape;
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
//                summarizedBoundaryShapes.add(currentConcatenatedIBoundaryShape);
//                currentConcatenatedIBoundaryShape = new ArrayList<>();
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

//            currentConcatenatedIBoundaryShape.add(tBoundaryShape);

            prevSeparatedRegions = separatedRegions;
            if(firstIteration){
                firstIteration = false;
                firstSeparatedRegions = separatedRegions;
            }

        }

//        if(currentConcatenatedIBoundaryShape.size() > 0){
//            if(summarizedBoundaryShapes.size() > 0 && areSameSeparatedRegions(firstSeparatedRegions, prevSeparatedRegions)){
//                currentConcatenatedIBoundaryShape.addAll(summarizedBoundaryShapes.get(0));
//                summarizedBoundaryShapes.set(0, currentConcatenatedIBoundaryShape);
//            }else{
//                summarizedBoundaryShapes.add(currentConcatenatedIBoundaryShape);
//            }
//        }

        if(currBoundaryShape != null){
            if(resultingBoundaryShape.size() > 0 && areSameSeparatedRegions(firstSeparatedRegions, prevSeparatedRegions)){
                currBoundaryShape.getXCoords().addAll(resultingBoundaryShape.get(0).getXCoords());
                currBoundaryShape.getYCoords().addAll(resultingBoundaryShape.get(0).getYCoords());

                resultingBoundaryShape.set(0, currBoundaryShape);
            }else{
                resultingBoundaryShape.add(currBoundaryShape);
            }
        }
//        return summarizedBoundaryShapes;
        return resultingBoundaryShape;
    }

}
