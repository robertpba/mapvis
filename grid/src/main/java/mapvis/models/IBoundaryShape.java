package mapvis.models;

import javafx.geometry.Point2D;

import java.util.List;

/**
 * Created by dacc on 11/24/2015.
 */
public interface IBoundaryShape<T> extends Iterable<Point2D> {

    int getLevel();

    Border<T> getFirstBorder();

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

    List<Point2D> getCoordinates();

    Point2D getCoordinateAtIndex(int index);

    void setCoordinates(List<Point2D> coordinates);

    boolean isCoordinatesNeedToBeReversed();

    void setCoordinatesNeedToBeReversed(boolean coordinatesNeedToBeReversed);

    @Override
    String toString();
}
