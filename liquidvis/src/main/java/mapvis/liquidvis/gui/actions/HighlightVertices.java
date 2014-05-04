package mapvis.liquidvis.gui.actions;

import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.Polygon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class HighlightVertices<T> implements RenderAction {

    private MapModel<T> model;

    public HighlightVertices(MapModel<T> model){

        this.model = model;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {
        Color color = Color.CYAN;
        g.setColor(color.darker());
        final int r = 4;

        model.getLeaves().forEach(n -> {
            Polygon polygon = model.getPolygon(n);

            for (Vertex vertice : polygon.vertices) {

                Point2D pos = vertice.getPoint();

                int x = (int) pos.getX() - (r/2);
                int y = (int) pos.getY() - (r/2);

                g.setColor(color);
                g.fillOval(x,y,r,r);
            }
        });
    }
}
