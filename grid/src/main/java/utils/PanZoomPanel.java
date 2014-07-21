package utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class PanZoomPanel extends Pane {
    public Group content = new Group();

    public PanZoomPanel(){
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setPrefSize(500, 500);
        getChildren().add(content);

        layoutBoundsProperty().addListener(this::onLayoutBoundsChange);

        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragEntered);
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        initializeProperties();
    }

    Rectangle clip = new Rectangle();
    private void onLayoutBoundsChange(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue){
        setClip(new Rectangle(getWidth(), getHeight()));
    }

    //////////////////////

    double originX = 0, originY = 0;
    double scaleFactor = 1;

    public void zoom(double scale){
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, scale);
    }
    public void zoomTo(double scale){
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, scale/this.scaleFactor);
    }
    public void zoom(Point2D pivot, double scale){
        Point2D o0 = content.localToParent(new Point2D(0, 0));
        double dx = (pivot.getX() - o0.getX()) * (scale - 1);
        double dy = (pivot.getY() - o0.getY()) * (scale - 1);
        double x1 = originX - dx;
        double y1 = originY - dy;

        Transform translateTransform = Affine.translate(x1, y1);
        originX = x1;
        originY = y1;
        panOriginX.set(x1);
        panOriginY.set(y1);

        double s0 = scaleFactor;
        double s1 = s0 * scale;
        //System.out.printf("%f, %f, %f\n", s0, s1, scale);
        scaleFactor = s1;
        zoomFactorProperty().set(s1);

        Transform scaleTransform = Affine.scale(s1, s1);
        content.getTransforms().clear();
        content.getTransforms().addAll(translateTransform,scaleTransform);
    }
    public void pan(double dx, double dy){
        Point2D o0 = new Point2D(originX, originY);
        Point2D o1 = new Point2D(originX+dx, originY+dy);
        pan(o0, o0, o1);
    }
    public void scrollTo(double x, double y){
        Point2D o0 = new Point2D(originX, originY);
        Point2D o1 = new Point2D(x+getWidth()/2, y+getHeight()/2);
        pan(o0, o0, o1);
    }
    private void pan(Point2D orig, Point2D from, Point2D to){
        double x = orig.getX();
        double y = orig.getY();
        double x1 = x + to.getX() - from.getX();
        double y1 = y + to.getY() - from.getY();
        //System.out.printf("trans: [%.1f,%.1f]\n", x1, y1);

        Transform translateTransform = Affine.translate(x1, y1);
        Transform scaleTransform = Affine.scale(scaleFactor, scaleFactor);

        originX = x1;
        originY = y1;
        panOriginX.set(x1);
        panOriginY.set(y1);

        content.getTransforms().clear();
        content.getTransforms().addAll(translateTransform, scaleTransform);
    }

    ////////////////////////

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

        saveOriginX = originX;
        saveOriginY = originY;
        saveOrigScale = scaleFactor;

        panButton = e.isPrimaryButtonDown();

        inDrag = true;
    }
    private void mouseReleased(MouseEvent e) {
        inDrag = false;
    }
    private void dragEntered(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        //System.out.printf("dragged: s[%.1f,%.1f] f[%.1f,%.1f] o[%.1f,%.1f] t[%.1f,%.1f]\n",
        //       saveOriginX,saveOriginY,
        //        startX, startY,
        //        originX, originY,
        //        x,y);

        pan(new Point2D(saveOriginX, saveOriginY),
                new Point2D(startX, startY),
                new Point2D(x, y));

    }
    private void onScroll(ScrollEvent event) {
        double scale =  1 + event.getDeltaY() / 40/10;
        Point2D o0 = content.localToParent(new Point2D(0, 0));

        Point2D pivot = new Point2D(event.getX(), event.getY());
        //System.out.printf("[%.1f,%.1f] [%.1f,%.1f] [%.1f,%.1f] %.1f %s\n",
        //        pivot.getX(),pivot.getY(),
        //        o0.getX(),o0.getY(),
        //        originX,originY,
        //        scaleFactor,
        //        event.getSource());

        zoom(pivot, scale);
    }

    //////////////////////






    private void initializeProperties() {
        zoomFactorProperty().addListener(this::onZoomFactorChange);
        panOriginXProperty().addListener(this::onPanOriginXChange);
        panOriginYProperty().addListener(this::onPanOriginYChange);
    }

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(scaleFactor);
    public DoubleProperty zoomFactorProperty() { return this.zoomFactor; }
    public final double getZoomFactor() { return this.zoomFactorProperty().get(); }
    public final void setZoomFactor(double zoom) { this.zoomFactorProperty().set(zoom); }
    public void onZoomFactorChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        if (newValue.doubleValue() == scaleFactor)
            return;
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, newValue.doubleValue() / scaleFactor);
    }

    private DoubleProperty panOriginX = new SimpleDoubleProperty(0);
    public DoubleProperty panOriginXProperty() { return this.panOriginX; }
    public final double getPanOriginX() { return this.panOriginXProperty().get(); }
    public final void setPanOriginX(double x) { this.panOriginXProperty().set(x); }
    public void onPanOriginXChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        if (newValue.doubleValue() == originX)
            return;
        Point2D o0 = new Point2D(originX, originY);
        Point2D o1 = new Point2D(newValue.doubleValue(), originY);
        pan(o0, o0, o1);
    }

    private DoubleProperty panOriginY = new SimpleDoubleProperty(0);
    public DoubleProperty panOriginYProperty() { return this.panOriginY; }
    public final double getPanOriginY() { return this.panOriginYProperty().get(); }
    public final void setPanOriginY(double y) { this.panOriginYProperty().set(y); }
    public void onPanOriginYChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        if (newValue.doubleValue() == originY)
            return;
        Point2D o0 = new Point2D(originX, originY);
        Point2D o1 = new Point2D(originX, newValue.doubleValue());
        pan(o0, o0, o1);
    }


}
