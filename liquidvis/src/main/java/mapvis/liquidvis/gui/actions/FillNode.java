package mapvis.liquidvis.gui.actions;

import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.MapModel;

import java.awt.*;
import java.awt.geom.Area;
import java.util.function.Function;

public class FillNode<T> implements RenderAction {

    private MapModel<T> model;
    private Function<T, Color> toColor;

    public FillNode(MapModel<T> model, Function<T, Color> toColor){

        this.model = model;
        this.toColor = toColor;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {

        model.getLeaves().forEach(n -> drawPolygon(g, n));

    }

    private void drawPolygon(Graphics2D g, T node) {
        g.setColor(getColor(node));
        g.fill(getArea(node));
    }

    protected Area getArea(T node){
        return (Area) model.getValue(node, "__area");
    }

    protected Color getColor(T node){
        return toColor.apply(node);
    }
}
