package mapvis.liquidvis.gui;

import algorithm.FTAOverlapRemoval;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LabelDrawer<T> {

    class Entry{
        Entry(T node, String label, Rectangle bound, int level) {
            this.node = node;
            this.label = label;
            this.bound = bound;
            this.level = level;
        }

        public T node;
        public String label;
        public Rectangle bound;
        public int level;

        public Font font;
        public Rectangle textRect;
    }
    public interface ToBoundRectangle<T> {
        Rectangle getRectangle(T node);
    }
    public interface ToLevel<T> {
        int getLevel(T node);
    }
    public interface ToLabel<T> {
        String getLabel(T node);
    }

    List<Entry> entries;

    public LabelDrawer(Collection<T> nodes,
                          ToLabel<T> toLabel,
                          ToLevel<T> toLevel,
                          ToBoundRectangle<T> toBoundRectangle){
        entries = nodes.stream()
                .map(n -> new Entry(n,
                        toLabel.getLabel(n),
                        toBoundRectangle.getRectangle(n),
                        toLevel.getLevel(n)))
        .collect(Collectors.toList());
    }

    public void calcTextAttribute(Graphics2D g, Entry entry){
        if (entry.level == 0){
            entry.textRect = new Rectangle();
            return;
        }
        if (entry.level == 1){
            Rectangle bounds = entry.bound;

            entry.font = new Font("Arial", Font.BOLD, 30);

            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            FontMetrics fontMetrics = g.getFontMetrics();
            int width = fontMetrics.stringWidth(entry.label);
            int height = fontMetrics.getHeight();

            entry.textRect = new Rectangle((int)(centerX-width/2),(int)(centerY-height/2),
                    width, height);
        }
        if (entry.level == 2){
            Rectangle bounds = entry.bound;

            entry.font = new Font("Arial", Font.ITALIC, 15);

            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            FontMetrics fontMetrics = g.getFontMetrics();
            int width = fontMetrics.stringWidth(entry.label);
            int height = fontMetrics.getHeight();

            entry.textRect = new Rectangle((int)(centerX-width/2),(int)(centerY-height/2),
                    width, height);
        }
        if (entry.level == 3){
            Rectangle bounds = entry.bound;

            entry.font = new Font("Arial", Font.PLAIN, 10);

            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            FontMetrics fontMetrics = g.getFontMetrics();
            int width = fontMetrics.stringWidth(entry.label);
            int height = fontMetrics.getHeight();

            entry.textRect = new Rectangle((int)(centerX-width/2),(int)(centerY-height/2),
                    width, height);
        }
    }

    public void layout(){
        FTAOverlapRemoval<Entry> removal = new FTAOverlapRemoval<>(entries, e -> e.textRect);
        removal.run();
        for (Entry entry : entries) {
            entry.textRect =  removal.getRectangle(entry);
        }
    }

    public void draw(Graphics2D g){
        for (Entry entry : entries) {
            calcTextAttribute(g, entry);
        }
        layout();

        for (Entry entry : entries) {
            if (entry.level == 0){
                return;
            }
            if (entry.level == 1){
                g.setFont(entry.font);
                g.setColor(Color.BLACK);

                g.drawString(entry.label, entry.textRect.x, entry.textRect.y);
                g.drawLine(entry.textRect.x, entry.textRect.y,
                        entry.textRect.x+entry.textRect.width, entry.textRect.y);
            }
            if (entry.level == 2){
                g.setFont(entry.font);
                g.setColor(Color.BLACK);

                g.drawString(entry.label, entry.textRect.x, entry.textRect.y);
            }
            if (entry.level == 3){
                g.setFont(entry.font);
                g.setColor(Color.BLACK);

                g.drawString(entry.label, entry.textRect.x, entry.textRect.y);
            }
        }
    }

}
