package mapvis.graphic;

import java.util.List;

/**
 * Created by dacc on 11/12/2015.
 */
public class DebugUtils {
    public static void printCoordinates(List<Double> xValues, List<Double> yValues, String startText, String endText) {
        System.out.println(startText);
        System.out.println("XVal");
        for (Double xValue : xValues) {
            System.out.println(xValue);
        }
        System.out.println("YVal");
        for (Double xValue : yValues) {
            System.out.println(xValue);
        }
        System.out.println(endText);
    }
}
