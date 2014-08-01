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

            List<Tuple2<Integer, Tile<T>>> list = waters.stream().map(t -> new Tuple2<>(score(t), t))
                    .sorted(Comparator.comparing(t -> t.first))
                    .collect(Collectors.toList());

            tile = weightedRandom(list);
            break;
        }
        if (tile == null) {
            return new Tile<>(0, 0, null);
        }
        return tile;
    }

    private Tile<T> weightedRandom(List<Tuple2<Integer, Tile<T>>> list){
        int sum_of_weight = 0;
        for (Tuple2<Integer, Tile<T>> tuple2 : list) {
            sum_of_weight += tuple2.first;
        }
        int rnd = random.nextInt(sum_of_weight);
        for (Tuple2<Integer, Tile<T>> tuple2 : list) {
            if(rnd < tuple2.first)
                return tuple2.second;
            rnd -= tuple2.first;
        }
        return null;
    }


    private int score(Tile<T> tile){
        Set<Tile<T>> neighbours = grid.getNeighbours(tile.getX(), tile.getY());
        long n = neighbours.stream().filter(t->t.getObj() != null)
                .count();
        return (int)Math.pow(6, n);
    }

    static class Tuple2<T1, T2>{
        public T1 first;
        public T2 second;

        Tuple2(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple2 tuple2 = (Tuple2) o;

            if (first != null ? !first.equals(tuple2.first) : tuple2.first != null) return false;
            if (second != null ? !second.equals(tuple2.second) : tuple2.second != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = first != null ? first.hashCode() : 0;
            result = 31 * result + (second != null ? second.hashCode() : 0);
            return result;
        }
    }






}
