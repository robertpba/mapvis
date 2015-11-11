package mapvis.graphic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;

import java.util.List;

public class RegionAreaRenderer {
    private final GraphicsContext graphicsContext;

    public RegionAreaRenderer(GraphicsContext regionRenderer) {
        this.graphicsContext = regionRenderer;
    }

    void drawArea(TileStyler<INode> styler, List<List<LeafRegion.BoundaryShape>> regionBoundaryShapes,
                  Color regionFillColor) {

//        Region<INode> border = boundaryShapeTuple.getKey();
//        TileStyler<INode> styler = graphicsContext.getView().getStyler();

        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (regionBoundaryShapes.size() == 0)
            return;

        graphicsContext.beginPath();
        regionBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());

        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {
//            if(drawIndex != shapeIndexToDraw)
//                continue;
            if (regionBoundaryShape.size() == 0)
                continue;

//            drawIndex++;
            boolean firstDraw = true;
            for (LeafRegion.BoundaryShape partialRegionBoundary : regionBoundaryShape) {


                if (partialRegionBoundary.coordinateNeedToBeReversed) {
                    for (int i = partialRegionBoundary.xValues.length - 1; i >= 0; i--) {
                        if (firstDraw) {
                            graphicsContext.moveTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                            firstDraw = false;
                        } else {
                            graphicsContext.lineTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                        }
                    }
                } else {
                    for (int i = 0; i < partialRegionBoundary.xValues.length; i++) {
                        if (firstDraw) {
                            graphicsContext.moveTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                            firstDraw = false;
                        } else {
                            graphicsContext.lineTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                        }
                    }
                }

            }
        }

        graphicsContext.closePath();
//        g.stroke();
        graphicsContext.fill();
    }

    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }
}