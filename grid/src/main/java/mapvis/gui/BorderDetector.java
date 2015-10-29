package mapvis.gui;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/29/2015.
 */
public class BorderDetector {
    private final Region<INode> world;
    private final Grid<INode> grid;
    private final Tree2<INode> tree;

    public BorderDetector(Region<INode> world, Grid<INode> grid, Tree2<INode> tree) {
        this.world = world;
        this.grid = grid;
        this.tree = tree;
    }

    public void Begin(){
        recursive(world);
    }

    private void recursive(Region<INode> region){
        if (!region.isLeaf()) {
            List<Region<INode>> childRegions = region.getChildRegions();
            childRegions.forEach((o1) -> recursive(o1));
        }
        processLeaf((LeafRegion) region);
    }

    private void processLeaf(LeafRegion<INode> leafRegion) {

        List<Border<INode>> splitBorders = new ArrayList<>();
        for (Border<INode> border : leafRegion.getBorders()) {
            List<Border.BorderItem> tileToDirs = border.getBorderItems();
            int level = -1;
            int prevLevel = -1;
            int borderItemIndex = 0;
            int borderDirListIndex = 0;
            for (Border.BorderItem borderItem  : tileToDirs) {
                Tile<INode> t = grid.getTile(borderItem.borderItem.first.getX(), borderItem.borderItem.first.getY());
                for (Dir dir : borderItem.borderItem.second) {
                    Tile<INode> tn = grid.getNeighbour(t.getX(), t.getX(), dir);
                    level = calcLevel(t, tn);
                    if(level != prevLevel){
                        splitBordersFromRange(splitBorders, border, prevLevel, level, borderItemIndex, borderDirListIndex);
                        prevLevel = level;
                    }else{
                        prevLevel = level;
                    }
                    borderDirListIndex++;
                }
                borderItemIndex++;
            }
        }
    }

    private void splitBordersFromRange(List<Border<INode>> splitBorders, Border<INode> border,
                                       final int prevLevel, final int level,
                                       final int borderItemIndex, final int borderDirListIndex) {
        List<Border.BorderItem> firstPartBorderItems = new ArrayList<>();
        List<Border.BorderItem> secondPartBorderItems = new ArrayList<>();
        List<Dir> firstPartDirItems = new ArrayList<>();
        List<Dir> secondPartDirItems = new ArrayList<>();
        int currIndexBorderItem = 0;
        int currIndexDirs = 0;
        for (Border.BorderItem borderItem : border.getBorderItems()) {
            if(currIndexBorderItem < borderItemIndex){
                firstPartBorderItems.add(borderItem);
            }else if(currIndexBorderItem > borderItemIndex){
                secondPartBorderItems.add(borderItem);
            }else{
                if(currIndexDirs < borderDirListIndex){

                }else if(currIndexDirs > borderDirListIndex){

                }else {

                }
                firstPartBorderItems.add(borderItem);
                secondPartBorderItems.add(borderItem);
            }
        }
        Border firstPartBorder = new Border();


    }


    private int calcLevel(Tile<INode> t, Tile<INode> tn) {
        int level;
        if (t.getItem() == null || tn.getItem() == null || t.getItem() == tn.getItem()
                || t.getTag() == Tile.SEA || tn.getTag() == Tile.SEA) {
            return 0;
        }

        INode lca = tree.getLCA(t.getItem(), tn.getItem());
        if (lca == null){
            return 0;
        }
        level = tree.getDepth(lca) + 1;
        return level;
    }
}
