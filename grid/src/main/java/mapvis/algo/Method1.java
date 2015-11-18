package mapvis.algo;

import mapvis.Impl.HashMapGrid;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.layouts.commons.RandomHelper;
import mapvis.models.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Algorithm for successive creation of regions/areas located in a grid.
 * The area of each region consists of hexagons. The size of the regions
 * is defined according to the size of the nodes in the provided tree.
 * @param <T> type for the items stored in the node in the provided tree
 */
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

    /**
     * Triggers the start of the algorithm. It creates
     * areas of hexagon tiles with respect to the size of
     * the node. The node size is defined in the tree as the
     * sum of the sizes of its child nodes.
     * In addition, the mapping of node to LeafRegion is stored and
     * the coordinates of the boundaries as @GridCoordinateCollection.
     * @return the created root region with connected children
     */
    public Region<T> Begin(){
        random = new Random(1);
        rootRegion = recursive(tree.getRoot());
        return rootRegion;
    }

    private Region<T> recursive(T o){
        Set<T> children = tree.getChildren(o);

        if (children.size() > 0) {
            //for nodes with children recursively create Regions for the children
            List<Region<T>> childRegions = new ArrayList<>();
            children.stream()
                    .filter(child -> tree.getDepth(child) > 0)
                    .forEach(nonLeafChild -> childRegions.add(recursive(nonLeafChild)));

            //if (maxHexagonLevelToShow == 1)
            //addPadding(o,5);
            return new Region<>(childRegions, o, tree.getDepth(o));
        }
        //create region for the leaves
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

    /**
     * allocates HexagonTiles for the given node according to the
     * size of the node object in the tree
     * @param o the Node object to allocate HexagonTiles
     * @return the LeafRegion created for that Node object
     */
    private LeafRegion<T> allocate(T o){
        //holds all allocated Tiles to delete created Tiles when
        //space is insufficient
        List<Tile<T>> rollback = new ArrayList<>();
        List<Tile<T>> prepare = new ArrayList<>();

        //local grid to identify the tiles which define the border of the node
        Grid<T> regionGrid = new HashMapGrid<>();

        //add HexagonTiles for the region according to
        //the size of the node
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
            //if finding next position filed count > 0
            //=> rollback, reinitialize at another position
            //and try again to allocate tiles
            if (count > 0){
                rollback.addAll(prepare);
                prepare.clear();
            }
        }

        //rollback required: remove all allocated tiles from the grids
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

        //find the coordinates of the hexagontiles/edges at the border of the
        //created region
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
        //find a point which is not yet occupied by another region
        Set<Tile<T>> waters = cache.getWaters(o);

        if (waters.size() == 0) return null;

        //weight the found tiles to try filling out wholes
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

    public List<List<GridCoordinateCollection>> getLeafRegionBoundaryCoordinates() {
        return leafRegionBoundaryCoordinates;
    }

    public Map<T, LeafRegion<T>> getItemToRegionMap() {
        return leafItemToLeafRegion;
    }
}
