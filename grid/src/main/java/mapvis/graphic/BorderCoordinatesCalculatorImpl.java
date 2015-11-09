package mapvis.graphic;

import javafx.geometry.Point2D;
import mapvis.common.datatype.INode;
import mapvis.models.Border;
import mapvis.models.Dir;
import mapvis.models.LeafRegion;
import mapvis.models.Region;

import java.util.*;

public class BorderCoordinatesCalculatorImpl<T> implements IBorderCoordinatesCalculator<T> {
    private Region<T> region;
    private List<Point2D> debugPoints;
    public BorderCoordinatesCalculatorImpl() {
        debugPoints = new ArrayList<>();
    }

    public List<Point2D> getDebugPoints() {
        return debugPoints;
    }

    public Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> getRegionToBoundaries() {
        return regionToBoundaries;
    }

    Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> regionToBoundaries = new HashMap<>();
    @Override
    public void setRegion(Region<T> region) {
        this.region = region;
    }

    public Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> computeCoordinates(Region<T> region, int levelToShow) {
        System.out.println("Region: " +((INode) region.getNodeItem()).getLabel());
        Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> result = new HashMap<>();
        if(!region.isLeaf()){
            if(region.getLevel() == levelToShow){
                List<LeafRegion.BoundaryShape> boundaryShapes = collectBoundariesForRegion(region, levelToShow);
                List<List<LeafRegion.BoundaryShape>> orderedShapes = orderBoundaryShapesAsPolygon(boundaryShapes);
//                List<List<LeafRegion.BoundaryShape>> orderedShapes = new ArrayList<>();
//                orderedShapes.add(boundaryShapes);
//                System.out.println("X");
//                for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//                    for (Double xValue : boundaryShape.xValues) {
//                        System.out.println(xValue);
//                    }
//                }
//                System.out.println("Y");
//                for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//                    for (Double yValue : boundaryShape.yValues) {
//                        System.out.println(yValue);
//                    }
//                }

                regionToBoundaries.put(region, orderedShapes);
                return result;
            }else  if(region.getLevel() < levelToShow){
                region.getChildRegions().forEach(tRegion -> {
                    Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> regionListMap = computeCoordinates(tRegion, levelToShow);
                    result.putAll(regionListMap);
//                    for (Map.Entry<Region<T>, List<List<LeafRegion.BoundaryShape>>> regionListEntry : regionListMap.entrySet()) {
//                        if(result.containsKey(regionListEntry.getKey())){
//                            result.get(regionListEntry.getKey()).addAll(regionListEntry.getValue());
//                        }else{
//                            result.put(regionListEntry.getKey(), regionListEntry.getValue());
//                        }
//
//                    }

                });
//                regionToBoundaries.put(region, regionListMap);
                return result;
            }
            return result;
        }
        List<LeafRegion.BoundaryShape> boundaryShapes = calcBoundaryShapeForLeafRegion((LeafRegion<T>) region, levelToShow);
        List<List<LeafRegion.BoundaryShape>> boundaryShapesList = new ArrayList<>();
        result.put(region, boundaryShapesList);
        return result;
    }

    private List<List<LeafRegion.BoundaryShape>> orderBoundaryShapesAsPolygon(List<LeafRegion.BoundaryShape> boundaryShapes) {
        List<List<LeafRegion.BoundaryShape>> resultingBoundaryShape = new ArrayList<>();
        Map<Point2D, LeafRegion.BoundaryShape> startPointToBoundaryShape = new HashMap<>();

        for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
            if(boundaryShape.xValues.length == 0)
                continue;
            Point2D startPoint = LeafRegion.roundToCoordinatesTo4Digits(boundaryShape.getStartPoint());
            if(startPointToBoundaryShape.containsKey(startPoint) && !startPointToBoundaryShape.get(startPoint).equals(boundaryShape)){
                debugPoints.add(startPoint);
                System.out.println("already there");
            }else{
                startPointToBoundaryShape.put(startPoint, boundaryShape);
            }

        }

        List<LeafRegion.BoundaryShape> boundaryShape = new ArrayList<>();
        LeafRegion.BoundaryShape startBoundaryShape = boundaryShapes.get(0);
        Point2D initialStartPoint = LeafRegion.roundToCoordinatesTo4Digits(startBoundaryShape.getStartPoint());
        boundaryShape.add(startBoundaryShape);

        for (int i = 0; i < boundaryShapes.size(); i++) {
            startBoundaryShape = startPointToBoundaryShape.get(LeafRegion.roundToCoordinatesTo4Digits(startBoundaryShape.getEndPoint()));
            if(startBoundaryShape == null){
                resultingBoundaryShape.add(boundaryShape);
                boundaryShape = new ArrayList<>();
//                System.out.println("circle found: " + i + "/" + (boundaryShapes.size() - 1));
                if(startPointToBoundaryShape.values().iterator().hasNext()){
                    startBoundaryShape = startPointToBoundaryShape.values().iterator().next();
                }else{
                    break;
                }

//                System.out.println("circle found: " + i + "/" + (boundaryShapes.size() - 1));
            }
//            if(i != 0 && initialStartPoint.equals(LeafRegion.roundToCoordinatesTo4Digits(startBoundaryShape.getEndPoint()))){
//                System.out.println("circle found: " + i + "/" + (boundaryShapes.size() - 1));
//                startBoundaryShape = startPointToBoundaryShape.values().iterator().next();
//
//            }/*else{
//                System.out.printf("no circle found");
//            }
            startPointToBoundaryShape.remove(LeafRegion.roundToCoordinatesTo4Digits(startBoundaryShape.getStartPoint()), startBoundaryShape);
            boundaryShape.add(startBoundaryShape);
        }
        ///maybe list of boundary shapes must be extended to cope with more than one boundary, again

//        boolean printReq = false;
//        for (int i = 0; i < resultingBoundaryShape.size(); i++) {
//            if(resultingBoundaryShape.get(i).getEndPoint().equals(resultingBoundaryShape.get( (i+1) % resultingBoundaryShape.size()).getStartPoint())){
//
//            }else{
//                System.out.printf("error");
//                printReq = true;
//
//            }
//        }
//        if(printReq){
//            System.out.println("X");
//            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//                for (int i = 0; i < boundaryShape.xValues.length; i++) {
//                    System.out.println(boundaryShape.xValues[i]);
//                }
//            }
//            System.out.println("Y");
//            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//                for (int i = 0; i < boundaryShape.xValues.length; i++) {
//                    System.out.println(boundaryShape.yValues[i]);
//                }
//            }
//        }
//        System.out.println("END");
        return resultingBoundaryShape;
    }

    private List<LeafRegion.BoundaryShape> collectBoundariesForRegion(Region<T> region, int levelToShow){
        if(!region.isLeaf()){
            List<LeafRegion.BoundaryShape> resultingCollection = new ArrayList<>();
            region.getChildRegions().forEach(tRegion ->  resultingCollection.addAll(collectBoundariesForRegion(tRegion, levelToShow)));
            return resultingCollection;
        }
        List<LeafRegion.BoundaryShape> boundaryShapes = calcBoundaryShapeForLeafRegion((LeafRegion<T>) region, levelToShow);

        return boundaryShapes;
    }

    private List<LeafRegion.BoundaryShape> calcBoundaryShapeForLeafRegion(LeafRegion<T> leafRegion, int levelToShow) {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        List<LeafRegion.BoundaryShape> result = new ArrayList<>();

        List<String> descriptionTexts = new ArrayList<>();

//        System.out.println("Start Leaf Region");
        for (Border<T> border : leafRegion.getBorders()) {
            if (border.getBorderItems().size() == 0) {
                continue;
            }

            if (border.getLevel() > levelToShow) {
                continue;
            }

            for (Border.BorderItem borderItem : border.getBorderItems()) {

                int x = borderItem.borderItem.first.getX();
                int y = borderItem.borderItem.first.getY();

                Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);

                for (Dir direction : borderItem.borderItem.second) {

                    int[] pointIndices = LeafRegion.DIR_TO_POINTS[direction.ordinal()];
                    double xStart = LeafRegion.POINTS[pointIndices[0]] + point2D.getX();
                    double yStart = LeafRegion.POINTS[pointIndices[1]] + point2D.getY();

                    Point2D startPoint = LeafRegion.roundToCoordinatesTo4Digits(new Point2D(xStart, yStart));

                    xValues.add(startPoint.getX());
                    yValues.add(startPoint.getY());
                }
            }
            LeafRegion.BoundaryShape boundaryShape = new LeafRegion.BoundaryShape(
                    xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                    yValues.stream().mapToDouble(Double::doubleValue).toArray(),
                    border);
            boundaryShape.text = descriptionTexts;

            boundaryShape.level = border.getLevel();
            result.add(boundaryShape);
//            computedBoundaryShapes.add(boundaryShape);

//            System.out.println("X");
//            for (Double xValue : xValues) {
//                System.out.println(xValue);
//            }
//            System.out.println("Y");
//            for (Double yValue : yValues) {
//                System.out.println(yValue);
//            }
            xValues.clear();
            yValues.clear();
        }


//        System.out.println("End Leaf Region");
        return result;
    }

    @Override
    public Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> computeCoordinates(int maxLevelToDraw) {
        if(region == null)
            return new HashMap<>();
        debugPoints.clear();
        regionToBoundaries.clear();
        return computeCoordinates(region, maxLevelToDraw);
    }
}