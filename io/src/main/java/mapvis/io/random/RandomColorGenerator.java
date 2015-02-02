package mapvis.io.random;

import mapvis.common.datatype.Node;

import java.awt.*;
import java.util.Random;

public class RandomColorGenerator {
    Random rand = new Random(0);

    Color[] colors = new Color[]{
            new Color(121,229,136),
            new Color(22,156,255),
            new Color(158,131,227),
            new Color(248,94,243),
            new Color(255,255,99)
    };
    int i=0;

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
        //float r = rand.nextFloat();
        //float g = rand.nextFloat();
        //float b = rand.nextFloat();
        //color = new Color(r, g, b);
        color = colors[i++%5];
        return color;
    }

}
