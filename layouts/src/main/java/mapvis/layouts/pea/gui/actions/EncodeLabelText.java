package mapvis.layouts.pea.gui.actions;

import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.MapModel;

import java.awt.*;
import java.util.function.Function;

public class EncodeLabelText<T> implements RenderAction {
    private boolean initialized =false;
    private MapModel<T> model;
    private Function<T, String> getLabel;

    public EncodeLabelText(MapModel<T> model, Function<T, String> getLabel){

        this.model = model;
        this.getLabel = getLabel;
    }

    @Override
    public void update() {
        if (initialized)
            return;

        for (T t : model.getAllNodes()) {
            model.setValue(t, "__label.text", getLabel.apply(t));
        }
        initialized = false;
    }

    @Override
    public void draw(Graphics2D g) {

    }
}
