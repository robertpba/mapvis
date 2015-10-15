package mapvis.graphic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.models.Grid;
import mapvis.models.Pos;
import mapvis.models.Tile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HexagonalTilingView extends Pane {

    public HexagonRender render;
    Canvas canvas;

    public HexagonalTilingView(){
        super();
        System.out.println("Creating: " + this.getClass().getName());

        initHexagonTilingView();

        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragEntered);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        originX.addListener(this::onOriginXChange);
        originY.addListener(this::onOriginYChange);
        zoom.addListener(this::onZoomChange);
        styler.addListener(this::onStylerChange);
    }

    private void initHexagonTilingView(){
        setPrefHeight(1000);
        setPrefWidth(1000);
        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        render = new HexagonRender(this);

        getChildren().addAll(canvas);

        updateHexagons();
    }

    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double SideLength = 10;


    public Point2D hexagonalToPlain(int x, int y){
        double cx = x * 3 * SideLength / 2;
        double cy;
        cy = 2 * COS30 * SideLength * y;

        if (x % 2 != 0) {
            cy += COS30 * SideLength;
        }

        return new Point2D(cx, cy);
    }
    public Point2D planeToHexagonal(double x, double y){
        double cx = x / 3 * 2 / SideLength;
        int nx = (int) Math.round(cx);
        int ny;

        if (nx%2 == 0) {
            ny = (int)Math.round(y / 2 / COS30 / SideLength);
        }else {
            ny = (int)Math.round((y - COS30 * SideLength) / 2 / COS30 / SideLength);
        }

        return new Point2D(nx, ny);
    }

    public Point2D localToPlane(double x, double y){
        double x1 = (x-originXProperty().get())/zoomProperty().get();
        double y1 = (y-originYProperty().get())/zoomProperty().get();
        return new Point2D(x1, y1);
    }

    public void updateHexagons(){
        System.out.println("updateHexagons");
        if (getGrid() == null)
            return;

        //canvas = new Canvas(getWidth(),getHeight());

        GraphicsContext g = canvas.getGraphicsContext2D();

        g.setFill(styler.get().getBackground());
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //Rectangle2D rect = viewport.get();

        Bounds rect = getLayoutBounds();
        double x0 = - originXProperty().get()/zoomProperty().get();
        double y0 = - originYProperty().get()/zoomProperty().get();
        double x1 = (getWidth() - originXProperty().get())/zoomProperty().get();
        double y1 = (getHeight()- originYProperty().get())/zoomProperty().get();

        Point2D tl = planeToHexagonal(x0, y0);
        Point2D br = planeToHexagonal(x1, y1);
        g.save();

        g.translate(originXProperty().get(), originYProperty().get());
        g.scale(zoomProperty().get(), zoomProperty().get());

        List<Tile<Node>> tiles = new ArrayList<>();

        grid.get().foreach(t -> {
            if (t.getX() > tl.getX()
                    && t.getX() < br.getX()
                    && t.getY() > tl.getY()
                    && t.getY() < br.getY())
                updateHexagon(t.getX(), t.getY(), g);
            if (t.getItem() != null && t.getTag() == Tile.LAND)
                tiles.add(t);
        });

        Map<Node, Pos> posmap = mapLabelPos(tiles);
        drawLabels(posmap, g);

        //getChildren().setAll(canvas);
        g.restore();
    }

    void drawLabels(Map<Node, Pos> posmap, GraphicsContext g){
        for (Map.Entry<Node, Pos> entry : posmap.entrySet()) {
            Node node = entry.getKey();
            Pos pos = entry.getValue();
            Point2D point2D = hexagonalToPlain(pos.getX(), pos.getY());
            //System.out.printf("%s\n", node.name);
            int level = tree.get().getDepth(node);
            if (level == 0)
                continue;

            if (level == 1)
                g.setFont(new Font(80));
            else if (level == 2)
                g.setFont(new Font(42));
            else if (level == 3)
                g.setFont(new Font(28));
            else
                continue;

            g.setFill(Color.BLACK);
            g.fillText(node.getLabel(), point2D.getX(), point2D.getY());
        }
    }


    Map<Node, Pos> mapLabelPos(Collection<Tile<Node>> tiles){
        Map<Node, List<Pos>> map = new HashMap<>();

        for (Tile<Node> tile : tiles) {
            Node item = tile.getItem();
            if (item == null || tile.getTag() != Tile.LAND)
                continue;
            List<Node> pathToNode = tree.get().getPathToNode(item);

            for (Node node : pathToNode) {
                List<Pos> poslist = map.get(node);
                if (poslist == null)
                    map.put(node, poslist= new ArrayList<>());

                poslist.add(tile.getPos());
            }

        }

        Map<Node, Pos> posmap= new HashMap<>();

        for (Map.Entry<Node, List<Pos>> entry : map.entrySet()) {
            int x=0; int y=0; int n=0;

            for (Pos pos : entry.getValue()) {
                x+=pos.getX();
                y+=pos.getY();
                n++;
            }

            Pos pos = new Pos(x/n,y/n);
            posmap.put(entry.getKey(), pos);
        }

        return  posmap;
    }




    public void save(String filename) throws IOException {
        if (getGrid() == null)
            return;

        int margin = 2;

        int minx = grid.get().getMinX() - margin;
        int miny = grid.get().getMinY() - margin;
        int maxx = grid.get().getMaxX() + margin;
        int maxy = grid.get().getMaxY() + margin;

        System.out.printf("h x[%d:%d] y:[%d:%d]\n",
                minx, maxx, miny, maxy);

        Point2D topleft = hexagonalToPlain(minx, miny);
        Point2D botright = hexagonalToPlain(maxx, maxy);

        System.out.printf("p x[%d:%d] y:[%d:%d]\n",
                (int)topleft.getX(), (int)botright.getX(),
                (int)topleft.getY(), (int)botright.getY());

        double scale = 1.0;
        double w = (botright.getX()-topleft.getX())*scale;
        double h = (botright.getY()-topleft.getY())*scale;

        Canvas c1 = new Canvas(w, h);
        GraphicsContext g = c1.getGraphicsContext2D();
        g.setFill(styler.get().getBackground());
        g.fillRect(0, 0, w, h);

        System.out.printf("w:%d, h:%d, xy[%d:%d]\n",(int)w, (int)h,
                (int)(topleft.getX()),
                (int)(topleft.getY())

        );
        g.save();

        g.scale(scale, scale);
        g.translate(-topleft.getX(), -topleft.getY());

        List<Tile<Node>> tiles = new ArrayList<>();

        grid.get().foreach(t -> {
            if (t.getX() >= minx
                    && t.getX() <= maxx
                    && t.getY() >= miny
                    && t.getY() <= maxy)

            updateHexagon(t.getX(), t.getY(), g);

            if (t.getItem() != null && t.getTag() == Tile.LAND)
                tiles.add(t);
        });

        Map<Node, Pos> posmap = mapLabelPos(tiles);
        //drawLabels(posmap, g);


        WritableImage wim = new WritableImage((int)w, (int)h);
        c1.snapshot(null, wim);
        File file = new File(filename);

        ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);


        g.restore();
    }



    private void updateHexagon(int x, int y, GraphicsContext g) {

        g.save();
        Point2D point2D = hexagonalToPlain(x, y);
        g.translate(point2D.getX(), point2D.getY());

        render.drawHexagon(g,x,y);

        g.restore();
    }

    private ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> gridProperty() { return this.grid; }
    public final Grid<Node> getGrid() { return this.gridProperty().get(); }
    public final void setGrid(Grid<Node> grid) { this.gridProperty().set(grid); }

    private ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Tree2<Node>> treeProperty() { return this.tree; }
    public final Tree2<Node> getTree() { return this.treeProperty().get(); }
    public final void setTree(Tree2<Node> tree) { this.treeProperty().set(tree); }

    private ObjectProperty<TileStyler<Node>> styler = new SimpleObjectProperty<>();
    public ObjectProperty<TileStyler<Node>> stylerProperty() { return this.styler; }
    public final TileStyler<Node> getStyler() { return this.stylerProperty().get(); }
    public final void setStyler(TileStyler<Node> styler) { this.stylerProperty().set(styler); }

    private DoubleProperty zoom = new SimpleDoubleProperty(1);
    public DoubleProperty zoomProperty() { return this.zoom; }
    public final double getZoom() { return this.zoomProperty().get(); }
    public final void setZoom(double zoom) { this.zoomProperty().set(zoom); }

    private DoubleProperty originX = new SimpleDoubleProperty(0);
    public DoubleProperty originXProperty() { return this.originX; }
    public final double getOriginX() { return this.originXProperty().get(); }
    public final void setOriginX(double x) { this.originXProperty().set(x); }

    private DoubleProperty originY = new SimpleDoubleProperty(0);
    public DoubleProperty originYProperty() { return this.originY; }
    public final double getOriginY() { return this.originYProperty().get(); }
    public final void setOriginY(double y) { this.originYProperty().set(y); }


    public void zoom(double scale){
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, scale);
    }
    public void zoomTo(double scale){
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, scale/this.zoom.get());
    }
    public void pan(double dx, double dy){
        Point2D o0 = new Point2D(originX.get(), originY.get());
        Point2D o1 = new Point2D(originX.get()+dx, originY.get()+dy);
        pan(o0, o0, o1);
    }
    public void scrollTo(double x, double y){
        Point2D o0 = new Point2D(originX.get(), originY.get());
        Point2D o1 = new Point2D(x+getWidth()/2, y+getHeight()/2);
        pan(o0, o0, o1);
    }

    public void zoom(Point2D pivot, double scale){
        double dx  = (pivot.getX() - originX.get()) * (scale - 1);
        double dy  = (pivot.getY() - originY.get()) * (scale - 1);
        double x1  = originX.get() - dx;
        double y1  = originY.get() - dy;

        originX.set(x1);
        originY.set(y1);

        double s0 = zoom.get();
        double s1 = s0 * scale;
        zoom.set(s1);
    }
    public void pan(Point2D orig, Point2D from, Point2D to){
        double x = orig.getX();
        double y = orig.getY();
        double x1 = x + to.getX() - from.getX();
        double y1 = y + to.getY() - from.getY();
        originX.set(x1);
        originY.set(y1);
    }

    Transform translateTransform = Affine.translate(0, 0);
    Transform scaleTransform = Affine.scale(1, 1);

    //////////////////////////
    void onZoomChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        scaleTransform = Affine.scale(newValue.doubleValue(), newValue.doubleValue());
        updateHexagons();

    }
    void onOriginXChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        translateTransform = Affine.translate(newValue.doubleValue(), originY.get());
        updateHexagons();
    }
    void onOriginYChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        translateTransform = Affine.translate(originX.get(), newValue.doubleValue());
        updateHexagons();
    }
    void onStylerChange(ObservableValue<? extends TileStyler<Node>> observable,
                        TileStyler<Node> oldValue, TileStyler<Node> newValue){
        updateHexagons();
    }

    //////////////////////////

    double startX = -1, startY = -1;
    double saveOriginX, saveOriginY;
    double saveOrigScale = 1;

    boolean inDrag;
    boolean panButton;

    private void mousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        startX = x;
        startY = y;

        saveOriginX = originX.get();
        saveOriginY = originY.get();
        saveOrigScale = zoom.get();

        panButton = e.isPrimaryButtonDown();

        inDrag = true;
    }
    private void mouseReleased(MouseEvent e) {
        inDrag = false;
    }
    private void dragEntered(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        pan(new Point2D(saveOriginX, saveOriginY),
                new Point2D(startX, startY),
                new Point2D(x, y));

    }
    private void onScroll(ScrollEvent event) {
        double scale =  1 + event.getDeltaY() / 40/10;

        Point2D pivot = new Point2D(event.getX(), event.getY());

        zoom(pivot, scale);
    }
}
