package mapvis.common;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class SpatialIndex<T> {
    Set<T>[] buckets;
    Map<T, Rectangle2D> pos;
    private int nCols;
    private int nRows;
    private double cellSize;

    public SpatialIndex(int nCols, int nRows, double size){
        this.nCols = nCols;
        this.nRows = nRows;
        this.cellSize = size;
        @SuppressWarnings("unchecked")
        final Set<T>[] buckets = new Set[ nCols * nRows];
        this.buckets = buckets;

        for (int x = 0; x < nCols; x++)
            for (int y = 0; y < nRows; y++){
                buckets[x*nCols + y] = new HashSet<>();
            }

        pos = new HashMap<>();
    }

    public void update(T node, Rectangle2D bounds){
        remove(node);
        add(node, bounds);
    }

    public void remove(T node){
        Rectangle2D bounds = pos.get(node);
        if (bounds == null)
            return;

        int minX = Math.min(nCols-1, Math.max(0, (int) (bounds.getMinX() / cellSize)));
        int maxX = Math.min(nCols-1, Math.max(0, (int) (bounds.getMaxX() / cellSize)));
        int minY = Math.min(nRows-1, Math.max(0, (int) (bounds.getMinY() / cellSize)));
        int maxY = Math.min(nRows-1, Math.max(0, (int) (bounds.getMaxY() / cellSize)));

        int x; int y = minY;
        do {
            x = minX;
            do
                buckets[x + y * nRows].remove(node);
            while (++x <= maxX);
        } while (++y <= maxY);


        pos.remove(node);
    }
    public void add(T node, Rectangle2D bounds){
        int minX = Math.min(nCols-1, Math.max(0, (int) (bounds.getMinX() / cellSize)));
        int maxX = Math.min(nCols-1, Math.max(0, (int) (bounds.getMaxX() / cellSize)));
        int minY = Math.min(nRows-1, Math.max(0, (int) (bounds.getMinY() / cellSize)));
        int maxY = Math.min(nRows-1, Math.max(0, (int) (bounds.getMaxY() / cellSize)));

        int x; int y = minY;
        do {
            x = minX;
            do
                buckets[x + y * nRows].add(node);
            while (++x <= maxX);
        } while (++y <= maxY);

        pos.put(node, bounds);
    }

    public Set<T> neighbours(Point2D point){
        int x = Math.min(nCols -1, Math.max(0, (int) (point.getX() / cellSize)));
        int y = Math.min(nRows -1, Math.max(0, (int) (point.getY() / cellSize)));

        return Collections.unmodifiableSet(buckets[x + y * nRows]);
    }


}

