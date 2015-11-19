package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.models.LeafRegion;

import java.util.*;

/**
 * Created by dacc on 11/6/2015.
 * This class is used to sort the unordered @BoundaryShape
 * defining the Region for rendering. To calculate the entire border
 * around one Region the @BoundaryShapes of the Region have to be
 * concatenated. Concatenation is resolved by calculating the
 * start and endpoint of the @BoundaryShape and finding subsequent
 * Border. Since the coordinate of @BoundaryShapes cannot be globally
 * ordered. Lookup of start and endpoints have to be used to resolve one
 * of the correct orders around one Region.
 */
public class UndirectedEdgeHashMap {

    Map<Point2D, List<BoundaryShape>> startToConnectedBoundaryShape = new HashMap<>();

    public void put(BoundaryShape boundaryShape){
        putPointToHashMap(boundaryShape.getStartPoint(), boundaryShape);
        putPointToHashMap(boundaryShape.getEndPoint(), boundaryShape);
    }

    public boolean isEmpty(){
        return startToConnectedBoundaryShape.isEmpty();
    }

    public BoundaryShape getNext(){
        return startToConnectedBoundaryShape.values().iterator().next().get(0);
    }

    public void remove(BoundaryShape boundaryShape){
        if(boundaryShape == null)
            return;

        removePointFromHashMap(boundaryShape.getStartPoint(), boundaryShape);
        removePointFromHashMap(boundaryShape.getEndPoint(), boundaryShape);
    }

    private void removePointFromHashMap(Point2D keyPointToRemove, BoundaryShape boundaryShape) {
        List<BoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(keyPointToRemove);

        if(boundaryShapes == null)
            return;

        Iterator<BoundaryShape> iterator = boundaryShapes.iterator();
        while (iterator.hasNext()) {
            BoundaryShape next = iterator.next();
            if(LeafRegion.isSameBorder(boundaryShape.border, next.border)){
                iterator.remove();
                break;
            }
        }

        if(boundaryShapes.isEmpty()){
            startToConnectedBoundaryShape.remove(keyPointToRemove);
        }
    }

    private void putPointToHashMap(Point2D pointToPut, BoundaryShape boundaryShape) {
        if(startToConnectedBoundaryShape.containsKey(pointToPut)){
            List<BoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(pointToPut);
            for (BoundaryShape shape : boundaryShapes) {
                if(LeafRegion.isSameBorder(boundaryShape.border, shape.border)){
                    return;
                }
            }
            if(startToConnectedBoundaryShape.get(pointToPut).size() >= 2){
                System.out.println("Already full!!: " + pointToPut.getX() + " " + pointToPut.getY());
            }else{
                startToConnectedBoundaryShape.get(pointToPut).add(boundaryShape);
            }
        }else{
            List<BoundaryShape> boundaryShapes = new ArrayList<>();
            boundaryShapes.add(boundaryShape);
            startToConnectedBoundaryShape.put(pointToPut, boundaryShapes);
        }
    }

    public BoundaryShape getNextEdgeWithPivotPoint(Point2D pivotPoint, BoundaryShape currentEdge){
        List<BoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(pivotPoint);
        if(boundaryShapes == null){
            return null;
        }

        for (BoundaryShape boundaryShape : boundaryShapes) {
            if(!LeafRegion.isSameBorder(boundaryShape.border, currentEdge.border)){
                return boundaryShape;
            }
        }
        return null;
    }

}
