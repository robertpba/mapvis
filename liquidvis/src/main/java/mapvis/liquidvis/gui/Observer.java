package mapvis.liquidvis.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.gui.NavigableImagePanel.ZoomDevice;
import mapvis.vistools.colormap.ColorMap;
import mapvis.vistools.colormap.Colorbar;

public class Observer implements ActionListener {
    private final JPanel controls;
    private final JButton saveButton;
    private final JButton iterButton;
    private final JButton backButton;
    private final JButton forthButton;
    public BufferedImage image;
    public MapImageUpdater imageUpdater;
    public JFrame frame;
    public MapModel method;
    public Timer timer;

    public int refreshThreshold = 100;

    public Observer(BufferedImage image, MapModel method)
    {
        imageUpdater = new MapImageUpdater(method);

        this.timer = new Timer(refreshThreshold, this);
        this.image = image;
        this.method = method;
        this.frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NavigableImagePanel panel = new NavigableImagePanel();
        try {
            panel.setImage(image); 
            panel.setZoomDevice(ZoomDevice.MOUSE_BUTTON);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        controls = new JPanel();
        saveButton = new JButton("Save");
        controls.add(saveButton);
        frame.getContentPane().add(controls, BorderLayout.SOUTH);

        iterButton = new JButton("By iterations");
        controls.add(iterButton);

        backButton = new JButton("By times pushed back");
        controls.add(backButton);

        forthButton = new JButton("By times move forth");
        controls.add(forthButton);

        saveButton.addActionListener(e -> {
            Stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                save("output/"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".png");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Start();

        });

        iterButton.addActionListener(e ->
        {
            imageUpdater.mapPolygonFillingColor = (polygon)-> mapColor(0,2000, (int)(polygon.mass - polygon.area));
        });

        backButton.addActionListener(e ->
        {
            imageUpdater.mapPolygonFillingColor = (polygon) -> mapColor(0, 2000, polygon.moveBackCount);
        });

        forthButton.addActionListener(e ->
        {
            imageUpdater.mapPolygonFillingColor = (polygon) -> mapColor(0, 5000, polygon.moveForwardCount);
        });

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.getContentPane().add(new Colorbar(v -> ColorMap.JET.getColor(v) ), BorderLayout.NORTH);


        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();
        frame.setSize(new Dimension(bounds.width, bounds.height));
        frame.setVisible(true);
    }

    public void Start() {

        imageUpdater.updateImage(image);
        timer.start();
    }
    public void Stop()
    {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        imageUpdater.updateImage(image);
        frame.repaint();
    }

    public static Color mapColor(int minVal, int maxVal, int actual) {
        actual = Math.min(maxVal, actual);
        actual = Math.max(minVal, actual);
        double percentage = ((double)(maxVal - actual))/((double)(maxVal - minVal));
        int intR = 10;
        int intG = 10;
        int intB = 0+(int)(percentage*255);

        return new Color(intR, intG, intB);
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

        String extension = ".png"; // the default is current

        // create the file object
        File file = new File(filename);
        File fileLoc = file.getParentFile();

        // canWrite is true only when the file exists already! (alexr)
        if (!fileLoc.canWrite()) {
            // System.err.println("can't write the file but trying anyway? ...");
            throw new IOException(filename +
              " could not be opened. Check to see if you can write to the directory.");
        }

        // get the extension
        int posDot = filename.indexOf('.');
        if (posDot >= 0)
            extension = filename.substring(posDot + 1);

        // write the contents of the buffered image to the file as jpeg
        ImageIO.write(image, extension, file);

    }

}
