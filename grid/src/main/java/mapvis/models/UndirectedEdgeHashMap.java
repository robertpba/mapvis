package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.models.LeafRegion;

import java.util.*;

/**
 * Created by dacc on 11/6/2015.
 */
public class UndirectedEdgeHashMap {

    Map<Point2D, List<LeafRegion.BoundaryShape>> startToConnectedBoundaryShape = new HashMap<>();

    public void put(LeafRegion.BoundaryShape boundaryShape){
        putPointToHashMap(boundaryShape.getStartPoint(), boundaryShape);
        putPointToHashMap(boundaryShape.getEndPoint(), boundaryShape);
    }

    public boolean isEmpty(){
        return startToConnectedBoundaryShape.isEmpty();
    }

    public LeafRegion.BoundaryShape getNext(){
        return startToConnectedBoundaryShape.values().iterator().next().get(0);
    }

    public void remove(LeafRegion.BoundaryShape boundaryShape){
        if(boundaryShape == null)
            return;

        removePointFromHashMap(boundaryShape.getStartPoint(), boundaryShape);
        removePointFromHashMap(boundaryShape.getEndPoint(), boundaryShape);
    }

    private void removePointFromHashMap(Point2D keyPointToRemove, LeafRegion.BoundaryShape boundaryShape) {
        List<LeafRegion.BoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(keyPointToRemove);

        if(boundaryShapes == null)
            return;

        Iterator<LeafRegion.BoundaryShape> iterator = boundaryShapes.iterator();
        while (iterator.hasNext()) {
            LeafRegion.BoundaryShape next = iterator.next();
            if(LeafRegion.isSameBorder(boundaryShape.border, next.border)){
                iterator.remove();
                break;
            }
        }

        if(boundaryShapes.isEmpty()){
            startToConnectedBoundaryShape.remove(keyPointToRemove);
        }
    }

    private void putPointToHashMap(Point2D pointToPut, LeafRegion.BoundaryShape boundaryShape) {
        if(startToConnectedBoundaryShape.containsKey(pointToPut)){
            List<LeafRegion.BoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(pointToPut);
            for (LeafRegion.BoundaryShape shape : boundaryShapes) {
                if(LeafRegion.isSameBorder(boundaryShape.border, shape.border)){
                    return;
                }
            }
            if(startToConnectedBoundaryShape.get(pointToPut).size() >= 2){
                System.out.println("Already full!!: " + pointToPut.getX() + " " + pointToPut.getY());
                boundaryShape.renderColored = true;
            }else{
                startToConnectedBoundaryShape.get(pointToPut).add(boundaryShape);
            }
        }else{
            List<LeafRegion.BoundaryShape> boundaryShapes = new ArrayList<>();
            boundaryShapes.add(boundaryShape);
            startToConnectedBoundaryShape.put(pointToPut, boundaryShapes);
        }
    }

    public LeafRegion.BoundaryShape getNextEdgeWithPivotPoint(Point2D pivotPoint, LeafRegion.BoundaryShape currentEdge){
        List<LeafRegion.BoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(pivotPoint);
        if(boundaryShapes == null){
            return null;
        }

        for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
            if(!LeafRegion.isSameBorder(boundaryShape.border, currentEdge.border)){
                return boundaryShape;
            }
        }
        return null;
    }

}
