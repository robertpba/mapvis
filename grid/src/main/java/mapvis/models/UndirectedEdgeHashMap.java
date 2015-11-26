package mapvis.models;

import javafx.geometry.Point2D;

import java.util.*;

/**
 * Created by dacc on 11/6/2015.
 * This class is used to sort the unordered @IBoundaryShape
 * defining the Region for rendering. To calculate the entire border
 * around one Region the @BoundaryShapes of the Region have to be
 * concatenated. Concatenation is resolved by calculating the
 * start and endpoint of the @IBoundaryShape and finding subsequent
 * Border. Since the coordinate of @BoundaryShapes cannot be globally
 * ordered. Lookup of start and endpoints have to be used to resolve one
 * of the correct orders around one Region.
 */
public class UndirectedEdgeHashMap {

    Map<Point2D, List<IBoundaryShape>> startToConnectedBoundaryShape = new HashMap<>();

    public void put(IBoundaryShape boundaryShape){
        putPointToHashMap(boundaryShape.getStartPoint(), boundaryShape);
        putPointToHashMap(boundaryShape.getEndPoint(), boundaryShape);
    }

    public boolean isEmpty(){
        return startToConnectedBoundaryShape.isEmpty();
    }

    public IBoundaryShape getNext(){
        return startToConnectedBoundaryShape.values().iterator().next().get(0);
    }

    public void remove(IBoundaryShape boundaryShape){
        if(boundaryShape == null)
            return;

        removePointFromHashMap(boundaryShape.getStartPoint(), boundaryShape);
        removePointFromHashMap(boundaryShape.getEndPoint(), boundaryShape);
    }

    private void removePointFromHashMap(Point2D keyPointToRemove, IBoundaryShape boundaryShape) {
        List<IBoundaryShape> IBoundaryShapes = startToConnectedBoundaryShape.get(keyPointToRemove);

        if(IBoundaryShapes == null)
            return;

        Iterator<IBoundaryShape> iterator = IBoundaryShapes.iterator();
        while (iterator.hasNext()) {
            IBoundaryShape next = iterator.next();
            if(LeafRegion.isSameBorder(boundaryShape.getFirstBorder(), next.getFirstBorder())){
                iterator.remove();
                break;
            }
        }

        if(IBoundaryShapes.isEmpty()){
            startToConnectedBoundaryShape.remove(keyPointToRemove);
        }
    }

    private void putPointToHashMap(Point2D pointToPut, IBoundaryShape boundaryShape) {
        if(startToConnectedBoundaryShape.containsKey(pointToPut)){
            List<IBoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(pointToPut);
            for (IBoundaryShape shape : boundaryShapes) {
                if(LeafRegion.isSameBorder(boundaryShape.getFirstBorder(), shape.getFirstBorder())){
                    return;
                }
            }
            if(startToConnectedBoundaryShape.get(pointToPut).size() >= 2){
                System.out.println("Already full!!: " + pointToPut.getX() + " " + pointToPut.getY());
            }else{
                startToConnectedBoundaryShape.get(pointToPut).add(boundaryShape);
            }
        }else{
            List<IBoundaryShape> IBoundaryShapes = new ArrayList<>();
            IBoundaryShapes.add(boundaryShape);
            startToConnectedBoundaryShape.put(pointToPut, IBoundaryShapes);
        }
    }

    public IBoundaryShape getNextEdgeWithPivotPoint(Point2D pivotPoint, IBoundaryShape currentEdge){
        List<IBoundaryShape> boundaryShapes = startToConnectedBoundaryShape.get(pivotPoint);
        if(boundaryShapes == null){
            return null;
        }

        for (IBoundaryShape boundaryShape : boundaryShapes) {
            if(!LeafRegion.isSameBorder(boundaryShape.getFirstBorder(), currentEdge.getFirstBorder())){
                return boundaryShape;
            }
        }
        return null;
    }

}
