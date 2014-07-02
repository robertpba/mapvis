package mapvis.grid;

import java.util.HashMap;
import java.util.Map;

public class HashMapGrid implements Grid {

    Map map = new HashMap();

    @Override
    public void put(int x, int y, Object obj) {
        map.put(new Pos(x, y), obj);
    }

    @Override
    public Object get(int x, int y) {
        return map.get(new Pos(x, y));
    }

    @Override
    public Tile getNeighbour(int x, int y, Dir dir) {
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

        return new Tile(nx, ny, map.get(new Pos(nx, ny)));
    }
}
