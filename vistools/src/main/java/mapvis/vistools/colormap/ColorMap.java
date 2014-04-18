package mapvis.vistools.colormap;

import java.awt.*;

public abstract class ColorMap {
    public abstract Color getColor(double value);

    public static final ColorMap JET = new GenericColorMap(
            new Color[]{Color.black, Color.blue, Color.cyan,
                    Color.yellow, Color.red, Color.black},
            -0.125f, 1.125f);

    public static  final ColorMap hsv = new GenericColorMap(
            new Color[]{Color.red, Color.yellow, Color.green,
                    Color.cyan, Color.blue, Color.magenta, Color.red});

    public static  final ColorMap hot = new GenericColorMap(
            new Color[]{Color.black, Color.red, Color.yellow, Color.white});

    public static  final ColorMap cool = new GenericColorMap(
            new Color[]{ Color.cyan, Color.magenta});

    public static  final ColorMap summer = new GenericColorMap(
            new Color[]{ new Color(0.0f, 0.5f, 0.4f), new Color(1.0f, 1.0f, 0.4f)});

    public static  final ColorMap pink = new GenericColorMap(
            new Color[]{ new Color(255, 128, 128), Color.white});

    public static  final ColorMap gray = new GenericColorMap(
            new Color[]{ Color.black, Color.white});

    public static  final ColorMap white = new GenericColorMap(
            new Color[]{ Color.white, Color.white});

}