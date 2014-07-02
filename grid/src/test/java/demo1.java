import mapvis.gui.NavigableImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class demo1 implements ActionListener {
    public BufferedImage image;
    public JFrame frame;
    public Timer timer;

    public int refreshThreshold = 1000;

    public static void main () {
        demo1 demo1 = new demo1();
    }

    public demo1(){
        this.timer = new Timer(refreshThreshold, this);
        this.frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        NavigableImagePanel panel = new NavigableImagePanel();
        try {
            panel.setImage(image);
            panel.setZoomDevice(NavigableImagePanel.ZoomDevice.MOUSE_BUTTON);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel controls = new JPanel();
        frame.getContentPane().add(controls, BorderLayout.SOUTH);
        frame.getContentPane().add(panel, BorderLayout.CENTER);

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

    public void Stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        draw();
        frame.repaint();
    }

    public Color backgroundColor = Color.white;

    public void draw() {
        Graphics2D g = image.createGraphics();
        g.setBackground(backgroundColor);
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
    }
}