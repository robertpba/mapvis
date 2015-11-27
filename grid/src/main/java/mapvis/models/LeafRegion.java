package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.graphic.RegionRendering.BoundaryShapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by dacc on 10/26/2015.
 * LeafRegions correspond to the LeafNodes of the Tree. LeafRegions
 * store the borders of all levels and parent Regions are able to
 * request the Borders of their level for calculating the BoundaryShape
 * defined by the Borders.
 */
public class LeafRegion<T> extends Region<T> {

    protected List<Border<T>> borders;

    public LeafRegion(T o, int level) {
        super(Collections.emptyList(), o, level);
        this.borders = new ArrayList<>();
    }

    public List<Border<T>> getBorders() {
        return borders;
    }

    @Override
    public List<List<IBoundaryShape<T>>> getBoundaryShape() {
        if(borders.size() == 0)
            return Collections.EMPTY_LIST;

        List<IBoundaryShape<T>> boundaryShapes = new ArrayList<>();
        for (Border<T> border : this.borders) {
            boundaryShapes.add(border.calcBoundaryShape());
        }

        return BoundaryShapeUtils.orderBoundaryShapes(boundaryShapes);
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
