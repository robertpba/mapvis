package mapvis.layouts.pea.gui.actions;

import mapvis.layouts.FTAOverlapRemoval;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class LabelRender<T> implements RenderAction {
    private MapModel<T> model;

    protected class Entry{
        public Entry (T element){
            this.element = element;
        }

        public T element;
        public String text;
        public Rectangle2D bounds;
        private Point2D anchor;
        public int level;

        public Label label;
        public Rectangle2D textRect;
        public Rectangle2D textRectOrig;

    }

    Map<T, Entry> entries = new HashMap<>();

    protected Rectangle2D getBounds(T node) {
        return ((Area) model.getValue(node, "__area")).getBounds2D();
    }
    protected int getLevel(T node) {
        return ((int) model.getValue(node, "__level"));
    }
    protected String getText(T node) {
        return ((String) model.getValue(node, "__label.text"));
    }
    protected Point2D getAnchor(T node) {
        mapvis.layouts.pea.model.Polygon polygon = model.getPolygon(node);

        if (polygon != null){
            return polygon.calcCentroid();
        }
        else {
            Rectangle2D area = ((Area) model.getValue(node, "__area")).getBounds2D();
            return new Point2D.Double(area.getCenterX(), area.getCenterY());
        }

    }

    public LabelRender(MapModel<T> model){
        this.model = model;
        entries = model.getLeaves().stream()
                .map(n -> new Entry(n))
                .collect(Collectors.toMap(e -> e.element, e -> e));
    }

    public void update() {
        entries = model.getAllNodes().stream()
                .filter(n -> getLevel(n) > 0)
                .filter(n -> getText(n) != null)
                .map(n -> new Entry(n))
                .collect(Collectors.toMap(e -> e.element, e -> e));

        for (Entry entry : entries.values()) {
            entry.text = getText(entry.element);
            entry.level = getLevel(entry.element);
            entry.bounds = getBounds(entry.element);
            entry.anchor = getAnchor(entry.element);

            Font font = getFont(entry.level);

            Label label = new Label();
            label.font = getFont(entry.level);
            label.text = String.join("\n", wrapLine(entry.level < 3 ? entry.text.toUpperCase(): entry.text));

            entry.label = label;

            Rectangle2D bounds = label.getBounds();
            label.setPosition(entry.anchor.getX() - bounds.getWidth()/2,
                    entry.anchor.getY() - bounds.getHeight() / 2);

            entry.textRect = label.getBounds();
            entry.textRectOrig = entry.textRect;
        }
        layout();
    }

    public void layout(){
        java.util.List<Entry> collect = entries.values().stream().filter(e -> e.level > 2).collect(Collectors.toList());
        FTAOverlapRemoval<Entry> removal = new FTAOverlapRemoval<>( collect, e -> e.textRect);
        removal.run();
        for (Entry entry : collect) {
            entry.textRect =  removal.getRectangle(entry);
            entry.label.setPosition(entry.textRect.getX(), entry.textRect.getY());
        }

        collect = entries.values().stream().filter(e -> e.level == 2).collect(Collectors.toList());
        removal = new FTAOverlapRemoval<>( collect, e -> e.textRect);
        removal.run();
        for (Entry entry : collect) {
            entry.textRect =  removal.getRectangle(entry);
            entry.label.setPosition(entry.textRect.getX(), entry.textRect.getY());
        }

        collect = entries.values().stream().filter(e -> e.level == 1).collect(Collectors.toList());
        removal = new FTAOverlapRemoval<>( collect, e -> e.textRect);
        removal.run();
        for (Entry entry : collect) {
            entry.textRect =  removal.getRectangle(entry);
            entry.label.setPosition(entry.textRect.getX(), entry.textRect.getY());
        }
    }

    public void draw(Graphics2D g){
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

    protected void renderLabel(Graphics2D g, Entry entry){
        if (entry.level == 0){
            return;
        }
        if (entry.level <= 3){
            g.setFont(getFont(entry.level));
            g.setColor(getColor(entry.level));

            entry.label.draw(g);
        }
    }

    private Font[] fonts;
    {
        Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

        fonts = new Font[] {
            new Font("Arial", Font.BOLD, 72).deriveFont(fontAttributes),
            new Font("Arial", Font.PLAIN, 26),
            new Font("Arial", Font.PLAIN, 24)
        };
    }

    protected Font getFont(int level){
        level = Math.min(3, level);
        level = Math.max(1, level);
        return fonts[level-1];
    }

    private Color[] colors = {Color.BLACK, Color.BLACK, Color.decode("#004000")};
    protected Color getColor(int level){
        level = Math.min(3, level);
        level = Math.max(1, level);
        return colors[level-1];
    }

    public static final int LINE_BREAK_LEN = 7;
    public static final int MAX_TAIL_LEN = 2;

    /**
     * Wrap a string using the length for one line defined in LINE_BREAK_LEN.
     * @param s String to be processed.
     * @return A string list with wrapped lines.
     */
    public static List<String> wrapLine(String s) {
        String[] broken = s.split(" ");
        ArrayList<String> list = new ArrayList<String>(broken.length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < broken.length; i++) {
            String current = broken[i];
            String next = i < broken.length - 1 ? broken[i + 1] : "";
            sb.append(current);
            if (sb.length() < LINE_BREAK_LEN || next.length() <= MAX_TAIL_LEN) {
                sb.append(" ");
            } else {
                list.add(sb.toString().trim());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0)
            list.add(sb.toString().trim());
        return list;
    }


    public static class Label {
        public Font font;
        public Color Color;
        public String text;
        public double posX, posY;

        Rectangle2D boundsCache;

        public void setPosition(double x, double y){
            posX = x;
            posY = y;
            boundsCache = null;
        }

        public Rectangle2D getBounds(){
            if (boundsCache != null)
                return boundsCache;


            FontRenderContext ctx = new FontRenderContext(null, false, false);

            double minX = posX, maxX = posX;
            double minY = posY, maxY = posY;
            double x=0.0, y=0.0;

            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length; i++) {
                LineMetrics metrics = font.getLineMetrics(lines[i], ctx);
                Rectangle2D bounds = font.getStringBounds(lines[i], ctx);

                if (i == 0) {
                    x = minX;
                    y = minY + metrics.getAscent();
                }
                maxX = Math.max(maxX, bounds.getMaxX() + x);
                maxY = Math.max(maxY, bounds.getMaxY() + y);

                y += metrics.getAscent() + metrics.getDescent();
            }

            return boundsCache = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
        }

        public void draw(Graphics2D g){
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics();
            String[] lines = text.split("\n");
            double x = posX, y = posY + metrics.getAscent();
            for (int i = 0; i < lines.length; i++) {
                g.drawString(lines[i], (float)x, (float)y);
                y += metrics.getAscent()+metrics.getDescent();
            }
        }
    }




}
