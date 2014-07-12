package mapvis.gui;

import mapvis.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class GridPanel extends JPanel {
    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double SideLength = 10;

    static Path2D.Double hexagon; {
        hexagon = new Path2D.Double();
        //    0 - 1
        //  5   c   2
        //    4 - 3
        hexagon.moveTo(-SideLength/2, -SideLength*COS30);
        hexagon.lineTo( SideLength/2, -SideLength*COS30);
        hexagon.lineTo( SideLength  , 0);
        hexagon.lineTo( SideLength/2,  SideLength*COS30);
        hexagon.lineTo(-SideLength/2,  SideLength*COS30);
        hexagon.lineTo(-SideLength  , 0);
        hexagon.closePath();
    }

    public Grid grid;

    public Point2D toCenter(int x, int y){
        double cx = x * 3 * SideLength / 2;
        double cy;
        cy = 2 * COS30 * SideLength * y;

        if (x % 2 != 0) {
            cy += COS30 * SideLength;
        }

        return new Point2D.Double(cx, cy);
    }

    public double zoom = 1.0;
    public double originX = 0.0;
    public double originY = 0.0;

    public GridPanel(){
        ButtonZoomDevice zoomDevice = new ButtonZoomDevice();
        addMouseMotionListener(zoomDevice);
        addMouseListener(zoomDevice);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.black);

        g2d.drawString(String.format("zoom:%f,center:%f,%f", zoom, originX, originY), 0, getHeight()-20);

        g2d.translate(getWidth()/2, getHeight()/2);
        g2d.translate(originX, originY);
        g2d.scale(zoom, zoom);

        for (int i = -100; i < 100; i++) {
            for (int j = -100; j < 100; j++) {
                Object o = grid.get(i, j);
                if (o!=null) {
                    Point2D point2D = toCenter(i, j);

                    AffineTransform save = g2d.getTransform();
                    g2d.translate(point2D.getX(), point2D.getY());
                    g2d.draw(hexagon);
                    //g2d.drawString(String.format("%d,%d",i,j), 0,0);
                    g2d.setTransform(save);
                }
            }
        }
    }


    private class ButtonZoomDevice extends MouseAdapter {
        int startX = -1, startY = -1;
        int lastX = -1, lastY = -1;
        int currX = -1, currY = -1;

        boolean inDrag;

        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();

            startX = p.x;
            startY = p.y;

            lastX = p.x;
            lastY = p.y;

            inDrag = true;
        }
        public void mouseReleased(MouseEvent e) {
            inDrag = false;
        }

        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            System.err.println("mouse Dragged from " + lastX+","+lastY +" to " + p);

            double dx = (lastX - p.x) / zoom;
            double dy = (lastY - p.y) / zoom;

            if (SwingUtilities.isLeftMouseButton(e)) {
                originX = originX - (lastX - p.x);// / zoom;
                originY = originY - (lastY - p.y);//;; / zoom;
            } else if (SwingUtilities.isRightMouseButton(e)) {
                zoom += dx/getWidth()*zoom  + dy/getHeight()*zoom;

                zoom = Math.max(0.001, zoom);
            }

            lastX = p.x;
            lastY = p.y;
            if (inDrag) {
                repaint();
            }
        }
    }
}
