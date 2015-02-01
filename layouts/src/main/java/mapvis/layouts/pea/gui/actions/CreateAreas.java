package mapvis.layouts.pea.gui.actions;

import mapvis.common.datatype.Node;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.*;
import mapvis.layouts.pea.model.Polygon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;

public class CreateAreas implements RenderAction {
    private MapModel model;

    public CreateAreas(MapModel model){

        this.model = model;
    }

    @Override
    public void update() {
        updatePosition(model.getRoot());
    }

    @Override
    public void draw(Graphics2D g) {
    }

    private void updatePosition(Node node) {
        Collection<Node> children = model.getChildren(node);
        if (children.isEmpty()) {
            Polygon polygon = model.getPolygon(node);

            Path2D path = new Path2D.Double();
            for(int j=0; j<polygon.npoints; j++){
                Point2D pos = polygon.vertices[j].getPoint();
                int x = (int) pos.getX();
                int y = (int) pos.getY();
                if (j == 0)
                    path.moveTo(x, y);
                else
                    path.lineTo(x, y);
            }
            path.closePath();

            Area area = new Area(path);

            model.setValue(node, "__area", area);
        }
        else {
            Area area = new Area();

            for (Node childNode : children) {
                updatePosition(childNode);
                Area childArea = (Area) model.getValue(childNode, "__area");
                area.add(childArea);
            }
            model.setValue(node, "__area", area);
        }
    }

}
