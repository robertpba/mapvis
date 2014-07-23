package utils;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

@DefaultProperty("content")
public class PanZoomPanel extends Pane {

    PanZoomBehavior behaviour;

    public PanZoomPanel(){
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setPrefSize(500, 500);

        behaviour = new PanZoomBehavior(contentProperty(), this,
                widthProperty(),
                heightProperty(),
                panOriginXProperty(),
                panOriginYProperty(),
                zoomFactorProperty());

        layoutBoundsProperty().addListener(this::onLayoutBoundsChange);

    }


    public final ObjectProperty<Node> contentProperty() {
        if (content == null) {
            content = new ChildNodeProperty("content");
        }
        return content;
    }
    private ObjectProperty<Node> content;
    public final void setContent(Node value) { contentProperty().set(value); }
    public final Node getContent() { return content == null ? null : content.get(); }


    private void onLayoutBoundsChange(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue){
        setClip(new Rectangle(getWidth(), getHeight()));
    }

    //////////////////////

    public void zoom(double scale){
        behaviour.zoom(scale);
    }
    public void zoomTo(double scale){
        behaviour.zoomTo(scale);
    }
    public void zoom(Point2D pivot, double scale){
        behaviour.zoom(pivot,scale);
    }
    public void pan(double dx, double dy){
        behaviour.pan(dx,dy);
    }
    public void scrollTo(double x, double y){
        behaviour.scrollTo(x,y);
    }

    ////////////////////////

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);
    public DoubleProperty zoomFactorProperty() { return this.zoomFactor; }
    public final double getZoomFactor() { return this.zoomFactorProperty().get(); }
    public final void setZoomFactor(double zoom) { this.zoomFactorProperty().set(zoom); }

    private DoubleProperty panOriginX = new SimpleDoubleProperty(0);
    public DoubleProperty panOriginXProperty() { return this.panOriginX; }
    public final double getPanOriginX() { return this.panOriginXProperty().get(); }
    public final void setPanOriginX(double x) { this.panOriginXProperty().set(x); }

    private DoubleProperty panOriginY = new SimpleDoubleProperty(0);
    public DoubleProperty panOriginYProperty() { return this.panOriginY; }
    public final double getPanOriginY() { return this.panOriginYProperty().get(); }
    public final void setPanOriginY(double y) { this.panOriginYProperty().set(y); }


    private final class ChildNodeProperty extends ObjectPropertyBase<Node> {
        private Node oldValue = null;
        private final String propertyName;
        private boolean isBeingInvalidated;

        ChildNodeProperty(String propertyName) {
            this.propertyName = propertyName;
            getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
                if (oldValue == null || isBeingInvalidated) {
                    return;
                }
                while (c.next()) {
                    if (c.wasRemoved()) {
                        List<? extends Node> removed = c.getRemoved();
                        // Do not remove again in invalidated
                        removed.stream()
                                .filter(aRemoved -> aRemoved == oldValue)
                                .forEach(aRemoved -> {
                            oldValue = null; // Do not remove again in invalidated
                            set(null);
                        });
                    }
                }
            });
        }

        @Override
        protected void invalidated() {
            final List<Node> children = getChildren();

            isBeingInvalidated = true;
            try {
                if (oldValue != null) {
                    children.remove(oldValue);
                }

                final Node _value = get();
                this.oldValue = _value;

                if (_value != null) {
                    children.add(_value);
                }
            } finally {
                isBeingInvalidated = false;
            }
        }

        @Override
        public Object getBean() {
            return PanZoomPanel.this;
        }

        @Override
        public String getName() {
            return propertyName;
        }
    }
}
