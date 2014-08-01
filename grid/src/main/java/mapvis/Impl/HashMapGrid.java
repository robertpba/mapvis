package mapvis.Impl;

import mapvis.models.Dir;
import mapvis.models.Grid;
import mapvis.models.Pos;
import mapvis.models.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class HashMapGrid<T> implements Grid<T> {

    Map<Pos, T> map = new HashMap<>();

    @Override
    public void put(int x, int y, T obj) {
        map.put(new Pos(x, y), obj);
    }

    @Override
    public T get(int x, int y) {
        return map.get(new Pos(x, y));
    }

    @Override
    public Tile<T> getTile(int x, int y) {
        return new Tile<>(x, y, map.get(new Pos(x, y)));
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

        return new Tile<>(nx, ny, map.get(new Pos(nx, ny)));
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
        for (Map.Entry<Pos, T> entry : map.entrySet()) {
            if (entry.getValue() != null)
                consumer.accept(new Tile<>(entry.getKey().getX(),
                        entry.getKey().getY(),
                        entry.getValue()));
        }
    }

}
