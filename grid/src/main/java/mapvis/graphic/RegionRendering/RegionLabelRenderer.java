package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.models.IBoundaryShape;
import mapvis.models.Region;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 11/12/2015.
 * This class is used to render the labels of the Regions. The
 * location of calculated as the center of all coordinates of the
 * BoundaryShapes defining the border of the region.
 */
public class RegionLabelRenderer {

    private final GraphicsContext graphicsContext;
    private Map<INode, Point2D> iNodeToLabelPos;

    public RegionLabelRenderer(GraphicsContext graphicsContext2D) {
        this.graphicsContext = graphicsContext2D;
        this.iNodeToLabelPos = new HashMap<>();
    }

    public void drawLabels(IRegionStyler<INode> regionStyler, Region<INode> region, List<List<IBoundaryShape<INode>>> boundaryShapes) {
//        graphicsContext.save();
        if(!regionStyler.isLabelVisible(region)){
            return;
        }

        Point2D labelPos = null;
        INode nodeItem = region.getNodeItem();
        //calc the location or reuse previously calculated position
        if(!iNodeToLabelPos.containsKey(nodeItem)){
            labelPos = calcLabelPos(boundaryShapes);
            iNodeToLabelPos.put(nodeItem, labelPos);
        }else{
            labelPos = iNodeToLabelPos.get(nodeItem );
        }

        graphicsContext.setFont(new Font(graphicsContext.getFont().getName(), 25));
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(2.5);
        graphicsContext.strokeText(nodeItem.getLabel(), labelPos.getX(), labelPos.getY());
//        graphicsContext.restore();
    }


    private Point2D calcLabelPos(List<List<IBoundaryShape<INode>>> regionBoundaryShapes) {

        Point2D sumOfCoordinates = new Point2D(0, 0);
        int numOfCoords = 0;
        //calc the position as the center of all coordinates of the IBoundaryShapes
        for (List<IBoundaryShape<INode>> regionIBoundaryShape : regionBoundaryShapes) {
            if (regionIBoundaryShape.size() == 0)
                continue;

            for (IBoundaryShape<INode> partialRegionBoundary : regionIBoundaryShape) {
                for (Point2D currPoint : partialRegionBoundary) {
                    sumOfCoordinates = sumOfCoordinates.add(currPoint);
                    numOfCoords++;
                }
            }
        }

        //avoid mutliplication by 1.0/0
        if(numOfCoords == 0)
            return sumOfCoordinates;

        return sumOfCoordinates.multiply(1.0 / numOfCoords);
    }
}
