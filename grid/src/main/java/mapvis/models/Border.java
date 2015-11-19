package mapvis.models;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dacc on 10/28/2015.
 * Border basically store a @TileBorders. A border is the
 * concatenation of subsequent @TileBorders. The border separate
 * one Region by another. The concatenation of all Borders around
 * one Region defines the Area of the Region. However, the Border
 * datastructure only saves that segment of the entire boundary
 * Shape of a Region where the two separated Regions stay the same.
 */
public class Border<T> {

    //the level as defined by the two separated Regions
    private int level;
    private int renderID;

    //Nodeitems of the Tree belonging to the two separated Regions
    //nodeA and nodeB are assigned according to their hash values
    //to be able to filter the borders created for neighboring regions
    private T nodeA;
    private T nodeB;

    private List<TileBorder> borderCoordinates;

    public Border() {
        this.level = -1;
        this.renderID = -1;
        this.borderCoordinates = new ArrayList<>();
    }

    public Border(List<TileBorder> borderCoordinates, int level) {
        this.renderID = -1;
        this.level = level;
        this.borderCoordinates = borderCoordinates;
    }

    public List<TileBorder> getBorderCoordinates() {
        return Collections.unmodifiableList(borderCoordinates);
    }

    public T getNodeA() {
        return nodeA;
    }

    public T getNodeB() {
        return nodeB;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setNodes(T nodeA, T nodeB){
        if(nodeA == null || nodeB == null){
            if(nodeA == null){
                this.nodeA = nodeB;
                this.nodeB = null;
            }else{
                this.nodeA = nodeA;
                this.nodeB = null;
            }
            return;
        }
        //use hash code to order them
        int hashNodeA = nodeA.hashCode();
        int hashNodeB = nodeB.hashCode();
        if(hashNodeA < hashNodeB){
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }else{
            this.nodeA = nodeB;
            this.nodeB = nodeA;
        }
    }

    public int getRenderID() {
        return renderID;
    }

    public void setRenderID(int renderID) {
        this.renderID = renderID;
    }

    public Point2D getStartPoint(){
        if(borderCoordinates.isEmpty())
            return null;

        TileBorder borderCoordinates = this.borderCoordinates.get(0);
        if(borderCoordinates.getDirections().isEmpty())
            return null;

        Dir dir = borderCoordinates.getDirections().get(0);
        return TileBorder.calcStartPointForBorderEdge(borderCoordinates.getTilePos(), dir);
    }

    public Point2D getLastPoint(){
        if(borderCoordinates.isEmpty())
            return null;
        TileBorder borderCoordinate = this.borderCoordinates.get(this.borderCoordinates.size() - 1);

        List<Dir> directions = borderCoordinate.getDirections();
        if(directions.isEmpty())
            return null;

        Dir dir = directions.get(directions.size() - 1);
        return TileBorder.calcStartPointForBorderEdge(borderCoordinate.getTilePos(), dir);
    }

}
