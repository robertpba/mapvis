package mapvis.gui;

import mapvis.RandomData;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.Grid;
import mapvis.grid.HashMapGrid;
import mapvis.tree.MPTT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Demo5 {
    static MPTT<Integer> tree;
    static CoastCache<Integer> cache;
    static Grid<Integer> grid;
    static JFrame frame;

    public static void main (String[] args){
        tree = RandomData.getTree();

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
        Method1<Integer> method1 = new Method1<>(tree, cache, grid);

        Set<Integer> leaves = tree.getLeaves();
        Map<Integer, Color> map = new HashMap();
        Random rand = new Random(1);
        System.out.printf("%d leaves\n", leaves.size());
        for (Integer leaf : leaves) {
            map.put(leaf, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        }

        GridPanel gridPanel = new GridPanel(){
            @Override
            protected void renderCell(Graphics2D g2d, int i, int j, Object o) {
                super.renderCell(g2d, i, j, o);
                //hexagon
                g2d.setColor(map.get(o));
                g2d.fill(hexagon);
                g2d.drawString(o.toString(), 0,0);
            }
        };
        gridPanel.grid = grid;
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point point = e.getPoint();
                Point coord = gridPanel.screenToGridCoordinate(point.x, point.y);
                Integer str = grid.get(coord.x, coord.y);
                System.out.printf("id:%s, weight:%d\n", str, tree.getWeight(str));

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
