package mapvis.utils;

public class Helper {
    public static float interpolate(float x, float x0, float y0, float y1, float x1){
        return (x -x0)*(y1-y0)/(x1-x0) + y0;
    }
    public static double interpolate(double x, double x0, double y0, double y1, double x1){
        return (x -x0)*(y1-y0)/(x1-x0) + y0;
    }
}
