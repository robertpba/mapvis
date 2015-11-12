package mapvis.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;
import mapvis.models.Region;
import mapvis.models.Tile;

import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 10/26/2015.
 */
public class RegionRenderer {

    static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6, 7},
            new int[]{ 6,  7,  8,  9},
    };

    private final Canvas canvas;
    private final RegionLabelRenderer regionLabelRenderer;

    private HexagonalTilingView view;
    private final double sideLength;
    private final double COS30 = Math.cos(Math.toRadians(30));
    private final double[] points;
    private final double[] x;
    private final double[] y;
    public int maxBorderLevelToShow;
    private final RegionAreaRenderer regionAreaRenderer;
    private final RegionBorderRenderer regionBorderRenderer;

    private final IBorderCoordinatesCalculator<INode> borderCoordinatesCalculator;

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        super();
        maxBorderLevelToShow = 1;
        System.out.println("Creating: " + this.getClass().getName());
        this.sideLength = view.SideLength;
        this.view = view;
        this.canvas = canvas;
        regionAreaRenderer = new RegionAreaRenderer(canvas.getGraphicsContext2D());
        regionBorderRenderer = new RegionBorderRenderer(canvas.getGraphicsContext2D());
        regionLabelRenderer = new RegionLabelRenderer(canvas.getGraphicsContext2D());
        borderCoordinatesCalculator = new BorderCoordinatesCalcImpl<>(view);
        points = new double[]{
                - sideLength/2, - sideLength*COS30, //  0 - 1
                sideLength/2, - sideLength*COS30,  // 5  c  2
                sideLength,     0.0,               //  4 - 3
                sideLength/2,   sideLength*COS30,
                - sideLength/2,   sideLength*COS30,
                - sideLength,     0.0
        };
        x = new double[]{
                - sideLength/2,
                sideLength/2,
                sideLength,
                sideLength/2,
                - sideLength/2,
                - sideLength
        };
        y = new double[]{
                - sideLength*COS30,
                - sideLength*COS30,
                0.0,
                sideLength*COS30,
                sideLength*COS30,
                0.0
        };
    }


    private boolean isTileVisibleOnScreen(Tile<INode> tile, Point2D topleftBorder, Point2D bottomRightBorder)
    {
        return tile.getX() > topleftBorder.getX()
                && tile.getX() < bottomRightBorder.getX()
                && tile.getY() > topleftBorder.getY()
                && tile.getY() < bottomRightBorder.getY();
    }


    public void drawRegionHelper(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder)
    {
        regionAreaRenderer.initForNextRenderingPhase();
        regionBorderRenderer.initForNextRenderingPhase();
        drawRegion(regionToDraw, topleftBorder, bottomRightBorder);
        regionAreaRenderer.finishRenderingPhase();
        regionBorderRenderer.finishRenderingPhase();
    }

    public static double roundTo4Digits(double val){
        return Math.round(100.0 * val) / 100.0;
    }

    public static Point2D roundToCoordinatesTo4Digits(Point2D point2D){
        return new Point2D(roundTo4Digits(point2D.getX()), roundTo4Digits(point2D.getY()));
    }

    public void drawRegion(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder){
//        TileStyler<INode> styler = view.getStyler();
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.save();
        borderCoordinatesCalculator.setRegion(regionToDraw);

        boolean disableOrdering = false;
        regionBorderRenderer.setIsSingleSideBorderRenderingEnabled(true);

        Map<Region<INode>, List<List<LeafRegion.BoundaryShape>>> regionToBoundaryShapes = borderCoordinatesCalculator.
                computeCoordinates(!disableOrdering);

        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
            IRegionStyler<INode> regionStyler = view.getRegionStyler();
            if(disableOrdering){
                regionBorderRenderer.drawBorder(regionStyler, boundaryShapes, view);
            }else {
                regionAreaRenderer.drawArea(regionStyler, boundaryShapeTuple.getKey(), boundaryShapes);
            }
        }
        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
            Region<INode> region = boundaryShapeTuple.getKey();
            IRegionStyler<INode> regionStyler = view.getRegionStyler();
            regionBorderRenderer.drawBorder(regionStyler, boundaryShapes, view);
        }

        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
            Region<INode> region = boundaryShapeTuple.getKey();
            IRegionStyler<INode> regionStyler = view.getRegionStyler();
            regionLabelRenderer.drawLabels(regionStyler, region, boundaryShapes);
        }

        g.restore();
    }

    public HexagonalTilingView getView() {
        return view;
    }
}
