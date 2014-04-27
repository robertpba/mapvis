package mapvis.liquidvis;

import mapvis.liquidvis.gui.Observer;
import mapvis.liquidvis.method.method3.Method3;
import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Node;
import mapvis.liquidvis.model.handler.CollectStatistics;
import mapvis.liquidvis.util.PatrickFormatLoader;
import mapvis.vistools.colormap.ColorMap;
import mapvis.vistools.colormap.GenericColorMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

import static mapvis.vistools.Helper.interpolate;

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

        MapModel model = new MapModel(loader.root, n-> n.figure * 1.6);
        Method3 method = new Method3(model);

        CollectStatistics collectStatistics = new CollectStatistics(model, 100);
        model.listeners.add(collectStatistics);

        BufferedImage image = new BufferedImage(loader.width, loader.height, BufferedImage.TYPE_INT_RGB);

        Observer observer = new Observer(image, model);
//        observer.imageUpdater.mapPolygonFillingColor = c ->{
//            double v = interpolate(((c.mass - c.area)/c.mass), 0.0, 0.75, 1.0, 1.0);
//            return ColorMap.JET.getColor(v);
//        };

        int[] nlevels = {0,1,4,16,64,256,1024};
        String[] ncolors = {"#ffffff","#aae8ff", "#ffff33", "#ffcc00", "#ff9900", "#ff6600", "#cc3300", "#990000"};

        Color[] colors = new Color[ncolors.length];
        //float[] levels = new float[ncolors.length];
        for (int i = 0; i < ncolors.length; i++) {
            colors[i] = Color.decode(ncolors[i]);
            //levels[i] = (float) nlevels[i];
        }
        //GenericColorMap genericColorMap = new GenericColorMap(colors);

        observer.imageUpdater.mapPolygonFillingColor = c ->{
            double v = c.node.figure2;
            if (v <= nlevels[1] )
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
            else if (v <= nlevels[7])
                return colors[7];
            else
                return colors[7];
        };

        for (Node node : loader.nodes) {
            System.out.println(node.figure2);
        }


//        observer.imageUpdater.mapPolygonFillingColor = c ->{
//            double v = interpolate(((c.mass - c.area)/c.mass), 0.0, 0.5, 1.0, 1.0);
//            return ColorMap.JET.getColor(v);
//        };



        observer.Start();

        method.IterateUntilStable(100000);

        System.out.println("finished!");
//

        method.growPolygons();

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

        for (CollectStatistics.Statistics l : collectStatistics.ls) {
            System.out.printf("%d\t%f\n", l.iteration, l.error);
        }


    }
}

