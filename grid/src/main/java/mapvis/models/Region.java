package mapvis.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/26/2015.
 */
public class Region<T> {
    final T nodeItem;
    private final int level;

    private List<Region<T>> childRegions;

//    public Region(List<Region<T>> enclosingElements, T nodeItem) {
//        this.childRegions = enclosingElements;
//        this.nodeItem = nodeItem;
//    }

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
}
