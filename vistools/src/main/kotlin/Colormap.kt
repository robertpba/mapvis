package mapvis.vistools.colormap

import java.awt.Color


fun interpolate(x: Float,
                x0: Float,
                y0: Float,
                x1: Float,
                y1: Float)
        = (x -x0)*(y1-y0)/(x1-x0) + y0

fun interpolate(x: Double,
                x0: Double,
                y0: Double,
                x1: Double,
                y1: Double)
        = (x -x0)*(y1-y0)/(x1-x0) + y0

//public class ColorMap(nm: String, c: Array<Color>) {
//    var name: String = nm
//    var clrs: Array<Color> = c
//
//    public fun getColor(here: Double): Color {
//        var res = clrs[0]
//        if (here > 0) {
//            val dn = 1.00001 / (clrs.size - 1).toDouble()
//            var n = (here / dn).toInt()
//            val dh = (here - n.toDouble() * dn) * (clrs.size - 1).toDouble()
//            val r0 = clrs[n].getRed()
//            val g0 = clrs[n].getGreen()
//            val b0 = clrs[n].getBlue()
//            if (n > (clrs.size - 2)) {
//                n = clrs.size - 2
//            }
//            val r1 = clrs[n + 1].getRed()
//            val g1 = clrs[n + 1].getGreen()
//            val b1 = clrs[n + 1].getBlue()
//            var r = r0.toDouble() + dh * (r1 - r0).toDouble()
//            r = check(r)
//            var g = g0.toDouble() + dh * (g1 - g0).toDouble()
//            g = check(g)
//            var b = b0.toDouble() + dh * (b1 - b0).toDouble()
//            b = check(b)
//            res = Color(r.toInt(), g.toInt(), b.toInt())
//        }
//        return res
//    }
//
//    private fun check(c: Double): Double {
//        if (c < 0)
//            return 0.0
//        if (c > 255) {
//            return 255.0
//        }
//        return c
//    }
//
//    override fun toString(): String {
//        var res = "Colormap " + name + " with " + clrs.size + " colors: { "
//        for (i in 0..clrs.size - 1) {
//            res += clrs[i].toString() + " "
//        }
//        return res + "}"
//    }
//
//    class object {
//
//        public fun getMap(nm: String): ColorMap {
//            var res = maps[0]
//            for (i in 0..maps.size - 1) {
//                if (nm.equalsIgnoreCase(maps[i].name)) {
//                    res = maps[i]
//                    break
//                }
//            }
//            //         Main.debug.println("set map to " + res);
//            return res
//        }
//        // colorcube contains as many regularly spaced colors in RGB color space as
//        //      possible, while attempting to provide more steps of gray, pure red,
//        //      pure green, and pure blue.
//        // flag consists of the colors red, white, blue, and black. This colormap
//        //      completely changes color with each index increment.
//        // lines produces a colormap of colors specified by the axes ColorOrder
//        //      property and a shade of gray.
//        // pink contains pastel shades of pink. The pink colormap provides sepia tone
//        //      colorization of grayscale photographs.
//        // prism repeats the six colors red, orange, yellow, green, blue, and violet.
//        // white is an all white monochrome colormap.
//        // jet ranges from blue to red, and passes through the colors cyan, yellow, and
//        //      orange. It is a variation of the hsv colormap.
//        //      The jet colormap is associated with an astrophysical fluid jet simulation
//        //      from the National Center for Supercomputer Applications.
//        public val jet: Array<Color> = array<Color>(Color.blue, Color.red, Color.cyan, Color.yellow, Color(255, 128, 0))
//        // hsv varies the hue component of the hue-saturation-value color model.
//        //      The colors begin with red, pass through yellow, green, cyan, blue,
//        //      magenta, and return to red. The colormap is particularly appropriate
//        //      for displaying periodic functions. hsv(m) is the same as
//        //      hsv2rgb([h ones(m,2)]) where h is the linear ramp, h = (0:m–1)'/m.
//        public val HSV: Array<Color> = array<Color>(Color.red, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta, Color.red)
//        // hot varies smoothly from black through shades of red, orange, and yellow,
//        //      to white.
//        public val hot: Array<Color> = array<Color>(Color.black, Color.red, Color(255, 128, 0), Color.yellow, Color.white)
//        // cool consists of colors that are shades of cyan and magenta.
//        //      It varies smoothly from cyan to magenta.
//        public val cool: Array<Color> = array<Color>(Color.cyan, Color.magenta)
//        // spring consists of colors that are shades of magenta and yellow.
//        public val spring: Array<Color> = array<Color>(Color.magenta, Color.yellow)
//        // summer consists of colors that are shades of green and yellow.
//        public val summer: Array<Color> = array<Color>(Color.green, Color.yellow)
//        // autumn varies smoothly from red, through orange, to yellow.
//        public val autumn: Array<Color> = array<Color>(Color.red, Color(255, 128, 0), Color.yellow)
//        // winter consists of colors that are shades of blue and green.
//        public val winter: Array<Color> = array<Color>(Color.blue, Color.green)
//        // gray returns a linear grayscale colormap.
//        public val gray: Array<Color> = array<Color>(Color.black, Color.white)
//        // bone is a grayscale colormap with a higher value for the blue component.
//        //      This colormap is useful for adding an "electronic" look to grayscale
//        //      images.
//        public val bone: Array<Color> = array<Color>(Color(0, 0, 50), Color.white)
//        // copper varies smoothly from black to bright copper.
//        public val copper: Array<Color> = array<Color>(Color.black, Color(220, 120, 80))
//        // pink contains pastel shades of pink. The pink colormap provides sepia
//        //      tone colorization of grayscale photographs.
//        // prism repeats the six colors red, orange, yellow, green, blue, and violet.
//        // white is an all white monochrome colormap.
//        public val pink: Array<Color> = array<Color>(Color(255, 128, 128), Color.white)
//        // prism repeats the six colors red, orange, yellow, green, blue, and violet.
//        // white is an all white monochrome colormap.
//        public val prism: Array<Color> = array<Color>(Color.red, Color(255, 128, 0), Color.yellow, Color.green, Color.cyan, Color.blue, Color(64, 0, 75))
//        // white is an all white monochrome colormap.
//        public val white: Array<Color> = array<Color>(Color.white, Color.white)
//
//        public val maps: Array<ColorMap> = array<ColorMap>(
//                ColorMap("jet", jet),
//                ColorMap("HSV", HSV),
//                ColorMap("hot", hot),
//                ColorMap("cool", cool),
//                ColorMap("spring", spring),
//                ColorMap("summer", summer),
//                ColorMap("autumn", autumn),
//                ColorMap("winter", winter),
//                ColorMap("gray", gray),
//                ColorMap("bone", bone),
//                ColorMap("copper", copper),
//                ColorMap("pink", pink),
//                ColorMap("prism", prism),
//                ColorMap("white", white))
//    }
//}
