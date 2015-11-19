package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.*;
/**
 * Created by dacc on 10/26/2015.
 */
public class LeafRegion<T> extends Region<T> {


    private Set<Border<T>> borders;
    private Color color;
    private List<BoundaryShape> boundaryShapes;

    public LeafRegion(T o, int level) {
        super(Collections.emptyList(), o, level);
        this.borders = new HashSet<>();
    }

    public static class BoundaryShape2{
        public List<Point2D> boundaryPoints;

        public BoundaryShape2() {
            boundaryPoints = new ArrayList<>();
        }
    }

    public static class BoundaryShape<T>{
        public Border<T> border;
        private double[] xValues;
        private double[] yValues;
        public List<String> text;
        public int level;
        public boolean renderColored;
        public Color color;
        public boolean coordinateNeedToBeReversed;

        public BoundaryShape(double[] xValues, double[] yValues, Border<T> border) {
            this.xValues = xValues;
            this.yValues = yValues;
            this.text = new ArrayList<>();
            this.border = border;
            this.renderColored = false;
            this.color = Color.TRANSPARENT;
            this.coordinateNeedToBeReversed = false;
        }

        public int getShapeLength(){
            return xValues.length;
        }

        public Point2D getStartPoint(){
            if(xValues.length > 0)
                return new Point2D(xValues[0], yValues[0]);
            return new Point2D(0, 0);
        }

        public Point2D getEndPoint(){
            if(xValues.length > 0)
                return new Point2D(xValues[xValues.length - 1], yValues[xValues.length - 1]);
            return new Point2D(0, 0);
        }

        public double getXValueAtIndex(int index){
            if(coordinateNeedToBeReversed){
                return xValues[xValues.length - 1 - index];
            }else{
                return xValues[index];
            }
        }

        public double getYValueAtIndex(int index){
            if(coordinateNeedToBeReversed){
                return yValues[yValues.length - 1 - index];
            }else{
                return yValues[index];
            }
        }

        public void setXValueAtIndex(int index, double value){
            if(coordinateNeedToBeReversed){
                xValues[xValues.length - 1 - index] = value;
            }else{
                xValues[index] = value;
            }
        }

        public void setYValueAtIndex(int index, double value){
            if(coordinateNeedToBeReversed){
                yValues[yValues.length - 1 - index] = value;
            }else{
                yValues[index] = value;
            }
        }

        public double[] getxValues() {
            return xValues;
        }

        public double[] getyValues() {
            return yValues;
        }

        public void setxValues(double[] xValues) {
            this.xValues = xValues;
        }

        public void setyValues(double[] yValues) {
            this.yValues = yValues;
        }

        @Override
        public String toString() {
            String result = "X" + "\n";
            for (double xValue : xValues) {
                result += xValue + "\n";
            }
            result += "Y" + "\n";
            for (double yValue : yValues) {
                result += yValue + "\n";
            }
            return result;
        }
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
