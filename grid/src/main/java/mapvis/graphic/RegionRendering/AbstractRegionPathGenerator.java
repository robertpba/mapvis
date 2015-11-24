package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.models.IBoundaryShape;
import mapvis.models.LeafRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 11/24/2015.
 */
public abstract class AbstractRegionPathGenerator<T> implements IRegionPathGenerator<T> {

    Map<BorderIdentifier, IBoundaryShape<T>> simplifiedBorders = new HashMap<>();

    protected final GraphicsContext graphicsContext;

    public AbstractRegionPathGenerator(GraphicsContext g) {
        this.graphicsContext = g;
    }

    public List<IBoundaryShape<T>> generatePathForBoundaryShape(List<IBoundaryShape<T>> singleBoundaryShape, int maxToCollect, Tree2<T> tree) {
        if(singleBoundaryShape.size() == 0)
            return singleBoundaryShape;

        List<IBoundaryShape<T>> summarizedBoundaryShape = MovingAverageRegionPathGenerator.summarizeBoundaryShape(singleBoundaryShape, maxToCollect, tree);

//        Tuple2<Point2D, Point2D> borderID = getBorderIdentifier(singleBoundaryShape);
//        if(simplifiedBorders.containsKey(borderID)){
//            List<IBoundaryShape<T>> iBoundaryShapes = simplifiedBorders.get(borderID);
//            Point2D startPoint = calcStartPointOfBoundaryShapeList(iBoundaryShapes);
//            Point2D endPoint = calcEndPointOfBoundaryShapeList(iBoundaryShapes);
//            if(startPoint.equals(calcStartPointOfBoundaryShapeList(singleBoundaryShape))
//                    && endPoint.equals(calcEndPointOfBoundaryShapeList(singleBoundaryShape))) {
//                System.out.println("reuse possible");
//                return iBoundaryShapes;
//            }else{
//                System.out.println("reverse reuse possible");
//            }
//
//        }

        List<IBoundaryShape<T>> simplifiedPath = new ArrayList<>();

        for (IBoundaryShape<T> summarizedBoundaryStep : summarizedBoundaryShape) {
            BorderIdentifier borderID = getBorderIdentifier(summarizedBoundaryStep);

            if(simplifiedBorders.containsKey(borderID)) {
                IBoundaryShape<T> iBoundaryShapes = simplifiedBorders.get(borderID);

                Point2D startPoint = getRoundedStartPointOfBoundaryShape(summarizedBoundaryStep);
                Point2D endPoint = getRoundedEndPointOfBoundaryShape(summarizedBoundaryStep);

                if ( (endPoint.equals(getRoundedStartPointOfBoundaryShape(iBoundaryShapes))
                        && startPoint.equals(getRoundedEndPointOfBoundaryShape(iBoundaryShapes)))) {
                    iBoundaryShapes.setCoordinatesNeedToBeReversed(!iBoundaryShapes.isCoordinatesNeedToBeReversed());
                }
//                System.out.println("reverse reuse possible");
//                graphicsContext.setStroke(Color.GREEN);
//                graphicsContext.setLineWidth(5);
//                graphicsContext.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
//                graphicsContext.setLineWidth(2);
                simplifiedPath.add(iBoundaryShapes);
            }else{
                createPathForBoundaryShape(summarizedBoundaryStep);
                simplifiedBorders.put(borderID, summarizedBoundaryStep);
                simplifiedPath.add(summarizedBoundaryStep);
            }
        }


        return simplifiedPath;
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
