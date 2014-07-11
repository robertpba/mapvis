package mapvis.algo;

import mapvis.grid.Dir;
import mapvis.grid.Grid;
import mapvis.grid.Tile;
import mapvis.tree.MPTT;

import java.util.*;

public class CoastCache<T> {

    Map<T, Set<Tile<T>>> m = new HashMap<>();

    public Grid<T> grid;
    public MPTT<T> tree;

    public CoastCache(Grid<T> grid, MPTT<T> tree){

        this.grid = grid;
        this.tree = tree;
    }

    public void insertAffect(int x, int y, T o){
        Tile<T> t = grid.getTile(x, y);
        this.recursivelyAffect(t);

        Set<Tile<T>> neighbours = grid.getNeighbours(t.getX(), t.getY());
        neighbours.forEach(this::recursivelyAffect);
    }

    public Set<Tile<T>> getCoast(T o){
        return Collections.unmodifiableSet(getCoastList(o));
    }


    private void recursivelyAffect(Tile<T> t) {
        if (t.getObj() == null)
            return;
        List<T> pathToNode = tree.getPathToNode(t.getObj());
        for (T t1 : pathToNode) {
            affect(t1, t);
        }
    }

    private void affect(T o, Tile<T> t){
        Set<Tile<T>> l = getCoastList(o);

        Set<Tile<T>> neighbours = grid.getNeighbours(t.getX(), t.getY());

        // surrounded by anything other than its decedents
        boolean isCoast = !neighbours.stream()
                .allMatch(n -> tree.isAncestorOf(o, n.getObj()));

        if (isCoast)
            l.add(t);
        else
            l.remove(t);
    }




    private Set<Tile<T>> getCoastList(T o){
        Set<Tile<T>> list = m.get(o);
        if (list==null)
            m.put(o, list = new HashSet<>());
        return list;
    }

}
