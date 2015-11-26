package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
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


    public RegionBorderRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.renderIDRandomGen = new Random(0);
        this.shapeRenderer = null;
        this.isSingleSideBorderRenderingEnabled = true;
    }

    private int getNextRenderID(){
        return renderIDRandomGen.nextInt();
    }


    public void drawBorder(IRegionStyler<INode> styler,
                           List<AbstractRegionPathGenerator.SortedBounaryShapes<INode>> regionBorders) {
        if(shapeRenderer == null)
            return;

//        graphicsContext.save();
//        ObservableList<Node> children = view.getChildren();
//        graphicsContext.setLineJoin(StrokeLineJoin.MITER);
        for (AbstractRegionPathGenerator.SortedBounaryShapes<INode> regionParts : regionBorders) {
             for (Tuple2<IBoundaryShape<INode>, Boolean> regionPartTuple : regionParts.boundaryShapeAndOrdering) {
                 IBoundaryShape<INode> regionPart = regionPartTuple.first;
                 if ( !isSingleSideBorderRenderingEnabled  || regionPart.getFirstBorder().getRenderID() != renderID) {
                    if(styler.isBorderVisible(regionPart.getFirstBorder())){
                        graphicsContext.beginPath();
                        shapeRenderer.renderBoundaryShape(regionPart);
                        graphicsContext.setLineWidth(styler.getBorderWidth(regionPart.getFirstBorder()));
//                        graphicsContext.setLineWidth(2);
                        graphicsContext.stroke();
                        drawIndex++;
                    }
                }
//                else{
//                    graphicsContext.save();
//                    graphicsContext.setStroke(Color.RED);
//                    int midIndex = regionPart.getShapeLength()/2;
//                    Point2D midPoint = new Point2D(regionPart.getXCoordinateAtIndex(midIndex), regionPart.getYCoordinateAtIndex(midIndex));
//                    graphicsContext.strokeLine(regionPart.getXCoordinateStartpoint(), regionPart.getYCoordinateStartpoint(),
//                            midPoint.getX(), midPoint.getY());
//                    graphicsContext.strokeLine(midPoint.getX(), midPoint.getY(),
//                            regionPart.getXCoordinateEndpoint(), regionPart.getYCoordinateEndpoint());
//                    graphicsContext.restore();
//                }

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

    public void setShapeRenderer(RegionRenderer.BoundaryShapeRenderer<INode> shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }
}