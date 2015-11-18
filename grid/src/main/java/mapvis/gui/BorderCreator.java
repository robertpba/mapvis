package mapvis.gui;

import javafx.geometry.Point2D;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.*;

import java.util.*;

/**
 * Created by dacc on 11/4/2015.
 */
public class BorderCreator<T> {

    private final Map<T, LeafRegion<T>> leafNodeToLeafRegionMap;
    private Map<Point2D, Point2D> startToEnd;
    private Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder;
    private Point2D startPoint = null;
    private Point2D initialPoint = null;
    private Pos lastPos = null;

    private final Region<T> world;
    private final Grid<T> grid;
    private final Tree2<T> tree;

    public BorderCreator(Region<T> world, Grid<T> grid, Tree2<T> tree, Map<T, LeafRegion<T>> leafNodeToLeafRegionMap) {
        this.world = world;
        this.grid = grid;
        this.tree = tree;
        this.leafNodeToLeafRegionMap = leafNodeToLeafRegionMap;
        initializeHashMaps();
    }

    private void initializeHashMaps(){
        this.startToEnd = new HashMap<>();
        this.point2DToBorderAbstrBorder = new HashMap<>();
    }

    private void createStartPointToEndPointMapping(List<GridCoordinateCollection> tileAndDirectionsToDraw) {
        for (GridCoordinateCollection borderCoordinates : tileAndDirectionsToDraw) {

            for (Dir direction : borderCoordinates.getDirections()) {

                Point2D startPoint = GridCoordinateCollection.calcStartPointForBorderEdge(borderCoordinates.getTilePos(), direction);
                Point2D endPoint = GridCoordinateCollection.calcEndPointForBorderEdge(borderCoordinates.getTilePos(), direction);

                startPoint = LeafRegion.roundToCoordinatesTo4Digits(startPoint);
                endPoint = LeafRegion.roundToCoordinatesTo4Digits(endPoint);

                startToEnd.put(startPoint, endPoint);
                point2DToBorderAbstrBorder.put(startPoint, new Tuple2<>(borderCoordinates.getTilePos(), direction));
            }
        }
    }

    public void orderBordersOfLeaves(List<List<GridCoordinateCollection>> leafRegionsToBorders){
        for (List<GridCoordinateCollection> leafRegionBoundaryCoordinates : leafRegionsToBorders) {
            if(leafRegionBoundaryCoordinates.size() == 0){
                continue;
            }
            orderBorders(leafRegionBoundaryCoordinates);
        }
    }

    private Point2D findBeginningBorderChange(){
        startPoint = findStartPointAtBorderChange();
        return startToEnd.get(startPoint);
    }

    private Point2D findStartPointAtBorderChange(){
        Iterator<Point2D> iterator = startToEnd.keySet().iterator();
        while (iterator.hasNext()){
            startPoint = iterator.next();
            Point2D endPoint = startToEnd.get(startPoint);
            if (isBorderChangeRequired(startPoint, endPoint)) {
                return endPoint;
            }
        }
        startPoint = startToEnd.keySet().iterator().next();
        return startToEnd.get(startPoint);
    }

    private boolean isBorderChangeRequired(Point2D prevStartPoint, Point2D startPoint) {
        if(prevStartPoint == null)
            return false;
        Tuple2<Pos, Dir> startPointTile = point2DToBorderAbstrBorder.get(startPoint);
        Tile<T> startItem  = grid.getNeighbour(startPointTile.first.getX(), startPointTile.first.getY(), startPointTile.second);

        Tuple2<Pos, Dir> prevStartPointTile = point2DToBorderAbstrBorder.get(prevStartPoint);
        Tile<T> endItem = grid.getNeighbour(prevStartPointTile.first.getX(), prevStartPointTile.first.getY(), prevStartPointTile.second);

        //check if the tiles belong to the same node. in case they are not a Land tile, they have to have the same type
        if(startItem == null || endItem == null)
            return true;
        if(startItem.getTag() != endItem.getTag())
            return true;
        if(startItem.getTag() == Tile.LAND)
            return !startItem.getItem().equals(endItem.getItem());

        return false;
    }

    private void orderBorders(List<GridCoordinateCollection> borderCoordinates){
        initializeHashMaps();
        createStartPointToEndPointMapping(borderCoordinates);

        int keySetSize = startToEnd.keySet().size();
        List<GridCoordinateCollection> currBorderCoordinates = new ArrayList<>();
        Point2D prevStartPoint = null;

        for(int i = 0; i < keySetSize; i++){
            Point2D endPoint = null;

            if(i == 0){
                //initialize with new startpoint
                endPoint = findBeginningBorderChange();
                initialPoint = startPoint;
                startToEnd.remove(startPoint);
            }else{
                //continue with last startpoint
                endPoint = startToEnd.get(startPoint);
                startToEnd.remove(startPoint);
            }
            if( circleDetected() && i != 0 ){
                //circle detected => close circular boundary
                appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, initialPoint);

                createBorderAndAddtoLeaves(currBorderCoordinates, getBorderLevelAtPosition(prevStartPoint));
                System.out.println("BorderCreator: Circle in iteration: " + i + "/" + keySetSize);
                currBorderCoordinates = new ArrayList<>();

                //reinitialize with next point to continue with next boundaries if there
                //are any left
//                endPoint = findStartPointAtBorderChange();
                endPoint = findBeginningBorderChange();
                prevStartPoint = null;
                initialPoint = startPoint;
                startToEnd.remove(startPoint);
            }

           if(isBorderChangeRequired(prevStartPoint, startPoint)){
               appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, startPoint);
               createBorderAndAddtoLeaves(currBorderCoordinates, getBorderLevelAtPosition(prevStartPoint));
               currBorderCoordinates = new ArrayList<>();

               appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, startPoint);
           }else{
               appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, startPoint);
           }
            prevStartPoint = startPoint;
            startPoint = endPoint;
        }

        if(circleDetected()){
            appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, initialPoint);
        }

        createBorderAndAddtoLeaves(currBorderCoordinates, getBorderLevelAtPosition(prevStartPoint));
    }

    private Border<T> createBorderAndAddtoLeaves(List<GridCoordinateCollection> borderCoordinates, int borderLevel) {
        GridCoordinateCollection borderCoordinateColl = borderCoordinates.get(0);
        Pos tilePos = borderCoordinateColl.getTilePos();

        Tile<T> innerNodeItem = grid.getTile(tilePos.getX(), tilePos.getY());
        Tile<T> outerNodeItem = grid.getNeighbour(tilePos.getX(), tilePos.getY(), borderCoordinateColl.getDirections().get(0));

        Border<T> tBorder = new Border<T>(borderCoordinates, borderLevel);

        T nodeA = null;
        T nodeB = null;
        if(innerNodeItem.getTag() == Tile.LAND){
            nodeA = innerNodeItem.getItem();
        }

        if(outerNodeItem.getTag() == Tile.LAND){
            nodeB = outerNodeItem.getItem();
        }

        tBorder.setNodes(nodeA, nodeB);

        if(innerNodeItem.getTag() == Tile.LAND){
            leafNodeToLeafRegionMap.get(innerNodeItem.getItem()).addBorder(tBorder);
        }

        if(outerNodeItem.getTag() == Tile.LAND){
            leafNodeToLeafRegionMap.get(outerNodeItem.getItem()).addBorder(tBorder);
        }

        return tBorder;
    }


    private boolean circleDetected() {
        return startPoint.equals(initialPoint);
    }


    private int getBorderLevelAtPosition(Point2D point2D){
        return getBorderLevelAtPosition(point2DToBorderAbstrBorder.get(point2D));
    }

    private int getBorderLevelAtPosition(Tuple2<Pos, Dir> addedBorderPart) {
        return calcLevel(addedBorderPart.first.getX(), addedBorderPart.first.getY(), addedBorderPart.second);
    }

    public int calcLevel(int x, int y, Dir dir){
        //calc level of border at tile position and direction
        Tile<T> t = grid.getTile(x, y);
        Tile<T> tn = grid.getNeighbour(x, y, dir);
        if (t.getItem() == null || tn.getItem() == null || t.getItem() == tn.getItem())
            return 0;
        if (t.getTag() == Tile.SEA)
            return 0;
        if (tn.getTag() == Tile.SEA)
            return 0;


        T lca = tree.getLCA(t.getItem(), tn.getItem());
        if (lca == null) return 0;

        int level = tree.getDepth(lca) + 1;

        return level;
    }

    private void appendBorderStepToBorderCollectionAtPos(List<GridCoordinateCollection> borderCoordinates, Point2D pointToAdd) {
        Tuple2<Pos, Dir> abstBorderPos = point2DToBorderAbstrBorder.get(pointToAdd);
        addBorderPartToList(lastPos, borderCoordinates, abstBorderPos);
    }


    private void addBorderPartToList(Pos lastPos, List<GridCoordinateCollection> borderCoordinates, Tuple2<Pos, Dir> abstractPosition) {
        if(borderCoordinates.size() > 0 && borderCoordinates.get(borderCoordinates.size() - 1).getTilePos().equals(abstractPosition.first)){
            //append to previous list
            GridCoordinateCollection gridCoordinateCollection = borderCoordinates.get(borderCoordinates.size() - 1);
            gridCoordinateCollection.addDirection(abstractPosition.second);
        }else{
            List<Dir> dirList = new ArrayList<>();
            dirList.add(abstractPosition.second);
            borderCoordinates.add(new GridCoordinateCollection(abstractPosition.first, dirList));
        }
    }
}
