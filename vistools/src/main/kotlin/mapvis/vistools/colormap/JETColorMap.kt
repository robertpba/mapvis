package mapvis.vistools.colormap

import java.awt.Color

public class JetColorMap : ColorMap() {
    override fun getColor(value: Double): Color {
        fun interpolate(x: Double,
                        x0: Double, y0: Double,
                        x1: Double, y1: Double)
                = (x -x0)*(y1-y0)/(x1-x0) + y0

        fun base(v : Double) = when (v) {
            in -0.375..-0.125 -> interpolate(v, -0.375, 0.0, -0.125, 1.0)
            in -0.125..+0.125 -> 1.0
            in +0.125..+0.375 -> interpolate(v, +0.125, 1.0, +0.375, 0.0)
            else  -> 0.0
        }

        fun r(v : Double) = base(v-0.25)
        fun g(v : Double) = base(v)
        fun b(v : Double) = base(v+0.25)

        val v = Math.max(0.0, Math.min(1.0, value)) - 0.5

        return Color(r(v).toFloat(), g(v).toFloat(), b(v).toFloat())
    }
}