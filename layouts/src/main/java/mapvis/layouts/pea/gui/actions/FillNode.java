package mapvis.layouts.pea.gui.actions;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.MapModel;

import java.awt.*;
import java.awt.geom.Area;
import java.util.function.Function;

public class FillNode implements RenderAction {


    private MapModel model;
    private Function<Node, Color> toColor;

    public FillNode(MapModel model, Function<Node, Color> toColor){

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

    private void drawPolygon(Graphics2D g, Node node) {
        g.setColor(getColor(node));
        g.fill(getArea(node));
    }

    protected Area getArea(Node node){
        return (Area) model.getValue(node, "__area");
    }

    protected Color getColor(Node node){
        return toColor.apply(node);
    }
}
