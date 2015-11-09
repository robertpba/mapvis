package mapvis.models;

import com.sun.scenario.effect.impl.Renderer;
import javafx.geometry.Point2D;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.BorderCoordinatesCalcImpl;
import mapvis.graphic.RegionRenderer;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dacc on 10/28/2015.
 */
public class Border<T> {

    public static class BorderItem{
        public Tuple2<Pos, List<Dir>> borderItem;
//        public List<String> text; = new ArrayList<>();
        public List<String> text;

        public BorderItem(Tuple2<Pos, List<Dir>> borderItem) {
            this.borderItem = borderItem;
        }

        @Override
        public boolean equals(Object obj) {
            System.out.println("Equals BorderItem");
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            BorderItem item = (BorderItem) obj;
            if (borderItem.first.equals(item.borderItem.first) && borderItem.second.size() == item.borderItem.second.size()) {
                int directionSize = borderItem.second.size();
                if (directionSize > 0) {
                    Dir firstOfThis = borderItem.second.get(0);
                    Dir firstOfObj = item.borderItem.second.get(0);
                    Dir lastOfThis = borderItem.second.get(directionSize - 1);
                    Dir lastOfObj = item.borderItem.second.get(directionSize - 1);
                    Point2D thisFirst2DPoint =
                            BorderCoordinatesCalcImpl.getPoint2DPointForBorderHexLocation(borderItem.first.getX(), borderItem.first.getY(),
                                    firstOfThis);
                    Point2D objFirst2DPoint =
                            BorderCoordinatesCalcImpl.getPoint2DPointForBorderHexLocation(item.borderItem.first.getX(), item.borderItem.first.getY(),
                                    firstOfObj);
                    Point2D thisLast2DPoint =
                            BorderCoordinatesCalcImpl.getPoint2DPointForBorderHexLocation(borderItem.first.getX(), borderItem.first.getY(),
                                    lastOfThis);
                    Point2D objLast2DPoint =
                            BorderCoordinatesCalcImpl.getPoint2DPointForBorderHexLocation(item.borderItem.first.getX(), item.borderItem.first.getY(),
                                    lastOfObj);


                    if (thisFirst2DPoint.equals(objFirst2DPoint) && thisLast2DPoint.equals(objLast2DPoint)
                            || thisFirst2DPoint.equals(objLast2DPoint) && thisLast2DPoint.equals(objFirst2DPoint)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

    }

    private int level;
//    private boolean isDrawn;
    private RegionRenderer.RenderState renderState;
    private List<BorderItem> borderItems;
    private T nodeA;
    private T nodeB;

    public Border() {
        this.level = -1;
        this.renderState = RegionRenderer.INITIAL_BORDER_RENDERSTATE;
        this.borderItems = new ArrayList<>();
    }

    public Border(List<BorderItem> borderItems, int level) {
        this.level = level;
        this.renderState = RegionRenderer.INITIAL_BORDER_RENDERSTATE;
        this.borderItems = borderItems;
    }

    public Border(List<BorderItem> borderItems) {
        this(borderItems, -1);
    }

    public Border splitBorder(int borderItemIndex, int borderDirListIndex) {
        if(borderItemIndex >= borderItems.size() || borderItems.get(borderItemIndex).borderItem == null
                || borderDirListIndex >= borderItems.get(borderItemIndex).borderItem.second.size()){
            System.out.println("Specified Indices out of bounds");
            return new Border(Collections.emptyList());
        }
        Pos pos = borderItems.get(borderItemIndex).borderItem.first;
        List<Dir> dirs = borderItems.get(borderItemIndex).borderItem.second;
        Iterator<Dir> iterator = dirs.iterator();
        List<Dir> newBorderDirs = new ArrayList<>();
        int index = 0;
        while(iterator.hasNext()){
            Dir next = iterator.next();
            if(index > borderDirListIndex){
                newBorderDirs.add(next);
                iterator.remove();
            }else if(index == borderDirListIndex){
                newBorderDirs.add(next);
            }
            index++;
        }
//        if(borderItemIndex + 1 > borderItems.size() - 1){
//
//        }else{
//
//        }
        List<BorderItem> secondPartListView = borderItems.subList(borderItemIndex + 1 , borderItems.size());

        List<BorderItem> secondPartList = new ArrayList();
        secondPartList.add(new BorderItem(new Tuple2<Pos, List<Dir>>(pos, newBorderDirs)));
        secondPartList.addAll(secondPartListView);
        Border result = new Border(secondPartList);
        secondPartListView.clear();
        return result;
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
        int i = 0;
    }
//    public void setNodeA(T nodeA) {
//        this.nodeA = nodeA;
//    }
//
//    public void setNodeB(T nodeB) {
//        this.nodeB = nodeB;
//    }

    public RegionRenderer.RenderState getRenderState() {
        return renderState;
    }

    public void setRenderState(RegionRenderer.RenderState renderState) {
        this.renderState = renderState;
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

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass()){
            return false;
        }
        Border border = (Border) obj;
        //same referenced node?
//        if(     (border.getNodeA().equals(this.getNodeA()) && border.getNodeB().equals(this.getNodeB())) ||
//                (border.getNodeA().equals(this.getNodeB()) && border.getNodeB().equals(this.getNodeA()))){
//            //same size of borderItems?
            if(borderItems.size() > 0 && borderItems.size() == border.getBorderItems().size()){
                int endIndex = borderItems.size() - 1;
                //same start/endpoints?
                boolean sameBorderPointStart = borderItems.get(0).equals(border.getBorderItems().get(0));
                boolean sameBorderPointEnd = borderItems.get(endIndex).equals(border.getBorderItems().get(endIndex));

                if(sameBorderPointStart && sameBorderPointEnd)
                    return true;
                //same start/endpoints switched?
                if(!sameBorderPointEnd && !sameBorderPointStart){
                    sameBorderPointStart = borderItems.get(endIndex).equals(border.getBorderItems().get(0));
                    sameBorderPointEnd = borderItems.get(0).equals(border.getBorderItems().get(endIndex));
                    if(sameBorderPointStart && sameBorderPointEnd)
                        return true;
                }
            }
//        }
        return false;
    }
}
