package mapvis.graphic.RegionRendering;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import mapvis.common.datatype.INode;

import mapvis.models.Region;

import java.util.List;

/**
 * This class is used to render the area of Regions according to the
 * IRegionStyler. Rendering is performed using the @BoundaryShapeRenderer
 */
public class RegionAreaRenderer {

    private final GraphicsContext graphicsContext;
    private RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer;


    public void setShapeRenderer(RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public RegionAreaRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.shapeRenderer = null;
    }

    public void drawArea(final IRegionStyler<INode> regionStyler, final Region<INode> regionToDraw,
                         final List<AbstractBoundaryShapeSmoother.BoundaryShapesWithReverseInformation<INode>> innerAndOuterBoundaryShapes) {
        if(shapeRenderer == null)
            return;

        Color regionFillColor = regionStyler.getColor(regionToDraw);
        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (innerAndOuterBoundaryShapes.size() == 0)
            return;

//        innerAndOuterBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());

        graphicsContext.beginPath();

        for (AbstractBoundaryShapeSmoother.BoundaryShapesWithReverseInformation<INode> singleBoundaryShape : innerAndOuterBoundaryShapes) {
            shapeRenderer.renderClosedBoundaryShapeArea(singleBoundaryShape);
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