package mapvis.vistools.colormap;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class ColorBar extends JComponent {

    public final static int X_AXIS = 0;
    public final static int Y_AXIS = 1;

    private Function<Double, Color> colorFunc;
    private int orientation;

    public ColorBar(Function<Double, Color> colorFunc) {
        this(colorFunc, X_AXIS);
    }

    public ColorBar(Function<Double, Color> colorFunc, int orientation) {
        super();
        this.colorFunc = colorFunc;
        this.orientation = orientation;
    }

    @Override
    public Dimension getPreferredSize() {
        return orientation == X_AXIS? new Dimension(100, 20):new Dimension(20,100);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth(), getHeight());

        if (orientation == X_AXIS) {
            double slope = getWidth() / 256.0;

            for (int n = 256; n != 0; n--) {
                int x = (int)(n * slope);
                g.setColor(colorFunc.apply((n - 1) / 255.0));
                g.fillRect(0, 0, x, getHeight());
            }
        }else {
            double slope = getHeight() / 256.0;

            for (int n = 256; n != 0; n--) {
                int y = (int) (n * slope);
                g.setColor(colorFunc.apply((256 - n) / 255.0));
                g.fillRect(0, 0, getWidth(), y);
            }
        }
    }
}