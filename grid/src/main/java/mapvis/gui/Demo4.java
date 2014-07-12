package mapvis.gui;

import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.Grid;
import mapvis.grid.HashMapGrid;
import mapvis.tree.MPTT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Demo4 {
    static MPTT<Color> tree;
    static CoastCache<Color> cache;
    static Grid<Color> grid;
    static JFrame frame;

    public static void main (String[] args){
        tree = new MPTT<>();
        tree.setRoot(Color.RED);
        tree.addChild(Color.RED, Color.GREEN, 10);
        tree.addChild(Color.RED, Color.BLUE, 10);
        tree.addChild(Color.GREEN, Color.YELLOW, 10);

        //       R
        //     /  \
        //    G    B
        //    |
        //    Y

        tree.refresh();

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
        Method1<Color> method1 = new Method1<>(tree, cache, grid);

        GridPanel gridPanel = new GridPanel(){
            @Override
            protected void renderCell(Graphics2D g2d, int i, int j, Object o) {
                super.renderCell(g2d, i, j, o);
                //hexagon
                g2d.setColor((Color)o);
                g2d.fill(hexagon);
            }
        };
        gridPanel.grid = grid;
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point point = e.getPoint();
                Point coord = gridPanel.screenToGridCoordinate(point.x, point.y);
                Color str = grid.get(coord.x, coord.y);
                System.out.println(str);

            }
        });


        frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);




        frame.getContentPane().add(gridPanel);

        frame.setSize(new Dimension(1000, 1000));
        frame.setVisible(true);

        method1.Begin();

    }

}
