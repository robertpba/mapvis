package mapvis.layouts.pea.gui.actions;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.MapModel;

import java.awt.*;
import java.awt.geom.Area;

public class RenderBoundary implements RenderAction {

    private MapModel model;

    public RenderBoundary(MapModel model){

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


    private void drawPolygonBorder(Graphics2D g, Node node) {
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
            color = getColor(node);
            g.setColor(color);
            g.setStroke(new BasicStroke(1));
            g.draw(getArea(node));
        }
        else if (level == 2)
        {
            g.setStroke(new BasicStroke(secondLevelThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            color = getColor(node);
            g.setColor(color);

            Area a = getArea(node);
            //a = expandShape(a, 3);
            //a = expandShape(a, (float) 0.5);


            //g.draw(expandShape(getArea(node), 2));
            //g.draw(expandShape(getArea(node), 0.5));
            //g.draw(expandShape(getArea(node), 1));
            g.draw(a);
        }else if (level == 1){
            g.setStroke(new BasicStroke(firstLevelThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            color = getColor(node);
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

    protected int getLevel(Node node) {
        return ((int) model.getValue(node, "__level"));
    }
    protected Area getArea(Node node) {
        return ((Area) model.getValue(node, "__area"));
    }

    public Color firstLevelColor = Color.black;
    public float firstLevelThickness = 15;
    public float secondLevelThickness = 8;
    protected Color getColor(Node node) {
        int level = getLevel(node);
        if (level == 1)
            return firstLevelColor;
        else
            return Color.BLACK;
    }
}
