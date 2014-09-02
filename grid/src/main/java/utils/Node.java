package utils;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public int id;
    public String name;

    public int level;
    public double figure;
    public double x;
    public double y;

    public int pageId;

    public Node parent;
    public List<Node> children = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFigure() {
        return figure;
    }

    public void setFigure(double figure) {
        this.figure = figure;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (id != node.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}