package mapvis.vistools.colormap;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class ColorBar extends JComponent {
    private Function<Double, Color> colorFunc;

    public ColorBar(Function<Double, Color> colorFunc) {
        super();
        this.colorFunc = colorFunc;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 20);
    }

    @Override
    public void paintComponent(Graphics g) {
        this.setOpaque(false);
        super.paintComponents(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        double slope = getWidth() / 256.0;

        for (int n = 256; n != 0; n--) {
            int x = (int)(n * slope);
            g.setColor(colorFunc.apply( (n-1) / 255.0));
            g.fillRect(0, 0, x, getHeight());
        }
    }
}