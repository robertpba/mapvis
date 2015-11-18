package mapvis.algo;

import mapvis.Impl.HashMapGrid;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.layouts.commons.RandomHelper;
import mapvis.models.*;

import java.util.*;
import java.util.stream.Collectors;


public class Method1<T> {
    public Tree2<T> tree;
    public CoastCache<T> cache;
    public Grid<T> grid;
    private Region<T> rootRegion;
    private Map<T, LeafRegion<T>> leafItemToLeafRegion;
    private List<List<GridCoordinateCollection>> leafRegionBoundaryCoordinates;

    private Random random = new Random(1);
    public double beta = 3;


    public Method1(Tree2<T> tree, Grid<T> grid) {
        this.tree = tree;
        this.grid = grid;
        this.cache = new CoastCache<>(grid, tree);
        this.leafRegionBoundaryCoordinates = new ArrayList<>();
        this.leafItemToLeafRegion = new HashMap<>();
    }

    public Region<T> Begin(){
        random = new Random(1);
        rootRegion = recursive(tree.getRoot());
        return rootRegion;
    }

    public List<List<GridCoordinateCollection>> getLeafRegionBoundaryCoordinates() {
        return leafRegionBoundaryCoordinates;
    }

    public Map<T, LeafRegion<T>> getItemToRegionMap() {
        return leafItemToLeafRegion;
    }

    private Region<T> recursive(T o){
        Set<T> children = tree.getChildren(o);
        if (children.size() > 0) {
            List<Region<T>> childRegions = new ArrayList<>();
            children.stream()
                    .filter(child -> tree.getDepth(child) > 0)
                    .forEach(nonLeafChild -> childRegions.add(recursive(nonLeafChild)));

            //if (maxHexagonLevelToShow == 1)
            //addPadding(o,5);
            return new Region<>(childRegions, o, tree.getDepth(o));
        }
        return allocate(o);
    }

    //not used at the moment
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

    private LeafRegion<T> allocate(T o){
        //holds all allocated Tiles to delete created Tiles when
        //space is insufficient
        List<Tile<T>> rollback = new ArrayList<>();
        List<Tile<T>> prepare = new ArrayList<>();

        Grid<T> regionGrid = new HashMapGrid<>();

        int count = tree.getWeight(o);
        while (count-- > 0) {
            Pos pos = findStartPoint(o);
            Tile<T> tile = new Tile<>(pos, o);

            grid.putTile(tile);
            regionGrid.putTile(tile);

            cache.insert(tile.getX(), tile.getY(), o);
            prepare.add(tile);

            while (count-- > 0) {
                pos = findNextPoint(o);
                if (pos == null)
                    break;

                tile = new Tile<>(pos, o);

                grid.putTile(tile);
                regionGrid.putTile(tile);

                cache.insert(tile.getX(), tile.getY(), o);
                prepare.add(tile);
            }
            if (count > 0){
                rollback.addAll(prepare);
                prepare.clear();
            }
        }

        for (Tile<T> tile : rollback) {
            Tile<T> empty = new Tile<>(tile.getPos());
            grid.putTile(empty);
            regionGrid.putTile(empty);
            cache.remove(tile.getX(), tile.getY(), tile.getItem());
        }
        if (count > 0) {
            System.out.printf("Insufficient space: %s\n", o.toString());
        }

        List<GridCoordinateCollection> gridCoordinatesOfBorderTiles = new ArrayList<>();

        List<Tile<T>> borderTiles = new ArrayList<>();
        for (Tile<T> tTile : prepare) {
            List<Dir> dirsAtBorderTiles = regionGrid.getNeighborDirectionsFulfilling(tTile2 -> tTile2.isEmpty(), tTile.getX(), tTile.getY());
            if(dirsAtBorderTiles.size() > 0){
                GridCoordinateCollection gridCoordinateCollection = new GridCoordinateCollection(tTile.getPos(), dirsAtBorderTiles);
                gridCoordinatesOfBorderTiles.add(gridCoordinateCollection);
                borderTiles.add(tTile);
            }
//            //collect all tiles with their directions to neighbors which have border to an tile of a different node
//            List<Dir> bordersOfTile = grid.getNeighborDirectionsFulfilling(tTile1 -> {
//                return tTile1.getTag() == Tile.LAND && !tTile.getItem().equals(tTile1.getItem());
//            }, tTile.getX(), tTile.getY());

        }

        LeafRegion<T> leafRegion = new LeafRegion<>(o, tree.getDepth(o));
        leafRegionBoundaryCoordinates.add(gridCoordinatesOfBorderTiles);
        leafItemToLeafRegion.put(o, leafRegion);

        return leafRegion;
    }

    private Pos findNextPoint(T o){
        Set<Tile<T>> waters = cache.getWaters(o);

        if (waters.size() == 0) return null;

        List<Tuple2<Integer, Tile<T>>> list = waters.stream().map(t -> new Tuple2<>(score(t), t))
                .sorted(Comparator.comparing(t -> t.first))
                .collect(Collectors.toList());

        Tile t  = RandomHelper.weightedRandom(list, random);

        return t.getPos();
    }

    private Pos findStartPoint(T o){
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
