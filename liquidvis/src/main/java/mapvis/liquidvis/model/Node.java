package mapvis.liquidvis.model;

public class Node {
    public int id;
    public String name;

    public int level;
    public double figure;
    public double figure2;
    public double x;
    public double y;

    public int pageId;

    public Node parent;
    public Node[] children = new Node[0];
}