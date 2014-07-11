package mapvis.algo;

import mapvis.grid.Grid;
import mapvis.grid.Tile;
import mapvis.tree.MPTT;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Method1<T> {
    public MPTT<T> tree;
    public CoastCache<T> cache;
    public Grid<T> grid;

    public Method1(MPTT<T> tree, CoastCache<T> cache, Grid<T> grid) {
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
            for (T child : children) {
                recursive(child);
            }
            return;
        }

        int count = tree.getWeight(o);
        while (count-->0) {
            Tile<T> tile = nextAvailablePlace(o);

            grid.put(tile.getX(), tile.getY(), o);
            cache.insertAffect(tile.getX(), tile.getY(), o);
        }
    }



    public Tile<T> nextAvailablePlace(T o){
        List<T> nodes = tree.getPathToNode(o);
        Collections.reverse(nodes);

        Tile<T> tile = null;

        for (int i = 0; i < nodes.size(); i++) {
            o = nodes.get(i);
            Set<Tile<T>> coast = cache.getCoast(o);
            if (coast.size() == 0)
                continue;
            Object[] array = coast.toArray();
            int size = coast.size();
            int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this

            tile = (Tile<T>)array[item];
            Set<Tile<T>> neighbours = grid.getNeighbours(tile.getX(), tile.getY());
            tile = neighbours.stream().filter(t -> t.getObj() == null).findFirst().get();
        }
        if (tile == null)
            return new Tile<>(0,0,null);
        return tile;
    }







}
