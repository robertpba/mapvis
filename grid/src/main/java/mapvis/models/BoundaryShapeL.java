package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dacc on 11/24/2015.
 */
public class BoundaryShapeL<T> implements IBoundaryShape<T> {
    public Border<T> border;
    private List<Double> xCoords;
    private List<Double> yCoords;
    public int level;
    public Color color;



    //BoundaryShapes are according to Region-Borders. Region-Borders may have ordering of the coordinates depending
    //the region to be rendered => coordinatesNeedToBeReversed is true if the stored xCoordinates, yCoordinates have to be reversed
    //for rendering. Only then the concatenated coordinates can be connected to a correct path
    public boolean coordinatesNeedToBeReversed;

    public BoundaryShapeL(List<Double> xCoords, List<Double> yCoords, Border<T> border) {
        this.xCoords = xCoords;
        this.yCoords = yCoords;
        this.border = border;
        this.coordinatesNeedToBeReversed = false;
    }

    @Override
    public Tuple2<T, T> getSeperatedRegionsID(int maxLevel, Tree2<T> tree){
        T nodeA = border.getNodeA();
        T nodeB = border.getNodeB();
        T regionNodeA = null;
        T regionNodeB = null;
        if(nodeA != null){
            List<T> pathToNodeA = tree.getPathToNode(nodeA);

            if(maxLevel >= pathToNodeA.size()){
                regionNodeA = nodeA;
            }else{
                regionNodeA = pathToNodeA.get(maxLevel);
            }

        }
        if(nodeB != null){
            List<T> pathToNodeB = tree.getPathToNode(nodeB);

            if(maxLevel >= pathToNodeB.size()){
                regionNodeB = nodeB;
            }else{
                regionNodeB = pathToNodeB.get(maxLevel);
            }
        }
        return new Tuple2<>(regionNodeA, regionNodeB);
    }

    @Override
    public int getShapeLength(){
        return xCoords.size();
    }

    @Override
    public Point2D getStartPoint(){
        if(xCoords.size() > 0)
            return new Point2D(xCoords.get(0), yCoords.get(0));
        return new Point2D(0, 0);
    }

    @Override
    public Point2D getEndPoint(){
        if(xCoords.size() > 0)
            return new Point2D(xCoords.get(xCoords.size() - 1), yCoords.get(yCoords.size() - 1));
        return new Point2D(0, 0);
    }

    @Override
    public double getXCoordinateEndpoint(){
        return getXCoordinateAtIndex(xCoords.size() - 1);
    }

    @Override
    public double getYCoordinateEndpoint(){
        return getYCoordinateAtIndex(xCoords.size() - 1);
    }

    @Override
    public double getXCoordinateStartpoint(){
        return getXCoordinateAtIndex(0);
    }

    @Override
    public double getYCoordinateStartpoint(){
        return getYCoordinateAtIndex(0);
    }


    @Override
    public double getXCoordinateAtIndex(int index){
        if(coordinatesNeedToBeReversed){
            return xCoords.get(xCoords.size() - 1 - index);
        }else{
            return xCoords.get(index);
        }
    }

    @Override
    public double getYCoordinateAtIndex(int index){
        if(coordinatesNeedToBeReversed){
            return yCoords.get(yCoords.size() - 1 - index);
        }else{
            return yCoords.get(index);
        }
    }

    @Override
    public void setXCoordinateAtIndex(int index, double value){
        if(coordinatesNeedToBeReversed){
            xCoords.set(xCoords.size() - 1 - index, value);
        }else{
            xCoords.set(index, value);
        }
    }

    @Override
    public void setYCoordinateAtIndex(int index, double value){
        if(coordinatesNeedToBeReversed){
            yCoords.set(yCoords.size() - 1 - index, value);
        }else{
            yCoords.set(index, value);
        }
    }

    @Override
    public void setXCoords(List<Double> xCoords) {
        this.xCoords = xCoords;
    }

    @Override
    public void setYCoords(List<Double> yCoords) {
        this.yCoords = yCoords;
    }

    @Override
    public List<Double> getXCoords() {
        if(coordinatesNeedToBeReversed) {
            Collections.reverse(xCoords);
            Collections.reverse(yCoords);
            coordinatesNeedToBeReversed = false;
        }
        return xCoords;
    }

    @Override
    public List<Double> getYCoords() {
        if(coordinatesNeedToBeReversed) {
            Collections.reverse(xCoords);
            Collections.reverse(yCoords);
            coordinatesNeedToBeReversed = false;
        }
        return yCoords;
    }

    @Override
    public double[] getXCoordsArray() {
        return xCoords.stream().mapToDouble(Double::doubleValue).toArray();
    }

    @Override
    public double[] getYCoordsArray() {
        return yCoords.stream().mapToDouble(Double::doubleValue).toArray();
    }

//    @Override
//    public void setXCoords(double[] xCoords) {
//        this.xCoords = xCoords;
//    }
//
//    @Override
//    public void setYCoords(double[] yCoords) {
//        this.yCoords = yCoords;
//    }

    @Override
    public int getLevel() {
        return border.getLevel();
    }

    @Override
    public Border<T> getFirstBorder() {
        return border;
    }

    @Override
    public boolean isCoordinatesNeedToBeReversed() {
        return coordinatesNeedToBeReversed;
    }

    @Override
    public void setCoordinatesNeedToBeReversed(boolean coordinatesNeedToBeReversed) {
        this.coordinatesNeedToBeReversed = coordinatesNeedToBeReversed;
    }

    @Override
    public String toString() {
        String result = "X" + "\n";
        for (double xValue : xCoords) {
            result += xValue + "\n";
        }
        result += "Y" + "\n";
        for (double yValue : yCoords) {
            result += yValue + "\n";
        }
        return result;
    }
}
