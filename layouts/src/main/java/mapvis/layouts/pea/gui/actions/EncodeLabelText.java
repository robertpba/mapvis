package mapvis.layouts.pea.gui.actions;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.MapModel;

import java.awt.*;
import java.util.function.Function;

public class EncodeLabelText implements RenderAction {
    private boolean initialized =false;
    private MapModel model;
    private Function<Node, String> getLabel;

    public EncodeLabelText(MapModel model, Function<Node, String> getLabel){

        this.model = model;
        this.getLabel = getLabel;
    }

    @Override
    public void update() {
        if (initialized)
            return;

        for (Node t : model.getAllNodes()) {
            model.setValue(t, "__label.text", getLabel.apply(t));
        }
        initialized = false;
    }

    @Override
    public void draw(Graphics2D g) {

    }
}
