package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;

import java.util.*;

/**
 * Created by dacc on 11/24/2015.
 */
public class BoundaryShape<T> implements IBoundaryShape<T>, Iterable<Point2D> {
    public Border<T> border;
//    private List<Double> xCoords;
//    private List<Double> yCoords;
    private List<Point2D> shapeCoordinates;
    public int level;
    public Color color;



    //BoundaryShapes are according to Region-Borders. Region-Borders may have ordering of the coordinates depending
    //the region to be rendered => coordinatesNeedToBeReversed is true if the stored xCoordinates, yCoordinates have to be reversed
    //for rendering. Only then the concatenated coordinates can be connected to a correct path
    public boolean coordinatesNeedToBeReversed;

//    public BoundaryShape(List<Double> xCoords, List<Double> yCoords, Border<T> border) {
//        this.xCoords = xCoords;
//        this.yCoords = yCoords;
//        this.border = border;
//        this.coordinatesNeedToBeReversed = false;
//    }

    public BoundaryShape(List<Point2D> shapeCoordinates, Border<T> tBorder) {
        this.border = tBorder;
        this.coordinatesNeedToBeReversed = false;
        this.shapeCoordinates = shapeCoordinates;
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
        return shapeCoordinates.size();
    }

    @Override
    public Point2D getStartPoint(){
        if(shapeCoordinates.size() > 0)
            return getCoordinateAtIndex(0);
        return new Point2D(0, 0);
    }

    @Override
    public Point2D getEndPoint(){
        if(shapeCoordinates.size() > 0)
            return getCoordinateAtIndex(getShapeLength() - 1);
        return new Point2D(0, 0);
    }

    @Override
    public double getXCoordinateEndpoint(){
        return getYCoordinateAtIndex(shapeCoordinates.size() - 1);
    }

    @Override
    public double getYCoordinateEndpoint(){
        return getYCoordinateAtIndex(shapeCoordinates.size() - 1);
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
            return shapeCoordinates.get(shapeCoordinates.size() - 1 - index).getX();
        }else{
            return shapeCoordinates.get(index).getX();
        }
    }

    @Override
    public double getYCoordinateAtIndex(int index){
        if(coordinatesNeedToBeReversed){
            return shapeCoordinates.get(shapeCoordinates.size() - 1 - index).getY();
        }else{
            return shapeCoordinates.get(index).getY();
        }
    }

    @Override
    public void setXCoordinateAtIndex(int index, double value){
        Point2D newCoordinate = new Point2D(value, getYCoordinateAtIndex(index));
        if(coordinatesNeedToBeReversed){
            shapeCoordinates.set(shapeCoordinates.size() - 1 - index, newCoordinate);
        }else{
            shapeCoordinates.set(index, newCoordinate);
        }
    }

    @Override
    public void setYCoordinateAtIndex(int index, double value){
        Point2D newCoordinate = new Point2D(getXCoordinateAtIndex(index), value);
        if(coordinatesNeedToBeReversed){
            shapeCoordinates.set(shapeCoordinates.size() - 1 - index, newCoordinate);
        }else{
            shapeCoordinates.set(index, newCoordinate);
        }
    }

    @Override
    public List<Point2D> getCoordinates() {
        if(coordinatesNeedToBeReversed){
            Collections.reverse(this.shapeCoordinates);
            coordinatesNeedToBeReversed = false;
        }
        return this.shapeCoordinates;
    }

    @Override
    public Point2D getCoordinateAtIndex(int index) {
        if(index < shapeCoordinates.size()){
            if(coordinatesNeedToBeReversed){
                return shapeCoordinates.get(shapeCoordinates.size() - 1 - index);
            }else{
                return shapeCoordinates.get(index);
            }
        }
        return new Point2D(0, 0);
    }

    @Override
    public void setCoordinates(List<Point2D> coordinates) {
        this.shapeCoordinates = coordinates;
    }

//    @Override
//    public void setXCoords(List<Double> xCoords) {
//        this.xCoords = xCoords;
//    }
//
//    @Override
//    public void setYCoords(List<Double> yCoords) {
//        this.yCoords = yCoords;
//    }

//    @Override
//    public List<Double> getXCoords() {
//        if(coordinatesNeedToBeReversed) {
//            Collections.reverse(xCoords);
//            Collections.reverse(yCoords);
//            coordinatesNeedToBeReversed = false;
//        }
//        return xCoords;
//    }
//
//    @Override
//    public List<Double> getYCoords() {
//        if(coordinatesNeedToBeReversed) {
//            Collections.reverse(xCoords);
//            Collections.reverse(yCoords);
//            coordinatesNeedToBeReversed = false;
//        }
//        return yCoords;
//    }
//
//    @Override
//    public double[] getXCoordsArray() {
//        return xCoords.stream().mapToDouble(Double::doubleValue).toArray();
//    }
//
//    @Override
//    public double[] getYCoordsArray() {
//        return yCoords.stream().mapToDouble(Double::doubleValue).toArray();
//    }

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
        for (Point2D coordinate : this) {
            result += coordinate.getX() + "\n";
        }
        result += "Y" + "\n";
        for (Point2D coordinate : this) {
            result += coordinate.getY() + "\n";
        }
        return result;
    }

    @Override
    public Iterator<Point2D> iterator() {
        return new Iterator<Point2D>(){
            private int currIndex = 0;
            private Iterator<Point2D> iterator = shapeCoordinates.iterator();
//            private ListIterator<Point2D> listIterator = null;
//            {
//                if(coordinatesNeedToBeReversed){
//                    listIterator = shapeCoordinates.listIterator(shapeCoordinates.size() - 1);
//                }else{
//                    listIterator = shapeCoordinates.listIterator();
//                }
//
//            }
            public boolean hasNext() {
                if(currIndex < shapeCoordinates.size())
                    return true;
                return false;
            }

            @Override
            public Point2D next() {

                Point2D result = null;
                if(coordinatesNeedToBeReversed){
                    result = getCoordinateAtIndex(currIndex);
                }else{
                    result = iterator.next();
                }
                currIndex++;
                return result;
            }
        };

    }
}
