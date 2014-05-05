//package mapvis.liquidvis;
//
//import mapvis.liquidvis.gui.Observer;
//import mapvis.liquidvis.method.method2.Method2;
//import mapvis.liquidvis.model.MapModel;
//import mapvis.liquidvis.model.handler.CollectStatistics;
//import mapvis.liquidvis.util.PatrickFormatLoader;
//import mapvis.vistools.colormap.ColorMap;
//
//import java.awt.image.BufferedImage;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import static mapvis.vistools.Helper.interpolate;
//
//public class DemoMethod2_5 {
//
//    public static void main (String[] args) throws IOException, InterruptedException {
//        PatrickFormatLoader loader = new PatrickFormatLoader();
//        try {
//
//            loader.load(
//                    "data/simple.txt",
//                    "data/points.txt");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
////
////        Node geography = Arrays.stream(loader.root.children)
////                .filter(n -> n.name.contains("Geography"))
////                .findFirst().get();
////
////        Node byCountry = Arrays.stream(geography.children)
////                .filter(n -> n.name.contains("Geography by country"))
////                .findFirst().get();
//
//        //geography.children = new Node[] { byCountry };
//
//        //loader.root.children = new Node[]{ geography};
//
//
//        //loader.height += 500;
//        //loader.width += 500;
//        MapModel model = new MapModel(loader.root, n-> n.figure);
//        Method2 method = new Method2(model);
//
//        model.listeners.add(new CollectStatistics(model, 10));
//
//
//        BufferedImage image = new BufferedImage(loader.width, loader.height, BufferedImage.TYPE_INT_RGB);
//
//        Observer observer = new Observer(image, model);
//        observer.imageUpdater.mapPolygonFillingColor = c ->{
//            double v = interpolate(c.mass - c.area, 0.0, 0.0, 10000.0, 1.0);
//            return ColorMap.JET.getColor(v);
//        };
//
////        observer.imageUpdater.mapPolygonFillingColor = c ->
////            colormap((float)interpolate(c.mass - c.area, 0.0, 0.0, 2000.0, 1.0) );
//        observer.Start();
//
//
//        method.IterateUntilStable(10000);
//
//        System.gc();
//
//        //observer.save("output/save.png");
//    }
//}
//
