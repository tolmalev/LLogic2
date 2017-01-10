package ru.llogic.ui;

import javafx.geometry.Point2D;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public class UiConnection {
    private final Point2D startPositiojn;
    private final Point2D endPosition;
    private final PointState state;

    public UiConnection(Point2D startPositiojn, Point2D endPosition, PointState state) {
        this.startPositiojn = startPositiojn;
        this.endPosition = endPosition;
        this.state = state;
    }

    public PointState getState() {
        return state;
    }

    public Point2D getStart() {
        return startPositiojn;
    }

    public Point2D getEnd() {
        return endPosition;
    }
}
