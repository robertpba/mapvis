package mapvis.gui;

import javafx.geometry.Point2D;
import mapvis.Impl.RandomColorStyler;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.*;

import java.util.*;

/**
 * Created by dacc on 11/4/2015.
 */
public class BorderCreator<T> {

    private Map<Point2D, Point2D> startToEnd;
    private Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder;
    private Point2D startPoint = null;
    private Point2D initialPoint = null;
    private Pos lastPos = null;

    private final Region<T> world;
    private final Grid<T> grid;
    private final Tree2<T> tree;
    private final RandomColorStyler<T> styler;

    public BorderCreator(Region<T> world, Grid<T> grid, Tree2<T> tree, RandomColorStyler<T> styler) {
        this.world = world;
        this.grid = grid;
        this.tree = tree;
        this.styler = styler;
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
//                if(isVectorPointingClockWise(startPoint, endPoint)){
//                    startToEnd.put(startPoint, endPoint);
//                    point2DToBorderAbstrBorder.put(startPoint, new Tuple2<>(leafTile.first.getPos(), direction));
//                }else{
//                    startToEnd.put(endPoint, startPoint);
//                    Pos endPointTilePos = grid.getNeighbour(leafTile.first.getX(), leafTile.first.getY(), direction).getPos();
//                    point2DToBorderAbstrBorder.put(endPoint, new Tuple2<>(endPointTilePos, getOppositeDirection(direction) ));
//                }


            }
        }
    }

    private Dir getOppositeDirection(Dir direction) {
        switch (direction) {
            case N:
                return Dir.S;
            case S:
                return Dir.N;
            case SE:
                return Dir.NW;
            case SW:
                return Dir.NE;
            case NE:
                return Dir.SW;
            case NW:
                return Dir.SE;
        }
        return null;
    }

    private boolean isVectorPointingClockWise(Point2D startPoint, Point2D endPoint) {
        return (startPoint.getY() * endPoint.getX()) > (startPoint.getX() * endPoint.getY());
    }


    public void orderBordersOfLeaves(List<Tuple2<LeafRegion, List<Tuple2<Tile<T>, List<Dir>>>>> leafRegionsToBorders){
        for (Tuple2<LeafRegion, List<Tuple2<Tile<T>, List<Dir>>>> leafRegionListEntry : leafRegionsToBorders) {
            if(leafRegionListEntry.second.size() == 0){
                continue;
            }
            List<Border<T>> borders = orderBorders(leafRegionListEntry.second);
            leafRegionListEntry.first.setBorders(borders);
        }
    }
//    private Point2D findBeginningBorderChange(){
//        Iterator<Point2D> iterator = startToEnd.keySet().iterator();
//        startPoint = iterator.next();
//        int prevBorderLevel = -1;
//        for(int i = 0; i < startToEnd.keySet().size(); i++){
//            int borderLevelAtPosition = getBorderLevelAtPosition(point2DToBorderAbstrBorder.get(startPoint));
//            if(prevBorderLevel != -1 && prevBorderLevel != borderLevelAtPosition){
//                return startToEnd.get(startPoint);
//            }
//            prevBorderLevel = borderLevelAtPosition;
//            startPoint = startToEnd.get(startPoint);
//        }
//        return startToEnd.get(startPoint);
//    }
    private Point2D findBeginningBorderChange(){
        startPoint = findStartPointAtBorderChange();
        return startToEnd.get(startPoint);
    }

    private Point2D findStartPointAtBorderChange(){
        Iterator<Point2D> iterator = startToEnd.keySet().iterator();
        startPoint = iterator.next();
        int prevBorderLevel = -1;
        Point2D prevStartPoint = null;
        for(int i = 0; i < startToEnd.keySet().size(); i++){

            int borderLevelAtPosition = getBorderLevelAtPosition(point2DToBorderAbstrBorder.get(startPoint));
//            if(prevBorderLevel != -1 && prevBorderLevel != borderLevelAtPosition){
            if (isBorderChangeRequired(prevStartPoint, startPoint)) {

                Point2D resultingEndpoint = startPoint;
                startPoint = prevStartPoint;
                return resultingEndpoint;
            }
            prevBorderLevel = borderLevelAtPosition;
            prevStartPoint = startPoint;
            startPoint = startToEnd.get(startPoint);
        }
        return startToEnd.get(startPoint);
    }

    private boolean isBorderChangeRequired(Point2D prevStartPoint, Point2D startPoint) {
        if(prevStartPoint == null)
            return false;

        int borderLevelAtStartPoint = getBorderLevelAtPosition(point2DToBorderAbstrBorder.get(startPoint));
        int borderLevelAtPrevStartPoint = getBorderLevelAtPosition(point2DToBorderAbstrBorder.get(prevStartPoint));
        return borderLevelAtStartPoint != borderLevelAtPrevStartPoint;
    }

    private List<Border<T>> orderBorders(List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw){
        initializeHashMaps();
        createStartPointToEndPointMapping(tileAndDirectionsToDraw);


        int keySetSize = startToEnd.keySet().size();

        List<Border.BorderItem> borderItems = new ArrayList<>();
        List<Border<T>> borders = new ArrayList<>();
        int prevBorderLevel = -1;
        Point2D prevStartPoint = null;
        for(int i = 0; i < keySetSize; i++){
            Point2D endPoint = null;

            if(i == 0){
                //initialize with new startpoint
                endPoint = findBeginningBorderChange();
                initialPoint = startPoint;
                prevBorderLevel = getBorderLevelAtCurrentPos();
            }else{
                //continue with last startpoint
                endPoint = startToEnd.get(startPoint);
            }

            if( circleDetected() && i != 0 ){
                //circle detected => close circular boundary
                appendBorderStepToBorderItemListToStartingAtPos(borderItems, initialPoint);

                borders.add(createBorder(borderItems, prevBorderLevel));
                borderItems = new ArrayList<>();

                //reinitialize with next point to continue with next boundaries if there
                //are any left
                endPoint = findStartPointAtBorderChange();
                initialPoint = startPoint;
                prevBorderLevel = getBorderLevelAtCurrentPos();
            }

           if(isBorderChangeRequired(prevStartPoint, startPoint)){
//         if(prevBorderLevel != -1 && prevBorderLevel != getBorderLevelAtCurrentPos()){
                   appendBorderStepToBorderItemListToStartingAtPos(borderItems, startPoint);
                   borders.add(createBorder(borderItems, prevBorderLevel));
                   borderItems = new ArrayList<>();

                   prevBorderLevel = getBorderLevelAtCurrentPos();
                   appendBorderStepToBorderItemListToStartingAtPos(borderItems, startPoint);
           }else{
               appendBorderStepToBorderItemListToStartingAtPos(borderItems, startPoint);
           }

            prevStartPoint = startPoint;
            startPoint = endPoint;
        }
//        closeBorderIfCircular(borderItems, initialPoint);
//        appendBorderStepToBorderItemListToStartingAtPos(borderItems, initialPoint);
        if(circleDetected()){
            appendBorderStepToBorderItemListToStartingAtPos(borderItems, initialPoint);
        }
        borders.add(createBorder(borderItems, prevBorderLevel));
        return borders;

    }

    private void closeBorderIfCircular(List<Border.BorderItem> borderItems, Point2D initialPoint) {
//        if(borderItems.size() > 0 && borderItems.get(borderItems.size() - 1))
    }

    private Border<T> createBorder(List<Border.BorderItem> borderItems, int prevBorderLevel) {
//        borderItems.add(borderItems.get(0));
        return new Border<T>(borderItems, prevBorderLevel);
    }

    private boolean borderLevelChanged(int prevLevel, int level) {
        return prevLevel != level;
    }

    private boolean circleDetected() {
        return startPoint.equals(initialPoint);
    }

    private int getBorderLevelAtCurrentPos(){
        return getBorderLevelAtPosition(point2DToBorderAbstrBorder.get(startPoint));
    }

    private int getBorderLevelAtPosition(Tuple2<Pos, Dir> addedBorderPart) {
        return styler.calcLevel(addedBorderPart.first.getX(), addedBorderPart.first.getY(), addedBorderPart.second);
    }

    private void appendBorderStepToBorderItemListToStartingAtPos(List<Border.BorderItem> borderItems, Point2D pointToAdd) {
        Tuple2<Pos, Dir> abstrBorderItem = point2DToBorderAbstrBorder.get(pointToAdd);
        addBorderPartToList(lastPos, borderItems, abstrBorderItem);
//        lastPos = abstrBorderItem.first;
    }

    private Point2D reinitializeAtNextPoint() {
        startPoint = startToEnd.keySet().iterator().next();
        Point2D endPoint = startToEnd.get(startPoint);
        return endPoint;
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
