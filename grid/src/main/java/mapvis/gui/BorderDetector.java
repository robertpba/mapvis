package mapvis.gui;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.Impl.RandomColorStyler;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dacc on 10/29/2015.
 */
public class BorderDetector {
    private final Region<INode> world;
    private final Grid<INode> grid;
    private final Tree2<INode> tree;
    private final RandomColorStyler<INode> styler;

    public BorderDetector(Region<INode> world, Grid<INode> grid, Tree2<INode> tree) {
        this.world = world;
        this.grid = grid;
        this.tree = tree;
        styler = new RandomColorStyler<INode>(tree, grid,
                100,
                Color.WHITE,
                1);
    }

    public void Begin(){
        recursive(world);
    }

    private void recursive(Region<INode> region){
        if (!region.isLeaf()) {
            List<Region<INode>> childRegions = region.getChildRegions();
            childRegions.forEach((o1) -> recursive(o1));
            return;
        }
//        processLeaf((LeafRegion) region);
        processLeafNew((LeafRegion<INode>) region);
    }
    int currLevel = 0;
    private void processLeafNew(LeafRegion<INode> leafRegion) {
        List<Border<INode>> splitBorders = new ArrayList<>();
        int prevLevel = -1;
        int index = 0;
//        System.out.println("Borders for " + leafRegion.getNodeItem().getLabel());
        for (Border<INode> border : leafRegion.getBorders()) {

            Border<INode> currBorder = new Border<>();
            Pos lastPos = null;
            List<String> levels = new ArrayList<>();
            for (Border.BorderItem borderItem : border.getBorderItems()) {
                Tile<INode> t = grid.getTile(borderItem.borderItem.first.getX(), borderItem.borderItem.first.getY());
                List<Dir> listOfDirs = new ArrayList<>();
                for (Dir dir : borderItem.borderItem.second) {
                    index++;
                    int currLevel = styler.calcLevel(t.getX(), t.getY(), dir);

//                    Tile<INode> tn = grid.getNeighbour(t.getX(), t.getX(), dir);
//                    int currLevel = calcLevel(t, tn);

                    if(currLevel != prevLevel && prevLevel != -1){
                        //detected the beginning of new border
                        //1. finish last border
//                        System.out.println("Finish last Border at " + t.getPos() + " " + dir.toString());
                        if(listOfDirs.size() > 0){
                            listOfDirs.add(dir);
                            Border.BorderItem lastBorderItem = new Border.BorderItem(new Tuple2<>(lastPos, listOfDirs));
                            lastBorderItem.text = levels;
                            levels = new ArrayList<>();

                            currBorder.setNodeA(t.getItem());
                            Tile<INode> tn = grid.getNeighbour(t.getX(), t.getY(), dir);
                            currBorder.setNodeB(tn.getItem());

                            currBorder.addBorderItem(lastBorderItem);
                            currBorder.setLevel(prevLevel);
                            splitBorders.add(currBorder);
                        }

//                        currBorder.setLevel(currLevel);
//                        currLevel++;
////
////                        //2. create new one
////                        System.out.println("Starting new Border at " + t.getPos() + " " + dir.toString());
////                        lastPos = t.getPos();
////                        prevLevel = currLevel;
                        currBorder = new Border<>();
                        currBorder.setLevel(currLevel);
                        listOfDirs = new ArrayList<>();
                        listOfDirs.add(dir);
                        levels.add(Integer.toString(currLevel));
//                        continue;
                    }else if(prevLevel == -1){
//                        System.out.println("Begining new Border at " + t.getPos() + " " + dir.toString());
                        //begin new border
//                        currBorder = new Border<>();
//                        listOfDirs = new ArrayList<>();

                        listOfDirs.add(dir);
                        levels.add(Integer.toString(currLevel));
                        currBorder.setLevel(currLevel);
                    }else{
//                        System.out.println("Continue Border at " + t.getPos() + " " + dir.toString());
                        //continue with old border
                        listOfDirs.add(dir);
                        levels.add(Integer.toString(currLevel));
                    }

                    lastPos = t.getPos();
                    prevLevel = currLevel;
                }

                if(listOfDirs.size() > 0){
//                    System.out.println("Finishing Border at " + t.getPos() + Arrays.toString(listOfDirs.toArray()));
                    Border.BorderItem lastBorderItem = new Border.BorderItem(new Tuple2<>(t.getPos(), listOfDirs));
                    lastBorderItem.text = levels;
                    levels = new ArrayList<>();
                    currBorder.addBorderItem(lastBorderItem);
                }
            }
            if(currBorder.getBorderItems().size() > 0){
                System.out.println("Adding curr Border");

                Border.BorderItem borderItem = currBorder.getBorderItems().get(0);
                Tile<INode> t = grid.getTile(borderItem.borderItem.first.getX(), borderItem.borderItem.first.getY());
                currBorder.setNodeA(t.getItem());

                Tile<INode> tn = grid.getNeighbour(t.getX(), t.getY(), borderItem.borderItem.second.get(0));
                currBorder.setNodeB(tn.getItem());

                splitBorders.add(currBorder);
            }
        }
        leafRegion.setBorders(splitBorders);
    }

//    private void processLeafNew(LeafRegion<INode> leafRegion) {
//        List<Border<INode>> splitBorders = new ArrayList<>();
//        int prevLevel = -1;
//        System.out.println("Borders for " + leafRegion.getNodeItem().getLabel());
//        for (Border<INode> border : leafRegion.getBorders()) {
//
//            Border<INode> currBorder = new Border<>();
//            Pos lastPos = null;
//
//            for (Border.BorderItem borderItem : border.getBorderItems()) {
//                Tile<INode> t = grid.getTile(borderItem.borderItem.first.getX(), borderItem.borderItem.first.getY());
//
//                List<Dir> listOfDirs = new ArrayList<>();
//                for (Dir dir : borderItem.borderItem.second) {
//                    Tile<INode> tn = grid.getNeighbour(t.getX(), t.getX(), dir);
//                    int currLevel = calcLevel(t, tn);
//                    if(currLevel != prevLevel && prevLevel != -1){
//                        //detected the beginning of new border
//                        //1. finish last border
//                        System.out.println("Finish last Border at " + t.getPos() + " " + dir.toString());
//                        Border.BorderItem lastBorderItem = new Border.BorderItem(new Tuple2<>(lastPos, listOfDirs));
//                        currBorder.addBorderItem(lastBorderItem);
//                        splitBorders.add(currBorder);
//
//                        //2. create new one
//                        System.out.println("Starting new Border at " + t.getPos() + " " + dir.toString());
//                        lastPos = t.getPos();
//                        prevLevel = currLevel;
//                        currBorder = new Border<>();
//                        listOfDirs = new ArrayList<>();
//                        listOfDirs.add(dir);
//                        continue;
//                    }else if(prevLevel == -1){
//                        System.out.println("Begining new Border at " + t.getPos() + " " + dir.toString());
//                        //begin new border
//                        listOfDirs.add(dir);
//                    }else{
//                        System.out.println("Continue Border at " + t.getPos() + " " + dir.toString());
//                        //continue with old border
//                        listOfDirs.add(dir);
//                    }
//                    lastPos = t.getPos();
//                    prevLevel = currLevel;
//                }
//
//                if(listOfDirs.size() > 0){
//                    System.out.println("Finishing Border at " + t.getPos() + Arrays.toString(listOfDirs.toArray()));
//                    Border.BorderItem lastBorderItem = new Border.BorderItem(new Tuple2<>(t.getPos(), listOfDirs));
//                    currBorder.addBorderItem(lastBorderItem);
//                }
//            }
//            if(currBorder.getBorderItems().size() > 0){
//                System.out.println("Adding curr Border");
//                splitBorders.add(currBorder);
//            }
//        }
//        leafRegion.setBorders(splitBorders);
//    }

    private void processLeaf(LeafRegion<INode> leafRegion) {

        List<Border<INode>> splitBorders = new ArrayList<>();
        for (Border<INode> border : leafRegion.getBorders()) {
            List<Border<INode>> splitBoder = splitBorderOnLevelChange(border);
            splitBorders.addAll(splitBoder);
        }
    }

    private List<Border<INode>> splitBorderOnLevelChange(Border<INode> border) {
        List<Border<INode>> splitBorders = new ArrayList<>();
        List<Border.BorderItem> tileToDirs = border.getBorderItems();
        int level = -1;
        int prevLevel = -1;
        int borderItemIndex = 0;
        int borderDirListIndex = 0;
        for (Border.BorderItem borderItem  : tileToDirs) {
            Tile<INode> t = grid.getTile(borderItem.borderItem.first.getX(), borderItem.borderItem.first.getY());
            for (Dir dir : borderItem.borderItem.second) {
                level  = styler.calcLevel(t.getX(), t.getY(), dir);
//
//                Tile<INode> tn = grid.getNeighbour(t.getX(), t.getX(), dir);
//                level = calcLevel(t, tn);
                if(level != prevLevel && prevLevel != -1){
                    Border secondPartBorder = border.splitBorder(borderItemIndex, borderDirListIndex);
                    secondPartBorder.setLevel(level);
                    splitBorders.addAll(splitBorderOnLevelChange(secondPartBorder));
                    return splitBorders;
//                    prevLevel = level;
                }else{
                    prevLevel = level;
                    border.setLevel(level);
                }
                borderDirListIndex++;
            }
            borderDirListIndex = 0;
            borderItemIndex++;
        }
        if(tileToDirs.size() > 0){
            splitBorders.add(border);
        }
        return splitBorders;
    }

//    private void splitBordersFromRange(List<Border<INode>> splitBorders, Border<INode> border,
//                                       final int prevLevel, final int level,
//                                       final int borderItemIndex, final int borderDirListIndex) {
//
//        List<Border.BorderItem> firstPartBorderItems = new ArrayList<>();
//        List<Border.BorderItem> secondPartBorderItems = new ArrayList<>();
//        List<Dir> firstPartDirItems = new ArrayList<>();
//        List<Dir> secondPartDirItems = new ArrayList<>();
//        int currIndexBorderItem = 0;
//        int currIndexDirs = 0;
//        for (Border.BorderItem borderItem : border.getBorderItems()) {
//            if(currIndexBorderItem < borderItemIndex){
//                firstPartBorderItems.add(borderItem);
//            }else if(currIndexBorderItem > borderItemIndex){
//                secondPartBorderItems.add(borderItem);
//            }else{
//                if(currIndexDirs < borderDirListIndex){
//
//                }else if(currIndexDirs > borderDirListIndex){
//
//                }else {
//
//                }
//                firstPartBorderItems.add(borderItem);
//                secondPartBorderItems.add(borderItem);
//            }
//        }
//        Border firstPartBorder = new Border();
//
//
//    }

//    private int calcLevel(Tile<INode> t, Tile<INode> tn) {
//        int level;
//        if (t.getItem() == null || tn.getItem() == null || t.getItem() == tn.getItem()
//                || t.getTag() == Tile.SEA || tn.getTag() == Tile.SEA) {
//            return 0;
//        }
//
//        INode lca = tree.getLCA(t.getItem(), tn.getItem());
//        if (lca == null){
//            return 0;
//        }
//        level = tree.getDepth(lca) + 1;
//        return level;
//    }


}
