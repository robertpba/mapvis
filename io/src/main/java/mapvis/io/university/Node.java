package mapvis.io.university;

import java.util.ArrayList;
import java.util.List;

public class Node {
    int level;
    String label;
    String type;
    int size;
    List<Node> children = new ArrayList<>();
}
