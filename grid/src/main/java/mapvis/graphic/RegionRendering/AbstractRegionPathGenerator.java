package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.IBoundaryShape;
import mapvis.models.LeafRegion;

import java.util.*;

/**
 * Created by dacc on 11/24/2015.
 */
public abstract class AbstractRegionPathGenerator<T> {

    Map<BorderIdentifier, IBoundaryShape<T>> simplifiedBorders = new HashMap<>();

    protected final GraphicsContext graphicsContext;

    public AbstractRegionPathGenerator(GraphicsContext g) {
        this.graphicsContext = g;
    }

    public static <T> List<IBoundaryShape<T>> summarizeBoundaryShapesWithSameNeighbours(List<IBoundaryShape<T>> regionIBoundaryShape, int maxShownRegionLevel, Tree2<T> tree) {
        Tuple2<T, T> prevSeparatedRegions = null;
        Tuple2<T, T> firstSeparatedRegions = null;
        boolean firstIteration = true;

        List<IBoundaryShape<T>> resultingBoundaryShape = new ArrayList<>();
        IBoundaryShape<T> currBoundaryShape = null;

        for (IBoundaryShape<T> tBoundaryShape : regionIBoundaryShape) {
            Tuple2<T, T> separatedRegions = tBoundaryShape.getSeperatedRegionsID(maxShownRegionLevel, tree);

            if ( (prevSeparatedRegions != null) && (!prevSeparatedRegions.hasSameTupleElements(separatedRegions) ) ){
                resultingBoundaryShape.add(currBoundaryShape);
                currBoundaryShape = tBoundaryShape;
            }else{
                //continue
                if(currBoundaryShape == null){
                    //init new
                    currBoundaryShape = tBoundaryShape;
                }else{
                    //append at existing but leave out the first one as it has the same coordinate like the end point of currBoundaryShape
                    Iterator<Point2D> coorIterator = tBoundaryShape.iterator();
                    coorIterator.next();

                    while (coorIterator.hasNext()){
                        currBoundaryShape.getCoordinates().add(coorIterator.next());
                    }
                }
            }

            prevSeparatedRegions = separatedRegions;
            if(firstIteration){
                firstIteration = false;
                firstSeparatedRegions = separatedRegions;
            }

        }

        if(currBoundaryShape != null){
            if(resultingBoundaryShape.size() > 0 && firstSeparatedRegions.hasSameTupleElements(prevSeparatedRegions)){
                //append at existing but leave out the first one as it has the same coordinate like the end point of currBoundaryShape
                Iterator<Point2D> coorIterator = resultingBoundaryShape.get(0).iterator();
                coorIterator.next();

                while (coorIterator.hasNext()){
                    currBoundaryShape.getCoordinates().add(coorIterator.next());
                }
                resultingBoundaryShape.set(0, currBoundaryShape);
            }else{
                resultingBoundaryShape.add(currBoundaryShape);
            }
        }

        return resultingBoundaryShape;
    }

    public static class BoundaryShapesWithReverseInformation<T> extends ArrayList<Tuple2<IBoundaryShape<T>, Boolean>>{
        public void addBoundaryShapeWithOrdering(IBoundaryShape<T> boundaryShape, Boolean ordering){
            this.add(new Tuple2<IBoundaryShape<T>, Boolean>(boundaryShape, ordering));
        }
    }

    public BoundaryShapesWithReverseInformation<T> generatePathForBoundaryShape(List<IBoundaryShape<T>> singleBoundaryShape, int maxToCollect, Tree2<T> tree) {
        if(singleBoundaryShape.size() == 0)
            return new BoundaryShapesWithReverseInformation();

        List<IBoundaryShape<T>> summarizedBoundaryShape = summarizeBoundaryShapesWithSameNeighbours(singleBoundaryShape, maxToCollect, tree);

        BoundaryShapesWithReverseInformation<T> result = new BoundaryShapesWithReverseInformation<>();

        for (IBoundaryShape<T> summarizedBoundaryStep : summarizedBoundaryShape) {
            BorderIdentifier borderID = getBorderIdentifier(summarizedBoundaryStep);

            if(simplifiedBorders.containsKey(borderID)) {
                IBoundaryShape<T> iBoundaryShapes = simplifiedBorders.get(borderID);

                Point2D startPoint = getRoundedStartPointOfBoundaryShape(summarizedBoundaryStep);
                Point2D endPoint = getRoundedEndPointOfBoundaryShape(summarizedBoundaryStep);
                Boolean reverseRequired = iBoundaryShapes.isCoordinatesNeedToBeReversed();
                if ( (endPoint.equals(getRoundedStartPointOfBoundaryShape(iBoundaryShapes))
                        && startPoint.equals(getRoundedEndPointOfBoundaryShape(iBoundaryShapes)))) {
                    reverseRequired = !reverseRequired;
                }
//                System.out.println("Reusing Border");

                result.addBoundaryShapeWithOrdering(iBoundaryShapes, reverseRequired);
            }else{
                createPathForBoundaryShape(summarizedBoundaryStep);
                simplifiedBorders.put(borderID, summarizedBoundaryStep);
                result.addBoundaryShapeWithOrdering(summarizedBoundaryStep, summarizedBoundaryStep.isCoordinatesNeedToBeReversed());
            }
        }


        return result;
    }

    abstract void createPathForBoundaryShape(IBoundaryShape<T> summarizedBoundaryStep);

    private Point2D getRoundedStartPointOfBoundaryShape(IBoundaryShape<T> summarizedBoundaryStep) {
        return LeafRegion.roundToCoordinatesTo4Digits(summarizedBoundaryStep.getStartPoint());
    }

    private BorderIdentifier getBorderIdentifier(IBoundaryShape<T> boundaryShapeStep) {

        Point2D startPoint = getRoundedStartPointOfBoundaryShape(boundaryShapeStep);
        Point2D endPoint = getRoundedEndPointOfBoundaryShape(boundaryShapeStep);

        BorderIdentifier identifier = new BorderIdentifier();
        if(startPoint.hashCode() > endPoint.hashCode()){
            identifier.startPoint = startPoint;
            identifier.endPoint = endPoint;
        }else{
            identifier.startPoint = endPoint;
            identifier.endPoint = startPoint;
        }
        identifier.borderLegth = boundaryShapeStep.getShapeLength();
        return identifier;
    }

    private Point2D getRoundedEndPointOfBoundaryShape(IBoundaryShape<T> boundaryShapeStep) {
        return LeafRegion.roundToCoordinatesTo4Digits(boundaryShapeStep.getEndPoint());
    }

    protected void clearChangedPaths(){
        simplifiedBorders.clear();
    }

    protected class BorderIdentifier{
        Point2D startPoint;
        Point2D endPoint;
        int borderLegth;

        @Override
        public int hashCode() {
            return startPoint.hashCode() + endPoint.hashCode() * endPoint.hashCode() +  borderLegth;
//            int result = startPoint != null ? startPoint.hashCode() : 0;
//            result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
//            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !this.getClass().equals(obj.getClass()))
                return false;
            MovingAverageRegionPathGenerator.BorderIdentifier otherID = (MovingAverageRegionPathGenerator.BorderIdentifier) obj;
            if(!this.startPoint.equals(otherID.startPoint))
                return false;
            if(!this.endPoint.equals(otherID.endPoint))
                return false;
//            if(!this.midPoint.equals(otherID.midPoint))
//                return false;
            if(borderLegth != ((MovingAverageRegionPathGenerator.BorderIdentifier) obj).borderLegth)
                return false;

            return true;
        }
    }
}
