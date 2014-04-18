package algorithm;
import java.util.*;


import java.awt.Rectangle;

public class FTAOverlapRemoval<T> {
    public interface ToRectangle<T> {
        Rectangle getRectangle(T obj);
    }
    public class Entry {
        public T element;
        public Rectangle rect;

        public Entry(T element, Rectangle rect) {
            this.element = element;
            this.rect = rect;
        }
    }

    protected enum Direction { RIGHT, LEFT, UP, DOWN }

    private int xGap = 0;
    private int yGap = 0;

    private List<Entry> entries = new ArrayList<>();
    private Map<T,Entry> map = new HashMap<>();

    public FTAOverlapRemoval(List<T> elementList, ToRectangle<T> toRectangle) {
        for (T obj: elementList) {
            Entry entry = new Entry(obj, toRectangle.getRectangle(obj));
            entries.add(entry);
            map.put(obj,entry);
        }
    }

    public Rectangle getRectangle(T obj){
        return map.get(obj).rect;
    }

    public void run() {
        rightHorizontalScan();
        leftHorizontalScan();
        upVerticalScan();
        downVerticalScan();
    }

    private void rightHorizontalScan() {
        Collections.sort(entries, (a,b)-> a.rect.x - b.rect.x);
        for (Entry vi : entries) {
            Set<Entry> rns = findNS(vi, Direction.RIGHT);
            if (!rns.isEmpty()) {
                Set<Entry> rtns = findTNS(vi, Direction.RIGHT);
                double f = 0;
                for (Entry vj : rns) {
                    double fx = Math.abs(vi.rect.getMaxX() - vj.rect.getX());
                    double fy = Math.min(Math.abs(vi.rect.getMaxY() - vj.rect.getY()),
                            Math.abs(vi.rect.getY() - vj.rect.getMaxY()));
                    double delta = Math.min(fx, fy);
                    if (delta == fx && (delta < f || f == 0))
                        f = delta;
                }
                if (f != 0) {
                    for (Entry e : rtns)
                        e.rect.x += f + xGap;
                }
            }
        }
    }

    private void leftHorizontalScan() {
        Collections.sort(entries, (a,b)-> a.rect.x - b.rect.x);
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry vi = entries.get(i);
            Set<Entry> lns = findNS(vi, Direction.LEFT);
            if (!lns.isEmpty()) {
                Set<Entry> ltns = findTNS(vi, Direction.LEFT);
                double f = 0;
                for (Entry vj: lns) {
                    double fx = Math.abs(vi.rect.getX() - vj.rect.getMaxX());
                    double fy = Math.min(Math.abs(vi.rect.getY() - vj.rect.getMaxY()),
                            Math.abs(vi.rect.getMaxY() - vj.rect.getY()));
                    double delta = Math.min(fx, fy);
                    if (delta == fx && (delta > f || f == 0))
                        f = delta;
                }
                if (f != 0) {
                    for (Entry e: ltns)
                        e.rect.x -= f - xGap;
                }
            }
        }
    }

    private void upVerticalScan() {
        Collections.sort(entries, (a,b)-> a.rect.y - b.rect.y);
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry vi = entries.get(i);
            Set<Entry> uns = findNS(vi, Direction.UP);
            if (!uns.isEmpty()) {
                Set<Entry> utns = findTNS(vi, Direction.UP);
                double f = 0;
                for (Entry vj: uns) {
                    double fy1 = Math.abs(vi.rect.getMaxY() - vj.rect.getY());
                    double fy2 = Math.abs(vi.rect.getY() - vj.rect.getMaxY());
                    double delta = Math.min(fy1, fy2);
                    if (delta == fy1 && (delta > f || f == 0))
                        f = delta;
                }
                if (f != 0) {
                    for (Entry e: utns)
                        e.rect.y -= f - yGap;
                }
            }
        }
    }

    private void downVerticalScan() {
        Collections.sort(entries, (a,b)-> a.rect.y - b.rect.y);
        for (Entry vi : entries) {
            Set<Entry> dns = findNS(vi, Direction.DOWN);
            if (!dns.isEmpty()) {
                Set<Entry> dtns = findTNS(vi, Direction.DOWN);
                double f = 0;
                for (Entry vj : dns) {
                    double fy1 = Math.abs(vi.rect.getY() - vj.rect.getMaxY());
                    double fy2 = Math.abs(vi.rect.getMaxY() - vj.rect.getY());
                    double delta = Math.min(fy1, fy2);
                    if (delta == fy2 && (delta > f || f == 0))
                        f = delta;
                }
                if (f != 0) {
                    for (Entry e : dtns)
                        e.rect.y += f + yGap;
                }
            }
        }
    }

    private static boolean intersects(Rectangle r1, Rectangle r2) {
        return r1 != null && r1.intersects(r2);
    }

    private Set<Entry> findNS(Entry argEntry, Direction dir) {
        HashSet<Entry> nsSet = new HashSet<>();
        for (Entry e: entries) {
            Rectangle r = e.rect;
            Rectangle rect = argEntry.rect;
            if (!rect.equals(r) && intersects(r, rect)) {
                if ((dir == Direction.LEFT && r.getX() < rect.getX()) ||
                        (dir == Direction.RIGHT && r.getX() >= rect.getX()) ||
                        (dir == Direction.UP && r.getY() < rect.getY()) ||
                        (dir == Direction.DOWN && r.getY() >= rect.getY()))
                    nsSet.add(e);
            }
        }
        return nsSet;
    }

    private Set<Entry> findTNS(Entry v, Direction dir) {
        Set<Entry> tnsSet = new HashSet<>();
        Set<Entry> oldSet = new HashSet<>();
        LinkedList<Entry> tempList = new LinkedList<>();
        int i = 1;
        while (true) {
            oldSet.clear();
            tempList.clear();
            if (i == 1) {
                Set<Entry> nsv = findNS(v, dir);
                for (Entry u: nsv)
                    tnsSet.addAll(findNS(u, dir));
                tnsSet.addAll(nsv);
                tnsSet.remove(v);
            } else {
                oldSet.addAll(tnsSet);
                for (Entry u: tnsSet)
                    tempList.addAll(findNS(u, dir));
                tnsSet.addAll(tempList);
                tnsSet.remove(v);
                if (tnsSet.equals(oldSet))
                    break;
            }
            i++;
        }
        return tnsSet;
    }
}
