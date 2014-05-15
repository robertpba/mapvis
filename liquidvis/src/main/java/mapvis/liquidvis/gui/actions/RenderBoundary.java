package mapvis.liquidvis.gui.actions;

import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.MapModel;

import java.awt.*;
import java.awt.geom.Area;

public class RenderBoundary<T> implements RenderAction {

    private MapModel<T> model;

    public RenderBoundary(MapModel<T> model){

        this.model = model;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {
        model.getAllNodes().stream().filter(n-> getLevel(n) == 3).forEach(n->drawPolygonBorder(g, n));
        model.getAllNodes().stream().filter(n-> getLevel(n) == 2).forEach(n->drawPolygonBorder(g, n));
        model.getAllNodes().stream().filter(n-> getLevel(n) == 1).forEach(n->drawPolygonBorder(g, n));
    }

    protected Color getColor(Object node){
        return Color.BLACK;
    }

    private void drawPolygonBorder(Graphics2D g, T node) {
        Color color;
        int level = getLevel(node);
        if (level == 3)
        {
            //final float dash1[] = {10.0f};
            //                final BasicStroke dashed =
            //                        new BasicStroke(1.0f,
            //                                BasicStroke.CAP_BUTT,
            //                                BasicStroke.JOIN_MITER,
            //                                10.0f, dash1, 0.0f);
            g.setColor(Color.black);
            g.setStroke(new BasicStroke(1));
            g.draw(getArea(node));
        }
        else if (level == 2)
        {
            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            color = Color.black;
            g.setColor(color);

            Area a = getArea(node);
            //a = expandShape(a, 3);
            //a = expandShape(a, (float) 0.5);


            //g.draw(expandShape(getArea(node), 2));
            //g.draw(expandShape(getArea(node), 0.5));
            //g.draw(expandShape(getArea(node), 1));
            g.draw(a);
        }else if (level == 1){
            g.setStroke(new BasicStroke(15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            color = Color.black;
            g.setColor(color);
            //g.draw(expandShape(getArea(node), 1));
            //g.draw(getArea(node));
            Area a = getArea(node);
            //a = expandShape(a, 2);
            //a = expandShape(a, (float) 0.5);
            g.draw(a);
        }
    }
    private Area expandShape(Shape s, float d) {
        //BasicStroke bs = new BasicStroke(d);
        BasicStroke bs = new BasicStroke(d, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Area a = new Area(bs.createStrokedShape(s));
        a.add(new Area(s));
        return a;
    }

    protected int getLevel(T node) {
        return ((int) model.getValue(node, "__level"));
    }
    protected Area getArea(T node) {
        return ((Area) model.getValue(node, "__area"));
    }

}
