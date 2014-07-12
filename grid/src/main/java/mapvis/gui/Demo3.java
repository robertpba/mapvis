package mapvis.gui;

import mapvis.grid.Grid;
import mapvis.grid.HashMapGrid;

import javax.swing.*;
import java.awt.*;

public class Demo3 {

    private static JFrame frame;

    public static void main (String[] args){


        Grid grid = new HashMapGrid();
        grid.put(1,1,1);
        grid.put(1,2,1);
        grid.put(1,3,1);
        grid.put(2,1,1);
        grid.put(2,2,1);
        grid.put(2,3,1);
        grid.put(3,1,1);
        grid.put(3,2,1);
        grid.put(3,3,1);
        //grid.put(1,4,1);
        //grid.put(1,6,1);

        GridRender gridRender = new GridRender(grid);

        GridPanel gridPanel = new GridPanel();
        gridPanel.grid = grid;


        frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);




        frame.getContentPane().add(gridPanel);

        frame.setSize(new Dimension(1000, 1000));
        frame.setVisible(true);
    }

}
