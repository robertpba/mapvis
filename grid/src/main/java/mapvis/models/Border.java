package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.RegionBorderRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dacc on 10/28/2015.
 */
public class Border<T> {

    private int level;
    private int renderID;

    private T nodeA;
    private T nodeB;

    private List<GridCoordinateCollection> borderCoordinates;

    public Border() {
        this.level = -1;
        this.renderID = -1;
        this.borderCoordinates = new ArrayList<>();
    }

    public Border(List<GridCoordinateCollection> borderCoordinates, int level) {
        this.renderID = -1;
        this.level = level;
        this.borderCoordinates = borderCoordinates;
    }

    public List<GridCoordinateCollection> getBorderCoordinates() {
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

        GridCoordinateCollection borderCoordinates = this.borderCoordinates.get(0);
        if(borderCoordinates.getDirections().isEmpty())
            return null;

        Dir dir = borderCoordinates.getDirections().get(0);
        return GridCoordinateCollection.calcStartPointForBorderEdge(borderCoordinates.getTilePos(), dir);
    }

    public Point2D getLastPoint(){
        if(borderCoordinates.isEmpty())
            return null;
        GridCoordinateCollection borderCoordinate = this.borderCoordinates.get(this.borderCoordinates.size() - 1);

        List<Dir> directions = borderCoordinate.getDirections();
        if(directions.isEmpty())
            return null;

        Dir dir = directions.get(directions.size() - 1);
        return GridCoordinateCollection.calcStartPointForBorderEdge(borderCoordinate.getTilePos(), dir);
    }

}
