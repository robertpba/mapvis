package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.Border;
import mapvis.models.IBoundaryShape;
import mapvis.models.LeafRegion;

import java.util.*;

/**
 * Created by dacc on 11/24/2015.
 * This class serves as basis for Algorithms used to smooth the borders of Regions.
 * Using the @AbstractBoundaryShapeSmoother#summarizeAndSmoothBoundaryShape method, given
 * boundaryShapes are first combined to longer BoundaryShapes. This is possible as long
 * as the VISUALIZED neighboring regions separated by the BoundaryShapes do not change.
 * Afterwards, the combined BoundaryShapes are smoothed using the @AbstractBoundaryShapeSmoother#smoothBoundaryShapes method.
 * The smoothed BoundaryShapes are then cached as undirected edge, so it can be reused
 * by the other neighboring region and in next rendering circles.
 */
public abstract class AbstractBoundaryShapeSmoother<T> {

    public static class BoundaryShapesWithReverseInformation<T> extends ArrayList<Tuple2<IBoundaryShape<T>, Boolean>>{
        public void addBoundaryShapeWithOrdering(IBoundaryShape<T> boundaryShape, Boolean ordering){
            this.add(new Tuple2<IBoundaryShape<T>, Boolean>(boundaryShape, ordering));
        }
    }

    private Map<BorderIdentifier, IBoundaryShape<T>> smoothBoundaryShapes = new HashMap<>();
    protected final GraphicsContext graphicsContext;

    public AbstractBoundaryShapeSmoother(GraphicsContext g) {
        this.graphicsContext = g;
    }

    /**
     * This methods smooths the coordinates of the provided
     * summarizedBoundaryShape
     * @param summarizedBoundaryStep the IBoundaryShape to smooth
     */
    abstract void smoothBoundaryShape(IBoundaryShape<T> summarizedBoundaryStep);

    public BoundaryShapesWithReverseInformation<T> summarizeAndSmoothBoundaryShape(List<IBoundaryShape<T>> singleBoundaryShape, int maxToCollect, Tree2<T> tree) {
        if(singleBoundaryShape.size() == 0)
            return new BoundaryShapesWithReverseInformation();

        //combine IBoundaryShapes if possible
        List<IBoundaryShape<T>> summarizedBoundaryShape = summarizeBoundaryShapesWithSameNeighbours(singleBoundaryShape, maxToCollect, tree);

        BoundaryShapesWithReverseInformation<T> smoothedBoudaryShapesWithReverseInformation = new BoundaryShapesWithReverseInformation<>();

        //smooth BoundaryShape or use cached one
        for (IBoundaryShape<T> summarizedBoundaryStep : summarizedBoundaryShape) {
            //ID used to identify BoundaryShapes which is the same for both separated regions (undirected edge between
            //startpoint and endpoint)
            BorderIdentifier borderID = getBorderIdentifier(summarizedBoundaryStep);

            if(smoothBoundaryShapes.containsKey(borderID)) {
                IBoundaryShape<T> iBoundaryShapes = smoothBoundaryShapes.get(borderID);

                //recover the orientation, so the cached smoothed Border has same start/end point as the unsmoothed
                Point2D startPoint = getRoundedStartPointOfBoundaryShape(summarizedBoundaryStep);
                Point2D endPoint = getRoundedEndPointOfBoundaryShape(summarizedBoundaryStep);
                Boolean reverseRequired = iBoundaryShapes.isCoordinatesNeedToBeReversed();

                if ( (endPoint.equals(getRoundedStartPointOfBoundaryShape(iBoundaryShapes))
                        && startPoint.equals(getRoundedEndPointOfBoundaryShape(iBoundaryShapes)))) {
                    reverseRequired = !reverseRequired;
                }
//                System.out.println("Reusing Border");
                //store reverse information for rendering
                smoothedBoudaryShapesWithReverseInformation.addBoundaryShapeWithOrdering(iBoundaryShapes, reverseRequired);
            }else{
                smoothBoundaryShape(summarizedBoundaryStep);
                smoothBoundaryShapes.put(borderID, summarizedBoundaryStep);
                smoothedBoudaryShapesWithReverseInformation.addBoundaryShapeWithOrdering(summarizedBoundaryStep, summarizedBoundaryStep.isCoordinatesNeedToBeReversed());
            }
        }


        return smoothedBoudaryShapesWithReverseInformation;
    }

    private Point2D getRoundedStartPointOfBoundaryShape(IBoundaryShape<T> summarizedBoundaryStep) {
        return LeafRegion.roundToCoordinatesTo4Digits(summarizedBoundaryStep.getStartPoint());
    }

    /**
     * creates an Identifier. The Identifier should be unique but the same for the two regions
     * separated by one boundaryShape.
     * @param boundaryShapeStep The BoundaryShape the BorderIdentifier should be created for
     * @return the created BorderIdentifier
     */
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
        identifier.borderLength = boundaryShapeStep.getShapeLength();
        return identifier;
    }

    private Point2D getRoundedEndPointOfBoundaryShape(IBoundaryShape<T> boundaryShapeStep) {
        return LeafRegion.roundToCoordinatesTo4Digits(boundaryShapeStep.getEndPoint());
    }

    protected void clearChangedPaths(){
        smoothBoundaryShapes.clear();
    }


    public static<T> Tuple2<T, T> getSeparatedRegionsAtBorder(Border<T> border, int maxLevel, Tree2<T> tree){
        T nodeA = border.getNodeA();
        T nodeB = border.getNodeB();
        T regionNodeA = tree.getParentAtLevel(nodeA, maxLevel);
        T regionNodeB = tree.getParentAtLevel(nodeB, maxLevel);
        return new Tuple2<>(regionNodeA, regionNodeB);
    }

    /**
     * This method summarizes subsequent BoundaryShapes that have the same neighbouring regions
     * to one combined longer BoundaryShape. The smaller the maximum region/border level shown
     * in the visualization, the shorter are the combined BoundaryShapes. In contrast, if only
     * level 0 is shown, the List of BoundaryShapes defining the root Region as combination of
     * all level 0 borders which stored in the LeafRegions, will be combined to one circular
     * BoundaryShape.
     * @param regionIBoundaryShape list of boundaryShape to be combined
     * @param maxShownRegionLevel the maximum border/region level used for summarizing
     * @param tree the current tree
     * @param <T> the tree's nodeType
     * @return summarized version of boundaryShapes
     */
    public static <T> List<IBoundaryShape<T>> summarizeBoundaryShapesWithSameNeighbours(List<IBoundaryShape<T>> regionIBoundaryShape, int maxShownRegionLevel, Tree2<T> tree) {
        Tuple2<T, T> prevSeparatedRegions = null;
        Tuple2<T, T> firstSeparatedRegions = null;
        boolean firstIteration = true;

        List<IBoundaryShape<T>> summarizedBoundaryShape = new ArrayList<>();
        IBoundaryShape<T> currBoundaryShape = null;

        for (IBoundaryShape<T> tBoundaryShape : regionIBoundaryShape) {
            //request the neighbors separated by the regions in the current visualization
            Tuple2<T, T> separatedRegions = getSeparatedRegionsAtBorder(tBoundaryShape.getFirstBorder(), maxShownRegionLevel, tree);

            if ( (prevSeparatedRegions != null) && (!prevSeparatedRegions.hasSameTupleElements(separatedRegions) ) ){
                //neighboring regions changed
                summarizedBoundaryShape.add(currBoundaryShape);
                currBoundaryShape = tBoundaryShape;
            }else{
                //continue last BoundaryShape
                if(currBoundaryShape == null){
                    //init required (first iteration)
                    currBoundaryShape = tBoundaryShape;
                }else{
                    //append at existing but leave out the first coordinate as it has the
                    //same coordinate like the end point of currBoundaryShape
                    Iterator<Point2D> coorIterator = tBoundaryShape.iterator();
                    coorIterator.next();

                    while (coorIterator.hasNext()){
                        currBoundaryShape.getCoordinates().add(coorIterator.next());
                    }
                }
            }

            prevSeparatedRegions = separatedRegions;
            if(firstIteration){
                //store the first seperated regions
                firstIteration = false;
                firstSeparatedRegions = separatedRegions;
            }
        }

        //last BoundaryShape can often be appended to the first one
        if(currBoundaryShape != null){
            if(summarizedBoundaryShape.size() > 0 && firstSeparatedRegions.hasSameTupleElements(prevSeparatedRegions)){
                //append at existing but leave out the first one as it has the same coordinate like the end point of currBoundaryShape
                Iterator<Point2D> coorIterator = summarizedBoundaryShape.get(0).iterator();
                coorIterator.next();

                while (coorIterator.hasNext()){
                    currBoundaryShape.getCoordinates().add(coorIterator.next());
                }
                summarizedBoundaryShape.set(0, currBoundaryShape);
            }else{
                summarizedBoundaryShape.add(currBoundaryShape);
            }
        }

        return summarizedBoundaryShape;
    }


    protected class BorderIdentifier{
        Point2D startPoint;
        Point2D endPoint;
        int borderLength;

        @Override
        public int hashCode() {
            return startPoint.hashCode() + endPoint.hashCode() * endPoint.hashCode() + borderLength;
//            int result = startPoint != null ? startPoint.hashCode() : 0;
//            result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
//            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !this.getClass().equals(obj.getClass()))
                return false;
            MovingAverageBoundaryShapeSmoother.BorderIdentifier otherID = (MovingAverageBoundaryShapeSmoother.BorderIdentifier) obj;
            if(!this.startPoint.equals(otherID.startPoint))
                return false;
            if(!this.endPoint.equals(otherID.endPoint))
                return false;
//            if(!this.midPoint.equals(otherID.midPoint))
//                return false;
            if(borderLength != ((MovingAverageBoundaryShapeSmoother.BorderIdentifier) obj).borderLength)
                return false;

            return true;
        }
    }
}
