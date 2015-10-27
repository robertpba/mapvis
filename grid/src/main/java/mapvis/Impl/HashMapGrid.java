package mapvis.Impl;

import mapvis.models.Dir;
import mapvis.models.Grid;
import mapvis.models.Pos;
import mapvis.models.Tile;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HashMapGrid<T> implements Grid<T> {

    public HashMapGrid() {
//        System.out.println("Creating: " + this.getClass().getName());
    }

    Map<Pos, Tile<T>> map = new HashMap<>();

    int minX;
    int minY;
    int maxX;
    int maxY;

    @Override
    public int getMinY() {
        return minY;
    }

    @Override
    public void resetGrid() {
        map.clear();
    }

    @Override
    public int getMinX() {
        return minX;
    }
    @Override
    public int getMaxX(){
        return maxX;
    }
    @Override
    public int getMaxY(){
        return maxY;
    }

    @Override
    public void putTile(Tile<T> tile) {
        map.put(tile.getPos(), tile);
        minX = Math.min(minX, tile.getX());
        maxX = Math.max(maxX, tile.getX());
        minY = Math.min(minY, tile.getY());
        maxY = Math.max(maxY, tile.getY());
    }


    @Override
    public Tile<T> getTile(Pos pos) {
        Tile<T> t = map.get(pos);
        if (t == null) return new Tile<>(pos);
        else return t;
    }

    @Override
    public Tile<T> getNeighbour(int x, int y, Dir dir) {
        int nx, ny;

        //  0,0         2,0
        //        1,0
        //  0,1         2,1
        //        1,1

        if (x % 2 != 0) {
            switch (dir) {
                case S:     nx = x;	    ny = y + 1;	break;
                case SW:    nx = x - 1;	ny = y + 1; break;
                case NW:    nx = x - 1;	ny = y;	    break;
                case N:	    nx = x;	    ny = y - 1; break;
                case NE:    nx = x + 1;	ny = y;	    break;
                case SE:    nx = x + 1;	ny = y + 1; break;
                default:
                    throw new IllegalArgumentException("Direction is incorrect.");
            }
        } else {
            switch (dir) {
                case S:     nx = x;     ny = y + 1;	break;
                case SW:    nx = x - 1; ny = y;     break;
                case NW:    nx = x - 1; ny = y - 1;	break;
                case N:     nx = x;     ny = y - 1; break;
                case NE:    nx = x + 1;	ny = y - 1;	break;
                case SE:    nx = x + 1;	ny = y;     break;
                default:
                    throw new IllegalArgumentException("Direction is incorrect.");
            }
        }

        return getTile(nx, ny);
    }

    public List<Dir> getNeighborDirectionsFulfilling(Predicate<Tile<T>> typeTester, int x, int y){
        return Arrays.asList(Dir.values()).
                stream().filter(
                dir -> typeTester.test(getNeighbour(x, y, dir))
        ).collect(Collectors.toList());
    }

    @Override
    public Set<Tile<T>> getNeighbours(int x, int y) {
        Tile<T> n = getNeighbour(x, y, Dir.N);
        Tile<T> ne = getNeighbour(x, y, Dir.NE);
        Tile<T> nw = getNeighbour(x, y, Dir.NW);
        Tile<T> s = getNeighbour(x, y, Dir.S);
        Tile<T> sw = getNeighbour(x, y, Dir.SW);
        Tile<T> se = getNeighbour(x, y, Dir.SE);
        HashSet<Tile<T>> set = new HashSet<>();
        set.add(n);
        set.add(ne);
        set.add(nw);
        set.add(s);
        set.add(sw);
        set.add(se);
        return set;
    }

    @Override
    public void foreach(Consumer<Tile<T>> consumer) {
        for (Map.Entry<Pos, Tile<T>> entry : map.entrySet()) {
            if (entry.getValue() != null)
                consumer.accept(entry.getValue());
        }
    }

    @Override
    public Collection<Tile<T>> allTiles(){
        return map.values();
    }
}
