package mapvis.graphic.RegionRendering;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import mapvis.common.datatype.INode;
import mapvis.graphic.HexagonalTilingView;

import mapvis.models.Region;

import java.util.List;

public class RegionAreaRenderer {

    private final GraphicsContext graphicsContext;
    private final HexagonalTilingView view;
    private RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer;


    public void setShapeRenderer(RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public RegionAreaRenderer(GraphicsContext graphicsContext, HexagonalTilingView view) {
        this.graphicsContext = graphicsContext;
        this.shapeRenderer = null;
        this.view = view;
    }

    public void drawArea(final IRegionStyler<INode> regionStyler, final Region<INode> regionToDraw,
                         final List<AbstractRegionPathGenerator.BoundaryShapesWithReverseInformation<INode>> innerAndOuterBoundaryShapes) {
        if(shapeRenderer == null)
            return;

        Color regionFillColor = regionStyler.getColor(regionToDraw);
        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (innerAndOuterBoundaryShapes.size() == 0)
            return;

//        innerAndOuterBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());

        graphicsContext.beginPath();

        for (AbstractRegionPathGenerator.BoundaryShapesWithReverseInformation<INode> singleBoundaryShape : innerAndOuterBoundaryShapes) {
            shapeRenderer.renderBoundaryShape(singleBoundaryShape);
        }

        graphicsContext.setLineCap(StrokeLineCap.ROUND);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);

        graphicsContext.fill();
    }

    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }

}