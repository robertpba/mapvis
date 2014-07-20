package mapvis.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mapvis.grid.Grid;
import mapvis.grid.Pos;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class GridPanel extends Pane {
    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double SideLength = 10;

    static protected Polygon standardHexagon;
    static {
        standardHexagon = new Polygon();
        standardHexagon.getPoints().addAll(
                -SideLength/2, -SideLength*COS30,
                SideLength/2, -SideLength*COS30,
                SideLength, 0.0,
                SideLength/2, SideLength*COS30,
                -SideLength/2, SideLength*COS30,
                -SideLength, 0.0);

        //    0 - 1
        //  5   c   2
        //    4 - 3
    }

    public Grid<?> grid;

    public Point2D gridToPlaneCoordinate(int x, int y){
        double cx = x * 3 * SideLength / 2;
        double cy;
        cy = 2 * COS30 * SideLength * y;

        if (x % 2 != 0) {
            cy += COS30 * SideLength;
        }

        return new Point2D(cx, cy);
    }
    public Point planeToGridCoordinate(double x, double y){
        double cx = x / 3 * 2 / SideLength;
        int nx = (int) Math.round(cx);
        int ny;

        if (nx%2 == 0) {
            ny = (int)Math.round(y / 2 / COS30 / SideLength);
        }else {
            ny = (int)Math.round((y - COS30 * SideLength) / 2 / COS30 / SideLength);
        }

        return new Point(nx, ny);
    }
    public Point paneToGridCoordinate(double x, double y){
        Point2D point2D = g.parentToLocal(x, y);
        return planeToGridCoordinate(point2D.getX(), point2D.getY());
    }

    public GridPanel(){
        //ButtonZoomDevice zoomDevice = new ButtonZoomDevice(this);
        setPrefSize(500, 500);
        getChildren().add(g);

        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragEntered);
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);

        Rectangle clipRectangle = new Rectangle();
        setClip(clipRectangle);
        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            clipRectangle.setWidth(newValue.getWidth());
            clipRectangle.setHeight(newValue.getHeight());
        });

        //g.getChildren().addAll(hexagons.values());

        zoomFactorProperty().addListener((observable, oldValue, newValue) -> {
            g.setScaleX(newValue.doubleValue());
            g.setScaleY(newValue.doubleValue());
        });
        panFactorXProperty().addListener((observable, oldValue, newValue) -> g.setTranslateX(newValue.doubleValue()));
        panFactorYProperty().addListener((observable, oldValue, newValue) -> g.setTranslateY(newValue.doubleValue()));

        updateHexagons();

    }



    Group g = new Group();
    Map<Pos, Shape> hexagons = new HashMap<>();

    public void updateHexagons(){
        g.getChildren().removeAll();

        if (grid == null)
            return;

        Point tl = paneToGridCoordinate(0, 0);
        Point br = paneToGridCoordinate(getWidth(), getHeight());
        tl.x -= 1;
        tl.y -= 1;
        br.x += 1;
        br.y += 1;

        hexagons.clear();

        grid.foreach(t-> updateHexagon(t.getX(), t.getY()));


        g.getChildren().addAll(hexagons.values());

        // updateHexagon(1, 3);
        // updateHexagon(1, 4);
        // updateHexagon(1, 4);
    }

    private void updateHexagon(int x, int y) {
        Object o = grid.get(x, y);
        Pos pos = new Pos(x, y);
        Shape shape = hexagons.get(pos);
        if (shape == null){
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(standardHexagon.getPoints());
            polygon.setFill(colorMap==null?Color.RED:colorMap.apply(o));
            shape = polygon;
            hexagons.put(pos, shape);
        }

        Point2D point2D = gridToPlaneCoordinate(x, y);
        shape.setTranslateX(point2D.getX());
        shape.setTranslateY(point2D.getY());
    }


        double startX = -1, startY = -1;
        double origTranslateX,origTranslateY;
        double origScale;

        boolean inDrag;

        boolean panButton;

        public void mousePressed(MouseEvent e) {
            System.out.println("translate: " + g.getTranslateX() + ", " + g.getTranslateY());
            System.out.println("scale: "+g.getScaleX()+", " + g.getScaleY());

            double x = e.getX();
            double y = e.getY();

            startX = x;
            startY = y;

            origTranslateX = getPanFactorX();
            origTranslateY = getPanFactorY();
            origScale = getZoomFactor();

            panButton = e.isPrimaryButtonDown();

            inDrag = true;

            Point point = paneToGridCoordinate(x, y);
            System.out.println(point);
        }
        public void mouseReleased(MouseEvent e) {
            inDrag = false;
        }
        public void dragEntered(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();
            //System.err.println("mouse Dragged from " + startX+","+startY +" to " + x+","+y);

            double dx = (startX - x);
            double dy = (startY - y);

            if (panButton) {
                setPanFactorX(origTranslateX - dx);
                setPanFactorY(origTranslateY - dy);
            } else {
                double zf = getZoomFactor();
                zf += dx/getWidth()* origScale  + dy/getHeight()*origScale;
                zf = Math.max(0.1, zf);
                zf = Math.min(1.0, zf);
                setZoomFactor(zf);
            }
        }


    public Function<Object, Color> colorMap;

    private DoubleProperty zoomFactor;
    public DoubleProperty zoomFactorProperty() {
        if(this.zoomFactor == null) { this.zoomFactor = new SimpleDoubleProperty(1.0); }
        return this.zoomFactor;
    }
    public final double getZoomFactor() { return this.zoomFactorProperty().get(); }
    public final void setZoomFactor(double height) { this.zoomFactorProperty().set(height); }

    private DoubleProperty panFactorX;
    public DoubleProperty panFactorXProperty() {
        if(this.panFactorX == null) { this.panFactorX = new SimpleDoubleProperty(0.0); }
        return this.panFactorX;
    }
    public final double getPanFactorX() { return this.panFactorXProperty().get(); }
    public final void setPanFactorX(double x) { this.panFactorXProperty().set(x); }

    private DoubleProperty panFactorY;
    public DoubleProperty panFactorYProperty() {
        if(this.panFactorY == null) { this.panFactorY = new SimpleDoubleProperty(0.0); }
        return this.panFactorY;
    }
    public final double getPanFactorY() { return this.panFactorYProperty().get(); }
    public final void setPanFactorY(double x) { this.panFactorYProperty().set(x); }
}
