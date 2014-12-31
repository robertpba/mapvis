package mapvis.io.university;

import java.util.ArrayList;
import java.util.List;

public class Node {
    static int nextId = 0;

    public int id = nextId++;
    public int level;

    public String program;

    public String label;
    public String type;




    public int size;
    public List<Node> children = new ArrayList<>();
}
