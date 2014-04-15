package mapvis.vistools.colormap

import javax.swing.*
import java.awt.*
import java.util.function.Function

public class Colorbar(val colorFunc: Function<Double, Color>) : JComponent() {
    {
        this.setPreferredSize(Dimension(100, 20))
    }

    override fun paintComponent(g: Graphics) {
        if (g !is Graphics2D)
            return;

        g.setColor(getBackground())
        g.fillRect(0, 0, getWidth(), getHeight())

        val slope = getWidth().toFloat() / 256.0.toFloat()

        for (n in 0..255) {
            val x = (n.toFloat() * slope).toInt()
            g.setColor(colorFunc.apply(n.toDouble() / 255.0))
            g.fillRect(x, 0, slope.toInt(), getHeight())
        }
    }
}