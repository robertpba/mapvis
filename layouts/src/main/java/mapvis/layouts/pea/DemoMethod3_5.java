package mapvis.layouts.pea;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.NodeUtils;
import mapvis.layouts.Epea;
import mapvis.utils.algorithm.CircleOverlapRemoval;
import mapvis.layouts.pea.gui.*;
import mapvis.layouts.pea.model.*;
import mapvis.layouts.pea.model.handler.CollectStatistics;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static mapvis.utils.Helper.interpolate;


public class DemoMethod3_5 {

    static void refinePoints(Node node, double scale, double tolerance) {
        //int scale = 8;
        java.util.List<INode> leaves = NodeUtils.getLeaves(node);
        for (INode leave : leaves) {
            double x = (double) leave.getVal("x");
            double y = (double) leave.getVal("y");
            double size = (double) leave.getVal("size");
            leave.setVal("x", (x + 50) * scale);
            leave.setVal("y", (y + 50) * scale);
            leave.setVal("size", size * scale * scale);
        }

        //width  = (int)((width + 100)* scale);
        //height = (int)((height + 100)* scale);

        CircleOverlapRemoval<INode> removal = new CircleOverlapRemoval<>(leaves,
                n -> new Point2D.Double((double)n.getVal("x"), (double)n.getVal("y")),
                n -> Math.sqrt((double)n.getVal("size") * tolerance / Math.PI));
        removal.run(1000);

        for (INode n : leaves) {
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
            node = yaml.loadAs(new FileInputStream("layouts/data/epea02.yaml"), Node.class);
            //refinePoints(node, 8, 0.81);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MapModel model = new MapModel(node, new MapModel.Initializer() {
            @Override
            public Point2D getPosition(INode node) {
                return new Point2D.Double((double)node.getVal("x"), (double)node.getVal("y"));
            }

            @Override
            public double getMass(INode node) {
                return ((double)node.getVal("size"));// * 1.4 * 0.81;
            }
        });
        Epea method = new Epea(model);

        CollectStatistics collectStatistics = new CollectStatistics(model, 100);
        model.listeners.add(collectStatistics);

        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        Visualization visualization = new Visualization(image, model);
        visualization.backgroundColor = Color.decode("#aaffff");


        visualization.Start();

        Date d1= new Date();

        method.IterateUntilStable(40000);

        Date d2= new Date();

        System.out.println("finished!");

        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        method.growPolygons(5);

        System.gc();

        for (CollectStatistics.Statistics l : collectStatistics.ls) {
            System.out.printf("%d\t%f\n", l.iteration, l.error);
        }

        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.\n");

        for (int i = 0; i < model.iteration; i++) {
            System.out.printf("%d\t%d\n", i, Epea.movecount[i]);

        }
    }
}

