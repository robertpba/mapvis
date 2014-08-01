package mapvis.commons.algorithm;


import java.awt.geom.Point2D;
import java.util.*;

public class CircleOverlapRemoval<T> {

    public interface PositionTransformer<T> {
        Point2D getPosition(T node);
    }

    public interface RadiusTransformer<T> {
        double getRadius(T node);
    }

    public class Entry {
        double radius;
        Point2D position;
        T element;

        public Entry(T element, double radius, Point2D position) {
            this.element = element;
            this.radius = radius;
            this.position = position;
        }
    }

    private Map<T, Entry> entries = new HashMap<>();

    public CircleOverlapRemoval(Collection<T> nodes,
                                PositionTransformer<T> toPosition,
                                RadiusTransformer<T> toRadius) {
        for (T node : nodes) {
            double radius = toRadius.getRadius(node);
            Point2D position = toPosition.getPosition(node);
            entries.put(node, new Entry(node, radius, position));
        }
    }

    public void run(int times) {
        while (times-- > 0) {
            if (!iterateOnce())
                break;
        }
    }

    public Point2D getPosition(T node) {
        return entries.get(node).position;
    }

    private boolean iterateOnce() {
        boolean update = false;
        for (Entry entry1 : entries.values()) {
            Point2D pos = (Point2D) entry1.position.clone();
            for (Entry entry2 : entries.values()) {
                if (entry1 == entry2)
                    continue;
                updatePos(entry1, entry2, pos);
            }
            if (!pos.equals(entry1.position)) {
                entry1.position = pos;
                update = true;
            }
        }
        return update;
    }

    private void updatePos(Entry e1, Entry e2, Point2D pos) {
        double dx = e2.position.getX() - e1.position.getX();
        double dy = e2.position.getY() - e1.position.getY();
        double d = Math.sqrt(dx * dx + dy * dy);
        double r = e1.radius + e2.radius;
        double deltaR = d - r;

        if (d > r)
            return;

        double deltaX = dx / d * deltaR;
        double deltaY = dy / d * deltaR;
        pos.setLocation(pos.getX() + deltaX, pos.getY() + deltaY);
    }
}
