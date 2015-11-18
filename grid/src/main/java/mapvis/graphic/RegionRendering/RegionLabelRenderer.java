package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;
import mapvis.models.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 11/12/2015.
 */
public class RegionLabelRenderer {

    private final GraphicsContext graphicsContext;
    private Map<INode, Point2D> iNodeToLabelPos;

    public RegionLabelRenderer(GraphicsContext graphicsContext2D) {
        this.graphicsContext = graphicsContext2D;
        this.iNodeToLabelPos = new HashMap<>();
    }

    public void drawLabels(IRegionStyler<INode> regionStyler, Region<INode> region, List<List<LeafRegion.BoundaryShape>> boundaryShapes) {
//        graphicsContext.save();
        if(!regionStyler.isLabelVisible(region)){
            return;
        }
        Point2D labelPos = null;
        INode nodeItem = region.getNodeItem();
        if(!iNodeToLabelPos.containsKey(nodeItem)){
            labelPos = calcLabelPos(boundaryShapes);
            iNodeToLabelPos.put(nodeItem, labelPos);
        }else{
            labelPos = iNodeToLabelPos.get(nodeItem );
        }
        graphicsContext.setFont(new Font(graphicsContext.getFont().getName(), 30));
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(3);
        graphicsContext.strokeText(nodeItem.getLabel(), labelPos.getX(), labelPos.getY());
//        graphicsContext.restore();
    }

//    private Point2D calcLabelPos(Region<INode> region) {
//        region.g
//    calc label position just by using the position of the tiles
//    }

    private Point2D calcLabelPos(List<List<LeafRegion.BoundaryShape>> regionBoundaryShapes) {
        double sumXValues = 0;
        double sumYValues = 0;
        int numOfCoords = 0;
        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {
            if (regionBoundaryShape.size() == 0)
                continue;

            for (LeafRegion.BoundaryShape partialRegionBoundary : regionBoundaryShape) {
                for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
                    double xValue = partialRegionBoundary.getXValueAtIndex(i);
                    double yValue = partialRegionBoundary.getYValueAtIndex(i);
                    sumXValues += xValue;
                    sumYValues += yValue;
                    numOfCoords++;
                }
            }
        }
        return new Point2D(sumXValues/numOfCoords, sumYValues/numOfCoords);
    }
}
