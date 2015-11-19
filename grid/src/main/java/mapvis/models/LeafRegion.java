package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.*;
/**
 * Created by dacc on 10/26/2015.
 */
public class LeafRegion<T> extends Region<T> {

    private Set<Border<T>> borders;

    public LeafRegion(T o, int level) {
        super(Collections.emptyList(), o, level);
        this.borders = new HashSet<>();
    }

    public Set<Border<T>> getBorders() {
        return borders;
    }

    public void addBorder(Border<T> border) {
        if(border.getBorderCoordinates().size() == 0 || border == null)
            return;

        for (Border<T> tBorder : this.borders) {
            if(isSameBorder(border, tBorder)){
                return;
            }
        }
        this.borders.add(border);

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
}
