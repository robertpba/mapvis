package mapvis.algo;

import mapvis.grid.Grid;
import mapvis.grid.Tile;
import mapvis.Impl.TreeModel;

import java.util.*;
import java.util.stream.Collectors;

public class Method1<T> {
    public TreeModel<T> tree;
    public CoastCache<T> cache;
    public Grid<T> grid;
    private Random random = new Random(1);

    public Method1(TreeModel<T> tree, CoastCache<T> cache, Grid<T> grid) {
        this.tree = tree;
        this.cache = cache;
        this.grid = grid;
    }

    public void Begin(){
        recursive(tree.getRoot());
    }

    public void recursive(T o){
        Set<T> children = tree.getChildren(o);
        if (children.size() > 0) {
            children.forEach(this::recursive);
            return;
        }

        int count = tree.getWeight(o);
        while (count-->0) {
            Tile<T> tile = nextAvailablePlace(o);

            grid.put(tile.getX(), tile.getY(), o);
            cache.insert(tile.getX(), tile.getY(), o);
        }
    }



    public Tile<T> nextAvailablePlace(T o){
        List<T> nodes = tree.getPathToNode(o);
        Collections.reverse(nodes);

        Tile<T> tile = null;

        for (T node : nodes) {
            o = node;
            Set<Tile<T>> waters = cache.getWaters(o);



            if (waters.size() == 0) {
                if (cache.getEdge(o).size() > 0)
                    System.out.printf("Insufficient space: %s\n", o.toString());
                continue;
            }
            @SuppressWarnings("unchecked")
            Tile<T>[] array = waters.toArray(new Tile[waters.size()]);
            int size = waters.size();

            int item = random.nextInt(size); // In real life, the Random object should be rather more shared than this
            tile = array[item];
            break;
        }
        if (tile == null) {
            return new Tile<>(0, 0, null);
        }
        return tile;
    }










}
