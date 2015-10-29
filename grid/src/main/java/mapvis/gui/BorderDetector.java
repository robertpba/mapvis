package mapvis.gui;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.models.*;

import java.util.List;

/**
 * Created by dacc on 10/29/2015.
 */
public class BorderDetector {
    private final Region<INode> world;
    private final Grid<INode> grid;

    public BorderDetector(Region<INode> world, Grid<INode> grid) {
        this.world = world;
        this.grid = grid;
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
        for (Border border : leafRegion.getBorders()) {
            List<Tuple2<Tile<INode>, List<Dir>>> tileToDirs = border.getBorderItems();
            for (Tuple2<Tile<INode>, List<Dir>> borderItem  : tileToDirs) {
//                grid.getNeighbour(borderItem.fi)
            }
        }
    }
}
