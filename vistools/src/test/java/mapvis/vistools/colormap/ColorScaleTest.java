package mapvis.vistools.colormap;

import javax.swing.*;

public class ColorScaleTest {

    public static void main(String[] args){
        JFrame frame = new JFrame("Navigable Image Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        frame.getContentPane().add(new ColorScale(
                ColorMap.JET::getColor,
                new float[]{0.0f, 0.3f, 1.0f},
                new String[]{"o", "p", "q"}));


        frame.pack();
        frame.setVisible(true);
    }
}


