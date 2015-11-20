package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.RegionRendering.BorderCoordinatesCalcImpl;

import java.util.*;
/**
 * Created by dacc on 10/26/2015.
 */
public class LeafRegion<T> extends Region<T> {
    boolean areBodersOrdered;
    protected List<Border<T>> borders;
    public LeafRegion(T o, int level) {
        super(Collections.emptyList(), o, level);
        this.borders = new ArrayList<>();
        this.areBodersOrdered = false;
    }

    public List<Border<T>> getBorders() {
        return borders;
    }

    @Override
    public List<List<BoundaryShape<T>>> getBoundaryShape() {
        if(borders.size() == 0)
            return Collections.EMPTY_LIST;

        List<Border<T>> resultingCollection = new ArrayList<>();

        List<BoundaryShape<T>> boundaryShapes = new ArrayList<>();
        for (Border<T> border : this.borders) {
            boundaryShapes.add(border.calcBoundaryShape());
        }
        List<List<BoundaryShape<T>>> lists = BorderCoordinatesCalcImpl.orderBoundaryShapes(boundaryShapes);
        return lists;
    }

    public void addBorder(Border<T> border) {
        if(border.getBorderCoordinates().size() == 0 || border == null)
            return;

        for (Border<T> existingBorder : this.borders) {
            if(isSameBorder(border, existingBorder)){
                return;
            }
        }
        borders.add(border);
    }

    public static boolean isSameBorder(Border newBorder, Border existingBorder) {
        if(newBorder == null || existingBorder == null)
            return true;

        if(newBorder.getNodeA() == null && existingBorder.getNodeA() != null)
            return false;

        if(newBorder.getNodeB() == null && existingBorder.getNodeB() != null)
            return false;

        if(newBorder.getNodeA() != null && !newBorder.getNodeA().equals(existingBorder.getNodeA()))
            return false;

        if(newBorder.getNodeB() != null && !newBorder.getNodeB().equals(existingBorder.getNodeB()))
            return false;

        Point2D newBorderStartPoint = roundToCoordinatesTo4Digits(newBorder.getStartPoint());
        Point2D existingBorderStartPoint = roundToCoordinatesTo4Digits(existingBorder.getStartPoint());

        Point2D newBorderLastPoint = roundToCoordinatesTo4Digits(newBorder.getLastPoint());
        Point2D existingBorderLastPoint = roundToCoordinatesTo4Digits(existingBorder.getLastPoint());

        if(newBorderStartPoint.equals(existingBorderStartPoint) && newBorderLastPoint.equals(existingBorderLastPoint)
                || newBorderStartPoint.equals(existingBorderLastPoint) && newBorderLastPoint.equals(existingBorderStartPoint)){
            if(newBorder.getNodeA() != null && !newBorder.getNodeA().equals(existingBorder.getNodeA()))
                return false;
            if(newBorder.getNodeB() != null && !newBorder.getNodeB().equals(existingBorder.getNodeB()))
                return false;

            return true;
        }

        return false;
    }

    @Override
    public boolean isLeaf(){
        return true;
    }

    public static double roundTo4Digits(double val){
        return Math.round(100.0 * val) / 100.0;
    }

    public static Point2D roundToCoordinatesTo4Digits(Point2D point2D){
        return new Point2D(roundTo4Digits(point2D.getX()), roundTo4Digits(point2D.getY()));
    }

    public T getNodeItem() {
        return nodeItem;
    }

    private boolean hasBorderElementsToShow(Border<T> border, int maxLevelToCollect) {
        return border.getLevel() <= maxLevelToCollect;
    }

    @Override
    protected List<Border<T>> getBoundaryShapeForLevel(int level) {
        List<Border<T>> result = new ArrayList<>();
        for (Border<T> borderStep : borders) {
            if(borderStep.getBorderCoordinates().size() == 0)
                continue;

            if(!hasBorderElementsToShow(borderStep, level))
                continue;

            result.add(borderStep);
        }

        return result;
    }

    @Override
    public List<Region<T>> getChildRegionsAtLevel(int level) {
        List<Region<T>> result = new ArrayList<>();
        if(level >= this.getLevel()){
            result.add(this);
        }
        return result;
    }

}
