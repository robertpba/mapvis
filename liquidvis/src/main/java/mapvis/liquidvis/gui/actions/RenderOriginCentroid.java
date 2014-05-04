package mapvis.liquidvis.gui.actions;

import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.Polygon;

import java.awt.*;
import java.awt.geom.Point2D;

public class RenderOriginCentroid<T> implements RenderAction {

    private MapModel<T> model;

    public RenderOriginCentroid(MapModel<T> model) {

        this.model = model;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {
        model.getLeaves().forEach(n -> {
            drawPolygonOrigin(g, n);
            drawPolygonCentroid(g, n);
        });
    }


    private void drawPolygonOrigin(Graphics2D g, T node){
        final int r = 4;
        Polygon polygon = model.getPolygon(node);
        int x = (int)polygon.originX - (r/2);
        int y = (int)polygon.originY - (r/2);

        g.setColor(Color.red);
        g.fillOval(x,y,r,r);
    }

    private void drawPolygonCentroid(Graphics2D g, T node){
        final int r = 4;
        Polygon polygon = model.getPolygon(node);
        Point2D centroid = polygon.calcCentroid();
        int x =  (int)centroid.getX() - (r/2);
        int y = (int)centroid.getY() - (r/2);

        g.setColor(Color.yellow);
        g.fillOval(x,y,r,r);
    }

}