package mapvis.layouts.dac;

import java.util.List;

public class Entry<T> {
    public T node;
    public double x;
    public double y;
    public double r;
    public int level;

    public double x0, y0; // top x,y
    public double x1, y1; // bottom x,y

    public List<Entry<T>> children;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        return node.equals(entry.node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    // coordinates of this lhs is adjusted by dx and dy
    public boolean overlap(Entry<T> rhs, double dx, double dy) {
        return !(x1 + dx < rhs.x0 || x0 + dx > rhs.x1
                || y1 + dy < rhs.y0 || y0 + dy > rhs.y1);
    }

    public static double
    distance (Entry e1, Entry e2){
        double dx = e1.x - e2.x;
        double dy = e1.y - e2.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public void move(double dx, double dy) {
        x += dx; y += dy;
        x0 += dx; y0 += dy;
        x1 += dx; y1 += dy;
        children.forEach(c -> c.move(dx, dy));
    }

    // not recursively
    public void updateBounds(){
        if (children.isEmpty()) {
            x0 = x - r;
            x1 = x + r;
            y0 = y - r;
            y1 = y + r;
            return;
        }

        x0 = children.stream().mapToDouble(n->n.x0).min().getAsDouble();
        x1 = children.stream().mapToDouble(n->n.x1).max().getAsDouble();
        y0 = children.stream().mapToDouble(n->n.y0).min().getAsDouble();
        y1 = children.stream().mapToDouble(n->n.y1).max().getAsDouble();
    }
}
