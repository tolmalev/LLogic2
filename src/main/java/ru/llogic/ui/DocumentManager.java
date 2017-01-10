package ru.llogic.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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

        selector = new Selector(elementsPane, selectionPane, this);

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

    void selectionCompleted(Bounds bounds, boolean addToSelection) {
        for (Node node : elementsPane.getChildren()) {
            if (node instanceof ElementWidget && node.getBoundsInParent().intersects(bounds)) {
                selectNodeInternal(node);
            } else if (!addToSelection) {
                unselectNode(node);
            }
        }
    }

    void selectOneNode(Node node, boolean addToSelection) {
        if (!addToSelection) {
            unselectAll();
        }
        selectNodeInternal(node);
    }

    void unselectAll() {
        elementsPane.getChildren().forEach(this::unselectNode);
    }

    private void selectNodeInternal(Node node) {
        node.getStyleClass().add("selected");
    }

    void unselectNode(Node node) {
        node.getStyleClass().remove("selected");
    }

    private void addWidget(AndElementWidget widget) {
        mainPane.getChildren().add(widget);
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
