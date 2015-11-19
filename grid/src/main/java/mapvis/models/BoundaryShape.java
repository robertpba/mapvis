package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * Created by dacc on 11/19/2015.
 * The BoundaryShape is a temporary object used to render
 * the Borders of a Region. It stores the 2D Coordinates of
 * the corresponding border of the region.
 */
public class BoundaryShape<T>{
    public Border<T> border;
    private double[] xCoords;
    private double[] yCoords;
    public int level;
    public Color color;

    //BoundaryShapes are according to Region-Borders. Region-Borders may have ordering of the coordinates depending
    //the region to be rendered => coordinateNeedToBeReversed is true if the stored xCoordinates, yCoordinates have to be reversed
    //for rendering. Only then the concatenated coordinates can be connected to a correct path
    public boolean coordinateNeedToBeReversed;

    public BoundaryShape(double[] xCoords, double[] yCoords, Border<T> border) {
        this.xCoords = xCoords;
        this.yCoords = yCoords;
        this.border = border;
        this.coordinateNeedToBeReversed = false;
    }

    public int getShapeLength(){
        return xCoords.length;
    }

    public Point2D getStartPoint(){
        if(xCoords.length > 0)
            return new Point2D(xCoords[0], yCoords[0]);
        return new Point2D(0, 0);
    }

    public Point2D getEndPoint(){
        if(xCoords.length > 0)
            return new Point2D(xCoords[xCoords.length - 1], yCoords[xCoords.length - 1]);
        return new Point2D(0, 0);
    }

    public double getXCoordinateAtIndex(int index){
        if(coordinateNeedToBeReversed){
            return xCoords[xCoords.length - 1 - index];
        }else{
            return xCoords[index];
        }
    }

    public double getYCoordinateAtIndex(int index){
        if(coordinateNeedToBeReversed){
            return yCoords[yCoords.length - 1 - index];
        }else{
            return yCoords[index];
        }
    }

    public void setXCoordinateAtIndex(int index, double value){
        if(coordinateNeedToBeReversed){
            xCoords[xCoords.length - 1 - index] = value;
        }else{
            xCoords[index] = value;
        }
    }

    public void setYCoordinateAtIndex(int index, double value){
        if(coordinateNeedToBeReversed){
            yCoords[yCoords.length - 1 - index] = value;
        }else{
            yCoords[index] = value;
        }
    }

    public double[] getXCoords() {
        return xCoords;
    }

    public double[] getYCoords() {
        return yCoords;
    }

    public void setXCoords(double[] xCoords) {
        this.xCoords = xCoords;
    }

    public void setYCoords(double[] yCoords) {
        this.yCoords = yCoords;
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
