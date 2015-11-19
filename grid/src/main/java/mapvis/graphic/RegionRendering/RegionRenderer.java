package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.*;

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
public class RegionRenderer implements ITreeVisualizationRenderer {

    private final Canvas canvas;
    private final RegionLabelRenderer regionLabelRenderer;
    private final HexagonalTilingView view;
    private final RegionAreaRenderer regionAreaRenderer;
    private final RegionBorderRenderer regionBorderRenderer;
    private Region regionToDraw;

    private final IBorderCoordinatesCalculator<INode> borderCoordinatesCalculator;

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        System.out.println("Creating: " + this.getClass().getName());

        this.view = view;
        this.canvas = canvas;
        this.regionAreaRenderer = new RegionAreaRenderer(canvas.getGraphicsContext2D(), view);
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


    @Override
    public void renderScene(final Point2D topleftBorder, final Point2D bottomRightBorder) {
        regionAreaRenderer.initForNextRenderingPhase();
        regionBorderRenderer.initForNextRenderingPhase();
        drawRegion(topleftBorder, bottomRightBorder);
        regionAreaRenderer.finishRenderingPhase();
        regionBorderRenderer.finishRenderingPhase();
    }

    @Override
    public void configure(Object input) {
        if(input == null)
            return;

        if(input instanceof Region){
            this.setRootRegion((Region<INode>) input);
        }
    }

    public void drawRegion(final Point2D topleftBorder, final Point2D bottomRightBorder) {

        GraphicsContext g = canvas.getGraphicsContext2D();
//        Path path = new Path();
//
//        MoveTo moveTo = new MoveTo();
//        moveTo.setX(100.0f);
//        moveTo.setY(100.0f);
//
//        HLineTo hLineTo = new HLineTo();
//        hLineTo.setX(300.0f);
//
//        VLineTo vLineTo = new VLineTo();
//        vLineTo.setY(300.0f);
//
//
//        path.getElements().addAll(moveTo, hLineTo, vLineTo);
//        path.setStroke(Color.RED);
//
//
//
//        Path path2 = new Path();
//
//        MoveTo moveTo2 = new MoveTo();
//        moveTo2.setX(300.0f);
//        moveTo2.setY(300.0f);
//
//        HLineTo hLineTo3 = new HLineTo();
//        hLineTo3.setX(100.0f);
//
//        VLineTo hLineTo2 = new VLineTo();
//        hLineTo2.setY(100.0f);
//
//        path2.getElements().addAll(moveTo2, hLineTo3, hLineTo2);
//        path2.setStroke(Color.YELLOW);
//
//        Path path3 = new Path();
//        path3.getElements().addAll(path.getElements());
//        path3.getElements().addAll(path2.getElements());
//        path3.setStroke(Color.TRANSPARENT);
//        path3.setFill(Color.GREEN);
//
//        this.view.getChildren().addAll(/*path, path2, */path3);
//        if(true)
//            return;

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

    public void setRootRegion(Region<INode> rootRegion) {
        this.regionToDraw = rootRegion;
    }
}
