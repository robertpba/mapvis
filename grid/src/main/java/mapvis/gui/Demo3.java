package mapvis.gui;

import mapvis.grid.Grid;
import mapvis.grid.HashMapGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Demo3 {

    private static JFrame frame;

    public static void main (String[] args){


        Grid<String> grid = new HashMapGrid<>();
        grid.put(1,1,"#1,1#");
        grid.put(1,2,"#1,2#");
        grid.put(1,3,"#1,3#");
        grid.put(2,1,"#2,1#");
        grid.put(2,2,"#2,2#");
        grid.put(2,3,"#2,3#");
        grid.put(3,1,"#3,1#");
        grid.put(3,2,"#3,2#");
        grid.put(3,3,"#3,3#");
        //grid.put(1,4,1);
        //grid.put(1,6,1);

        GridRender gridRender = new GridRender(grid);

        GridPanel gridPanel = new GridPanel();
        gridPanel.grid = grid;
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point point = e.getPoint();
                Point coord = gridPanel.screenToGridCoordinate(point.x, point.y);
                String str = grid.get(coord.x, coord.y);
                System.out.println(str);

            }
        });


        frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);




        frame.getContentPane().add(gridPanel);

        frame.setSize(new Dimension(1000, 1000));
        frame.setVisible(true);
    }

}
