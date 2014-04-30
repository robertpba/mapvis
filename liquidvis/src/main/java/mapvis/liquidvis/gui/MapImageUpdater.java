package mapvis.liquidvis.gui;

import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.Polygon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapImageUpdater {
    private MapModel model;

    public MapImageUpdater(MapModel model) {
        this.model = model;

        createDescriptors(model.getRoot(), 0);
        descriptors = new ArrayList<>(nodeToDescriptor.values());

        mapPolygonFillingColor = (polygon)-> Color.blue;
    }

    private RegionDescriptor createDescriptors(Object node, int level) {
        if (model.getChildren(node).isEmpty()) {
            Polygon polygon = model.getPolygon(node);

            RegionDescriptor descriptor = new RegionDescriptor();
            descriptor.polygon = polygon;
            descriptor.nPoints = polygon.npoints;
            descriptor.x = new int[polygon.npoints];
            descriptor.y = new int[polygon.npoints];
            descriptor.node = polygon.node;
            descriptor.level = level;

            updatePosition(descriptor);
            setDescriptor(node, descriptor);
            return descriptor;
        }
        else {
            RegionDescriptor descriptor = new RegionDescriptor();
            descriptor.node = node;
            descriptor.area = new Area();
            descriptor.level = level;

            for (Object childNode : model.getChildren(node)) {
                RegionDescriptor child = createDescriptors(childNode, level+1);
                descriptor.area.add(child.area);
            }
            setDescriptor(node, descriptor);
            return descriptor;
        }
    }

    protected void setDescriptor(Object node, RegionDescriptor descriptor) {
        nodeToDescriptor.put(node, descriptor);
        model.setValue(node, "map region descriptor", descriptor);
    }

    private void updatePosition(Object node) {
        RegionDescriptor descriptor = nodeToDescriptor.get(node);
        if (model.getChildren(node).isEmpty()) {
            updatePosition(descriptor);
        }
        else {
            descriptor.area = new Area();

            for (Object childNode : model.getChildren(node)) {
                updatePosition(childNode);
                RegionDescriptor child = nodeToDescriptor.get(childNode);
                descriptor.area.add(child.area);

                        //expandShape(child.area, (float) 5.0));
            }
        }
    }

    public Area expandShape(Shape s, float d) {
        BasicStroke bs = new BasicStroke(d);
        // or new BasicStroke(d, CAP_ROUND, JOIN_ROUND);
        Area a = new Area(bs.createStrokedShape(s));
        a.add(new Area(s));
        return a;
    }




    public void updateImage(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0,0, image.getWidth(), image.getHeight());

        updatePosition(model.getRoot());

        for (RegionDescriptor descriptor : descriptors) {
            if (model.getChildren(descriptor.node).isEmpty())
                drawPolygon(g, descriptor);
        }

        for (RegionDescriptor descriptor : descriptors) {
            if (descriptor.level == 3)
                drawPolygonBorder(g, descriptor);
        }
        for (RegionDescriptor descriptor : descriptors) {
            if (descriptor.level == 2)
                drawPolygonBorder(g, descriptor);
        }
        for (RegionDescriptor descriptor : descriptors) {
            if (descriptor.level == 1)
                drawPolygonBorder(g, descriptor);
        }

        for (RegionDescriptor descriptor : descriptors) {
            if (model.getChildren(descriptor.node).isEmpty()) {
                //highlightVertices(g, descriptor);
                //drawSimplePolygonBorder(g, descriptor);
                drawPolygonOrigin(g, descriptor);
                drawPolygonCentroid(g, descriptor);
            }
        }
            drawLabel(g);

    }

    public class RegionDescriptor {
        public int nPoints;
        public int[] y;
        public int[] x;
        public int originX;
        public int originY;
        public int level;

        public Area    area;

        public Polygon polygon;
        public Object    node;
    }

    private List<RegionDescriptor> descriptors;
    private Map<Object, RegionDescriptor> nodeToDescriptor = new HashMap<>();

    private void drawPolygon(Graphics2D g, RegionDescriptor descriptor) {
        g.setColor(mapPolygonFillingColor.apply(descriptor.polygon));
        g.fill(descriptor.area);
    }

    private void drawSimplePolygonBorder(Graphics2D g, RegionDescriptor descriptor) {
        Color color;
        if (model.getChildren(descriptor.node).isEmpty()) {
            color = mapPolygonBorderColor.apply(descriptor.polygon);
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 220);
            g.setColor(color);
            g.setStroke(new BasicStroke(1));
            g.draw(descriptor.area);
        }
    }

    private void drawPolygonBorder(Graphics2D g, RegionDescriptor descriptor) {
        Color color;
        if (((Node)descriptor.node).level == 3)
        {
            //final float dash1[] = {10.0f};
    //                final BasicStroke dashed =
    //                        new BasicStroke(1.0f,
    //                                BasicStroke.CAP_BUTT,
    //                                BasicStroke.JOIN_MITER,
    //                                10.0f, dash1, 0.0f);
            g.setColor(Color.lightGray);
            g.setStroke(new BasicStroke(1));
            g.draw(descriptor.area);
        }
        else if (((Node)descriptor.node).level == 2)
        {
            g.setStroke(new BasicStroke(1));
            color = Color.darkGray;
            g.setColor(color);
            g.draw(expandShape(descriptor.area, 1));
        }else if (((Node)descriptor.node).level == 1){
            g.setStroke(new BasicStroke(3));
            color = Color.black;
            g.setColor(color);
            g.draw(expandShape(descriptor.area, 3));
        }
    }

    private void highlightVertices(Graphics2D g, RegionDescriptor descriptor) {
        // highlight vertices
        Color color = mapPolygonBorderColor.apply(descriptor.polygon);
        g.setColor(color.darker());

        for (int i=0; i< descriptor.nPoints; i++)
        {
            final int r = 4;
            int x = descriptor.x[i] - (r/2);
            int y = descriptor.y[i] -(r/2);

            g.setColor(color);
            g.fillOval(x,y,r,r);
        }
    }

    private void drawPolygonOrigin(Graphics2D g, RegionDescriptor descriptor){
        final int r = 4;
        int x = descriptor.originX - (r/2);
        int y = descriptor.originY - (r/2);

        g.setColor(Color.red);
        g.fillOval(x,y,r,r);
    }

    private void drawPolygonCentroid(Graphics2D g, RegionDescriptor descriptor){
        final int r = 4;
        Vector2D centroid = descriptor.polygon.calcCentroid();
        int x =  (int)centroid.x - (r/2);
        int y = (int)centroid.y - (r/2);

        g.setColor(Color.yellow);
        g.fillOval(x,y,r,r);
    }

    private void updatePosition(RegionDescriptor descriptor){
        Polygon polygon = descriptor.polygon;

        Path2D path = new Path2D.Double();
        descriptor.originX = (int) polygon.originX;
        descriptor.originY = (int) polygon.originY;
        for(int j=0; j<polygon.npoints; j++){
            descriptor.x[j] = (int) polygon.vertices[j].x;
            descriptor.y[j] = (int) polygon.vertices[j].y;
            if (j == 0)
                path.moveTo(polygon.vertices[j].x, polygon.vertices[j].y);
            else
                path.lineTo(polygon.vertices[j].x, polygon.vertices[j].y);
        }
        path.closePath();

        descriptor.area = new Area(path);
    }

    private void drawLabel(Graphics2D g){
        LabelDrawer<RegionDescriptor> labelDrawer = new LabelDrawer<RegionDescriptor>(
                descriptors,
                new LabelDrawer.ToLabel<RegionDescriptor>() {
                    @Override
                    public Rectangle2D bounds(RegionDescriptor node) {
                        return node.area.getBounds2D();
                    }

                    @Override
                    public int level(RegionDescriptor node) {
                        return node.level;
                    }

                    @Override
                    public String text(RegionDescriptor node) {
                        return ((Node)node.node).name;
                    }

                    @Override
                    public Point2D anchor(RegionDescriptor node) {
                        if (node.polygon != null){
                            Vector2D centroid = node.polygon.calcCentroid();
                            return new Point.Double(centroid.x,centroid.y);
                        }
                        else
                        return new Point.Double(
                                node.area.getBounds2D().getCenterX(),
                                node.area.getBounds2D().getCenterY());
                    }
                });
        labelDrawer.draw(g);


    }


    public Function<Polygon, Color> mapPolygonFillingColor;
    public Function<Polygon, Color> mapPolygonBorderColor;
}
