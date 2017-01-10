package ru.llogic.ui;

import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.ui.widget.ElementWidget;

/**
 * @author tolmalev
 */
public class Selector {
    private static final Logger logger = LogManager.getLogger(Selector.class);

    private final Pane elementsPane;
    private final Pane selectionPane;

    private final Rectangle selection;

    private Optional<Point2D> selectionStart = Optional.empty();

    public Selector(Pane elementsPane, Pane selectionPane, BiConsumer<Point2D, Point2D> selectionCompletedConsumer) {
        this.elementsPane = elementsPane;
        this.selectionPane = selectionPane;

        selection = new Rectangle(0, 0, 0, 0);
        selection.setFill(Color.rgb(176, 204, 241, 100.0 / 255));
        selection.setStroke(Color.rgb(53, 100, 255));

        elementsPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (selectionStart.isPresent()) {
                selection.setLayoutX(Math.min(event.getX(), selectionStart.get().getX()));
                selection.setLayoutY(Math.min(event.getY(), selectionStart.get().getY()));

                selection.setWidth(Math.max(event.getX(), selectionStart.get().getX()) - selection.getLayoutX());
                selection.setHeight(Math.max(event.getY(), selectionStart.get().getY()) - selection.getLayoutY());
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getTarget().equals(elementsPane)) {
                selectionStart = Optional.of(new Point2D(event.getX(), event.getY()));

                logger.debug("Started selection at " + selectionStart.get());

                selection.setLayoutX(event.getX());
                selection.setLayoutY(event.getY());
                selection.setWidth(0);
                selection.setHeight(0);

                selectionPane.getChildren().add(selection);
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (selectionStart.isPresent()) {
                selectionPane.getChildren().remove(selection);

                Point2D start = new Point2D(
                        Math.min(selectionStart.get().getX(), event.getX()),
                        Math.min(selectionStart.get().getY(), event.getY())
                );

                Point2D end = new Point2D(
                        Math.max(selectionStart.get().getX(), event.getX()),
                        Math.max(selectionStart.get().getY(), event.getY())
                );

                logger.debug("Selection completed width bounds: " + start + ", " + end);
                selectionStart = Optional.empty();

                selectionCompletedConsumer.accept(start, end);
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getTarget() instanceof ElementWidget) {
                ((ElementWidget) event.getTarget()).getStyleClass().add("selected");
            }
        });
    }
}
