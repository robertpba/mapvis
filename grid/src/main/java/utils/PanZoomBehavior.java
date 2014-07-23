package utils;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class PanZoomBehavior {
    private SimpleObjectProperty<Node> view = new SimpleObjectProperty<>();
    private Node track;

    // the width and height of the viewport,
    // used to calculate the center as the the pivot of zooming
    private DoubleProperty width = new SimpleDoubleProperty();
    private DoubleProperty height = new SimpleDoubleProperty();

    // translate
    private DoubleProperty originX = new SimpleDoubleProperty();
    private DoubleProperty originY = new SimpleDoubleProperty();
    // scale
    private DoubleProperty zoom = new SimpleDoubleProperty();

    public PanZoomBehavior(ReadOnlyObjectProperty<Node> view,
                           Node track,
                           ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height,
                           DoubleProperty originX, DoubleProperty originY,
                           DoubleProperty zoom){
        this.view.bind(view);
        this.track = track;

        this.width.bind(width);
        this.height.bind(height);
        this.originX.bindBidirectional(originX);
        this.originY.bindBidirectional(originY);
        this.zoom.bindBidirectional(zoom);

        track.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragEntered);
        track.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        track.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        track.addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        originX.addListener(this::onOriginXChange);
        originY.addListener(this::onOriginYChange);
        zoom.addListener(this::onZoomChange);
    }

    public void zoom(double scale){
        Point2D center = new Point2D(width.get() / 2, height.get() / 2);
        zoom(center, scale);
    }
    public void zoomTo(double scale){
        Point2D center = new Point2D(width.get() / 2, height.get() / 2);
        zoom(center, scale/this.zoom.get());
    }
    public void pan(double dx, double dy){
        Point2D o0 = new Point2D(originX.get(), originY.get());
        Point2D o1 = new Point2D(originX.get()+dx, originY.get()+dy);
        pan(o0, o0, o1);
    }
    public void scrollTo(double x, double y){
        Point2D o0 = new Point2D(originX.get(), originY.get());
        Point2D o1 = new Point2D(x+width.get()/2, y+height.get()/2);
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

        if (view.get() == null) return;
        view.get().getTransforms().clear();
        view.get().getTransforms().addAll(translateTransform,scaleTransform);
    }
    void onOriginXChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        translateTransform = Affine.translate(newValue.doubleValue(), originY.get());
        if (view.get() == null) return;
        view.get().getTransforms().clear();
        view.get().getTransforms().addAll(translateTransform,scaleTransform);
    }
    void onOriginYChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        translateTransform = Affine.translate(originX.get(), newValue.doubleValue());
        if (view.get() == null) return;
        view.get().getTransforms().clear();
        view.get().getTransforms().addAll(translateTransform,scaleTransform);
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
        // Point2D o0 = getContent().localToParent(new Point2D(0, 0));

        Point2D pivot = new Point2D(event.getX(), event.getY());
        //System.out.printf("[%.1f,%.1f] [%.1f,%.1f] [%.1f,%.1f] %.1f %s\n",
        //        pivot.getX(),pivot.getY(),
        //        o0.getX(),o0.getY(),
        //        originX,originY,
        //        scaleFactor,
        //        event.getSource());

        zoom(pivot, scale);
    }
}
