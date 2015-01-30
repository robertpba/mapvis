package mapvis.layouts;


import mapvis.common.datatype.Node;
import mapvis.layouts.dac.Compact;
import mapvis.layouts.dac.Disperse;
import mapvis.layouts.dac.Entry;

import java.util.stream.Collectors;

public class Dac {

    static Entry<Node> createEntry(Node root){

        Entry<Node> entry = new Entry<>();
        entry.node = root;
        entry.r =  Math.sqrt((int) root.getVal("size"))/2;

        entry.children = root.getChildren().stream()
                .map(Dac::createEntry)
                .collect(Collectors.toList());

        return entry;
    }

    static void updatePosition(Entry<Node> entry){
        entry.node.setVal("x",entry.x);
        entry.node.setVal("y", entry.y);

        entry.node.setVal("x0",entry.x0);
        entry.node.setVal("x1",entry.x1);
        entry.node.setVal("y0",entry.y0);
        entry.node.setVal("y1",entry.y1);

        entry.children.forEach(Dac::updatePosition);
    }

    public static void run(Node root, int steps){
        Entry<Node> entry = createEntry(root);
        Disperse.disperse(entry, steps);
        Compact.compact(entry);
        //entry.children.forEach(Compact::compact);
        updatePosition(entry);
    }
}
