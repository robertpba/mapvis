package mapvis.models;

import mapvis.common.datatype.Tuple2;
import mapvis.graphic.RegionBorderRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dacc on 10/28/2015.
 */
public class Border<T> {

    public static class BorderItem{
        public Tuple2<Pos, List<Dir>> borderItem;
        public BorderItem(Tuple2<Pos, List<Dir>> borderItem) {
            this.borderItem = borderItem;
        }
    }

    private int level;
    private int renderID;
    private List<BorderItem> borderItems;
    private T nodeA;
    private T nodeB;

    public Border() {
        this.level = -1;
        this.renderID = -1;
        this.borderItems = new ArrayList<>();
    }

    public Border(List<BorderItem> borderItems, int level) {
        this.renderID = -1;
        this.level = level;
        this.borderItems = borderItems;
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

    public void setLevel(int level) {
        this.level = level;
    }

    public void setNodes(T nodeA, T nodeB){
        if(nodeA == null || nodeB == null){
            if(nodeA == null){
                this.nodeA = nodeB;
                this.nodeB = null;
            }else{
                this.nodeA = nodeA;
                this.nodeB = null;
            }
            return;
        }

        int hashNodeA = nodeA.hashCode();
        int hashNodeB = nodeB.hashCode();
        if(hashNodeA < hashNodeB){
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }else{
            this.nodeA = nodeB;
            this.nodeB = nodeA;
        }
    }

    public int getRenderID() {
        return renderID;
    }

    public void setRenderID(int renderID) {
        this.renderID = renderID;
    }

    public Tuple2<Pos, Dir> getStartPoint(){
        if(borderItems.isEmpty())
            return null;
        BorderItem borderItem = borderItems.get(0);
        if(borderItem.borderItem.second.isEmpty())
            return null;
        Dir dir = borderItem.borderItem.second.get(0);
        return new Tuple2<>(borderItem.borderItem.first, dir);
    }

    public Tuple2<Pos, Dir> getLastPoint(){
        if(borderItems.isEmpty())
            return null;
        BorderItem borderItem = borderItems.get(borderItems.size() - 1);
        if(borderItem.borderItem.second.isEmpty())
            return null;
        Dir dir = borderItem.borderItem.second.get(borderItem.borderItem.second.size() - 1);
        return new Tuple2<>(borderItem.borderItem.first, dir);
    }

}
