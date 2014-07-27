package mapvis.grid.jfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import mapvis.grid.Dir;
import mapvis.grid.Tile;

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

    public void drawHexagon(GraphicsContext g, int x, int y){
        Integer o = view.grid.get(x, y);
        g.setFill(view.colorMap.apply(o));
        fill(g);
    }

    public void fill(GraphicsContext g){
        g.fillPolygon(x,y,x.length);
    }

    public void borderN(GraphicsContext g){
        g.strokeLine(points[0], points[1], points[2], points[3]);
    }
    public void borderNE(GraphicsContext g){
        g.strokeLine(points[2], points[3], points[4], points[5]);
    }
    public void borderSE(GraphicsContext g){
        g.strokeLine(points[4], points[5], points[6], points[7]);
    }
    public void borderS(GraphicsContext g){
        g.strokeLine(points[6], points[7], points[8], points[9]);
    }
    public void borderSW(GraphicsContext g){
        g.strokeLine(points[8], points[9], points[10], points[11]);
    }
    public void borderNW(GraphicsContext g){
        g.strokeLine(points[10], points[11], points[0], points[1]);
    }

    public void drawBorders(GraphicsContext g,int x, int y) {
        Integer o = view.grid.get(x, y);

        g.setLineCap(StrokeLineCap.ROUND);
        applyBorderStyle(g, o, x, y, Dir.N);
        applyBorderStyle(g, o, x, y, Dir.NE);
        applyBorderStyle(g, o, x, y, Dir.NW);
        applyBorderStyle(g, o, x, y, Dir.S);
        applyBorderStyle(g, o, x, y, Dir.SE);
        applyBorderStyle(g, o, x, y, Dir.SW);
    }

    private void applyBorderStyle(GraphicsContext g,Integer o, int x, int y, Dir dir) {
        Tile<Integer> tn = view.grid.getNeighbour(x, y, dir);
        if (tn == null || tn.getObj() == null || o.equals(tn.getObj())) return;
        Integer lca = view.tree.getLCA(o, tn.getObj());
        if (lca == null) return;

        int d = view.tree.getDepth(lca);
        g.setStroke(Color.BLACK);
        g.setLineWidth((4.0 - d)*(4.0 - d)/2);

        switch (dir){
            case N: borderN(g); return;
            case NE: borderNE(g); return;
            case NW: borderNW(g); return;
            case S: borderS(g); return;
            case SW: borderSW(g); return;
            case SE: borderSE(g); return;
        }

    }



}
