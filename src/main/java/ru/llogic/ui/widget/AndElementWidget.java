package ru.llogic.ui.widget;

import javafx.geometry.Point2D;
import ru.llogic.core.Point;
import ru.llogic.core.element.AndElement;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public class AndElementWidget extends ElementWidget<AndElement> {
    public AndElementWidget(AndElement andElement) {
        super(andElement);
    }

    @Override
    public Point2D getPointPosition(Point point) {
        int pointIndex = element.getPointIndex(point);

        double deltaX, deltaY;

        if (pointIndex < element.getInputsCount()) {
            deltaX = 0;
            deltaY = GridUtils.gridSize(pointIndex + 1);
        } else {
            deltaX = getWidth();
            deltaY = GridUtils.gridSize(pointIndex - element.getInputsCount() + 1);
        }

        return new Point2D(getBoundsInParent().getMinX() + deltaX, getBoundsInParent().getMinY() + deltaY);
    }
}
