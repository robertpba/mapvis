package mapvis.layouts.dac;


public class Compact {

    static double length(double dx, double dy){
        return Math.sqrt(dx*dx+dy*dy);
    }


    public static <T> void compact(Entry<T> root){
        if (root.children.isEmpty()) {
            root.updateBounds();
            return;
        }

        root.children.forEach(Compact::compact);

        boolean moving = true;
        while (moving){
            moving=false;

            for (Entry<T> child : root.children) {
                double dx = root.x-child.x;
                double dy = root.y-child.y;

                if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
                    double length = length(dx, dy);
                    final double dX = dx/length/100;
                    final double dY = dy/length/100;

                    if (!root.children.stream().anyMatch(e -> e != child
                            && child.overlap(e, dX, dY))) {
                        child.move(dX,dY);
                        moving=true;
                    }
                }
            }
        }

        root.updateBounds();
    }
}
