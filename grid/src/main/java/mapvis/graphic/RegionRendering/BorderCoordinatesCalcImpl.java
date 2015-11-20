package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.*;

import java.util.*;

/**
 * Created by dacc on 11/6/2015.
 */
public class BorderCoordinatesCalcImpl<T> implements IBorderCoordinatesCalculator {

    private final HexagonalTilingView view;
    private Region<T> region;
    private List<Point2D> debugPoints;
    private Map<Region<T>, List<List<BoundaryShape<T>>>> regionToBoundaries = new HashMap<>();
    private IRegionStyler regionStyler;
    private int maxLevelToCollect;

    public BorderCoordinatesCalcImpl(HexagonalTilingView view) {
        debugPoints = new ArrayList<>();
        this.view = view;
    }

    @Override
    public Map<Region<T>, List<List<BoundaryShape<T>>>> computeCoordinates(boolean doOrdering, int levelToCollect) {
        if(region == null)
            return new HashMap<>();

        regionStyler = view.getRegionStyler();
        debugPoints.clear();
        regionToBoundaries.clear();
        maxLevelToCollect = levelToCollect;
        computeCoordinates(region, doOrdering);
        return regionToBoundaries;
    }


    public boolean hasRegionChildrenWithAreasOrBordersToShow(Region<T> region) {
        return region.getLevel() < maxLevelToCollect;
    }

    public boolean hasRegionAreasOrBordersToShow(Region<T> region) {
        return (region.isLeaf() && region.getLevel() < maxLevelToCollect ) || (region.getLevel() == maxLevelToCollect);
    }

    private boolean hasBorderElementsToShow(Border<T> border) {
        return border.getLevel() <= maxLevelToCollect;
    }

    private void computeCoordinates(Region<T> region, boolean doOrdering) {

        if( hasRegionAreasOrBordersToShow(region) ){
            List<BoundaryShape<T>> boundaryShapes = collectBoundariesForRegion(region);

            if(doOrdering){
                List<List<BoundaryShape<T>>> lists = orderBoundaryShapes(boundaryShapes);
                regionToBoundaries.put(region, lists);
            }else{
                List<List<BoundaryShape<T>>> bShapeList = new ArrayList<>();
                bShapeList.add(boundaryShapes);
                regionToBoundaries.put(region, bShapeList);
            }
        }else if(hasRegionChildrenWithAreasOrBordersToShow(region)){
            region.getChildRegions().forEach(tRegion -> computeCoordinates(tRegion, doOrdering));
        }
    }

    public static <T> List<List<BoundaryShape<T>>> orderBoundaryShapes(List<BoundaryShape<T>> boundaryShapes) {
        if(boundaryShapes.isEmpty())
            return Collections.emptyList();

        UndirectedEdgeHashMap undirectedEdgeHashMap = new UndirectedEdgeHashMap();
        for (BoundaryShape boundaryShape : boundaryShapes) {
            undirectedEdgeHashMap.put(boundaryShape);
        }


        List<BoundaryShape<T>> boundaryShape = new ArrayList<>();

        List<List<BoundaryShape<T>>> resultingBoundaryShape = new ArrayList<>();

        Point2D currentPoint = null;
        BoundaryShape currentBoundaryShape = null;
        for (int i = 0; i < boundaryShapes.size(); i++) {
            if(i == 0){
                //get any start edge/boundary shape and initial the current point
                //point to step from one boundary shape end to the start of the next boundary shape
                currentBoundaryShape = undirectedEdgeHashMap.getNext();
                currentPoint = currentBoundaryShape.getStartPoint();
            }else{
                currentBoundaryShape = undirectedEdgeHashMap.getNextEdgeWithPivotPoint(currentPoint, currentBoundaryShape);
            }

            if(currentBoundaryShape == null  // we found a circle since no edge starts/ends with the current point
                    //the boundary shape itself is circual
                    || currentBoundaryShape.getStartPoint().equals(currentBoundaryShape.getEndPoint())
                    ){
                resultingBoundaryShape.add(boundaryShape);
                undirectedEdgeHashMap.remove(currentBoundaryShape);
                boundaryShape = new ArrayList<>();
                if(!undirectedEdgeHashMap.isEmpty()){
                    currentBoundaryShape = undirectedEdgeHashMap.getNext();
                }else{
                    break;
                }

            }

            //the boundary shapes are undirected => check if to continue with end or start point
            if(currentBoundaryShape.getStartPoint().equals(currentPoint)){
                currentPoint = currentBoundaryShape.getEndPoint();
                currentBoundaryShape.coordinatesNeedToBeReversed = false;
            }else{
                currentPoint = currentBoundaryShape.getStartPoint();
                currentBoundaryShape.coordinatesNeedToBeReversed = true;
            }

            undirectedEdgeHashMap.remove(currentBoundaryShape);
            boundaryShape.add(currentBoundaryShape);
        }
        if(!boundaryShape.isEmpty()){
            resultingBoundaryShape.add(boundaryShape);
        }
        return resultingBoundaryShape;
    }

    private List<BoundaryShape<T>> collectBoundariesForRegion(Region<T> region){
        if(!region.isLeaf()){
            List<BoundaryShape<T>> resultingCollection = new ArrayList<>();
            region.getChildRegions().forEach(tRegion ->  resultingCollection.addAll(collectBoundariesForRegion(tRegion)));
            return resultingCollection;
        }
        List<BoundaryShape<T>> boundaryShapes = calcBoundaryShapeForLeafRegion((LeafRegion<T>) region);

        return boundaryShapes;
    }

    private List<BoundaryShape<T>> calcBoundaryShapeForLeafRegion(LeafRegion<T> leafRegion) {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        List<BoundaryShape<T>> result = new ArrayList<>();

        for (Border<T> borderStep : leafRegion.getBorders()) {
            if (borderStep.getBorderCoordinates().size() == 0) {
                continue;
            }
            if(!hasBorderElementsToShow(borderStep)) {
                continue;
            }

            for (TileBorder tileBorder : borderStep.getBorderCoordinates()) {
                for (Dir direction : tileBorder.getDirections()) {

                    Point2D startPoint = LeafRegion.roundToCoordinatesTo4Digits(
                            TileBorder.calcStartPointForBorderEdge(tileBorder.getTilePos(),
                                    direction)
                    );

                    xValues.add(startPoint.getX());
                    yValues.add(startPoint.getY());
                }
            }
            BoundaryShape boundaryShape = new BoundaryShape(
                    xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                    yValues.stream().mapToDouble(Double::doubleValue).toArray(),
                    borderStep);

            boundaryShape.level = borderStep.getLevel();
            result.add(boundaryShape);

            xValues.clear();
            yValues.clear();
        }

        return result;
    }


    @Override
    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public void setRegionStyler(IRegionStyler regionStyler) {
        this.regionStyler = regionStyler;
    }

    @Override
    public List<Point2D> getDebugPoints() {
        return debugPoints;
    }

}
