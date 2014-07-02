package mapvis.gui;

import mapvis.grid.Grid;

import java.awt.*;
import java.awt.geom.Point2D;

public class GridRender {
    public GridRender(Grid grid) {
        this.grid = grid;
    }

    Grid grid;

    public void draw(Graphics2D g2d){

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                Object o = grid.get(i, j);
                if (o!=null) {
                    Point2D point2D = toCenter(i, j);
                    drawHex(g2d, (point2D.getX() +100),(point2D.getY() + 100));
                }
            }
        }
    }


    public static final double SIN60 = Math.sin(Math.toRadians(60));
    public static final double SideLength = 20;

    public Point2D toCenter(int x, int y){
        double cx = x * SIN60 * SideLength;
        double cy;

        if (x % 2 == 0) {
            cy = y * SideLength;
        }else {
            cy = 2 * SIN60 * SideLength * y;
        }

        return new Point2D.Double(cx, cy);
    }

    public void drawHex(Graphics2D g, double centerX, double centerY){
        //    0 - 1
        //  5   c   2
        //    4 - 3

        int pointX[] = new int[6];
        int pointY[] = new int[6];

        pointX[0] = (int)(centerX - SideLength/2);
        pointX[1] = (int)(centerX + SideLength/2);
        pointX[2] = (int)(centerX + SideLength);
        pointX[3] = (int)(centerX + SideLength/2);
        pointX[4] = (int)(centerX - SideLength/2);
        pointX[5] = (int)(centerX - SideLength);

        pointY[0] = (int)(centerY - SideLength*SIN60);
        pointY[1] = (int)(centerY - SideLength*SIN60);
        pointY[2] = (int)(centerY);
        pointY[3] = (int)(centerY + SideLength*SIN60);
        pointY[4] = (int)(centerY + SideLength*SIN60);
        pointY[5] = (int)(centerY);

        g.drawPolygon(pointX, pointY, 6);

        g.drawRect((int)centerX,(int)centerY,1,1);
    }


}
