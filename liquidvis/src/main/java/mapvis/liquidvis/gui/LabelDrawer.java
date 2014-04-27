package mapvis.liquidvis.gui;

import algorithm.FTAOverlapRemoval;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.stream.Collectors;

public class LabelDrawer<T> {
    private ToLabel<T> toLabel;

    class Entry{
        public Entry (T element){
            this.element = element;
        }

        public T element;
        public String text;
        public Rectangle2D bounds;
        private Point2D anchor;
        public int level;

        public Font font;
        public Rectangle2D textRect;
        public Point2D textOrgin;
    }

    public interface ToLabel<T> {
        Rectangle2D bounds(T node);
        int level(T node);
        String text(T node);
        Point2D anchor(T node);
    }

    Map<T, Entry> entries = new HashMap<>();

    public LabelDrawer(Collection<T> items,
                          ToLabel<T> toLabel){
        this.toLabel = toLabel;
        entries = items.stream()
                .map(n -> new Entry(n))
        .collect(Collectors.toMap(e -> e.element, e -> e));
        update();
    }

    public void update() {
        FontRenderContext ctx = new FontRenderContext(null, false, false);
        for (Entry entry : entries.values()) {
            entry.text = toLabel.text(entry.element);
            entry.level = toLabel.level(entry.element);
            entry.bounds = toLabel.bounds(entry.element);
            entry.anchor = toLabel.anchor(entry.element);

            if (entry.level == 0){
                entry.font = new Font("Arial", Font.BOLD, 30);
                entry.anchor = new Point.Double(-10, -10);
            }
            if (entry.level == 1){
                entry.font = new Font("Arial", Font.BOLD, 30);
            }
            if (entry.level == 2){
                entry.font = new Font("Arial", Font.ITALIC, 18);
            }
            if (entry.level == 3){
                entry.font = new Font("Arial", Font.PLAIN, 15);
            }

            Rectangle2D strBounds = entry.font.getStringBounds(entry.text, ctx);
            entry.textRect = new Rectangle2D.Double(
                    -strBounds.getWidth()/2  + entry.anchor.getX(),
                    -strBounds.getHeight()/2 + entry.anchor.getY(),
                    strBounds.getWidth(),strBounds.getHeight());
            entry.textOrgin = new Point2D.Double(strBounds.getX(), strBounds.getY());
        }
    }

    public void layout(){

        FTAOverlapRemoval<Entry> removal = new FTAOverlapRemoval<>( entries.values(), e -> e.textRect);
        removal.run();

        for (Entry entry : entries.values()) {
            entry.textRect =  removal.getRectangle(entry);
        }
    }

    public void draw(Graphics2D g){
        layout();

        for (Entry entry : entries.values()) {
            if (entry.level == 3)
                renderLabel(g, entry);
        }
        for (Entry entry : entries.values()) {
            if (entry.level == 2)
                renderLabel(g, entry);
        }
        for (Entry entry : entries.values()) {
            if (entry.level == 1)
                renderLabel(g, entry);
        }
    }

    public void renderLabel(Graphics2D g, Entry entry){
        if (entry.level == 0){
            return;
        }
        if (entry.level == 1){
            g.setFont(entry.font);
            g.setColor(Color.BLACK);

            g.drawString(entry.text,
                    (float)(entry.textRect.getX() - entry.textOrgin.getX()),
                    (float)(entry.textRect.getY() - entry.textOrgin.getY()));
        }
        if (entry.level == 2){

            g.setFont(entry.font);
            g.setColor(new Color(156, 93, 82));

            g.drawString(entry.text,
                    (float)(entry.textRect.getX() - entry.textOrgin.getX()),
                    (float)(entry.textRect.getY() - entry.textOrgin.getY()));
        }
        if (entry.level == 3){
            g.setFont(entry.font);
            g.setColor(Color.BLACK);

            g.drawString(entry.text,
                    (float)(entry.textRect.getX() - entry.textOrgin.getX()),
                    (float)(entry.textRect.getY() - entry.textOrgin.getY()));
        }
    }
}
