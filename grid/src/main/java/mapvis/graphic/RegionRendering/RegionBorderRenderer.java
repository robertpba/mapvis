package mapvis.graphic.RegionRendering;

import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.INode;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.IBoundaryShape;

import java.util.List;
import java.util.Random;

public class RegionBorderRenderer {

//    public static final RenderState INITIAL_BORDER_RENDERSTATE = RenderState.StateA;
    private int totalDrawnBorder;
    private int drawIndex;
    private final GraphicsContext graphicsContext;
    private int renderID;
    private Random renderIDRandomGen;
    private boolean isSingleSideBorderRenderingEnabled;


    public RegionBorderRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.renderIDRandomGen = new Random(0);
        this.isSingleSideBorderRenderingEnabled = true;
    }

    private int getNextRenderID(){
        return renderIDRandomGen.nextInt();
    }


    public void drawBorder(IRegionStyler<INode> styler, List<List<IBoundaryShape>> regionBorders, HexagonalTilingView view) {
//        graphicsContext.save();
//        ObservableList<Node> children = view.getChildren();
//        graphicsContext.setLineJoin(StrokeLineJoin.MITER);
        for (List<IBoundaryShape> regionParts : regionBorders) {
            for (IBoundaryShape regionPart : regionParts) {
                if ( !isSingleSideBorderRenderingEnabled  || regionPart.getFirstBorder().getRenderID() != renderID) {
                    if(styler.isBorderVisible(regionPart.getFirstBorder())){
                        graphicsContext.setLineWidth(styler.getBorderWidth(regionPart.getFirstBorder()));
                        graphicsContext.strokePolyline(regionPart.getXCoordsArray(), regionPart.getYCoordsArray(), regionPart.getShapeLength());
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