package mapvis.liquidvis;
import mapvis.liquidvis.method.method3.Method3;
import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.handler.CollectStatistics;
import mapvis.liquidvis.util.LegacySeparateTextFileTreeLoader;
import mapvis.liquidvis.gui.Observer;
import mapvis.vistools.colormap.ColorMap;

import java.io.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static mapvis.vistools.Helper.interpolate;

public class DemoMethod3 {
    
    public static void main (String[] args) throws IOException, InterruptedException {
        LegacySeparateTextFileTreeLoader loader = new LegacySeparateTextFileTreeLoader();
        try {
            loader.load(
                    "data/finalPointInfo.txt",
                    "data/edgeInfo.txt",
                    "data/categoryName.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Node geography = Arrays.stream(loader.root.children)
                .filter(n -> n.name.contains("Geography"))
                .findFirst().get();

        Node byCountry = Arrays.stream(geography.children)
                .filter(n -> n.name.contains("Geography by country"))
                .findFirst().get();

        geography.children = new Node[] { byCountry };

        loader.root.children = new Node[]{ geography};


        MapModel model = new MapModel(loader.root);
        Method3 method = new Method3(model);
        model.listeners.add(new CollectStatistics(model, 1000));

        BufferedImage image = new BufferedImage(loader.width, loader.height+500, BufferedImage.TYPE_INT_RGB);
        
        Observer observer = new Observer(image, model);
        observer.imageUpdater.mapPolygonFillingColor = c ->{
            double v = interpolate(c.mass - c.area, 0.0, 0.0, 10000.0, 1.0);
            return ColorMap.JET.getColor(v);
        };

        observer.Start();

        method.IterateUntilStable(10000);

        System.out.println("finished!");

        model.getPolygons().values().stream()
                .forEach(p -> {
                    p.moveBackCount = 0;
                    p.moveForwardCount = 0;
                });

        Node first = loader.nodes.stream()
                .filter(n -> n.name.contains("Belgium"))
                .filter(n -> n.name.contains("Geography"))
                .findFirst().get();

        Polygon polygon = model.getPolygons().get(first);
        polygon.figure += 30;
        //polygon.mass   += 30 * polygon.scale.apply(polygon.node);
        method.IterateUntilStable(100000);

        System.gc();

        //observer.save("output/save.png");
    }
}

