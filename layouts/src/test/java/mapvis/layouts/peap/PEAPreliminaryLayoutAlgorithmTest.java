package mapvis.layouts.peap;

import mapvis.common.datatype.Node;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.dom.GenericDOMImplementation;

import org.testng.annotations.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;

public class PEAPreliminaryLayoutAlgorithmTest {

    @Test
    public void testRun() throws Exception {
        Yaml yaml = new Yaml();
        FileInputStream ins = new FileInputStream("io/data/rand1.yaml");
        Node node = yaml.loadAs(ins, Node.class);
        PEAPreliminaryLayoutAlgorithm alg = new PEAPreliminaryLayoutAlgorithm(node);
        alg.run(1000);
        System.out.println(yaml.dump(node));


    }

    public static void main(String[] args) throws IOException {
        Yaml yaml = new Yaml();
        FileInputStream ins = new FileInputStream("io/data/rand1.yaml");
        Node node = yaml.loadAs(ins, Node.class);
        PEAPreliminaryLayoutAlgorithm alg = new PEAPreliminaryLayoutAlgorithm(node);
        alg.run(2000);


        //OvalPanelCanvas.start(node);
        OvalPanelCanvas.export(node);
    }



    public static class OvalPanelCanvas extends JPanel {
        Node root;

        public OvalPanelCanvas(Node root) {
            this.root = root;
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            recPaintNode((Graphics2D) g,root);
        }

        void recPaintNode(Graphics2D g, Node node) {
            double x = (double) node.getVal("x");
            double y = (double) node.getVal("y");
            double r = Math.sqrt((int)node.getVal("size") / Math.PI);
            int    ir = (int) r;

            //System.out.printf("%f,%f,%i");

            Color color = new Color((int)node.getVal("color"));
            g.setColor(color);
            //g.translate(,y);
            //new Ellipse2D.Double(x-r,y-r,2*r,2*r);
            g.draw(new Ellipse2D.Double(x-r,y-r,2*r,2*r));


            for (Node child : node.getChildren()) {
                recPaintNode(g, child);
            }
        }



        public static void start(Node root) {
            JFrame frame = new JFrame("Oval Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new OvalPanelCanvas(root));
            frame.setSize(1000, 1000);
            frame.setVisible(true);
        }

        public static void export(Node root) throws IOException {
            // Get a DOMImplementation
            DOMImplementation domImpl =
                    GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document
            Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

            // Ask the test to render into the SVG Graphics2D implementation
            OvalPanelCanvas canvas = new OvalPanelCanvas(root);
            canvas.recPaintNode(svgGenerator, root);

            // Finally, stream out SVG to the standard output using UTF-8
            // character to byte encoding
            boolean useCSS = true; // we want to use CSS style attribute


            File file = File.createTempFile("vis", ".svg");
            FileOutputStream os = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(os, "UTF-8");
            svgGenerator.stream(out, useCSS);

            Desktop.getDesktop().browse(file.toURI());
        }
    }

}