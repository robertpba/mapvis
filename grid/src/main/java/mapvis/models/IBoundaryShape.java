package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;

import java.util.List;

/**
 * Created by dacc on 11/24/2015.
 */
public interface IBoundaryShape<T> {

    int getLevel();

    Border<T> getFirstBorder();

    Tuple2<T, T> getSeperatedRegionsID(int maxLevel, Tree2<T> tree);

    int getShapeLength();

    Point2D getStartPoint();

    Point2D getEndPoint();

    double getXCoordinateEndpoint();

    double getYCoordinateEndpoint();

    double getXCoordinateStartpoint();

    double getYCoordinateStartpoint();

    double getXCoordinateAtIndex(int index);

    double getYCoordinateAtIndex(int index);

    void setXCoordinateAtIndex(int index, double value);

    void setYCoordinateAtIndex(int index, double value);

    List<Double> getXCoords();

    List<Double> getYCoords();

    double[] getXCoordsArray();

    double[] getYCoordsArray();

    void setXCoords(List<Double> xCoords);

    void setYCoords(List<Double> yCoords);

    boolean isCoordinatesNeedToBeReversed();

    void setCoordinatesNeedToBeReversed(boolean coordinatesNeedToBeReversed);

    @Override
    String toString();
}
