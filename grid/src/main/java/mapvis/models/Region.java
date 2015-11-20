package mapvis.models;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.geometry.Point2D;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.RegionRendering.BorderCoordinatesCalcImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dacc on 10/26/2015.
 * Region are created for each Node in the tree. Regions
 * have store their subregions as childRegions. The borders
 * the region can be calculated by accumulating the border
 * segments stored in the LeafRegions of its children.
 */
public class Region<T> {

    final T nodeItem;
    private final int level;
    private List<Region<T>> childRegions;


    public Region(List<Region<T>> childRegions, T nodeItem, int level) {
        this.childRegions = childRegions;
        this.nodeItem = nodeItem;
        this.level = level;
    }

    public boolean isLeaf(){
        return false;
    }

    public List<Region<T>> getChildRegions() {
        return childRegions;
    }

    public int getLevel() {
        return level;
    }

    public T getNodeItem() {
        return nodeItem;
    }

    public List<Region<T>> getChildRegionsAtLevel(int level){
        List<Region<T>> result = new ArrayList<>();
        if(level == this.level){
            result.add(this);
        }else if(level > this.level){
            childRegions.forEach(region -> result.addAll(region.getChildRegionsAtLevel(level)));
        }
        return result;
    }

    public List<List<BoundaryShape<T>>> getBoundaryShape(){

        List<Border<T>> resultingCollection = new ArrayList<>();
        childRegions.forEach(tRegion -> resultingCollection.addAll(getBoundaryShapeForLevel(level)));

        List<BoundaryShape<T>> boundaryShapes = new ArrayList<>();
        for (Border<T> tBorder : resultingCollection) {
            boundaryShapes.add(tBorder.calcBoundaryShape());
        }

        return BorderCoordinatesCalcImpl.orderBoundaryShapes(boundaryShapes);
    }

    List<List<BoundaryShape<T>>> calcBoundaryShapeFromOrderedBorders(List<List<Tuple2<Border<T>, Boolean>>> orderedBorders) {
        List<List<BoundaryShape<T>>> boundaryShapes = new ArrayList<>();
        for (List<Tuple2<Border<T>, Boolean>> regionBorder : orderedBorders) {
            List<BoundaryShape<T>> boundaryStep = new ArrayList<>();
            for (Tuple2<Border<T>, Boolean> borderStep : regionBorder) {
                BoundaryShape<T> borderShape = borderStep.first.calcBoundaryShape();
                borderShape.coordinatesNeedToBeReversed = borderStep.second;
                boundaryStep.add(borderShape);
            }

//            Iterator<Border<T>> currBorderIt = regionBorder.iterator();
//            Iterator<Border<T>> nextBorderIt = regionBorder.iterator();
//            if(!nextBorderIt.hasNext()){
//                boundaryShapes.add(currBorderIt.next().calcBoundaryShape());
//                continue;
//            }
//            nextBorderIt.next();
//            while (nextBorderIt.hasNext()) {
//                Border<T> nextBorder = nextBorderIt.next();
//                Border<T> currBorder = currBorderIt.next();
//
//                Point2D lastPoint = currBorder.getLastPoint();
//                boolean reverseOfNextBorderRequired = false;
//                boolean reverseOfCurrBorderRequired = false;
//                if(lastPoint.equals(nextBorder.getStartPoint())){
//                    reverseOfNextBorderRequired = false;
//                }else if(lastPoint.equals(nextBorder.getLastPoint())){
//                    reverseOfNextBorderRequired = true;
//                }else{
//                    Point2D currFistPoint = currBorder.getStartPoint();
//                    reverseOfCurrBorderRequired = true;
//                    if(currFistPoint.equals(nextBorder.getStartPoint())){
//                        reverseOfNextBorderRequired = false;
//                    }else if(currFistPoint.equals(nextBorder.getLastPoint())){
//                        reverseOfNextBorderRequired = true;
//                    }else{
//                        System.out.println("ERROR: no match possible");
//                    }
//                }
//                boundaryStep.get(boundaryShapes.size() - 1).coordinatesNeedToBeReversed = reverseOfCurrBorderRequired;
//
//                BoundaryShape<T> nextBorderStep = nextBorder.calcBoundaryShape();
//                nextBorderStep.coordinatesNeedToBeReversed = reverseOfNextBorderRequired;
//                boundaryStep.add(nextBorderStep);
//            }

            boundaryShapes.add(boundaryStep);
        }
        return boundaryShapes;
    }

    protected List<List<Tuple2<Border<T>, Boolean>>> orderBorders(final List<Border<T>> bordersToOrder) {
        UndirectedEdgeHashMapBorders undirectedEdgeHashMap = new UndirectedEdgeHashMapBorders();
        for (Border<T> border : bordersToOrder) {
            undirectedEdgeHashMap.put(border);
        }

        List<Tuple2<Border<T>, Boolean>> currentCircularBorder = new ArrayList<>();

        List<List<Tuple2<Border<T>, Boolean>>> resultingCircualBorders = new ArrayList<>();

        Point2D currentPoint = null;
        Border<T> currentBorder = null;
        for (int i = 0; i < bordersToOrder.size(); i++) {
            if(i == 0){
                //get any start edge/boundary shape and initial the current point
                //point to step from one boundary shape end to the start of the next boundary shape
                currentBorder = undirectedEdgeHashMap.getNext();
                currentPoint = currentBorder.getStartPoint();
            }else{
                currentBorder = undirectedEdgeHashMap.getNextEdgeWithPivotPoint(currentPoint, currentBorder);
            }

            if(currentBorder == null  // we found a circle since no edge starts/ends with the current point
                    //the boundary shape itself is circual
                    || currentBorder.getStartPoint().equals(currentBorder.getLastPoint())
                    ){
                resultingCircualBorders.add(currentCircularBorder);
                undirectedEdgeHashMap.remove(currentBorder);
                currentCircularBorder = new ArrayList<>();
                if(!undirectedEdgeHashMap.isEmpty()){
                    currentBorder = undirectedEdgeHashMap.getNext();
                }else{
                    break;
                }

            }
            boolean reverseRequired = false;
            //the boundary shapes are undirected => check if to continue with end or start point
            if(currentBorder.getStartPoint().equals(currentPoint)){
                currentPoint = currentBorder.getLastPoint();
//                currentBorder.setReverseRequiredForNode(false, this.getNodeItem());
//                currentBorder.coordinatesNeedToBeReversed = false;
                reverseRequired = false;
            }else{
                currentPoint = currentBorder.getStartPoint();
//                currentBorder.setReverseRequiredForNode(true, this.getNodeItem());
//                currentBorder.coordinatesNeedToBeReversed = true;
                reverseRequired = true;
            }

            undirectedEdgeHashMap.remove(currentBorder);
            currentCircularBorder.add(new Tuple2(currentBorder, reverseRequired));
        }
        if(!currentCircularBorder.isEmpty()){
            resultingCircualBorders.add(currentCircularBorder);
        }
        return resultingCircualBorders;
    }

    protected List<Border<T>> getBoundaryShapeForLevel(int level){
        List<Border<T>> resultingCollection = new ArrayList<>();
        childRegions.forEach(tRegion -> resultingCollection.addAll(tRegion.getBoundaryShapeForLevel(level)));
        return resultingCollection;
    }
}
