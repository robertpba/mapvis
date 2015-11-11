package mapvis.graphic;

import javafx.scene.canvas.GraphicsContext;
import mapvis.models.LeafRegion;

import java.util.List;

public class RegionBorderRenderer {

    public static final RenderState INITIAL_BORDER_RENDERSTATE = RenderState.StateA;
    private int totalDrawnBorder;
    private int drawIndex;
    private final GraphicsContext graphicsContext;
    private RenderState currentRegionRenderState;
    private boolean isSingleSideBorderRenderingEnabled;


    public enum RenderState{
        StateA,
        StateB
    }


    public RegionBorderRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.currentRegionRenderState = INITIAL_BORDER_RENDERSTATE;
        this.isSingleSideBorderRenderingEnabled = true;
    }

    private RenderState getNextRenderState(){
        switch (currentRegionRenderState) {
            case StateA:
                return RenderState.StateB;
            case StateB:
                return RenderState.StateA;
        }
        return null;
    }


    void drawBorder(TileStyler styler, List<List<LeafRegion.BoundaryShape>> regionBorders) {
        for (List<LeafRegion.BoundaryShape> regionParts : regionBorders) {
            for (LeafRegion.BoundaryShape regionPart : regionParts) {
                if ( !isSingleSideBorderRenderingEnabled  || regionPart.border.getRenderState() == currentRegionRenderState) {
                    int level = regionPart.border.getLevel();
                    graphicsContext.setLineWidth(styler.getBorderWidthByLevel(level));
                    graphicsContext.strokePolyline(regionPart.xValues, regionPart.yValues, regionPart.xValues.length);
                    drawIndex++;
                }
                regionPart.border.setRenderState(currentRegionRenderState);
                totalDrawnBorder++;
            }
        }
    }

    public void initForNextRenderingPhase() {
        this.currentRegionRenderState = getNextRenderState();
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