package mapvis.vistools.colormap;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class ColorBar extends JComponent {
    private Function<Double, Color> colorFunc;

    public ColorBar(Function<Double, Color> colorFunc) {
        this.colorFunc = colorFunc;
        setPreferredSize(new Dimension(100, 20));
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        if (g instanceof Graphics2D)
            return;

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        double slope = getWidth() / 256.0;

        int n = 255;
        while(n-- != 0) {
            int x = (int)(n * slope);
            g.setColor(colorFunc.apply(n / 255.0));
            g.fillRect(x, 0, (int)slope, getHeight());
        }
    }
}