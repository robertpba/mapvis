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
                double length = length(dx, dy);
                final double dX = dx/length;
                final double dY = dy/length;

                if (Math.abs(dx) > 1) {
                    if (!root.children.stream().anyMatch(e -> e != child
                            && child.overlap(e, dX, 0))) {
                        child.move(dX,0);
                        moving=true;
                    }
                }
                if (Math.abs(dy) > 1) {
                    if (!root.children.stream().anyMatch(e -> e != child
                            && child.overlap(e, 0, dY))) {
                        child.move(0,dY);
                        moving=true;
                    }
                }
            }
        }

        root.updateBounds();
    }
}
