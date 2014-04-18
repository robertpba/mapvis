package mapvis.vistools.colormap;


import java.awt.*;

public class GenericColorMap extends ColorMap {

    @Override
    public Color getColor(double value) {
        if (value < 0.0)
            return colors[0];
        if (value > 1.0)
            return colors[colors.length-1];

        for (int i0 = 0, i = 1; i < colors.length; i0 = i++) {
            if (value <= levels[i])
                return interpolate((float)value, levels[i0], colors[i0], levels[i], colors[i]);
        }
        return colors[colors.length-1];
    }

    private Color interpolate(float x, float l0, Color c0, float l1, Color c1) {
        return new Color(
                interpolate(x, l0, c0.getRed(), l1, c1.getRed()),
                interpolate(x, l0, c0.getGreen(), l1, c1.getGreen()),
                interpolate(x, l0, c0.getBlue(), l1, c1.getBlue()));
    }

    private int interpolate(float x, float x0, float y0, float x1, float y1) {
        return (int)((x - x0) * (y1 - y0) / (x1 - x0) + y0);
    }

    Color[] colors;
    float[] levels;

    public GenericColorMap(Color[] colors, float[] levels) {
        this.colors = colors;
        this.levels = levels;
    }
    public GenericColorMap(Color[] colors) {
        this(colors, 0.0f, 1.0f);
    }
    public GenericColorMap(Color[] colors, float min, float max) {
        this.colors = colors;
        levels = new float[colors.length];
        float scale = (max-min)/(colors.length-1);
        for (int i = 0; i < colors.length; i++) {
            levels[i] = scale*i + min;
        }
    }


}
