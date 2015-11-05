package mapvis.graphic;

import javafx.geometry.Point2D;
import mapvis.common.datatype.Tuple2;
import mapvis.models.Border;
import mapvis.models.Dir;
import mapvis.models.LeafRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BorderCoordinatesCalculatorImpl<T> implements IBorderCoordinatesCalculator<T> {
    private LeafRegion<T> leafRegion;

    public BorderCoordinatesCalculatorImpl() {
    }

    @Override
    public void setLeafRegion(LeafRegion<T> leafRegion) {
        this.leafRegion = leafRegion;
    }

    @Override
    public List<Tuple2<Border<T>, LeafRegion.BoundaryShape>> computeCoordinates() {
        if(leafRegion == null)
            return Collections.emptyList();

        List<Double> xValues = new ArrayList<Double>();
        List<Double> yValues = new ArrayList<Double>();
        List<LeafRegion.BoundaryShape> computedBoundaryShapes = new ArrayList<LeafRegion.BoundaryShape>();
        List<Tuple2<Border<T>, LeafRegion.BoundaryShape>> result = new ArrayList<Tuple2<Border<T>, LeafRegion.BoundaryShape>>();

        List<String> descriptionTexts = new ArrayList<String>();
        for (Border<T> border : leafRegion.getBorders()) {
            if (border.getBorderItems().size() == 0) {
                continue;
            }
            for (Border.BorderItem borderItem : border.getBorderItems()) {

                int x = borderItem.borderItem.first.getX();
                int y = borderItem.borderItem.first.getY();

                Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);

                for (Dir direction : borderItem.borderItem.second) {

                    int[] pointIndices = LeafRegion.DIR_TO_POINTS[direction.ordinal()];
                    double xStart = LeafRegion.POINTS[pointIndices[0]] + point2D.getX();
                    double yStart = LeafRegion.POINTS[pointIndices[1]] + point2D.getY();

                    Point2D startPoint = LeafRegion.roundToCoordinatesTo4Digits(new Point2D(xStart, yStart));

                    xValues.add(startPoint.getX());
                    yValues.add(startPoint.getY());
                }
            }
            LeafRegion.BoundaryShape boundaryShape = new LeafRegion.BoundaryShape(
                    xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                    yValues.stream().mapToDouble(Double::doubleValue).toArray());
            boundaryShape.text = descriptionTexts;

            boundaryShape.level = border.getLevel();
            result.add(new Tuple2<Border<T>, LeafRegion.BoundaryShape>(border, boundaryShape));
//            computedBoundaryShapes.add(boundaryShape);

//            System.out.println("X");
//            for (Double xValue : xValues) {
//                System.out.println(xValue);
//            }
//            System.out.println("Y");
//            for (Double yValue : yValues) {
//                System.out.println(yValue);
//            }
            xValues.clear();
            yValues.clear();
        }
//        return computedBoundaryShapes;
        return result;
    }
}