package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;

import java.util.List;

/**
 * Created by dacc on 11/19/2015.
 * The BoundaryShape is a temporary object used to render
 * the Borders of a Region. It stores the 2D Coordinates of
 * the corresponding border of the region.
 */
public class BoundaryShape<T> implements IBoundaryShape<T> {
    public Border<T> border;
    private double[] xCoords;
    private double[] yCoords;
    public int level;
    public Color color;



    //BoundaryShapes are according to Region-Borders. Region-Borders may have ordering of the coordinates depending
    //the region to be rendered => coordinatesNeedToBeReversed is true if the stored xCoordinates, yCoordinates have to be reversed
    //for rendering. Only then the concatenated coordinates can be connected to a correct path
    public boolean coordinatesNeedToBeReversed;

    public BoundaryShape(double[] xCoords, double[] yCoords, Border<T> border) {
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
        return xCoords.length;
    }

    @Override
    public Point2D getStartPoint(){
        if(xCoords.length > 0)
            return new Point2D(xCoords[0], yCoords[0]);
        return new Point2D(0, 0);
    }

    @Override
    public Point2D getEndPoint(){
        if(xCoords.length > 0)
            return new Point2D(xCoords[xCoords.length - 1], yCoords[yCoords.length - 1]);
        return new Point2D(0, 0);
    }

    @Override
    public double getXCoordinateEndpoint(){
        return getXCoordinateAtIndex(xCoords.length - 1);
    }

    @Override
    public double getYCoordinateEndpoint(){
        return getYCoordinateAtIndex(xCoords.length - 1);
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
            return xCoords[xCoords.length - 1 - index];
        }else{
            return xCoords[index];
        }
    }

    @Override
    public double getYCoordinateAtIndex(int index){
        if(coordinatesNeedToBeReversed){
            return yCoords[yCoords.length - 1 - index];
        }else{
            return yCoords[index];
        }
    }

    @Override
    public void setXCoordinateAtIndex(int index, double value){
        if(coordinatesNeedToBeReversed){
            xCoords[xCoords.length - 1 - index] = value;
        }else{
            xCoords[index] = value;
        }
    }

    @Override
    public void setYCoordinateAtIndex(int index, double value){
        if(coordinatesNeedToBeReversed){
            yCoords[yCoords.length - 1 - index] = value;
        }else{
            yCoords[index] = value;
        }
    }

    @Override
    public List<Double> getXCoords() {
        return null;
    }

    @Override
    public List<Double> getYCoords() {
        return null;
    }

    @Override
    public double[] getXCoordsArray() {
        return new double[0];
    }

    @Override
    public double[] getYCoordsArray() {
        return new double[0];
    }

    @Override
    public void setXCoords(List<Double> xCoords) {

    }

    @Override
    public void setYCoords(List<Double> yCoords) {

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

    public int getLevel() {
        return border.getLevel();
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
