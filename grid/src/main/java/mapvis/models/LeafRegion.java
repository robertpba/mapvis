package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.graphic.RegionRendering.BoundaryShapeUtils;

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
    public List<List<IBoundaryShape<T>>> getBoundaryShape() {
        if(borders.size() == 0)
            return Collections.EMPTY_LIST;

        List<IBoundaryShape<T>> IBoundaryShapes = new ArrayList<>();
        for (Border<T> border : this.borders) {
            IBoundaryShapes.add(border.calcBoundaryShape());
        }
        List<List<IBoundaryShape<T>>> ordererBoundaryShapeList = BoundaryShapeUtils.orderBoundaryShapes(IBoundaryShapes);
        return ordererBoundaryShapeList;
    }

    public void addBorder(Border<T> border) {
        if(border.getBorderCoordinates().size() == 0 || border == null)
            return;

        for (Border<T> existingBorder : this.borders) {
            if(existingBorder.equals(border)){
                return;
            }
        }
        borders.add(border);
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
    protected List<Border<T>> getBordersForLevel(int level) {
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
