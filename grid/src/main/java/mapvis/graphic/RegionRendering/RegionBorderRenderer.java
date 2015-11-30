package mapvis.graphic.RegionRendering;

import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.models.IBoundaryShape;

import java.util.List;
import java.util.Random;

/**
 * This class renders the borders/IBoundaryShapes of Regions. Thereby it is
 * ensured that each Border is rendered only once. Rendering of the borders is
 * performed using the provided BoundaryShapeRenderer and according to the
 * settings stored in the IRegionBorderStyler.
 */
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


    public void drawBorder(IRegionBorderStyler<INode> styler,
                           List<AbstractBoundaryShapeSmoother.BoundaryShapesWithReverseInformation<INode>> regionBorders) {
        if(shapeRenderer == null)
            return;

//        graphicsContext.save();
//        graphicsContext.setLineJoin(StrokeLineJoin.MITER);
        for (AbstractBoundaryShapeSmoother.BoundaryShapesWithReverseInformation<INode> regionParts : regionBorders) {
             for (Tuple2<IBoundaryShape<INode>, Boolean> regionPartTuple : regionParts) {
                 IBoundaryShape<INode> regionPart = regionPartTuple.first;
                 if ( !isSingleSideBorderRenderingEnabled  || regionPart.getFirstBorder().getRenderID() != renderID) {
                    if(styler.isBorderVisible(regionPart.getFirstBorder())){
                        graphicsContext.beginPath();
                        shapeRenderer.renderBoundaryShapeSegment(regionPart);
                        graphicsContext.setLineWidth(styler.getBorderWidth(regionPart.getFirstBorder()));
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

    /**
     * For each new rendering phase a random ID is generated.
     * This ID is then used to detect whether one Border was
     * already rendered or not.
     */
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