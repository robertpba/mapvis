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
import mapvis.vistools.colormap.ColorBar;
import mapvis.vistools.colormap.ColorMap;

public class Visualization implements ActionListener {
    public BufferedImage image;
    public JFrame frame;
    public MapModel model;
    public Timer timer;

    public int refreshThreshold = 1000;


    public Visualization(BufferedImage image, MapModel model)
    {

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

        JButton iterButton = new JButton("By iterations");
        controls.add(iterButton);

        JButton backButton = new JButton("By times pushed back");
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
        model.draw(g);
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
