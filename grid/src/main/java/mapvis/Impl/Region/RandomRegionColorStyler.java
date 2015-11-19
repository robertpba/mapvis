package mapvis.Impl.Region;

import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import mapvis.Impl.Tile.TileStylerBase;
import mapvis.common.datatype.Tree2;
import mapvis.models.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by dacc on 11/12/2015.
 */
public class RandomRegionColorStyler<T> extends RegionStylerBase<T> {
    public int depth;
    private Map<T, Color> map = new HashMap<>();
    private Random rand;
    private ObjectProperty<Color> background;

    public RandomRegionColorStyler(ObjectProperty<Tree2<T>> tree,
                                   TileStylerBase.StylerUIElements stylerUIElements, int seed) {
        super(tree, stylerUIElements);
        this.background = stylerUIElements.getBackground();
        this.rand = new Random(seed);
        rec(tree.get().getRoot());
        this.tree.addListener((observable, oldValue, newValue) -> rec(tree.get().getRoot()));
    }

    private void rec(T node){
        if (node == null)
            return;

        Color color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0);

        map.put(node, color);

        for (T child : tree.get().getChildren(node)) {
            rec(child);
        }

        depth = Math.max(tree.get().getDepth(node), depth);
    }

    @Override
    public Color getColor(Region<T> region) {
        if(!isRegionVisible(region)){
            //calc the parent node corresponding to the max region level to show
            List<T> pathToNode = tree.get().getPathToNode(region.getNodeItem());
            int levelsToGoUp = region.getLevel() - maxRegionLevelToShow.intValue();
            int indexOfParentWithMaxLevelToShow = Math.max(0, pathToNode.size() - levelsToGoUp - 1);

            return getColorByValue(pathToNode.get(indexOfParentWithMaxLevelToShow));
        }
        return super.getColor(region);
    }

    @Override
    public Color getColorByValue(T v) {
        return map.get(v);
    }

    @Override
    public double getBorderWidthByLevel(int l) {
        return  Math.pow(depth + 1 - l, 1.2)/2.0;
    }

    @Override
    public Color getBackground() {
        return background.get();
    }

    public ObjectProperty<Color> backgroundProperty() {
        return background;
    }

    public void setBackground(Color background) {
        this.background.set(background);
    }

}
