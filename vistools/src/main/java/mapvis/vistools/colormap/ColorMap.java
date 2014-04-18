package mapvis.vistools.colormap;

import java.awt.*;

abstract class ColorMap {
    abstract Color getColor(double value);

    static final ColorMap JET = new JetColorMap();
}