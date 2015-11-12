package mapvis.gui;

import javafx.geometry.Point2D;
import mapvis.Impl.RandomColorStyler;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.graphic.RegionRenderer;
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

    private void createStartPointToEndPointMapping(List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw) {
        for (Tuple2<Tile<T>, List<Dir>> leafTile : tileAndDirectionsToDraw) {
            int x = leafTile.first.getX();
            int y = leafTile.first.getY();

            Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);

            for (Dir direction : leafTile.second) {

                int[] pointIndices = LeafRegion.DIR_TO_POINTS[direction.ordinal()];
                double xStart = LeafRegion.POINTS[pointIndices[0]] + point2D.getX();
                double xEnd = LeafRegion.POINTS[pointIndices[2]] + point2D.getX();
                double yStart = LeafRegion.POINTS[pointIndices[1]] + point2D.getY();
                double yEnd = LeafRegion.POINTS[pointIndices[3]] + point2D.getY();

                Point2D startPoint = LeafRegion.roundToCoordinatesTo4Digits(new Point2D(xStart, yStart));
                Point2D endPoint = LeafRegion.roundToCoordinatesTo4Digits(new Point2D(xEnd, yEnd));

                startToEnd.put(startPoint, endPoint);
                point2DToBorderAbstrBorder.put(startPoint, new Tuple2<>(leafTile.first.getPos(), direction));
            }
        }
    }

    public void orderBordersOfLeaves(List<Tuple2<LeafRegion, List<Tuple2<Tile<T>, List<Dir>>>>> leafRegionsToBorders){
        for (Tuple2<LeafRegion, List<Tuple2<Tile<T>, List<Dir>>>> leafRegionListEntry : leafRegionsToBorders) {
            if(leafRegionListEntry.second.size() == 0){
                continue;
            }
            orderBorders(leafRegionListEntry.second);
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

    private void orderBorders(List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw){
        initializeHashMaps();
        createStartPointToEndPointMapping(tileAndDirectionsToDraw);
//        System.out.println("Order Borders");
        int keySetSize = startToEnd.keySet().size();
        boolean circleDetected = false;
        List<Border.BorderItem> borderItems = new ArrayList<>();
        Point2D prevStartPoint = null;
//        List<Point2D> startPoints = new ArrayList<>();
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
            checkPoint(startPoint);
            checkPoint(endPoint);

            if( circleDetected() && i != 0 ){
                circleDetected = true;
                //circle detected => close circular boundary
//                startPoints.add(initialPoint);
//                startPoints.add(new Point2D(0, 0));
                appendBorderStepToBorderItemListToStartingAtPos(borderItems, initialPoint);

                createBorderAndAddtoLeaves(borderItems, getBorderLevelAtPosition(prevStartPoint));
                System.out.println("BorderCreator: Circle in iteration: " + i + "/" + keySetSize);
                borderItems = new ArrayList<>();

                //reinitialize with next point to continue with next boundaries if there
                //are any left
//                endPoint = findStartPointAtBorderChange();
                endPoint = findBeginningBorderChange();
                prevStartPoint = null;
                initialPoint = startPoint;
                startToEnd.remove(startPoint);
            }

           if(isBorderChangeRequired(prevStartPoint, startPoint)){
//               startPoints.add(startPoint);
//               startPoints.add(new Point2D(0, 0));
               appendBorderStepToBorderItemListToStartingAtPos(borderItems, startPoint);
               createBorderAndAddtoLeaves(borderItems, getBorderLevelAtPosition(prevStartPoint));
               borderItems = new ArrayList<>();

//               startPoints.add(startPoint);
               appendBorderStepToBorderItemListToStartingAtPos(borderItems, startPoint);
           }else{
//               startPoints.add(startPoint);
               appendBorderStepToBorderItemListToStartingAtPos(borderItems, startPoint);
           }
            checkPoint(startPoint);
            checkPoint(endPoint);
            prevStartPoint = startPoint;
            startPoint = endPoint;
        }

        if(circleDetected()){
            checkPoint(startPoint);

            appendBorderStepToBorderItemListToStartingAtPos(borderItems, initialPoint);
//            borderItems = new ArrayList<>();
//            startPoints.add(initialPoint);
        }
        checkPoint(startPoint);

//        if(!circleDetected)
//        if( !(initialPoint.getX() == 25 && initialPoint.getY() == 8.66 && prevStartPoint.getX() == 20.0 && prevStartPoint.getY() == 17.32) )
            createBorderAndAddtoLeaves(borderItems, getBorderLevelAtPosition(prevStartPoint));

//        if(borderItems.size() > 0){
//            createBorderAndAddtoLeaves(borderItems, getBorderLevelAtPosition(prevStartPoint));
//        }

//        List<Double> xValues = new ArrayList<>();
//        List<Double> yValues = new ArrayList<>();
//        startPoints.forEach(point2D ->  {
//            xValues.add(point2D.getX());
//            yValues.add(point2D.getY());
//        });
//        RegionRenderer.printCoordinates(xValues, yValues, "StartSjpe", "endSHape");
    }

    private void checkPoint(Point2D pointToCheck) {
        if(pointToCheck != null && pointToCheck.getX() == 20.0 && pointToCheck.getY() == 0.0){
            System.out.println();
        }
    }

    private Border<T> createBorderAndAddtoLeaves(List<Border.BorderItem> borderItems, int borderLevel) {
        Tuple2<Pos, List<Dir>> borderItem = borderItems.get(0).borderItem;
        Tile<T> innerNodeItem = grid.getTile(borderItem.first.getX(), borderItem.first.getY());
        Tile<T> outerNodeItem = grid.getNeighbour(borderItem.first.getX(), borderItem.first.getY(), borderItem.second.get(0));
        Border<T> tBorder = new Border<T>(borderItems, borderLevel);

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
//            Node item = (Node) innerNodeItem.getItem();
//            if( ((Node)nodeA).getId().equals("7")){
//                System.out.printf("");
//            }
        }
        if(outerNodeItem.getTag() == Tile.LAND){
            leafNodeToLeafRegionMap.get(outerNodeItem.getItem()).addBorder(tBorder);
//            Node item = (Node) outerNodeItem.getItem();
//            if(((Node)nodeB).getId().equals("4")){
//                System.out.printf("");
//            }
        }

        if(nodeA != null && nodeB != null && ((Node)tBorder.getNodeA()).getId().equals("7") && ((Node)tBorder.getNodeB()).getId().equals("4")){
            System.out.printf("");
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

//        return getTree().getDepth(lca) + 1;
        int level = tree.getDepth(lca) + 1;
//        if(level > maxBorderLevelToShow)
//            return 0;
        return level;
    }

    private void appendBorderStepToBorderItemListToStartingAtPos(List<Border.BorderItem> borderItems, Point2D pointToAdd) {
        Tuple2<Pos, Dir> abstrBorderItem = point2DToBorderAbstrBorder.get(pointToAdd);
        addBorderPartToList(lastPos, borderItems, abstrBorderItem);
    }


    private void addBorderPartToList(Pos lastPos, List<Border.BorderItem> borderItems, Tuple2<Pos, Dir> abstrBorderItem) {
        if(borderItems.size() > 0 && borderItems.get(borderItems.size() - 1).borderItem.first.equals(abstrBorderItem.first)){
            //append to previous list
            Tuple2<Pos, List<Dir>> borderItem = borderItems.get(borderItems.size() - 1).borderItem;
            borderItem.second.add(abstrBorderItem.second);
        }else{
            List<Dir> dirList = new ArrayList<>();
            dirList.add(abstrBorderItem.second);
            borderItems.add(new Border.BorderItem(new Tuple2<>(abstrBorderItem.first, dirList)));
        }
    }
}
