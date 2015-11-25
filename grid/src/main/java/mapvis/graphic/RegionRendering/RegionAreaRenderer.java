package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Pair;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.ConfigurationConstants;
import mapvis.models.IBoundaryShape;
import mapvis.models.Region;

import java.util.ArrayList;
import java.util.List;

public class RegionAreaRenderer {

    private final GraphicsContext graphicsContext;
    private final HexagonalTilingView view;
    private RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer;


    public void setShapeRenderer(RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public RegionAreaRenderer(GraphicsContext graphicsContext, HexagonalTilingView view, RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer) {
        this.graphicsContext = graphicsContext;
        this.shapeRenderer = shapeRenderer;
        this.view = view;
    }

    public void drawArea(final IRegionStyler<INode> regionStyler, final Region<INode> regionToDraw, final List<List<IBoundaryShape<INode>>> innerAndOuterBoundaryShapes,
                         AbstractRegionPathGenerator<INode> averageRegionPathGenerator) {
        Color regionFillColor = regionStyler.getColor(regionToDraw);
        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (innerAndOuterBoundaryShapes.size() == 0)
            return;

//        regionBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());

        graphicsContext.beginPath();
        for (List<IBoundaryShape<INode>> singleBoundaryShape : innerAndOuterBoundaryShapes) {
            int maxToCollect = Math.max(view.getMaxLevelOfBordersToShow(), view.getMaxLevelOfRegionsToShow());
            singleBoundaryShape = averageRegionPathGenerator.generatePathForBoundaryShape(singleBoundaryShape, maxToCollect, view.getTree());
            shapeRenderer.renderBoundaryShape(singleBoundaryShape);
        }

        graphicsContext.setLineCap(StrokeLineCap.ROUND);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        if(ConfigurationConstants.DRAW_ORIGINAL_SHAPE){
            this.graphicsContext.setStroke(Color.RED);
        }else{
            this.graphicsContext.setStroke(Color.BLACK);
        }


//        graphicsContext.stroke();
        if(ConfigurationConstants.FILL_SHAPE){
            graphicsContext.fill();
        }

        if(!ConfigurationConstants.DRAW_ORIGINAL_SHAPE)
            return;

//        graphicsContext.beginPath();
//        for (List<BoundaryShape<INode>> regionBoundaryShape : regionBoundaryShapes) {
//
//            if (regionBoundaryShape.size() == 0)
//                continue;
//
//            List<Point2D[]> originalShapePoints = originalBorderPointsGenerator.generatePathForBoundaryShape(regionBoundaryShape);
////            this.bezierCurveRenderer.accept(shapePoints);
//            new DirectPolylineBoundaryShapeRenderer<INode>().renderBoundaryShape(regionBoundaryShapes, regionFillColor);
//            this.graphicsContext.setStroke(Color.BLACK);
//        }
//
//        graphicsContext.setLineCap(StrokeLineCap.ROUND);
//        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
//        graphicsContext.stroke();
//        graphicsContext.fill();
    }

    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }

}