package mapvis.liquidvis;

import mapvis.liquidvis.gui.Observer;
import mapvis.liquidvis.method.method2.Method2;
import mapvis.liquidvis.method.method3.Method3;
import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Node;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.handler.CollectStatistics;
import mapvis.liquidvis.util.PatrickFormatLoader;
import mapvis.vistools.colormap.ColorMap;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import static mapvis.vistools.colormap.ColormapPackage.interpolate;

public class DemoMethod3_5 {
    
    public static void main (String[] args) throws IOException, InterruptedException {
        PatrickFormatLoader loader = new PatrickFormatLoader();
        try {

            loader.load(
                    "data/simple.txt",
                    "data/points.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//
//        Node geography = Arrays.stream(loader.root.children)
//                .filter(n -> n.name.contains("Geography"))
//                .findFirst().get();
//
//        Node byCountry = Arrays.stream(geography.children)
//                .filter(n -> n.name.contains("Geography by country"))
//                .findFirst().get();

        //geography.children = new Node[] { byCountry };

        //loader.root.children = new Node[]{ geography};

        MapModel model = new MapModel(loader.root, n-> n.figure * 1.5);
        Method3 method = new Method3(model);
        model.listeners.add(new CollectStatistics(model, 100));

        BufferedImage image = new BufferedImage(loader.width, loader.height, BufferedImage.TYPE_INT_RGB);

        Observer observer = new Observer(image, model);
//        observer.imageUpdater.mapPolygonFillingColor = c ->{
//            double v = interpolate(((c.mass - c.area)/c.mass), 0.0, 0.75, 1.0, 1.0);
//            return ColorMap.JET.getColor(v);
//        };
        observer.imageUpdater.mapPolygonFillingColor = c ->{
            double v = interpolate(((c.mass - c.area)/c.mass), 0.0, 0.5, 1.0, 1.0);
            return ColorMap.JET.getColor(v);
        };

        observer.Start();

        method.IterateUntilStable(100000);

        System.out.println("finished!");
//
//        model.getPolygons().values().stream()
//                .forEach(p -> {
//                    p.moveBackCount = 0;
//                    p.moveForwardCount = 0;
//                });
//
//        Node first = loader.nodes.stream()
//                .filter(n -> n.name.contains("Belgium"))
//                .filter(n -> n.name.contains("Geography"))
//                .findFirst().get();

        //Polygon polygon = model.getPolygons().get(first);
        //polygon.figure += 30;
        //polygon.mass   += 30 * polygon.scale.apply(polygon.node);
        //method.IterateUntilStable(100000);

        System.gc();
    }
}

