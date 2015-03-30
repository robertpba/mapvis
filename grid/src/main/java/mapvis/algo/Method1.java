package mapvis.algo;

import mapvis.common.datatype.Tree2;
import mapvis.layouts.commons.RandomHelper;
import mapvis.common.datatype.Tuple2;
import mapvis.models.Grid;
import mapvis.models.Pos;
import mapvis.models.Tile;

import java.util.*;
import java.util.stream.Collectors;

public class Method1<T> {
    public Tree2<T> tree;
    public CoastCache<T> cache;
    public Grid<T> grid;
    private Random random = new Random(1);
    public double beta = 3;

    public Method1(Tree2<T> tree, Grid<T> grid) {
        this.tree = tree;
        this.grid = grid;
        this.cache = new CoastCache<>(grid, tree);
    }

    public void Begin(){
        random = new Random(1);
        recursive(tree.getRoot());
    }

    public void recursive(T o){
        Set<T> children = tree.getChildren(o);

        if (children.size() > 0) {
            children.forEach(this::recursive);
            int level = tree.getDepth(o);

            //if (level == 1)
            //addPadding(o,5);


            return;
        }

        allocate(o);
    }

    private void addPadding(T o, int i) {
        while (i-->0) {
            List<Tile<T>> list = cache.getEdge(o).stream()
                    .flatMap(t -> grid.getNeighbours(t.getX(), t.getY()).stream())
                    .filter(t -> t.getItem() == null)
                    .distinct()
                    .collect(Collectors.toList());
            list.forEach(t -> {
                Tile<T> tile = new Tile<>(t.getPos(), o, Tile.SEA);
                grid.putTile(tile);
                cache.insert(tile.getX(), tile.getY(), o);
            });
        }

    }


    public void allocate(T o){
        ArrayList<Tile<T>> rollback = new ArrayList<>();
        int count = tree.getWeight(o);

        while (count-->0) {
            ArrayList<Tile<T>> prepare = new ArrayList<>();
            Pos pos = findStartPoint(o);
            Tile<T> tile = new Tile<>(pos, o);

            grid.putTile(tile);
            cache.insert(tile.getX(), tile.getY(), o);
            prepare.add(tile);

            while (count-- > 0) {
                pos = findNextPoint(o);
                if (pos == null)
                    break;

                tile = new Tile<>(pos, o);

                grid.putTile(tile);
                cache.insert(tile.getX(), tile.getY(), o);
                prepare.add(tile);
            }
            if (count > 0){
                rollback.addAll(prepare);
                prepare.clear();
            }
        }
        if (count > 0) {
            System.out.printf("Insufficient space: %s\n", o.toString());
        }
        for (Tile<T> tile : rollback) {
            Tile<T> empty = new Tile<>(tile.getPos());
            grid.putTile(empty);
            cache.remove(tile.getX(), tile.getY(), tile.getItem());
        }
    }

    public Pos findNextPoint(T o){
        Set<Tile<T>> waters = cache.getWaters(o);

        if (waters.size() == 0) return null;

        List<Tuple2<Integer, Tile<T>>> list = waters.stream().map(t -> new Tuple2<>(score(t), t))
                .sorted(Comparator.comparing(t -> t.first))
                .collect(Collectors.toList());

        Tile t  = RandomHelper.weightedRandom(list, random);

        return t.getPos();
    }
    public Pos findStartPoint(T o){
        List<T> nodes = tree.getPathToNode(o);
        Collections.reverse(nodes);

        for (T node : nodes) {
            Pos pos = findNextPoint(node);
            if (pos != null) return pos;
        }

        return new Pos(0, 0);
    }

    private int score(Tile<T> tile){
        Set<Tile<T>> neighbours = grid.getNeighbours(tile.getX(), tile.getY());
        long n = neighbours.stream().filter(t->t.getItem() != null)
                .count();
        return (int)Math.pow(beta, n);
    }
}
