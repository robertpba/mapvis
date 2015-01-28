package mapvis.io.random;

import mapvis.common.datatype.Node;

import java.awt.*;
import java.util.Random;

public class RandomColorGenerator {
    Random rand = new Random(0);

    public void color(Node root, int level){
        _color(root, level+1, getRandomColor());
    }

    void _color(Node node, int level, Color color){
        if (level > 0){
            color = getRandomColor();
        }
        node.setVal("color", color.getRGB());

        for (Node child : node.getChildren()) {
            _color(child, level-1, color);
        }
    }

    private Color getRandomColor() {
        Color color;
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
        return color;
    }

}
