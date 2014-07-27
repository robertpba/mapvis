package mapvis.graphic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import mapvis.Impl.RandomColorStyler;
import mapvis.grid.Dir;

class HexagonRender {
    HexagonalTilingView view;

    final double sideLength;

    final double COS30 = Math.cos(Math.toRadians(30));
    final double[] points;
    final double[] x;
    final double[] y;

    public HexagonRender(HexagonalTilingView view) {
        super();
        this.sideLength = view.SideLength;
        this.view = view;

        points = new double[]{
                -sideLength/2, -sideLength*COS30, //  0 - 1
                sideLength/2, -sideLength*COS30,  // 5  c  2
                sideLength, 0.0,                  //  4 - 3
                sideLength/2, sideLength*COS30,
                -sideLength/2, sideLength*COS30,
                -sideLength, 0.0
        };
        x = new double[]{
                -sideLength/2,
                sideLength/2,
                sideLength,
                sideLength/2,
                -sideLength/2,
                -sideLength
        };
        y = new double[]{
                -sideLength*COS30,
                -sideLength*COS30,
                0.0,
                sideLength*COS30,
                sideLength*COS30,
                0.0
        };
    }

    public void drawHexagon(GraphicsContext g,int x, int y) {
        TileStyler<Integer> styler = view.getStyler();

        if (!styler.isVisible(x,y))
            return;

        g.setFill(styler.getColor(x,y));
        g.fillPolygon(this.x,this.y,this.x.length);

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
}
