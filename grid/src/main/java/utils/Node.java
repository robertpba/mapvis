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
}