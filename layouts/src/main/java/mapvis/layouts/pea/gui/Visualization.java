package mapvis.layouts.pea.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.model.MapModel;
import mapvis.layouts.pea.gui.NavigableImagePanel.ZoomDevice;
import mapvis.utils.colormap.ColorBar;
import mapvis.utils.colormap.ColorMap;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class Visualization implements ActionListener {
    public final JButton iterButton;
    public final JButton backButton;
    public BufferedImage image;
    public JFrame frame;
    public MapModel model;
    public Timer timer;

    public int refreshThreshold = 1000;

    public Drawer drawer;

    public Visualization(BufferedImage image, MapModel model)
    {
        drawer = new Drawer(model);

        this.timer = new Timer(refreshThreshold, this);
        this.image = image;
        this.model = model;
        this.frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        NavigableImagePanel panel = new NavigableImagePanel();
        try {
            panel.setImage(image); 
            panel.setZoomDevice(ZoomDevice.MOUSE_BUTTON);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel controls = new JPanel();
        JButton saveButton = new JButton("Save");
        controls.add(saveButton);
        frame.getContentPane().add(controls, BorderLayout.SOUTH);

        iterButton = new JButton("border color");
        controls.add(iterButton);

        backButton = new JButton("thickness");
        controls.add(backButton);

        JButton forthButton = new JButton("By times move forth");
        controls.add(forthButton);



        saveButton.addActionListener(e -> {
            Stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                save("output/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Start();

        });


        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.getContentPane().add(new ColorBar(ColorMap.JET::getColor), BorderLayout.NORTH);


        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();
        frame.setSize(new Dimension(bounds.width, bounds.height));
        frame.setVisible(true);
    }

    public void Start() {
        draw();
        timer.start();
    }
    public void Stop()
    {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        draw();
        frame.repaint();
    }

    public Color backgroundColor = Color.white;

    public void draw(){
        Graphics2D g = image.createGraphics();
        g.setBackground(backgroundColor);
        g.clearRect(0,0, image.getWidth(), image.getHeight());

        drawer.draw(g);
    }

    public void save(String filename) throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Stop();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        export();
    }


    public void export() throws IOException {
        // Get a DOMImplementation
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        drawer.draw(svgGenerator);

        boolean useCSS = true;

        File file = File.createTempFile("vis", ".svg");
        FileOutputStream os = new FileOutputStream(file);
        Writer out = new OutputStreamWriter(os, "UTF-8");
        svgGenerator.stream(out, useCSS);

        Desktop.getDesktop().browse(file.toURI());
    }

}
