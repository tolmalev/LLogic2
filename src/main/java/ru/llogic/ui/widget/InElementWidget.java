package ru.llogic.ui.widget;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ru.llogic.core.Point;
import ru.llogic.core.PointState;
import ru.llogic.core.element.InElement;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public class InElementWidget extends ElementWidget<InElement> {
    public InElementWidget(InElement element) {
        super(element);
    }

    @Override
    public Point2D getPointPosition(Point point) {
        return new Point2D(getWidth() - 2 * GridUtils.ELEMENT_BORDER, GridUtils.gridSize(1));
    }

    @Override
    protected void onDoubleClick(MouseEvent event) {
        element.switchState();

        redrawOnPointChange(element.getOutputPoint());
        element.calculate();
    }

    @Override
    protected void drawCenter() {
        super.drawCenter();
        GraphicsContext c = getGraphicsContext2D();

        c.setFill(element.getSettings().getPointState() == PointState.HIGH
                  ? Color.GREEN
                  : Color.RED);
        Bounds bounds = getCenterBounds();
        c.fillOval(bounds.getMinX() + 1, bounds.getMinY() + 1, bounds.getWidth() - 2, bounds.getHeight() - 2);
    }

    @Override
    public int rightPointsCount() {
        return 1;
    }
}
