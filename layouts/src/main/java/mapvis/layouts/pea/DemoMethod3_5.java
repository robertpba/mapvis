package mapvis.layouts.pea;

import mapvis.common.datatype.Node;
import mapvis.common.datatype.NodeUtils;
import mapvis.utils.algorithm.CircleOverlapRemoval;
import mapvis.layouts.pea.gui.*;
import mapvis.layouts.pea.gui.actions.*;
import mapvis.layouts.pea.method.method3.Method3;
import mapvis.layouts.pea.model.*;
import mapvis.layouts.pea.model.Polygon;
import mapvis.layouts.pea.model.handler.CollectStatistics;
import mapvis.common.datatype.TreeImp;
import mapvis.utils.colormap.ColorMap;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static mapvis.utils.Helper.interpolate;


public class DemoMethod3_5 {

    static void refinePoints(Node node, double scale, double tolerance) {
        //int scale = 8;
        java.util.List<Node> leaves = NodeUtils.getLeaves(node);
        for (Node leave : leaves) {
            double x = (double) leave.getVal("x");
            double y = (double) leave.getVal("y");
            double size = (double) leave.getVal("size");
            leave.setVal("x", (x + 50) * scale);
            leave.setVal("y", (y + 50) * scale);
            leave.setVal("size", size * scale * scale);
        }

        //width  = (int)((width + 100)* scale);
        //height = (int)((height + 100)* scale);

        CircleOverlapRemoval<Node> removal = new CircleOverlapRemoval<>(leaves,
                n -> new Point2D.Double((double)n.getVal("x"), (double)n.getVal("y")),
                n -> Math.sqrt((double)n.getVal("size") * tolerance / Math.PI));
        removal.run(1000);

        for (Node n : leaves) {
            Point2D position = removal.getPosition(n);
            n.setVal("x", position.getX());
            n.setVal("y", position.getY());
        }

        NodeUtils.filterBySize(node,0,"size");
    }


    public static void main (String[] args) throws IOException, InterruptedException {
        Node node = null;
        try {
            Yaml yaml = new Yaml();
            node = yaml.loadAs(new FileInputStream("layouts/data/tree3.yaml"), Node.class);
            refinePoints(node, 8, 0.81);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        TreeImp<Node> tree = TreeImp.from(node);

        MapModel<Node> model = new MapModel<>(tree, new MapModel.ToInitialValue<Node>() {
            @Override
            public Point2D getPosition(Node node) {
                return new Point2D.Double((double)node.getVal("x"), (double)node.getVal("y"));
            }

            @Override
            public double getMass(Node node) {
                return ((double)node.getVal("size")) * 1.4 * 0.81;
            }
        });
        Method3 method = new Method3(model);

        CollectStatistics collectStatistics = new CollectStatistics(model, 100);
        model.listeners.add(collectStatistics);

        BufferedImage image = new BufferedImage(8000, 8000, BufferedImage.TYPE_INT_RGB);

        Visualization visualization = new Visualization(image, model);
        visualization.backgroundColor = Color.decode("#aaffff");

        int[] nlevels = {0,1,4,16,64,256,1024};
        String[] ncolors = {"#ffffff","#aae8ff", "#ffff33", "#ffcc00", "#ff9900", "#ff6600", "#cc3300", "#990000"};
        Color[] colors = new Color[ncolors.length];
        for (int i = 0; i < ncolors.length; i++) {
            colors[i] = Color.decode(ncolors[i]);
        }

        final Function<Node, Color> colorMap1 = c -> {
            Polygon polygon = model.getPolygon(c);
            double v = interpolate(((polygon.mass - polygon.area)/polygon.mass), 0.0, 0.5, 1.0, 1.0);
            return ColorMap.JET.getColor(v);
        };

        final Function<Node, Color> colorMap2 = c -> {
            double v = (int)c.getVal("articles");
            if (v <= nlevels[1])
                return colors[1];
            else if (v <= nlevels[2])
                return colors[2];
            else if (v <= nlevels[3])
                return colors[3];
            else if (v <= nlevels[4])
                return colors[4];
            else if (v <= nlevels[5])
                return colors[5];
            else if (v <= nlevels[6])
                return colors[6];
            else
                return colors[7];
        };

        model.actions.add(new LevelEncoder<>(model));
        model.actions.add(new EncodeLabelText<>(model, Node::getLabel));
        model.actions.add(new CreateAreas<>(model));




        model.actions.add(new FillNode<>(model, colorMap2));

        RenderBoundary<Node> renderBoundary = new RenderBoundary<>(model);

        model.actions.add(renderBoundary);
        //model.actions.add(new RenderOriginCentroid<>(model));
        model.actions.add(new LabelRender<Node>(model){
            protected void renderLabel(Graphics2D g, Entry entry){
                Node element = (Node) entry.element;
                if (entry.level == 2 && element.getLabel().equals("Engineering")) {
                    Rectangle2D bounds = entry.label.getBounds();

                    entry.label.setPosition(bounds.getX(), bounds.getY() - 250);
                }
                super.renderLabel(g,entry);
            }
        });

        visualization.Start();
        visualization.iterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (renderBoundary.firstLevelColor == Color.black)
                    renderBoundary.firstLevelColor = visualization.backgroundColor;
                else
                    renderBoundary.firstLevelColor = Color.black;
            }
        });
        visualization.backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (renderBoundary.firstLevelThickness == 15) {
                    renderBoundary.firstLevelThickness = 10;
                    renderBoundary.secondLevelThickness = 6;
                } else if (renderBoundary.firstLevelThickness == 10) {
                    renderBoundary.firstLevelThickness = 5;
                    renderBoundary.secondLevelThickness = 3;
                }else {
                    renderBoundary.firstLevelThickness = 15;
                    renderBoundary.secondLevelThickness = 8;
                }
            }
        });

        Date d1= new Date();

        method.IterateUntilStable(100000);

        Date d2= new Date();

        System.out.println("finished!");

        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);



        method.growPolygons();

        System.gc();

        for (CollectStatistics.Statistics l : collectStatistics.ls) {
            System.out.printf("%d\t%f\n", l.iteration, l.error);
        }

        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.\n");

        for (int i = 0; i < model.iteration; i++) {
            System.out.printf("%d\t%d\n", i, method.movecount[i]);

        }
    }
}

