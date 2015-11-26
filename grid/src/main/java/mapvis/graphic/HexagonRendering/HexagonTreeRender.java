package mapvis.graphic.HexagonRendering;

import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.HexagonRendering.HexagonRender;
import mapvis.graphic.HexagonRendering.TileStyler;
import mapvis.graphic.HexagonalTilingView;
import mapvis.graphic.RegionRendering.ITreeVisualizationRenderer;
import mapvis.models.Dir;
import mapvis.models.Grid;
import mapvis.models.Pos;
import mapvis.models.Tile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HexagonTreeRender implements ITreeVisualizationRenderer {
    private final HexagonalTilingView hexagonalTilingView;
    private ObjectProperty<TileStyler<INode>> styler;
    private ObjectProperty<Grid<INode>> grid;
    private ObjectProperty<Tree2<INode>> tree;
    HexagonRender hexagonRender;

    public HexagonTreeRender(HexagonalTilingView hexagonalTilingView,
                             ObjectProperty<TileStyler<INode>> styler,
                             ObjectProperty<Grid<INode>> grid,
                             ObjectProperty<Tree2<INode>> tree) {
        this.hexagonalTilingView = hexagonalTilingView;
        this.grid = grid;
        this.tree = tree;
        this.styler = styler;
        this.hexagonRender = new HexagonRender(hexagonalTilingView, styler);
    }

    @Override
    public void renderScene(Point2D topleftBorder, Point2D bottomRightBorder) {
        if (hexagonalTilingView.getGrid() == null || styler.get() == null)
            return;

        GraphicsContext g = hexagonalTilingView.getCanvas().getGraphicsContext2D();

        List<Tile<INode>> tiles = new ArrayList<Tile<INode>>();

        grid.get().foreach(t -> {
            if (isTileVisibleOnScreen(t, topleftBorder, bottomRightBorder)) {
                updateHexagon(t.getX(), t.getY(), g);
            }
            if (t.getItem() != null && t.getTag() == Tile.LAND)
                tiles.add(t);
        });
        if (hexagonalTilingView.areLabelsShownProperty().get()) {
            Map<INode, Pos> posmap = mapLabelPos(tiles);
            drawLabels(posmap, g);
        }

        g.restore();
    }

    @Override
    public void configure(Object input) {

    }

    private boolean isTileVisibleOnScreen(Tile<INode> tile, Point2D topleftBorder, Point2D bottomRightBorder) {
        return tile.getX() > topleftBorder.getX()
                && tile.getX() < bottomRightBorder.getX()
                && tile.getY() > topleftBorder.getY()
                && tile.getY() < bottomRightBorder.getY();
    }

    private void drawLabels(Map<INode, Pos> posmap, GraphicsContext g) {
        for (Map.Entry<INode, Pos> entry : posmap.entrySet()) {
            INode node = entry.getKey();
            Pos pos = entry.getValue();
            Point2D point2D = HexagonalTilingView.hexagonalToPlain(pos.getX(), pos.getY());
            //System.out.printf("%s\n", node.name);
            int level = tree.get().getDepth(node);

            if (level == 0 || level > hexagonalTilingView.getMaxLevelOfLabelsToShow())
                continue;
            int fontSize = (int) (80 / Math.log(level + 1));
            g.setFont(new Font(fontSize));
//            if (level == 1)
//                g.setFont(new Font(80));
//            else if (level == 2)
//                g.setFont(new Font(42));
//            else if (level == 3)
//                g.setFont(new Font(28));
//            else
//                continue;

            g.setFill(Color.BLACK);
            g.fillText(node.getLabel(), point2D.getX(), point2D.getY());
        }
    }

    private Map<INode, Pos> mapLabelPos(Collection<Tile<INode>> tiles) {
        Map<INode, List<Pos>> map = new HashMap<INode, List<Pos>>();

        for (Tile<INode> tile : tiles) {
            INode item = tile.getItem();
            if (item == null || tile.getTag() != Tile.LAND)
                continue;

            List<INode> pathToNode = tree.get().getPathToNode(item);

            for (INode node : pathToNode) {
                List<Pos> poslist = map.get(node);
                if (poslist == null)
                    map.put(node, poslist = new ArrayList<Pos>());

                poslist.add(tile.getPos());
            }

        }

        Map<INode, Pos> posmap = new HashMap<INode, Pos>();

        for (Map.Entry<INode, List<Pos>> entry : map.entrySet()) {
            int x = 0;
            int y = 0;
            int n = 0;

            for (Pos pos : entry.getValue()) {
                x += pos.getX();
                y += pos.getY();
                n++;
            }

            Pos pos = new Pos(x / n, y / n);
            posmap.put(entry.getKey(), pos);
        }

        return posmap;
    }

    public void save(String filename) throws IOException {
        if (hexagonalTilingView.getGrid() == null)
            return;

        int margin = 2;

        int minx = grid.get().getMinX() - margin;
        int miny = grid.get().getMinY() - margin;
        int maxx = grid.get().getMaxX() + margin;
        int maxy = grid.get().getMaxY() + margin;

        System.out.printf("h x[%d:%d] y:[%d:%d]\n",
                minx, maxx, miny, maxy);

        Point2D topleft = HexagonalTilingView.hexagonalToPlain(minx, miny);
        Point2D botright = HexagonalTilingView.hexagonalToPlain(maxx, maxy);

        System.out.printf("p x[%d:%d] y:[%d:%d]\n",
                (int) topleft.getX(), (int) botright.getX(),
                (int) topleft.getY(), (int) botright.getY());

        double scale = 1.0;
        double w = (botright.getX() - topleft.getX()) * scale;
        double h = (botright.getY() - topleft.getY()) * scale;

        Canvas c1 = new Canvas(w, h);
        GraphicsContext g = c1.getGraphicsContext2D();
        g.setFill(styler.get().getBackground());
        g.fillRect(0, 0, w, h);

        System.out.printf("w:%d, h:%d, xy[%d:%d]\n", (int) w, (int) h,
                (int) (topleft.getX()),
                (int) (topleft.getY())

        );
        g.save();

        g.scale(scale, scale);
        g.translate(-topleft.getX(), -topleft.getY());

        List<Tile<INode>> tiles = new ArrayList<Tile<INode>>();

        grid.get().foreach(t -> {
            if (t.getX() >= minx
                    && t.getX() <= maxx
                    && t.getY() >= miny
                    && t.getY() <= maxy)

                updateHexagon(t.getX(), t.getY(), g);

            if (t.getItem() != null && t.getTag() == Tile.LAND)
                tiles.add(t);
        });

        Map<INode, Pos> posmap = mapLabelPos(tiles);
        //drawLabels(posmap, g);


        WritableImage wim = new WritableImage((int) w, (int) h);
        c1.snapshot(null, wim);
        File file = new File(filename);

        ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);


        g.restore();
    }

    private void drawHexagonBorders(int x, int y, List<Dir> directions, GraphicsContext g) {
        g.save();
        Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);
        g.translate(point2D.getX(), point2D.getY());

        hexagonRender.drawPointsOfHexagon(g, x, y, directions);

        g.restore();
    }

    private void updateHexagon(int x, int y, GraphicsContext g) {

        g.save();
        Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);
        g.translate(point2D.getX(), point2D.getY());

        hexagonRender.drawHexagon(g, x, y);

        g.restore();
    }
}