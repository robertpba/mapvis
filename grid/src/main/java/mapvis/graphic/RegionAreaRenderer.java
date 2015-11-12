package mapvis.graphic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;
import mapvis.models.Region;

import java.util.List;

public class RegionAreaRenderer {
    private final GraphicsContext graphicsContext;

    public RegionAreaRenderer(GraphicsContext regionRenderer) {
        this.graphicsContext = regionRenderer;
    }


    public void drawArea(IRegionStyler<INode> regionStyler, Region<INode> regionToDraw, List<List<LeafRegion.BoundaryShape>> regionBoundaryShapes) {
//        graphicsContext.save();
        Color regionFillColor = regionStyler.getColor(regionToDraw);
        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (regionBoundaryShapes.size() == 0)
            return;

        graphicsContext.beginPath();
        regionBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());
        graphicsContext.moveTo(0, 0);

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
                        double xValue = partialRegionBoundary.xValues[i];
                        double yValue = partialRegionBoundary.yValues[i];

                        if (firstDraw) {
                            graphicsContext.moveTo(xValue, yValue);
                            firstDraw = false;
                        } else {
                            graphicsContext.lineTo(xValue, yValue);
                        }
                    }
                } else {
                    for (int i = 0; i < partialRegionBoundary.xValues.length; i++) {
                        double xValue = partialRegionBoundary.xValues[i];
                        double yValue = partialRegionBoundary.yValues[i];

                        if (firstDraw) {
                            graphicsContext.moveTo(xValue, yValue);
                            firstDraw = false;
                        } else {
                            graphicsContext.lineTo(xValue, yValue);
                        }
                    }
                }

            }
        }
        graphicsContext.closePath();
//        g.stroke();
        graphicsContext.fill();
//        graphicsContext.restore();
    }

    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }

}