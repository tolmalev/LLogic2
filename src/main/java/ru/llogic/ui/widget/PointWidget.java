package ru.llogic.ui.widget;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.llogic.core.Point;
import ru.llogic.core.element.PointElement;

/**
 * @author tolmalev
 */
public class PointWidget extends ElementWidget<PointElement> {
    public PointWidget(PointElement element) {
        super(element);
    }

    @Override
    public Point2D getPointPosition(Point point) {
        return new Point2D(0, 0);
    }

    @Override
    public void draw() {
        GraphicsContext c = getGraphicsContext2D();
        c.setFill(Color.BLACK);

        c.fillOval(0, 0, getWidth(), getHeight());
    }

    @Override
    public double getWidthInPixels() {
        return 4;
    }

    @Override
    public double getHeightInPixels() {
        return 4;
    }

    @Override
    public int widthCells() {
        return 0;
    }

    @Override
    public int heightCells() {
        return 0;
    }
}
