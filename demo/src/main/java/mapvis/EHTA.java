package mapvis;


import mapvis.Impl.HashMapGrid;
import mapvis.algo.Method1;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.io.random.Config;
import mapvis.io.random.RandomNodeGen;
import mapvis.io.random.YamlCfgReader;
import mapvis.layouts.Dac;
import mapvis.layouts.Pea;
import mapvis.layouts.pea.gui.Visualization;
import mapvis.layouts.pea.model.MapModel;
import mapvis.layouts.pea.model.handler.CollectStatistics;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class EHTA {


    public static void run(String file) throws IOException {
        YamlCfgReader reader = new YamlCfgReader();
        Config config = reader.read(file);

        System.out.print( new YamlCfgReader().dump(config));

        RandomNodeGen generator = new RandomNodeGen();
        Node root = generator.getTree(config);
        
        MPTreeImp<Node> treemodel = MPTreeImp.from(root);

        HashMapGrid<Node> grid = new HashMapGrid<>();
        Method1<Node> method1 = new Method1<>(treemodel, grid);

        method1.Begin();

        Drawer drawer = new Drawer(grid, treemodel);
        drawer.export();

    }

    public static void main(String[] args) throws IOException {
        //run("demo/data/pea_cfg_01.yaml");
        run("demo/data/ehta_cfg_01.yaml");
    }

}
