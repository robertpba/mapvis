package mapvis.layouts.pea.gui;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.model.MapModel;
import mapvis.layouts.pea.model.Polygon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;

public class Drawer {
    private MapModel model;

    public double[] thickness = new double[]{0.0, 15.0, 8.0, 5.0};

    public Drawer(MapModel model){
        this.model = model;
    }

    static class Entry {
        Node node;
        Polygon polygon;
        Area area;
        int level;
        boolean leaf;
    }
    Collection<Entry> entries;

    public void draw(Graphics2D g) {
        entries = new ArrayList<>();
        updateEntry(model.getRoot(), 0);
        drawNode(g);
        drawBorder(g);
    }



    Entry updateEntry(Node node, int level) {
        Entry entry = new Entry();
        entry.node = node;
        entry.polygon = model.getPolygon(node);
        entry.level = level;

        Collection<Node> children = node.getChildren();
        if (children.isEmpty()) {
            entry.leaf = true;

            Path2D path = new Path2D.Double();
            for (int j = 0; j < entry.polygon.npoints; j++) {
                Point2D pos = entry.polygon.vertices[j].getPoint();
                double x = pos.getX();
                double y = pos.getY();
                if (j == 0)
                    path.moveTo(x, y);
                else
                    path.lineTo(x, y);
            }
            path.closePath();

            entry.area = new Area(path);
        } else {
            entry.leaf = false;
            entry.area = new Area();

            for (Node childNode : children) {
                Entry childEntry = updateEntry(childNode, level + 1);
                entry.area.add(childEntry.area);
            }
        }
        entries.add(entry);
        return entry;
    }

    public void drawNode(Graphics2D g){
        entries.stream().filter(e->e.leaf)
                .forEach(e->{
                    g.setColor(Color.DARK_GRAY);
                    g.fill(e.area);
                });
    }

    public void drawBorder(Graphics2D g) {
        entries.stream()
                .filter(e->e.node != model.getRoot())
                .sorted((e1,e2)->e2.level-e1.level)
                .forEach(n->drawPolygonBorder(g, n));
    }

    private void drawPolygonBorder(Graphics2D g, Entry entry) {
        Color color;
        color = Color.black;
        g.setColor(color);
        g.setStroke(new BasicStroke((float) thickness[entry.level],
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(entry.area);
    }
    private Area expandShape(Shape s, float d) {
        //BasicStroke bs = new BasicStroke(d);
        BasicStroke bs = new BasicStroke(d, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Area a = new Area(bs.createStrokedShape(s));
        a.add(new Area(s));
        return a;
    }
}
