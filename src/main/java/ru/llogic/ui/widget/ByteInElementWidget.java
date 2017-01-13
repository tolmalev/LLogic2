package ru.llogic.ui.widget;

import java.util.Optional;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import ru.llogic.core.Point;
import ru.llogic.core.PointState;
import ru.llogic.core.element.ByteInElement;
import ru.llogic.core.element.InElement;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public class ByteInElementWidget extends ElementWidget<ByteInElement> {
    public ByteInElementWidget(ByteInElement element) {
        super(element);
        element.getOutputPoints().forEach(this::redrawOnPointChange);
    }

    @Override
    public Point2D getPointPosition(Point point) {
        return getRightPointPosition(element.getPoints().indexOf(point));
    }

    @Override
    protected void onDoubleClick(MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog("" + element.getSettings().getValue());
        dialog.setHeaderText("Change input");
        Optional<String> newVal = dialog.showAndWait();

        if (newVal.isPresent()) {
            try {
                int value = Integer.parseInt(newVal.get());
                if (value < 0 || value > 255) {
                    new Alert(Alert.AlertType.ERROR, "Not in bounds [0, 255] : " + newVal.get()).show();
                    return;
                }
                element.setValue(value);
                element.addToCalculationQueue();
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Not a number : " + newVal.get()).show();
            }
        }
    }

    @Override
    protected void drawCenter() {
        super.drawCenter();
        GraphicsContext c = getGraphicsContext2D();

        c.setTextAlign(TextAlignment.CENTER);
        c.setTextBaseline(VPos.CENTER);

        Bounds bounds = getCenterBounds();
        c.strokeText("" + element.getSettings().getValue(),
                bounds.getMinX() + bounds.getWidth() / 2,
                bounds.getMinY() + bounds.getHeight() / 2
        );
    }

    @Override
    public int rightPointsCount() {
        return element.getOutputPoints().size();
    }
}
