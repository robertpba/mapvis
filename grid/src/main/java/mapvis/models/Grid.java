package mapvis.models;

import java.util.Set;
import java.util.function.Consumer;

public interface Grid<T> {
    //void put(int x, int y, T obj);
    T get(int x, int y);
    Tile<T> getTile(int x, int y);
    Tile<T> getNeighbour(int x, int y, Dir dir);
    Set<Tile<T>> getNeighbours(int x, int y);



    void foreach(Consumer<Tile<T>> consumer);


    void putTile(Tile<T> tile);
//    Tile<T> getTile(int x, int y);
//    Tile<T> getNeighbour(int x, int y, Dir dir);
//    Set<Tile<T>> getNeighbours(int x, int y);
//
//    void foreach(Consumer<Tile<T>> consumer);
//
//    default Tile<T> getTile(Pos pos){ return getTile(pos.getX(), pos.getY());}
//
    default void put(int x, int y, T obj){
        putTile(new Tile<>(x,y,obj));
    }
    default void put(int x, int y, T obj, int tag){
        putTile(new Tile<>(x,y,obj, tag));
    }
//    default T get(int x, int y){
//        return getTile(x,y).getObj();
//    }
}
