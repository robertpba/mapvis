package mapvis.liquidvis.gui;

import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.Polygon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapImageUpdater {
    private MapModel model;

    public MapImageUpdater(MapModel model) {
        this.model = model;

        createDescriptors(model.root, 0);
        descriptors = new ArrayList<>(nodeToDescriptor.values());

        mapPolygonFillingColor = (polygon)-> mapColor(0,2000, (int)(polygon.mass - polygon.area));
        mapPolygonBorderColor  = (polygon)-> {
            Color[] colors = new Color[]{
                    Color.BLACK,
                    Color.WHITE,
                    Color.LIGHT_GRAY,
                    Color.BLUE,
                    Color.ORANGE,
                    Color.CYAN,
                    Color.RED,
                    Color.PINK
            };

            return colors[polygon.node.id % colors.length];
        };
    }

    private RegionDescriptor createDescriptors(Node node, int level) {
        if (node.children == null || node.children.length == 0) {
            Polygon polygon = model.getPolygons().get(node);

            RegionDescriptor descriptor = new RegionDescriptor();
            descriptor.polygon = polygon;
            descriptor.nPoints = polygon.npoints;
            descriptor.x = new int[polygon.npoints];
            descriptor.y = new int[polygon.npoints];
            descriptor.node = polygon.node;
            descriptor.level = level;

            updatePosition(descriptor);
            nodeToDescriptor.put(node, descriptor);
            return descriptor;
        }
        else {
            RegionDescriptor descriptor = new RegionDescriptor();
            descriptor.node = node;
            descriptor.area = new Area();
            descriptor.level = level;

            for (Node childNode : node.children) {
                RegionDescriptor child = createDescriptors(childNode, level+1);
                descriptor.area.add(child.area);
            }
            nodeToDescriptor.put(node, descriptor);
            return descriptor;
        }
    }

    private void updatePosition(Node node) {
        RegionDescriptor descriptor = nodeToDescriptor.get(node);
        if (node.children == null || node.children.length == 0) {
            updatePosition(descriptor);
        }
        else {
            descriptor.area = new Area();

            for (Node childNode : node.children) {
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

        updatePosition(model.root);

        for (RegionDescriptor descriptor : descriptors) {
            if (descriptor.node.children == null || descriptor.node.children.length == 0)
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
            if (descriptor.node.children == null || descriptor.node.children.length == 0) {
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
        public Node    node;
    }

    private List<RegionDescriptor> descriptors;
    private Map<Node, RegionDescriptor> nodeToDescriptor = new HashMap<>();

    private void drawPolygon(Graphics2D g, RegionDescriptor descriptor) {
        g.setColor(mapPolygonFillingColor.apply(descriptor.polygon));
        g.fill(descriptor.area);
    }

    private void drawSimplePolygonBorder(Graphics2D g, RegionDescriptor descriptor) {
        Color color;
        if (descriptor.node.children == null || descriptor.node.children.length == 0) {
            color = mapPolygonBorderColor.apply(descriptor.polygon);
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 220);
            g.setColor(color);
            g.setStroke(new BasicStroke(1));
            g.draw(descriptor.area);
        }
    }

    private void drawPolygonBorder(Graphics2D g, RegionDescriptor descriptor) {
        Color color;
        if (descriptor.node.level == 3)
        {
            //final float dash1[] = {10.0f};
//                final BasicStroke dashed =
//                        new BasicStroke(1.0f,
//                                BasicStroke.CAP_BUTT,
//                                BasicStroke.JOIN_MITER,
//                                10.0f, dash1, 0.0f);
            g.setColor(Color.gray);
            g.setStroke(new BasicStroke(1));
            g.draw(descriptor.area);
        }
        else if (descriptor.node.level == 2)
        {
            g.setStroke(new BasicStroke(1));
            color = Color.black;
            g.setColor(color);
            g.draw(expandShape(descriptor.area, 1));
        }else if (descriptor.node.level == 1){
            g.setStroke(new BasicStroke(5));
            color = Color.black;
            g.setColor(color);
            g.draw(expandShape(descriptor.area, 5));
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

    public static Color mapColor(int minVal, int maxVal, int actual) {
        actual = Math.min(maxVal, actual);
        actual = Math.max(minVal, actual);
        double percentage = ((double)(maxVal - actual))/((double)(maxVal - minVal));
        int intR = 10;
        int intG = 10;
        int intB = 0+(int)(percentage*255);

        return new Color(intR, intG, intB);
    }

    private void drawLabel(Graphics2D g){
        LabelDrawer<RegionDescriptor> labelDrawer = new LabelDrawer<RegionDescriptor>(
                descriptors.stream().filter(d->d.level == 3).collect(Collectors.toList()),
                d -> d.node.name,
                d -> d.level,
                d -> d.area.getBounds());
        labelDrawer.draw(g);

        LabelDrawer<RegionDescriptor> labelDrawer2 = new LabelDrawer<RegionDescriptor>(
                descriptors.stream().filter(d->d.level == 2).collect(Collectors.toList()),
                d -> d.node.name,
                d -> d.level,
                d -> d.area.getBounds());
        labelDrawer2.draw(g);

        LabelDrawer<RegionDescriptor> labelDrawer1 = new LabelDrawer<RegionDescriptor>(
                descriptors.stream().filter(d->d.level == 1).collect(Collectors.toList()),
                d -> d.node.name,
                d -> d.level,
                d -> d.area.getBounds());
        labelDrawer1.draw(g);


    }


    public Function<Polygon, Color> mapPolygonFillingColor;
    public Function<Polygon, Color> mapPolygonBorderColor;
}
