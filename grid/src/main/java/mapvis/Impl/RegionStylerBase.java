package mapvis.Impl;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.IRegionAreaStyler;
import mapvis.graphic.IRegionBorderStyler;
import mapvis.graphic.IRegionStyler;
import mapvis.models.Border;
import mapvis.models.Region;

/**
 * Created by dacc on 11/12/2015.
 */
public class RegionStylerBase<T> implements IRegionStyler<T> {

    protected ObjectProperty<Tree2<T>> tree;
    protected DoubleProperty maxBorderLevelToShow;
    protected DoubleProperty maxRegionLevelToShow;

    public RegionStylerBase(ObjectProperty<Tree2<T>> tree,
                            DoubleProperty maxBorderLevelToShow,
                            DoubleProperty maxRegionLevelToShow) {
        System.out.println("Creating: " + this.getClass().getName());
        this.tree = tree;
        this.maxBorderLevelToShow = maxBorderLevelToShow;
        this.maxRegionLevelToShow = maxRegionLevelToShow;
    }

    @Override
    public boolean isRegionVisible(Region<T> region) {
        return region.getLevel() < maxRegionLevelToShow.get();
    }

    @Override
    public Color getColor(Region<T> region) {
        return getColorByValue(region.getNodeItem());
    }

    @Override
    public Color getBackground() {
        return Color.AQUAMARINE;
    }

    protected Color getBorderColorByLevel(int l){
        return Color.BLACK;
    }

    public Color getColorByValue(T v) {
        return Color.RED;
    }

//    public Tree2<T> getTree() {
//        return tree;
//    }
//
//    public void setTree(Tree2<T> tree) {
//        this.tree = tree;
//    }
//
//    void resetStyler(Tree2<T> tree){
//        this.tree = tree;
//    }

    public double getBorderWidthByLevel(int l){
        return (4.0 - l)*(4.0 - l)/2;
    }

    @Override
    public boolean isBorderVisible(Border<T> border) {
        return border.getLevel() <= maxBorderLevelToShow.get();
    }

    @Override
    public double getBorderWidth(Border<T> border) {
        return getBorderWidthByLevel(border.getLevel());
    }

    @Override
    public Color getBorderColor(Border<T> border) {
        return Color.BLACK;
    }

//    @Override
//    public void setMaxBorderLevelToShow(int maxBorderLevelToShow) {
//        this.maxBorderLevelToShow = maxBorderLevelToShow;
//    }

    @Override
    public int getMaxBorderLevelToShow() {
        return maxBorderLevelToShow.intValue();
    }

    @Override
    public int getMaxRegionLevelToShow() {
        return maxRegionLevelToShow.intValue();
    }

//    @Override
//    public void setMaxRegionLevelToShow(int maxRegionLevelToShow) {
//        this.maxRegionLevelToShow = maxRegionLevelToShow;
//    }


    public Tree2<T> getTree() {
        return tree.get();
    }

    public ObjectProperty<Tree2<T>> treeProperty() {
        return tree;
    }

    public void setTree(Tree2<T> tree) {
        this.tree.set(tree);
    }

    public DoubleProperty maxBorderLevelToShowProperty() {
        return maxBorderLevelToShow;
    }

    public void setMaxBorderLevelToShow(int maxBorderLevelToShow) {
        this.maxBorderLevelToShow.set(maxBorderLevelToShow);
    }

    public DoubleProperty maxRegionLevelToShowProperty() {
        return maxRegionLevelToShow;
    }

    public void setMaxRegionLevelToShow(int maxRegionLevelToShow) {
        this.maxRegionLevelToShow.set(maxRegionLevelToShow);
    }
}
