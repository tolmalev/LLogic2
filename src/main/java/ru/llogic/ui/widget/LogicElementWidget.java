package ru.llogic.ui.widget;

import javafx.geometry.Point2D;
import ru.llogic.core.Point;
import ru.llogic.core.element.LogicElement;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public abstract class LogicElementWidget<T extends LogicElement> extends ElementWidget<T> {
    public LogicElementWidget(T element) {
        super(element);
    }

    @Override
    public Point2D getPointPosition(Point point) {
        int pointIndex = element.getPointIndex(point);

        if (pointIndex < element.getInputsCount()) {
            return getLeftPointPosition(pointIndex);
        } else {
            return getRightPointPosition(pointIndex - element.getInputsCount());
        }
    }

    @Override
    public int topPointsCount() {
        return 0;
    }

    @Override
    public int bottomPointsCount() {
        return 0;
    }

    @Override
    public int leftPointsCount() {
        return element.getInputsCount();
    }

    @Override
    public int rightPointsCount() {
        return element.getOutputPoints().size();
    }
}
