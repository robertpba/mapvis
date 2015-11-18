package mapvis.gui;

import javafx.geometry.Point2D;
import mapvis.algo.Method1;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.*;

import java.util.*;

/**
 * Created by dacc on 11/4/2015.
 * This class creates the borders for the LeafRegions created by Method1. Therefore,
 * the HexagonTiles on the borders of leaf regions are processed by "stepping" along the edges
 * of the HexagonTiles. For each region, a new border/bordersection is created for each neighboring
 * region.
 */
public class BorderCreator<T> {

    private final Map<T, LeafRegion<T>> leafNodeToLeafRegionMap;
    private final List<List<TileBorder>> leafRegionsBorders;
    private Map<Point2D, Point2D> startToEndPointOfTileBorderEdges;
    private Map<Point2D, Tuple2<Pos, Dir>> borderStartPointToGridCoordinates;

    private Point2D startPoint = null;
    private Point2D endPoint = null;
    private Point2D initialPoint = null;
    private Pos lastPos = null;

    private final Grid<T> grid;
    private final Tree2<T> tree;

    public BorderCreator(Method1<T> method1) {
        this.grid = method1.grid;
        this.tree = method1.tree;
        this.leafNodeToLeafRegionMap = method1.getLeafNodeItemToRegionMap();
        this.leafRegionsBorders = method1.getLeafRegionBoundaryCoordinates();
        initializeHashMaps();
    }

    private void initializeHashMaps(){
        this.startToEndPointOfTileBorderEdges = new HashMap<>();
        this.borderStartPointToGridCoordinates = new HashMap<>();
    }

    /**
     *  for each of the regions created in method1, borders are created
     *  defining the boundary shape of the region.
     */
    public void createBorders(){
        //process create boders for each of the leaf regions
        for (List<TileBorder> leafRegionBorders : leafRegionsBorders) {
            if(leafRegionBorders.size() == 0){
                continue;
            }
            createBorderForRegion(leafRegionBorders);
        }
    }

    /**
     * for each tile border, the edges are stored in the @startToEndPointOfTileBorderEdges. In addition,
     * the mapping from start point to the @TileBorder coordinate are stored.
     * @param tileBorders the borders for the mapping
     */
    private void createStartPointToEndPointMappingForBorders(List<TileBorder> tileBorders) {
        for (TileBorder tileBorder : tileBorders) {
            for (Dir direction : tileBorder.getDirections()) {

                //for each edge of the border store the start and end point to order the borders afterwards
                Point2D startPoint = TileBorder.calcStartPointForBorderEdge(tileBorder.getTilePos(), direction);
                Point2D endPoint = TileBorder.calcEndPointForBorderEdge(tileBorder.getTilePos(), direction);

                startPoint = LeafRegion.roundToCoordinatesTo4Digits(startPoint);
                endPoint = LeafRegion.roundToCoordinatesTo4Digits(endPoint);

                startToEndPointOfTileBorderEdges.put(startPoint, endPoint);
                borderStartPointToGridCoordinates.put(startPoint, new Tuple2<>(tileBorder.getTilePos(), direction));
            }
        }
    }


    private void createBorderForRegion(List<TileBorder> borderCoordinates){
        initializeHashMaps();
        createStartPointToEndPointMappingForBorders(borderCoordinates);

        int keySetSize = startToEndPointOfTileBorderEdges.keySet().size();
        List<TileBorder> currBorderCoordinates = new ArrayList<>();
        Point2D prevStartPoint = null;

        //iterate through the each of the edges defining the border of a region
        //use the start to endpoint hash maps to find the next edge
        for(int i = 0; i < keySetSize; i++){

            if(i == 0){
                //initialize with new start point
                reinitializeAtBorderChangeStartPoint();

                //remove passed start point to detect circles
                startToEndPointOfTileBorderEdges.remove(startPoint);
            }else{
                //continue with last start point
                endPoint = startToEndPointOfTileBorderEdges.get(startPoint);
                startToEndPointOfTileBorderEdges.remove(startPoint);
            }

            if( circleDetected() && i != 0 ){
                //circle detected => close circular boundary by appending the initial point
                appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, initialPoint);
                createBorderAndAddtoLeaveRegion(currBorderCoordinates);

                System.out.println("BorderCreator: Circle in iteration: " + i + "/" + keySetSize);

                //reinitialize with next point to continue with next border if there
                //are any left; start at a position where the level of one border changes
                currBorderCoordinates = new ArrayList<>();
                reinitializeAtBorderChangeStartPoint();
                prevStartPoint = null;
                startToEndPointOfTileBorderEdges.remove(startPoint);
            }

            //a new border section is created if the border level changes (e.g. if neighboring region has different level)
            if(isBorderChangeRequired(prevStartPoint, startPoint)){
                //append the current point and create border
                appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, startPoint);
                createBorderAndAddtoLeaveRegion(currBorderCoordinates);

                //reinitialize processed coordinates
                currBorderCoordinates = new ArrayList<>();
                appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, startPoint);
            }else{
                //just append processed edge to current border
                appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, startPoint);
            }

            //continue processing along border edge at current end point
            prevStartPoint = startPoint;
            startPoint = endPoint;
        }

        //last border of region might not be closed
        if(circleDetected()){
            appendBorderStepToBorderCollectionAtPos(currBorderCoordinates, initialPoint);
        }

        //created border for bordersCoordinates since last initialization
        createBorderAndAddtoLeaveRegion(currBorderCoordinates);
    }

    private void reinitializeAtBorderChangeStartPoint(){
        startPoint = findStartPointAtBorderChange();
        endPoint = startToEndPointOfTileBorderEdges.get(startPoint);
        initialPoint = startPoint;
    }

    /**
     * finds the point at which the neighbor of the border changed
     * changes (different neighbouring region)
     * @return the start point of the edge after the change of the neighbor
     */
    private Point2D findStartPointAtBorderChange(){
        Iterator<Point2D> iterator = startToEndPointOfTileBorderEdges.keySet().iterator();
        while (iterator.hasNext()){
            startPoint = iterator.next();
            Point2D endPoint = startToEndPointOfTileBorderEdges.get(startPoint);
            if (isBorderChangeRequired(startPoint, endPoint)) {
                return endPoint;
            }
        }
        startPoint = startToEndPointOfTileBorderEdges.keySet().iterator().next();
        return startToEndPointOfTileBorderEdges.get(startPoint);
    }

    /**
     * checks whether between the two point plain coordinates belonging to a border,
     * the neighbouring regions are different
     * @param firstBorderCoordinate
     * @param secondBorderCoordinate
     * @return true if the areas corresponding to the first and second coordinate are different, otherwise false
     */
    private boolean isBorderChangeRequired(Point2D firstBorderCoordinate, Point2D secondBorderCoordinate) {
        if(firstBorderCoordinate == null)
            return false;

        //identify the node items of the two neighbouring regions
        Tuple2<Pos, Dir> startPointTile = borderStartPointToGridCoordinates.get(secondBorderCoordinate);
        Tile<T> startItem  = grid.getNeighbour(startPointTile.first.getX(), startPointTile.first.getY(), startPointTile.second);

        Tuple2<Pos, Dir> prevStartPointTile = borderStartPointToGridCoordinates.get(firstBorderCoordinate);
        Tile<T> endItem = grid.getNeighbour(prevStartPointTile.first.getX(), prevStartPointTile.first.getY(), prevStartPointTile.second);

        //check if the tiles belong to the same node. in case they are not a Land tile, they have to have the same type
        if(startItem == null || endItem == null) //borders at empty area
            return true;
        if(startItem.getTag() != endItem.getTag()) //border at whole/water
            return true;

        //both coordinates belong to regions => border change required if they belong to different regions
        if(startItem.getTag() == Tile.LAND)
            return !startItem.getItem().equals(endItem.getItem());

        return false;
    }

    private void createBorderAndAddtoLeaveRegion(List<TileBorder> borderCoordinates) {
        //get one tile border to calc the level and identify the regions to which the borders belong
        TileBorder tileBorder = borderCoordinates.get(0);
        Pos tilePos = tileBorder.getTilePos();

        //get border tiles
        Tile<T> innerNodeItem = grid.getTile(tilePos.getX(), tilePos.getY());
        Tile<T> outerNodeItem = grid.getNeighbour(tilePos.getX(), tilePos.getY(), tileBorder.getDirections().get(0));

        //calc the level
        int borderLevel = calcBorderLevelBetweenTwoTiles(innerNodeItem, outerNodeItem);

        //first set the border; then set the nodes
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

        //add the borders to the corresponding leaf regions
        if(innerNodeItem.getTag() == Tile.LAND){
            leafNodeToLeafRegionMap.get(innerNodeItem.getItem()).addBorder(tBorder);
        }

        if(outerNodeItem.getTag() == Tile.LAND){
            leafNodeToLeafRegionMap.get(outerNodeItem.getItem()).addBorder(tBorder);
        }
    }


    private boolean circleDetected() {
        return startPoint.equals(initialPoint);
    }

    public int calcBorderLevelBetweenTwoTiles(Tile<T> t, Tile<T> tn){

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

    private void appendBorderStepToBorderCollectionAtPos(List<TileBorder> borderCoordinates, Point2D pointToAdd) {
        Tuple2<Pos, Dir> abstBorderPos = borderStartPointToGridCoordinates.get(pointToAdd);
        addBorderPartToList(lastPos, borderCoordinates, abstBorderPos);
    }


    private void addBorderPartToList(Pos lastPos, List<TileBorder> borderCoordinates, Tuple2<Pos, Dir> abstractPosition) {
        if(borderCoordinates.size() > 0 && borderCoordinates.get(borderCoordinates.size() - 1).getTilePos().equals(abstractPosition.first)){
            //append to previous list (same tile as previous)
            TileBorder tileBorder = borderCoordinates.get(borderCoordinates.size() - 1);
            tileBorder.addDirection(abstractPosition.second);
        }else{
            //create new tile border the current tile is different to the last
            List<Dir> dirList = new ArrayList<>();
            dirList.add(abstractPosition.second);
            borderCoordinates.add(new TileBorder(abstractPosition.first, dirList));
        }
    }
}
