package mapvis.utils.colormap;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class ColorScale extends JComponent {
    public final static int X_AXIS = 0;
    public final static int Y_AXIS = 1;

    public ColorScale(Function<Double, Color> colorFunc, float[] levels, String[] labels) {
        this.colorFunc = colorFunc;
        this.levels = levels;
        this.labels = labels;
        this.colorBar = new ColorBar(colorFunc);
        colorBar.setSize(new Dimension(180,20));
        colorBar.setLocation(10,0);
    }

    public interface StringFormatter {
        String getString(float level);
    }
    Function<Double, Color> colorFunc;

    float[] levels;
    String[] labels;
    ColorBar colorBar;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.translate(10,0);
        colorBar.paintComponent(g);
        paintScale((Graphics2D) g);

    }

    private void paintScale(Graphics2D g){
        double slope = (double)200-20;
        for (int i = 0; i < levels.length; i++) {
            int x = (int)(levels[i] * slope);
            g.drawLine(x, 20, x, 25);
            System.out.printf("scale x : %d\n", x);

            g.setColor(Color.black);
            drawStringCenter(g, labels[i], x, 50);
        }
    }

    private void drawStringCenter(Graphics2D g, String s, int x, int y){
        Font font = g.getFont();
        FontMetrics fm   = g.getFontMetrics(font);
        java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);

        int textHeight = (int)(rect.getHeight());
        int textWidth  = (int)(rect.getWidth());

        g.drawString(s, x-textWidth/2, y-textHeight/2);
    }


}
