package mapvis.liquidvis;

import mapvis.liquidvis.gui.Observer;
import mapvis.liquidvis.method.method2.Method2;
import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Node;
import mapvis.liquidvis.model.handler.CollectStatistics;
import mapvis.liquidvis.util.LegacySeparateTextFileTreeLoader;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class DemoMethod2 {
    
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
        Method2 method = new Method2(model);

        model.listeners.add(new CollectStatistics(model, 10));


        BufferedImage image = new BufferedImage(loader.width, loader.height+500, BufferedImage.TYPE_INT_RGB);
        
        Observer observer = new Observer(image, model);
//        observer.imageUpdater.mapPolygonFillingColor = c ->
//            colormap((float)interpolate(c.mass - c.area, 0.0, 0.0, 2000.0, 1.0) );
        observer.Start();


        method.IterateUntilStable(10000);

        System.gc();

        //observer.save("output/save.png");
    }
}

