package mapvis;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
import mapvis.io.random.Config;
import mapvis.io.random.RandomNodeGen;
import mapvis.io.random.YamlCfgReader;
import mapvis.layouts.Dac;
import mapvis.layouts.Epea;
import mapvis.layouts.Pea;
import mapvis.layouts.pea.gui.Visualization;
import mapvis.layouts.pea.model.MapModel;
import mapvis.layouts.pea.model.handler.CollectStatistics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EPEA {

    public static void enlarge(INode root, double scale){
        root.setVal("size", (double)root.getVal("size")*scale);

        root.getChildren().forEach(n->enlarge(n,scale));
    }

    public static void main(String[] args) throws IOException {
        YamlCfgReader reader = new YamlCfgReader();
        Config config = reader.read("demo/data/epea_cfg_01.yaml");

        System.out.print( new YamlCfgReader().dump(config));

        RandomNodeGen generator = new RandomNodeGen();
        Node root = generator.getTree(config);

        // preliminary layout
        Dac.run(root, 10000);
        RectangleCanvas.export(root);

        enlarge(root,1.3);

        MapModel model = new MapModel(root, new MapModel.Initializer() {
        });
        Epea method = new Epea(model);

        model.listeners.add(new CollectStatistics(model, 10));


        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        Visualization visualization = new Visualization(image, model);

        visualization.backgroundColor = Color.decode("#aaffff");

        visualization.Start();

        method.IterateUntilStable(10000);

        method.growPolygons(5);
    }
}
