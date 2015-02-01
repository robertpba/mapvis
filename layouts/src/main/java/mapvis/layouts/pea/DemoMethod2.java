package mapvis.layouts.pea;

import mapvis.layouts.pea.gui.Visualization;
import mapvis.layouts.pea.gui.actions.*;
import mapvis.layouts.pea.method.method2.Method2;
import mapvis.layouts.pea.model.*;
import mapvis.layouts.pea.model.handler.CollectStatistics;
import mapvis.utils.colormap.ColorMap;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.geom.Point2D;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Function;

import mapvis.common.datatype.Node;

import static mapvis.utils.Helper.interpolate;


public class DemoMethod2 {

    public static void main (String[] args) throws IOException, InterruptedException {
        Yaml yaml = new Yaml();
        String filename = "layouts/data/tree1.yaml";
        Node node = yaml.loadAs(
                new FileInputStream(filename), Node.class);

        MapModel model = new MapModel(node, new MapModel.Initializer() {
            @Override
            public Point2D getPosition(Node n) {
                return new Point2D.Double((double)n.getVal("x")/10, (double)n.getVal("y")/10);
            }
            @Override
            public double getMass(Node n) {
                return (int)n.getVal("articles");
            }
        });


        Method2 method = new Method2(model);

        model.listeners.add(new CollectStatistics(model, 10));


        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        Visualization visualization = new Visualization(image, model);

        visualization.backgroundColor = Color.decode("#aaffff");

        int[] nlevels = {0,1,4,16,64,256,1024};
        String[] ncolors = {"#ffffff","#aae8ff", "#ffff33", "#ffcc00", "#ff9900", "#ff6600", "#cc3300", "#990000"};
        Color[] colors = new Color[ncolors.length];
        for (int i = 0; i < ncolors.length; i++) {
            colors[i] = Color.decode(ncolors[i]);
        }

        final Function<Node, Color> colorMap1 = c -> {
            mapvis.layouts.pea.model.Polygon polygon = model.getPolygon(c);
            double v = interpolate(((polygon.mass - polygon.area)/polygon.mass), 0.0, 0.5, 1.0, 1.0);
            return ColorMap.JET.getColor(v);
        };

        model.actions.add(new LevelEncoder(model));
        //model.actions.add(new EncodeLabelText<>(model, Node::getLabel));
        model.actions.add(new CreateAreas(model));




        //model.actions.add(new FillNode<>(model, colorMap2));

        RenderBoundary renderBoundary = new RenderBoundary(model);

        model.actions.add(renderBoundary);
        //model.actions.add(new RenderOriginCentroid<>(model));
        //model.actions.add(new LabelRender<>(model));

        visualization.Start();

        method.IterateUntilStable(10000);


        System.gc();

        //observer.save("output/save.png");
    }
}

