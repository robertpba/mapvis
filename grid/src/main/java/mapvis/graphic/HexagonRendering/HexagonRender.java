package mapvis.graphic.HexagonRendering;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import mapvis.common.datatype.INode;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.Dir;

import java.util.Collections;
import java.util.List;

/**
 * This class is resonsible to render a hexagon
 * at the provided position on the screen. Rendering
 * is performed according to the provided @TileStyler.
 */
public class HexagonRender {
    static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6, 7},
            new int[]{ 6,  7,  8,  9},
    };
    private final ObjectProperty<TileStyler<INode>> tileStyler;

    final double sideLength;

    final double COS30 = Math.cos(Math.toRadians(30));
    final double[] points;
    final double[] x;
    final double[] y;

    public HexagonRender(HexagonalTilingView view, ObjectProperty<TileStyler<INode>> tileStyler) {
        super();
        System.out.println("Creating: " + this.getClass().getName());
        this.sideLength = view.SideLength;
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
        this.tileStyler = tileStyler;
    }

    public void drawHexagon(GraphicsContext g, int x, int y) {
        TileStyler<INode> styler = tileStyler.get();

        if (!styler.isVisible(x,y))
            return;

        Color col = styler.getColor(x,y);
        g.setFill(col);

        g.fillPolygon(this.x, this.y, this.x.length);

        g.setLineCap(StrokeLineCap.ROUND);

        if (styler.isBorderVisible(x, y, Dir.N)) {
            g.setLineWidth(styler.getBorderWidth(x, y, Dir.N));
            g.setStroke(styler.getBorderColor(x, y, Dir.N));
            g.strokeLine(points[0], points[1], points[2], points[3]);
        }

        if (styler.isBorderVisible(x, y, Dir.NE)) {
            g.setLineWidth(styler.getBorderWidth(x, y, Dir.NE));
            g.setStroke(styler.getBorderColor(x, y, Dir.NE));
            g.strokeLine(points[2], points[3], points[4], points[5]);
        }

        if (styler.isBorderVisible(x, y, Dir.NW)) {
            g.setLineWidth(styler.getBorderWidth(x, y, Dir.NW));
            g.setStroke(styler.getBorderColor(x, y, Dir.NW));
            g.strokeLine(points[10], points[11], points[0], points[1]);
        }

        if (styler.isBorderVisible(x, y, Dir.S)) {
            g.setLineWidth(styler.getBorderWidth(x, y, Dir.S));
            g.setStroke(styler.getBorderColor(x, y, Dir.S));
            g.strokeLine(points[6], points[7], points[8], points[9]);
        }

        if (styler.isBorderVisible(x, y, Dir.SW)) {
            g.setLineWidth(styler.getBorderWidth(x, y, Dir.SW));
            g.setStroke(styler.getBorderColor(x, y, Dir.SW));
            g.strokeLine(points[8], points[9], points[10], points[11]);
        }

        if (styler.isBorderVisible(x, y, Dir.SE)) {
            g.setLineWidth(styler.getBorderWidth(x, y, Dir.SE));
            g.setStroke(styler.getBorderColor(x, y, Dir.SE));
            g.strokeLine(points[4], points[5], points[6], points[7]);
        }
    }

    private void drawHexagonBorders(int x, int y, List<Dir> directions, GraphicsContext g) {
        g.save();
        Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);
        g.translate(point2D.getX(), point2D.getY());

        drawPointsOfHexagon(g, x, y, directions);

        g.restore();
    }


    public void drawPointsOfHexagon(GraphicsContext g, int x, int y, List<Dir> directions) {
        TileStyler<INode> styler = tileStyler.get();

        if (!styler.isVisible(x,y))
            return;

//        Color col = styler.getColor(x,y);
//        g.setFill(col);
//
//        g.drawArea(this.x, this.y, this.x.length);

        g.setLineCap(StrokeLineCap.ROUND);
        Collections.reverse(directions);
        for (Dir direction : directions) {
            g.setLineWidth(styler.getBorderWidth(x, y, direction));
            g.setStroke(styler.getBorderColor(x, y, direction));

            int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
            g.strokeLine(points[pointIndices[0]], points[pointIndices[1]], points[pointIndices[2]], points[pointIndices[3]]);
        }
    }
}
