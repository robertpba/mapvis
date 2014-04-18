package mapvis.vistools.colormap;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;

public class GenericColorMapTest {

    @Test
    public void testGetColor1() throws Exception {
        GenericColorMap jet = new GenericColorMap(
                new Color[]{
                        new Color(0.0f, 0.0f, 0.0f), new Color(0.0f, 0.0f, 1.0f),
                        new Color(0.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 0.0f),
                        new Color(1.0f, 0.0f, 0.0f), new Color(0.0f, 0.0f, 0.0f)},
                new float[]{
                        -0.125f, 0.125f,
                        0.375f, 0.625f,
                        0.875f, 1.125f}
        );

        Color c0 = jet.getColor(0.0);
        Assert.assertTrue(close(c0,new Color(0.0f, 0.0f, 0.5f)));

        Color c1 = jet.getColor(0.5);
        Assert.assertTrue(close(c1,new Color(0.5f, 1.0f, 0.5f)));

        Color c2 = jet.getColor(1.0);
        Assert.assertTrue(close(c2,new Color(0.5f, 0.0f, 0.0f)));
    }

    @Test
    public void testGetColor2() throws Exception {
        GenericColorMap jet = new GenericColorMap(
                new Color[]{
                        new Color(0.0f, 0.0f, 0.0f), new Color(0.0f, 0.0f, 1.0f),
                        new Color(0.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 0.0f),
                        new Color(1.0f, 0.0f, 0.0f), new Color(0.0f, 0.0f, 0.0f)},
                -0.125f, 1.125f);

        Color c0 = jet.getColor(0.0);
        Assert.assertTrue(close(c0,new Color(0.0f, 0.0f, 0.5f)));

        Color c1 = jet.getColor(0.5);
        Assert.assertTrue(close(c1,new Color(0.5f, 1.0f, 0.5f)));

        Color c2 = jet.getColor(1.0);
        Assert.assertTrue(close(c2,new Color(0.5f, 0.0f, 0.0f)));
    }


    private boolean close(Color c1, Color c2){
        if (Math.abs(c1.getRed() - c2.getRed()) > 1)
            return false;
        if (Math.abs(c1.getBlue() - c2.getBlue()) > 1)
            return false;
        if (Math.abs(c1.getGreen() - c2.getGreen()) > 1)
            return false;
        return true;
    }

}
