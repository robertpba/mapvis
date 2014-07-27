package mapvis.graphic;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import mapvis.grid.Grid;
import mapvis.Impl.TreeModel;

import java.util.List;
import java.util.function.Function;

public class HexagonalTilingView extends Pane {

    public final HexagonRender render;
    Canvas canvas;

    public HexagonalTilingView(){
        super();
        setPrefHeight(1000);
        setPrefWidth(1000);
        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        render = new HexagonRender(this);

        getChildren().addAll(canvas);


        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragEntered);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        originX.addListener(this::onOriginXChange);
        originY.addListener(this::onOriginYChange);
        zoom.addListener(this::onZoomChange);
        styler.addListener(this::onStylerChange);

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
        if (getGrid() == null)
            return;

        //canvas = new Canvas(getWidth(),getHeight());

        GraphicsContext g = canvas.getGraphicsContext2D();

        g.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        //Rectangle2D rect = viewport.get();

        Bounds rect = getLayoutBounds();
        double x0 = -originXProperty().get()/zoomProperty().get();
        double y0 = -originYProperty().get()/zoomProperty().get();
        double x1 = (getWidth()-originXProperty().get())/zoomProperty().get();
        double y1 = (getHeight()-originYProperty().get())/zoomProperty().get();

        Point2D tl = planeToHexagonal(x0, y0);
        Point2D br = planeToHexagonal(x1, y1);

        g.save();

        g.translate(originXProperty().get(), originYProperty().get());
        g.scale(zoomProperty().get(), zoomProperty().get());



        grid.get().foreach(t -> {
            if (t.getX() > tl.getX()
                    && t.getX() < br.getX()
                    && t.getY() > tl.getY()
                    && t.getY() < br.getY())

                updateHexagon(t.getX(), t.getY(), g);
        });

        //getChildren().setAll(canvas);
        g.restore();
    }

    private void updateHexagon(int x, int y, GraphicsContext g) {

        g.save();
        Point2D point2D = hexagonalToPlain(x, y);
        g.translate(point2D.getX(), point2D.getY());

        render.drawHexagon(g,x,y);

        g.restore();
    }

    private ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty();
    public ObjectProperty<Grid<Integer>> gridProperty() { return this.grid; }
    public final Grid<Integer> getGrid() { return this.gridProperty().get(); }
    public final void setGrid(Grid<Integer> colormap) { this.gridProperty().set(colormap); }

    private ObjectProperty<TileStyler<Integer>> styler = new SimpleObjectProperty();
    public ObjectProperty<TileStyler<Integer>> stylerProperty() { return this.styler; }
    public final TileStyler<Integer> getStyler() { return this.stylerProperty().get(); }
    public final void setStyler(TileStyler<Integer> colormap) { this.stylerProperty().set(colormap); }

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
    void onStylerChange(ObservableValue<? extends TileStyler<Integer>> observable,
                        TileStyler<Integer> oldValue, TileStyler<Integer> newValue){
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
