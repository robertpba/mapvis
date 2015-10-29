package mapvis.models;

import mapvis.common.datatype.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dacc on 10/28/2015.
 */
public class Border<T> {
    public static class BorderItem{
        Tuple2<Pos, List<Dir>> borderItem;

        public BorderItem(Tuple2<Pos, List<Dir>> borderItem) {
            this.borderItem = borderItem;
        }
    }
    private int level;
    private boolean isDrawn;
    private List<BorderItem> borderItems;
    private T nodeA;
    private T nodeB;

    public Border() {
        this.level = -1;
        this.isDrawn = false;
        this.borderItems = new ArrayList<>();
    }

    public Border(List<BorderItem> borderItems) {
        this.level = -1;
        this.isDrawn = false;
        this.borderItems = borderItems;
    }


    public void addBorderItem(BorderItem borderItem){
        borderItems.add(borderItem);
    }

    public List<BorderItem> getBorderItems() {
        return Collections.unmodifiableList(borderItems);
    }

    public T getNodeA() {
        return nodeA;
    }

    public T getNodeB() {
        return nodeB;
    }

    public int getLevel() {
        return level;
    }

    public void setNodeA(T nodeA) {
        this.nodeA = nodeA;
    }

    public void setNodeB(T nodeB) {
        this.nodeB = nodeB;
    }

    public void setIsDrawn(boolean isDrawn) {
        this.isDrawn = isDrawn;
    }

    public boolean isDrawn() {
        return isDrawn;
    }
}
