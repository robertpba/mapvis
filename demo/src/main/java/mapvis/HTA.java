package mapvis;


import mapvis.common.datatype.Node;
import mapvis.io.random.Config;
import mapvis.io.random.RandomNodeGen;
import mapvis.io.random.YamlCfgReader;
import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;
import mapwiki.layout.HexagonLayout2;
import mapwiki.layout.LayoutGraphic;
import mapwiki.layout.categoryprovider.NodeCategoryProvider;
import mapwiki.layout.hexagon.HexagonCanvas;
import mapwiki.layout.mapdrawing.NodeMapDrawer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HTA {



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        LayoutGraphic.props.load(new FileInputStream("patrick/data/defaults.properties"));

        YamlCfgReader reader = new YamlCfgReader();
        Config config = reader.read("demo/data/ehta_cfg_01.yaml");

        System.out.print( new YamlCfgReader().dump(config));

        RandomNodeGen generator = new RandomNodeGen();
        Node root = generator.getTree(config);
        NodeCategoryProvider pvdr = new NodeCategoryProvider(root);

        HexagonLayout2.LayoutConfig layoutcfg = new HexagonLayout2.LayoutConfig();
        layoutcfg.affectRange = 12;
        layoutcfg.areaSizeCityVisible = 50;
        layoutcfg.minCategorySize = 1;
        //layoutcfg.counters

        layoutcfg.layoutRectMinSize = 2;
        layoutcfg.layoutRectFixedSize = -1;
        layoutcfg.maxSizeForLevels = new int[]{Integer.MAX_VALUE, 10000,10000,10000,10000,10000};

        layoutcfg.startLevel = 2;

        LayoutGraphic.layoutDepth = 2;

        HexagonLayout2 hexagonLayout2 = new HexagonLayout2(pvdr, layoutcfg);
        hexagonLayout2.run();

        NodeMapDrawer drawer = new NodeMapDrawer(6.0, pvdr);

        File file = Files.createTempFile("hta", ".png").toFile();

        HexagonCanvas canvas = hexagonLayout2.hexCanvas;
        Dimension realImageSize = drawer.computeImageSize(canvas);
        System.err.printf("Creating a canvas with %dx%d (Real output size: %dx%d).%n",
                canvas.getWidth(), canvas.getHeight(),
                realImageSize.width, realImageSize.height);
        BufferedImage img = new BufferedImage(realImageSize.width, realImageSize.height,
                BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g = img.createGraphics();

        try {
            g.setBackground(Color.white);
            g.clearRect(0, 0, img.getWidth(), img.getHeight());

            // Perform the real drawing.
            System.err.println("Drawing on the canvas...");
            drawer.draw(canvas, g);

            long imageOutputStartTime = System.currentTimeMillis();
            System.err.println("Writing image...");
            ImageIO.write(img, "png", file);
            System.err.printf("Done. Image output duration: %,dms%n",
                    System.currentTimeMillis() - imageOutputStartTime);
        } finally {
            g.dispose();
        }

        System.out.println(file.getAbsolutePath());


        //layoutDepth = (Integer)options.valueOf("l");

    }
}
