package mapvis.layouts.pea;

import mapvis.layouts.pea.gui.Visualization;
import mapvis.layouts.Pea;
import mapvis.layouts.pea.model.*;
import mapvis.layouts.pea.model.handler.CollectStatistics;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.geom.Point2D;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import mapvis.common.datatype.Node;


public class DemoMethod2 {

    public static void main (String[] args) throws IOException, InterruptedException {
        Yaml yaml = new Yaml();
        String filename = "layouts/data/pea01.yaml";
        Node node = yaml.loadAs(
                new FileInputStream(filename), Node.class);

        MapModel model = new MapModel(node, new MapModel.Initializer() {
            @Override
            public Point2D getPosition(Node n) {
                return new Point2D.Double((double)n.getVal("x"), (double)n.getVal("y"));
            }
            @Override
            public double getMass(Node n) {
                return (double)n.getVal("size");
            }
        });


        Pea method = new Pea(model);

        model.listeners.add(new CollectStatistics(model, 10));


        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        Visualization visualization = new Visualization(image, model);

        visualization.backgroundColor = Color.decode("#aaffff");

        visualization.Start();

        method.IterateUntilStable(10000);


        System.gc();

        //observer.save("output/save.png");
    }
}

