package mapvis.layouts.pea.gui.actions;

import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.MapModel;

import java.awt.*;

public class LevelEncoder<T> implements RenderAction {
    private boolean initialized =false;
    private MapModel<T> model;

    public LevelEncoder(MapModel<T> model){
        this.model = model;
    }

    @Override
    public void update() {
        if (initialized)
            return;

        updateLevel(model.getRoot(), 0);
        initialized = true;
    }

    private void updateLevel(T node, int level){
        model.setValue(node, "__level", level);
        final int lv = level + 1;
        model.getChildren(node)
                .forEach(n -> updateLevel(n, lv));
    }

    @Override
    public void draw(Graphics2D g) {

    }
}