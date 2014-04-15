package mapvis.vistools.colormap

import java.awt.Color

abstract class ColorMap {
    abstract fun getColor(value : Double) : Color

    class object {
        val JET = JetColorMap()
    }

}

