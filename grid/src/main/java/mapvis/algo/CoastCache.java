package mapvis.algo;

import mapvis.models.Grid;
import mapvis.models.Tile;
import mapvis.models.TreeModel;

import java.util.*;

public class CoastCache<T> {
    Map<T, Set<Tile<T>>> edge = new HashMap<>();
    Map<T, Set<Tile<T>>> waters = new HashMap<>();

    public Grid<T> grid;
    public TreeModel<T> tree;

    public CoastCache(Grid<T> grid, TreeModel<T> tree){

        this.grid = grid;
        this.tree = tree;
    }


    public void insert(int x, int y, T o){
        Tile<T> t = grid.getTile(x, y);

        List<T> pathToNode = tree.getPathToNode(t.getItem());

        Set<Tile<T>> neighbours = grid.getNeighbours(x, y);

        updateEdge(t);
        for (Tile<T> tile : neighbours) {
            if (tile.getItem() == null){
                addWaters(pathToNode, tile);
            } else {
                removeWaters(tile.getItem(), t);
                updateEdge(tile);
            }
        }
    }
    void removeWaters(T o, Tile<T> water){
        List<T> nodes = tree.getPathToNode(o);
        for (T node : nodes) {
            Set<Tile<T>> list = getWatersList(node);
            list.remove(water);
        }
    }
    void addWaters(List<T> nodes, Tile<T> water) {
        for (T node : nodes) {
            Set<Tile<T>> set = getWatersList(node);
            set.add(water);
        }
    }
    void updateEdge(Tile<T> t){
        List<T> nodes = tree.getPathToNode(t.getItem());
        Set<Tile<T>> neighbours = grid.getNeighbours(t.getX(), t.getY());

        for (T node : nodes) {
            boolean isEdge = !neighbours.stream()
                    .allMatch(n -> tree.isAncestorOf(node, n.getItem()));

            Set<Tile<T>> el = getEdgeList(node);

            if (isEdge)
                el.add(t);
            else
                el.remove(t);
        }
    }


    public void remove(int x, int y, T o){
        Tile<T> t = grid.getTile(x, y);

        Set<Tile<T>> neighbours = grid.getNeighbours(x, y);

        updateEdge(t);
        for (Tile<T> tile : neighbours) {
            if (tile.getItem() == null){
                removeWaters(o, tile);
            } else {
                addWaters(tree.getPathToNode(tile.getItem()), t);
                updateEdge(tile);
            }
        }
    }

    public Set<Tile<T>> getEdge(T o){
        return Collections.unmodifiableSet(getEdgeList(o));
    }
    public Set<Tile<T>> getWaters(T o){
        return Collections.unmodifiableSet(getWatersList(o));
    }

    private Set<Tile<T>> getEdgeList(T o){
        Set<Tile<T>> list = edge.get(o);
        if (list==null)
            edge.put(o, list = new HashSet<>());
        return list;
    }
    private Set<Tile<T>> getWatersList(T o){
        Set<Tile<T>> list = waters.get(o);
        if (list==null)
            waters.put(o, list = new HashSet<>());
        return list;
    }


}
