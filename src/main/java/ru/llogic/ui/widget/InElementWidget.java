package ru.llogic.ui.widget;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
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
        return new Point2D(getWidth(), GridUtils.gridSize(1));
    }

    @Override
    protected void onDoubleClick(MouseEvent event) {
        element.setSettings(new InElement.InElementSettings(
                element.getSettings().getPointState() == PointState.HIGH ? PointState.LOW : PointState.HIGH
        ));
        setCenter(buildCenter());

        element.calculate();
    }

    @Override
    protected Node buildCenter() {
        Pane pane = new Pane();

        Rectangle superCenter = (Rectangle) super.buildCenter();

        pane.getChildren().add(superCenter);
        Ellipse e = new Ellipse(superCenter.getWidth() / 2, superCenter.getHeight() / 2,
                superCenter.getWidth() / 2, superCenter.getHeight() / 2);

        e.setFill(
                element.getSettings().getPointState() == PointState.HIGH
                    ? Color.GREEN
                    : Color.RED
        );

        pane.getChildren().add(e);

        return pane;
    }

    @Override
    public int rightPointsCount() {
        return 1;
    }
}
