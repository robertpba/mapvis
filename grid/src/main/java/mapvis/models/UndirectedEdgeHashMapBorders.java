package mapvis.models;

import javafx.geometry.Point2D;

import java.util.*;


public class UndirectedEdgeHashMapBorders {

    Map<Point2D, List<Border>> startToConnectedBorder = new HashMap<>();

    public void put(Border border){
        Point2D roundedStartpoint = LeafRegion.roundToCoordinatesTo4Digits(border.getStartPoint());
        Point2D roundedEndpoint = LeafRegion.roundToCoordinatesTo4Digits(border.getLastPoint());
        putPointToHashMap(roundedStartpoint, border);
        putPointToHashMap(roundedEndpoint, border);
    }

    public boolean isEmpty(){
        return startToConnectedBorder.isEmpty();
    }

    public Border getNext(){
        return startToConnectedBorder.values().iterator().next().get(0);
    }

    public void remove(Border border){
        if(border == null)
            return;
        Point2D roundedStartpoint = LeafRegion.roundToCoordinatesTo4Digits(border.getStartPoint());
        Point2D roundedEndpoint = LeafRegion.roundToCoordinatesTo4Digits(border.getLastPoint());

        removePointFromHashMap(roundedStartpoint, border);
        removePointFromHashMap(roundedEndpoint, border);
    }

    private void removePointFromHashMap(Point2D pivotPointToRemove, Border border) {
        List<Border> bordersWithPivotPoint = startToConnectedBorder.get(pivotPointToRemove);

        if(bordersWithPivotPoint == null)
            return;

        Iterator<Border> iterator = bordersWithPivotPoint.iterator();
        while (iterator.hasNext()) {
            Border next = iterator.next();
            if(LeafRegion.isSameBorder(border, next)){
                iterator.remove();
                break;
            }
        }

        if(bordersWithPivotPoint.isEmpty()){
            startToConnectedBorder.remove(pivotPointToRemove);
        }
    }

    private void putPointToHashMap(Point2D pivotPointToPut, Border borderToAdd) {
        if(startToConnectedBorder.containsKey(pivotPointToPut)){
            List<Border> bordersWithPivotPoint = startToConnectedBorder.get(pivotPointToPut);
            for (Border existingBorder : bordersWithPivotPoint) {
                if(LeafRegion.isSameBorder(borderToAdd, existingBorder)){
                    return;
                }
            }
            if(startToConnectedBorder.get(pivotPointToPut).size() >= 2){
                System.out.println("Already full!!: " + pivotPointToPut.getX() + " " + pivotPointToPut.getY());
            }else{
                startToConnectedBorder.get(pivotPointToPut).add(borderToAdd);
            }
        }else{
            List<Border> newBorderList = new ArrayList<>();
            newBorderList.add(borderToAdd);
            startToConnectedBorder.put(pivotPointToPut, newBorderList);
        }
    }

    public Border getNextEdgeWithPivotPoint(Point2D pivotPoint, Border lastTraversedBorder){
        pivotPoint = LeafRegion.roundToCoordinatesTo4Digits(pivotPoint);

        List<Border> bordersWithPivotPoint = startToConnectedBorder.get(pivotPoint);
        if(bordersWithPivotPoint == null){
            return null;
        }

        for (Border existingBorderWithPivotPoint : bordersWithPivotPoint) {
            if(!LeafRegion.isSameBorder(existingBorderWithPivotPoint, lastTraversedBorder)){
                return existingBorderWithPivotPoint;
            }
        }
        return null;
    }

}
