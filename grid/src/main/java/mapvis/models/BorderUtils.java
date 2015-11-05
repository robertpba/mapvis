package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;

import java.util.*;

/**
 * Created by dacc on 10/29/2015.
 */
public class BorderUtils {

    private static <T> void createStartPointToEndPointMapping(List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw,
                                                          Map<Point2D, Point2D> startToEnd,
                                                          Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder) {
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

    public static <T> List<Border<T>> orderBorders(List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw){

        Map<Point2D, Point2D> startToEnd = new HashMap<>();
        Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder = new HashMap<>();
        createStartPointToEndPointMapping(tileAndDirectionsToDraw, startToEnd, point2DToBorderAbstrBorder);

        List<LeafRegion.BoundaryShape> computedBoundaryShapes = new ArrayList<>();

        Point2D startPoint = null;
        Point2D initialPoint = null;
        Pos lastPos = null;

        int keySetSize = startToEnd.keySet().size();

        List<Border.BorderItem> borderItems = new ArrayList<>();
        List<Border<T>> borders = new ArrayList<>();
        for(int i = 0; i < keySetSize; i++){

            if(i == 0){
                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
            }

            Point2D endPoint = startToEnd.get(startPoint);
            if(startPoint.equals(initialPoint) && i != 0){
                System.out.println("Circle Detected!:" + i);
                System.out.println("new Shape!\n------------------------");

                Tuple2<Pos, Dir> abstrBorderItem = point2DToBorderAbstrBorder.get(initialPoint);
                addBorderPartToList(lastPos, borderItems, abstrBorderItem);
                lastPos = abstrBorderItem.first;

                borders.add(new Border<T>(borderItems));
                borderItems = new ArrayList<>();

                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
                endPoint = startToEnd.get(startPoint);
            }
            if(endPoint == null){
                //no circular boundary => found endpoint of boundary
                lastPos = null;

                borders.add(new Border<T>(borderItems));
                borderItems = new ArrayList<>();

                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
                endPoint = startToEnd.get(startPoint);
                System.out.println("detected end of boundary");
            }else{
                Tuple2<Pos, Dir> abstrBorderItem = point2DToBorderAbstrBorder.get(startPoint);
                addBorderPartToList(lastPos, borderItems, abstrBorderItem);
                lastPos = abstrBorderItem.first;
            }

            //remove points to avoid circular points if two boundaries more than one circle
            startToEnd.remove(startPoint, endPoint);
            startPoint = endPoint;
        }
//        System.out.println("Circle end!");
        borders.add(new Border<T>(borderItems));
        return borders;

    }

    private static void addBorderPartToList(Pos lastPos, List<Border.BorderItem> abstrBoundaries, Tuple2<Pos, Dir> abstrBorderItem) {
        if(abstrBorderItem.first.equals(lastPos)){
            //append to previous list
            Tuple2<Pos, List<Dir>> borderItem = abstrBoundaries.get(abstrBoundaries.size() - 1).borderItem;
            borderItem.second.add(abstrBorderItem.second);
        }else{
            List<Dir> dirList = new ArrayList<>();
            dirList.add(abstrBorderItem.second);
            abstrBoundaries.add(new Border.BorderItem(new Tuple2<>(abstrBorderItem.first, dirList)));
        }
    }
}
