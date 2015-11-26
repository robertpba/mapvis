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

}
