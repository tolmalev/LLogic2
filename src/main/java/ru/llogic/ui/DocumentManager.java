package ru.llogic.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.element.AndElement;
import ru.llogic.ui.widget.AndElementWidget;
import ru.llogic.ui.widget.ElementWidget;

/**
 * @author tolmalev
 */
public class DocumentManager {
    private static final Logger logger = LogManager.getLogger(DocumentManager.class);

    private boolean active = false;

    private final Pane mainPane;

    private final Selector selector;

    private final Pane linesPane;
    private final Pane newElementPane;
    private final Pane elementsPane;
    private final Pane selectionPane;

    private final Map<Element, ElementWidget> widgets = new ConcurrentHashMap<>();
    private final CalculationManager calculationManager;

    public DocumentManager(UiController uiController, Pane mainPane) {
        this.mainPane = mainPane;
        this.calculationManager = new CalculationManager();

        linesPane = (Pane) mainPane.lookup(".lines-pane");
        newElementPane = (Pane) mainPane.lookup(".new-element-pane");
        elementsPane = (Pane) mainPane.lookup(".elements_pane");
        selectionPane = (Pane) mainPane.lookup(".selection-pane");

        selector = new Selector(elementsPane, selectionPane, this::selectionCompleted);

        elementsPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getTarget().equals(elementsPane) && event.getClickCount() == 1) {
                AndElement andElement = new AndElement(calculationManager, 2);
                AndElementWidget widget = new AndElementWidget(andElement);

                widget.setLayoutX(event.getX());
                widget.setLayoutY(event.getY());

                elementsPane.getChildren().add(widget);
            }
        });
    }

    private void selectionCompleted(Point2D start, Point2D end) {
        logger.debug("Selection completed: " + start + ":" + end);
        linesPane.getChildren().add(new Line(start.getX(), start.getY(), end.getX(), end.getY()));
    }

    private void addWidget(AndElementWidget widget) {
        mainPane.getChildren().add(widget);
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
