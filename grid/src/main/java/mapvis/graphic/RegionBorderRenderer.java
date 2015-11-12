package mapvis.graphic;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;

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


    public void drawBorder(IRegionStyler<INode> styler, List<List<LeafRegion.BoundaryShape>> regionBorders, HexagonalTilingView view) {
//        graphicsContext.save();
//        ObservableList<Node> children = view.getChildren();

        for (List<LeafRegion.BoundaryShape> regionParts : regionBorders) {
            for (LeafRegion.BoundaryShape regionPart : regionParts) {
                if ( !isSingleSideBorderRenderingEnabled  || regionPart.border.getRenderID() != renderID) {
                    if(styler.isBorderVisible(regionPart.border)){
                        graphicsContext.setLineWidth(styler.getBorderWidth(regionPart.border));
                        graphicsContext.strokePolyline(regionPart.xValues, regionPart.yValues, regionPart.xValues.length);
                        drawIndex++;
                    }
                }

                regionPart.border.setRenderID(renderID);
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