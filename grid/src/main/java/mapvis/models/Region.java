package mapvis.models;

import mapvis.graphic.RegionRendering.BoundaryShapeSorter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/26/2015.
 * Region are created for each Node in the tree. Regions
 * have store their subregions as childRegions. The borders
 * the region can be calculated by accumulating the border
 * segments stored in the LeafRegions of its children.
 */
public class Region<T> {

    final T nodeItem;
    private final int level;
    private List<Region<T>> childRegions;


    public Region(List<Region<T>> childRegions, T nodeItem, int level) {
        this.childRegions = childRegions;
        this.nodeItem = nodeItem;
        this.level = level;
    }

    public boolean isLeaf(){
        return false;
    }

    public List<Region<T>> getChildRegions() {
        return childRegions;
    }

    public int getLevel() {
        return level;
    }

    public T getNodeItem() {
        return nodeItem;
    }

    public List<Region<T>> getChildRegionsAtLevel(int level){
        List<Region<T>> result = new ArrayList<>();
        if(level == this.level){
            result.add(this);
        }else if(level > this.level){
            childRegions.forEach(region -> result.addAll(region.getChildRegionsAtLevel(level)));
        }
        return result;
    }

    public List<List<BoundaryShape<T>>> getBoundaryShape(){
        List<BoundaryShape<T>> boundaryShapes = new ArrayList<>();

        for (Region<T> childRegion : childRegions) {
            List<Border<T>> bordersForLevel = childRegion.getBordersForLevel(level);
            for (Border<T> tBorder : bordersForLevel) {
                boundaryShapes.add(tBorder.calcBoundaryShape());
            }
        }

        return BoundaryShapeSorter.orderBoundaryShapes(boundaryShapes);
    }


    protected List<Border<T>> getBordersForLevel(int level){
        List<Border<T>> resultingCollection = new ArrayList<>();
        childRegions.forEach(tRegion -> resultingCollection.addAll(tRegion.getBordersForLevel(level)));
        return resultingCollection;
    }
}
