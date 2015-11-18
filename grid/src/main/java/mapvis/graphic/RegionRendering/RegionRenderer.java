package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.INode;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.LeafRegion;
import mapvis.models.Region;
import mapvis.models.Tile;

import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 10/26/2015.
 */
public class RegionRenderer {

    private final Canvas canvas;
    private final RegionLabelRenderer regionLabelRenderer;
    private final HexagonalTilingView view;
    private final RegionAreaRenderer regionAreaRenderer;
    private final RegionBorderRenderer regionBorderRenderer;

    private final IBorderCoordinatesCalculator<INode> borderCoordinatesCalculator;

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        System.out.println("Creating: " + this.getClass().getName());

        this.view = view;
        this.canvas = canvas;
        this.regionAreaRenderer = new RegionAreaRenderer(canvas.getGraphicsContext2D());
        this.regionBorderRenderer = new RegionBorderRenderer(canvas.getGraphicsContext2D());
        this.regionLabelRenderer = new RegionLabelRenderer(canvas.getGraphicsContext2D());
        this.borderCoordinatesCalculator = new BorderCoordinatesCalcImpl<>(view);
    }


    private boolean isTileVisibleOnScreen(Tile<INode> tile, Point2D topleftBorder, Point2D bottomRightBorder) {
        return tile.getX() > topleftBorder.getX()
                && tile.getX() < bottomRightBorder.getX()
                && tile.getY() > topleftBorder.getY()
                && tile.getY() < bottomRightBorder.getY();
    }


    public void drawRegionHelper(final Region regionToDraw, final Point2D topleftBorder, final Point2D bottomRightBorder) {
        regionAreaRenderer.initForNextRenderingPhase();
        regionBorderRenderer.initForNextRenderingPhase();
        drawRegion(regionToDraw, topleftBorder, bottomRightBorder);
        regionAreaRenderer.finishRenderingPhase();
        regionBorderRenderer.finishRenderingPhase();
    }

    public void drawRegion(final Region regionToDraw, final Point2D topleftBorder, final Point2D bottomRightBorder) {
//        TileStyler<INode> styler = view.getStyler();
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.save();
        borderCoordinatesCalculator.setRegion(regionToDraw);
        regionBorderRenderer.setIsSingleSideBorderRenderingEnabled(true);
//
        boolean disableOrdering = false;
        IRegionStyler<INode> regionStyler = view.getRegionStyler();
        int maxLevelToCollect = Math.max(regionStyler.getMaxBorderLevelToShow(), regionStyler.getMaxRegionLevelToShow());

        Map<Region<INode>, List<List<LeafRegion.BoundaryShape>>> regionToBoundaryShapes = borderCoordinatesCalculator.
                computeCoordinates(!disableOrdering, maxLevelToCollect);
//        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

//
        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
            regionAreaRenderer.drawArea(regionStyler, boundaryShapeTuple.getKey(), boundaryShapes);
        }
//
//        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
//            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
//            Region<INode> region = boundaryShapeTuple.getKey();
//            regionBorderRenderer.drawBorder(regionStyler, boundaryShapes, view);
//        }
//
//        if(regionStyler.getShowLabels()){
//            if(maxLevelToCollect != regionStyler.getMaxLabelLevelToShow()){
//                regionToBoundaryShapes = borderCoordinatesCalculator.computeCoordinates(false, regionStyler.getMaxLabelLevelToShow());
//            }
//            for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
//                List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
//                Region<INode> region = boundaryShapeTuple.getKey();
//                regionLabelRenderer.drawLabels(regionStyler, region, boundaryShapes);
//            }
//        }

        g.restore();
    }

}
