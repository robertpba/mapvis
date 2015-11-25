package mapvis.graphic.RegionRendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mapvis.common.datatype.INode;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.IBoundaryShape;

import java.util.List;
import java.util.Random;

public class RegionBorderRenderer {

    private RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer;
    private int totalDrawnBorder;
    private int drawIndex;
    private final GraphicsContext graphicsContext;
    private int renderID;
    private Random renderIDRandomGen;
    private boolean isSingleSideBorderRenderingEnabled;


    public RegionBorderRenderer(GraphicsContext graphicsContext, RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer) {
        this.graphicsContext = graphicsContext;
        this.renderIDRandomGen = new Random(0);
        this.shapeRenderer = shapeRenderer;
        this.isSingleSideBorderRenderingEnabled = true;
    }

    private int getNextRenderID(){
        return renderIDRandomGen.nextInt();
    }


    public void drawBorder(IRegionStyler<INode> styler,
                           List<List<IBoundaryShape<INode>>> regionBorders,
                           HexagonalTilingView view,
                           AbstractRegionPathGenerator<INode> simplificationAlgorithm) {
//        graphicsContext.save();
//        ObservableList<Node> children = view.getChildren();
//        graphicsContext.setLineJoin(StrokeLineJoin.MITER);
        for (List<IBoundaryShape<INode>> regionParts : regionBorders) {
            int maxToCollect = Math.max(view.getMaxLevelOfBordersToShow(), view.getMaxLevelOfRegionsToShow());
            regionParts = simplificationAlgorithm.generatePathForBoundaryShape(regionParts, maxToCollect, view.getTree());
//            if (!isSingleSideBorderRenderingEnabled || regionPart.getFirstBorder().getRenderID() != renderID) {
//                if (styler.isBorderVisible(regionPart.getFirstBorder())) {
//                    graphicsContext.beginPath();
//
////                        graphicsContext.strokePolyline(regionPart.getXCoordsArray(), regionPart.getYCoordsArray(), regionPart.getShapeLength());
//                    shapeRenderer.renderBoundaryShape(regionParts);
////                        graphicsContext.setStroke(Color.BLACK);
////                        graphicsContext.setLineWidth(styler.getBorderWidth(regionPart.getFirstBorder()));
//                    graphicsContext.setLineWidth(2);
//                    graphicsContext.stroke();
//                    drawIndex++;
//                }
//            }

             for (IBoundaryShape<INode> regionPart : regionParts) {
                if ( !isSingleSideBorderRenderingEnabled  || regionPart.getFirstBorder().getRenderID() != renderID) {
                    if(styler.isBorderVisible(regionPart.getFirstBorder())){
                        graphicsContext.beginPath();

//                        graphicsContext.strokePolyline(regionPart.getXCoordsArray(), regionPart.getYCoordsArray(), regionPart.getShapeLength());
                        shapeRenderer.renderBoundaryShape(regionPart);
//                        graphicsContext.setStroke(Color.BLACK);
                        graphicsContext.setLineWidth(styler.getBorderWidth(regionPart.getFirstBorder()));
//                        graphicsContext.setLineWidth(2);
                        graphicsContext.stroke();
                        drawIndex++;
                    }
                }

                regionPart.getFirstBorder().setRenderID(renderID);
                totalDrawnBorder++;
            }
        }
//        graphicsContext.restore();
    }

    public void initForNextRenderingPhase() {
        this.renderID = getNextRenderID();
        this.drawIndex = 0;
        this.totalDrawnBorder = 0;
    }

    public void finishRenderingPhase() {
        System.out.println("Drawn Borders " + drawIndex + " of " + totalDrawnBorder);
    }

    public void setIsSingleSideBorderRenderingEnabled(boolean isSingleSideBorderRenderingEnabled) {
        this.isSingleSideBorderRenderingEnabled = isSingleSideBorderRenderingEnabled;
    }

    public boolean isSingleSideBorderRenderingEnabled() {
        return isSingleSideBorderRenderingEnabled;
    }
}