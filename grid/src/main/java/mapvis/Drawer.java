package mapvis;

import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.models.Dir;
import mapvis.models.Grid;
import mapvis.models.Tile;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.*;

public class Drawer {
    private Grid<Node> grid;
    Tree2<Node> tree;

    public Drawer(Grid<Node> grid, Tree2<Node> tree) {
        this.grid = grid;
        this.tree = tree;
    }

    public void draw(Graphics2D g) {
        cache = null;

        int margin = 5;
        int minx = grid.getMinX() - margin;
        int miny = grid.getMinY() - margin;
        //int maxx = grid.getMaxX() + margin;
        //int maxy = grid.getMaxY() + margin;

        Point2D topleft = hexagonalToPlain(minx, miny);

        g.translate( -topleft.getX(), -topleft.getY());


        grid.foreach(t -> {
            drawTile(g, t);
        });
    }


    static double SideLength = 1;
    static double Cos30 = Math.cos(Math.toRadians(30));
    static Shape N;
    static Shape NE;
    static Shape SE;
    static Shape S;
    static Shape SW;
    static Shape NW;
    static Shape HXG;

    static  {
        final double l = SideLength * 1.1;

        N = new Line2D.Double(-l/2, -l* Cos30, l/2, -l* Cos30);
        NE = new Line2D.Double(l/2, -l* Cos30, l, 0.0);
        SE = new Line2D.Double(l, 0.0, l/2, l* Cos30);
        S = new Line2D.Double(l/2, l* Cos30, -l/2, l* Cos30);
        SW = new Line2D.Double(-l/2, l* Cos30, -l, 0.0);
        NW = new Line2D.Double(-l, 0.0, -l/2, -l* Cos30);

        final Path2D h = new Path2D.Double();
        h.moveTo(-l/2, -l * Cos30);
        h.lineTo(l/2,-l* Cos30);
        h.lineTo(l, 0.0);
        h.lineTo(l/2,l* Cos30);
        h.lineTo(-l/2,l* Cos30);
        h.lineTo(-l,0.0);
        h.closePath();
        HXG = h;
    }

    public Point2D hexagonalToPlain(int x, int y){
        double cx = x * 3 * SideLength / 2;
        double cy;
        cy = 2 * Cos30 * SideLength * y;

        if (x % 2 != 0) {
            cy += Cos30 * SideLength;
        }

        return new Point2D.Double(cx, cy);
    }

    void drawTile(Graphics2D g, Tile<Node> tile) {
        TileCache<Node> cache = getCache(tile.getX(), tile.getY());

        if (cache.t == Tile.SEA || cache.v == null)
            return;

        Point2D xy = hexagonalToPlain(tile.getX(), tile.getY());

        AffineTransform transform = g.getTransform();
        g.translate(xy.getX(), xy.getY());

        Color color = new Color((int) tile.getItem().getVal("color"));
        if (color == null) color = Color.gray;
        g.setColor(color);
        g.fill(HXG);

        drawBorder(g, cache.borderN, N);
        drawBorder(g, cache.borderNE, NE);
        drawBorder(g, cache.borderSE, SE);
        drawBorder(g, cache.borderS, S);
        drawBorder(g, cache.borderSW, SW);
        drawBorder(g, cache.borderNW, NW);
        g.setTransform(transform);


    }

    private void drawBorder(Graphics2D g, int level, Shape line) {
        if (level > 0){
            double alpha = 1.4;
            double beta = alpha*0.25;
            double width = (SideLength* alpha - level* beta)*(SideLength*alpha - level*beta)/2;;
            g.setColor(new Color(0xaa,0xaa,0xaa));
            g.setStroke(new BasicStroke((float)width,
                    BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.draw(line);
        }
    }


    TileCache<Node> getCache(int x, int y){
        if (cache == null) cache = new TileCache<>();
        Tile<Node> tile = grid.getTile(x, y);
        if (cache.x == x && cache.y == y && cache.t == tile.getTag())
            return cache;

        cache.x = x;
        cache.y = y;
        cache.t = tile.getTag();

        Node t = grid.getItem(x, y);

        if (t == null)
            cache.v = null;
        else
            cache.v = t;

        cache.borderN = calcLevel(x, y, Dir.N);
        cache.borderS = calcLevel(x, y, Dir.S);
        cache.borderNE = calcLevel(x, y, Dir.NE);
        cache.borderSE = calcLevel(x, y, Dir.SE);
        cache.borderNW = calcLevel(x, y, Dir.NW);
        cache.borderSW = calcLevel(x, y, Dir.SW);

        return cache;
    }

    int calcLevel(int x, int y, Dir dir){
        Tile<Node> t = grid.getTile(x, y);
        Tile<Node> tn = grid.getNeighbour(x, y, dir);
        if (t.getItem() == null || tn.getItem() == null || t.getItem() == tn.getItem())
            return 0;
        if (t.getTag() == Tile.SEA)
            return 0;
        if (tn.getTag() == Tile.SEA)
            return 0;


        Node lca = tree.getLCA(t.getItem(), tn.getItem());
        if (lca == null) return 0;

        return tree.getDepth(lca) + 1;
    }

    static class TileCache<T> {
        public T v;
        public int x;
        public int y;
        public int t;

        public int borderN;
        public int borderS;
        public int borderNE;
        public int borderSE;
        public int borderNW;
        public int borderSW;
    }
    TileCache<Node> cache;


    public void export() throws IOException {
        // Get a DOMImplementation
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        draw(svgGenerator);

        boolean useCSS = true;

        File file = File.createTempFile("vis", ".svg");
        FileOutputStream os = new FileOutputStream(file);
        Writer out = new OutputStreamWriter(os, "UTF-8");
        svgGenerator.stream(out, useCSS);

        System.out.println(file.toURI());
        //Desktop.getDesktop().browse(file.toURI());
    }


}
